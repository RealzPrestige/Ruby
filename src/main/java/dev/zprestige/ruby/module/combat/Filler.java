package dev.zprestige.ruby.module.combat;

import dev.zprestige.ruby.module.Module;
import dev.zprestige.ruby.setting.impl.*;
import dev.zprestige.ruby.util.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;

import java.awt.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

public class Filler extends Module {
    public static Filler Instance;
    public ParentSetting ranges = createSetting("Ranges");
    public FloatSetting targetRange = createSetting("Target Range", 5.0f, 0.0f, 6.0f).setParent(ranges);
    public FloatSetting placeRange = createSetting("Place Range", 5.0f, 0.0f, 6.0f).setParent(ranges);
    public FloatSetting smartRange = createSetting("Smart Range", 5.0f, 0.0f, 6.0f).setParent(ranges);
    public ParentSetting modes = createSetting("Modes");
    public ModeSetting smartMode = createSetting("Mode", "Linear", Arrays.asList("Linear", "Complete")).setParent(modes);
    public ModeSetting block = createSetting("Block", "Obsidian", Arrays.asList("Obsidian", "EChest", "Webs", "Fallback")).setParent(modes);
    public ParentSetting misc = createSetting("Misc");
    public BooleanSetting multitask = createSetting("Multitask", false).setParent(misc);
    public BooleanSetting doubleHoles = createSetting("Double Holes", false).setParent(misc);
    public BooleanSetting excludeY = createSetting("Exclude Y", false).setParent(misc);
    public BooleanSetting packet = createSetting("Packet", false).setParent(misc);
    public BooleanSetting rotate = createSetting("Rotate", false).setParent(misc);

    public ParentSetting rendering = createSetting("Rendering");
    public BooleanSetting render = createSetting("Render", false).setParent(rendering);
    public BooleanSetting box = createSetting("Place Box", false, v -> render.getValue()).setParent(rendering);
    public ColorSetting boxColor = createSetting("Place Box Color", new Color(0xFFFFFF), v -> box.getValue() && render.getValue()).setParent(rendering);
    public BooleanSetting outline = createSetting("Place Outline", false, v -> render.getValue()).setParent(rendering);
    public ColorSetting outlineColor = createSetting("Place Outline Color", new Color(0xFFFFFF), v -> outline.getValue() && render.getValue()).setParent(rendering);
    public FloatSetting lineWidth = createSetting("Place Line Width", 1.0f, 0.0f, 5.0f, (Predicate<Float>) v -> outline.getValue() && render.getValue()).setParent(rendering);
    public IntegerSetting fadeSpeed = createSetting("Fade Speed", 200, 100, 1000, (Predicate<Integer>) v -> render.getValue()).setParent(rendering);
    public HashMap<BlockPos, Integer> filledBlocks = new HashMap<>();

    public Filler(){
        Instance = this;
    }

    @Override
    public void onEnable() {
        filledBlocks.clear();
    }

    @Override
    public void onTick() {
        EntityPlayer entityPlayer = EntityUtil.getTarget(targetRange.getValue());
        if (entityPlayer == null) {
            return;
        }
        BlockPos targetPos = BlockUtil.getClosestHoleToPlayer(entityPlayer, smartRange.getValue(), doubleHoles.getValue());
        if (targetPos == null)
            return;
        if (mc.player.getDistanceSq(targetPos) > (placeRange.getValue() * placeRange.getValue()))
            return;
        if (smartMode.getValue().equals("Linear") && (excludeY.getValue() ? entityPlayer.getDistanceSq(new BlockPos(targetPos.getX(), entityPlayer.posY, targetPos.getZ())) : entityPlayer.getDistanceSq(targetPos)) > (smartRange.getValue() * smartRange.getValue()))
            return;
        if (!mc.world.getEntitiesWithinAABBExcludingEntity(null, new AxisAlignedBB(targetPos)).isEmpty() || !mc.world.getEntitiesWithinAABBExcludingEntity(null, new AxisAlignedBB(targetPos).setMaxY(1)).isEmpty())
            return;
        if (!multitask.getValue() && mc.player.getHeldItemMainhand().getItem().equals(Items.GOLDEN_APPLE) && mc.gameSettings.keyBindUseItem.isKeyDown())
            return;
        int slot = -1;
        switch (block.getValue()) {
            case "Obsidian":
                slot = InventoryUtil.getItemFromHotbar(Item.getItemFromBlock(Blocks.OBSIDIAN));
                break;
            case "EChest":
                slot = InventoryUtil.getItemFromHotbar(Item.getItemFromBlock(Blocks.ENDER_CHEST));
                break;
            case "Webs":
                slot = InventoryUtil.getItemFromHotbar(Item.getItemFromBlock(Blocks.WEB));
                break;
            case "Fallback":
                slot = InventoryUtil.getItemFromHotbar(Item.getItemFromBlock(Blocks.OBSIDIAN));
                if (slot == -1)
                    slot = InventoryUtil.getItemFromHotbar(Item.getItemFromBlock(Blocks.ENDER_CHEST));
                break;
        }
        if (slot != -1)
            BlockUtil.placeBlockWithSwitch(targetPos, EnumHand.MAIN_HAND, rotate.getValue(), packet.getValue(), slot);
        else {
            disableModule("No blocks found in hotbar, disabling Filler.");
            return;
        }
        filledBlocks.put(targetPos, boxColor.getValue().getAlpha());
    }

    @Override
    public void onGlobalRenderTick() {
        if (render.getValue()) {
            for (Map.Entry<BlockPos, Integer> entry : filledBlocks.entrySet()) {
                filledBlocks.put(entry.getKey(), entry.getValue() - (fadeSpeed.getValue() / 200));
                if (entry.getValue() <= 0) {
                    filledBlocks.remove(entry.getKey());
                    return;
                }
                try {
                    RenderUtil.drawBoxESP(entry.getKey(), new Color(boxColor.getValue().getRed(), boxColor.getValue().getGreen(), boxColor.getValue().getBlue(), entry.getValue()), true, new Color(outlineColor.getValue().getRed(), outlineColor.getValue().getGreen(), outlineColor.getValue().getBlue(), entry.getValue() * 2), lineWidth.getValue(), outline.getValue(), box.getValue(), entry.getValue(), true);
                } catch (Exception exception){
                    MessageUtil.sendRemovableMessage("Alpha parameter out of range (Choose a different Alpha)" + exception, 1);
                }
            }
        }
    }
}
