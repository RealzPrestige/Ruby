package dev.zprestige.ruby.module.movement;

import com.mojang.realmsclient.gui.ChatFormatting;
import dev.zprestige.ruby.Ruby;
import dev.zprestige.ruby.eventbus.annotation.RegisterListener;
import dev.zprestige.ruby.events.MoveEvent;
import dev.zprestige.ruby.events.PacketEvent;
import dev.zprestige.ruby.module.Category;
import dev.zprestige.ruby.module.Module;
import dev.zprestige.ruby.module.ModuleInfo;
import dev.zprestige.ruby.module.visual.ESP;
import dev.zprestige.ruby.setting.impl.*;
import dev.zprestige.ruby.util.EntityUtil;
import dev.zprestige.ruby.util.MessageUtil;
import dev.zprestige.ruby.util.Timer;
import net.minecraft.init.MobEffects;
import net.minecraft.network.play.server.SPacketPlayerPosLook;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.MovementInput;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import org.lwjgl.input.Keyboard;

import java.awt.*;
import java.util.Arrays;
import java.util.Objects;
import java.util.function.Predicate;

@ModuleInfo(name = "Speed", category = Category.Movement, description = "Moves the speed")
public class Speed extends Module {
    public static Speed Instance;
    public ParentSetting modes = createSetting("Modes");
    public ModeSetting mode = createSetting("Mode", "Normal", Arrays.asList("Normal", "Switch")).setParent(modes);
    public KeySetting switchKey = createSetting("Switch Key", Keyboard.KEY_NONE, v -> mode.getValue().equals("Switch")).setParent(modes);
    public BooleanSetting announceSwitch = createSetting("Announce Switch", false, v -> mode.getValue().equals("Switch")).setParent(modes);
    public BooleanSetting switchPullToGround = createSetting("Switch Pull To Ground", false, v -> mode.getValue().equals("Switch")).setParent(modes);
    public ModeSetting speedMode = createSetting("Speed Mode", "Strafe", Arrays.asList("OnGround", "Strafe")).setParent(modes);

    public ParentSetting misc = createSetting("Misc");
    public BooleanSetting returnOnShift = createSetting("Return On Shift", false).setParent(misc);
    public BooleanSetting slowdownOnGroundNearHoles = createSetting("Slow Down On Ground Near Holes", false).setParent(misc);
    public FloatSetting slowDownValue = createSetting("Slow Down Value", 1.0f, 0.1f, 5.0f).setParent(misc);
    public BooleanSetting liquids = createSetting("Liquids", false).setParent(misc);
    public BooleanSetting useTimer = createSetting("Use Timer", false).setParent(misc);
    public FloatSetting timerAmount = createSetting("Timer Amount", 1.0f, 0.9f, 2.0f, (Predicate<Float>) v -> useTimer.getValue()).setParent(misc);
    public ParentSetting factoring = createSetting("Factoring");
    public ModeSetting strafeFactorMode = createSetting("Strafe Factor Mode", "Manual", Arrays.asList("Manual", "Auto")).setParent(factoring);
    public ModeSetting reFactorizeMode = createSetting("Re-Factorize Mode", "Accelerating", Arrays.asList("Accelerating", "Instant"), v -> strafeFactorMode.getValue().equals("Auto")).setParent(factoring);
    public IntegerSetting reFactorizeStartDelay = createSetting("Re-Factorize Start Delay", 100, 10, 500, (Predicate<Integer>) v -> strafeFactorMode.getValue().equals("Auto")).setParent(factoring);
    public FloatSetting reFactorizeStart = createSetting("Re-Factorize Start Value", 1.0f, 0.1f, 1.0f, (Predicate<Float>) v -> strafeFactorMode.getValue().equals("Auto")).setParent(factoring);
    public FloatSetting reFactorizeTarget = createSetting("Re-Factorize Target", 1.1f, 0.1f, 3.0f, (Predicate<Float>) v -> strafeFactorMode.getValue().equals("Auto")).setParent(factoring);
    public FloatSetting accelerationFactor = createSetting("Acceleration Factor", 1.0f, 0.1f, 5.0f, (Predicate<Float>) v -> strafeFactorMode.getValue().equals("Auto") && reFactorizeMode.getValue().equals("Accelerating")).setParent(factoring);
    public FloatSetting strafeFactor = createSetting("Strafe Factor", 1.0f, 0.1f, 3.0f, (Predicate<Float>) v -> ((mode.getValue().equals("Normal") && speedMode.getValue().equals("Strafe")) || mode.getValue().equals("Switch")) && strafeFactorMode.getValue().equals("Manual")).setParent(factoring);
    public FloatSetting strafeFactorSpeed = createSetting("Strafe Factor Speed Amplifier", 1.2f, 0.1f, 3.0f, (Predicate<Float>) v -> ((mode.getValue().equals("Normal") && speedMode.getValue().equals("Strafe")) || mode.getValue().equals("Switch")) && strafeFactorMode.getValue().equals("Manual") ).setParent(factoring);
    public double previousDistance;
    public double motionSpeed;
    public int currentState = 1;
    public float f = strafeFactor.getValue();
    public Timer factorTimer = new Timer();
    public Timer postSwitchTimer = new Timer();
    public boolean isCloseToHole, isTimering;

