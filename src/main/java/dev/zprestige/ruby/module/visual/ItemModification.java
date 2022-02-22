package dev.zprestige.ruby.module.visual;

import dev.zprestige.ruby.eventbus.annotation.RegisterListener;
import dev.zprestige.ruby.events.PacketEvent;
import dev.zprestige.ruby.events.RenderItemEvent;
import dev.zprestige.ruby.module.Module;
import dev.zprestige.ruby.setting.impl.*;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.network.play.client.CPacketAnimation;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.Arrays;
import java.util.function.Predicate;

public class ItemModification extends Module {
    public static ItemModification Instance;
    public ParentSetting misc = createSetting("Misc");
    public ColorSetting color = createSetting("Item Colors", new Color(255, 255, 255)).setParent(misc);
    public BooleanSetting animationTest = createSetting("Animation", false).setParent(misc);
    public ModeSetting direction = createSetting("Direction", "Forwards", Arrays.asList("Forwards", "Backwards")).setParent(misc);
    public FloatSetting animationSpeed = createSetting("Animation Speed", 0.1f, 0.1f, 1.0f, (Predicate<Float>) v -> animationTest.getValue()).setParent(misc);
    public FloatSetting animationDistance = createSetting("Animation Distance", 0.1f, 0.1f, 10.0f, (Predicate<Float>) v -> animationTest.getValue()).setParent(misc);

    public ParentSetting mainHands = createSetting("MainHands");
    public BooleanSetting mainhand = createSetting("Mainhand", false).setParent(mainHands);
    public BooleanSetting mainhandTranslation = createSetting("Mainhand Translation", false, v -> mainhand.getValue()).setParent(mainHands);
    public FloatSetting mainhandX = createSetting("Mainhand X", 0.0f, -10.0f, 10.0f, (Predicate<Float>) v -> mainhand.getValue() && mainhandTranslation.getValue()).setParent(mainHands);
    public FloatSetting mainhandY = createSetting("Mainhand Y", 0.0f, -10.0f, 10.0f, (Predicate<Float>) v -> mainhand.getValue() && mainhandTranslation.getValue()).setParent(mainHands);
    public FloatSetting mainhandZ = createSetting("Mainhand Z", 0.0f, -10.0f, 10.0f, (Predicate<Float>) v -> mainhand.getValue() && mainhandTranslation.getValue()).setParent(mainHands);
    public BooleanSetting mainhandScaling = createSetting("Mainhand Scaling", false, v -> mainhand.getValue()).setParent(mainHands);
    public FloatSetting mainhandScaleX = createSetting("Mainhand Scale X", 0.0f, -10.0f, 10.0f, (Predicate<Float>) v -> mainhand.getValue() && mainhandScaling.getValue()).setParent(mainHands);
    public FloatSetting mainhandScaleY = createSetting("Mainhand Scale Y", 0.0f, -10.0f, 10.0f, (Predicate<Float>) v -> mainhand.getValue() && mainhandScaling.getValue()).setParent(mainHands);
    public FloatSetting mainhandScaleZ = createSetting("Mainhand Scale Z", 0.0f, -10.0f, 10.0f, (Predicate<Float>) v -> mainhand.getValue() && mainhandScaling.getValue()).setParent(mainHands);
    public BooleanSetting mainhandRotation = createSetting("Mainhand Rotation", false, v -> mainhand.getValue()).setParent(mainHands);
    public FloatSetting mainhandRotationX = createSetting("Mainhand Rotation X", 0.0f, 0.0f, 10.0f, (Predicate<Float>) v -> mainhand.getValue() && mainhandRotation.getValue()).setParent(mainHands);
    public FloatSetting mainhandRotationY = createSetting("Mainhand Rotation Y", 0.0f, 0.0f, 10.0f, (Predicate<Float>) v -> mainhand.getValue() && mainhandRotation.getValue()).setParent(mainHands);
    public FloatSetting mainhandRotationZ = createSetting("Mainhand Rotation Z", 0.0f, 0.0f, 10.0f, (Predicate<Float>) v -> mainhand.getValue() && mainhandRotation.getValue()).setParent(mainHands);

