package dev.zprestige.ruby.module.visual;

import dev.zprestige.ruby.eventbus.annotation.RegisterListener;
import dev.zprestige.ruby.events.PacketEvent;
import dev.zprestige.ruby.events.RenderItemEvent;
import dev.zprestige.ruby.module.Module;
import dev.zprestige.ruby.settings.impl.*;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.network.play.client.CPacketAnimation;
import org.lwjgl.opengl.GL11;

public class ItemModification extends Module {
    public static ItemModification Instance;
    public final Parent misc = Menu.Parent("Misc");
    public final ColorBox color = Menu.Color("Item Colors").parent(misc);
    public final Switch animationTest = Menu.Switch("Animation").parent(misc);
    public final ComboBox direction = Menu.ComboBox("Direction", new String[]{"Forwards", "Backwards"}).parent(misc);
    public final Slider animationSpeed = Menu.Slider("Animation Speed", 0.1f, 1.0f).parent(misc);
    public final Slider animationDistance = Menu.Slider("Animation Distance", 0.1f, 10.0f).parent(misc);

    public final Parent mainHands = Menu.Parent("MainHands");
    public final Switch mainhand = Menu.Switch("Mainhand").parent(mainHands);
    public final Switch mainhandTranslation = Menu.Switch("Mainhand Translation").parent(mainHands);
    public final Slider mainhandX = Menu.Slider("Mainhand X", -10.0f, 10.0f).parent(mainHands);
    public final Slider mainhandY = Menu.Slider("Mainhand Y", -10.0f, 10.0f).parent(mainHands);
    public final Slider mainhandZ = Menu.Slider("Mainhand Z", -10.0f, 10.0f).parent(mainHands);
    public final Switch mainhandScaling = Menu.Switch("Mainhand Scaling").parent(mainHands);
    public final Slider mainhandScaleX = Menu.Slider("Mainhand Scale X", -10.0f, 10.0f).parent(mainHands);
    public final Slider mainhandScaleY = Menu.Slider("Mainhand Scale Y", -10.0f, 10.0f).parent(mainHands);
    public final Slider mainhandScaleZ = Menu.Slider("Mainhand Scale Z", -10.0f, 10.0f).parent(mainHands);
    public final Switch mainhandRotation = Menu.Switch("Mainhand Rotation").parent(mainHands);
    public final Slider mainhandRotationX = Menu.Slider("Mainhand Rotation X", 0.0f, 10.0f).parent(mainHands);
    public final Slider mainhandRotationY = Menu.Slider("Mainhand Rotation Y", 0.0f, 10.0f).parent(mainHands);
    public final Slider mainhandRotationZ = Menu.Slider("Mainhand Rotation Z", 0.0f, 10.0f).parent(mainHands);

    public final Parent offHands = Menu.Parent("OffHands");
    public final Switch offhand = Menu.Switch("Offhand").parent(offHands);
    public final Switch offhandTranslation = Menu.Switch("Offhand Translation").parent(offHands);
    public final Slider offhandX = Menu.Slider("Offhand X", -10.0f, 10.0f).parent(offHands);
    public final Slider offhandY = Menu.Slider("Offhand Y", -10.0f, 10.0f).parent(offHands);
    public final Slider offhandZ = Menu.Slider("Offhand Z", -10.0f, 10.0f).parent(offHands);
    public final Switch offhandScaling = Menu.Switch("Offhand Scaling").parent(offHands);
    public final Slider offhandScaleX = Menu.Slider("Offhand Scale X", -10.0f, 10.0f).parent(offHands);
    public final Slider offhandScaleY = Menu.Slider("Offhand Scale Y", -10.0f, 10.0f).parent(offHands);
    public final Slider offhandScaleZ = Menu.Slider("Offhand Scale Z", -10.0f, 10.0f).parent(offHands);
    public final Switch offhandRotation = Menu.Switch("Offhand Rotation");
    public final Slider offhandRotationX = Menu.Slider("Offhand Rotation X", 0.0f, 10.0f).parent(offHands);
    public final Slider offhandRotationY = Menu.Slider("Offhand Rotation Y", 0.0f, 10.0f).parent(offHands);
    public final Slider offhandRotationZ = Menu.Slider("Offhand Rotation Z", 0.0f, 10.0f).parent(offHands);
    public boolean swung;
    public boolean forwarded;
    public float anim;

