package dev.zprestige.ruby.events;

import dev.zprestige.ruby.eventbus.event.Event;

public class Render3DEvent extends Event {
    public float partialTicks;

    public Render3DEvent(float partialTicks) {
        this.partialTicks = partialTicks;
    }
}
