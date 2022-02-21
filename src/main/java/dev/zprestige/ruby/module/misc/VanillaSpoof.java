package dev.zprestige.ruby.module.misc;

import dev.zprestige.ruby.events.PacketEvent;
import dev.zprestige.ruby.module.Category;
import dev.zprestige.ruby.module.Module;
import dev.zprestige.ruby.module.ModuleInfo;
import io.netty.buffer.Unpooled;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.client.CPacketCustomPayload;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.internal.FMLProxyPacket;

@ModuleInfo(name = "VanillaSpoof", category = Category.Misc, description = "spoofs yo mods")
public class VanillaSpoof extends Module {

    @SubscribeEvent
    public void onPacketSend(PacketEvent.PacketSendEvent event) {
        if (event.getPacket() instanceof FMLProxyPacket && !mc.isSingleplayer()) {
            event.setCanceled(true);
        }
        if (event.getPacket() instanceof CPacketCustomPayload) {
            CPacketCustomPayload packet = (CPacketCustomPayload) event.getPacket();
            if (packet.getChannelName().equals("MC|Brand")) {
                packet.data = new PacketBuffer(Unpooled.buffer()).writeString("vanilla");
            }
        }
    }
}
