package dev.zprestige.ruby.events;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;

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
