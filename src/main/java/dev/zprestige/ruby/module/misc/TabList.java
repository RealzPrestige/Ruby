package dev.zprestige.ruby.module.misc;

import dev.zprestige.ruby.module.Module;
import dev.zprestige.ruby.setting.impl.BooleanSetting;
import dev.zprestige.ruby.setting.impl.IntegerSetting;
import dev.zprestige.ruby.setting.impl.ModeSetting;

import java.util.Arrays;

public class TabList extends Module {
    public static TabList Instance;
    public ModeSetting order = createSetting("Order", "Normal", Arrays.asList("Ping", "Alphabet", "Length"));
    public IntegerSetting maxSize = createSetting("Max Size", 300, 1, 1000);
    public BooleanSetting showPing = createSetting("Show Ping", false);

    public TabList() {
        Instance = this;
    }
}
