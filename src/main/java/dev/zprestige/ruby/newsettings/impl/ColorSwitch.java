package dev.zprestige.ruby.newsettings.impl;

import dev.zprestige.ruby.newsettings.Setting;

import java.awt.Color;

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

    public boolean GetBool() {
        return booleanValue;
    }

    public Color GetColor() {
        return colorValue;
    }

    public ColorSwitch parent(Parent parent){
        setHasParent(true);
        setParent(parent);
        return this;
    }
}
