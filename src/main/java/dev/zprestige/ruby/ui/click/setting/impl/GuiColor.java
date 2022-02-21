package dev.zprestige.ruby.ui.click.setting.impl;

import dev.zprestige.ruby.Ruby;
import dev.zprestige.ruby.module.client.NewGui;
import dev.zprestige.ruby.setting.impl.ColorSetting;
import dev.zprestige.ruby.ui.click.setting.GuiSetting;
import dev.zprestige.ruby.util.AnimationUtil;
import dev.zprestige.ruby.util.RenderUtil;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.text.TextComponentString;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.Objects;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.glEnable;

public class GuiColor extends GuiSetting {
    ColorSetting setting;
    private Color finalColor;
    boolean pickingColor = false;
    boolean pickingHue = false;
    boolean pickingAlpha = false;
    public static Tessellator tessellator;
    public static BufferBuilder builder;
    int hoverAnimWidth;

    static {
        tessellator = Tessellator.getInstance();
        builder = tessellator.getBuffer();
    }

    public GuiColor(ColorSetting setting) {
        super(setting);
        this.setting = setting;
        finalColor = setting.getValue();
        this.hoverAnimWidth = 0;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY) {
        RenderUtil.drawRect(x, y, x + width, y + (setting.isOpen() ? 122 : height), NewGui.Instance.backgroundColor.getValue().getRGB());
        RenderUtil.drawOutlineRect(x, y, x + width, y + (setting.isOpen() ? 122 : height), NewGui.Instance.backgroundColor.getValue(), 1f);
        try {
            RenderUtil.drawRect(x + width - 12, y + 1, x + width - 2, y + height - 2, finalColor.getRGB());
        } catch (Exception ignored) {
        }
        RenderUtil.drawOutlineRect(x + width - 12, y + 1, x + width - 2, y + height - 2, Color.BLACK, 0.1f);
        Ruby.rubyFont.drawStringWithShadow(setting.getName(), x, y, -1);
        if (setting.isOpen()) {
            drawPicker(setting, x, y + 15, x, y + 103, x, y + 93, mouseX, mouseY);
            {
                RenderUtil.drawRect(x, y + 111, x + (82 / 2f) - 1, y + 121, new Color(0, 0, 0, 50).getRGB());
                RenderUtil.drawRect(x + (82 / 2f) + 1, y + 111, x + 93, y + 121, new Color(0, 0, 0, 50).getRGB());
            }
            {
                RenderUtil.drawOutlineRect(x, y + 111, x + (82 / 2f) - 1, y + 121, NewGui.Instance.backgroundColor.getValue(), 1f);
                RenderUtil.drawOutlineRect(x + (82 / 2f) + 1, y + 111, x + 93, y + 121, NewGui.Instance.backgroundColor.getValue(), 1f);
            }
            {
                Ruby.rubyFont.drawStringWithShadow("Copy", (x + ((82) / 8f) * 2) - (Ruby.rubyFont.getStringWidth("Copy") / 2f), y + 112, isInsideCopy(mouseX, mouseY) ? NewGui.Instance.color.getValue().getRGB() : -1);
                Ruby.rubyFont.drawStringWithShadow("Paste", (x + ((82) / 8f) * 6) - (Ruby.rubyFont.getStringWidth("Paste") / 2f), y + 112, isInsidePaste(mouseX, mouseY) ? NewGui.Instance.color.getValue().getRGB() : -1);
            }
            setting.setValue(finalColor);
            if (hoverAnimWidth > width - NewGui.Instance.animationSpeed.getValue() && hoverAnimWidth < width)
                hoverAnimWidth = width;
            if (isInside(mouseX, mouseY))
                hoverAnimWidth = AnimationUtil.increaseNumber(hoverAnimWidth, width, NewGui.Instance.animationSpeed.getValue());
            else
                hoverAnimWidth = AnimationUtil.decreaseNumber(hoverAnimWidth, 0, NewGui.Instance.animationSpeed.getValue());
            RenderUtil.drawRect(x, y, x + hoverAnimWidth, y + height, new Color(0, 0, 0, 50).getRGB());
        }
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (mouseButton == 1 && isInside(mouseX, mouseY))
            setting.setOpen(!setting.isOpen());
        if (mouseButton == 0 && isInsideCopy(mouseX, mouseY) && setting.isOpen()) {
            String hex = String.format("#%06x", setting.getValue().getRGB() & 0xFFFFFF);
            StringSelection selection = new StringSelection(hex);
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            clipboard.setContents(selection, selection);
            Ruby.mc.player.sendMessage(new TextComponentString(("Color has been successfully copied to clipboard!")));
        }
        if (mouseButton == 0 && isInsidePaste(mouseX, mouseY) && setting.isOpen()) {
            try {
                if (readClipboard() != null) {
                    if (Objects.requireNonNull(readClipboard()).startsWith("#")) {
                        setting.setValue(Color.decode(Objects.requireNonNull(readClipboard())));
                    } else {
                        String[] color = Objects.requireNonNull(readClipboard()).split(",");
                        setting.setValue(new Color(Integer.parseInt(color[0]), Integer.parseInt(color[1]), Integer.parseInt(color[2])));
                    }
                }
            } catch (NumberFormatException e) {
                Ruby.mc.player.sendMessage(new TextComponentString("Not a color format! Available color formats:"));
                Ruby.mc.player.sendMessage(new TextComponentString("RGB: (red),(green),(blue)"));
                Ruby.mc.player.sendMessage(new TextComponentString("HEX: #FFFFFF - must start with a hashtag '#'"));
            }
        }
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int releaseButton) {
        pickingColor = pickingHue = pickingAlpha = false;
    }


