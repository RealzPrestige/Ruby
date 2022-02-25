package dev.zprestige.ruby.module.visual;

import dev.zprestige.ruby.eventbus.annotation.RegisterListener;
import dev.zprestige.ruby.events.PacketEvent;
import dev.zprestige.ruby.events.ParticleEvent;
import dev.zprestige.ruby.module.Module;
import dev.zprestige.ruby.settings.impl.*;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.network.play.server.SPacketEffect;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class WorldTweaks extends Module {
    public static WorldTweaks Instance;
    public final Parent weather = Menu.Parent("Weather");
    public final ColorBox fogColor = Menu.Color("Fog Color").parent(weather);
    public final Slider density = Menu.Slider("Density", 0.0f, 1000.0f).parent(weather);
    public final ComboBox weatherMode = Menu.ComboBox("Weather", new String[]{"Clear", "Rain", "Thunder"}).parent(weather);
    public final Slider time = Menu.Slider("Time", 0, 24000).parent(weather);
    public final Parent player = Menu.Parent("Player");
    public final Slider fov = Menu.Slider("Fov", 50.0f, 200.0f).parent(player);
    public final Slider chunkLoadDelay = Menu.Slider("Chunk Load Delay", 0, 300).parent(player);
    public final Switch antiParticles = Menu.Switch("AntiParticles").parent(player);
    public final Switch noEffects = Menu.Switch("No SPacketEffects").parent(player);
    public final Switch noSwing = Menu.Switch("No Swing").parent(player);
    public final Switch noSwitchAnim = Menu.Switch("No Switch Anim").parent(player);
    public final Switch fullBright = Menu.Switch("FullBright").parent(player);


    public WorldTweaks() {
        Instance = this;
    }

    @RegisterListener
    public void onParticle(ParticleEvent event) {
        if (nullCheck() || !isEnabled())
            return;
        event.setCancelled(antiParticles.GetSwitch());
    }

    @SubscribeEvent
    public void onWorld(EntityViewRenderEvent.RenderFogEvent event) {
        if (isEnabled())
            mc.world.setTotalWorldTime((long) time.GetSlider());
        mc.world.setWorldTime((long) time.GetSlider());
    }

    @Override
    public void onTick() {
        mc.world.setWorldTime((long) time.GetSlider());
        switch (weatherMode.GetCombo()) {
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
        if (noSwitchAnim.GetSwitch()) {
            if (mc.entityRenderer.itemRenderer.prevEquippedProgressMainHand >= 0.9) {
                mc.entityRenderer.itemRenderer.equippedProgressMainHand = 1.0f;
                mc.entityRenderer.itemRenderer.itemStackMainHand = mc.player.getHeldItemMainhand();
            }
            if (mc.entityRenderer.itemRenderer.prevEquippedProgressOffHand >= 0.9) {
                mc.entityRenderer.itemRenderer.equippedProgressOffHand = 1.0f;
                mc.entityRenderer.itemRenderer.itemStackOffHand = mc.player.getHeldItemOffhand();
            }
        }
        if (noSwing.GetSwitch()) {
            mc.player.isSwingInProgress = false;
            mc.player.swingProgressInt = 0;
            mc.player.swingProgress = 0.0f;
            mc.player.prevSwingProgress = 0.0f;
        }
        mc.gameSettings.setOptionFloatValue(GameSettings.Options.FOV, fov.GetSlider());

        if (fullBright.GetSwitch() && mc.gameSettings.gammaSetting != 6900)
            mc.gameSettings.gammaSetting = 6900;
    }

    @SubscribeEvent
    public void onFogColor(EntityViewRenderEvent.FogColors event) {
        if (nullCheck() || !isEnabled())
            return;
        event.setRed(fogColor.GetColor().getRed() / 255.0f);
        event.setGreen(fogColor.GetColor().getGreen() / 255.0f);
        event.setBlue(fogColor.GetColor().getBlue() / 255.0f);
    }

    @SubscribeEvent
    public void onFogDensity(EntityViewRenderEvent.FogDensity event) {
        if (nullCheck() || !isEnabled())
            return;
        event.setDensity(density.GetSlider());
        if (mc.player.isInWater() || mc.player.isInLava())
            event.setCanceled(true);
    }

    @RegisterListener
    public void onPacketReceive(PacketEvent.PacketReceiveEvent event) {
        if (nullCheck() || !isEnabled() || !noEffects.GetSwitch() || !(event.getPacket() instanceof SPacketEffect))
            return;
        event.setCancelled(true);
    }

}
