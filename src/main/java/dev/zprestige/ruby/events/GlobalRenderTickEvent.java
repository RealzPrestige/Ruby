package dev.zprestige.ruby.events;

import net.minecraftforge.fml.common.eventhandler.Event;

public class GlobalRenderTickEvent extends Event {

    public float partialTicks;

    public GlobalRenderTickEvent(float partialTicks) {
        this.partialTicks = partialTicks;
    }
}
