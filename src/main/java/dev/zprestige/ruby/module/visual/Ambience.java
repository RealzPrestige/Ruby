package dev.zprestige.ruby.module.visual;

import dev.zprestige.ruby.module.Category;
import dev.zprestige.ruby.module.Module;
import dev.zprestige.ruby.module.ModuleInfo;
import dev.zprestige.ruby.setting.impl.ColorSetting;

import java.awt.*;

@ModuleInfo(name = "Ambience", category = Category.Visual, description = "Makes ambience.")
public class Ambience extends Module {
    public static Ambience Instance;

    public ColorSetting color = createSetting("Color", new Color(-1));

    public Ambience() {
        Instance = this;
    }
}
