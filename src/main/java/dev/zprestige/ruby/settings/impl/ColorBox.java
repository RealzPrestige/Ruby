package dev.zprestige.ruby.settings.impl;

import dev.zprestige.ruby.settings.Setting;

import java.awt.*;

public class ColorBox extends Setting {
    protected java.awt.Color value;

    public ColorBox(String name) {
        this.name = name;
        this.value = java.awt.Color.RED;
    }

    public String getName() {
        return name;
    }

    public void setValue(java.awt.Color value) {
        this.value = value;
    }

    public java.awt.Color GetColor() {
        return value;
    }

    public ColorBox parent(Parent parent) {
        setHasParent(true);
        setParent(parent);
        return this;
    }

    public ColorBox defaultValue(Color value){
        this.value = value;
        return this;
    }
}