    public Speed() {
        Instance = this;
    }


    @Override
    public void onDisable() {
        mc.timer.tickLength = 50.0f;
    }

    @SubscribeEvent
    public void onKeyInput(InputEvent.KeyInputEvent event) {
        if (Ruby.mc.player == null || Ruby.mc.world == null || !mode.getValue().equals("Switch") || mc.currentScreen != null || !isEnabled())
            return;
        if (Keyboard.getEventKeyState() && switchKey.getValue() != -1 && switchKey.getValue().equals(Keyboard.getEventKey())) {
                switch (speedMode.getValue()) {
                    case "OnGround":
                        speedMode.setValue("Strafe");
                        break;
                    case "Strafe":
                        speedMode.setValue("OnGround");
                        if (switchPullToGround.getValue())
                            postSwitchTimer.setTime(0);
                        break;
                }
            if (announceSwitch.getValue())
                MessageUtil.sendRemovableMessage(ChatFormatting.BOLD + "Speed " + ChatFormatting.RESET + "switched mode to " + ChatFormatting.RED + speedMode.getValue() + ChatFormatting.RESET + ".", 1);
        }
    }

    @Override
    public void onTick() {
        previousDistance = Math.sqrt((mc.player.posX - mc.player.prevPosX) * (mc.player.posX - mc.player.prevPosX) + (mc.player.posZ - mc.player.prevPosZ) * (mc.player.posZ - mc.player.prevPosZ));
        if (!strafeFactorMode.getValue().equals("Manual") && f < reFactorizeTarget.getValue()) {
            switch (reFactorizeMode.getValue()) {
                case "Instant":
                    if (factorTimer.getTime(reFactorizeStartDelay.getValue()))
                        f = reFactorizeTarget.getValue();
                    break;
                case "Accelerating":
                    if (factorTimer.getTime(reFactorizeStartDelay.getValue()))
                        f += accelerationFactor.getValue() / 10;
                    break;

            }
        }
        if (switchPullToGround.getValue() && postSwitchTimer.getTimeSub(20))
            mc.player.motionY = -1;
        if (slowdownOnGroundNearHoles.getValue()) {
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
        if (nullCheck() || !isEnabled() || (!liquids.getValue() && (mc.player.isInWater() || mc.player.isInLava() || mc.player.isSpectator())) || (switchPullToGround.getValue() && postSwitchTimer.getTimeSub(20)) || mc.player.isElytraFlying())
            return;
        if (returnOnShift.getValue() && mc.gameSettings.keyBindSprint.isKeyDown())
            return;
        if (!mc.player.isSprinting())
            mc.player.setSprinting(true);
        if (!dev.zprestige.ruby.module.exploit.Timer.Instance.isEnabled() && !TickShift.Instance.isEnabled() && timerAmount.getValue() != 1.0f) {
            if (useTimer.getValue() && (Ruby.mc.player.moveForward != 0.0 || Ruby.mc.player.moveStrafing != 0.0)) {
                mc.timer.tickLength = 50.0f / timerAmount.getValue();
                isTimering = true;
            }else if (isTimering){
                mc.timer.tickLength = 50.0f;
                isTimering = false;
            }
        }
        PotionEffect speed = mc.player.getActivePotionEffect(MobEffects.SPEED);
        switch (speedMode.getValue()) {
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
                        motionSpeed = previousDistance - 0.76 * (previousDistance - EntityUtil.getBaseMotionSpeed() * (strafeFactorMode.getValue().equals("Auto") ? f : speed != null ? strafeFactorSpeed.getValue() : strafeFactor.getValue()));
                }
                motionSpeed = Math.max(motionSpeed, EntityUtil.getBaseMotionSpeed() * (strafeFactorMode.getValue().equals("Auto") ? f : speed != null ? strafeFactorSpeed.getValue() : strafeFactor.getValue()));
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
                        event.motionX = ((double) moveForward * EntityUtil.getMaxSpeed() * cos + (double) moveStrafe * EntityUtil.getMaxSpeed() * sin) / (slowdownOnGroundNearHoles.getValue() && isCloseToHole ? slowDownValue.getValue() : 1.0);
                        event.motionZ = ((double) moveForward * EntityUtil.getMaxSpeed() * sin - (double) moveStrafe * EntityUtil.getMaxSpeed() * cos) / (slowdownOnGroundNearHoles.getValue() && isCloseToHole ? slowDownValue.getValue() : 1.0);
                    }
                }
                break;
        }
    }

    @RegisterListener
    public void onPacketReceive(PacketEvent.PacketReceiveEvent event) {
        if (nullCheck() || !isEnabled() || strafeFactorMode.getValue().equals("Manual") || !(event.getPacket() instanceof SPacketPlayerPosLook))
            return;
        f = reFactorizeStart.getValue();
        factorTimer.setTime(0);
    }
}
