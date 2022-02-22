package dev.zprestige.ruby.module.combat;

import dev.zprestige.ruby.module.Module;
import dev.zprestige.ruby.setting.impl.BooleanSetting;
import dev.zprestige.ruby.setting.impl.FloatSetting;
import dev.zprestige.ruby.setting.impl.IntegerSetting;
import dev.zprestige.ruby.setting.impl.ParentSetting;
import dev.zprestige.ruby.util.BlockUtil;
import dev.zprestige.ruby.util.InventoryUtil;
import dev.zprestige.ruby.util.Timer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;

import java.util.TreeMap;

public class AntiCity extends Module {
    public ParentSetting timing = createSetting("Timing");
     public IntegerSetting placeDelay = createSetting("Place Delay", 50, 0, 1000).setParent(timing);
    public FloatSetting placeRange = createSetting("Place Range", 5.0f, 0.1f, 6.0f).setParent(timing);
    public ParentSetting misc = createSetting("Misc");
    public BooleanSetting inLiquids = createSetting("In Liquids", false).setParent(misc);
    public BooleanSetting rotate = createSetting("Rotate", false).setParent(misc);
    public BooleanSetting packet = createSetting("Packet", false).setParent(misc);
    public Timer timer = new Timer();

    @Override
    public void onTick() {
        if (timer.getTime(placeDelay.getValue())) {
            BlockPos pos = getNextPos();
            if (pos == null) {
                disableModule("No posses found, disabling AntiCity");
                return;
            }
            BlockUtil.placeBlockWithSwitch(pos, EnumHand.MAIN_HAND, rotate.getValue(), packet.getValue(), InventoryUtil.getItemFromHotbar(Item.getItemFromBlock(Blocks.OBSIDIAN)), timer);
        }
    }

    public BlockPos getNextPos() {
        TreeMap<Float, BlockPos> posTreeMap = new TreeMap<>();
        for (BlockPos pos : BlockUtil.getSphere(placeRange.getValue() - 1.0f, BlockUtil.AirType.OnlyAir, mc.player)) {
            if (!isPosSurroundedByBlocks(pos) || !mc.world.getEntitiesWithinAABBExcludingEntity(null, new AxisAlignedBB(pos)).isEmpty())
                continue;
            posTreeMap.put((float) mc.player.getDistanceSq(pos), pos);
        }
        if (!posTreeMap.isEmpty())
            return posTreeMap.firstEntry().getValue();
        return null;
    }

    public boolean isPosSurroundedByBlocks(BlockPos blockPos) {
        return canPlace(blockPos.north()) || canPlace(blockPos.east()) || canPlace(blockPos.south()) || canPlace(blockPos.west()) || canPlace(blockPos.down()) || canPlace(blockPos.up());
    }

    public boolean canPlace(BlockPos pos) {
        return !mc.world.getBlockState(pos).getBlock().equals(Blocks.AIR) && (!inLiquids.getValue() || ((!mc.world.getBlockState(pos).getBlock().equals(Blocks.WATER) && !mc.world.getBlockState(pos).getBlock().equals(Blocks.FLOWING_WATER)) && (!mc.world.getBlockState(pos).getBlock().equals(Blocks.LAVA) && !mc.world.getBlockState(pos).getBlock().equals(Blocks.FLOWING_LAVA))));
    }
}
