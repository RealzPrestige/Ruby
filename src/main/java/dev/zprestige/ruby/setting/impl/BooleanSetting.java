package dev.zprestige.ruby.setting.impl;

import dev.zprestige.ruby.setting.Setting;

import java.util.function.Predicate;

public class BooleanSetting extends Setting<Boolean> {

    public final Switch(String name, Boolean value) {
        super(name, value);
    }

    public final Switch(String name, boolean value, Predicate<Boolean> shown) {
        super(name, value, shown);
    }

    public Boolean getValue() {
        return value;
    }

    public final Switch setParent(ParentSetting parentSetting) {
        this.parentSetting = parentSetting;
        hasParentSetting = true;
        parentSetting.addChild(this);
        return this;
    }
}
