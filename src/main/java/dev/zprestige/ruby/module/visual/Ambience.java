package dev.zprestige.ruby.module.visual;

import dev.zprestige.ruby.module.Module;
import dev.zprestige.ruby.settings.impl.ColorBox;

public class Ambience extends Module {
    public static Ambience Instance;

    public final ColorBox color = Menu.Color("Color");

    public Ambience() {
        Instance = this;
    }
}
