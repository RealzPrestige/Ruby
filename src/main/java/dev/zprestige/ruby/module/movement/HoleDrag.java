package dev.zprestige.ruby.module.movement;

import dev.zprestige.ruby.Ruby;
import dev.zprestige.ruby.manager.HoleManager;
import dev.zprestige.ruby.module.Module;
import dev.zprestige.ruby.settings.impl.ComboBox;
import dev.zprestige.ruby.settings.impl.Slider;
import dev.zprestige.ruby.settings.impl.Switch;
import dev.zprestige.ruby.util.BlockUtil;
import dev.zprestige.ruby.util.Timer;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class HoleDrag extends Module {
    public final Slider holeRange = Menu.Slider("Hole Range", 0.1f, 3.0f);
    public final ComboBox dragMode = Menu.ComboBox("Drag Mode", new String[]{"Smooth", "Teleport"});
    public final Slider smoothSpeed = Menu.Slider("Smooth Speed", 0.0f, 2.0f);
    public final Switch onGroundOnly = Menu.Switch("On Ground Only");
    public Timer timer = new Timer();

    @Override
    public void onTick() {
        if (BlockUtil.isPlayerSafe(mc.player)) {
            timer.setTime(0);
            return;
        }
        if (!timer.getTime(1000))
            return;
        if (onGroundOnly.GetSwitch() && !mc.player.onGround)
            return;
        ArrayList<HoleManager.HolePos> holes = Ruby.holeManager.holes.stream().filter(holePos -> mc.player.getDistanceSq(holePos.pos) / 2 < holeRange.GetSlider()).collect(Collectors.toCollection(ArrayList::new));
        for (HoleManager.HolePos holePos : holes) {
            final BlockPos pos = holePos.pos;
            switch (dragMode.GetCombo()) {
                case "Smooth":
                    if (mc.player.posX > pos.up().x + 0.5) {
                        mc.player.motionX = -smoothSpeed.GetSlider() / 10.0f;
                    } else if (mc.player.posX < pos.up().x + 0.5) {
                        mc.player.motionX = smoothSpeed.GetSlider() / 10.0f;
                    }
                    if (mc.player.posZ > pos.up().z + 0.5) {
                        mc.player.motionZ = -smoothSpeed.GetSlider() / 10.0f;
                    } else if (mc.player.posZ < pos.up().z + 0.5) {
                        mc.player.motionZ = smoothSpeed.GetSlider() / 10.0f;
                    }
                    break;
                case "Teleport":
                    mc.player.setPosition(pos.up().x + 0.5, pos.up().y, pos.up().z + 0.5);
                    break;

            }
        }
    }
}
