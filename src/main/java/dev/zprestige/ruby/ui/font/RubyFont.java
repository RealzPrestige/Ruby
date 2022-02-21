package dev.zprestige.ruby.ui.font;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.StringUtils;
import org.newdawn.slick.UnicodeFont;
import org.newdawn.slick.font.effects.ColorEffect;

import java.awt.*;
import java.io.IOException;
import java.util.Objects;
import java.util.regex.Pattern;

public class RubyFont {
    private int scaleFactor = new ScaledResolution(Minecraft.getMinecraft()).getScaleFactor();
    private static final Pattern colorPattern = Pattern.compile("\u00c2\u00a7[0123456789abcdefklmnor]");
    public final int height = 9;
    private UnicodeFont font;
    private final String name;
    private final float size;
    private float aAFactor;

    public RubyFont(String name, float size) {
        this.name = name;
        this.size = size;
        ScaledResolution sr = new ScaledResolution(Minecraft.getMinecraft());
        try {
            this.scaleFactor = sr.getScaleFactor();
            this.font = new UnicodeFont(this.getFontByName(name).deriveFont(size * (float) this.scaleFactor / 2.0f));
            this.font.addAsciiGlyphs();
            this.font.getEffects().add(new ColorEffect(Color.WHITE));
            this.font.loadGlyphs();
        } catch (Exception e2) {
            e2.printStackTrace();
        }
        this.aAFactor = sr.getScaleFactor();
    }

    private java.awt.Font getFontByName(String name) throws IOException, FontFormatException {
        return this.getFontFromInput("/assets/minecraft/textures/ruby/font/" + name + ".ttf");
    }

    private java.awt.Font getFontFromInput(String path) throws IOException, FontFormatException {
        return java.awt.Font.createFont(0, Objects.requireNonNull(RubyFont.class.getResourceAsStream(path)));
    }

    public int drawString(String text, float x2, float y2, int color) {
        if (text == null) {
            return 0;
        }
        ScaledResolution resolution = new ScaledResolution(Minecraft.getMinecraft());
        try {
            if (resolution.getScaleFactor() != this.scaleFactor) {
                this.scaleFactor = resolution.getScaleFactor();
                this.font = new UnicodeFont(this.getFontByName(this.name).deriveFont(this.size * (float) this.scaleFactor / 2.0f));
                this.font.addAsciiGlyphs();
                this.font.getEffects().add(new ColorEffect(Color.WHITE));
                this.font.loadGlyphs();
            }
        } catch (Exception e2) {
            e2.printStackTrace();
        }
        this.aAFactor = resolution.getScaleFactor();
        GlStateManager.pushMatrix();
        GlStateManager.scale(1.0f / this.aAFactor, 1.0f / this.aAFactor, 1.0f / this.aAFactor);
        y2 *= this.aAFactor;
        float originalX = x2 *= this.aAFactor;
        float red = (float) (color >> 16 & 0xFF) / 255.0f;
        float green = (float) (color >> 8 & 0xFF) / 255.0f;
        float blue = (float) (color & 0xFF) / 255.0f;
        float alpha = (float) (color >> 24 & 0xFF) / 255.0f;
        GlStateManager.color(red, green, blue, alpha);
        char[] characters = text.toCharArray();
        GlStateManager.disableLighting();
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.blendFunc(770, 771);
        String[] parts = colorPattern.split(text);
        int index = 0;
        for (String s2 : parts) {
            for (String s22 : s2.split("\n")) {
                for (String s3 : s22.split("\r")) {
                    this.font.drawString(x2, y2, s3, new org.newdawn.slick.Color(color));
                    x2 += (float) this.font.getWidth(s3);
                    if ((index += s3.length()) >= characters.length || characters[index] != '\r') continue;
                    x2 = originalX;
                    ++index;
                }
                if (index >= characters.length || characters[index] != '\n') continue;
                x2 = originalX;
                y2 += this.getHeight(s22) * 2.0f;
                ++index;
            }
        }
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        GlStateManager.popMatrix();
        return (int) x2;
    }

    public int drawStringWithShadow(String text, float x2, float y2, int color) {
        this.drawString(StringUtils.stripControlCodes(text), x2 + 0.5f, y2 + 0.5f, 0);
        return this.drawString(text, x2, y2, color);
    }

    public int drawString(String text, float x2, float y2, int color, boolean shadow) {
        if (shadow) {
            this.drawStringWithShadow(text, x2, y2, color);
        } else {
            this.drawString(text, x2, y2, color);
        }
        return this.drawString(text, x2, y2, color);
    }

    public float getHeight(String s2) {
        return (float) this.font.getHeight(s2) / 2.0f;
    }

    public float getStringWidth(String text) {
        return this.font.getWidth(text) / 2f;
    }

    public String getName() {
        return name;
    }

    public float getSize() {
        return size;
    }
}