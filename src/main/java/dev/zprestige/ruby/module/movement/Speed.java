package dev.zprestige.ruby.module.movement;

import com.mojang.realmsclient.gui.ChatFormatting;
import dev.zprestige.ruby.Ruby;
import dev.zprestige.ruby.eventbus.annotation.RegisterListener;
import dev.zprestige.ruby.events.MoveEvent;
import dev.zprestige.ruby.events.PacketEvent;
import dev.zprestige.ruby.module.Module;
import dev.zprestige.ruby.module.visual.ESP;
import dev.zprestige.ruby.settings.impl.*;
import dev.zprestige.ruby.util.EntityUtil;
import dev.zprestige.ruby.util.Timer;
import net.minecraft.init.MobEffects;
import net.minecraft.network.play.server.SPacketPlayerPosLook;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.MovementInput;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import org.lwjgl.input.Keyboard;

import java.util.Objects;

public class Speed extends Module {
    public static Speed Instance;
    public final Parent modes = Menu.Parent("Modes");
    public final ComboBox mode = Menu.ComboBox("Mode", new String[]{"Normal", "Switch"}).parent(modes);
    public final Key switchKey = Menu.Key("Switch Key", Keyboard.KEY_NONE).parent(modes);
    public final Switch announceSwitch = Menu.Switch("Announce Switch").parent(modes);
    public final Switch switchPullToGround = Menu.Switch("Switch Pull To Ground").parent(modes);
    public final ComboBox speedMode = Menu.ComboBox("Speed Mode", new String[]{"OnGround", "Strafe"}).parent(modes);

    public final Parent misc = Menu.Parent("Misc");
    public final Switch returnOnShift = Menu.Switch("Return On Shift").parent(misc);
    public final Switch slowdownOnGroundNearHoles = Menu.Switch("Slow Down On Ground Near Holes").parent(misc);
    public final Slider slowDownValue = Menu.Slider("Slow Down Value", 0.1f, 5.0f).parent(misc);
    public final Switch liquids = Menu.Switch("Liquids").parent(misc);
    public final Switch useTimer = Menu.Switch("Use Timer").parent(misc);
    public final Slider timerAmount = Menu.Slider("Timer Amount", 0.9f, 2.0f).parent(misc);
    public final Parent factoring = Menu.Parent("Factoring");
    public final ComboBox strafeFactorMode = Menu.ComboBox("Strafe Factor Mode", new String[]{"Manual", "Auto"}).parent(factoring);
    public final ComboBox reFactorizeMode = Menu.ComboBox("Re-Factorize Mode", new String[]{"Accelerating", "Instant"}).parent(factoring);
    public final Slider reFactorizeStartDelay = Menu.Slider("Re-Factorize Start Delay", 10, 500).parent(factoring);
    public final Slider reFactorizeStart = Menu.Slider("Re-Factorize Start Value", 0.1f, 1.0f).parent(factoring);
    public final Slider reFactorizeTarget = Menu.Slider("Re-Factorize Target", 0.1f, 3.0f).parent(factoring);
    public final Slider accelerationFactor = Menu.Slider("Acceleration Factor", 0.1f, 5.0f).parent(factoring);
    public final Slider strafeFactor = Menu.Slider("Strafe Factor", 0.1f, 3.0f).parent(factoring);
    public float f = strafeFactor.GetSlider();
    public final Slider strafeFactorSpeed = Menu.Slider("Strafe Factor Speed Amplifier", 0.1f, 3.0f).parent(factoring);
    public double previousDistance;
    public double motionSpeed;
    public int currentState = 1;
    public Timer factorTimer = new Timer();
    public Timer postSwitchTimer = new Timer();
    public boolean isCloseToHole, isTimering;

    public Speed() {
        Instance = this;
    }

    @Override
    public void onEnable(){
        MinecraftForge.EVENT_BUS.register(this);
    }


    @Override
    public void onDisable() {
        mc.timer.tickLength = 50.0f;
        MinecraftForge.EVENT_BUS.unregister(this);
    }

