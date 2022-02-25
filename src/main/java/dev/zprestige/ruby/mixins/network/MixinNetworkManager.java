package dev.zprestige.ruby.mixins.network;

import dev.zprestige.ruby.Ruby;
import dev.zprestige.ruby.events.PacketEvent;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = NetworkManager.class)
public class MixinNetworkManager {

    @Inject(method = "sendPacket(Lnet/minecraft/network/Packet;)V", at = @At("HEAD"), cancellable = true)
    protected void onPacketSend(Packet<?> packet, CallbackInfo info) {
        PacketEvent.PacketSendEvent event = new PacketEvent.PacketSendEvent(packet);
        Ruby.eventBus.post(event);
        if (event.isCancelled())
            info.cancel();
    }

    @Inject(method = "channelRead0*", at = @At("HEAD"), cancellable = true)
    protected void onPacketReceive(ChannelHandlerContext chc, Packet<?> packet, CallbackInfo info) {
        PacketEvent.PacketReceiveEvent event = new PacketEvent.PacketReceiveEvent(packet);
        Ruby.eventBus.post(event);
        if (event.isCancelled())
            info.cancel();
    }
}