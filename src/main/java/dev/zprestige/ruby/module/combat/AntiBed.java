package dev.zprestige.ruby.module.combat;

import dev.zprestige.ruby.module.Category;
import dev.zprestige.ruby.module.Module;
import dev.zprestige.ruby.module.ModuleInfo;
import dev.zprestige.ruby.setting.impl.BooleanSetting;
import dev.zprestige.ruby.setting.impl.ColorSetting;
import dev.zprestige.ruby.setting.impl.IntegerSetting;
import dev.zprestige.ruby.util.BlockUtil;
import dev.zprestige.ruby.util.InventoryUtil;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;

import java.awt.*;
import java.util.ArrayList;
import java.util.function.Predicate;
import java.util.stream.IntStream;

@ModuleInfo(name = "AntiBed", category = Category.Combat, description = "Fuck ecme niggers")
public class AntiBed extends Module {
    public ColorSetting color = createSetting("Color", new Color(0xFF6565));
    public BooleanSetting enableTrigger = createSetting("Enable Trigger", false);
    public BooleanSetting packet = createSetting("Packet", false);
    public BooleanSetting rotate = createSetting("Rotate", false);
    public BooleanSetting retry = createSetting("Retry", false);
    public IntegerSetting retries = createSetting("Retries", 1, 1, 10, (Predicate<Integer>) v -> retry.getValue());
    public boolean bedFagged;

    public ArrayList<BlockPos> getDefendPosses(BlockPos pos) {
        ArrayList<BlockPos> posses = new ArrayList<>();
        if (mc.world.getBlockState(pos.north()).getBlock().equals(Blocks.AIR))
            posses.add(pos.north());
        if (mc.world.getBlockState(pos.east()).getBlock().equals(Blocks.AIR))
            posses.add(pos.east());
        if (mc.world.getBlockState(pos.south()).getBlock().equals(Blocks.AIR))
            posses.add(pos.south());
        if (mc.world.getBlockState(pos.west()).getBlock().equals(Blocks.AIR))
            posses.add(pos.west());
        if (!posses.isEmpty())
            return posses;

        return null;
    }

    @Override
    public void onTick() {
        BlockPos pos = BlockUtil.getPlayerPos().up();
        if (mc.world.getBlockState(pos).getBlock().equals(Blocks.BED))
            bedFagged = true;
        if (!BlockUtil.isPlayerSafe(mc.player) || !mc.player.onGround)
            bedFagged = false;
        bedFagged = mc.world.getBlockState(pos).getBlock().equals(Blocks.BED);
        if (bedFagged || enableTrigger.getValue()) {
            int slot = InventoryUtil.getItemFromHotbar(Item.getItemFromBlock(Blocks.OBSIDIAN));
            if (slot == -1)
                return;
            ArrayList<BlockPos> posses = getDefendPosses(pos);
            if (posses == null)
                return;
            IntStream.range(0, (retry.getValue() ? retries.getValue() : 1)).forEach(i -> posses.forEach(defendPos -> BlockUtil.placeBlockWithSwitch(defendPos, EnumHand.MAIN_HAND, rotate.getValue(), packet.getValue(), slot)));
        }

    }
}
