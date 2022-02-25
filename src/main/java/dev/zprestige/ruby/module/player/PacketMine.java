package dev.zprestige.ruby.module.player;

import dev.zprestige.ruby.eventbus.annotation.RegisterListener;
import dev.zprestige.ruby.events.BlockInteractEvent;
import dev.zprestige.ruby.module.Module;
import dev.zprestige.ruby.newsettings.impl.*;
import dev.zprestige.ruby.util.InventoryUtil;
import dev.zprestige.ruby.util.RenderUtil;
import dev.zprestige.ruby.util.Timer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;

import java.awt.*;
import java.util.Objects;

public class PacketMine extends Module {
    public static PacketMine Instance;
    public Timer timer = new Timer();
    public final Parent misc = Menu.Parent("Misc");
    public final Switch lmbBreak = Menu.Switch("LMB Break").parent(misc);
    public final Switch posFix = Menu.Switch("Pos Fix").parent(misc);
    public final Switch preSwitch = Menu.Switch("Pre Switch").parent(misc);
    public final Slider setNullRange = Menu.Slider("Set Null Range", 0.1f, 20.0f).parent(misc);
    public final Parent rendering = Menu.Parent("Rendering");
    public final ComboBox renderMode = Menu.ComboBox("Render Mode", new String[]{"AlphaIncrease", "AlphaDecrease", "Shrink", "Grow", "ShrinkGrow", "HeightIncrease", "HeightDecrease", "ShrinkGrowHeightIncrease", "ShrinkGrowHeightDecrease"}).parent(rendering);
    public final ComboBox colorMode = Menu.ComboBox("Color Mode", new String[]{"Static", "Fade"}).parent(rendering);
    public final Slider fadeBoxColorAlpha = Menu.Slider("Fade Box Color Alpha", 0.0f, 255.0f).parent(rendering);
    public final Slider fadeOutlineColorAlpha = Menu.Slider("Fade Outline Color Alpha", 0.0f, 255.0f).parent(rendering);
    public final ColorSwitch box = Menu.ColorSwitch("Box").parent(rendering);
    public final ColorSwitch outline = Menu.ColorSwitch("Outline").parent(rendering);
    public final Slider outlineWidth = Menu.Slider("Outline Width", 0.1f, 5.0f).parent(rendering);
    public final Slider maxAlpha = Menu.Slider("Max Alpha", 0.0f, 255.0f).parent(rendering);
    public final Slider minAlpha = Menu.Slider("Min Alpha", 0.0f, 255.0f).parent(rendering);
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
        switch (colorMode.GetCombo()) {
            case "Static":
                boxRed = box.GetColor().getRed() / 255.0f;
                boxBlue = box.GetColor().getBlue() / 255.0f;
                boxGreen = box.GetColor().getGreen() / 255.0f;
                boxAlpha = box.GetColor().getAlpha() / 255.0f;
                outlineRed = outline.GetColor().getRed() / 255.0f;
                outlineGreen = outline.GetColor().getGreen() / 255.0f;
                outlineBlue = outline.GetColor().getBlue() / 255.0f;
                outlineAlpha = outline.GetColor().getAlpha() / 255.0f;
                break;
            case "Fade":
                boxRed = 1 - currState + (mc.world.getBlockState(currentPos).getBlock().equals(Blocks.OBSIDIAN) ? 0.025f : mc.world.getBlockState(currentPos).getBlock().equals(Blocks.NETHERRACK) ? 0.5f : 0.05f);
                boxGreen = currState - (mc.world.getBlockState(currentPos).getBlock().equals(Blocks.OBSIDIAN) ? 0.025f : mc.world.getBlockState(currentPos).getBlock().equals(Blocks.NETHERRACK) ? 0.5f : 0.05f);
                boxBlue = 0.0f;
                boxAlpha = fadeBoxColorAlpha.GetSlider() / 255.0f;
                outlineRed = 1 - currState + (mc.world.getBlockState(currentPos).getBlock().equals(Blocks.OBSIDIAN) ? 0.025f : mc.world.getBlockState(currentPos).getBlock().equals(Blocks.NETHERRACK) ? 0.5f : 0.05f);
                outlineGreen = currState - (mc.world.getBlockState(currentPos).getBlock().equals(Blocks.OBSIDIAN) ? 0.025f : mc.world.getBlockState(currentPos).getBlock().equals(Blocks.NETHERRACK) ? 0.5f : 0.05f);
                outlineBlue = 0.0f;
                outlineAlpha = fadeOutlineColorAlpha.GetSlider() / 255.0f;
                break;
        }
        if ((!mc.world.getBlockState(currentPos).equals(mc.world.getBlockState(currentPos)) || mc.world.getBlockState(currentPos).getBlock().equals(Blocks.AIR) || mc.world.getBlockState(currentPos).getBlock().equals(Blocks.BEDROCK)))
            currentPos = null;
        else if (mc.player.getDistanceSq(currentPos) > (setNullRange.GetSlider() * setNullRange.GetSlider())) {
            currentPos = null;
        }
    }

    @Override
    public void onGlobalRenderTick() {
        if (currentPos != null) {
            AxisAlignedBB bb = new AxisAlignedBB(currentPos);
            switch (renderMode.GetCombo()) {
                case "AlphaIncrease":
                    RenderUtil.drawFullBox(outline.GetSwitch(), box.GetSwitch(), new Color(boxRed, boxGreen, boxBlue, Math.min(currState, maxAlpha.GetSlider() / 255.0f)), new Color(outlineRed, outlineGreen, outlineBlue, Math.min(currState, maxAlpha.GetSlider() / 255.0f)), outlineWidth.GetSlider(), currentPos);
                    break;
                case "BreakIncrease":
                    RenderUtil.drawFullBox(outline.GetSwitch(), box.GetSwitch(), new Color(boxRed, boxGreen, boxBlue, Math.max(1 - currState, minAlpha.GetSlider() / 255.0f)), new Color(outlineRed, outlineGreen, outlineBlue, Math.max(1 - currState, minAlpha.GetSlider() / 255.0f)), outlineWidth.GetSlider(), currentPos);
                    break;
                case "Shrink":
                    bb = new AxisAlignedBB(currentPos).shrink(currState / 2f);
                    if (box.GetSwitch())
                        RenderUtil.drawBBBox(bb, new Color(boxRed, boxGreen, boxBlue), (int) (boxAlpha * 255.0f));
                    if (outline.GetSwitch())
                        RenderUtil.drawBlockOutlineBB(bb, new Color(outlineRed, outlineGreen, outlineBlue, outlineAlpha), outlineWidth.GetSlider());
                    break;
                case "Grow":
                    bb = new AxisAlignedBB(currentPos).shrink(0.5 + (currState / 2f));
                    if (box.GetSwitch())
                        RenderUtil.drawBBBox(bb, new Color(boxRed, boxGreen, boxBlue), (int) (boxAlpha * 255.0f));
                    if (outline.GetSwitch())
                        RenderUtil.drawBlockOutlineBB(bb, new Color(outlineRed, outlineGreen, outlineBlue, outlineAlpha), outlineWidth.GetSlider());
                    break;
                case "ShrinkGrow":
                    bb = new AxisAlignedBB(currentPos).shrink(currState);
                    if (box.GetSwitch())
                        RenderUtil.drawBBBox(bb, new Color(boxRed, boxGreen, boxBlue), (int) (boxAlpha * 255.0f));
                    if (outline.GetSwitch())
                        RenderUtil.drawBlockOutlineBB(bb, new Color(outlineRed, outlineGreen, outlineBlue, outlineAlpha), outlineWidth.GetSlider());
                    break;
                case "HeightIncrease":
                    if (box.GetSwitch())
                        RenderUtil.drawBBBoxWithHeight(bb, new Color(boxRed, boxGreen, boxBlue), (int) (boxAlpha * 255.0f), currState);
                    if (outline.GetSwitch())
                        RenderUtil.drawBlockOutlineBBWithHeight(bb, new Color(outlineRed, outlineGreen, outlineBlue, outlineAlpha), outlineWidth.GetSlider(), currState);
                    break;
                case "HeightDecrease":
                    if (box.GetSwitch())
                        RenderUtil.drawBBBoxWithHeight(bb, new Color(boxRed, boxGreen, boxBlue), (int) (boxAlpha * 255.0f), 1 - currState);
                    if (outline.GetSwitch())
                        RenderUtil.drawBlockOutlineBBWithHeight(bb, new Color(outlineRed, outlineGreen, outlineBlue, outlineAlpha), outlineWidth.GetSlider(), 1 - currState);
                    break;
                case "ShrinkGrowHeightIncrease":
                    bb = new AxisAlignedBB(currentPos).shrink(currState);
                    if (box.GetSwitch())
                        RenderUtil.drawBBBoxWithHeight(bb, new Color(boxRed, boxGreen, boxBlue), (int) (boxAlpha * 255.0f), currState);
                    if (outline.GetSwitch())
                        RenderUtil.drawBlockOutlineBBWithHeight(bb, new Color(outlineRed, outlineGreen, outlineBlue, outlineAlpha), outlineWidth.GetSlider(), currState);
                    break;
                case "ShrinkGrowHeightDecrease":
                    bb = new AxisAlignedBB(currentPos).shrink(currState);
                    if (box.GetSwitch())
                        RenderUtil.drawBBBoxWithHeight(bb, new Color(boxRed, boxGreen, boxBlue), (int) (boxAlpha * 255.0f), 1 - currState);
                    if (outline.GetSwitch())
                        RenderUtil.drawBlockOutlineBBWithHeight(bb, new Color(outlineRed, outlineGreen, outlineBlue, outlineAlpha), outlineWidth.GetSlider(), 1 - currState);
                    break;
                case "Complete":
                    if (box.GetSwitch())
                        RenderUtil.drawBBBoxWithHeight(bb, new Color(boxRed, boxGreen, boxBlue), (int) (boxAlpha * 255.0f), currState > 0.25f ? currState - 0.25f : 0);
                    if (outline.GetSwitch())
                        RenderUtil.drawBlockOutlineBBWithHeight(bb, new Color(outlineRed, outlineGreen, outlineBlue, outlineAlpha), outlineWidth.GetSlider(), currState > 0.25f ? currState - 0.25f : 0);

                    break;
            }
        }
    }

    @RegisterListener
    public void onBlockEvent(BlockInteractEvent.ClickBlock event) {
        if (nullCheck() || !isEnabled())
            return;
        if (mc.playerController.curBlockDamageMP > 0.1f)
            mc.playerController.isHittingBlock = true;
        if (posFix.GetSwitch())
            currentPos = event.pos;
        int slot = InventoryUtil.getItemFromHotbar(Items.DIAMOND_PICKAXE);
        if (lmbBreak.GetSwitch() && currState >= 1.0f && !mc.player.getHeldItemMainhand().getItem().equals(Items.DIAMOND_PICKAXE) && slot != -1 && currentPos != null && currentPos.equals(event.pos)) {
            int currentItem = mc.player.inventory.currentItem;
            InventoryUtil.switchToSlot(slot);
            Objects.requireNonNull(mc.getConnection()).sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, event.pos, event.facing));
            mc.player.inventory.currentItem = currentItem;
            mc.playerController.updateController();
        }
    }

    @RegisterListener
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
        if (preSwitch.GetSwitch()) {
            int slot = InventoryUtil.getItemFromHotbar(Items.DIAMOND_PICKAXE);
            InventoryUtil.switchToSlot(slot);
        }
        mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.START_DESTROY_BLOCK, event.pos, event.facing));
        mc.player.swingArm(EnumHand.MAIN_HAND);
        mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, event.pos, event.facing));
        if (preSwitch.GetSwitch()) {
            mc.player.inventory.currentItem = currentItem;
            mc.playerController.updateController();
        }
        event.setCancelled(true);
    }
}