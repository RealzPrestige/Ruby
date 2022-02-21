package dev.zprestige.ruby.setting.impl;

import dev.zprestige.ruby.setting.Setting;

import java.util.function.Predicate;

public class IntegerSetting extends Setting<Integer> {

    int minimum;
    int maximum;
    boolean isOpen = false;

    public IntegerSetting(String name, int value, int minimum, int maximum) {
        super(name, value);
        this.minimum = minimum;
        this.maximum = maximum;
    }

    public IntegerSetting(String name, int value, int minimum, int maximum, Predicate<Integer> shown) {
        super(name, value, shown);
        this.minimum = minimum;
        this.maximum = maximum;
    }

    public Integer getValue() {
        return value;
    }

    public int getMaximum() {
        return maximum;
    }

    public int getMinimum() {
        return minimum;
    }

    public boolean isOpen() {
        return isOpen;
    }

    public void setOpen(boolean open) {
        isOpen = open;
    }

    public IntegerSetting setParent(ParentSetting parentSetting) {
        this.parentSetting = parentSetting;
        hasParentSetting = true;
        parentSetting.addChild(this);
        return this;
    }
}
