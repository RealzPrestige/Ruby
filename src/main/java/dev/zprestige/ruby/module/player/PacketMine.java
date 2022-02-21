package dev.zprestige.ruby.module.player;

import dev.zprestige.ruby.events.BlockInteractEvent;
import dev.zprestige.ruby.module.Category;
import dev.zprestige.ruby.module.Module;
import dev.zprestige.ruby.module.ModuleInfo;
import dev.zprestige.ruby.setting.impl.*;
import dev.zprestige.ruby.util.InventoryUtil;
import dev.zprestige.ruby.util.RenderUtil;
import dev.zprestige.ruby.util.Timer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.awt.*;
import java.util.Arrays;
import java.util.Objects;
import java.util.function.Predicate;

@ModuleInfo(name = "PacketMine", category = Category.Player, description = "vroom miner")
public class PacketMine extends Module {
    public static PacketMine Instance;
    public Timer timer = new Timer();
    public ParentSetting misc = createSetting("Misc");
    public BooleanSetting lmbBreak = createSetting("LMB Break", false).setParent(misc);
    public BooleanSetting posFix = createSetting("Pos Fix", false).setParent(misc);
    public BooleanSetting preSwitch = createSetting("Pre Switch", false).setParent(misc);
    public FloatSetting setNullRange = createSetting("Set Null Range", 5.0f, 0.1f, 20.0f).setParent(misc);
    public ParentSetting rendering = createSetting("Rendering");
    public ModeSetting renderMode = createSetting("Render Mode", "AlphaIncrease", Arrays.asList("AlphaIncrease", "AlphaDecrease", "Shrink", "Grow", "ShrinkGrow", "HeightIncrease", "HeightDecrease", "ShrinkGrowHeightIncrease", "ShrinkGrowHeightDecrease", "Complete")).setParent(rendering);
    public ModeSetting colorMode = createSetting("Color Mode", "Static", Arrays.asList("Static", "Fade")).setParent(rendering);
    public FloatSetting fadeBoxColorAlpha = createSetting("Fade Box Color Alpha", 255.0f, 0.0f, 255.0f, (Predicate<Float>) v -> colorMode.getValue().equals("Fade")).setParent(rendering);
    public FloatSetting fadeOutlineColorAlpha = createSetting("Fade Outline Color Alpha", 255.0f, 0.0f, 255.0f, (Predicate<Float>) v -> colorMode.getValue().equals("Fade")).setParent(rendering);
    public BooleanSetting box = createSetting("Box", false).setParent(rendering);
    public ColorSetting boxColor = createSetting("Box Color", new Color(-1), v -> box.getValue() && !colorMode.getValue().equals("Fade")).setParent(rendering);
    public BooleanSetting outline = createSetting("Outline", false).setParent(rendering);
    public ColorSetting outlineColor = createSetting("Outline Color", new Color(-1), v -> outline.getValue() && !colorMode.getValue().equals("Fade")).setParent(rendering);
    public FloatSetting outlineWidth = createSetting("Outline Width", 1.0f, 0.1f, 5.0f, (Predicate<Float>) v -> outline.getValue()).setParent(rendering);
    public FloatSetting maxAlpha = createSetting("Max Alpha", 255.0f, 0.0f, 255.0f, (Predicate<Float>) v -> renderMode.getValue().equals("AlphaIncrease")).setParent(rendering);
    public FloatSetting minAlpha = createSetting("Min Alpha", 0.0f, 0.0f, 255.0f, (Predicate<Float>) v -> renderMode.getValue().equals("AlphaDecrease")).setParent(rendering);
    public BlockPos currentPos;
    public float currState;
    public float boxRed;
    public float boxGreen;
    public float boxBlue;
    public float boxAlpha;
    public float outlineRed;
    public float outlineGreen;
    public float outlineBlue;
    public float outlineAlpha;

    public PacketMine() {
        Instance = this;
    }

