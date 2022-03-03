package dev.zprestige.ruby.manager;

import dev.zprestige.ruby.Ruby;
import dev.zprestige.ruby.module.client.CustomFont;
import dev.zprestige.ruby.ui.font.FontRenderer;
import net.minecraft.client.Minecraft;

import java.awt.*;
import java.io.InputStream;

public class FontManager {
    protected final Minecraft mc = Ruby.mc;
    protected FontRenderer customFont;
    protected int size;

    public FontManager() {
        loadFont(size = 17);
    }

    public void loadFont(int size) {
        customFont = new FontRenderer(getFont(this.size = size));
    }

    public void drawStringWithShadow(String text, float x, float y, int color) {
        if (CustomFont.Instance.isEnabled()) {
            customFont.drawStringWithShadow(text, x, y, color);
        } else {
            mc.fontRenderer.drawStringWithShadow(text, x, y, color);
        }
    }

    public int getStringWidth(String text) {
        if (CustomFont.Instance.isEnabled()) {
            return customFont.getStringWidth(text);
        }
        return mc.fontRenderer.getStringWidth(text);
    }

    public float getFontHeight() {
        return (CustomFont.Instance.isEnabled() ? customFont.getHeight() : mc.fontRenderer.FONT_HEIGHT) / 2f;
    }

    private Font getFont(float size) {
        final Font plain = new Font("default", Font.PLAIN, (int) size);
        try {
            InputStream inputStream = FontManager.class.getResourceAsStream("/assets/minecraft/textures/ruby/font/" + "Font" + ".ttf");

            if (inputStream != null) {
                Font awtClientFont = Font.createFont(0, inputStream);
                awtClientFont = awtClientFont.deriveFont(Font.PLAIN, size);
                inputStream.close();
                return awtClientFont;
            }
            return plain;
        } catch (Exception exception) {
            return plain;
        }
    }

    public int getSize() {
        return size;
    }
}