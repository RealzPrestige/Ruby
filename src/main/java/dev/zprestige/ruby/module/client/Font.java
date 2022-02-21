package dev.zprestige.ruby.module.client;

import dev.zprestige.ruby.Ruby;
import dev.zprestige.ruby.module.Category;
import dev.zprestige.ruby.module.Module;
import dev.zprestige.ruby.module.ModuleInfo;
import dev.zprestige.ruby.setting.impl.BooleanSetting;
import dev.zprestige.ruby.setting.impl.FloatSetting;
import dev.zprestige.ruby.setting.impl.ModeSetting;
import dev.zprestige.ruby.ui.font.RubyFont;

import java.util.Arrays;

@ModuleInfo(name = "Font", category = Category.Client, description = "Change font")
public class Font extends Module {
    public FloatSetting size = createSetting("Size", 17.0f, 10.0f, 30.0f);
    public BooleanSetting reloadFont = createSetting("Reload Font", false);

    @Override
    public void onTick() {
        if (reloadFont.getValue()){
            Ruby.rubyFont = new RubyFont("Font", size.getValue());
            reloadFont.setValue(false);
        }
    }
}
