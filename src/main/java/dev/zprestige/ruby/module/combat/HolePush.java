package dev.zprestige.ruby.module.combat;

import dev.zprestige.ruby.module.Category;
import dev.zprestige.ruby.module.Module;
import dev.zprestige.ruby.module.ModuleInfo;
import dev.zprestige.ruby.setting.impl.BooleanSetting;
import dev.zprestige.ruby.setting.impl.FloatSetting;
import dev.zprestige.ruby.setting.impl.IntegerSetting;
import dev.zprestige.ruby.setting.impl.ParentSetting;
import dev.zprestige.ruby.util.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;

@ModuleInfo(name = "HolePush", category = Category.Combat, description = "pushes fat kids outta they hole")
public class HolePush extends Module {
    public IntegerSetting placeDelay = createSetting("Place Delay", 50, 0, 500);
    public ParentSetting ranges = createSetting("Ranges");
    public FloatSetting targetRange = createSetting("Target Range", 9.0f, 0.1f, 15.0f).setParent(ranges);
    public FloatSetting placeRange = createSetting("Place Range", 5.0f, 0.1f, 6.0f).setParent(ranges);
    public ParentSetting misc = createSetting("Misc");
    public BooleanSetting inLiquids = createSetting("In Liquids", false).setParent(misc);
    public BooleanSetting oppositeCheck = createSetting("Opposite Check", false).setParent(misc);
    public BooleanSetting packetPlace = createSetting("Packet Place", false).setParent(misc);
    public BooleanSetting tickWaitRotate = createSetting("Tick Wait Rotate", false).setParent(misc);
    public BooleanSetting rotate = createSetting("Rotate Place", false).setParent(misc);
    public Timer placeTimer = new Timer();
    public BlockPos placedPistonPos = null;
    public boolean rotated = false;
    public boolean rotated2 = false;

    @Override
    public void onEnable(){
        rotated = false;
        rotated2 = false;
    }

    @Override
    public void onTick() {
        EntityPlayer target = EntityUtil.getTarget(targetRange.getValue());
        if (target == null)
            return;
        if (!BlockUtil.isPlayerSafe(target))
            return;
        BlockPos pos = EntityUtil.getPlayerPos(target).up();
        Side side = getSide(pos);
        if (side == null)
            return;
        BlockPos pistonPos = getPistonBySide(side, pos);
        if (pistonPos == null)
            return;
        int slot = InventoryUtil.getItemFromHotbar(Item.getItemFromBlock(Blocks.PISTON));
        if (slot == -1) {
            disableModule("No Pistons found, disabling HolePush.");
            return;
        }
        if (!placeTimer.getTime(placeDelay.getValue()))
            return;
        if (!rotated2){
            rotateBySide(side);
            rotated2 = true;
            if (tickWaitRotate.getValue())
                return;
        }
        if (placedPistonPos == null) {
            BlockUtil.placeBlockWithSwitch(pistonPos, EnumHand.MAIN_HAND, rotate.getValue(), packetPlace.getValue(), slot, placeTimer);
            placedPistonPos = pistonPos;
            return;
        }
        BlockPos redstonePos = getRedstoneBlockBySide(side, pos);
        if (redstonePos == null)
            return;
        int redstone = InventoryUtil.getItemFromHotbar(Item.getItemFromBlock(Blocks.REDSTONE_BLOCK));
        if (redstone == -1) {
            disableModule("No Redstone Blocks found, disabling HolePush.");
            return;
        }
        if (!rotated){
            rotateBySide(side);
            rotated = true;
            if (tickWaitRotate.getValue())
                return;
        }
        if (placedPistonPos != null) {
            BlockUtil.placeBlockWithSwitch(redstonePos, EnumHand.MAIN_HAND, rotate.getValue(), packetPlace.getValue(), redstone, placeTimer);
            placedPistonPos = null;
            disableModule("Finished hole pushing, disabling HolePush");
        }
    }

