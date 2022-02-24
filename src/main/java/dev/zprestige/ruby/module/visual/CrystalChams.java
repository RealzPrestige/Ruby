package dev.zprestige.ruby.module.visual;

import dev.zprestige.ruby.module.Module;
import dev.zprestige.ruby.setting.impl.BooleanSetting;
import dev.zprestige.ruby.setting.impl.ColorSetting;
import dev.zprestige.ruby.setting.impl.FloatSetting;
import dev.zprestige.ruby.setting.impl.ModeSetting;
import net.minecraft.util.ResourceLocation;

import java.awt.*;
import java.util.Arrays;
public class CrystalChams extends Module {
    public static CrystalChams Instance;
    public final Switch glint = Menu.Switch("Glint");
    public final Switch glintDepth = Menu.Switch("Glint Depth", false);
    public final Slider glintSpeed = Menu.Switch("Glint Speed", 5.0f, 0.1f, 20.0f);
    public final Slider glintScale = Menu.Switch("Glint Scale", 1.0f, 0.1f, 10.0f);
    public final ColorBox glintColor = Menu.Switch("Glint Color", new Color(-1));
    public final Switch fill = Menu.Switch("Fill", false);
    public final Switch fillDepth = Menu.Switch("Fill Depth", false);
    public final Switch fillLighting = Menu.Switch("Fill Lighting", false);
    public final ColorBox fillColor = Menu.Switch("Fill Color", new Color(-1));
    public final Switch outline = Menu.Switch("Outline", false);
    public final Switch outlineDepth = Menu.Switch("Outline Depth", false);
    public final Slider outlineWidth = Menu.Switch("Outline Width", 1.0f, 0.1f, 10.0f);
    public final ColorBox outlineColor = Menu.Switch("Outline Color", new Color(-1));
    public final Slider scale = Menu.Switch("Scale", 1.0f, 0.1f, 10.0f);
    public final Slider rotationSpeed = Menu.Switch("Rotation Speed", 1.0f, 0.0f, 10.0f);
    public final Slider verticalSpeed = Menu.Switch("Vertical Speed", 1.0f, 0.0f, 10.0f);
    public static ResourceLocation ENCHANTED_ITEM_GLINT_RES = new ResourceLocation("textures/misc/enchanted_item_glint.png");
    public static ResourceLocation LIGHTNING_TEXTURE = new ResourceLocation("textures/glint.png");

    public CrystalChams(){
        Instance = this;
    }
}
