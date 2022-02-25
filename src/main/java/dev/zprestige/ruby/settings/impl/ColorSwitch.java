package dev.zprestige.ruby.settings.impl;

import dev.zprestige.ruby.settings.Setting;

import java.awt.*;

public class ColorSwitch extends Setting {
    protected boolean booleanValue;
    protected Color colorValue;

    public ColorSwitch(String name) {
        this.name = name;
        this.booleanValue = false;
        this.colorValue = Color.RED;
    }

    public String getName() {
        return name;
    }

    public void setBool(boolean value) {
        this.booleanValue = value;
    }

    public void setColor(Color value) {
        this.colorValue = value;
    }

    public boolean GetSwitch() {
        return booleanValue;
    }

    public Color GetColor() {
        return colorValue;
    }

    public void setSwitchValue(boolean booleanValue) {
        this.booleanValue = booleanValue;
    }

    public void setColorValue(Color colorValue) {
        this.colorValue = colorValue;
    }

    public ColorSwitch parent(Parent parent) {
        setHasParent(true);
        setParent(parent);
        return this;
    }
}
