package dev.zprestige.ruby.module.movement;

import dev.zprestige.ruby.eventbus.annotation.RegisterListener;
import dev.zprestige.ruby.events.MoveEvent;
import dev.zprestige.ruby.module.Module;
import dev.zprestige.ruby.module.exploit.Timer;
import dev.zprestige.ruby.setting.impl.BooleanSetting;
import dev.zprestige.ruby.setting.impl.FloatSetting;
import dev.zprestige.ruby.setting.impl.ModeSetting;
import dev.zprestige.ruby.util.BlockUtil;
import net.minecraft.init.Blocks;

import java.util.Arrays;
import java.util.function.Predicate;

public class FastFall extends Module {
    public ModeSetting mode = createSetting("Mode", "Motion", Arrays.asList("Motion", "Timer"));
    public FloatSetting timerAmount = createSetting("Timer Amount", 1.0f, 0.1f, 10.0f, (Predicate<Float>) v -> mode.getValue().equals("Timer"));
    public BooleanSetting preventHorizontalMotion = createSetting("Prevent Horizontal Motion", false, v -> mode.getValue().equals("Timer"));
    public FloatSetting height = createSetting("Height", 2.0f, 0.1f, 10.0f);
    public BooleanSetting strict = createSetting("Strict", false, v -> mode.getValue().equals("Motion"));
    public double prevTickY;
    public boolean isTimering, prevOnGround;

    @Override
    public void onDisable() {
        mc.timer.tickLength = 50.0f;
    }

    @Override
    public void onTick() {
        switch (mode.getValue()) {
            case "Timer":
                if (prevTickY < mc.player.posY || mc.gameSettings.keyBindJump.isKeyDown() || mc.player.isInWater() || mc.player.isInLava() || mc.player.isInWeb || mc.player.isElytraFlying()) {
                    prevOnGround = false;
                }
                if (prevTickY > mc.player.posY && prevOnGround) {
                    mc.timer.tickLength = 50.0f / timerAmount.getValue();
                    isTimering = true;
                } else if (isTimering && !TickShift.Instance.isEnabled() && !Timer.Instance.isEnabled()) {
                    mc.timer.tickLength = 50.0f;
                    isTimering = false;
                }
                prevTickY = mc.player.posY;
                if (mc.player.onGround) {
                    prevOnGround = true;
                }
                break;
            case "Motion":
                if (!mc.player.onGround || mc.player.isOnLadder() || mc.player.isInWeb || mc.player.isInLava() || mc.player.isInWater() || mc.world.getBlockState(BlockUtil.getPlayerPos()).getBlock().equals(Blocks.WATER) || mc.player.noClip)
                    return;
                mc.player.motionY = strict.getValue() ? -1 : -5;
                break;
        }
    }

    @RegisterListener
    public void onMove(MoveEvent event) {
        if (nullCheck() || !isEnabled() || !mode.getValue().equals("Timer") || !preventHorizontalMotion.getValue() || !prevOnGround || !isTimering)
            return;
        event.setMotion(0, event.motionY, 0);
    }
}
