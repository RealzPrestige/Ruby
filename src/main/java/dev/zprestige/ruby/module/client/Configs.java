package dev.zprestige.ruby.module.client;

import dev.zprestige.ruby.module.Category;
import dev.zprestige.ruby.module.Module;
import dev.zprestige.ruby.module.ModuleInfo;
import dev.zprestige.ruby.ui.config.ConfigGuiScreen;

@ModuleInfo(name = "Configs", category = Category.Client, description = "configs stuff")
public class Configs extends Module {
    public static Configs Instance;

    public Configs() {
        Instance = this;
    }

    @Override
    public void onEnable() {
        mc.displayGuiScreen(new ConfigGuiScreen());
    }

    @Override
    public void onTick() {
        if (mc.currentScreen instanceof ConfigGuiScreen)
            disableModule();
    }
}
