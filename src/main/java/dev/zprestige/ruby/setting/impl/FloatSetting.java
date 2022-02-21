package dev.zprestige.ruby.setting.impl;

import dev.zprestige.ruby.setting.Setting;

import java.util.function.Predicate;

public class FloatSetting extends Setting<Float> {

    float minimum;
    float maximum;

    public FloatSetting(String name, float value, float minimum, float maximum) {
        super(name, value);
        this.minimum = minimum;
        this.maximum = maximum;
    }

    public FloatSetting(String name, float value, float minimum, float maximum, Predicate<Float> shown) {
        super(name, value, shown);
        this.minimum = minimum;
        this.maximum = maximum;
    }

    public Float getValue() {
        return value;
    }

    public float getMaximum() {
        return maximum;
    }

    public float getMinimum() {
        return minimum;
    }

    public FloatSetting setParent(ParentSetting parentSetting){
        this.parentSetting = parentSetting;
        hasParentSetting = true;
        parentSetting.addChild(this);
        return this;
    }
}
