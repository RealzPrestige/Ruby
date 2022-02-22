package dev.zprestige.ruby.events;

import dev.zprestige.ruby.eventbus.event.Event;
import net.minecraft.entity.Entity;

public class EntityAddedEvent extends Event {
    public Entity entity;

    public EntityAddedEvent(Entity entity) {
        this.entity = entity;
    }
}
