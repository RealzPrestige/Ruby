package dev.zprestige.ruby.ui.config;

import dev.zprestige.ruby.Ruby;
import dev.zprestige.ruby.module.client.ClickGui;
import dev.zprestige.ruby.util.RenderUtil;
import dev.zprestige.ruby.util.Timer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ChatAllowedCharacters;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

import static org.lwjgl.opengl.GL11.*;

public class ConfigGuiScreen extends GuiScreen {
    public int x = 0, y = 0, width = 200, height = 150, dragX, dragY;
    public boolean isCorrected = false, isDragging = false, isTyping = false;
    public ArrayList<ConfigGuiButton> configButtons = new ArrayList<>();
    public String searchString = "";

    public ConfigGuiScreen() {
        File path = new File(Ruby.mc.gameDir + File.separator + "Ruby" + File.separator + "Configs");
        if (!path.exists())
            return;
        int i = y + 4;
        for (String file : Objects.requireNonNull(path.list())) {
            if (!file.equals("Active.txt") && !file.equals("hitmarker.wav") && !file.equals("Analyzer"))
                configButtons.add(new ConfigGuiButton(file, x + 4, i += 14, width - 8, 13));
        }
    }

    public void dragScreen(int mouseX, int mouseY) {
        if (!isDragging)
            return;
        x = dragX + mouseX;
        y = dragY + mouseY;
        int i = y + 4;
        for (ConfigGuiButton configGuiButton : configButtons) {
            if (searchString.equals("") || configGuiButton.name.contains(searchString)) {
                configGuiButton.y = configGuiButton.scrollY + (i += 14);
                configGuiButton.x = x + 4;
            }
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        { // Setting up
            if (isInsideFileList(mouseX, mouseY))
                setScroll();
            dragScreen(mouseX, mouseY);
        }
        { // Correcting x and y
            if (!isCorrected) {
                ScaledResolution scaledResolution = new ScaledResolution(mc);
                x = (scaledResolution.getScaledWidth() / 2) - 100;
                y = (scaledResolution.getScaledHeight() / 2) - 75;
                int i = y + 4;
                for (ConfigGuiButton configGuiButton : configButtons) {
                    if (searchString.equals("") || configGuiButton.name.contains(searchString)) {
                        configGuiButton.y = configGuiButton.scrollY + (i += 14);
                        configGuiButton.x = x + 4;
                    }
                }
                isCorrected = true;
            }
        }
        {
            int i = y + 4;
            for (ConfigGuiButton configGuiButton : configButtons) {
                if (searchString.equals("") || configGuiButton.name.contains(searchString)) {
                    configGuiButton.y = configGuiButton.scrollY + (i += 14);
                    configGuiButton.x = x + 4;
                }
            }
        }
        { // Background
            RenderUtil.drawRect(x, y, x + width, y + height, ClickGui.Instance.backgroundColor.GetColor().getRGB());
            RenderUtil.drawOutlineRect(x, y, x + width, y + height, ClickGui.Instance.color.GetColor(), 2f);
        }
        { // Top Bar
            RenderUtil.drawRect(x + 2, y + 2, x + width - 2, y + 15, ClickGui.Instance.backgroundColor.GetColor().getRGB());
            RenderUtil.drawOutlineRect(x + 2, y + 2, x + width - 2, y + 15, ClickGui.Instance.color.GetColor(), 2f);
            drawBarIcon();
            Ruby.rubyFont.drawStringWithShadow("Config Manager", x + 20, (y + (15 / 2f) - (Ruby.mc.fontRenderer.FONT_HEIGHT / 2f)), -1);
        }
        { // Config Button Area
            RenderUtil.drawRect(x + 2, y + 17, x + width - 2, y + height - 17, ClickGui.Instance.backgroundColor.GetColor().getRGB());
            RenderUtil.drawOutlineRect(x + 2, y + 17, x + width - 2, y + height - 17, ClickGui.Instance.color.GetColor(), 2f);
        }
        { // Search bar
            RenderUtil.drawRect(x + 2, y + height - 15, x + width - 2, y + height - 2, ClickGui.Instance.backgroundColor.GetColor().getRGB());
            RenderUtil.drawOutlineRect(x + 2, y + height - 15, x + width - 2, y + height - 2, ClickGui.Instance.color.GetColor(), 2f);
            if (searchString.equals("")) {
                Ruby.rubyFont.drawStringWithShadow("Search"+ (isTyping ? idleSign() : ""), x + 3, y + height - 15 + (13 / 2f) - (Ruby.rubyFont.getHeight("Search"+ (isTyping ? idleSign() : "")) / 2f), Color.GRAY.getRGB());
            }else {
                Ruby.rubyFont.drawStringWithShadow(searchString + (isTyping ? idleSign() : ""), x + 3, y + height - 15 + (13 / 2f) - (Ruby.rubyFont.getHeight(searchString + (isTyping ? idleSign() : "")) / 2f), -1);
            }
        }
        {
            glPushMatrix();
            glPushAttrib(GL_SCISSOR_BIT);
            {
                RenderUtil.scissor(x + 2, y + 17, x + width - 2, y + height - 17);
                glEnable(GL_SCISSOR_TEST);
            }
            configButtons.stream().filter(configGuiButton -> searchString.equals("") || configGuiButton.name.contains(searchString)).forEach(configGuiButton -> configGuiButton.render(mouseX, mouseY));
            glDisable(GL_SCISSOR_TEST);
            glPopAttrib();
            glPopMatrix();
        }
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int clickedButton) {
        {
            if (clickedButton == 0) {
                if (isInsideSearchField(mouseX, mouseY))
                    isTyping = !isTyping;
                if (isInsideBar(mouseX, mouseY)) {
                    dragX = x - mouseX;
                    dragY = y - mouseY;
                    isDragging = true;
                }
            }
        }
        {
            configButtons.stream().filter(configGuiButton ->  searchString.equals("") || configGuiButton.name.contains(searchString)).forEach(configGuiButton -> configGuiButton.mouseClicked(mouseX, mouseY, clickedButton));
        }
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int state) {
        if (state == 0)
            isDragging = false;
    }

    public void setScroll() {
        int dWheel = Mouse.getDWheel();
        configButtons.forEach(configGuiButton -> {
            if (dWheel < 0)
                configGuiButton.scrollY -= ClickGui.Instance.scrollSpeed.GetSlider();
            else if (dWheel > 0)
                configGuiButton.scrollY += ClickGui.Instance.scrollSpeed.GetSlider();
        });
    }


    public boolean isInsideBar(int mouseX, int mouseY) {
        return mouseX > x + 2 && mouseX < x + width - 2 && mouseY > y + 2 && mouseY < y + 15;
    }

    public boolean isInsideFileList(int mouseX, int mouseY) {
        return mouseX > x + 2 && mouseX < x + width - 2 && mouseY > y + 17 && mouseY < y + height - 17;
    }

    public boolean isInsideSearchField(int mouseX, int mouseY) {
        return mouseX > x + 2 && mouseX < x + width - 2 && mouseY > y + height - 15 && mouseY < y + height - 2;
    }

    @Override
    public void keyTyped(char typedChar, int keyCode) {
        try {
            super.keyTyped(typedChar,keyCode);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (!isTyping)
            return;
        if (keyCode == 14) {
            if (Keyboard.isKeyDown(Keyboard.KEY_LCONTROL))
                searchString = "";
            if ( searchString.length() > 0)
                searchString = searchString.substring(0,  searchString.length() - 1);
        } else if (keyCode == 28 || keyCode == 27) {
            isTyping = false;
        } else if (ChatAllowedCharacters.isAllowedCharacter(typedChar))
            searchString = ( searchString + "" + typedChar);
    }

    public void drawBarIcon() {
        GlStateManager.enableAlpha();
        Ruby.mc.getTextureManager().bindTexture(new ResourceLocation("textures/icons/folder.png"));
        GlStateManager.color(1f, 1f, 1f);
        GL11.glPushMatrix();
        GuiScreen.drawScaledCustomSizeModalRect(x + 2, y + 2, 0, 0, 14, 14, 14, 14, 14, 14);
        GL11.glPopMatrix();
        GlStateManager.disableAlpha();
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    public Timer timer = new Timer();

    public String idleSign() {
        if (timer.getTime(1000))
            timer.setTime(0);
        if (timer.getTime(500))
            return "_";
        return "";
    }
}
