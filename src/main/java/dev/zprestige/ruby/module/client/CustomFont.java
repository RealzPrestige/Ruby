package dev.zprestige.ruby.module.client;

import dev.zprestige.ruby.Ruby;
import dev.zprestige.ruby.module.Module;
import dev.zprestige.ruby.settings.impl.Slider;

public class CustomFont extends Module {
    public static CustomFont Instance;
    public final Slider fontSize = Menu.Slider("Font Size (%)", 20, 100);

    public CustomFont(){
        Instance = this;
        setEnabled(true);
    }

    @Override
    public void onTick(){
        final int slider = (int) fontSize.GetSlider();
        if (Ruby.fontManager.getSize() != slider){
            Ruby.fontManager.loadFont(slider);
        }
    }
}
