package dev.zprestige.ruby.module.combat;

import dev.zprestige.ruby.Ruby;
import dev.zprestige.ruby.module.Module;
import dev.zprestige.ruby.settings.impl.*;
import dev.zprestige.ruby.util.BlockUtil;
import dev.zprestige.ruby.util.EntityUtil;
import dev.zprestige.ruby.util.InventoryUtil;
import dev.zprestige.ruby.util.RenderUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class Filler extends Module {
    public static Filler Instance;
    public final Parent ranges = Menu.Parent("Ranges");
    public final Slider targetRange = Menu.Slider("Target Range", 0.0f, 6.0f).parent(ranges);
    public final Slider placeRange = Menu.Slider("Place Range", 0.0f, 6.0f).parent(ranges);
    public final Slider smartRange = Menu.Slider("Smart Range", 0.0f, 6.0f).parent(ranges);
    public final Parent modes = Menu.Parent("Modes");
    public final ComboBox smartMode = Menu.ComboBox("Mode", new String[]{"Linear", "Complete"}).parent(modes);
    public final ComboBox block = Menu.ComboBox("Block", new String[]{"Obsidian", "EChest", "Webs", "Fallback"}).parent(modes);
    public final Parent misc = Menu.Parent("Misc");
    public final Switch multitask = Menu.Switch("Multitask").parent(misc);
    public final Switch doubleHoles = Menu.Switch("Double Holes").parent(misc);
    public final Switch excludeY = Menu.Switch("Exclude Y").parent(misc);
    public final Switch packet = Menu.Switch("Packet").parent(misc);
    public final Switch rotate = Menu.Switch("Rotate").parent(misc);

    public final Parent rendering = Menu.Parent("Rendering");
    public final Switch render = Menu.Switch("Render").parent(rendering);
    public final ColorSwitch box = Menu.ColorSwitch("Place Box").parent(rendering);
    public final ColorSwitch outline = Menu.ColorSwitch("Place Outline").parent(rendering);
    public final Slider lineWidth = Menu.Slider("Place Line Width", 0.0f, 5.0f).parent(rendering);
    public final Slider fadeSpeed = Menu.Slider("Fade Speed", 100, 1000).parent(rendering);
    public HashMap<BlockPos, Integer> filledBlocks = new HashMap<>();

    public Filler() {
        Instance = this;
    }

    @Override
    public void onEnable() {
        filledBlocks.clear();
    }

    @Override
    public void onTick() {
        EntityPlayer entityPlayer = EntityUtil.getTarget(targetRange.GetSlider());
        if (entityPlayer == null) {
            return;
        }
        BlockPos targetPos = BlockUtil.getClosestHoleToPlayer(entityPlayer, smartRange.GetSlider(), doubleHoles.GetSwitch());
        if (targetPos == null)
            return;
        if (mc.player.getDistanceSq(targetPos) > (placeRange.GetSlider()) * placeRange.GetSlider())
            return;
        if (smartMode.GetCombo().equals("Linear") && (excludeY.GetSwitch() ? entityPlayer.getDistanceSq(new BlockPos(targetPos.getX(), entityPlayer.posY, targetPos.getZ())) : entityPlayer.getDistanceSq(targetPos)) > (smartRange.GetSlider() * smartRange.GetSlider()))
            return;
        if (!mc.world.getEntitiesWithinAABBExcludingEntity(null, new AxisAlignedBB(targetPos)).isEmpty() || !mc.world.getEntitiesWithinAABBExcludingEntity(null, new AxisAlignedBB(targetPos).setMaxY(1)).isEmpty())
            return;
        if (!multitask.GetSwitch() && mc.player.getHeldItemMainhand().getItem().equals(Items.GOLDEN_APPLE) && mc.gameSettings.keyBindUseItem.isKeyDown())
            return;
        int slot = -1;
        switch (block.GetCombo()) {
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
            BlockUtil.placeBlockWithSwitch(targetPos, EnumHand.MAIN_HAND, rotate.GetSwitch(), packet.GetSwitch(), slot);
        else {
            disableModule("No blocks found in hotbar, disabling Filler.");
            return;
        }
        filledBlocks.put(targetPos, box.GetColor().getAlpha());
    }

    @Override
    public void onGlobalRenderTick() {
        if (render.GetSwitch()) {
            for (Map.Entry<BlockPos, Integer> entry : filledBlocks.entrySet()) {
                filledBlocks.put(entry.getKey(), (int) (entry.getValue() - (fadeSpeed.GetSlider() / 200)));
                if (entry.getValue() <= 0) {
                    filledBlocks.remove(entry.getKey());
                    return;
                }
                try {
                    RenderUtil.drawBoxESP(entry.getKey(), new Color(box.GetColor().getRed(), box.GetColor().getGreen(), box.GetColor().getBlue(), entry.getValue()), true, new Color(outline.GetColor().getRed(), outline.GetColor().getGreen(), outline.GetColor().getBlue(), entry.getValue() * 2), lineWidth.GetSlider(), outline.GetSwitch(), box.GetSwitch(), entry.getValue(), true);
                } catch (Exception exception) {
                    Ruby.chatManager.sendRemovableMessage("Alpha parameter out of range (Choose a different Alpha)" + exception, 1);
                }
            }
        }
    }
}
