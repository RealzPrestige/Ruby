package dev.zprestige.ruby.module.combat;

import dev.zprestige.ruby.module.Module;
import dev.zprestige.ruby.newsettings.impl.Parent;
import dev.zprestige.ruby.newsettings.impl.Slider;
import dev.zprestige.ruby.newsettings.impl.Switch;
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
    public final Parent timing = Menu.Parent("Timing");
    public final Slider placeDelay = Menu.Slider("Place Delay", 0, 1000).parent(timing);
    public final Slider placeRange = Menu.Slider("Place Range", 0.1f, 6.0f).parent(timing);
    public final Parent misc = Menu.Parent("Misc");
    public final Switch inLiquids = Menu.Switch("In Liquids").parent(misc);
    public final Switch rotate = Menu.Switch("Rotate").parent(misc);
    public final Switch packet = Menu.Switch("Packet").parent(misc);
    public Timer timer = new Timer();

    @Override
    public void onTick() {
        if (timer.getTime((long) placeDelay.GetSlider())) {
            BlockPos pos = getNextPos();
            if (pos == null) {
                disableModule("No posses found, disabling AntiCity");
                return;
            }
            BlockUtil.placeBlockWithSwitch(pos, EnumHand.MAIN_HAND, rotate.GetSwitch(), packet.GetSwitch(), InventoryUtil.getItemFromHotbar(Item.getItemFromBlock(Blocks.OBSIDIAN)), timer);
        }
    }

    public BlockPos getNextPos() {
        TreeMap<Float, BlockPos> posTreeMap = new TreeMap<>();
        for (BlockPos pos : BlockUtil.getSphere(placeRange.GetSlider() - 1.0f, BlockUtil.AirType.OnlyAir, mc.player)) {
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
        return !mc.world.getBlockState(pos).getBlock().equals(Blocks.AIR) && (!inLiquids.GetSwitch() || ((!mc.world.getBlockState(pos).getBlock().equals(Blocks.WATER) && !mc.world.getBlockState(pos).getBlock().equals(Blocks.FLOWING_WATER)) && (!mc.world.getBlockState(pos).getBlock().equals(Blocks.LAVA) && !mc.world.getBlockState(pos).getBlock().equals(Blocks.FLOWING_LAVA))));
    }
}
