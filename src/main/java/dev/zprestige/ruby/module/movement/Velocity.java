package dev.zprestige.ruby.module.movement;

import dev.zprestige.ruby.eventbus.annotation.RegisterListener;
import dev.zprestige.ruby.events.BlockPushEvent;
import dev.zprestige.ruby.events.EntityPushEvent;
import dev.zprestige.ruby.events.MoveEvent;
import dev.zprestige.ruby.events.PacketEvent;
import dev.zprestige.ruby.module.Module;
import dev.zprestige.ruby.settings.impl.Switch;
import net.minecraft.entity.MoverType;
import net.minecraft.network.play.server.SPacketEntityVelocity;
import net.minecraft.network.play.server.SPacketExplosion;

public class Velocity extends Module {
    public final Switch explosions = Menu.Switch("Explosions");
    public final Switch push = Menu.Switch("Push");
    public final Switch blocks = Menu.Switch("Blocks");
    public final Switch pistons = Menu.Switch("Pistons");

    @RegisterListener
    public void onPacketReceived(PacketEvent.PacketReceiveEvent event) {
        if (nullCheck() || !isEnabled() || !explosions.GetSwitch())
            return;
        event.setCancelled(event.getPacket() instanceof SPacketEntityVelocity ? ((SPacketEntityVelocity) event.getPacket()).getEntityID() == mc.player.entityId : event.getPacket() instanceof SPacketExplosion);
    }

    @RegisterListener
    public void onBlockPush(BlockPushEvent event) {
        if (nullCheck() || !isEnabled() || !blocks.GetSwitch())
            return;
        event.setCancelled(true);
    }

    @RegisterListener
    public void onEntityCollision(EntityPushEvent event) {
        if (nullCheck() || !isEnabled() || !push.GetSwitch())
            return;
        event.setCancelled(true);
    }

    @RegisterListener
    public void onMove(MoveEvent event) {
        if (nullCheck() || !isEnabled() || !pistons.GetSwitch() || !(event.getType().equals(MoverType.PISTON) || event.getType().equals(MoverType.SHULKER_BOX))) {
            return;
        }
        event.setCancelled(true);
    }
}
