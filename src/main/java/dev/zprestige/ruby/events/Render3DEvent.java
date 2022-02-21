package dev.zprestige.ruby.events;

import net.minecraftforge.fml.common.eventhandler.Event;

public class Render3DEvent extends Event {
    public float partialTicks;

    public Render3DEvent(float partialTicks) {
        this.partialTicks = partialTicks;
    }
}
