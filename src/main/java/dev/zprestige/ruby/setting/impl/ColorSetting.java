package dev.zprestige.ruby.setting.impl;

import dev.zprestige.ruby.setting.Setting;

import java.awt.*;
import java.util.function.Predicate;

public class ColorSetting extends Setting<Color> {
    boolean isSelected = false;

    public ColorSetting(String name, Color value) {
        super(name, value);
    }

    public ColorSetting(String name, Color value, Predicate<Color> shown) {
        super(name, value, shown);
    }

    public void setColor(Color value) {
        this.value = value;
    }

    public boolean isOpen() {
        return isOpen;
    }

    public void setOpen(boolean open) {
        isOpen = open;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public ColorSetting setParent(ParentSetting parentSetting){
        this.parentSetting = parentSetting;
        hasParentSetting = true;
        parentSetting.addChild(this);
        return this;
    }
}