    public void rotateBySide(Side side){
        switch (side){
            case North:
                mc.player.connection.sendPacket(new CPacketPlayer.Rotation(180, mc.player.rotationPitch, mc.player.onGround));
                break;
            case East:
                mc.player.connection.sendPacket(new CPacketPlayer.Rotation(-90, mc.player.rotationPitch, mc.player.onGround));
                break;
            case South:
                mc.player.connection.sendPacket(new CPacketPlayer.Rotation(0, mc.player.rotationPitch, mc.player.onGround));
                break;
            case West:
                mc.player.connection.sendPacket(new CPacketPlayer.Rotation(90, mc.player.rotationPitch, mc.player.onGround));
                break;
        }
    }

    public BlockPos getPistonBySide(Side side, BlockPos pos) {
        switch (side) {
            case North:
                return pos.north();
            case East:
                return pos.east();
            case South:
                return pos.south();
            case West:
                return pos.west();
        }
        return null;
    }

    public BlockPos getRedstoneBlockBySide(Side side, BlockPos pos) {
        switch (side) {
            case North:
                return pos.north().north();
            case East:
                return pos.east().east();
            case South:
                return pos.south().south();
            case West:
                return pos.west().west();
        }
        return null;
    }

    public Side getSide(BlockPos pos) {
        if ((canPlace(pos.north()) || isPiston(pos.north())) && canPlace(pos.north().north()) && (!oppositeCheck.getValue() || (canPlace(pos.south()) && canPlace(pos.up().south()))) && mc.player.getDistanceSq(pos.north()) < (placeRange.getValue() * placeRange.getValue()) && mc.player.getDistanceSq(pos.north().north()) < (placeRange.getValue() * placeRange.getValue()))
            return Side.North;
        if ((canPlace(pos.east()) || isPiston(pos.east())) && canPlace(pos.east().east()) && (!oppositeCheck.getValue() || (canPlace(pos.west()) && canPlace(pos.up().west()))) && mc.player.getDistanceSq(pos.east()) < (placeRange.getValue() * placeRange.getValue()) && mc.player.getDistanceSq(pos.east().east()) < (placeRange.getValue() * placeRange.getValue()))
            return Side.East;
        if ((canPlace(pos.south()) || isPiston(pos.south())) && canPlace(pos.south().south()) && (!oppositeCheck.getValue() || (canPlace(pos.north()) && canPlace(pos.up().north()))) && mc.player.getDistanceSq(pos.south()) < (placeRange.getValue() * placeRange.getValue()) && mc.player.getDistanceSq(pos.south().south()) < (placeRange.getValue() * placeRange.getValue()))
            return Side.South;
        if ((canPlace(pos.west()) || isPiston(pos.west())) && canPlace(pos.west().west()) && (!oppositeCheck.getValue() || (canPlace(pos.east()) && canPlace(pos.up().east()))) && mc.player.getDistanceSq(pos.west()) < (placeRange.getValue() * placeRange.getValue()) && mc.player.getDistanceSq(pos.west().west()) < (placeRange.getValue() * placeRange.getValue()))
            return Side.West;
        return null;
    }
    
    public boolean canPlace(BlockPos pos){
        return mc.world.getBlockState(pos).getBlock().equals(Blocks.AIR) || (inLiquids.getValue() && ((mc.world.getBlockState(pos).getBlock().equals(Blocks.WATER) || mc.world.getBlockState(pos).getBlock().equals(Blocks.FLOWING_WATER))|| (mc.world.getBlockState(pos).getBlock().equals(Blocks.LAVA) || mc.world.getBlockState(pos).getBlock().equals(Blocks.FLOWING_LAVA))));
    }

    public boolean isPiston(BlockPos pos) {
        return mc.world.getBlockState(pos).getBlock().equals(Blocks.PISTON);
    }

    public enum Side {
        North,
        East,
        South,
        West
    }
}
