package dev.zprestige.ruby.module.visual;

import dev.zprestige.ruby.module.Category;
import dev.zprestige.ruby.module.Module;
import dev.zprestige.ruby.module.ModuleInfo;
import dev.zprestige.ruby.setting.impl.BooleanSetting;
import dev.zprestige.ruby.setting.impl.ColorSetting;
import dev.zprestige.ruby.setting.impl.FloatSetting;
import dev.zprestige.ruby.setting.impl.ModeSetting;
import net.minecraft.util.ResourceLocation;

import java.awt.*;
import java.util.Arrays;
@ModuleInfo(name = "CrystalChams" , category = Category.Visual, description = "Changes the way crystal looks.")
public class CrystalChams extends Module {
    public static CrystalChams Instance;
    public ModeSetting glintMode = createSetting("Glint Mode", "None", Arrays.asList("None", "Vanilla", "Custom"));
    public BooleanSetting glintDepth = createSetting("Glint Depth", false);
    public FloatSetting glintSpeed = createSetting("Glint Speed", 5.0f, 0.1f, 20.0f);
    public FloatSetting glintScale = createSetting("Glint Scale", 1.0f, 0.1f, 10.0f);
    public ColorSetting glintColor = createSetting("Glint Color", new Color(-1));
    public BooleanSetting fill = createSetting("Fill", false);
    public BooleanSetting fillDepth = createSetting("Fill Depth", false);
    public BooleanSetting fillLighting = createSetting("Fill Lighting", false);
    public ColorSetting fillColor = createSetting("Fill Color", new Color(-1));
    public BooleanSetting outline = createSetting("Outline", false);
    public BooleanSetting outlineDepth = createSetting("Outline Depth", false);
    public FloatSetting outlineWidth = createSetting("Outline Width", 1.0f, 0.1f, 10.0f);
    public ColorSetting outlineColor = createSetting("Outline Color", new Color(-1));
    public FloatSetting scale = createSetting("Scale", 1.0f, 0.1f, 10.0f);
    public FloatSetting rotationSpeed = createSetting("Rotation Speed", 1.0f, 0.0f, 10.0f);
    public FloatSetting verticalSpeed = createSetting("Vertical Speed", 1.0f, 0.0f, 10.0f);
    public static ResourceLocation ENCHANTED_ITEM_GLINT_RES = new ResourceLocation("textures/misc/enchanted_item_glint.png");
    public static ResourceLocation LIGHTNING_TEXTURE = new ResourceLocation("textures/glint.png");

    public CrystalChams(){
        Instance = this;
    }
}
