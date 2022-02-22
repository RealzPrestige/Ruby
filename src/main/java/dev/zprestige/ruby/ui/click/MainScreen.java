package dev.zprestige.ruby.ui.click;

import dev.zprestige.ruby.Ruby;
import dev.zprestige.ruby.module.Module;
import dev.zprestige.ruby.module.client.NewGui;
import dev.zprestige.ruby.setting.impl.KeySetting;
import dev.zprestige.ruby.ui.buttons.BlurButton;
import dev.zprestige.ruby.ui.buttons.ConfigButton;
import dev.zprestige.ruby.ui.click.setting.impl.GuiColor;
import dev.zprestige.ruby.util.RenderUtil;
import dev.zprestige.ruby.util.Timer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import java.io.IOException;
import java.util.ArrayList;

public class MainScreen extends GuiScreen {
    public ArrayList<GuiCategory> guiCategories = new ArrayList<>();
    public BlurButton blurButton;
    public ConfigButton configButton;
    public int deltaX;
    public boolean isBlurred;

    public MainScreen() {
        deltaX = 26;
        blurButton = new BlurButton(NewGui.Instance.color.getValue(), 929, 509, 30, 30);
        configButton = new ConfigButton(NewGui.Instance.color.getValue(), 897, 509, 30, 30);
        Ruby.moduleManager.getCategories().forEach(category -> guiCategories.add(new GuiCategory(category, deltaX += 101, 2, 100, 13)));
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        {
            RenderUtil.drawOutlineRect(-1, 507, 1000, 600, NewGui.Instance.color.getValue(), 2f);
            RenderUtil.drawRect(-1, 507, 1000, 600, NewGui.Instance.backgroundColor.getValue().getRGB());
        }
        {
            GlStateManager.enableAlpha();
            Ruby.mc.getTextureManager().bindTexture(new ResourceLocation("textures/icons/ruby.png"));
            GlStateManager.color(1f, 1f, 1f);
            GL11.glPushMatrix();
            GuiScreen.drawScaledCustomSizeModalRect(2, 511, 0, 0, 68, 28, 68, 28, 68, 28);
            GlStateManager.disableAlpha();
            GL11.glPopMatrix();
        }
        blurButton.drawScreen(mouseX, mouseY, partialTicks);
        configButton.drawScreen(mouseX, mouseY);
        guiCategories.forEach(newCategory -> newCategory.drawScreen(mouseX, mouseY));
        for (GuiCategory newCategory : guiCategories) {
            for (GuiModule newModule : newCategory.newModuleArrayList) {
                newModule.newSettings.stream().filter(newSetting -> newSetting.isInside(mouseX, mouseY) && getGuiModuleByModule(newSetting.getSetting().getModule()).isOpened).forEach(newSetting -> {
                    if (newSetting.getSetting() instanceof KeySetting) {
                        KeySetting setting = (KeySetting) newSetting.getSetting();
                        Ruby.rubyFont.drawStringWithShadow(newSetting.getSetting().name + " | " + (setting.getValue().equals(-1) ? "None" : Keyboard.getKeyName(setting.getValue())), new ScaledResolution(Minecraft.getMinecraft()).getScaledWidth() / 2f - (Ruby.rubyFont.getStringWidth((setting.getValue().equals(-1) ? "None" : Keyboard.getKeyName(setting.getValue()))) / 2f), 530 - (Ruby.rubyFont.getHeight((setting.getValue().equals(-1) ? "None" : Keyboard.getKeyName(setting.getValue()))) / 2f), -1);
                    } else if (newSetting instanceof GuiColor) {
                        Ruby.rubyFont.drawStringWithShadow(newSetting.getSetting().name + " | " + newSetting.getSetting().getValue().toString().replaceAll("java.awt.Color", "").replace("[", "").replace("]", "").replace("=", " ").replace(",", " - "), new ScaledResolution(Minecraft.getMinecraft()).getScaledWidth() / 2f - (Ruby.rubyFont.getStringWidth(newSetting.getSetting().name + " | " + newSetting.getSetting().getValue().toString().replaceAll("java.awt.Color", "").replace("[", "").replace("]", "").replace("=", " ").replace(",", " - ")) / 2f), 530 - (Ruby.rubyFont.getHeight(newSetting.getSetting().name + " | " + newSetting.getSetting().getValue().toString().replaceAll("java.awt.Color", "").replace("[", "").replace("]", "").replace("=", " ").replace(",", " - ")) / 2f), -1);
                    } else {
                        Ruby.rubyFont.drawStringWithShadow(newSetting.getSetting().name + " | " + newSetting.getSetting().getValue(), new ScaledResolution(Minecraft.getMinecraft()).getScaledWidth() / 2f - (Ruby.rubyFont.getStringWidth(newSetting.getSetting().name + " | " + newSetting.getSetting().getValue()) / 2f), 530 - (Ruby.rubyFont.getHeight(newSetting.getSetting().name + " | " + newSetting.getSetting().getValue()) / 2f), -1);
                    }
                });
            }
        }
        if (OpenGlHelper.shadersSupported && mc.getRenderViewEntity() instanceof EntityPlayer && blurButton.isEnabled() && !isBlurred) {
            if (mc.entityRenderer.getShaderGroup() != null)
                mc.entityRenderer.getShaderGroup().deleteShaderGroup();
            isBlurred = true;
            mc.entityRenderer.loadShader(new ResourceLocation("shaders/post/blur.json"));
        } else if (mc.entityRenderer.getShaderGroup() != null) {
            mc.entityRenderer.getShaderGroup().deleteShaderGroup();
            isBlurred = false;
        }
    }

    protected GuiModule getGuiModuleByModule(Module module){
        return guiCategories.stream().filter(guiCategory -> guiCategory.category.equals(module.getCategory())).flatMap(guiCategory -> guiCategory.newModuleArrayList.stream()).findFirst().orElse(null);
    }

    @Override
    public void onGuiClosed() {
        if (mc.entityRenderer.getShaderGroup() != null)
            mc.entityRenderer.getShaderGroup().deleteShaderGroup();
    }

    @Override
    public void keyTyped(char typedChar, int keyCode) throws IOException {
        super.keyTyped(typedChar, keyCode);
        guiCategories.forEach(newCategory -> newCategory.keyTyped(typedChar, keyCode));
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        blurButton.mouseClicked(mouseX, mouseY, mouseButton);
        configButton.mouseClicked(mouseX, mouseY, mouseButton);
        guiCategories.forEach(newCategory -> newCategory.mouseClicked(mouseX, mouseY, mouseButton));
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int state) {
        guiCategories.forEach(newCategory -> newCategory.mouseReleased(mouseX, mouseY, state));
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }

    public static Timer timer = new Timer();

    public static String idleSign() {
        if (timer.getTime(1000))
            timer.setTime(0);
        if (timer.getTime(500))
            return "_";
        return "";
    }
}
