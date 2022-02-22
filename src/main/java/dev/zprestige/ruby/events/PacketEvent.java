package dev.zprestige.ruby.events;

import dev.zprestige.ruby.eventbus.event.Event;
import dev.zprestige.ruby.eventbus.event.IsCancellable;
import net.minecraft.network.Packet;

public class PacketEvent extends Event {

    Packet<?> packet;

    public PacketEvent(Packet<?> packet) {
        this.packet = packet;
    }

    public Packet<?> getPacket() {
        return this.packet;
    }

    @IsCancellable
    public static class PacketReceiveEvent extends PacketEvent {
        public PacketReceiveEvent(Packet<?> packet) {
            super(packet);
        }
    }

    @IsCancellable
    public static class PacketSendEvent extends PacketEvent {
        public PacketSendEvent(Packet<?> packet) {
            super(packet);
        }
    }
}
