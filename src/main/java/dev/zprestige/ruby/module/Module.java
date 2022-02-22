package dev.zprestige.ruby.module;

import dev.zprestige.ruby.Ruby;
import dev.zprestige.ruby.events.ModuleToggleEvent;
import dev.zprestige.ruby.events.Render3DEvent;
import dev.zprestige.ruby.setting.Setting;
import dev.zprestige.ruby.setting.impl.*;
import dev.zprestige.ruby.util.MessageUtil;
import net.minecraft.client.Minecraft;
import org.lwjgl.input.Keyboard;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class Module {
    public boolean open, drawn = true;
    protected String name;
    protected Category category;
    protected final List<Setting<?>> settingList = new ArrayList<>();
    protected final  KeySetting keybind = createSetting("Keybind", Keyboard.KEY_NONE);
    protected final BooleanSetting enabled = createSetting("Enabled", false);
    protected final Minecraft mc = Minecraft.getMinecraft();
    public int scrollY;

    public void onEnable() {
    }

    public void onDisable() {
    }

    public void onTick() {
    }

    public void onOverlayTick() {
    }


    public void onGlobalRenderTick() {
    }

    public void onGlobalRenderTick(Render3DEvent event) {
    }

    public void enableModule() {
        setEnabled(true);
        onEnable();
        Ruby.eventBus.post(new ModuleToggleEvent.Enable(this));
        Ruby.eventBus.register(this);
    }

    public void disableModule() {
        setEnabled(false);
        onDisable();
        Ruby.eventBus.post(new ModuleToggleEvent.Disable(this));
        Ruby.eventBus.unregister(this);
    }

    public void disableModule(String message) {
        disableModule();
        MessageUtil.sendMessage(message);
    }


    public boolean isOpen() {
        return open;
    }

    public void setOpen(boolean open) {
        this.open = open;
    }

    public boolean isEnabled() {
        return enabled.getValue();
    }

    public void setEnabled(boolean enabled) {
        this.enabled.setValue(enabled);
    }

    public String getName() {
        return name;
    }

    public Category getCategory() {
        return category;
    }

    public Integer getKeybind() {
        return keybind.getKey();
    }

    public void setKeybind(Integer keybind) {
        this.keybind.setValue(keybind);
    }

    public boolean nullCheck() {
        return mc.world == null || mc.player == null;
    }

    public Module withSuper(String name, Category category){
        this.name = name;
        this.category = category;
        return this;
    }

    public BooleanSetting createSetting(String name, boolean value) {
        BooleanSetting setting = new BooleanSetting(name, value);
        setting.setModule(this);
        settingList.add(setting);
        return setting;
    }

    public BooleanSetting createSetting(String name, boolean value, Predicate<Boolean> shown) {
        BooleanSetting setting = new BooleanSetting(name, value, shown);
        setting.setModule(this);
        settingList.add(setting);
        return setting;
    }

    public ColorSetting createSetting(String name, Color value) {
        ColorSetting setting = new ColorSetting(name, value);
        setting.setModule(this);
        settingList.add(setting);
        return setting;
    }

    public ColorSetting createSetting(String name, Color value, Predicate<Color> shown) {
        ColorSetting setting = new ColorSetting(name, value, shown);
        setting.setModule(this);
        settingList.add(setting);
        return setting;
    }

    public DoubleSetting createSetting(String name, double value, double minimum, double maximum) {
        DoubleSetting setting = new DoubleSetting(name, value, minimum, maximum);
        setting.setModule(this);
        settingList.add(setting);
        return setting;
    }

    public DoubleSetting createSetting(String name, double value, double minimum, double maximum, Predicate<Double> shown) {
        DoubleSetting setting = new DoubleSetting(name, value, minimum, maximum, shown);
        setting.setModule(this);
        settingList.add(setting);
        return setting;
    }

    public ModeSetting createSetting(String name, String value, java.util.List<String> modeList) {
        ModeSetting setting = new ModeSetting(name, value, modeList);
        setting.setModule(this);
        settingList.add(setting);
        return setting;
    }

    public ModeSetting createSetting(String name, String value, List<String> modeList, Predicate<String> shown) {
        ModeSetting setting = new ModeSetting(name, value, modeList, shown);
        setting.setModule(this);
        settingList.add(setting);
        return setting;
    }

    public FloatSetting createSetting(String name, float value, float minimum, float maximum) {
        FloatSetting setting = new FloatSetting(name, value, minimum, maximum);
        setting.setModule(this);
        settingList.add(setting);
        return setting;
    }

    public FloatSetting createSetting(String name, float value, float minimum, float maximum, Predicate<Float> shown) {
        FloatSetting setting = new FloatSetting(name, value, minimum, maximum, shown);
        setting.setModule(this);
        settingList.add(setting);
        return setting;
    }

    public IntegerSetting createSetting(String name, int value, int minimum, int maximum) {
        IntegerSetting setting = new IntegerSetting(name, value, minimum, maximum);
        setting.setModule(this);
        settingList.add(setting);
        return setting;
    }

    public IntegerSetting createSetting(String name, int value, int minimum, int maximum, Predicate<Integer> shown) {
        IntegerSetting setting = new IntegerSetting(name, value, minimum, maximum, shown);
        setting.setModule(this);
        settingList.add(setting);
        return setting;
    }

    public KeySetting createSetting(String name, int value) {
        KeySetting setting = new KeySetting(name, value);
        setting.setModule(this);
        settingList.add(setting);
        return setting;
    }

    public KeySetting createSetting(String name, int value, Predicate<Integer> shown) {
        KeySetting setting = new KeySetting(name, value, shown);
        setting.setModule(this);
        settingList.add(setting);
        return setting;
    }

    public StringSetting createSetting(String name, String value) {
        StringSetting setting = new StringSetting(name, value);
        setting.setModule(this);
        settingList.add(setting);
        return setting;
    }

    public StringSetting createSetting(String name, String value, Predicate<String> shown) {
        StringSetting setting = new StringSetting(name, value, shown);
        setting.setModule(this);
        settingList.add(setting);
        return setting;
    }

    public ParentSetting createSetting(String name) {
        ParentSetting setting = new ParentSetting(name);
        setting.setModule(this);
        settingList.add(setting);
        return setting;
    }

    public ParentSetting createSetting(String name, Predicate<Boolean> shown) {
        ParentSetting setting = new ParentSetting(name, shown);
        setting.setModule(this);
        settingList.add(setting);
        return setting;
    }

    public List<Setting<?>> getSettingList() {
        return settingList;
    }
}
