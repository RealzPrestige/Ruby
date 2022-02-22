package dev.zprestige.ruby.events;

import dev.zprestige.ruby.eventbus.event.Event;
import net.minecraft.entity.player.EntityPlayer;

public class PlayerChangeEvent extends Event {
    public EntityPlayer entityPlayer;

    public PlayerChangeEvent(EntityPlayer entityPlayer) {
        this.entityPlayer = entityPlayer;
    }

    public static class TotemPop extends PlayerChangeEvent {
        public TotemPop(EntityPlayer player) {
            super(player);
        }
    }

    public static class Death extends PlayerChangeEvent {
        public Death(EntityPlayer player) {
            super(player);
        }
    }
}
