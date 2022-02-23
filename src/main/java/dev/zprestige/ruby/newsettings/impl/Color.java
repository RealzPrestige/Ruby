package dev.zprestige.ruby.newsettings.impl;

import dev.zprestige.ruby.newsettings.Setting;

public class Color extends Setting {
    protected java.awt.Color value;

    public Color(String name) {
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

    public Color parent(Parent parent){
        setHasParent(true);
        setParent(parent);
        return this;
    }
}
