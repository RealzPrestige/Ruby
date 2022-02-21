package dev.zprestige.ruby.setting.impl;

import dev.zprestige.ruby.setting.Setting;

import java.util.function.Predicate;

public class KeySetting extends Setting<Integer> {

    public boolean isTyping = false;

    public KeySetting(String name, int value) {
        super(name, value);
    }

    public KeySetting(String name, int value, Predicate<Integer> shown) {
        super(name, value, shown);
    }

    public int getKey() {
        return value;
    }

    public void setBind(int bind){
        value = bind;
    }

    public KeySetting setParent(ParentSetting parentSetting){
        this.parentSetting = parentSetting;
        hasParentSetting = true;
        parentSetting.addChild(this);
        return this;
    }
}
