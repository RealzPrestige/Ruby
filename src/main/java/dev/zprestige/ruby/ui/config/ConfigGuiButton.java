package dev.zprestige.ruby.ui.config;

import com.mojang.realmsclient.gui.ChatFormatting;
import dev.zprestige.ruby.Ruby;
import dev.zprestige.ruby.module.client.Configs;
import dev.zprestige.ruby.module.client.NewGui;
import dev.zprestige.ruby.util.AnimationUtil;
import dev.zprestige.ruby.util.MessageUtil;
import dev.zprestige.ruby.util.RenderUtil;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;

public class ConfigGuiButton {
    public String name;
    public int x, y, width, height, animX, scrollY;
    public ArrayList<icon> icons = new ArrayList<>();

    public ConfigGuiButton(String name, int x, int y, int width, int height) {
        this.name = name;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        icons.add(new icon(iconType.Save, new ResourceLocation("textures/icons/save.png"), x + width, y));
        icons.add(new icon(iconType.Load, new ResourceLocation("textures/icons/load.png"), x + width + 15, y));
        icons.add(new icon(iconType.Delete, new ResourceLocation("textures/icons/delete.png"), x + width + 30, y));
    }

    public void render(int mouseX, int mouseY) {
        {
            if (isInside(mouseX, mouseY)){
                animX = AnimationUtil.increaseNumber(animX, 45, 1);
            } else {
                animX = AnimationUtil.decreaseNumber(animX, 0, 1);
            }
            for (icon icon : icons) {
                switch (icon.iconType) {
                    case Save:
                        icon.x = x + width - animX;
                        break;
                    case Load:
                        icon.x = x + width - animX + 15;
                        break;
                    case Delete:
                        icon.x = x + width - animX + 30;
                }

                icon.y = y;
            }
        }
        {
            RenderUtil.drawRect(x, y, x + width, y + height, NewGui.Instance.backgroundColor.getValue().getRGB());
            RenderUtil.drawOutlineRect(x, y, x + width, y + height, NewGui.Instance.backgroundColor.getValue(), 1f);
            Ruby.rubyFont.drawStringWithShadow(name, x + (isInside(mouseX, mouseY) ? 3 : 2), y + (height / 2f) - (Ruby.rubyFont.getHeight(name) / 2f), -1);
        }
        {
            if (isInside(mouseX, mouseY))
                RenderUtil.drawRect(x, y, x + width, y + height, NewGui.Instance.backgroundColor.getValue().getRGB());
        }
        {
            icons.forEach(icon -> icon.render(mouseX, mouseY));
        }
    }

    public void mouseClicked(int mouseX, int mouseY, int clickedButton) {
        if (isInside(mouseX, mouseY) && clickedButton == 0) {
            Ruby.configManager.load(name, false);
            Configs.Instance.enableModule();
        }
        icons.forEach(icon -> icon.mouseClicked(mouseX, mouseY, clickedButton));
    }

    public boolean isInside(int mouseX, int mouseY) {
        return mouseX > x && mouseX < x + width && mouseY > y && mouseY < y + height;
    }

    public class icon {
        iconType iconType;
        ResourceLocation resourceLocation;
        int x, y;

        public icon(iconType iconType, ResourceLocation resourceLocation, int x, int y) {
            this.iconType = iconType;
            this.resourceLocation = resourceLocation;
            this.x = x;
            this.y = y;
        }

        public void render(int mouseX, int mouseY) {
            {
                GlStateManager.enableAlpha();
                Ruby.mc.getTextureManager().bindTexture(resourceLocation);
                GlStateManager.color(1f, 1f, 1f);
                GL11.glPushMatrix();
                GuiScreen.drawScaledCustomSizeModalRect(x + 2, y, 0, 0, 14, 14, 14, 14, 14, 14);
                GL11.glPopMatrix();
                GlStateManager.disableAlpha();
            }
            {
                if (isInside(mouseX, mouseY)) {
                    RenderUtil.drawRect(mouseX + 5, mouseY - 5, mouseX + 6 + Ruby.rubyFont.getStringWidth(iconType.toString()), mouseY + 8, NewGui.Instance.backgroundColor.getValue().getRGB());
                    RenderUtil.drawOutlineRect(mouseX + 5, mouseY - 5, mouseX + 6 + Ruby.rubyFont.getStringWidth(iconType.toString()), mouseY + 8, NewGui.Instance.backgroundColor.getValue(), 1f);
                    Ruby.rubyFont.drawStringWithShadow(iconType.toString(), mouseX + 1, mouseY - 5 + (13 / 2f) - (Ruby.rubyFont.getHeight(iconType.toString()) / 2f), -1);
                }
            }
        }

        public void mouseClicked(int mouseX, int mouseY, int clickedButton) {
            if (isInside(mouseX, mouseY) && clickedButton == 0) {
                switch (iconType) {
                    case Save:
                        Ruby.configManager.save(name, false);
                        MessageUtil.sendMessage(ChatFormatting.WHITE + "Successfully saved " + ChatFormatting.GRAY + name + ChatFormatting.WHITE + ".");
                        break;
                    case Load:
                        Ruby.configManager.load(name, false);
                        MessageUtil.sendMessage(ChatFormatting.WHITE + "Successfully loaded " + ChatFormatting.GRAY + name + ChatFormatting.WHITE + ".");
                        break;
                    case Delete:
                        //Ruby.configManager.deleteFolder(name);
                        MessageUtil.sendMessage(ChatFormatting.WHITE + "Successfully deleted " + ChatFormatting.GRAY + name + ChatFormatting.WHITE + ".");
                        break;
                }
            }
        }

        public boolean isInside(int mouseX, int mouseY) {
            return mouseX > x && mouseX < x + 15 && mouseY > y && mouseY < y + height;
        }
    }

    public enum iconType {
        Save,
        Load,
        Delete
    }
}
