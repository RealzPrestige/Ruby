package dev.zprestige.ruby.setting.impl;

import dev.zprestige.ruby.setting.Setting;

import java.util.function.Predicate;

public class BooleanSetting extends Setting<Boolean> {

    public BooleanSetting(String name, Boolean value) {
        super(name, value);
    }

    public BooleanSetting(String name, boolean value, Predicate<Boolean> shown) {
        super(name, value, shown);
    }

    public Boolean getValue() {
        return value;
    }

    public BooleanSetting setParent(ParentSetting parentSetting) {
        this.parentSetting = parentSetting;
        hasParentSetting = true;
        parentSetting.addChild(this);
        return this;
    }
}
