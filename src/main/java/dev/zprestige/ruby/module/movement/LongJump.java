package dev.zprestige.ruby.module.movement;

import dev.zprestige.ruby.Ruby;
import dev.zprestige.ruby.eventbus.annotation.RegisterListener;
import dev.zprestige.ruby.events.MotionUpdateEvent;
import dev.zprestige.ruby.events.MoveEvent;
import dev.zprestige.ruby.events.PacketEvent;
import dev.zprestige.ruby.module.Module;
import dev.zprestige.ruby.setting.impl.BooleanSetting;
import dev.zprestige.ruby.setting.impl.DoubleSetting;
import dev.zprestige.ruby.setting.impl.FloatSetting;
import dev.zprestige.ruby.util.EntityUtil;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.network.play.server.SPacketPlayerPosLook;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class LongJump extends Module {
    public final Slider speed = Menu.Switch("Speed", 2000.0, 0.1, 3000.0);
    public final Switch disableOnLag = Menu.Switch("Disable On Lag");
    public final Switch damageCheck = Menu.Switch("Damage Check");
    public final Slider minDamage = Menu.Switch("Min Damage", 5.0f, 0.1f, 36.0f);
    public final Switch renderInfo = Menu.Switch("Render Info", false);
    public double prevDistance;
    public double moveSpeed;
    public int stage;
    public int ticks;
    public float prevTickDamage;
    public float damage;

    @Override
    public void onEnable() {
        stage = 0;
        ticks = 2;
        prevTickDamage = mc.player.getHealth() + mc.player.getAbsorptionAmount();
        damage = 0.0f;
    }

    @Override
    public void onTick() {
        if (mc.player.getHealth() + mc.player.getAbsorptionAmount() < prevTickDamage)
            damage += prevTickDamage - (mc.player.getHealth() + mc.player.getAbsorptionAmount());
        prevTickDamage = mc.player.getHealth() + mc.player.getAbsorptionAmount();
    }

    @RegisterListener
    public void onPacketReceive(PacketEvent.PacketReceiveEvent event) {
        if (nullCheck() || !isEnabled() || !(event.getPacket() instanceof SPacketPlayerPosLook))
            return;
        if (disableOnLag.getValue())
            disableModule("Rubberband detected, disabling LongJump.");
    }

    @RegisterListener
    public void onMotionUpdate(MotionUpdateEvent event) {
        if (nullCheck() || !isEnabled())
            return;
        double xDist = mc.player.posX - mc.player.prevPosX;
        double zDist = mc.player.posZ - mc.player.prevPosZ;
        prevDistance = Math.sqrt(xDist * xDist + zDist * zDist);
    }

    @RegisterListener
    public void onMove(MoveEvent event) {
        if (nullCheck() || !isEnabled() || event.isCancelled())
            return;
        if (damageCheck.getValue() && damage < minDamage.getValue())
            return;
        if (mc.player.collidedHorizontally || (mc.player.moveForward == 0.0f && mc.player.moveStrafing == 0.0f)) {
            stage = 0;
            ticks = 2;
            event.setCancelled(true);
            event.motionX = 0.0;
            event.motionZ = 0.0;
        } else {
            if (ticks > 0) {
                moveSpeed = 0.09;
                --ticks;
            } else if (stage == 1 && mc.player.collidedVertically)
                moveSpeed = 1.0 + EntityUtil.getDefaultSpeed() - 0.05;
            else if (stage == 2 && mc.player.collidedVertically) {
                event.motionY = mc.player.motionY = 0;
                moveSpeed *= speed.getValue() / 100f;
            } else if (stage == 3) {
                moveSpeed = prevDistance - 0.66 * (prevDistance - EntityUtil.getDefaultSpeed());
            } else
                moveSpeed = prevDistance - prevDistance / 159.0;
            event.setCancelled(true);
            EntityUtil.setMoveSpeed(event, moveSpeed);
            if (!mc.player.collidedVertically && (mc.world.getCollisionBoxes(mc.player, mc.player.getEntityBoundingBox().offset(0.0, mc.player.motionY, 0.0)).size() > 0 || mc.world.getCollisionBoxes(mc.player, mc.player.getEntityBoundingBox().offset(0.0, -0.4, 0.0)).size() > 0) && stage > 10) {
                if (stage >= 38) {
                    event.motionY = mc.player.motionY = 0;
                    stage = 0;
                    ticks = 5;
                } else
                    event.motionY = mc.player.motionY = 0;
            }
            if (ticks <= 0 && (mc.player.moveForward != 0.0f || mc.player.moveStrafing != 0.0f))
                ++stage;
        }
    }

    @Override
    public void onOverlayTick() {
        if (!renderInfo.getValue())
            return;
        int screenWidth = new ScaledResolution(mc).getScaledWidth();
        int screenHeight = new ScaledResolution(mc).getScaledHeight();
        String string = "LongJump: [" + "Stage: " + stage + " | PrevDistance: " + roundNumber(prevDistance, 1) + " | Ticks: " + ticks + " | MoveSpeed: " + roundNumber(moveSpeed, 1) + " | DamageCheck: " + roundNumber(damage, 1) + " - " + (damage > minDamage.getValue()) + "]";
        Ruby.rubyFont.drawStringWithShadow(string, (screenWidth / 2f) - (Ruby.rubyFont.getStringWidth(string) / 2f), screenHeight - 100, -1);
    }

    public static float roundNumber(double value, int places) {
        if (places < 0)
            throw new IllegalArgumentException();
        BigDecimal decimal = BigDecimal.valueOf(value);
        decimal = decimal.setScale(places, RoundingMode.FLOOR);
        return decimal.floatValue();
    }
}