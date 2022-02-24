package dev.zprestige.ruby.module.visual;

import dev.zprestige.ruby.eventbus.annotation.RegisterListener;
import dev.zprestige.ruby.events.PacketEvent;
import dev.zprestige.ruby.events.ParticleEvent;
import dev.zprestige.ruby.module.Module;
import dev.zprestige.ruby.setting.impl.*;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.network.play.server.SPacketEffect;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.awt.*;
import java.util.Arrays;

public class WorldTweaks extends Module {
    public static WorldTweaks Instance;
    public final Parent weather = Menu.Switch("Weather");
    public final ColorBox fogColor = Menu.Switch("Fog Color", new Color(-1)).parent(weather);
    public final Slider density = Menu.Switch("Density", 1.0f, 0.0f, 1000.0f).parent(weather);
    public final ComboBox weatherMode = Menu.Switch("Weather", "Clear", Arrays.asList("Clear", "Rain", "Thunder")).parent(weather);
    public final Slider time = Menu.Switch("Time", 0, 0, 24000).parent(weather);
    public final Parent player = Menu.Switch("Player");
    public final Slider fov = Menu.Switch("Fov", 140.0f, 50.0f, 200.0f).parent(player);
    public final Slider chunkLoadDelay = Menu.Switch("Chunk Load Delay", 100, 0, 300).parent(player);
    public final Switch antiParticles = Menu.Switch("AntiParticles").parent(player);
    public final Switch noEffects = Menu.Switch("No SPacketEffects").parent(player);
    public final Switch noSwing = Menu.Switch("No Swing").parent(player);
    public final Switch noSwitchAnim = Menu.Switch("No Switch Anim", false).parent(player);
    public final Switch fullBright = Menu.Switch("FullBright", false).parent(player);


    public WorldTweaks() {
        Instance = this;
    }

    @RegisterListener
    public void onParticle(ParticleEvent event) {
        if (nullCheck() || !isEnabled())
            return;
        event.setCancelled(antiParticles.getValue());
    }

    @SubscribeEvent
    public void onWorld(EntityViewRenderEvent.RenderFogEvent event) {
        if (isEnabled())
            mc.world.setTotalWorldTime((long) time.getValue());
        mc.world.setWorldTime((long) time.getValue());
    }

    @Override
    public void onTick() {
        mc.world.setWorldTime(time.getValue());
        switch (weatherMode.getValue()) {
            case "Clear":
                mc.world.setRainStrength(0);
                break;
            case "Rain":
                mc.world.setRainStrength(1);
                break;
            case "Thunder":
                mc.world.setRainStrength(2);
                break;
        }
        if (noSwitchAnim.getValue()) {
            if (mc.entityRenderer.itemRenderer.prevEquippedProgressMainHand >= 0.9) {
                mc.entityRenderer.itemRenderer.equippedProgressMainHand = 1.0f;
                mc.entityRenderer.itemRenderer.itemStackMainHand = mc.player.getHeldItemMainhand();
            }
            if (mc.entityRenderer.itemRenderer.prevEquippedProgressOffHand >= 0.9) {
                mc.entityRenderer.itemRenderer.equippedProgressOffHand = 1.0f;
                mc.entityRenderer.itemRenderer.itemStackOffHand = mc.player.getHeldItemOffhand();
            }
        }
        if (noSwing.getValue()) {
            mc.player.isSwingInProgress = false;
            mc.player.swingProgressInt = 0;
            mc.player.swingProgress = 0.0f;
            mc.player.prevSwingProgress = 0.0f;
        }
        mc.gameSettings.setOptionFloatValue(GameSettings.Options.FOV, fov.getValue());

        if (fullBright.getValue() && mc.gameSettings.gammaSetting != 6900)
            mc.gameSettings.gammaSetting = 6900;
    }

    @SubscribeEvent
    public void onFogColor(EntityViewRenderEvent.FogColors event) {
        if (nullCheck() || !isEnabled())
            return;
        event.setRed(fogColor.getValue().getRed() / 255.0f);
        event.setGreen(fogColor.getValue().getGreen() / 255.0f);
        event.setBlue(fogColor.getValue().getBlue() / 255.0f);
    }

    @SubscribeEvent
    public void onFogDensity(EntityViewRenderEvent.FogDensity event) {
        if (nullCheck() || !isEnabled())
            return;
        event.setDensity(density.getValue());
        if (mc.player.isInWater() || mc.player.isInLava())
            event.setCanceled(true);
    }

    @RegisterListener
    public void onPacketReceive(PacketEvent.PacketReceiveEvent event) {
        if (nullCheck() || !isEnabled() || !noEffects.getValue() || !(event.getPacket() instanceof SPacketEffect))
            return;
        event.setCancelled(true);
    }

}
