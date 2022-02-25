package dev.zprestige.ruby.module.visual;

import dev.zprestige.ruby.module.Module;
import dev.zprestige.ruby.settings.impl.ColorBox;
import dev.zprestige.ruby.settings.impl.Slider;
import dev.zprestige.ruby.settings.impl.Switch;
import net.minecraft.util.ResourceLocation;

public class CrystalChams extends Module {
    public static CrystalChams Instance;
    public static ResourceLocation ENCHANTED_ITEM_GLINT_RES = new ResourceLocation("textures/misc/enchanted_item_glint.png");
    public final Switch glint = Menu.Switch("Glint");
    public final Switch glintDepth = Menu.Switch("Glint Depth");
    public final Slider glintSpeed = Menu.Slider("Glint Speed", 0.1f, 20.0f);
    public final Slider glintScale = Menu.Slider("Glint Scale", 0.1f, 10.0f);
    public final ColorBox glintColor = Menu.Color("Glint Color");
    public final Switch fill = Menu.Switch("Fill");
    public final Switch fillDepth = Menu.Switch("Fill Depth");
    public final Switch fillLighting = Menu.Switch("Fill Lighting");
    public final ColorBox fillColor = Menu.Color("Fill Color");
    public final Switch outline = Menu.Switch("Outline");
    public final Switch outlineDepth = Menu.Switch("Outline Depth");
    public final Slider outlineWidth = Menu.Slider("Outline Width", 0.1f, 10.0f);
    public final ColorBox outlineColor = Menu.Color("Outline Color");
    public final Slider scale = Menu.Slider("Scale", 0.1f, 10.0f);
    public final Slider rotationSpeed = Menu.Slider("Rotation Speed", 0.0f, 10.0f);
    public final Slider verticalSpeed = Menu.Slider("Vertical Speed", 0.0f, 10.0f);

    public CrystalChams() {
        Instance = this;
    }
}
