package dev.zprestige.ruby.module.client;

import dev.zprestige.ruby.module.Module;
import dev.zprestige.ruby.settings.impl.Switch;

public class Enemies extends Module {
    public static Enemies Instance;
    public final Switch tabHighlight = Menu.Switch("Tab Highlight");
    public Switch tabPrefix = Menu.Switch("Tab Prefix");

    public Enemies() {
        Instance = this;
    }
}
