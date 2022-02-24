package dev.zprestige.ruby.module.client;

import dev.zprestige.ruby.Ruby;
import dev.zprestige.ruby.module.Module;
import dev.zprestige.ruby.newsettings.impl.Slider;
import dev.zprestige.ruby.newsettings.impl.Switch;
import dev.zprestige.ruby.setting.impl.BooleanSetting;
import dev.zprestige.ruby.setting.impl.FloatSetting;
import dev.zprestige.ruby.ui.font.RubyFont;

public class Font extends Module {
    public final Slider size = Menu.Slider("Size", 10.0f, 30.0f);
    public final Switch reloadFont = Menu.Switch("Reload Font");

    @Override
    public void onTick() {
        if (reloadFont.GetSwitch()){
            Ruby.rubyFont = new RubyFont("Font", size.GetSlider());
            reloadFont.setValue(false);
        }
    }
}