    public ItemModification() {
        Instance = this;
    }

    @Override
    public void onFrame(float partialTicks) {
        if (swung) {
            switch (direction.GetCombo()) {
                case "Forwards":
                    if (!forwarded) {
                        if (anim < animationDistance.GetSlider()) {
                            anim += animationSpeed.GetSlider();
                        } else {
                            forwarded = true;
                        }
                    } else {
                        if (anim > 0.0f) {
                            anim -= animationSpeed.GetSlider();
                        } else {
                            swung = false;
                            forwarded = false;
                        }
                    }
                    break;
                case "Backwards":
                    if (!forwarded) {
                        if (anim > -animationDistance.GetSlider()) {
                            anim -= animationSpeed.GetSlider();
                        } else {
                            forwarded = true;
                        }
                    } else {
                        if (anim < 0.0f) {
                            anim += animationSpeed.GetSlider();
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
        if (isEnabled() && event.entityLivingBase.equals(mc.player) && mainhand.GetSwitch()) {
            if (animationTest.GetSwitch()) {
                GL11.glTranslated(mainhandX.GetSlider() / 40.0f, mainhandY.GetSlider() / 40.0f, (mainhandZ.GetSlider() - anim) / 40.0f);
            }
            if (mainhandTranslation.GetSwitch())
                GL11.glTranslated(mainhandX.GetSlider() / 40.0f, mainhandY.GetSlider() / 40.0f, mainhandZ.GetSlider() / 40.0f);
            if (mainhandScaling.GetSwitch())
                GlStateManager.scale((mainhandScaleX.GetSlider() / 10.0f) + 1.0f, (mainhandScaleY.GetSlider() / 10.0f) + 1.0f, (mainhandScaleZ.GetSlider() / 10.0f) + 1.0f);
            if (!mainhandRotation.GetSwitch())
                return;
            GlStateManager.rotate(mainhandRotationX.GetSlider() * 36.0f, 1.0f, 0.0f, 0.0f);
            GlStateManager.rotate(mainhandRotationY.GetSlider() * 36.0f, 0.0f, 1.0f, 0.0f);
            GlStateManager.rotate(mainhandRotationZ.GetSlider() * 36.0f, 0.0f, 0.0f, 1.0f);
        }
    }

    @RegisterListener
    public void onRenderOffhand(RenderItemEvent.Offhand event) {
        if (isEnabled() && event.entityLivingBase.equals(mc.player) && offhand.GetSwitch()) {
            if (offhandTranslation.GetSwitch())
                GL11.glTranslated(offhandX.GetSlider() / 40.0f, offhandY.GetSlider() / 40.0f, offhandZ.GetSlider() / 40.0f);
            if (offhandScaling.GetSwitch())
                GlStateManager.scale((offhandScaleX.GetSlider() / 10.0f) + 1.0f, (offhandScaleY.GetSlider() / 10.0f) + 1.0f, (offhandScaleZ.GetSlider() / 10.0f) + 1.0f);
            if (!offhandRotation.GetSwitch())
                return;
            GlStateManager.rotate(offhandRotationX.GetSlider() * 36.0f, 1.0f, 0.0f, 0.0f);
            GlStateManager.rotate(offhandRotationY.GetSlider() * 36.0f, 0.0f, 1.0f, 0.0f);
            GlStateManager.rotate(offhandRotationZ.GetSlider() * 36.0f, 0.0f, 0.0f, 1.0f);
        }
    }

    @RegisterListener
    public void onPacketSent(PacketEvent.PacketSendEvent event) {
        if (nullCheck() || !isEnabled() || !(event.getPacket() instanceof CPacketAnimation))
            return;
        swung = true;
    }
}
