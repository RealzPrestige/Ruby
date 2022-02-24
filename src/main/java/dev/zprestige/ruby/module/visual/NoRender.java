package dev.zprestige.ruby.module.visual;

import dev.zprestige.ruby.eventbus.annotation.RegisterListener;
import dev.zprestige.ruby.events.PacketEvent;
import dev.zprestige.ruby.module.Module;
import dev.zprestige.ruby.setting.impl.BooleanSetting;
import net.minecraft.network.play.server.SPacketExplosion;
import net.minecraftforge.client.event.RenderBlockOverlayEvent;

public class NoRender extends Module {
    public static NoRender Instance;
    public final Switch hurtCam = Menu.Switch("Hurt Cam");
    public final Switch fire = Menu.Switch("Fire", false);
    public final Switch explosions = Menu.Switch("Explosions", false);
    public final Switch insideBlocks = Menu.Switch("Inside Blocks", false);
    public final Switch armor = Menu.Switch("Armor", false);

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