    public boolean isInsideCopy(int mouseX, int mouseY) {
        return (mouseX > x + 1 && mouseX < x + (82 / 2f)) && (mouseY > y + 111 && mouseY < y + 121);
    }

    public boolean isInsidePaste(int mouseX, int mouseY) {
        return (mouseX > x + (82 / 2f) && mouseX < x + 93) && (mouseY > y + 111 && mouseY < y + 121);
    }

    public void drawPicker(ColorSetting setting, int pickerX, int pickerY, int hueSliderX, int hueSliderY, int alphaSliderX, int alphaSliderY, int mouseX, int mouseY) {
        float[] color = new float[]{
                0, 0, 0, 0
        };

        try {
            color = new float[]{
                    Color.RGBtoHSB(setting.getValue().getRed(), setting.getValue().getGreen(), setting.getValue().getBlue(), null)[0], Color.RGBtoHSB(setting.getValue().getRed(), setting.getValue().getGreen(), setting.getValue().getBlue(), null)[1], Color.RGBtoHSB(setting.getValue().getRed(), setting.getValue().getGreen(), setting.getValue().getBlue(), null)[2], setting.getValue().getAlpha() / 255f
            };
        } catch (Exception ignored) {

        }

        int pickerWidth = width - 2;
        int pickerHeight = 78;
        int hueSliderWidth = pickerWidth + 5;
        int hueSliderHeight = 7;
        int alphaSliderHeight = 7;

        if (pickingColor) {
            if (!(Mouse.isButtonDown(0) && mouseOver(pickerX, pickerY, pickerX + pickerWidth, pickerY + pickerHeight, mouseX, mouseY))) {
                pickingColor = false;
            }
        }

        if (pickingHue) {
            if (!(Mouse.isButtonDown(0) && mouseOver(hueSliderX, hueSliderY, hueSliderX + hueSliderWidth, hueSliderY + hueSliderHeight, mouseX, mouseY))) {
                pickingHue = false;
            }
        }

        if (pickingAlpha) {
            if (!(Mouse.isButtonDown(0) && mouseOver(alphaSliderX, alphaSliderY, alphaSliderX + pickerWidth, alphaSliderY + alphaSliderHeight, mouseX, mouseY)))
                pickingAlpha = false;
        }

        if (Mouse.isButtonDown(0) && mouseOver(pickerX, pickerY, pickerX + pickerWidth, pickerY + pickerHeight, mouseX, mouseY))
            pickingColor = true;
        if (Mouse.isButtonDown(0) && mouseOver(hueSliderX, hueSliderY, hueSliderX + hueSliderWidth, hueSliderY + hueSliderHeight, mouseX, mouseY))
            pickingHue = true;
        if (Mouse.isButtonDown(0) && mouseOver(alphaSliderX, alphaSliderY, alphaSliderX + pickerWidth, alphaSliderY + alphaSliderHeight, mouseX, mouseY))
            pickingAlpha = true;

        if (pickingHue) {
            float restrictedX = (float) Math.min(Math.max(hueSliderX, mouseX), hueSliderX + hueSliderWidth);
            color[0] = (restrictedX - (float) hueSliderX) / hueSliderWidth;
        }

        if (pickingAlpha) {
            float restrictedX = (float) Math.min(Math.max(alphaSliderX, mouseX), alphaSliderX + pickerWidth);
            color[3] = 1 - (restrictedX - (float) alphaSliderX) / pickerWidth;
        }

        if (pickingColor) {
            float restrictedX = (float) Math.min(Math.max(pickerX, mouseX), pickerX + pickerWidth);
            float restrictedY = (float) Math.min(Math.max(pickerY, mouseY), pickerY + pickerHeight);
            color[1] = (restrictedX - (float) pickerX) / pickerWidth;
            color[2] = 1 - (restrictedY - (float) pickerY) / pickerHeight;
        }

        int selectedColor = Color.HSBtoRGB(color[0], 1.0f, 1.0f);

        float selectedRed = (selectedColor >> 16 & 0xFF) / 255.0f;
        float selectedGreen = (selectedColor >> 8 & 0xFF) / 255.0f;
        float selectedBlue = (selectedColor & 0xFF) / 255.0f;

        drawPickerBase(pickerX, pickerY, pickerWidth, pickerHeight, selectedRed, selectedGreen, selectedBlue);

        drawHueSlider(hueSliderX, hueSliderY, hueSliderWidth - 2, hueSliderHeight, color[0]);

        int cursorX = (int) (pickerX + color[1] * pickerWidth);
        int cursorY = (int) ((pickerY + pickerHeight) - color[2] * pickerHeight);

        RenderUtil.drawOutlineRect(cursorX - 2, cursorY - 2, cursorX + 2, cursorY + 2, Color.black, 1);
        Gui.drawRect(cursorX - 2, cursorY - 2, cursorX + 2, cursorY + 2, -1);


        drawAlphaSlider(alphaSliderX, alphaSliderY, pickerWidth - 1, alphaSliderHeight, selectedRed, selectedGreen, selectedBlue, color[3]);

        finalColor = getColor(new Color(Color.HSBtoRGB(color[0], color[1], color[2])), color[3]);
    }

