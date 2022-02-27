package dev.zprestige.ruby.module.combat;

import dev.zprestige.ruby.module.Module;
import dev.zprestige.ruby.settings.impl.*;
import dev.zprestige.ruby.util.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;

public class Trap extends Module {
    public static Trap Instance;
    public final Parent placing = Menu.Parent("Placing");
    public final ComboBox placeMode = Menu.ComboBox("Place Mode", new String[]{"Linear", "Gradually"}).parent(placing);
    public final Slider placeDelay = Menu.Slider("Place Delay", 0, 500).parent(placing);
    public final Parent ranges = Menu.Parent("Ranges");
    public final Slider targetRange = Menu.Slider("Target Range", 0.1f, 15.0f).parent(ranges);
    public final Slider placeRange = Menu.Slider("Place Range", 0.1f, 6.0f).parent(ranges);
    public final Parent misc = Menu.Parent("Misc");
    public final Switch inLiquids = Menu.Switch("In Liquids").parent(misc);
    public final Switch extraTop = Menu.Switch("ExtraTop").parent(misc);
    public final Switch packet = Menu.Switch("Packet").parent(misc);
    public final Switch rotate = Menu.Switch("Rotate").parent(misc);
    public final Parent rendering = Menu.Parent("Rendering");
    public final Switch render = Menu.Switch("Render").parent(rendering);
    public final ColorSwitch box = Menu.ColorSwitch("Box").parent(rendering);
    public final ColorSwitch outline = Menu.ColorSwitch("Outline").parent(rendering);
    public final Slider outlineWidth = Menu.Slider("Outline Width", 0.1f, 5.0).parent(rendering);
    public BlockPos placePos = null;
    public Timer timer = new Timer();
    public ArrayList<BlockPos> firstLayerPosses = new ArrayList<>();
    public BlockPos postFirstLayerPos = null;

    public Trap() {
        Instance = this;
    }

    @Override
    public void onTick() {
        EntityPlayer entityPlayer = EntityUtil.getTarget(targetRange.GetSlider());
        if (entityPlayer == null || !BlockUtil.isPlayerSafe(entityPlayer)) {
            disableModule();
            return;
        }
        int slot = InventoryUtil.getItemFromHotbar(Item.getItemFromBlock(Blocks.OBSIDIAN));
        if (slot == -1) {
            disableModule("No Obsidian found in hotbar, disabling Trap");
            return;
        }
        BlockPos entityPlayerPos = EntityUtil.getPlayerPos(entityPlayer);
        switch (placeMode.GetCombo()) {
            case "Gradually":
                setPlacePos(entityPlayerPos);
                if (placePos != null && mc.player.getDistanceSq(placePos) < (placeRange.GetSlider() * placeRange.GetSlider()) && timer.getTime((long) placeDelay.GetSlider()))
                    BlockUtil.placeBlockWithSwitch(placePos, EnumHand.MAIN_HAND, rotate.GetSwitch(), packet.GetSwitch(), slot, timer);
                break;
            case "Linear":
                setFirstLayer(entityPlayerPos);
                if (!firstLayerPosses.isEmpty())
                    firstLayerPosses.stream().filter(pos -> mc.player.getDistanceSq(pos) < (placeRange.GetSlider() * placeRange.GetSlider())).forEach(pos -> BlockUtil.placeBlockWithSwitch(pos, EnumHand.MAIN_HAND, rotate.GetSwitch(), packet.GetSwitch(), slot));
                else if (canPlace(entityPlayerPos.up().up().north()) && canPlace(entityPlayerPos.up().up().east()) && canPlace(entityPlayerPos.up().up().south()) && canPlace(entityPlayerPos.up().up().west())) {
                    if (mc.player.getDistanceSq(entityPlayerPos.up().up().north()) < (placeRange.GetSlider() * placeRange.GetSlider())) {
                        BlockUtil.placeBlockWithSwitch(entityPlayerPos.up().up().north(), EnumHand.MAIN_HAND, rotate.GetSwitch(), packet.GetSwitch(), slot);
                        postFirstLayerPos = entityPlayerPos.up().up().north();
                    }
                } else if (canPlace(entityPlayerPos.up().up())) {
                    if (mc.player.getDistanceSq(entityPlayerPos.up().up()) < (placeRange.GetSlider() * placeRange.GetSlider())) {
                        BlockUtil.placeBlockWithSwitch(entityPlayerPos.up().up(), EnumHand.MAIN_HAND, rotate.GetSwitch(), packet.GetSwitch(), slot);
                        postFirstLayerPos = entityPlayerPos.up().up();
                    }
                } else if (extraTop.GetSwitch() && canPlace(entityPlayerPos.up().up().up())) {
                    if (mc.player.getDistanceSq(entityPlayerPos.up().up().up()) < (placeRange.GetSlider() * placeRange.GetSlider())) {
                        BlockUtil.placeBlockWithSwitch(entityPlayerPos.up().up().up(), EnumHand.MAIN_HAND, rotate.GetSwitch(), packet.GetSwitch(), slot);
                        postFirstLayerPos = entityPlayerPos.up().up().up();
                    }
                } else
                    postFirstLayerPos = null;

                break;
        }
    }


