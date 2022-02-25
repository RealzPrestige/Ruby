package dev.zprestige.ruby.module;


import dev.zprestige.ruby.settings.Setting;
import dev.zprestige.ruby.settings.impl.*;

public class Menu {
    protected Module module;

    public Menu(Module module) {
        this.module = module;
    }

    protected void addSetting(Setting setting) {
        module.newSettings.add(setting);
    }

    protected void setModule(Setting setting) {
        setting.setModule(module);
    }

    public ColorBox Color(String name) {
        ColorBox setting = new ColorBox(name);
        setModule(setting);
        addSetting(setting);
        return setting;
    }

    public ColorSwitch ColorSwitch(String name) {
        ColorSwitch setting = new ColorSwitch(name);
        setModule(setting);
        addSetting(setting);
        return setting;
    }

    public ComboBox ComboBox(String name, String[] values) {
        ComboBox setting = new ComboBox(name, values);
        setModule(setting);
        addSetting(setting);
        return setting;
    }

    public Key Key(String name, int key) {
        Key setting = new Key(name, key);
        setModule(setting);
        addSetting(setting);
        return setting;
    }

    public Parent Parent(String name) {
        Parent setting = new Parent(name);
        setModule(setting);
        addSetting(setting);
        return setting;
    }

    public Slider Slider(String name, int min, int max) {
        Slider setting = new Slider(name, min, max);
        setModule(setting);
        addSetting(setting);
        return setting;
    }

    public Slider Slider(String name, double min, double max) {
        Slider setting = new Slider(name, min, max);
        setModule(setting);
        addSetting(setting);
        return setting;
    }

    public Slider Slider(String name, float min, float max) {
        Slider setting = new Slider(name, min, max);
        setModule(setting);
        addSetting(setting);
        return setting;
    }

    public Switch Switch(String name) {
        Switch setting = new Switch(name);
        setModule(setting);
        addSetting(setting);
        return setting;
    }
}