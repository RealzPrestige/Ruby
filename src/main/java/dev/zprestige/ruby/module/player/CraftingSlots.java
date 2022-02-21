package dev.zprestige.ruby.module.player;

import dev.zprestige.ruby.events.CloseInventoryEvent;
import dev.zprestige.ruby.events.PacketEvent;
import dev.zprestige.ruby.module.Category;
import dev.zprestige.ruby.module.Module;
import dev.zprestige.ruby.module.ModuleInfo;
import net.minecraft.network.play.client.CPacketCloseWindow;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@ModuleInfo(name = "CraftingSlots", category = Category.Player, description = "Tweak crafting slots")
public class CraftingSlots extends Module {

    @SubscribeEvent
    public void onPacketSend(PacketEvent.PacketSendEvent event) {
        if (nullCheck() || !isEnabled() || !(event.getPacket() instanceof CPacketCloseWindow))
            return;
        event.setCanceled(((CPacketCloseWindow) event.getPacket()).windowId == 0);
    }

    @SubscribeEvent
    public void onInventoryClose(CloseInventoryEvent event) {
        if (nullCheck() || !isEnabled())
            return;
        event.setCanceled(true);
    }
}
