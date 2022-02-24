package dev.zprestige.ruby.setting;

import dev.zprestige.ruby.module.Module;
import dev.zprestige.ruby.setting.impl.ParentSetting;

import java.util.function.Predicate;

public class Setting<T> {
    public String name;
    public Module module;
    public T value;
    public Predicate<T> shown;
    public boolean isOpen = false;
    public final Parent parentSetting;
    public boolean hasParentSetting = false;

    public Setting(String name) {
        this.name = name;
    }

    public Setting(String name, T value) {
        this.name = name;
        this.value = value;
    }

    public Setting(String name, T value, Predicate<T> shown) {
        this.name = name;
        this.value = value;
        this.shown = shown;
    }

    public void setValue(T value) {
        this.value = value;
    }

    public T getValue() {
        return value;
    }

    public String getName() {
        return name;
    }

    public Module getModule() {
        return module;
    }

    public void setModule(Module module) {
        this.module = module;
    }

    public boolean isVisibleExcludingParent() {
        if (shown == null)
            return true;

        return shown.test(getValue());
    }

    public boolean isVisible() {
        if (hasParentSetting && !parentSetting.isOpened())
            return false;

        if (shown == null)
            return true;

        return shown.test(getValue());
    }
}
