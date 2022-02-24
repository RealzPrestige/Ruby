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
    public final Parent misc = Menu.Switch("Misc");
    public final ColorBox color = Menu.Switch("Item Colors", new Color(255, 255, 255)).parent(misc);
    public final Switch animationTest = Menu.Switch("Animation").parent(misc);
    public final ComboBox direction = Menu.Switch("Direction", "Forwards", Arrays.asList("Forwards", "Backwards")).parent(misc);
    public final Slider animationSpeed = Menu.Switch("Animation Speed", 0.1f, 0.1f, 1.0f, (Predicate<Float>) v -> animationTest.getValue()).parent(misc);
    public final Slider animationDistance = Menu.Switch("Animation Distance", 0.1f, 0.1f, 10.0f, (Predicate<Float>) v -> animationTest.getValue()).parent(misc);

    public final Parent mainHands = Menu.Switch("MainHands");
    public final Switch mainhand = Menu.Switch("Mainhand").parent(mainHands);
    public final Switch mainhandTranslation = Menu.Switch("Mainhand Translation", false, v -> mainhand.getValue()).parent(mainHands);
    public final Slider mainhandX = Menu.Switch("Mainhand X", 0.0f, -10.0f, 10.0f, (Predicate<Float>) v -> mainhand.getValue() && mainhandTranslation.getValue()).parent(mainHands);
    public final Slider mainhandY = Menu.Switch("Mainhand Y", 0.0f, -10.0f, 10.0f, (Predicate<Float>) v -> mainhand.getValue() && mainhandTranslation.getValue()).parent(mainHands);
    public final Slider mainhandZ = Menu.Switch("Mainhand Z", 0.0f, -10.0f, 10.0f, (Predicate<Float>) v -> mainhand.getValue() && mainhandTranslation.getValue()).parent(mainHands);
    public final Switch mainhandScaling = Menu.Switch("Mainhand Scaling", false, v -> mainhand.getValue()).parent(mainHands);
    public final Slider mainhandScaleX = Menu.Switch("Mainhand Scale X", 0.0f, -10.0f, 10.0f, (Predicate<Float>) v -> mainhand.getValue() && mainhandScaling.getValue()).parent(mainHands);
    public final Slider mainhandScaleY = Menu.Switch("Mainhand Scale Y", 0.0f, -10.0f, 10.0f, (Predicate<Float>) v -> mainhand.getValue() && mainhandScaling.getValue()).parent(mainHands);
    public final Slider mainhandScaleZ = Menu.Switch("Mainhand Scale Z", 0.0f, -10.0f, 10.0f, (Predicate<Float>) v -> mainhand.getValue() && mainhandScaling.getValue()).parent(mainHands);
    public final Switch mainhandRotation = Menu.Switch("Mainhand Rotation", false, v -> mainhand.getValue()).parent(mainHands);
    public final Slider mainhandRotationX = Menu.Switch("Mainhand Rotation X", 0.0f, 0.0f, 10.0f, (Predicate<Float>) v -> mainhand.getValue() && mainhandRotation.getValue()).parent(mainHands);
    public final Slider mainhandRotationY = Menu.Switch("Mainhand Rotation Y", 0.0f, 0.0f, 10.0f, (Predicate<Float>) v -> mainhand.getValue() && mainhandRotation.getValue()).parent(mainHands);
    public final Slider mainhandRotationZ = Menu.Switch("Mainhand Rotation Z", 0.0f, 0.0f, 10.0f, (Predicate<Float>) v -> mainhand.getValue() && mainhandRotation.getValue()).parent(mainHands);

    public final Parent offHands = Menu.Switch("OffHands");
    public final Switch offhand = Menu.Switch("Offhand", false).parent(offHands);
    public final Switch offhandTranslation = Menu.Switch("Offhand Translation", false, v -> offhand.getValue()).parent(offHands);
    public final Slider offhandX = Menu.Switch("Offhand X", 0.0f, -10.0f, 10.0f, (Predicate<Float>) v -> offhand.getValue() && offhandTranslation.getValue()).parent(offHands);
    public final Slider offhandY = Menu.Switch("Offhand Y", 0.0f, -10.0f, 10.0f, (Predicate<Float>) v -> offhand.getValue() && offhandTranslation.getValue()).parent(offHands);
    public final Slider offhandZ = Menu.Switch("Offhand Z", 0.0f, -10.0f, 10.0f, (Predicate<Float>) v -> offhand.getValue() && offhandTranslation.getValue()).parent(offHands);
    public final Switch offhandScaling = Menu.Switch("Offhand Scaling", false, v -> offhand.getValue()).parent(offHands);
    public final Slider offhandScaleX = Menu.Switch("Offhand Scale X", 0.0f, -10.0f, 10.0f, (Predicate<Float>) v -> offhand.getValue() && offhandScaling.getValue()).parent(offHands);
    public final Slider offhandScaleY = Menu.Switch("Offhand Scale Y", 0.0f, -10.0f, 10.0f, (Predicate<Float>) v -> offhand.getValue() && offhandScaling.getValue()).parent(offHands);
    public final Slider offhandScaleZ = Menu.Switch("Offhand Scale Z", 0.0f, -10.0f, 10.0f, (Predicate<Float>) v -> offhand.getValue() && offhandScaling.getValue()).parent(offHands);
    public final Switch offhandRotation = Menu.Switch("Offhand Rotation", false, v -> offhand.getValue());
    public final Slider offhandRotationX = Menu.Switch("Offhand Rotation X", 0.0f, 0.0f, 10.0f, (Predicate<Float>) v -> offhand.getValue() && offhandRotation.getValue()).parent(offHands);
    public final Slider offhandRotationY = Menu.Switch("Offhand Rotation Y", 0.0f, 0.0f, 10.0f, (Predicate<Float>) v -> offhand.getValue() && offhandRotation.getValue()).parent(offHands);
    public final Slider offhandRotationZ = Menu.Switch("Offhand Rotation Z", 0.0f, 0.0f, 10.0f, (Predicate<Float>) v -> offhand.getValue() && offhandRotation.getValue()).parent(offHands);
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
