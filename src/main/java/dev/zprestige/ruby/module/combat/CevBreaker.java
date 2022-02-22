package dev.zprestige.ruby.module.combat;

import dev.zprestige.ruby.module.Module;
import dev.zprestige.ruby.setting.impl.*;
import dev.zprestige.ruby.util.BlockUtil;
import dev.zprestige.ruby.util.EntityUtil;
import dev.zprestige.ruby.util.InventoryUtil;
import dev.zprestige.ruby.util.Timer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.Arrays;
import java.util.Objects;

public class CevBreaker extends Module {

    public FloatSetting targetRange = createSetting("Target Range", 5.0f, 0.1f, 6.0f);
    public IntegerSetting actionDelay = createSetting("Action Delay", 100, 0, 1000);
    public ParentSetting placing = createSetting("Placing");
    public BooleanSetting silentSwitchCrystal = createSetting("Silent Switch Crystal", false).setParent(placing);
    public BooleanSetting packetPlace = createSetting("Packet Place Crystal", false).setParent(placing);
    public BooleanSetting placeSwing = createSetting("Place Swing Crystal", false).setParent(placing);
    public ModeSetting placeSwingHand = createSetting("Place Swing Hand Crystal", "Mainhand", Arrays.asList("Mainhand", "Offhand", "Packet"), v -> placeSwing.getValue()).setParent(placing);
    public ParentSetting exploding = createSetting("Exploding");
    public BooleanSetting packetExplodeCrystal = createSetting("Packet Explode Crystal", false).setParent(exploding);
    public BooleanSetting explodeSwing = createSetting("Explode Swing Crystal", false).setParent(exploding);
    public ModeSetting explodeSwingHand = createSetting("Explode Swing Hand Crystal", "Mainhand", Arrays.asList("Mainhand", "Offhand", "Packet"), v -> placeSwing.getValue()).setParent(exploding);
    public ParentSetting misc = createSetting("Misc");
    public BooleanSetting selfSafe = createSetting("Self Safe", false).setParent(misc);
    public BooleanSetting packet = createSetting("Packet", false).setParent(misc);
    public BooleanSetting rotate = createSetting("Rotate", false).setParent(misc);
    public Timer timer = new Timer();
    public boolean isMining;
    public boolean needsCrystal;

    @Override
    public void onEnable() {
        needsCrystal = false;
        isMining = false;
        timer.setTime(0);
    }

    @Override
    public void onTick() {
        EntityPlayer entityPlayer = EntityUtil.getTarget(targetRange.getValue());
        if (entityPlayer == null)
            return;
        if (!BlockUtil.isPlayerSafe(entityPlayer) || (selfSafe.getValue() && !BlockUtil.isPlayerSafe(mc.player)))
            return;
        if (!isPlayerCevBreakable(entityPlayer))
            return;
        int slot = InventoryUtil.getItemFromHotbar(Item.getItemFromBlock(Blocks.OBSIDIAN));
        if (slot == -1) {
            disableModule("No obsidian found in hotbar, disabling CevBreaker.");
            return;
        }
        BlockPos pos = EntityUtil.getPlayerPos(entityPlayer).up().up();
        if (!needsCrystal && mc.world.getBlockState(pos).getBlock().equals(Blocks.AIR) && isPosSurroundedByBlocks(pos) && timer.getTime(actionDelay.getValue())) {
            BlockUtil.placeBlockWithSwitch(pos, EnumHand.MAIN_HAND, rotate.getValue(), packet.getValue(), slot);
            timer.setTime(0);
            needsCrystal = true;
        }

        if (mc.world.getBlockState(pos).getBlock().equals(Blocks.OBSIDIAN) && mc.world.getEntitiesWithinAABBExcludingEntity(null, new AxisAlignedBB(pos.up())).isEmpty() && timer.getTime(actionDelay.getValue())) {
            placeCrystal(pos);
            clickBlock(pos);
            isMining = true;
            timer.setTime(0);
        }
        if (isMining && timer.getTime(2000)) {
            int slot1 = InventoryUtil.getItemFromHotbar(Items.DIAMOND_PICKAXE);
            if (!mc.player.getHeldItemMainhand().getItem().equals(Items.DIAMOND_PICKAXE) && slot1 != -1) {
                clickBlock(pos);
            }
            isMining = false;
        }
        if (timer.getTime(2000 + actionDelay.getValue())) {
            for (Entity entity : mc.world.loadedEntityList) {
                if (!(entity instanceof EntityEnderCrystal))
                    continue;
                if (entity.getDistanceSq(pos) > 4.0f)
                    continue;
                if (packetExplodeCrystal.getValue())
                    Objects.requireNonNull(mc.getConnection()).sendPacket(new CPacketUseEntity(entity));
                else
                    mc.playerController.attackEntity(mc.player, entity);
                if (explodeSwing.getValue())
                    EntityUtil.swingArm(explodeSwingHand.getValue().equals("Mainhand") ? EntityUtil.SwingType.MainHand : explodeSwingHand.getValue().equals("Offhand") ? EntityUtil.SwingType.OffHand : EntityUtil.SwingType.Packet);
                needsCrystal = false;
            }
        }
    }

