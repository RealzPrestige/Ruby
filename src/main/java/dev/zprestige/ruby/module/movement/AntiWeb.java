package dev.zprestige.ruby.module.movement;

import dev.zprestige.ruby.module.Category;
import dev.zprestige.ruby.module.Module;
import dev.zprestige.ruby.module.ModuleInfo;
import dev.zprestige.ruby.setting.impl.FloatSetting;
import dev.zprestige.ruby.setting.impl.ModeSetting;

import java.util.Arrays;
import java.util.function.Predicate;

@ModuleInfo(name = "AntiWeb", category = Category.Movement, description = "Ok, i like team web but non-teamwebbers cant be webbing so we counter.")
public class AntiWeb extends Module {
    public ModeSetting mode = createSetting("Mode", "Vanilla", Arrays.asList("Vanilla", "Motion", "Timer"));
    public FloatSetting horizontalSpeed = createSetting("Horizontal Speed", 1.0f, 0.1f, 2.0f, (Predicate<Float>) v-> mode.getValue().equals("Motion"));
    public FloatSetting verticalSpeed = createSetting("Vertical Speed", 1.0f, 0.1f, 2.0f, (Predicate<Float>) v-> mode.getValue().equals("Motion"));
    public FloatSetting timerAmount = createSetting("Timer Amount", 1.0f, 0.1f, 10.0f, (Predicate<Float>) v-> mode.getValue().equals("Timer"));
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
