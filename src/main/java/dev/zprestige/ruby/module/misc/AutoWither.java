package dev.zprestige.ruby.module.misc;

import dev.zprestige.ruby.module.Module;
import dev.zprestige.ruby.settings.impl.Slider;
import dev.zprestige.ruby.settings.impl.Switch;
import dev.zprestige.ruby.util.BlockUtil;
import dev.zprestige.ruby.util.InventoryUtil;
import dev.zprestige.ruby.util.RenderUtil;
import dev.zprestige.ruby.util.Timer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;

import java.awt.*;
import java.util.TreeMap;

public class AutoWither extends Module {
    public final Slider placeDelay = Menu.Slider("Place Delay", 0, 1000);
    public final Slider placeRange = Menu.Slider("Place Range", 0.1f, 6.0f);
    public final Switch packet = Menu.Switch("Packet");
    public final Switch rotate = Menu.Switch("Rotate");
    public Timer timer = new Timer();
    public Timer restartTimer = new Timer();
    public trolleyBus trolleyPos = null;

    @Override
    public void onEnable() {
        timer.setTime(0);
        trolleyPos = null;
    }

    @Override
    public void onTick() {
        if (trolleyPos == null && restartTimer.getTime(1000)) {
            TreeMap<Double, trolleyBus> treeMap = new TreeMap<>();
            for (BlockPos pos1 : BlockUtil.getSphere(placeRange.GetSlider(), BlockUtil.AirType.IgnoreAir, mc.player)) {
                if (mc.player.getDistanceSq(pos1) < 4.0f)
                    continue;
                trolleyBus canPlaceWither = canPlaceWither(pos1);
                if (canPlaceWither.i != -1) {
                    treeMap.put(mc.player.getDistanceSq(pos1) / 2f, canPlaceWither);
                }
            }
            if (!treeMap.isEmpty())
                trolleyPos = treeMap.firstEntry().getValue();
        } else if (timer.getTime((long) placeDelay.GetSlider())) {
            if (trolleyPos != null && mc.player.getDistanceSq(trolleyPos.pos) > (placeRange.GetSlider() * placeRange.GetSlider())) {
                trolleyPos = null;
                restartTimer.setTime(0);
                return;
            }
            placeWither();
            timer.setTime(0);
        }
    }

