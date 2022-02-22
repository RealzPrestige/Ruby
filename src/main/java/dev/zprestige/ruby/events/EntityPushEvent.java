package dev.zprestige.ruby.events;

import dev.zprestige.ruby.eventbus.event.Event;
import dev.zprestige.ruby.eventbus.event.IsCancellable;
import net.minecraftforge.fml.common.eventhandler.Cancelable;

@IsCancellable
public class EntityPushEvent extends Event {
}
