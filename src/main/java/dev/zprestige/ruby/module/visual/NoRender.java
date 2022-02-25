package dev.zprestige.ruby.module.visual;

import dev.zprestige.ruby.eventbus.annotation.RegisterListener;
import dev.zprestige.ruby.events.PacketEvent;
import dev.zprestige.ruby.module.Module;
import dev.zprestige.ruby.settings.impl.Switch;
import net.minecraft.network.play.server.SPacketExplosion;
import net.minecraftforge.client.event.RenderBlockOverlayEvent;

public class NoRender extends Module {
    public static NoRender Instance;
    public final Switch hurtCam = Menu.Switch("Hurt Cam");
    public final Switch fire = Menu.Switch("Fire");
    public final Switch explosions = Menu.Switch("Explosions");
    public final Switch insideBlocks = Menu.Switch("Inside Blocks");
    public final Switch armor = Menu.Switch("Armor");

    public NoRender() {
        Instance = this;
    }

    @RegisterListener
    public void onRenderBlockOverlay(RenderBlockOverlayEvent event) {
        if (nullCheck() || !isEnabled())
            return;

        if (event.getOverlayType().equals(RenderBlockOverlayEvent.OverlayType.FIRE))
            event.setCanceled(fire.GetSwitch());

        if (event.getOverlayType().equals(RenderBlockOverlayEvent.OverlayType.BLOCK))
            event.setCanceled(insideBlocks.GetSwitch());
    }

    @RegisterListener
    public void onPacketReceive(PacketEvent.PacketReceiveEvent event) {
        if (nullCheck() || !isEnabled())
            return;

        if (event.getPacket() instanceof SPacketExplosion)
            event.setCancelled(explosions.GetSwitch());
    }

}
