package dev.zprestige.ruby.mixins.network;

import dev.zprestige.ruby.Ruby;
import dev.zprestige.ruby.events.PacketEvent;
import io.netty.channel.ChannelHandlerContext;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = NetworkManager.class)
public class MixinNetworkManager {

    @Inject(method = "sendPacket(Lnet/minecraft/network/Packet;)V", at = @At("HEAD"), cancellable = true)
    public void onPacketSend(Packet<?> packet, CallbackInfo info) {
        PacketEvent.PacketSendEvent packetSendEvent = new PacketEvent.PacketSendEvent(packet);
        Ruby.RubyEventBus.post(packetSendEvent);
        if (packetSendEvent.isCanceled())
            info.cancel();
    }

    @Inject(method = "channelRead0", at = @At("HEAD"), cancellable = true)
    public void onPacketReceive(ChannelHandlerContext chc, Packet<?> packet, CallbackInfo info) {
        PacketEvent.PacketReceiveEvent packetReceiveEvent = new PacketEvent.PacketReceiveEvent(packet);
        Ruby.RubyEventBus.post(packetReceiveEvent);
        if (packetReceiveEvent.isCanceled())
            info.cancel();
    }
}