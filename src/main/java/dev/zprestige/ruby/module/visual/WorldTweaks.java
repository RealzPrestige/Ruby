package dev.zprestige.ruby.module.visual;

import dev.zprestige.ruby.events.PacketEvent;
import dev.zprestige.ruby.events.ParticleEvent;
import dev.zprestige.ruby.module.Category;
import dev.zprestige.ruby.module.Module;
import dev.zprestige.ruby.module.ModuleInfo;
import dev.zprestige.ruby.setting.impl.*;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.network.play.server.SPacketEffect;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.awt.*;
import java.util.Arrays;

@ModuleInfo(name = "World", category = Category.Visual, description = "Changes THE WORLD")
public class WorldTweaks extends Module {
    public static WorldTweaks Instance;
    public ParentSetting weather = createSetting("Weather");
    public ColorSetting fogColor = createSetting("Fog Color", new Color(-1)).setParent(weather);
    public FloatSetting density = createSetting("Density", 1.0f, 0.0f, 1000.0f).setParent(weather);
    public ModeSetting weatherMode = createSetting("Weather", "Clear", Arrays.asList("Clear", "Rain", "Thunder")).setParent(weather);
    public IntegerSetting time = createSetting("Time", 0, 0, 24000).setParent(weather);
    public ParentSetting player = createSetting("Player");
    public FloatSetting fov = createSetting("Fov", 140.0f, 50.0f, 200.0f).setParent(player);
    public IntegerSetting chunkLoadDelay = createSetting("Chunk Load Delay", 100, 0, 300).setParent(player);
    public BooleanSetting antiParticles = createSetting("AntiParticles", false).setParent(player);
    public BooleanSetting noEffects = createSetting("No SPacketEffects", false).setParent(player);
    public BooleanSetting noSwing = createSetting("No Swing", false).setParent(player);
    public BooleanSetting noSwitchAnim = createSetting("No Switch Anim", false).setParent(player);
    public BooleanSetting fullBright = createSetting("FullBright", false).setParent(player);


    public WorldTweaks() {
        Instance = this;
    }

    @SubscribeEvent
    public void onParticle(ParticleEvent event) {
        if (nullCheck() || !isEnabled())
            return;
        event.setCanceled(antiParticles.getValue());
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

    @SubscribeEvent
    public void onPacketReceive(PacketEvent.PacketReceiveEvent event) {
        if (nullCheck() || !isEnabled() || !noEffects.getValue() || !(event.getPacket() instanceof SPacketEffect))
            return;
        event.setCanceled(true);
    }

}
