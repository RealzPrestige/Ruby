package dev.zprestige.ruby.module.player;

import dev.zprestige.ruby.eventbus.annotation.RegisterListener;
import dev.zprestige.ruby.events.PacketEvent;
import dev.zprestige.ruby.module.Module;
import dev.zprestige.ruby.setting.impl.BooleanSetting;
import dev.zprestige.ruby.setting.impl.FloatSetting;
import dev.zprestige.ruby.util.EntityUtil;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketUseEntity;

public class Criticals extends Module {

    public final Slider offset = Menu.Switch("Offset", 0.1f, 0.1f, 1.0f);
    public final Switch allowMoving = Menu.Switch("Allow Moving");
    public final Switch onGroundOnly = Menu.Switch("On Ground Only");

    @RegisterListener
    public void onPacketSend(PacketEvent.PacketSendEvent event) {
        if (nullCheck() || !isEnabled() || !(event.getPacket() instanceof CPacketUseEntity) || !(((CPacketUseEntity) event.getPacket()).getAction() == CPacketUseEntity.Action.ATTACK) || !(((CPacketUseEntity) event.getPacket()).getEntityFromWorld(mc.world) instanceof EntityLivingBase) || !mc.player.onGround || mc.player.isInWater() || mc.player.isInLava())
            return;
        if ((!allowMoving.getValue() && EntityUtil.isMoving()) || (onGroundOnly.getValue() && !mc.player.onGround))
            return;
        mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + offset.getValue(), mc.player.posZ, false));
        mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY, mc.player.posZ, false));
    }
}
