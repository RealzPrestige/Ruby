package dev.zprestige.ruby.module.combat;

import dev.zprestige.ruby.module.Module;
import dev.zprestige.ruby.settings.impl.ColorBox;
import dev.zprestige.ruby.settings.impl.Slider;
import dev.zprestige.ruby.settings.impl.Switch;
import dev.zprestige.ruby.util.BlockUtil;
import dev.zprestige.ruby.util.InventoryUtil;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.stream.IntStream;

public class AntiBed extends Module {
    public final ColorBox color = Menu.Color("Color");
    public final Switch enableTrigger = Menu.Switch("Enable Trigger");
    public final Switch packet = Menu.Switch("Packet");
    public final Switch rotate = Menu.Switch("Rotate");
    public final Switch retry = Menu.Switch("Retry");
    public final Slider retries = Menu.Slider("Retries", 1, 10);
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
        if (bedFagged || enableTrigger.GetSwitch()) {
            int slot = InventoryUtil.getItemFromHotbar(Item.getItemFromBlock(Blocks.OBSIDIAN));
            if (slot == -1)
                return;
            ArrayList<BlockPos> posses = getDefendPosses(pos);
            if (posses == null)
                return;
            IntStream.range(0, retry.GetSwitch() ? (int) retries.GetSlider() : 1).forEach(i -> posses.forEach(defendPos -> BlockUtil.placeBlockWithSwitch(defendPos, EnumHand.MAIN_HAND, rotate.GetSwitch(), packet.GetSwitch(), slot)));
        }

    }
}
