package dev.zprestige.ruby.module.player;

import dev.zprestige.ruby.eventbus.annotation.RegisterListener;
import dev.zprestige.ruby.events.CloseInventoryEvent;
import dev.zprestige.ruby.events.PacketEvent;
import dev.zprestige.ruby.module.Module;
import net.minecraft.network.play.client.CPacketCloseWindow;

public class CraftingSlots extends Module {

    @RegisterListener
    public void onPacketSend(PacketEvent.PacketSendEvent event) {
        if (nullCheck() || !isEnabled() || !(event.getPacket() instanceof CPacketCloseWindow))
            return;
        event.setCancelled(((CPacketCloseWindow) event.getPacket()).windowId == 0);
    }

    @RegisterListener
    public void onInventoryClose(CloseInventoryEvent event) {
        if (nullCheck() || !isEnabled())
            return;
        event.setCancelled(true);
    }
}