    @Override
    public void onTick() {
        mc.playerController.blockHitDelay = 0;
        if (currentPos == null)
            return;
        if (currState < 1.0f) {
            if (mc.world.getBlockState(currentPos).getBlock().equals(Blocks.OBSIDIAN)) {
                currState += 0.025f;
            } else if (mc.world.getBlockState(currentPos).getBlock().equals(Blocks.ENDER_CHEST)) {
                currState += 0.05f;
            } else if (mc.world.getBlockState(currentPos).getBlock().equals(Blocks.NETHERRACK)) {
                currState += 0.5f;
            }
        }
        switch (colorMode.getValue()) {
            case "Static":
                boxRed = boxColor.getValue().getRed() / 255.0f;
                boxBlue = boxColor.getValue().getBlue() / 255.0f;
                boxGreen = boxColor.getValue().getGreen() / 255.0f;
                boxAlpha = boxColor.getValue().getAlpha() / 255.0f;
                outlineRed = outlineColor.getValue().getRed() / 255.0f;
                outlineGreen = outlineColor.getValue().getGreen() / 255.0f;
                outlineBlue = outlineColor.getValue().getBlue() / 255.0f;
                outlineAlpha = outlineColor.getValue().getAlpha() / 255.0f;
                break;
            case "Fade":
                boxRed = 1 - currState + (mc.world.getBlockState(currentPos).getBlock().equals(Blocks.OBSIDIAN) ? 0.025f : mc.world.getBlockState(currentPos).getBlock().equals(Blocks.NETHERRACK) ? 0.5f : 0.05f);
                boxGreen = currState - (mc.world.getBlockState(currentPos).getBlock().equals(Blocks.OBSIDIAN) ? 0.025f : mc.world.getBlockState(currentPos).getBlock().equals(Blocks.NETHERRACK) ? 0.5f : 0.05f);
                boxBlue = 0.0f;
                boxAlpha = fadeBoxColorAlpha.getValue() / 255.0f;
                outlineRed = 1 - currState + (mc.world.getBlockState(currentPos).getBlock().equals(Blocks.OBSIDIAN) ? 0.025f : mc.world.getBlockState(currentPos).getBlock().equals(Blocks.NETHERRACK) ? 0.5f : 0.05f);
                outlineGreen = currState - (mc.world.getBlockState(currentPos).getBlock().equals(Blocks.OBSIDIAN) ? 0.025f : mc.world.getBlockState(currentPos).getBlock().equals(Blocks.NETHERRACK) ? 0.5f : 0.05f);
                outlineBlue = 0.0f;
                outlineAlpha = fadeOutlineColorAlpha.getValue() / 255.0f;
                break;
        }
        if ((!mc.world.getBlockState(currentPos).equals(mc.world.getBlockState(currentPos)) || mc.world.getBlockState(currentPos).getBlock().equals(Blocks.AIR) || mc.world.getBlockState(currentPos).getBlock().equals(Blocks.BEDROCK)))
            currentPos = null;
        else if (mc.player.getDistanceSq(currentPos) > (setNullRange.getValue() * setNullRange.getValue())) {
            currentPos = null;
        }
        setHudString("Semi-Bypass");
        setHudStringColor(new Color(255, 255, 255));
    }

