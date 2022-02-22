package dev.zprestige.ruby.module.combat;

import dev.zprestige.ruby.module.Module;
import dev.zprestige.ruby.setting.impl.*;
import dev.zprestige.ruby.util.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.function.Predicate;

public class Trap extends Module {
    public static Trap Instance;
    public ParentSetting placing = createSetting("Placing");
    public ModeSetting placeMode = createSetting("Place Mode", "Linear", Arrays.asList("Linear", "Gradually")).setParent(placing);
    public IntegerSetting placeDelay = createSetting("Place Delay", 50, 0, 500).setParent(placing);
    public ParentSetting ranges = createSetting("Ranges");
    public FloatSetting targetRange = createSetting("Target Range", 9.0f, 0.1f, 15.0f).setParent(ranges);
    public FloatSetting placeRange = createSetting("Place Range", 5.0f, 0.1f, 6.0f).setParent(ranges);
    public ParentSetting misc = createSetting("Misc");
    public BooleanSetting inLiquids = createSetting("In Liquids", false).setParent(misc);
    public BooleanSetting extraTop = createSetting("ExtraTop", false).setParent(misc);
    public BooleanSetting packet = createSetting("Packet", false).setParent(misc);
    public BooleanSetting rotate = createSetting("Rotate", false).setParent(misc);
    public ParentSetting rendering = createSetting("Rendering");
    public BooleanSetting render = createSetting("Render", false).setParent(rendering);
    public BooleanSetting box = createSetting("Box", false, v -> render.getValue()).setParent(rendering);
    public ColorSetting boxColor = createSetting("Box Color", new Color(-1), v -> render.getValue() && box.getValue()).setParent(rendering);
    public BooleanSetting outline = createSetting("Outline", false, v -> render.getValue()).setParent(rendering);
    public ColorSetting outlineColor = createSetting("Outline Color", new Color(-1), v -> render.getValue() && outline.getValue()).setParent(rendering);
    public FloatSetting outlineWidth = createSetting("Outline Width", 1.0f, 0.1f, 5.0f, (Predicate<Float>) v -> render.getValue() && outline.getValue()).setParent(rendering);
    public BlockPos placePos = null;
    public Timer timer = new Timer();
    public ArrayList<BlockPos> firstLayerPosses = new ArrayList<>();
    public BlockPos postFirstLayerPos = null;

    public Trap(){
        Instance = this;
    }

    @Override
    public void onTick() {
        EntityPlayer entityPlayer = EntityUtil.getTarget(targetRange.getValue());
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
        switch (placeMode.getValue()) {
            case "Gradually":
                setPlacePos(entityPlayerPos);
                if (placePos != null && mc.player.getDistanceSq(placePos) < (placeRange.getValue() * placeRange.getValue()) && timer.getTime(placeDelay.getValue()))
                    BlockUtil.placeBlockWithSwitch(placePos, EnumHand.MAIN_HAND, rotate.getValue(), packet.getValue(), slot, timer);
                break;
            case "Linear":
                setFirstLayer(entityPlayerPos);
                if (!firstLayerPosses.isEmpty())
                    firstLayerPosses.stream().filter(pos -> mc.player.getDistanceSq(pos) < (placeRange.getValue() * placeRange.getValue())).forEach(pos -> BlockUtil.placeBlockWithSwitch(pos, EnumHand.MAIN_HAND, rotate.getValue(), packet.getValue(), slot));
                else if (canPlace(entityPlayerPos.up().up().north()) && canPlace(entityPlayerPos.up().up().east()) && canPlace(entityPlayerPos.up().up().south()) && canPlace(entityPlayerPos.up().up().west())) {
                   if (mc.player.getDistanceSq(entityPlayerPos.up().up().north()) < (placeRange.getValue() * placeRange.getValue())) {
                        BlockUtil.placeBlockWithSwitch(entityPlayerPos.up().up().north(), EnumHand.MAIN_HAND, rotate.getValue(), packet.getValue(), slot);
                        postFirstLayerPos = entityPlayerPos.up().up().north();
                    }
                }
                else if (canPlace(entityPlayerPos.up().up())) {
                    if (mc.player.getDistanceSq(entityPlayerPos.up().up()) < (placeRange.getValue() * placeRange.getValue())) {
                        BlockUtil.placeBlockWithSwitch(entityPlayerPos.up().up(), EnumHand.MAIN_HAND, rotate.getValue(), packet.getValue(), slot);
                        postFirstLayerPos = entityPlayerPos.up().up();
                    }
                } else if (extraTop.getValue() && canPlace(entityPlayerPos.up().up().up())) {
                    if (mc.player.getDistanceSq(entityPlayerPos.up().up().up()) < (placeRange.getValue() * placeRange.getValue())) {
                        BlockUtil.placeBlockWithSwitch(entityPlayerPos.up().up().up(), EnumHand.MAIN_HAND, rotate.getValue(), packet.getValue(), slot);
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
        else if (extraTop.getValue() && canPlace(pos.up().up().up()))
            placePos = pos.up().up().up();
        else
            placePos = null;
    }

    public boolean canPlace(BlockPos pos){
        return mc.world.getBlockState(pos).getBlock().equals(Blocks.AIR) || (inLiquids.getValue() && ((mc.world.getBlockState(pos).getBlock().equals(Blocks.WATER) || mc.world.getBlockState(pos).getBlock().equals(Blocks.FLOWING_WATER))|| (mc.world.getBlockState(pos).getBlock().equals(Blocks.LAVA) || mc.world.getBlockState(pos).getBlock().equals(Blocks.FLOWING_LAVA))));
    }

    @Override
    public void onGlobalRenderTick() {
        switch (placeMode.getValue()) {
            case "Gradually":
                if (placePos == null || !render.getValue())
                    return;
                RenderUtil.drawBoxESP(placePos, boxColor.getValue(), true, outlineColor.getValue(), outlineWidth.getValue(), outline.getValue(), box.getValue(), boxColor.getValue().getAlpha(), true);
                break;
            case "Linear":
                if (!firstLayerPosses.isEmpty())
                    firstLayerPosses.forEach(pos -> RenderUtil.drawBoxESP(pos, boxColor.getValue(), true, outlineColor.getValue(), outlineWidth.getValue(), outline.getValue(), box.getValue(), boxColor.getValue().getAlpha(), true));
                else if (postFirstLayerPos != null){
                    RenderUtil.drawBoxESP(postFirstLayerPos, boxColor.getValue(), true, outlineColor.getValue(), outlineWidth.getValue(), outline.getValue(), box.getValue(), boxColor.getValue().getAlpha(), true);
                }
                break;
        }
    }
}
