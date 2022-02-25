package dev.zprestige.ruby.ui.click.setting.newsettings;

import dev.zprestige.ruby.Ruby;
import dev.zprestige.ruby.module.client.ClickGui;
import dev.zprestige.ruby.settings.impl.ColorBox;
import dev.zprestige.ruby.ui.click.setting.NewSetting;
import dev.zprestige.ruby.util.RenderUtil;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

import static org.lwjgl.opengl.GL11.*;

public class ColorButton extends NewSetting {
    protected static Tessellator tessellator;
    protected static BufferBuilder builder;

    static {
        tessellator = Tessellator.getInstance();
        builder = tessellator.getBuffer();
    }

    protected ColorBox color;
    protected java.awt.Color finalColor;
    protected boolean pickingColor = false, pickingHue = false, pickingAlpha = false, opened = false, dragging = false;
    protected int hoverAnimWidth, panelX, panelY, dragX, dragY;

    public ColorButton(ColorBox setting) {
        super(setting);
        this.color = setting;
        finalColor = setting.GetColor();
        this.hoverAnimWidth = 0;
    }

    public static boolean mouseOver(int minX, int minY, int maxX, int maxY, int mX, int mY) {
        return mX >= minX && mY >= minY && mX <= maxX && mY <= maxY;
    }

