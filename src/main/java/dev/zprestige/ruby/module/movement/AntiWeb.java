package dev.zprestige.ruby.module.movement;

import dev.zprestige.ruby.module.Module;
import dev.zprestige.ruby.settings.impl.ComboBox;
import dev.zprestige.ruby.settings.impl.Slider;

public class AntiWeb extends Module {
    public final ComboBox mode = Menu.ComboBox("Mode", new String[]{"Vanilla", "Motion", "Timer"});
    public final Slider horizontalSpeed = Menu.Slider("Horizontal Speed", 0.1f, 2.0f);
    public final Slider verticalSpeed = Menu.Slider("Vertical Speed", 0.1f, 2.0f);
    public final Slider timerAmount = Menu.Slider("Timer Amount", 0.1f, 10.0f);
    public boolean isTimering;

    @Override
    public void onTick() {
        if (!mc.player.isInWeb) {
            if (isTimering) {
                mc.timer.tickLength = 50.0f;
                isTimering = false;
            }
            return;
        }
        switch (mode.GetCombo()) {
            case "Vanilla":
                mc.player.isInWeb = false;
                break;
            case "Motion":
                mc.player.motionX *= (1 + horizontalSpeed.GetSlider());
                mc.player.motionY = -verticalSpeed.GetSlider();
                mc.player.motionZ *= (1 + horizontalSpeed.GetSlider());
                break;
            case "Timer":
                mc.timer.tickLength = 50.0f / timerAmount.GetSlider();
                isTimering = true;
                break;
        }
    }
}
