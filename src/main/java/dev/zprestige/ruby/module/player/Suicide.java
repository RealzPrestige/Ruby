package dev.zprestige.ruby.module.player;

import dev.zprestige.ruby.module.Category;
import dev.zprestige.ruby.module.Module;
import dev.zprestige.ruby.module.ModuleInfo;
import dev.zprestige.ruby.setting.impl.*;
import dev.zprestige.ruby.util.BlockUtil;
import dev.zprestige.ruby.util.EntityUtil;
import dev.zprestige.ruby.util.InventoryUtil;
import dev.zprestige.ruby.util.Timer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.Item;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.Arrays;
import java.util.Objects;
import java.util.TreeMap;

@ModuleInfo(name = "Suicide" , category = Category.Player, description = "be like neo")
public class Suicide extends Module {
    public IntegerSetting throwDelay = createSetting("Throw Delay", 100, 0, 500);

    public ParentSetting placing = createSetting("Placing");
    public IntegerSetting placeDelay = createSetting("Place Delay Crystal", 100, 0, 500).setParent(placing);
    public FloatSetting placeRange = createSetting("Place Range Crystal", 5.0f, 0.0f, 6.0f).setParent(placing);
    public BooleanSetting silentSwitchCrystal = createSetting("Silent Switch Crystal", false).setParent(placing);
    public BooleanSetting packetPlaceCrystal = createSetting("Packet Place Crystal", false).setParent(placing);
    public BooleanSetting placeSwing = createSetting("Place Swing Crystal", false).setParent(placing);
    public ModeSetting placeSwingHand = createSetting("Place Swing Hand Crystal", "Mainhand", Arrays.asList("Mainhand", "Offhand", "Packet"), v -> placeSwing.getValue()).setParent(placing);

    public ParentSetting breaking = createSetting("Breaking");
    public IntegerSetting breakDelay = createSetting("Break Delay Crystal", 100, 0, 500).setParent(breaking);
    public FloatSetting breakRange = createSetting("Break Range Crystal", 5.0f, 0.0f, 6.0f).setParent(breaking);
    public BooleanSetting explodeAntiWeakness = createSetting("Explode Anti Weakness Crystal", false).setParent(breaking);
    public BooleanSetting packetBreakCrystal = createSetting("Packet Break Crystal", false).setParent(breaking);
    public BooleanSetting breakSwing = createSetting("Break Swing", false).setParent(breaking);
    public ModeSetting breakSwingHand = createSetting("Break Swing", "Mainhand", Arrays.asList("Mainhand", "Offhand", "Packet"), v -> breakSwing.getValue()).setParent(breaking);

    public Timer throwTimer = new Timer();
    public Timer placeTimer = new Timer();
    public Timer breakTimer = new Timer();
    public int currentTakeoff;

    @Override
    public void onTick() {
        currentTakeoff = getCurrentTakeOff();
        if (currentTakeoff != -1 && throwTimer.getTime(throwDelay.getValue())) {
            takeOff(currentTakeoff);
            return;
        }
        if (mc.player.getHeldItemOffhand().getItem().equals(Items.TOTEM_OF_UNDYING)) {
            takeOff(45);
            return;
        }
        int totemSlot = InventoryUtil.getItemSlot(Items.TOTEM_OF_UNDYING);
        if (totemSlot != -1 && throwTimer.getTime(throwDelay.getValue())) {
            takeOff(totemSlot);
            return;
        }
        if (placeTimer.getTime(placeDelay.getValue())) {
            BlockPos pos = getPosition();
            if (pos == null)
                return;
            placeCrystal(pos);
            return;
        }
        if (breakTimer.getTime(breakDelay.getValue())) {
            for (Entity entity : mc.world.loadedEntityList) {
                if (!(entity instanceof EntityEnderCrystal) || mc.player.getDistance(entity) > breakRange.getValue())
                    continue;
                float selfDamage = EntityUtil.calculatePosDamage(new BlockPos(entity.posX + 0.5, entity.posY, entity.posZ + 0.5), mc.player);
                if (selfDamage >= (mc.player.getHealth() + mc.player.getAbsorptionAmount()))
                    breakCrystal((EntityEnderCrystal) entity);
            }
        }
    }

    public void takeOff(int i) {
        mc.playerController.windowClick(0, i, 0, ClickType.PICKUP, mc.player);
        mc.playerController.windowClick(0, -999, 0, ClickType.PICKUP, mc.player);
        throwTimer.setTime(0);
    }