    public void setFirstLayer(BlockPos pos) {
        firstLayerPosses.clear();
        if (canPlace(pos.up().north()))
            firstLayerPosses.add(pos.up().north());
        if (canPlace(pos.up().east()))
            firstLayerPosses.add(pos.up().east());
        if (canPlace(pos.up().south()))
            firstLayerPosses.add(pos.up().south());
        if (canPlace(pos.up().west()))
            firstLayerPosses.add(pos.up().west());
    }

    public void setPlacePos(BlockPos pos) {
        if (canPlace(pos.up().north()))
            placePos = pos.up().north();
        else if (canPlace(pos.up().east()))
            placePos = pos.up().east();
        else if (canPlace(pos.up().south()))
            placePos = pos.up().south();
        else if (canPlace(pos.up().west()))
            placePos = pos.up().west();
        else if (canPlace(pos.up().up().north()) && canPlace(pos.up().up().east()) && canPlace(pos.up().up().south()) && canPlace(pos.up().up().west()))
            placePos = pos.up().up().north();
        else if (canPlace(pos.up().up()))
            placePos = pos.up().up();
        else if (extraTop.GetSwitch() && canPlace(pos.up().up().up()))
            placePos = pos.up().up().up();
        else
            placePos = null;
    }

    public boolean canPlace(BlockPos pos) {
        return mc.world.getBlockState(pos).getBlock().equals(Blocks.AIR) || (inLiquids.GetSwitch() && ((mc.world.getBlockState(pos).getBlock().equals(Blocks.WATER) || mc.world.getBlockState(pos).getBlock().equals(Blocks.FLOWING_WATER)) || (mc.world.getBlockState(pos).getBlock().equals(Blocks.LAVA) || mc.world.getBlockState(pos).getBlock().equals(Blocks.FLOWING_LAVA))));
    }

    @Override
    public void onFrame(float partialTicks) {
        switch (placeMode.GetCombo()) {
            case "Gradually":
                if (placePos == null || !render.GetSwitch())
                    return;
                RenderUtil.drawBoxESP(placePos, box.GetColor(), true, outline.GetColor(), outlineWidth.GetSlider(), outline.GetSwitch(), box.GetSwitch(), box.GetColor().getAlpha(), true);
                break;
            case "Linear":
                if (!firstLayerPosses.isEmpty())
                    firstLayerPosses.forEach(pos -> RenderUtil.drawBoxESP(pos, box.GetColor(), true, outline.GetColor(), outlineWidth.GetSlider(), outline.GetSwitch(), box.GetSwitch(), box.GetColor().getAlpha(), true));
                else if (postFirstLayerPos != null) {
                    RenderUtil.drawBoxESP(postFirstLayerPos, box.GetColor(), true, outline.GetColor(), outlineWidth.GetSlider(), outline.GetSwitch(), box.GetSwitch(), box.GetColor().getAlpha(), true);
                }
                break;
        }
    }
}
