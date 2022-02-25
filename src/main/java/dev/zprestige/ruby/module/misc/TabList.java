package dev.zprestige.ruby.module.misc;

import dev.zprestige.ruby.module.Module;
import dev.zprestige.ruby.settings.impl.ComboBox;
import dev.zprestige.ruby.settings.impl.Slider;
import dev.zprestige.ruby.settings.impl.Switch;

public class TabList extends Module {
    public static TabList Instance;
    public final ComboBox order = Menu.ComboBox("Order", new String[]{"Ping", "Alphabet", "Length"});
    public final Slider maxSize = Menu.Slider("Max Size", 1, 1000);
    public final Switch showPing = Menu.Switch("Show Ping");

    public TabList() {
        Instance = this;
    }
}
