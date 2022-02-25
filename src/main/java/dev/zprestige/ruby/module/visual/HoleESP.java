package dev.zprestige.ruby.module.visual;

import dev.zprestige.ruby.Ruby;
import dev.zprestige.ruby.manager.HoleManager;
import dev.zprestige.ruby.module.Module;
import dev.zprestige.ruby.settings.impl.ColorSwitch;
import dev.zprestige.ruby.settings.impl.ComboBox;
import dev.zprestige.ruby.settings.impl.Slider;
import dev.zprestige.ruby.settings.impl.Switch;
import dev.zprestige.ruby.util.RenderUtil;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;

import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class HoleESP extends Module {
    protected final Slider radius = Menu.Slider("Radius", 1.0f, 50.0f);
    protected final Slider height = Menu.Slider("Height", 0.0f, 2.0f);
    protected final Slider lineWidth = Menu.Slider("Line Width", 0.1f, 5.0f);
    protected final Switch doubles = Menu.Switch("Doubles");
    protected final ComboBox animation = Menu.ComboBox("Animation", new String[]{"None", "Grow", "Fade"});
    protected final Slider growSpeed = Menu.Slider("Grow Speed", 0.0f, 1000.0f);
    protected final Slider distanceDivision = Menu.Slider("Distance Division", 0.1f, 50.0f);
    protected final ColorSwitch bedrockBox = Menu.ColorSwitch("Bedrock Box");
    protected final ColorSwitch bedrockOutline = Menu.ColorSwitch("Bedrock Outline");
    protected final ColorSwitch obsidianBox = Menu.ColorSwitch("Obsidian Box");
    protected final ColorSwitch obsidianOutline = Menu.ColorSwitch("Obsidian Outline");
    protected final HashMap<BlockPos, Long> holePosLongHashMap = new HashMap<>();
    protected final ICamera camera = new Frustum();

    @Override
    public void onGlobalRenderTick() {
        camera.setPosition(Objects.requireNonNull(mc.getRenderViewEntity()).posX, mc.getRenderViewEntity().posY, mc.getRenderViewEntity().posZ);
        final List<HoleManager.HolePos> holes = Ruby.holeManager.holes.stream().filter(holePos -> (mc.player.getDistanceSq(holePos.pos) < radius.GetSlider() * radius.GetSlider()) && (doubles.GetSwitch() || (holePos.holeType.equals(HoleManager.Type.Bedrock) || holePos.holeType.equals(HoleManager.Type.Obsidian)))).collect(Collectors.toList());
        new HashMap<>(holePosLongHashMap).entrySet().stream().filter(entry -> holes.stream().noneMatch(holePos -> holePos.pos.equals(entry.getKey()))).forEach(entry -> holePosLongHashMap.remove(entry.getKey()));
        for (HoleManager.HolePos holePos : holes) {
            AxisAlignedBB bb = animation.GetCombo().equals("Grow") ? new AxisAlignedBB(holePos.pos).shrink(0.5) : new AxisAlignedBB(holePos.pos);
            if (animation.GetCombo().equals("Grow")) {
                for (Map.Entry<BlockPos, Long> entry : holePosLongHashMap.entrySet()) {
                    if (entry.getKey().equals(holePos.pos)) {
                        bb = bb.grow(Math.min((System.currentTimeMillis() - entry.getValue()) / (1001f - growSpeed.GetSlider()), 0.5));
                    }
                }
            }
            final int bedrockAlpha = (int) Math.min(bedrockBox.GetColor().getAlpha() / (Math.max(1.0, mc.player.getDistanceSq(holePos.pos) / distanceDivision.GetSlider())), bedrockBox.GetColor().getAlpha());
            final int obsidianAlpha = (int) Math.min(obsidianBox.GetColor().getAlpha() / (Math.max(1.0, mc.player.getDistanceSq(holePos.pos) / distanceDivision.GetSlider())), obsidianBox.GetColor().getAlpha());
            final int bedrockOutlineAlpha = (int) Math.min(bedrockOutline.GetColor().getAlpha() / (Math.max(1.0, mc.player.getDistanceSq(holePos.pos) / distanceDivision.GetSlider())), bedrockOutline.GetColor().getAlpha());
            final int obsidianOutlineAlpha = (int) Math.min(obsidianOutline.GetColor().getAlpha() / (Math.max(1.0, mc.player.getDistanceSq(holePos.pos) / distanceDivision.GetSlider())), obsidianOutline.GetColor().getAlpha());
            final Color bedrockBoxColor = animation.GetCombo().equals("Fade") ? new Color(bedrockBox.GetColor().getRed() / 255.0f, bedrockBox.GetColor().getGreen() / 255.0f, bedrockBox.GetColor().getBlue() / 255.0f, bedrockAlpha / 255.0f) : bedrockBox.GetColor();
            final Color obsidianBoxColor = animation.GetCombo().equals("Fade") ? new Color(obsidianBox.GetColor().getRed() / 255.0f, obsidianBox.GetColor().getGreen() / 255.0f, obsidianBox.GetColor().getBlue() / 255.0f, obsidianAlpha / 255.0f) : obsidianBox.GetColor();
            final Color bedrockOutlineColor = animation.GetCombo().equals("Fade") ? new Color(bedrockOutline.GetColor().getRed() / 255.0f, bedrockOutline.GetColor().getGreen() / 255.0f, bedrockOutline.GetColor().getBlue() / 255.0f, bedrockOutlineAlpha / 255.0f) : bedrockOutline.GetColor();
            final Color obsidianOutlineColor = animation.GetCombo().equals("Fade") ? new Color(obsidianOutline.GetColor().getRed() / 255.0f, obsidianOutline.GetColor().getGreen() / 255.0f, obsidianOutline.GetColor().getBlue() / 255.0f, obsidianOutlineAlpha / 255.0f) : obsidianOutline.GetColor();
            if (camera.isBoundingBoxInFrustum(bb.grow(2.0))) {
                switch (holePos.holeType) {
                    case Bedrock:
                        RenderUtil.drawBoxWithHeight(bb, bedrockBoxColor, height.GetSlider());
                        RenderUtil.drawBlockOutlineBBWithHeight(bb, bedrockOutlineColor, lineWidth.GetSlider(), height.GetSlider());
                        break;
                    case Obsidian:
                        RenderUtil.drawBoxWithHeight(bb, obsidianBoxColor, height.GetSlider());
                        RenderUtil.drawBlockOutlineBBWithHeight(bb, obsidianOutlineColor, lineWidth.GetSlider(), height.GetSlider());
                        break;
                    case DoubleBedrockNorth:
                        RenderUtil.drawCustomBB(bedrockBoxColor, bb.minX, bb.minY, bb.minZ - 1, bb.maxX, bb.maxY - 1 + height.GetSlider(), bb.maxZ);
                        RenderUtil.drawBlockOutlineBBWithHeight(new AxisAlignedBB(bb.minX, bb.minY, bb.minZ - 1, bb.maxX, bb.maxY, bb.maxZ), bedrockOutlineColor, lineWidth.GetSlider(), height.GetSlider());
                        break;
                    case DoubleObsidianNorth:
                        RenderUtil.drawCustomBB(obsidianBoxColor, bb.minX, bb.minY, bb.minZ - 1, bb.maxX, bb.maxY - 1 + height.GetSlider(), bb.maxZ);
                        RenderUtil.drawBlockOutlineBBWithHeight(new AxisAlignedBB(bb.minX, bb.minY, bb.minZ - 1, bb.maxX, bb.maxY, bb.maxZ), obsidianOutlineColor, lineWidth.GetSlider(), height.GetSlider());
                        break;
                    case DoubleBedrockWest:
                        RenderUtil.drawCustomBB(bedrockBoxColor, bb.minX - 1, bb.minY, bb.minZ, bb.maxX, bb.maxY - 1 + height.GetSlider(), bb.maxZ);
                        RenderUtil.drawBlockOutlineBBWithHeight(new AxisAlignedBB(bb.minX - 1, bb.minY, bb.minZ, bb.maxX, bb.maxY, bb.maxZ), bedrockOutlineColor, lineWidth.GetSlider(), height.GetSlider());
                        break;
                    case DoubleObsidianWest:
                        RenderUtil.drawCustomBB(obsidianBoxColor, bb.minX - 1, bb.minY, bb.minZ, bb.maxX, bb.maxY - 1 + height.GetSlider(), bb.maxZ);
                        RenderUtil.drawBlockOutlineBBWithHeight(new AxisAlignedBB(bb.minX - 1, bb.minY, bb.minZ, bb.maxX, bb.maxY, bb.maxZ), obsidianOutlineColor, lineWidth.GetSlider(), height.GetSlider());
                        break;
                }
            }
            if (!holePosLongHashMap.containsKey(holePos.pos)) {
                holePosLongHashMap.put(holePos.pos, System.currentTimeMillis());
            }
        }
    }
}
