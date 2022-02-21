package dev.zprestige.ruby.setting.impl;

import dev.zprestige.ruby.setting.Setting;

import java.util.function.Predicate;

public class StringSetting extends Setting<String> {
    boolean isOpen = false;

    public StringSetting(String name, String value) {
        super(name, value);
    }

    public StringSetting(String name, String value, Predicate<String> shown) {
        super(name, value, shown);
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public boolean isOpen() {
        return isOpen;
    }

    public void setOpen(boolean open) {
        isOpen = open;
    }

    public StringSetting setParent(ParentSetting parentSetting) {
        this.parentSetting = parentSetting;
        hasParentSetting = true;
        parentSetting.addChild(this);
        return this;
    }
}
