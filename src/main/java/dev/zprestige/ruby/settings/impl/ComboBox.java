package dev.zprestige.ruby.settings.impl;

import dev.zprestige.ruby.settings.Setting;

public class ComboBox extends Setting {
    protected String value;
    protected String[] values;

    public ComboBox(String name, String[] values) {
        this.name = name;
        this.value = values[0];
        this.values = values;
    }

    public String getName() {
        return name;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String GetCombo() {
        return value;
    }

    public String[] getValues() {
        return values;
    }

    public ComboBox parent(Parent parent) {
        setHasParent(true);
        setParent(parent);
        return this;
    }
}
