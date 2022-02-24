package dev.zprestige.ruby.module.movement;

import dev.zprestige.ruby.module.Module;
import dev.zprestige.ruby.setting.impl.FloatSetting;
import dev.zprestige.ruby.setting.impl.ModeSetting;

import java.util.Arrays;
import java.util.function.Predicate;

public class AntiWeb extends Module {
    public final ComboBox mode = Menu.Switch("Mode", "Vanilla", Arrays.asList("Vanilla", "Motion", "Timer"));
    public final Slider horizontalSpeed = Menu.Switch("Horizontal Speed", 1.0f, 0.1f, 2.0f, (Predicate<Float>) v-> mode.getValue().equals("Motion"));
    public final Slider verticalSpeed = Menu.Switch("Vertical Speed", 1.0f, 0.1f, 2.0f, (Predicate<Float>) v-> mode.getValue().equals("Motion"));
    public final Slider timerAmount = Menu.Switch("Timer Amount", 1.0f, 0.1f, 10.0f, (Predicate<Float>) v-> mode.getValue().equals("Timer"));
    public boolean isTimering;

    @Override
    public void onTick(){
        if (!mc.player.isInWeb) {
            if (isTimering) {
                mc.timer.tickLength = 50.0f;
                isTimering = false;
            }
            return;
        }
        switch (mode.getValue()){
            case "Vanilla":
                mc.player.isInWeb = false;
                break;
            case "Motion":
                mc.player.motionX *= (1 + horizontalSpeed.getValue());
                mc.player.motionY = -verticalSpeed.getValue();
                mc.player.motionZ *= (1 + horizontalSpeed.getValue());
                break;
            case "Timer":
                mc.timer.tickLength = 50.0f / timerAmount.getValue();
                isTimering = true;
                break;
        }
    }
}
