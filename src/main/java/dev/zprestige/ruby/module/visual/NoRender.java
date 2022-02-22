package dev.zprestige.ruby.module.visual;

import dev.zprestige.ruby.eventbus.annotation.RegisterListener;
import dev.zprestige.ruby.events.PacketEvent;
import dev.zprestige.ruby.module.Module;
import dev.zprestige.ruby.setting.impl.BooleanSetting;
import net.minecraft.network.play.server.SPacketExplosion;
import net.minecraftforge.client.event.RenderBlockOverlayEvent;

public class NoRender extends Module {
    public static NoRender Instance;
    public BooleanSetting hurtCam = createSetting("Hurt Cam", false);
    public BooleanSetting fire = createSetting("Fire", false);
    public BooleanSetting explosions = createSetting("Explosions", false);
    public BooleanSetting insideBlocks = createSetting("Inside Blocks", false);
    public BooleanSetting armor = createSetting("Armor", false);

    public NoRender() {
        Instance = this;
    }

    @RegisterListener
    public void onRenderBlockOverlay(RenderBlockOverlayEvent event) {
        if (nullCheck() || !isEnabled())
            return;

        if (event.getOverlayType().equals(RenderBlockOverlayEvent.OverlayType.FIRE))
            event.setCanceled(fire.getValue());

        if (event.getOverlayType().equals(RenderBlockOverlayEvent.OverlayType.BLOCK))
            event.setCanceled(insideBlocks.getValue());
    }

    @RegisterListener
    public void onPacketReceive(PacketEvent.PacketReceiveEvent event) {
        if (nullCheck() || !isEnabled())
            return;

        if (event.getPacket() instanceof SPacketExplosion)
            event.setCancelled(explosions.getValue());
    }

}