    @Override
    public void onGlobalRenderTick() {
        if (currentPos != null) {
            AxisAlignedBB bb = new AxisAlignedBB(currentPos);
            switch (renderMode.getValue()) {
                case "AlphaIncrease":
                    RenderUtil.drawFullBox(outline.getValue(), box.getValue(), new Color(boxRed, boxGreen, boxBlue, Math.min(currState, maxAlpha.getValue() / 255.0f)), new Color(outlineRed, outlineGreen, outlineBlue, Math.min(currState, maxAlpha.getValue() / 255.0f)), outlineWidth.getValue(), currentPos);
                    break;
                case "BreakIncrease":
                    RenderUtil.drawFullBox(outline.getValue(), box.getValue(), new Color(boxRed, boxGreen, boxBlue, Math.max(1 - currState, minAlpha.getValue() / 255.0f)), new Color(outlineRed, outlineGreen, outlineBlue, Math.max(1 - currState, minAlpha.getValue() / 255.0f)), outlineWidth.getValue(), currentPos);
                    break;
                case "Shrink":
                    bb = new AxisAlignedBB(currentPos).shrink(currState / 2f);
                    if (box.getValue())
                        RenderUtil.drawBBBox(bb, new Color(boxRed, boxGreen, boxBlue), (int) (boxAlpha * 255.0f));
                    if (outline.getValue())
                        RenderUtil.drawBlockOutlineBB(bb, new Color(outlineRed, outlineGreen, outlineBlue, outlineAlpha), outlineWidth.getValue());
                    break;
                case "Grow":
                    bb = new AxisAlignedBB(currentPos).shrink(0.5 + (currState / 2f));
                    if (box.getValue())
                        RenderUtil.drawBBBox(bb, new Color(boxRed, boxGreen, boxBlue), (int) (boxAlpha * 255.0f));
                    if (outline.getValue())
                        RenderUtil.drawBlockOutlineBB(bb, new Color(outlineRed, outlineGreen, outlineBlue, outlineAlpha), outlineWidth.getValue());
                    break;
                case "ShrinkGrow":
                    bb = new AxisAlignedBB(currentPos).shrink(currState);
                    if (box.getValue())
                        RenderUtil.drawBBBox(bb, new Color(boxRed, boxGreen, boxBlue), (int) (boxAlpha * 255.0f));
                    if (outline.getValue())
                        RenderUtil.drawBlockOutlineBB(bb, new Color(outlineRed, outlineGreen, outlineBlue, outlineAlpha), outlineWidth.getValue());
                    break;
                case "HeightIncrease":
                    if (box.getValue())
                        RenderUtil.drawBBBoxWithHeight(bb, new Color(boxRed, boxGreen, boxBlue), (int) (boxAlpha * 255.0f), currState);
                    if (outline.getValue())
                        RenderUtil.drawBlockOutlineBBWithHeight(bb, new Color(outlineRed, outlineGreen, outlineBlue, outlineAlpha), outlineWidth.getValue(), currState);
                    break;
                case "HeightDecrease":
                    if (box.getValue())
                        RenderUtil.drawBBBoxWithHeight(bb, new Color(boxRed, boxGreen, boxBlue), (int) (boxAlpha * 255.0f), 1 - currState);
                    if (outline.getValue())
                        RenderUtil.drawBlockOutlineBBWithHeight(bb, new Color(outlineRed, outlineGreen, outlineBlue, outlineAlpha), outlineWidth.getValue(), 1 - currState);
                    break;
                case "ShrinkGrowHeightIncrease":
                    bb = new AxisAlignedBB(currentPos).shrink(currState);
                    if (box.getValue())
                        RenderUtil.drawBBBoxWithHeight(bb, new Color(boxRed, boxGreen, boxBlue), (int) (boxAlpha * 255.0f), currState);
                    if (outline.getValue())
                        RenderUtil.drawBlockOutlineBBWithHeight(bb, new Color(outlineRed, outlineGreen, outlineBlue, outlineAlpha), outlineWidth.getValue(), currState);
                    break;
                case "ShrinkGrowHeightDecrease":
                    bb = new AxisAlignedBB(currentPos).shrink(currState);
                    if (box.getValue())
                        RenderUtil.drawBBBoxWithHeight(bb, new Color(boxRed, boxGreen, boxBlue), (int) (boxAlpha * 255.0f), 1 - currState);
                    if (outline.getValue())
                        RenderUtil.drawBlockOutlineBBWithHeight(bb, new Color(outlineRed, outlineGreen, outlineBlue, outlineAlpha), outlineWidth.getValue(), 1 - currState);
                    break;
                case "Complete":
                    if (box.getValue())
                        RenderUtil.drawBBBoxWithHeight(bb, new Color(boxRed, boxGreen, boxBlue), (int) (boxAlpha * 255.0f), currState > 0.25f ? currState - 0.25f : 0);
                    if (outline.getValue())
                        RenderUtil.drawBlockOutlineBBWithHeight(bb, new Color(outlineRed, outlineGreen, outlineBlue, outlineAlpha), outlineWidth.getValue(), currState > 0.25f ? currState - 0.25f : 0);

                    break;
            }
        }
    }

    @SubscribeEvent
    public void onBlockEvent(BlockInteractEvent.ClickBlock event) {
        if (nullCheck() || !isEnabled())
            return;
        if (mc.playerController.curBlockDamageMP > 0.1f)
            mc.playerController.isHittingBlock = true;
        if (posFix.getValue())
            currentPos = event.pos;
        int slot = InventoryUtil.getItemFromHotbar(Items.DIAMOND_PICKAXE);
        if (lmbBreak.getValue() && currState >= 1.0f && !mc.player.getHeldItemMainhand().getItem().equals(Items.DIAMOND_PICKAXE) && slot != -1 && currentPos != null && currentPos.equals(event.pos)) {
            int currentItem = mc.player.inventory.currentItem;
            InventoryUtil.switchToSlot(slot);
            Objects.requireNonNull(mc.getConnection()).sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, event.pos, event.facing));
            mc.player.inventory.currentItem = currentItem;
            mc.playerController.updateController();
        }
    }

    @SubscribeEvent
    public void onDamageBlock(BlockInteractEvent.DamageBlock event) {
        if (nullCheck() || !isEnabled())
            return;
        if (!mc.world.getBlockState(event.pos).getBlock().equals(Blocks.OBSIDIAN) && !mc.world.getBlockState(event.pos).getBlock().equals(Blocks.ENDER_CHEST) && !mc.world.getBlockState(event.pos).getBlock().equals(Blocks.NETHERRACK))
            return;
        mc.playerController.isHittingBlock = false;
        if (currentPos == null) {
            currentPos = event.pos;
            timer.setTime(0);
            currState = 0.0f;
        }
        int currentItem = mc.player.inventory.currentItem;
        if (preSwitch.getValue()) {
            int slot = InventoryUtil.getItemFromHotbar(Items.DIAMOND_PICKAXE);
            InventoryUtil.switchToSlot(slot);
        }
        mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.START_DESTROY_BLOCK, event.pos, event.facing));
        mc.player.swingArm(EnumHand.MAIN_HAND);
        mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, event.pos, event.facing));
        if (preSwitch.getValue()) {
            mc.player.inventory.currentItem = currentItem;
            mc.playerController.updateController();
        }
        event.setCanceled(true);
    }
}