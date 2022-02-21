package dev.zprestige.ruby.module.client;

import dev.zprestige.ruby.module.Category;
import dev.zprestige.ruby.module.Module;
import dev.zprestige.ruby.module.ModuleInfo;
import dev.zprestige.ruby.setting.impl.BooleanSetting;
import dev.zprestige.ruby.setting.impl.StringSetting;

@ModuleInfo(name = "Enemies", category = Category.Client, description = "Stuff with enemies.")
public class Enemies extends Module {
    public static Enemies Instance;
    public BooleanSetting tabHighlight = createSetting("Tab Highlight", false);
    public BooleanSetting tabPrefix = createSetting("Tab Prefix", false, v -> tabHighlight.getValue());
    public StringSetting tabPrefixPrefix = createSetting("Tab Prefix Prefix", "[Enemy]", v -> tabPrefix.getValue() && tabHighlight.getValue());

    public Enemies() {
        Instance = this;
    }
}
