package dev.zprestige.ruby.module.visual;

import dev.zprestige.ruby.Ruby;
import dev.zprestige.ruby.manager.HoleManager;
import dev.zprestige.ruby.module.Module;
import dev.zprestige.ruby.setting.impl.BooleanSetting;
import dev.zprestige.ruby.setting.impl.ColorSetting;
import dev.zprestige.ruby.setting.impl.FloatSetting;
import dev.zprestige.ruby.setting.impl.ModeSetting;
import dev.zprestige.ruby.util.RenderUtil;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;

import java.awt.*;
import java.util.List;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class HoleESP extends Module {
    protected final FloatSetting radius = Menu.Switch("Radius", 50.0f, 1.0f, 50.0f);
    protected final FloatSetting height = Menu.Switch("Height", 1.0f, 0.0f, 2.0f);
    protected final FloatSetting lineWidth = Menu.Switch("Line Width", 1.0f, 0.1f, 5.0f);
    protected final BooleanSetting doubles = Menu.Switch("Doubles");
    protected final ModeSetting animation = Menu.Switch("Animation", "None", Arrays.asList("None", "Grow", "Fade"));
    protected final FloatSetting growSpeed = Menu.Switch("Grow Speed", 10.0f, 0.0f, 1000.0f, (Predicate<Float>) v -> animation.getValue().equals("Grow"));
    protected final FloatSetting distanceDivision = Menu.Switch("Distance Division", 20.0f, 0.1f, 50.0f, (Predicate<Float>) v -> animation.getValue().equals("Fade"));
    protected final ColorSetting bedrockBox = Menu.Switch("Bedrock Box", Color.GREEN);
    protected final ColorSetting bedrockOutline = Menu.Switch("Bedrock Outline", Color.GREEN);
    protected final ColorSetting obsidianBox = Menu.Switch("Obsidian Box", Color.RED);
    protected final ColorSetting obsidianOutline = Menu.Switch("Obsidian Outline", Color.RED);
    protected final HashMap<BlockPos, Long> holePosLongHashMap = new HashMap<>();
    protected final ICamera camera = new Frustum();

    @Override
    public void onGlobalRenderTick() {
        camera.setPosition(Objects.requireNonNull(mc.getRenderViewEntity()).posX, mc.getRenderViewEntity().posY, mc.getRenderViewEntity().posZ);
        final List<HoleManager.HolePos> holes = Ruby.holeManager.holes.stream().filter(holePos -> (mc.player.getDistanceSq(holePos.pos) < radius.getValue() * radius.getValue()) && (doubles.getValue() || (holePos.holeType.equals(HoleManager.Type.Bedrock) || holePos.holeType.equals(HoleManager.Type.Obsidian)))).collect(Collectors.toList());
        new HashMap<>(holePosLongHashMap).entrySet().stream().filter(entry -> holes.stream().noneMatch(holePos -> holePos.pos.equals(entry.getKey()))).forEach(entry -> holePosLongHashMap.remove(entry.getKey()));
        for (HoleManager.HolePos holePos : holes) {
            AxisAlignedBB bb = animation.getValue().equals("Grow") ? new AxisAlignedBB(holePos.pos).shrink(0.5) : new AxisAlignedBB(holePos.pos);
            if (animation.getValue().equals("Grow")) {
                for (Map.Entry<BlockPos, Long> entry : holePosLongHashMap.entrySet()) {
                    if (entry.getKey().equals(holePos.pos)) {
                        bb = bb.grow(Math.min((System.currentTimeMillis() - entry.getValue()) / (1001f - growSpeed.getValue()), 0.5));
                    }
                }
            }
            final int bedrockAlpha = (int) Math.min(bedrockBox.getValue().getAlpha() / (Math.max(1.0, mc.player.getDistanceSq(holePos.pos) / distanceDivision.getValue())), bedrockBox.getValue().getAlpha());
            final int obsidianAlpha = (int) Math.min(obsidianBox.getValue().getAlpha() / (Math.max(1.0, mc.player.getDistanceSq(holePos.pos) / distanceDivision.getValue())), obsidianBox.getValue().getAlpha());
            final int bedrockOutlineAlpha = (int) Math.min(bedrockOutline.getValue().getAlpha() / (Math.max(1.0, mc.player.getDistanceSq(holePos.pos) / distanceDivision.getValue())), bedrockOutline.getValue().getAlpha());
            final int obsidianOutlineAlpha = (int) Math.min(obsidianOutline.getValue().getAlpha() / (Math.max(1.0, mc.player.getDistanceSq(holePos.pos) / distanceDivision.getValue())), obsidianOutline.getValue().getAlpha());
            final Color bedrockBoxColor = animation.getValue().equals("Fade") ? new Color(bedrockBox.getValue().getRed() / 255.0f, bedrockBox.getValue().getGreen() / 255.0f, bedrockBox.getValue().getBlue() / 255.0f, bedrockAlpha / 255.0f) : bedrockBox.getValue();
            final Color obsidianBoxColor = animation.getValue().equals("Fade") ? new Color(obsidianBox.getValue().getRed() / 255.0f, obsidianBox.getValue().getGreen() / 255.0f, obsidianBox.getValue().getBlue() / 255.0f, obsidianAlpha / 255.0f) : obsidianBox.getValue();
            final Color bedrockOutlineColor = animation.getValue().equals("Fade") ? new Color(bedrockOutline.getValue().getRed() / 255.0f, bedrockOutline.getValue().getGreen() / 255.0f, bedrockOutline.getValue().getBlue() / 255.0f, bedrockOutlineAlpha / 255.0f) : bedrockOutline.getValue();
            final Color obsidianOutlineColor = animation.getValue().equals("Fade") ? new Color(obsidianOutline.getValue().getRed() / 255.0f, obsidianOutline.getValue().getGreen() / 255.0f, obsidianOutline.getValue().getBlue() / 255.0f, obsidianOutlineAlpha / 255.0f) : obsidianOutline.getValue();
            if (camera.isBoundingBoxInFrustum(bb.grow(2.0))) {
                switch (holePos.holeType) {
                    case Bedrock:
                        RenderUtil.drawBoxWithHeight(bb, bedrockBoxColor, height.getValue());
                        RenderUtil.drawBlockOutlineBBWithHeight(bb, bedrockOutlineColor, lineWidth.getValue(), height.getValue());
                        break;
                    case Obsidian:
                        RenderUtil.drawBoxWithHeight(bb, obsidianBoxColor, height.getValue());
                        RenderUtil.drawBlockOutlineBBWithHeight(bb, obsidianOutlineColor, lineWidth.getValue(), height.getValue());
                        break;
                    case DoubleBedrockNorth:
                        RenderUtil.drawCustomBB(bedrockBoxColor, bb.minX, bb.minY, bb.minZ - 1, bb.maxX, bb.maxY - 1 + height.getValue(), bb.maxZ);
                        RenderUtil.drawBlockOutlineBBWithHeight(new AxisAlignedBB(bb.minX, bb.minY, bb.minZ - 1, bb.maxX, bb.maxY, bb.maxZ), bedrockOutlineColor, lineWidth.getValue(), height.getValue());
                        break;
                    case DoubleObsidianNorth:
                        RenderUtil.drawCustomBB(obsidianBoxColor, bb.minX, bb.minY, bb.minZ - 1, bb.maxX, bb.maxY - 1 + height.getValue(), bb.maxZ);
                        RenderUtil.drawBlockOutlineBBWithHeight(new AxisAlignedBB(bb.minX, bb.minY, bb.minZ - 1, bb.maxX, bb.maxY, bb.maxZ), obsidianOutlineColor, lineWidth.getValue(), height.getValue());
                        break;
                    case DoubleBedrockWest:
                        RenderUtil.drawCustomBB(bedrockBoxColor, bb.minX - 1, bb.minY, bb.minZ, bb.maxX, bb.maxY - 1 + height.getValue(), bb.maxZ);
                        RenderUtil.drawBlockOutlineBBWithHeight(new AxisAlignedBB(bb.minX - 1, bb.minY, bb.minZ, bb.maxX, bb.maxY, bb.maxZ), bedrockOutlineColor, lineWidth.getValue(), height.getValue());
                        break;
                    case DoubleObsidianWest:
                        RenderUtil.drawCustomBB(obsidianBoxColor, bb.minX - 1, bb.minY, bb.minZ, bb.maxX, bb.maxY - 1 + height.getValue(), bb.maxZ);
                        RenderUtil.drawBlockOutlineBBWithHeight(new AxisAlignedBB(bb.minX - 1, bb.minY, bb.minZ, bb.maxX, bb.maxY, bb.maxZ), obsidianOutlineColor, lineWidth.getValue(), height.getValue());
                        break;
                }
            }
            if (!holePosLongHashMap.containsKey(holePos.pos)) {
                holePosLongHashMap.put(holePos.pos, System.currentTimeMillis());
            }
        }
    }
}
