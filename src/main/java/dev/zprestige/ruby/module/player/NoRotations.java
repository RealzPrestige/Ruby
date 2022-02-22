package dev.zprestige.ruby.module.player;

import dev.zprestige.ruby.eventbus.annotation.RegisterListener;
import dev.zprestige.ruby.events.PacketEvent;
import dev.zprestige.ruby.module.Module;
import net.minecraft.network.play.server.SPacketPlayerPosLook;

public class NoRotations extends Module {

    @RegisterListener
    public void onPacketReceive(PacketEvent.PacketReceiveEvent event){
        if (nullCheck() || !isEnabled() || !(event.getPacket() instanceof SPacketPlayerPosLook) || mc.currentScreen != null)
            return;
        ((SPacketPlayerPosLook) event.getPacket()).yaw = mc.player.rotationYaw;
        ((SPacketPlayerPosLook) event.getPacket()).pitch = mc.player.rotationPitch;
        ((SPacketPlayerPosLook) event.getPacket()).getFlags().remove(SPacketPlayerPosLook.EnumFlags.X_ROT);
        ((SPacketPlayerPosLook) event.getPacket()).getFlags().remove(SPacketPlayerPosLook.EnumFlags.Y_ROT);
    }
}
