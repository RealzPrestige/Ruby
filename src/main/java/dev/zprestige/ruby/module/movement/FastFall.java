package dev.zprestige.ruby.module.movement;

import dev.zprestige.ruby.eventbus.annotation.RegisterListener;
import dev.zprestige.ruby.events.MoveEvent;
import dev.zprestige.ruby.module.Module;
import dev.zprestige.ruby.module.exploit.Timer;
import dev.zprestige.ruby.settings.impl.ComboBox;
import dev.zprestige.ruby.settings.impl.Slider;
import dev.zprestige.ruby.settings.impl.Switch;
import dev.zprestige.ruby.util.BlockUtil;
import net.minecraft.init.Blocks;

public class FastFall extends Module {
    public final ComboBox mode = Menu.ComboBox("Mode", new String[]{"Motion", "Timer"});
    public final Slider timerAmount = Menu.Slider("Timer Amount", 0.1f, 10.0f);
    public final Switch preventHorizontalMotion = Menu.Switch("Prevent Horizontal Motion");
    public final Slider height = Menu.Slider("Height", 0.1f, 10.0f);
    public final Switch strict = Menu.Switch("Strict");
    public double prevTickY;
    public boolean isTimering, prevOnGround;

    @Override
    public void onDisable() {
        mc.timer.tickLength = 50.0f;
    }

    @Override
    public void onTick() {
        switch (mode.GetCombo()) {
            case "Timer":
                if (prevTickY < mc.player.posY || mc.gameSettings.keyBindJump.isKeyDown() || mc.player.isInWater() || mc.player.isInLava() || mc.player.isInWeb || mc.player.isElytraFlying()) {
                    prevOnGround = false;
                }
                if (prevTickY > mc.player.posY && prevOnGround) {
                    mc.timer.tickLength = 50.0f / timerAmount.GetSlider();
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
                mc.player.motionY = strict.GetSwitch() ? -1 : -5;
                break;
        }
    }

    @RegisterListener
    public void onMove(MoveEvent event) {
        if (nullCheck() || !isEnabled() || !mode.GetCombo().equals("Timer") || !preventHorizontalMotion.GetSwitch() || !prevOnGround || !isTimering)
            return;
        event.setMotion(0, event.motionY, 0);
    }
}
