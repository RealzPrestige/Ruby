package dev.zprestige.ruby.events;

import net.minecraftforge.fml.common.eventhandler.Event;

public class ChorusEvent extends Event {
    public double x;
    public double y;
    public double z;

    public ChorusEvent(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }
}