    public static boolean mouseOver(int minX, int minY, int maxX, int maxY, int mX, int mY) {
        return mX >= minX && mY >= minY && mX <= maxX && mY <= maxY;
    }

    public static Color getColor(Color color, float alpha) {
        final float red = (float) color.getRed() / 255;
        final float green = (float) color.getGreen() / 255;
        final float blue = (float) color.getBlue() / 255;
        return new Color(red, green, blue, alpha);
    }

    public static void drawPickerBase(int pickerX, int pickerY, int pickerWidth, int pickerHeight, float red, float green, float blue) {
        glEnable(GL_BLEND);
        glDisable(GL_TEXTURE_2D);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glShadeModel(GL_SMOOTH);
        glBegin(GL_POLYGON);
        glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        glVertex2f(pickerX, pickerY);
        glVertex2f(pickerX, pickerY + pickerHeight);
        glColor4f(red, green, blue, 255f);
        glVertex2f(pickerX + pickerWidth, pickerY + pickerHeight);
        glVertex2f(pickerX + pickerWidth, pickerY);
        glEnd();
        glDisable(GL_ALPHA_TEST);
        glBegin(GL_POLYGON);
        glColor4f(0.0f, 0.0f, 0.0f, 0.0f);
        glVertex2f(pickerX, pickerY);
        glColor4f(0.0f, 0.0f, 0.0f, 1.0f);
        glVertex2f(pickerX, pickerY + pickerHeight);
        glVertex2f(pickerX + pickerWidth, pickerY + pickerHeight);
        glColor4f(0.0f, 0.0f, 0.0f, 0.0f);
        glVertex2f(pickerX + pickerWidth, pickerY);
        glEnd();
        glEnable(GL_ALPHA_TEST);
        glShadeModel(GL_FLAT);
        glEnable(GL_TEXTURE_2D);
        glDisable(GL_BLEND);
    }

