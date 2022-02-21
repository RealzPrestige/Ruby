package dev.zprestige.ruby.setting.impl;

import dev.zprestige.ruby.setting.Setting;

import java.util.List;
import java.util.function.Predicate;

public final class ModeSetting extends Setting<String> {
    public List<String> modes;

    public ModeSetting(String name, String value, List<String> modeList) {
        super(name, value);
        this.modes = modeList;
    }

    public ModeSetting(String name, String value, List<String> modeList, Predicate<String> shown) {
        super(name, value, shown);
        this.modes = modeList;
    }

    public List<String> getModes() {
        return this.modes;
    }

    public boolean isOpen() {
        return isOpen;
    }

    public void setOpen(boolean open) {
        isOpen = open;
    }

    public void setValue(String value) {
        this.value = (modes.contains(value) ? value : this.value);
    }

    public ModeSetting setParent(ParentSetting parentSetting){
        this.parentSetting = parentSetting;
        hasParentSetting = true;
        parentSetting.addChild(this);
        return this;
    }
}
