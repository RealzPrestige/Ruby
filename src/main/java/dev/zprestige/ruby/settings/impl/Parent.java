package dev.zprestige.ruby.settings.impl;

import dev.zprestige.ruby.settings.Setting;

import java.util.ArrayList;
import java.util.List;

public class Parent extends Setting {
    protected final List<Setting> children = new ArrayList<>();
    protected boolean value;

    public Parent(String name) {
        this.name = name;
        this.value = false;
    }

    public String getName() {
        return name;
    }

    public void setValue(boolean value) {
        this.value = value;
    }

    public boolean GetParent() {
        return value;
    }

    public List<Setting> getChildren() {
        return children;
    }
}