    public void placeCrystal(BlockPos pos) {
        if (!silentSwitchCrystal.getValue() && !mc.player.getHeldItemMainhand().getItem().equals(Items.END_CRYSTAL) && !mc.player.getHeldItemOffhand().getItem().equals(Items.END_CRYSTAL))
            return;
        int slot = InventoryUtil.getItemFromHotbar(Items.END_CRYSTAL);
        int currentItem = mc.player.inventory.currentItem;
        if (silentSwitchCrystal.getValue() && slot != -1 && !mc.player.getHeldItemMainhand().getItem().equals(Items.END_CRYSTAL) && !mc.player.getHeldItemOffhand().getItem().equals(Items.END_CRYSTAL))
            InventoryUtil.switchToSlot(slot);
        if (packetPlace.getValue())
            Objects.requireNonNull(mc.getConnection()).sendPacket(new CPacketPlayerTryUseItemOnBlock(pos, EnumFacing.UP, mc.player.getHeldItemOffhand().getItem() == Items.END_CRYSTAL ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND, 0.5f, 0.5f, 0.5f));
        else
            mc.playerController.processRightClickBlock(mc.player, mc.world, pos, EnumFacing.UP, new Vec3d(mc.player.posX, -mc.player.posY, -mc.player.posZ), mc.player.getHeldItemOffhand().getItem().equals(Items.END_CRYSTAL) ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND);
        if (silentSwitchCrystal.getValue()) {
            mc.player.inventory.currentItem = currentItem;
            mc.playerController.updateController();
        }
        if (placeSwing.getValue())
            EntityUtil.swingArm(placeSwingHand.getValue().equals("Mainhand") ? EntityUtil.SwingType.MainHand : placeSwingHand.getValue().equals("Offhand") ? EntityUtil.SwingType.OffHand : EntityUtil.SwingType.Packet);
    }


    public boolean isPosSurroundedByBlocks(BlockPos blockPos) {
        return !mc.world.getBlockState(blockPos.north()).getBlock().equals(Blocks.AIR) || !mc.world.getBlockState(blockPos.east()).getBlock().equals(Blocks.AIR) || !mc.world.getBlockState(blockPos.south()).getBlock().equals(Blocks.AIR) || !mc.world.getBlockState(blockPos.west()).getBlock().equals(Blocks.AIR) || !mc.world.getBlockState(blockPos.down()).getBlock().equals(Blocks.AIR) || !mc.world.getBlockState(blockPos.up()).getBlock().equals(Blocks.AIR);
    }

    public boolean isPlayerCevBreakable(EntityPlayer entityPlayer) {
        BlockPos pos = EntityUtil.getPlayerPos(entityPlayer).up().up();
        return (mc.world.getBlockState(pos).getBlock().equals(Blocks.AIR) || mc.world.getBlockState(pos).getBlock().equals(Blocks.OBSIDIAN)) && mc.world.getBlockState(pos.up()).getBlock().equals(Blocks.AIR) && mc.world.getBlockState(pos.up().up()).getBlock().equals(Blocks.AIR);
    }

    public EnumFacing getEmptyNeighbour(BlockPos pos) {
        for (EnumFacing side : EnumFacing.values()) {
            BlockPos neighbour = pos.offset(side);
            if (!mc.world.getBlockState(neighbour).getBlock().canCollideCheck(mc.world.getBlockState(neighbour), false))
                continue;
            IBlockState blockState = mc.world.getBlockState(neighbour);
            if (!blockState.getMaterial().isReplaceable()) {
                return side;
            }
        }
        return null;
    }

    public void clickBlock(BlockPos pos) {
        EnumFacing facing = getEmptyNeighbour(pos);
        EntityUtil.swingArm(EntityUtil.SwingType.MainHand);
        mc.playerController.clickBlock(pos, facing);
    }

}
