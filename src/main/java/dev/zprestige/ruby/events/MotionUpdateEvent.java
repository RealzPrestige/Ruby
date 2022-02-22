package dev.zprestige.ruby.events;

import dev.zprestige.ruby.eventbus.event.Event;
import dev.zprestige.ruby.eventbus.event.IsCancellable;

@IsCancellable
public class MotionUpdateEvent extends Event {
    public double x;
    public double y;
    public double z;
    public float rotationYaw;
    public float rotationPitch;
    public boolean onGround;
    public boolean modified;
    public int stage;

    public MotionUpdateEvent(int stage, double x, double y, double z, float rotationYaw, float rotationPitch, boolean onGround) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.rotationYaw = rotationYaw;
        this.rotationPitch = rotationPitch;
        this.onGround = onGround;
        this.stage = stage;
    }

    public MotionUpdateEvent(int stage, MotionUpdateEvent event) {
        this(stage, event.x, event.y, event.z, event.rotationYaw, event.rotationPitch, event.onGround);
    }

    public boolean isModified() {
        return this.modified;
    }

    public double getX() {
        return this.x;
    }

    public void setX(double x) {
        this.modified = true;
        this.x = x;
    }

    public double getY() {
        return this.y;
    }

    public void setY(double y) {
        this.modified = true;
        this.y = y;
    }

    public double getZ() {
        return this.z;
    }

    public void setZ(double z) {
        this.modified = true;
        this.z = z;
    }

    public float getYaw() {
        return this.rotationYaw;
    }

    public void setYaw(float rotationYaw) {
        this.modified = true;
        this.rotationYaw = rotationYaw;
    }

    public float getPitch() {
        return this.rotationPitch;
    }

    public void setPitch(float rotationPitch) {
        this.modified = true;
        this.rotationPitch = rotationPitch;
    }

    public boolean isOnGround() {
        return this.onGround;
    }

    public void setOnGround(boolean onGround) {
        this.modified = true;
        this.onGround = onGround;
    }
}