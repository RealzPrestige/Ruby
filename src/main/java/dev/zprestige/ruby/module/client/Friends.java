package dev.zprestige.ruby.module.client;

import dev.zprestige.ruby.module.Module;
import dev.zprestige.ruby.settings.impl.Switch;

public class Friends extends Module {
    public static Friends Instance;
    public final Switch tabHighlight = Menu.Switch("Tab Highlight");
    public final Switch tabPrefix = Menu.Switch("Tab Prefix");

    public Friends() {
        Instance = this;
    }
}