    public void drawHueSlider(int x, int y, int width, int height, float hue) {
        int step = 0;
        if (height > width) {
            RenderUtil.drawRect(x, y, x + width, y + 4, 0xFFFF0000);
            y += 4;

            for (int colorIndex = 0; colorIndex < 6; colorIndex++) {
                int previousStep = Color.HSBtoRGB((float) step / 6, 1.0f, 1.0f);
                int nextStep = Color.HSBtoRGB((float) (step + 1) / 6, 1.0f, 1.0f);
                drawGradientRect(x, y + step * (height / 6f), x + width, y + (step + 1) * (height / 6f), previousStep, nextStep, false);
                step++;
            }
            int sliderMinY = (int) (y + height * hue) - 4;
            RenderUtil.drawRect(x, sliderMinY - 1, x + width, sliderMinY + 1, -1);
            RenderUtil.drawOutlineRect(x, sliderMinY - 1, x + width, sliderMinY + 1, Color.BLACK, 1);
        } else {
            for (int colorIndex = 0; colorIndex < 6; colorIndex++) {
                int previousStep = Color.HSBtoRGB((float) step / 6, 1.0f, 1.0f);
                int nextStep = Color.HSBtoRGB((float) (step + 1) / 6, 1.0f, 1.0f);
                gradient(x + step * (width / 6), y, x + (step + 1) * (width / 6), y + height, previousStep, nextStep, true);
                step++;
            }

            int sliderMinX = (int) (x + (width * hue));
            RenderUtil.drawRect(sliderMinX - 1, y, sliderMinX + 1, y + height, -1);
            RenderUtil.drawOutlineRect(sliderMinX - 1, y, sliderMinX + 1, y + height, Color.BLACK, 1);
        }
    }

    public void drawAlphaSlider(int x, int y, int width, int height, float red, float green, float blue, float alpha) {
        drawLeftGradientRect(x, y, x + width + 13, y + height, new Color(red, green, blue, 1).getRGB(), 0);
        int sliderMinX = (int) (x + width - (width * alpha));
        RenderUtil.drawRect(sliderMinX - 1, y, sliderMinX + 1, y + height, -1);
        RenderUtil.drawOutlineRect(sliderMinX - 1, y, sliderMinX + 1, y + height, Color.BLACK, 1);
    }

    public static void drawGradientRect(final double leftpos, final double top, final double right, final double bottom, final int col1, final int col2) {
        final float f = (col1 >> 24 & 0xFF) / 255.0f;
        final float f2 = (col1 >> 16 & 0xFF) / 255.0f;
        final float f3 = (col1 >> 8 & 0xFF) / 255.0f;
        final float f4 = (col1 & 0xFF) / 255.0f;
        final float f5 = (col2 >> 24 & 0xFF) / 255.0f;
        final float f6 = (col2 >> 16 & 0xFF) / 255.0f;
        final float f7 = (col2 >> 8 & 0xFF) / 255.0f;
        final float f8 = (col2 & 0xFF) / 255.0f;
        glEnable(3042);
        GL11.glDisable(3553);
        GL11.glBlendFunc(770, 771);
        glEnable(2848);
        GL11.glShadeModel(7425);
        GL11.glPushMatrix();
        GL11.glBegin(7);
        GL11.glColor4f(f2, f3, f4, f);
        GL11.glVertex2d(leftpos, top);
        GL11.glVertex2d(leftpos, bottom);
        GL11.glColor4f(f6, f7, f8, f5);
        GL11.glVertex2d(right, bottom);
        GL11.glVertex2d(right, top);
        GL11.glEnd();
        GL11.glPopMatrix();
        glEnable(3553);
        GL11.glDisable(3042);
        GL11.glDisable(2848);
        GL11.glShadeModel(7424);
    }