    public int getCurrentTakeOff() {
        Item head = mc.player.inventory.getStackInSlot(39).getItem();
        Item chest = mc.player.inventory.getStackInSlot(38).getItem();
        Item legs = mc.player.inventory.getStackInSlot(37).getItem();
        Item feet = mc.player.inventory.getStackInSlot(36).getItem();
        if (!head.equals(Items.AIR)) {
            return 5;
        }
        if (!chest.equals(Items.AIR)) {
            return 6;
        }
        if (!legs.equals(Items.AIR)) {
            return 7;
        }
        if (!feet.equals(Items.AIR)) {
            return 8;
        }
        return -1;
    }

    public BlockPos getPosition() {
        TreeMap<Float, BlockPos> treeMap = new TreeMap<>();
        for (BlockPos pos : BlockUtil.getSphereAutoCrystal(placeRange.getValue(), true)) {
            float selfDamage = EntityUtil.calculatePosDamage(pos, mc.player);
            if (mc.world.getEntitiesWithinAABBExcludingEntity(null, new AxisAlignedBB(pos.up())).isEmpty())
                treeMap.put(selfDamage, pos);
        }
        if (!treeMap.isEmpty())
            return treeMap.lastEntry().getValue();
        return null;
    }

    public void placeCrystal(BlockPos pos) {
        if (!silentSwitchCrystal.getValue() && !mc.player.getHeldItemMainhand().getItem().equals(Items.END_CRYSTAL) && !mc.player.getHeldItemOffhand().getItem().equals(Items.END_CRYSTAL))
            return;
        int slot = InventoryUtil.getItemFromHotbar(Items.END_CRYSTAL);
        int currentItem = mc.player.inventory.currentItem;
        if (silentSwitchCrystal.getValue() && slot != -1 && !mc.player.getHeldItemMainhand().getItem().equals(Items.END_CRYSTAL) && !mc.player.getHeldItemOffhand().getItem().equals(Items.END_CRYSTAL))
            InventoryUtil.switchToSlot(slot);
        if (packetPlaceCrystal.getValue())
            Objects.requireNonNull(mc.getConnection()).sendPacket(new CPacketPlayerTryUseItemOnBlock(pos, EnumFacing.UP, mc.player.getHeldItemOffhand().getItem() == Items.END_CRYSTAL ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND, 0.5f, 0.5f, 0.5f));
        else
            mc.playerController.processRightClickBlock(mc.player, mc.world, pos, EnumFacing.UP, new Vec3d(mc.player.posX, -mc.player.posY, -mc.player.posZ), mc.player.getHeldItemOffhand().getItem().equals(Items.END_CRYSTAL) ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND);
        if (silentSwitchCrystal.getValue()) {
            mc.player.inventory.currentItem = currentItem;
            mc.playerController.updateController();
        }
        if (placeSwing.getValue())
            EntityUtil.swingArm(placeSwingHand.getValue().equals("Mainhand") ? EntityUtil.SwingType.MainHand : placeSwingHand.getValue().equals("Offhand") ? EntityUtil.SwingType.OffHand : EntityUtil.SwingType.Packet);
        placeTimer.setTime(0);
    }

    public void breakCrystal(EntityEnderCrystal entity) {
        boolean switched = false;
        int currentItem = -1;
        if (explodeAntiWeakness.getValue()) {
            PotionEffect weakness = mc.player.getActivePotionEffect(MobEffects.WEAKNESS);
            if (weakness != null && !mc.player.getHeldItemMainhand().getItem().equals(Items.DIAMOND_SWORD)) {
                int swordSlot = InventoryUtil.getItemFromHotbar(Items.DIAMOND_SWORD);
                currentItem = mc.player.inventory.currentItem;
                InventoryUtil.switchToSlot(swordSlot);
                switched = true;
            }
        }
        if (packetBreakCrystal.getValue())
            Objects.requireNonNull(mc.getConnection()).sendPacket(new CPacketUseEntity(entity));
        else
            mc.playerController.attackEntity(mc.player, entity);
        if (switched) {
            mc.player.inventory.currentItem = currentItem;
            mc.playerController.updateController();
        }
        if (breakSwing.getValue())
            EntityUtil.swingArm(breakSwingHand.getValue().equals("Mainhand") ? EntityUtil.SwingType.MainHand : breakSwingHand.getValue().equals("Offhand") ? EntityUtil.SwingType.OffHand : EntityUtil.SwingType.Packet);
        breakTimer.setTime(0);
        disableModule("Completed suicide");
    }
}
