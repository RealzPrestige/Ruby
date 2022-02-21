package dev.zprestige.ruby.module.player;

import dev.zprestige.ruby.events.PacketEvent;
import dev.zprestige.ruby.module.Category;
import dev.zprestige.ruby.module.Module;
import dev.zprestige.ruby.module.ModuleInfo;
import net.minecraft.network.play.server.SPacketPlayerPosLook;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@ModuleInfo(name = "NoRotations", category = Category.Player, description = "Server no longer force rotation on u")
public class NoRotations extends Module {

    @SubscribeEvent
    public void onPacketReceive(PacketEvent.PacketReceiveEvent event){
        if (nullCheck() || !isEnabled() || !(event.getPacket() instanceof SPacketPlayerPosLook) || mc.currentScreen != null)
            return;
        ((SPacketPlayerPosLook) event.getPacket()).yaw = mc.player.rotationYaw;
        ((SPacketPlayerPosLook) event.getPacket()).pitch = mc.player.rotationPitch;
        ((SPacketPlayerPosLook) event.getPacket()).getFlags().remove(SPacketPlayerPosLook.EnumFlags.X_ROT);
        ((SPacketPlayerPosLook) event.getPacket()).getFlags().remove(SPacketPlayerPosLook.EnumFlags.Y_ROT);
    }
}
