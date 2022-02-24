package dev.zprestige.ruby.module.movement;

import dev.zprestige.ruby.module.Module;
import dev.zprestige.ruby.setting.impl.FloatSetting;
import dev.zprestige.ruby.setting.impl.IntegerSetting;
import dev.zprestige.ruby.setting.impl.ModeSetting;
import dev.zprestige.ruby.util.EntityUtil;

import java.util.Arrays;

public class TickShift extends Module {
    public static TickShift Instance;
    public final Slider timer = Menu.Switch("Timer", 1.0f, 0.1f, 10.0f);
    public final Slider disableTicks = Menu.Switch("Disable Ticks", 12, 1, 100);
    public final ComboBox offGroundAction = Menu.Switch("Off Ground Action", "None", Arrays.asList("None", "Ignore", "Disable"));
    public int ticks = 0;

    public TickShift() {
        Instance = this;
    }

    @Override
    public void onDisable() {
        mc.timer.tickLength = 50.0f;
        ticks = 0;
    }

    @Override
    public void onTick() {
        ticks++;
        if (ticks >= disableTicks.getValue()) {
            disableModule();
            return;
        }
        if (!mc.player.onGround) {
            switch (offGroundAction.getValue()) {
                case "None":
                    break;
                case "Ignore":
                    return;
                case "Disable":
                    disableModule();
                    return;
            }
        }
        if (EntityUtil.isMoving() && !mc.player.isSneaking() && !mc.player.isInLava() && !mc.player.isInWater() && !mc.player.isOnLadder())
            mc.timer.tickLength = 50.0f / timer.getValue();
    }
}