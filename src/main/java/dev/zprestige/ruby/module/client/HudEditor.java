package dev.zprestige.ruby.module.client;

import dev.zprestige.ruby.module.Module;
import dev.zprestige.ruby.ui.hudeditor.HudEditorScreen;

public class HudEditor extends Module {

    @Override
    public void onEnable(){
        mc.displayGuiScreen(new HudEditorScreen());
    }

    @Override
    public void onTick(){
        if (!(mc.currentScreen instanceof HudEditorScreen)){
            disableModule();
        }
    }
}