    @SubscribeEvent
    public void onKeyInput(InputEvent.KeyInputEvent event) {
        if (Ruby.mc.player == null || Ruby.mc.world == null || !mode.GetCombo().equals("Switch") || mc.currentScreen != null || !isEnabled())
            return;
        if (Keyboard.getEventKeyState() && switchKey.GetKey() != -1 && switchKey.GetKey() == Keyboard.getEventKey()) {
            switch (speedMode.GetCombo()) {
                case "OnGround":
                    speedMode.setValue("Strafe");
                    break;
                case "Strafe":
                    speedMode.setValue("OnGround");
                    if (switchPullToGround.GetSwitch())
                        postSwitchTimer.setTime(0);
                    break;
            }
            if (announceSwitch.GetSwitch())
                Ruby.chatManager.sendRemovableMessage(ChatFormatting.BOLD + "Speed " + ChatFormatting.RESET + "switched mode to " + ChatFormatting.RED + speedMode.GetCombo() + ChatFormatting.RESET + ".", 1);
        }
    }

    @Override
    public void onTick() {
        previousDistance = Math.sqrt((mc.player.posX - mc.player.prevPosX) * (mc.player.posX - mc.player.prevPosX) + (mc.player.posZ - mc.player.prevPosZ) * (mc.player.posZ - mc.player.prevPosZ));
        if (!strafeFactorMode.GetCombo().equals("Manual") && f < reFactorizeTarget.getMin()) {
            switch (reFactorizeMode.GetCombo()) {
                case "Instant":
                    if (factorTimer.getTime((long) reFactorizeStartDelay.GetSlider()))
                        f = reFactorizeTarget.GetSlider();
                    break;
                case "Accelerating":
                    if (factorTimer.getTime((long) reFactorizeStartDelay.GetSlider()))
                        f += accelerationFactor.GetSlider() / 10f;
                    break;

            }
        }
        if (switchPullToGround.GetSwitch() && postSwitchTimer.getTimeSub(20))
            mc.player.motionY = -1;
        if (slowdownOnGroundNearHoles.GetSwitch()) {
            isCloseToHole = false;
            if (ESP.Instance.bedrockHoles != null)
                for (BlockPos pos : ESP.Instance.bedrockHoles) {
                    if (mc.player.getDistanceSq(pos) < 1.5f)
                        isCloseToHole = true;
                }
            if (ESP.Instance.obsidianHoles != null)
                for (BlockPos pos : ESP.Instance.obsidianHoles) {
                    if (mc.player.getDistanceSq(pos) < 1.5f)
                        isCloseToHole = true;
                }
        }
    }

