package dev.zprestige.ruby.events;

import dev.zprestige.ruby.eventbus.event.Event;
import dev.zprestige.ruby.eventbus.event.IsCancellable;
import net.minecraftforge.fml.common.eventhandler.Cancelable;

@IsCancellable
public class TurnEvent extends Event {
    private final float yaw;
    private final float pitch;

    public TurnEvent(float yaw, float pitch) {
        this.yaw = yaw;
        this.pitch = pitch;
    }

    public float getYaw() {
        return yaw;
    }

    public float getPitch() {
        return pitch;
    }
}