    public void placeWither() {
        int slot = InventoryUtil.getItemFromHotbar(Item.getItemFromBlock(Blocks.SOUL_SAND));
        int skull = InventoryUtil.getItemFromHotbar(Items.SKULL);
        if (slot == -1) {
            disableModule("No soul sand found, disabling AutoWither.");
            return;
        }
        if (skull == -1) {
            disableModule("No skulls found, disabling AutoWither.");
            return;
        }
        if (trolleyPos == null)
            return;
        if (mc.world.getBlockState(trolleyPos.pos.up()).getBlock().equals(Blocks.AIR)) {
            BlockUtil.placeBlockWithSwitch(trolleyPos.pos.up(), EnumHand.MAIN_HAND, rotate.GetSwitch(), packet.GetSwitch(), slot);
            return;
        }
        if (mc.world.getBlockState(trolleyPos.pos.up().up()).getBlock().equals(Blocks.AIR)) {
            BlockUtil.placeBlockWithSwitch(trolleyPos.pos.up().up(), EnumHand.MAIN_HAND, rotate.GetSwitch(), packet.GetSwitch(), slot);
            return;
        }
        switch (trolleyPos.side) {
            case NorthSouth:
                if (mc.world.getBlockState(trolleyPos.pos.up().up().north()).getBlock().equals(Blocks.AIR)) {
                    BlockUtil.placeBlockWithSwitch(trolleyPos.pos.up().up().north(), EnumHand.MAIN_HAND, rotate.GetSwitch(), packet.GetSwitch(), slot);
                    return;
                }
                if (mc.world.getBlockState(trolleyPos.pos.up().up().south()).getBlock().equals(Blocks.AIR)) {
                    BlockUtil.placeBlockWithSwitch(trolleyPos.pos.up().up().south(), EnumHand.MAIN_HAND, rotate.GetSwitch(), packet.GetSwitch(), slot);
                    return;
                }
                if (mc.world.getBlockState(trolleyPos.pos.up().up().up()).getBlock().equals(Blocks.AIR)) {
                    BlockUtil.placeBlockWithSwitch(trolleyPos.pos.up().up().up(), EnumHand.MAIN_HAND, rotate.GetSwitch(), packet.GetSwitch(), skull);
                    return;
                }
                if (mc.world.getBlockState(trolleyPos.pos.up().up().up().north()).getBlock().equals(Blocks.AIR)) {
                    BlockUtil.placeBlockWithSwitch(trolleyPos.pos.up().up().up().north(), EnumHand.MAIN_HAND, rotate.GetSwitch(), packet.GetSwitch(), skull);
                    return;
                }
                if (mc.world.getBlockState(trolleyPos.pos.up().up().up().south()).getBlock().equals(Blocks.AIR)) {
                    BlockUtil.placeBlockWithSwitch(trolleyPos.pos.up().up().up().south(), EnumHand.MAIN_HAND, rotate.GetSwitch(), packet.GetSwitch(), skull);
                    trolleyPos = null;
                    restartTimer.setTime(0);
                    return;
                }
                break;
            case EastWest:
                if (mc.world.getBlockState(trolleyPos.pos.up().up().east()).getBlock().equals(Blocks.AIR)) {
                    BlockUtil.placeBlockWithSwitch(trolleyPos.pos.up().up().east(), EnumHand.MAIN_HAND, rotate.GetSwitch(), packet.GetSwitch(), slot);
                    return;
                }
                if (mc.world.getBlockState(trolleyPos.pos.up().up().west()).getBlock().equals(Blocks.AIR)) {
                    BlockUtil.placeBlockWithSwitch(trolleyPos.pos.up().up().west(), EnumHand.MAIN_HAND, rotate.GetSwitch(), packet.GetSwitch(), slot);
                    return;
                }
                if (mc.world.getBlockState(trolleyPos.pos.up().up().up()).getBlock().equals(Blocks.AIR)) {
                    BlockUtil.placeBlockWithSwitch(trolleyPos.pos.up().up().up(), EnumHand.MAIN_HAND, rotate.GetSwitch(), packet.GetSwitch(), skull);
                    return;
                }
                if (mc.world.getBlockState(trolleyPos.pos.up().up().up().east()).getBlock().equals(Blocks.AIR)) {
                    BlockUtil.placeBlockWithSwitch(trolleyPos.pos.up().up().up().east(), EnumHand.MAIN_HAND, rotate.GetSwitch(), packet.GetSwitch(), skull);
                    return;
                }
                if (mc.world.getBlockState(trolleyPos.pos.up().up().up().west()).getBlock().equals(Blocks.AIR)) {
                    BlockUtil.placeBlockWithSwitch(trolleyPos.pos.up().up().up().west(), EnumHand.MAIN_HAND, rotate.GetSwitch(), packet.GetSwitch(), skull);
                    trolleyPos = null;
                    restartTimer.setTime(0);
                    return;
                }
                break;
        }
    }

