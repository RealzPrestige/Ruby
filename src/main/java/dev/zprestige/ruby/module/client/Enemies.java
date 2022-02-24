package dev.zprestige.ruby.module.client;

import dev.zprestige.ruby.module.Module;
import dev.zprestige.ruby.newsettings.impl.Switch;
import dev.zprestige.ruby.setting.impl.BooleanSetting;
import dev.zprestige.ruby.setting.impl.StringSetting;

public class Enemies extends Module {
    public static Enemies Instance;
    public final Switch tabHighlight = Menu.Switch("Tab Highlight");
    public Switch tabPrefix = Menu.Switch("Tab Prefix");

    public Enemies() {
        Instance = this;
    }
}
