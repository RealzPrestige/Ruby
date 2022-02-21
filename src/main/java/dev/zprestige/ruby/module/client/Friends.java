package dev.zprestige.ruby.module.client;

import dev.zprestige.ruby.module.Category;
import dev.zprestige.ruby.module.Module;
import dev.zprestige.ruby.module.ModuleInfo;
import dev.zprestige.ruby.setting.impl.BooleanSetting;
import dev.zprestige.ruby.setting.impl.StringSetting;

@ModuleInfo(name = "Friends", category = Category.Client, description = "Stuff with friends.")
public class Friends extends Module {
    public static Friends Instance;
    public BooleanSetting tabHighlight = createSetting("Tab Highlight", false);
    public BooleanSetting tabPrefix = createSetting("Tab Prefix", false, v -> tabHighlight.getValue());
    public StringSetting tabPrefixPrefix = createSetting("Tab Prefix Prefix", "[Friend]", v -> tabPrefix.getValue() && tabHighlight.getValue());

    public Friends() {
        Instance = this;
    }
}
