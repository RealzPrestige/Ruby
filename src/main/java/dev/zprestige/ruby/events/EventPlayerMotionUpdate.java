package dev.zprestige.ruby.events;

import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraftforge.fml.common.eventhandler.Event;

import java.util.function.Consumer;

public class EventPlayerMotionUpdate extends Event {
    protected float _yaw;
    protected float _pitch;
    protected double x;
    protected double y;
    protected double z;
    protected boolean onGround;
    private Consumer<EntityPlayerSP> _funcToCall;
    private boolean _isForceCancelled;

    public EventPlayerMotionUpdate(final double posX, final double posY, final double posZ, final boolean pOnGround) {
        this._funcToCall = null;
        this.x = posX;
        this.y = posY;
        this.z = posZ;
        this.onGround = pOnGround;
    }

    public Consumer<EntityPlayerSP> getFunc() {
        return this._funcToCall;
    }

    public void setFunct(final Consumer<EntityPlayerSP> post) {
        this._funcToCall = post;
    }

    public float getYaw() {
        return this._yaw;
    }

    public void setYaw(final float yaw) {
        this._yaw = yaw;
    }

    public void setYaw(final double yaw) {
        this._yaw = (float) yaw;
    }

    public float getPitch() {
        return this._pitch;
    }

    public void setPitch(final float pitch) {
        this._pitch = pitch;
    }

    public void setPitch(final double pitch) {
        this._pitch = (float) pitch;
    }

    public void forceCancel() {
        this._isForceCancelled = true;
    }

    public boolean isForceCancelled() {
        return this._isForceCancelled;
    }

    public double getX() {
        return this.x;
    }

    public void setX(final double posX) {
        this.x = posX;
    }

    public double getY() {
        return this.y;
    }

    public void setY(final double d) {
        this.y = d;
    }

    public double getZ() {
        return this.z;
    }

    public void setZ(final double posZ) {
        this.z = posZ;
    }

    public boolean getOnGround() {
        return this.onGround;
    }

    public void setOnGround(final boolean b) {
        this.onGround = b;
    }
}