package dev.zprestige.ruby.events;

import net.minecraft.entity.MoverType;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;

@Cancelable
public class MoveEvent extends Event {

    private MoverType type;
    public double motionX;
    public double motionY;
    public double motionZ;

    public MoveEvent(MoverType type, double x, double y, double z) {
        this.type = type;
        this.motionX = x;
        this.motionY = y;
        this.motionZ = z;
    }

    public MoverType getType() {
        return this.type;
    }

    public void setType(MoverType type) {
        this.type = type;
    }

    public double getMotionX() {
        return this.motionX;
    }

    public void setMotionX(double motionX) {
        this.motionX = motionX;
    }

    public double getMotionY() {
        return this.motionY;
    }

    public void setMotionY(double motionY) {
        this.motionY = motionY;
    }

    public double getMotionZ() {
        return this.motionZ;
    }

    public void setMotionZ(double motionZ) {
        this.motionZ = motionZ;
    }

    public void setMotion(double x, double y, double z){
        motionX = x;
        motionY = y;
        motionZ = z;
    }
}