    public static void drawLeftGradientRect(int left, int top, int right, int bottom, int startColor, int endColor) {
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.disableAlpha();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.shadeModel(GL_SMOOTH);
        builder.begin(GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
        builder.pos(right, top, 0).color((float) (endColor >> 24 & 255) / 255.0F, (float) (endColor >> 16 & 255) / 255.0F, (float) (endColor >> 8 & 255) / 255.0F, (float) (endColor >> 24 & 255) / 255.0F).endVertex();
        builder.pos(left, top, 0).color((float) (startColor >> 16 & 255) / 255.0F, (float) (startColor >> 8 & 255) / 255.0F, (float) (startColor & 255) / 255.0F, (float) (startColor >> 24 & 255) / 255.0F).endVertex();
        builder.pos(left, bottom, 0).color((float) (startColor >> 16 & 255) / 255.0F, (float) (startColor >> 8 & 255) / 255.0F, (float) (startColor & 255) / 255.0F, (float) (startColor >> 24 & 255) / 255.0F).endVertex();
        builder.pos(right, bottom, 0).color((float) (endColor >> 24 & 255) / 255.0F, (float) (endColor >> 16 & 255) / 255.0F, (float) (endColor >> 8 & 255) / 255.0F, (float) (endColor >> 24 & 255) / 255.0F).endVertex();
        tessellator.draw();
        GlStateManager.shadeModel(GL_FLAT);
        GlStateManager.disableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.enableTexture2D();
    }

    public static void gradient(int minX, int minY, int maxX, int maxY, int startColor, int endColor, boolean left) {
        if (left) {
            final float startA = (startColor >> 24 & 0xFF) / 255.0f;
            final float startR = (startColor >> 16 & 0xFF) / 255.0f;
            final float startG = (startColor >> 8 & 0xFF) / 255.0f;
            final float startB = (startColor & 0xFF) / 255.0f;

            final float endA = (endColor >> 24 & 0xFF) / 255.0f;
            final float endR = (endColor >> 16 & 0xFF) / 255.0f;
            final float endG = (endColor >> 8 & 0xFF) / 255.0f;
            final float endB = (endColor & 0xFF) / 255.0f;

            glEnable(GL11.GL_BLEND);
            GL11.glDisable(GL11.GL_TEXTURE_2D);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            GL11.glShadeModel(GL_SMOOTH);
            GL11.glBegin(GL11.GL_POLYGON);
            {
                GL11.glColor4f(startR, startG, startB, startA);
                GL11.glVertex2f(minX, minY);
                GL11.glVertex2f(minX, maxY);
                GL11.glColor4f(endR, endG, endB, endA);
                GL11.glVertex2f(maxX, maxY);
                GL11.glVertex2f(maxX, minY);
            }
            GL11.glEnd();
            GL11.glShadeModel(GL11.GL_FLAT);
            glEnable(GL11.GL_TEXTURE_2D);
            GL11.glDisable(GL11.GL_BLEND);
        } else drawGradientRect(minX, minY, maxX, maxY, startColor, endColor);
    }

    public static int gradientColor(int color, int percentage) {
        int r = (((color & 0xFF0000) >> 16) * (100 + percentage) / 100);
        int g = (((color & 0xFF00) >> 8) * (100 + percentage) / 100);
        int b = ((color & 0xFF) * (100 + percentage) / 100);
        return new Color(r, g, b).hashCode();
    }

    public static void drawGradientRect(float left, float top, float right, float bottom, int startColor,
                                        int endColor, boolean hovered) {
        if (hovered) {
            startColor = gradientColor(startColor, -20);
            endColor = gradientColor(endColor, -20);
        }
        float c = (float) (startColor >> 24 & 255) / 255.0F;
        float c1 = (float) (startColor >> 16 & 255) / 255.0F;
        float c2 = (float) (startColor >> 8 & 255) / 255.0F;
        float c3 = (float) (startColor & 255) / 255.0F;
        float c4 = (float) (endColor >> 24 & 255) / 255.0F;
        float c5 = (float) (endColor >> 16 & 255) / 255.0F;
        float c6 = (float) (endColor >> 8 & 255) / 255.0F;
        float c7 = (float) (endColor & 255) / 255.0F;
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.disableAlpha();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.shadeModel(7425);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_COLOR);
        bufferbuilder.pos(right, top, 0).color(c1, c2, c3, c).endVertex();
        bufferbuilder.pos(left, top, 0).color(c1, c2, c3, c).endVertex();
        bufferbuilder.pos(left, bottom, 0).color(c5, c6, c7, c4).endVertex();
        bufferbuilder.pos(right, bottom, 0).color(c5, c6, c7, c4).endVertex();
        tessellator.draw();
        GlStateManager.shadeModel(7424);
        GlStateManager.disableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.enableTexture2D();
    }

    public static String readClipboard() {
        try {
            return (String) Toolkit.getDefaultToolkit().getSystemClipboard().getData(DataFlavor.stringFlavor);
        } catch (IOException | UnsupportedFlavorException exception) {
            return null;
        }

    }
}

