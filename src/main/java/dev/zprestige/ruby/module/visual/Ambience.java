package dev.zprestige.ruby.module.visual;

import dev.zprestige.ruby.module.Module;
import dev.zprestige.ruby.setting.impl.ColorSetting;

import java.awt.*;

public class Ambience extends Module {
    public static Ambience Instance;

    public ColorSetting color = createSetting("Color", new Color(-1));

    public Ambience() {
        Instance = this;
    }
}
