package dev.zprestige.ruby.settings.impl;

import dev.zprestige.ruby.settings.Setting;

public class Key extends Setting {
    protected int key;

    public Key(String name, int key) {
        this.name = name;
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public void setValue(int value) {
        this.key = value;
    }

    public int GetKey() {
        return key;
    }

    public Key parent(Parent parent) {
        setHasParent(true);
        setParent(parent);
        return this;
    }
}