    public ParentSetting offHands = createSetting("OffHands");
    public BooleanSetting offhand = createSetting("Offhand", false).setParent(offHands);
    public BooleanSetting offhandTranslation = createSetting("Offhand Translation", false, v -> offhand.getValue()).setParent(offHands);
    public FloatSetting offhandX = createSetting("Offhand X", 0.0f, -10.0f, 10.0f, (Predicate<Float>) v -> offhand.getValue() && offhandTranslation.getValue()).setParent(offHands);
    public FloatSetting offhandY = createSetting("Offhand Y", 0.0f, -10.0f, 10.0f, (Predicate<Float>) v -> offhand.getValue() && offhandTranslation.getValue()).setParent(offHands);
    public FloatSetting offhandZ = createSetting("Offhand Z", 0.0f, -10.0f, 10.0f, (Predicate<Float>) v -> offhand.getValue() && offhandTranslation.getValue()).setParent(offHands);
    public BooleanSetting offhandScaling = createSetting("Offhand Scaling", false, v -> offhand.getValue()).setParent(offHands);
    public FloatSetting offhandScaleX = createSetting("Offhand Scale X", 0.0f, -10.0f, 10.0f, (Predicate<Float>) v -> offhand.getValue() && offhandScaling.getValue()).setParent(offHands);
    public FloatSetting offhandScaleY = createSetting("Offhand Scale Y", 0.0f, -10.0f, 10.0f, (Predicate<Float>) v -> offhand.getValue() && offhandScaling.getValue()).setParent(offHands);
    public FloatSetting offhandScaleZ = createSetting("Offhand Scale Z", 0.0f, -10.0f, 10.0f, (Predicate<Float>) v -> offhand.getValue() && offhandScaling.getValue()).setParent(offHands);
    public BooleanSetting offhandRotation = createSetting("Offhand Rotation", false, v -> offhand.getValue());
    public FloatSetting offhandRotationX = createSetting("Offhand Rotation X", 0.0f, 0.0f, 10.0f, (Predicate<Float>) v -> offhand.getValue() && offhandRotation.getValue()).setParent(offHands);
    public FloatSetting offhandRotationY = createSetting("Offhand Rotation Y", 0.0f, 0.0f, 10.0f, (Predicate<Float>) v -> offhand.getValue() && offhandRotation.getValue()).setParent(offHands);
    public FloatSetting offhandRotationZ = createSetting("Offhand Rotation Z", 0.0f, 0.0f, 10.0f, (Predicate<Float>) v -> offhand.getValue() && offhandRotation.getValue()).setParent(offHands);
    public boolean swung;
    public boolean forwarded;
    public float anim;

    public ItemModification() {
        Instance = this;
    }

    @Override
    public void onGlobalRenderTick() {
        if (swung) {
            switch (direction.getValue()) {
                case "Forwards":
                    if (!forwarded) {
                        if (anim < animationDistance.getValue()) {
                            anim += animationSpeed.getValue();
                        } else {
                            forwarded = true;
                        }
                    } else {
                        if (anim > 0.0f) {
                            anim -= animationSpeed.getValue();
                        } else {
                            swung = false;
                            forwarded = false;
                        }
                    }
                    break;
                case "Backwards":
                    if (!forwarded) {
                        if (anim > -animationDistance.getValue()) {
                            anim -= animationSpeed.getValue();
                        } else {
                            forwarded = true;
                        }
                    } else {
                        if (anim < 0.0f) {
                            anim += animationSpeed.getValue();
                        } else {
                            swung = false;
                            forwarded = false;
                        }
                    }
                    break;
            }
        }
    }

    @RegisterListener
    public void onRenderMainhand(RenderItemEvent.MainHand event) {
        if (isEnabled() && event.entityLivingBase.equals(mc.player) && mainhand.getValue()) {
            if (animationTest.getValue()) {
                GL11.glTranslated(mainhandX.getValue() / 40.0f, mainhandY.getValue() / 40.0f, (mainhandZ.getValue() - anim) / 40.0f);
            }
            if (mainhandTranslation.getValue())
                GL11.glTranslated(mainhandX.getValue() / 40.0f, mainhandY.getValue() / 40.0f, mainhandZ.getValue() / 40.0f);
            if (mainhandScaling.getValue())
                GlStateManager.scale((mainhandScaleX.getValue() / 10.0f) + 1.0f, (mainhandScaleY.getValue() / 10.0f) + 1.0f, (mainhandScaleZ.getValue() / 10.0f) + 1.0f);
            if (!mainhandRotation.getValue())
                return;
            GlStateManager.rotate(mainhandRotationX.getValue() * 36.0f, 1.0f, 0.0f, 0.0f);
            GlStateManager.rotate(mainhandRotationY.getValue() * 36.0f, 0.0f, 1.0f, 0.0f);
            GlStateManager.rotate(mainhandRotationZ.getValue() * 36.0f, 0.0f, 0.0f, 1.0f);
        }
    }

    @RegisterListener
    public void onRenderOffhand(RenderItemEvent.Offhand event) {
        if (isEnabled() && event.entityLivingBase.equals(mc.player) && offhand.getValue()) {
            if (offhandTranslation.getValue())
                GL11.glTranslated(offhandX.getValue() / 40.0f, offhandY.getValue() / 40.0f, offhandZ.getValue() / 40.0f);
            if (offhandScaling.getValue())
                GlStateManager.scale((offhandScaleX.getValue() / 10.0f) + 1.0f, (offhandScaleY.getValue() / 10.0f) + 1.0f, (offhandScaleZ.getValue() / 10.0f) + 1.0f);
            if (!offhandRotation.getValue())
                return;
            GlStateManager.rotate(offhandRotationX.getValue() * 36.0f, 1.0f, 0.0f, 0.0f);
            GlStateManager.rotate(offhandRotationY.getValue() * 36.0f, 0.0f, 1.0f, 0.0f);
            GlStateManager.rotate(offhandRotationZ.getValue() * 36.0f, 0.0f, 0.0f, 1.0f);
        }
    }

    @RegisterListener
    public void onPacketSent(PacketEvent.PacketSendEvent event) {
        if (nullCheck() || !isEnabled() || !(event.getPacket() instanceof CPacketAnimation))
            return;
        swung = true;
    }
}