    public static java.awt.Color getColor(java.awt.Color color, float alpha) {
        final float red = (float) color.getRed() / 255;
        final float green = (float) color.getGreen() / 255;
        final float blue = (float) color.getBlue() / 255;
        return new java.awt.Color(red, green, blue, alpha);
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

    public static void drawGradientRect(float left, float top, float right, float bottom, int startColor, int endColor, boolean hovered) {
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

    @Override
    public void render(int mouseX, int mouseY) {
        super.render(mouseX, mouseY);
        Ruby.rubyFont.drawStringWithShadow(color.getName(), x, getStringMiddle(color.getName()), -1);
        RenderUtil.drawOutlineRect(x + width - 11, y + 2, x + width - 2, y + height - 2, ClickGui.Instance.color.GetColor(), 1.0f);
        RenderUtil.drawRect(x + width - 10, y + 3, x + width - 3, y + height - 3, color.GetColor().getRGB());
        RenderUtil.drawOutlineRect(x + width - 10, y + 3, x + width - 3, y + height - 3, ClickGui.Instance.color.GetColor(), 1.0f);

        if (opened) {
            glPushMatrix();
            glPushAttrib(GL_SCISSOR_BIT);
            {
                RenderUtil.scissor(0, 0, 2000, 2000);
                glEnable(GL_SCISSOR_TEST);
            }
            dragScreen(mouseX, mouseY);
            RenderUtil.drawRect(panelX - 1, panelY - 14, panelX + width - 1, panelY + 109, ClickGui.Instance.backgroundColor.GetColor().getRGB());
            Ruby.rubyFont.drawStringWithShadow(color.getName(), panelX + (width / 2f) - (Ruby.rubyFont.getStringWidth(color.getName()) / 2f), panelY - 7.5f - (Ruby.rubyFont.getHeight(color.getName()) / 2f), -1);
            RenderUtil.drawOutlineRect(panelX - 1, panelY - 14, panelX + width - 1, panelY - 1, ClickGui.Instance.color.GetColor(), 1.0f);
            RenderUtil.drawOutlineRect(panelX - 1, panelY - 1, panelX + width - 1, panelY + 109, ClickGui.Instance.color.GetColor(), 1.0f);

            RenderUtil.prepareScissor(panelX + 1, 0, width - 4, 1000);
            drawPicker(color, panelX + 1, panelY, panelX, panelY + 90, panelX, panelY + 80, mouseX, mouseY);
            RenderUtil.releaseScissor();

            RenderUtil.drawOutlineRect(panelX + 1, panelY, panelX + width - 3, panelY + 78, ClickGui.Instance.color.GetColor(), 1.0f);
            RenderUtil.drawOutlineRect(panelX + 1, panelY + 80, panelX + width - 3, panelY + 87, ClickGui.Instance.color.GetColor(), 1.0f);
            RenderUtil.drawOutlineRect(panelX + 1, panelY + 90, panelX + width - 3, panelY + 97, ClickGui.Instance.color.GetColor(), 1.0f);

            RenderUtil.drawRect(panelX + 1, panelY + 98, panelX + width - 3, panelY + 108, ClickGui.Instance.backgroundColor.GetColor().getRGB());
            RenderUtil.drawOutlineRect(panelX + 1, panelY + 98, panelX + width - 3, panelY + 108, ClickGui.Instance.color.GetColor(), 1.0f);
            final String hex = String.format("#%06x", color.GetColor().getRGB() & 0xFFFFFF);
            Ruby.rubyFont.drawStringWithShadow(hex, panelX + 2, panelY + 103 - (Ruby.rubyFont.getHeight(hex) / 2f), -1);

            color.setValue(finalColor);
            glDisable(GL_SCISSOR_TEST);
            glPopAttrib();
            glPopMatrix();
        }
    }

    protected boolean isInsideHex(int mouseX, int mouseY) {
        return mouseX > panelX + 1 && mouseX < panelX + width - 3 && mouseY > panelY + 98 && mouseY < panelY + 108;
    }

    protected boolean insideBox(int mouseX, int mouseY) {
        return mouseX > x + width - 12 && mouseX < x + width - 2 && mouseY > y + 2 && mouseY < y + height - 2;
    }

    protected boolean isInsideTop(int mouseX, int mouseY) {
        return mouseX > panelX - 1 && mouseX < panelX + width - 1 && mouseY > panelY - 14 && mouseY < panelY - 1;
    }

    @Override
    public void click(int mouseX, int mouseY, int mouseButton) {
        if (mouseButton == 0 && opened && isInsideTop(mouseX, mouseY)) {
            dragX = panelX - mouseX;
            dragY = panelY - mouseY;
            dragging = true;
        }
        if (mouseButton == 0 && insideBox(mouseX, mouseY)) {
            if (!opened) {
                panelX = mouseX;
                panelY = mouseY;
            }
            opened = !opened;
        }
        if (mouseButton == 1 && isInsideHex(mouseX, mouseY) && opened) {
            copyClipBoard();
        }
        if (mouseButton == 0 && isInsideHex(mouseX, mouseY) && opened) {
            pasteClipBoard();
        }
    }

    public void copyClipBoard() {
        String hex = finalColor.getRed() + "-" + finalColor.getGreen() + "-" + finalColor.getBlue() + "-" + finalColor.getAlpha();
        StringSelection selection = new StringSelection(hex);
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(selection, selection);
        ClickGui.Instance.mainScreen.copyPasteMap.put(255.0f, "Color has been successfully copied to clipboard.");
    }

    public void pasteClipBoard() {
        String string;
        try {
            string = (String) Toolkit.getDefaultToolkit().getSystemClipboard().getData(DataFlavor.stringFlavor);
        } catch (IOException | UnsupportedFlavorException exception) {
            return;
        }
        try {
            String[] color1 = string.split("-");
            color.setValue(new Color(Integer.parseInt(color1[0]), Integer.parseInt(color1[1]), Integer.parseInt(color1[2]), Integer.parseInt(color1[3])));
            ClickGui.Instance.mainScreen.copyPasteMap.put(255.0f, "Color has been successfully pasted from clipboard.");
        } catch (Exception exception) {
            ClickGui.Instance.mainScreen.copyPasteMap.put(255.0f, "Wrong color format" + exception.getLocalizedMessage().replace("\"", ""));
        }
    }

    public void dragScreen(int mouseX, int mouseY) {
        if (dragging) {
            panelX = dragX + mouseX;
            panelY = dragY + mouseY;
        }
    }

    @Override
    public void release(int mouseX, int mouseY, int releaseButton) {
        dragging = false;
        pickingColor = pickingHue = pickingAlpha = false;
    }

    public void drawPicker(ColorBox setting, int pickerX, int pickerY, int hueSliderX, int hueSliderY, int alphaSliderX, int alphaSliderY, int mouseX, int mouseY) {
        float[] color = new float[]{
                0, 0, 0, 0
        };

        try {
            color = new float[]{
                    java.awt.Color.RGBtoHSB(setting.GetColor().getRed(), setting.GetColor().getGreen(), setting.GetColor().getBlue(), null)[0], java.awt.Color.RGBtoHSB(setting.GetColor().getRed(), setting.GetColor().getGreen(), setting.GetColor().getBlue(), null)[1], java.awt.Color.RGBtoHSB(setting.GetColor().getRed(), setting.GetColor().getGreen(), setting.GetColor().getBlue(), null)[2], setting.GetColor().getAlpha() / 255f
            };
        } catch (Exception ignored) {

        }

        final int pickerWidth = width - 4;
        final int pickerHeight = 78;
        final int hueSliderWidth = pickerWidth + 8;
        final int hueSliderHeight = 7;
        final int alphaSliderHeight = 7;

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
            float restrictedX = (float) Math.min(Math.max(hueSliderX, mouseX), hueSliderX + hueSliderWidth - 7);
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

        int selectedColor = java.awt.Color.HSBtoRGB(color[0], 1.0f, 1.0f);

        float selectedRed = (selectedColor >> 16 & 0xFF) / 255.0f;
        float selectedGreen = (selectedColor >> 8 & 0xFF) / 255.0f;
        float selectedBlue = (selectedColor & 0xFF) / 255.0f;

        drawPickerBase(pickerX, pickerY, pickerWidth, pickerHeight, selectedRed, selectedGreen, selectedBlue);

        drawHueSlider(hueSliderX, hueSliderY, hueSliderWidth - 2, hueSliderHeight, color[0]);

        int cursorX = (int) (pickerX + color[1] * pickerWidth);
        int cursorY = (int) ((pickerY + pickerHeight) - color[2] * pickerHeight);

        Gui.drawRect(cursorX - 1, cursorY - 1, cursorX + 1, cursorY + 1, -1);
        RenderUtil.drawOutlineRect(cursorX - 1, cursorY - 1, cursorX + 1, cursorY + 1, ClickGui.Instance.color.GetColor(), 1.0f);


        drawAlphaSlider(alphaSliderX, alphaSliderY, pickerWidth, alphaSliderHeight, selectedRed, selectedGreen, selectedBlue, color[3]);

        finalColor = getColor(new java.awt.Color(java.awt.Color.HSBtoRGB(color[0], color[1], color[2])), color[3]);
    }

    public void drawHueSlider(int x, int y, int width, int height, float hue) {
        int step = 0;
        if (height > width) {
            RenderUtil.drawRect(x, y, x + width, y + 4, 0xFFFF0000);
            y += 4;

            for (int colorIndex = 0; colorIndex < 6; colorIndex++) {
                int previousStep = java.awt.Color.HSBtoRGB((float) step / 6, 1.0f, 1.0f);
                int nextStep = java.awt.Color.HSBtoRGB((float) (step + 1) / 6, 1.0f, 1.0f);
                drawGradientRect(x, y + step * (height / 6f), x + width, y + (step + 1) * (height / 6f), previousStep, nextStep, false);
                step++;
            }
            int sliderMinY = (int) (y + height * hue) - 4;
            RenderUtil.drawRect(x, sliderMinY - 1, x + width, sliderMinY + 1, -1);
            RenderUtil.drawOutlineRect(x, sliderMinY - 1, x + width, sliderMinY + 1, java.awt.Color.BLACK, 1);
            RenderUtil.drawOutlineRect(x, sliderMinY - 1, x + width, sliderMinY + 1, ClickGui.Instance.color.GetColor(), 1.0f);
        } else {
            for (int colorIndex = 0; colorIndex < 6; colorIndex++) {
                int previousStep = java.awt.Color.HSBtoRGB((float) step / 6, 1.0f, 1.0f);
                int nextStep = java.awt.Color.HSBtoRGB((float) (step + 1) / 6, 1.0f, 1.0f);
                gradient(x + step * (width / 6), y, x + (step + 1) * (width / 6), y + height, previousStep, nextStep, true);
                step++;
            }

            int sliderMinX = (int) (x + (width * hue));
            RenderUtil.drawRect(sliderMinX - 1, y, sliderMinX + 1, y + height, -1);
            RenderUtil.drawOutlineRect(sliderMinX - 1, y, sliderMinX + 1, y + height, java.awt.Color.BLACK, 1);
            RenderUtil.drawOutlineRect(sliderMinX - 1, y, sliderMinX + 1, y + height, ClickGui.Instance.color.GetColor(), 1.0f);
        }
    }

    public void drawAlphaSlider(int x, int y, int width, int height, float red, float green, float blue, float alpha) {

        boolean left = true;
        int checkerBoardSquareSize = height / 2;

        for (int squareIndex = -checkerBoardSquareSize; squareIndex < width; squareIndex += checkerBoardSquareSize) {
            if (!left) {
                RenderUtil.drawRect(x + squareIndex, y, x + squareIndex + checkerBoardSquareSize, y + height, 0xFFFFFFFF);
                RenderUtil.drawRect(x + squareIndex, y + checkerBoardSquareSize, x + squareIndex + checkerBoardSquareSize, y + height, 0xFF909090);

                if (squareIndex < width - checkerBoardSquareSize) {
                    int minX = x + squareIndex + checkerBoardSquareSize;
                    int maxX = Math.min(x + width, x + squareIndex + checkerBoardSquareSize * 2);
                    RenderUtil.drawRect(minX, y, maxX, y + height, 0xFF909090);
                    RenderUtil.drawRect(minX, y + checkerBoardSquareSize, maxX, y + height, 0xFFFFFFFF);
                }
            }

            left = !left;
        }

        drawLeftGradientRect(x, y, x + width + 13, y + height, new java.awt.Color(red, green, blue, 1).getRGB(), 0);
        int sliderMinX = (int) (x + width - (width * alpha));
        RenderUtil.drawRect(sliderMinX - 1, y, sliderMinX + 1, y + height, -1);
        RenderUtil.drawOutlineRect(sliderMinX - 1, y, sliderMinX + 1, y + height, java.awt.Color.BLACK, 1);
        RenderUtil.drawOutlineRect(sliderMinX - 1, y, sliderMinX + 1, y + height, ClickGui.Instance.color.GetColor(), 1.0f);
    }
}


