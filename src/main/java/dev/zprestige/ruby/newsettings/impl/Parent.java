package dev.zprestige.ruby.newsettings.impl;

import dev.zprestige.ruby.newsettings.Setting;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Parent extends Setting {
    protected final List<Setting> settings = new ArrayList<>();
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
        return settings;
    }
}
