package dev.zprestige.ruby.module.movement;

import dev.zprestige.ruby.Ruby;
import dev.zprestige.ruby.manager.HoleManager;
import dev.zprestige.ruby.module.Module;
import dev.zprestige.ruby.setting.impl.BooleanSetting;
import dev.zprestige.ruby.setting.impl.FloatSetting;
import dev.zprestige.ruby.setting.impl.ModeSetting;
import dev.zprestige.ruby.util.BlockUtil;
import dev.zprestige.ruby.util.Timer;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.function.Predicate;
import java.util.stream.Collectors;

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
        ArrayList<HoleManager.HolePos> holes = Ruby.holeManager.holes.stream().filter(holePos -> mc.player.getDistanceSq(holePos.pos) / 2 < holeRange.getValue()).collect(Collectors.toCollection(ArrayList::new));
        for (HoleManager.HolePos holePos : holes) {
            final BlockPos pos = holePos.pos;
            switch (dragMode.getValue()) {
                case "Smooth":
                    if (mc.player.posX > pos.up().x + 0.5) {
                        mc.player.motionX = -smoothSpeed.getValue() / 10.0f;
                    } else if (mc.player.posX < pos.up().x + 0.5) {
                        mc.player.motionX = smoothSpeed.getValue() / 10.0f;
                    }
                    if (mc.player.posZ > pos.up().z + 0.5) {
                        mc.player.motionZ = -smoothSpeed.getValue() / 10.0f;
                    } else if (mc.player.posZ < pos.up().z + 0.5) {
                        mc.player.motionZ = smoothSpeed.getValue() / 10.0f;
                    }
                    break;
                case "Teleport":
                    mc.player.setPosition(pos.up().x + 0.5, pos.up().y, pos.up().z + 0.5);
                    break;

            }
        }
    }
}
