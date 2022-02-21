package dev.zprestige.ruby.ui.buttons;

import dev.zprestige.ruby.Ruby;
import dev.zprestige.ruby.ui.config.ConfigGuiScreen;
import dev.zprestige.ruby.util.RenderUtil;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import java.awt.*;

public class ConfigButton {
    Color color;
    int x;
    int y;
    int width;
    int height;

    public ConfigButton(Color color, int x, int y, int width, int height) {
        this.color = color;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public void drawScreen(int mouseX, int mouseY) {
        GlStateManager.enableAlpha();
        Ruby.mc.getTextureManager().bindTexture(new ResourceLocation("textures/icons/folder.png"));
        GlStateManager.color(1f, 1f, 1f);
        GL11.glPushMatrix();
        GuiScreen.drawScaledCustomSizeModalRect(x + 2, y + 2, 0, 0, width - 2, height - 2, width - 2, height - 2, width - 2, height - 2);
        GL11.glPopMatrix();
        GlStateManager.disableAlpha();
        RenderUtil.drawOutlineRect(x, y, x + width, y + height, color, 2f);
        if (isInside(mouseX, mouseY))
            RenderUtil.drawRect(x, y, x + width, y + height, new Color(0, 0, 0, 50).getRGB());
    }

    public void mouseClicked(int mouseX, int mouseY, int clickedButton) {
        if (isInside(mouseX, mouseY) && clickedButton == 0)
            Ruby.mc.displayGuiScreen(new ConfigGuiScreen());
    }

    public boolean isInside(int mouseX, int mouseY) {
        return mouseX > x && mouseX < x + width && mouseY > y && mouseY < y + height;
    }
}

