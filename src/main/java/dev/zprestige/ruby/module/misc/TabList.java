package dev.zprestige.ruby.module.misc;

import dev.zprestige.ruby.module.Module;
import dev.zprestige.ruby.newsettings.impl.Slider;
import dev.zprestige.ruby.newsettings.impl.Switch;
import dev.zprestige.ruby.setting.impl.ModeSetting;

import java.util.Arrays;

public class TabList extends Module {
    public static TabList Instance;
    public final ComboBox order = Menu.Switch("Order", "Normal", Arrays.asList("Ping", "Alphabet", "Length"));
    public final Slider maxSize = Menu.Slider("Max Size", 1, 1000);
    public final Switch showPing = Menu.Switch("Show Ping");

    public TabList() {
        Instance = this;
    }
}