    @RegisterListener
    public void onMove(MoveEvent event) {
        if (nullCheck() || !isEnabled() || (!liquids.GetSwitch() && (mc.player.isInWater() || mc.player.isInLava() || mc.player.isSpectator())) || (switchPullToGround.GetSwitch() && postSwitchTimer.getTimeSub(20)) || mc.player.isElytraFlying())
            return;
        if (returnOnShift.GetSwitch() && mc.gameSettings.keyBindSprint.isKeyDown())
            return;
        if (!mc.player.isSprinting())
            mc.player.setSprinting(true);
        if (!dev.zprestige.ruby.module.exploit.Timer.Instance.isEnabled() && !TickShift.Instance.isEnabled() && timerAmount.GetSlider() != 1.0f) {
            if (useTimer.GetSwitch() && (Ruby.mc.player.moveForward != 0.0 || Ruby.mc.player.moveStrafing != 0.0)) {
                mc.timer.tickLength = 50.0f / timerAmount.GetSlider();
                isTimering = true;
            } else if (isTimering) {
                mc.timer.tickLength = 50.0f;
                isTimering = false;
            }
        }
        PotionEffect speed = mc.player.getActivePotionEffect(MobEffects.SPEED);
        switch (speedMode.GetCombo()) {
            case "Strafe":
                switch (currentState) {
                    case 0:
                        ++currentState;
                        previousDistance = 0.0;
                        break;
                    case 1:
                    default:
                        if ((mc.world.getCollisionBoxes(mc.player, mc.player.getEntityBoundingBox().offset(0.0, mc.player.motionY, 0.0)).size() > 0 || mc.player.collidedVertically) && currentState > 0)
                            currentState = mc.player.moveForward == 0.0F && mc.player.moveStrafing == 0.0F ? 0 : 1;
                        motionSpeed = previousDistance - previousDistance / 159.0;
                        break;
                    case 2:
                        double var2 = 0.40123128;
                        if ((mc.player.moveForward != 0.0f || mc.player.moveStrafing != 0.0f) && mc.player.onGround) {
                            if (mc.player.isPotionActive(MobEffects.JUMP_BOOST))
                                var2 += (float) (Objects.requireNonNull(mc.player.getActivePotionEffect(MobEffects.JUMP_BOOST)).getAmplifier() + 1) * 0.1f;
                            event.motionY = (mc.player.motionY = var2);
                            motionSpeed *= 2.149;
                        }
                        break;
                    case 3:
                        motionSpeed = previousDistance - 0.76 * (previousDistance - EntityUtil.getBaseMotionSpeed() * (strafeFactorMode.GetCombo().equals("Auto") ? f : speed != null ? strafeFactorSpeed.GetSlider() : strafeFactor.GetSlider()));
                }
                motionSpeed = Math.max(motionSpeed, EntityUtil.getBaseMotionSpeed() * (strafeFactorMode.GetCombo().equals("Auto") ? f : speed != null ? strafeFactorSpeed.GetSlider() : strafeFactor.GetSlider()));
                double var4 = mc.player.movementInput.moveForward;
                double var6 = mc.player.movementInput.moveStrafe;
                double var8 = mc.player.rotationYaw;
                if (var4 != 0.0 && var6 != 0.0) {
                    var4 *= Math.sin(0.7853981633974483);
                    var6 *= Math.cos(0.7853981633974483);
                }
                event.motionX = ((var4 * motionSpeed * -Math.sin(Math.toRadians(var8)) + var6 * motionSpeed * Math.cos(Math.toRadians(var8))) * 0.99);
                event.motionZ = ((var4 * motionSpeed * Math.cos(Math.toRadians(var8)) - var6 * motionSpeed * -Math.sin(Math.toRadians(var8))) * 0.99);
                ++currentState;
                break;
            case "OnGround":
                if (!(mc.player.isSneaking() || mc.player.movementInput.moveForward == 0.0f && mc.player.movementInput.moveStrafe == 0.0f) || !mc.player.onGround) {
                    MovementInput movementInput = mc.player.movementInput;
                    float moveForward = movementInput.moveForward;
                    float moveStrafe = movementInput.moveStrafe;
                    float rotationYaw = mc.player.rotationYaw;
                    if ((double) moveForward == 0.0 && (double) moveStrafe == 0.0) {
                        event.motionX = (0.0);
                        event.motionZ = (0.0);
                    } else {
                        if ((double) moveForward != 0.0) {
                            if ((double) moveStrafe > 0.0) {
                                rotationYaw += (float) ((double) moveForward > 0.0 ? -45 : 45);
                            } else if ((double) moveStrafe < 0.0) {
                                rotationYaw += (float) ((double) moveForward > 0.0 ? 45 : -45);
                            }
                            moveStrafe = 0.0f;
                        }
                        moveStrafe = moveStrafe == 0.0f ? moveStrafe : ((double) moveStrafe > 0.0 ? 1.0f : -1.0f);
                        final double cos = Math.cos(Math.toRadians(rotationYaw + 90.0f));
                        final double sin = Math.sin(Math.toRadians(rotationYaw + 90.0f));
                        event.motionX = ((double) moveForward * EntityUtil.getMaxSpeed() * cos + (double) moveStrafe * EntityUtil.getMaxSpeed() * sin) / (slowdownOnGroundNearHoles.GetSwitch() && isCloseToHole ? slowDownValue.GetSlider() : 1.0);
                        event.motionZ = ((double) moveForward * EntityUtil.getMaxSpeed() * sin - (double) moveStrafe * EntityUtil.getMaxSpeed() * cos) / (slowdownOnGroundNearHoles.GetSwitch() && isCloseToHole ? slowDownValue.GetSlider() : 1.0);
                    }
                }
                break;
        }
    }

    @RegisterListener
    public void onPacketReceive(PacketEvent.PacketReceiveEvent event) {
        if (nullCheck() || !isEnabled() || strafeFactorMode.GetCombo().equals("Manual") || !(event.getPacket() instanceof SPacketPlayerPosLook))
            return;
        f = reFactorizeStart.GetSlider();
        factorTimer.setTime(0);
    }
}