    public trolleyBus canPlaceWither(BlockPos pos) {
        if (!mc.world.getBlockState(pos.up()).getBlock().equals(Blocks.AIR) || !mc.world.getEntitiesWithinAABBExcludingEntity(null, new AxisAlignedBB(pos.up())).isEmpty())
            return new trolleyBus(-1, null, null);
        if (!mc.world.getBlockState(pos.up().up()).getBlock().equals(Blocks.AIR) || !mc.world.getEntitiesWithinAABBExcludingEntity(null, new AxisAlignedBB(pos.up().up())).isEmpty())
            return new trolleyBus(-1, null, null);
        Side currSide = null;
        if ((mc.world.getBlockState(pos.up().up().north()).getBlock().equals(Blocks.AIR) && mc.world.getBlockState(pos.up().up().south()).getBlock().equals(Blocks.AIR)) && mc.world.getEntitiesWithinAABBExcludingEntity(null, new AxisAlignedBB(pos.up().up().north())).isEmpty() && mc.world.getEntitiesWithinAABBExcludingEntity(null, new AxisAlignedBB(pos.up().up().south())).isEmpty())
            currSide = Side.NorthSouth;
        if ((mc.world.getBlockState(pos.up().up().east()).getBlock().equals(Blocks.AIR) && mc.world.getBlockState(pos.up().up().west()).getBlock().equals(Blocks.AIR)) && mc.world.getEntitiesWithinAABBExcludingEntity(null, new AxisAlignedBB(pos.up().up().east())).isEmpty() && mc.world.getEntitiesWithinAABBExcludingEntity(null, new AxisAlignedBB(pos.up().up().west())).isEmpty())
            currSide = Side.EastWest;
        if (currSide == null)
            return new trolleyBus(-1, null, null);
        switch (currSide) {
            case NorthSouth:
                if (!mc.world.getBlockState(pos.up().north()).getBlock().equals(Blocks.AIR) || !mc.world.getBlockState(pos.up().south()).getBlock().equals(Blocks.AIR))
                    return new trolleyBus(-1, null, null);
                if (!mc.world.getBlockState(pos.up().up().up().north()).getBlock().equals(Blocks.AIR) || !mc.world.getBlockState(pos.up().up().up().south()).getBlock().equals(Blocks.AIR) || !mc.world.getBlockState(pos.up().up().up()).getBlock().equals(Blocks.AIR) || !mc.world.getEntitiesWithinAABBExcludingEntity(null, new AxisAlignedBB(pos.up().up().up())).isEmpty() || !mc.world.getEntitiesWithinAABBExcludingEntity(null, new AxisAlignedBB(pos.up().up().up().north())).isEmpty() || !mc.world.getEntitiesWithinAABBExcludingEntity(null, new AxisAlignedBB(pos.up().up().up().south())).isEmpty())
                    return new trolleyBus(-1, null, null);
                break;
            case EastWest:
                if (!mc.world.getBlockState(pos.up().east()).getBlock().equals(Blocks.AIR) || !mc.world.getBlockState(pos.up().west()).getBlock().equals(Blocks.AIR))
                    return new trolleyBus(-1, null, null);
                if (!mc.world.getBlockState(pos.up().up().up().east()).getBlock().equals(Blocks.AIR) || !mc.world.getBlockState(pos.up().up().up().west()).getBlock().equals(Blocks.AIR) || !mc.world.getBlockState(pos.up().up().up()).getBlock().equals(Blocks.AIR) || !mc.world.getEntitiesWithinAABBExcludingEntity(null, new AxisAlignedBB(pos.up().up().up())).isEmpty() || !mc.world.getEntitiesWithinAABBExcludingEntity(null, new AxisAlignedBB(pos.up().up().up().east())).isEmpty() || !mc.world.getEntitiesWithinAABBExcludingEntity(null, new AxisAlignedBB(pos.up().up().up().west())).isEmpty())
                    return new trolleyBus(-1, null, null);
                break;
        }
        return new trolleyBus(0, currSide, pos);
    }

    @Override
    public void onFrame(float partialTicks) {
        if (trolleyPos != null)
            RenderUtil.drawBox(trolleyPos.pos, new Color(-1));
    }

    public enum Side {
        NorthSouth,
        EastWest
    }

    public static class trolleyBus {
        int i;
        Side side;
        BlockPos pos;

        public trolleyBus(int i, Side side, BlockPos pos) {
            this.i = i;
            this.side = side;
            this.pos = pos;
        }
    }
}
