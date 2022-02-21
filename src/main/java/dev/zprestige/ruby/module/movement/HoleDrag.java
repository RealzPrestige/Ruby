package dev.zprestige.ruby.module.movement;

import dev.zprestige.ruby.Ruby;
import dev.zprestige.ruby.module.Category;
import dev.zprestige.ruby.module.Module;
import dev.zprestige.ruby.module.ModuleInfo;
import dev.zprestige.ruby.setting.impl.BooleanSetting;
import dev.zprestige.ruby.setting.impl.FloatSetting;
import dev.zprestige.ruby.setting.impl.ModeSetting;
import dev.zprestige.ruby.util.BlockUtil;
import dev.zprestige.ruby.util.Timer;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.function.Predicate;

@ModuleInfo(name = "HoleDrag", category = Category.Movement, description = "drags yo ass into holes")
public class HoleDrag extends Module {
    public FloatSetting holeRange = createSetting("Hole Range", 1.0f, 0.1f, 3.0f);
    public ModeSetting dragMode = createSetting("Drag Mode", "Smooth", Arrays.asList("Smooth", "Teleport"));
    public FloatSetting smoothSpeed = createSetting("Smooth Speed", 1.0f, 0.0f, 2.0f, (Predicate<Float>) v -> dragMode.getValue().equals("Smooth"));
    public BooleanSetting onGroundOnly = createSetting("On Ground Only", false);
    public Timer timer = new Timer();

    @Override
    public void onTick() {
        if (BlockUtil.isPlayerSafe(mc.player)) {
            timer.setTime(0);
            return;
        }
        if (!timer.getTime(1000))
            return;
        if (onGroundOnly.getValue() && !mc.player.onGround)
            return;
        ArrayList<BlockPos> bedrockHoles = Ruby.holeManager.getBedrockHoles(holeRange.getValue());
        if (bedrockHoles != null) {
            ArrayList<BlockPos> bedrockHoles2 = new ArrayList<>(bedrockHoles);
            for (BlockPos pos : bedrockHoles2) {
                String s = dragMode.getValue();
                switch (s) {
                    case "Smooth":
                        if (mc.player.posX > pos.up().x + 0.5)
                            mc.player.motionX = -smoothSpeed.getValue() / 10.0f;
                        else if (mc.player.posX < pos.up().x + 0.5)
                            mc.player.motionX = smoothSpeed.getValue() / 10.0f;
                        if (mc.player.posZ > pos.up().z + 0.5)
                            mc.player.motionZ = -smoothSpeed.getValue() / 10.0f;
                        else if (mc.player.posZ < pos.up().z + 0.5)
                            mc.player.motionZ = smoothSpeed.getValue() / 10.0f;
                        break;
                    case "Teleport":
                        mc.player.setPosition(pos.up().x + 0.5, pos.up().y, pos.up().z + 0.5);
                        break;

                }
            }
        }
        ArrayList<BlockPos> obsidianHoles = Ruby.holeManager.getObsidianHoles(holeRange.getValue());
        if (obsidianHoles != null) {
            ArrayList<BlockPos> obsidianHoles2 = new ArrayList<>(obsidianHoles);
            for (BlockPos pos : obsidianHoles2) {
                String s2 = dragMode.getValue();
                switch (s2) {
                    case "Smooth":
                        if (mc.player.posX > pos.up().x + 0.5)
                            mc.player.motionX = -smoothSpeed.getValue() / 10.0f;
                        else if (mc.player.posX < pos.up().x + 0.5)
                            mc.player.motionX = smoothSpeed.getValue() / 10.0f;
                        if (mc.player.posZ > pos.up().z + 0.5)
                            mc.player.motionZ = -smoothSpeed.getValue() / 10.0f;
                        else if (mc.player.posZ < pos.up().z + 0.5)
                            mc.player.motionZ = smoothSpeed.getValue() / 10.0f;
                        break;
                    case "Teleport":
                        mc.player.setPosition(pos.up().x + 0.5, pos.up().y, pos.up().z + 0.5);
                        break;
                }
            }
        }
    }
}
