package dev.zprestige.ruby.setting.impl;

import dev.zprestige.ruby.setting.Setting;

import java.util.function.Predicate;

public class DoubleSetting extends Setting<Double> {

    public double minimum;
    public double maximum;

    public DoubleSetting(String name, double value, double minimum, double maximum) {
        super(name, value);
        this.minimum = minimum;
        this.maximum = maximum;
    }

    public DoubleSetting(String name, double value, double minimum, double maximum, Predicate<Double> shown) {
        super(name, value, shown);
        this.minimum = minimum;
        this.maximum = maximum;
    }

    public Double getValue() {
        return value;
    }

    public double getMaximum() {
        return maximum;
    }

    public double getMinimum() {
        return minimum;
    }

    public DoubleSetting setParent(ParentSetting parentSetting){
        this.parentSetting = parentSetting;
        hasParentSetting = true;
        parentSetting.addChild(this);
        return this;
    }
}
