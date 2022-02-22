package dev.zprestige.ruby.module.client;

import dev.zprestige.ruby.module.Module;
import dev.zprestige.ruby.ui.config.ConfigGuiScreen;

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
