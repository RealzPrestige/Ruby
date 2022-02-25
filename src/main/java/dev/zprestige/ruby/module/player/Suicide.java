package dev.zprestige.ruby.module.player;

import dev.zprestige.ruby.module.Module;
import dev.zprestige.ruby.settings.impl.ComboBox;
import dev.zprestige.ruby.settings.impl.Parent;
import dev.zprestige.ruby.settings.impl.Slider;
import dev.zprestige.ruby.settings.impl.Switch;
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

import java.util.Objects;
import java.util.TreeMap;

public class Suicide extends Module {
    public final Slider throwDelay = Menu.Slider("Throw Delay", 0, 500);

    public final Parent placing = Menu.Parent("Placing");
    public final Slider placeDelay = Menu.Slider("Place Delay Crystal", 0, 500).parent(placing);
    public final Slider placeRange = Menu.Slider("Place Range Crystal", 0.0f, 6.0f).parent(placing);
    public final Switch silentSwitchCrystal = Menu.Switch("Silent Switch Crystal").parent(placing);
    public final Switch packetPlaceCrystal = Menu.Switch("Packet Place Crystal").parent(placing);
    public final Switch placeSwing = Menu.Switch("Place Swing Crystal").parent(placing);
    public final ComboBox placeSwingHand = Menu.ComboBox("Place Swing Hand Crystal", new String[]{"Mainhand", "Offhand", "Packet"}).parent(placing);

    public final Parent breaking = Menu.Parent("Breaking");
    public final Slider breakDelay = Menu.Slider("Break Delay Crystal", 0, 500).parent(breaking);
    public final Slider breakRange = Menu.Slider("Break Range Crystal", 0.0f, 6.0f).parent(breaking);
    public final Switch explodeAntiWeakness = Menu.Switch("Explode Anti Weakness Crystal").parent(breaking);
    public final Switch packetBreakCrystal = Menu.Switch("Packet Break Crystal").parent(breaking);
    public final Switch breakSwing = Menu.Switch("Break Swing").parent(breaking);
    public final ComboBox breakSwingHand = Menu.ComboBox("Break Swing", new String[]{"Mainhand", "Offhand", "Packet"}).parent(breaking);

    public Timer throwTimer = new Timer();
    public Timer placeTimer = new Timer();
    public Timer breakTimer = new Timer();
    public int currentTakeoff;

    @Override
    public void onTick() {
        currentTakeoff = getCurrentTakeOff();
        if (currentTakeoff != -1 && throwTimer.getTime((long) throwDelay.GetSlider())) {
            takeOff(currentTakeoff);
            return;
        }
        if (mc.player.getHeldItemOffhand().getItem().equals(Items.TOTEM_OF_UNDYING)) {
            takeOff(45);
            return;
        }
        int totemSlot = InventoryUtil.getItemSlot(Items.TOTEM_OF_UNDYING);
        if (totemSlot != -1 && throwTimer.getTime((long) throwDelay.GetSlider())) {
            takeOff(totemSlot);
            return;
        }
        if (placeTimer.getTime((long) placeDelay.GetSlider())) {
            BlockPos pos = getPosition();
            if (pos == null)
                return;
            placeCrystal(pos);
            return;
        }
        if (breakTimer.getTime((long) breakDelay.GetSlider())) {
            for (Entity entity : mc.world.loadedEntityList) {
                if (!(entity instanceof EntityEnderCrystal) || mc.player.getDistance(entity) > breakRange.GetSlider())
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
        for (BlockPos pos : BlockUtil.getSphereAutoCrystal(placeRange.GetSlider(), true)) {
            float selfDamage = EntityUtil.calculatePosDamage(pos, mc.player);
            if (mc.world.getEntitiesWithinAABBExcludingEntity(null, new AxisAlignedBB(pos.up())).isEmpty())
                treeMap.put(selfDamage, pos);
        }
        if (!treeMap.isEmpty())
            return treeMap.lastEntry().getValue();
        return null;
    }

    public void placeCrystal(BlockPos pos) {
        if (!silentSwitchCrystal.GetSwitch() && !mc.player.getHeldItemMainhand().getItem().equals(Items.END_CRYSTAL) && !mc.player.getHeldItemOffhand().getItem().equals(Items.END_CRYSTAL))
            return;
        int slot = InventoryUtil.getItemFromHotbar(Items.END_CRYSTAL);
        int currentItem = mc.player.inventory.currentItem;
        if (silentSwitchCrystal.GetSwitch() && slot != -1 && !mc.player.getHeldItemMainhand().getItem().equals(Items.END_CRYSTAL) && !mc.player.getHeldItemOffhand().getItem().equals(Items.END_CRYSTAL))
            InventoryUtil.switchToSlot(slot);
        if (packetPlaceCrystal.GetSwitch())
            Objects.requireNonNull(mc.getConnection()).sendPacket(new CPacketPlayerTryUseItemOnBlock(pos, EnumFacing.UP, mc.player.getHeldItemOffhand().getItem() == Items.END_CRYSTAL ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND, 0.5f, 0.5f, 0.5f));
        else
            mc.playerController.processRightClickBlock(mc.player, mc.world, pos, EnumFacing.UP, new Vec3d(mc.player.posX, -mc.player.posY, -mc.player.posZ), mc.player.getHeldItemOffhand().getItem().equals(Items.END_CRYSTAL) ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND);
        if (silentSwitchCrystal.GetSwitch()) {
            mc.player.inventory.currentItem = currentItem;
            mc.playerController.updateController();
        }
        if (placeSwing.GetSwitch())
            EntityUtil.swingArm(placeSwingHand.GetCombo().equals("Mainhand") ? EntityUtil.SwingType.MainHand : placeSwingHand.GetCombo().equals("Offhand") ? EntityUtil.SwingType.OffHand : EntityUtil.SwingType.Packet);
        placeTimer.setTime(0);
    }

    public void breakCrystal(EntityEnderCrystal entity) {
        boolean switched = false;
        int currentItem = -1;
        if (explodeAntiWeakness.GetSwitch()) {
            PotionEffect weakness = mc.player.getActivePotionEffect(MobEffects.WEAKNESS);
            if (weakness != null && !mc.player.getHeldItemMainhand().getItem().equals(Items.DIAMOND_SWORD)) {
                int swordSlot = InventoryUtil.getItemFromHotbar(Items.DIAMOND_SWORD);
                currentItem = mc.player.inventory.currentItem;
                InventoryUtil.switchToSlot(swordSlot);
                switched = true;
            }
        }
        if (packetBreakCrystal.GetSwitch())
            Objects.requireNonNull(mc.getConnection()).sendPacket(new CPacketUseEntity(entity));
        else
            mc.playerController.attackEntity(mc.player, entity);
        if (switched) {
            mc.player.inventory.currentItem = currentItem;
            mc.playerController.updateController();
        }
        if (breakSwing.GetSwitch())
            EntityUtil.swingArm(breakSwingHand.GetCombo().equals("Mainhand") ? EntityUtil.SwingType.MainHand : breakSwingHand.GetCombo().equals("Offhand") ? EntityUtil.SwingType.OffHand : EntityUtil.SwingType.Packet);
        breakTimer.setTime(0);
        disableModule("Completed suicide");
    }
}
