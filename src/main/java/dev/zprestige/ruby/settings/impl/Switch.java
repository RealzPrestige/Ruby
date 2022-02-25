package dev.zprestige.ruby.settings.impl;

import dev.zprestige.ruby.settings.Setting;

public class Switch extends Setting {
    protected boolean value;

    public Switch(String name) {
        this.name = name;
        this.value = false;
    }

    public String getName() {
        return name;
    }

    public void setValue(boolean value) {
        this.value = value;
    }

    public boolean GetSwitch() {
        return value;
    }

    public Switch parent(Parent parent) {
        setHasParent(true);
        setParent(parent);
        return this;
    }
}
