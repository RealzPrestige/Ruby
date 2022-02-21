package dev.zprestige.ruby.ui.click.setting.impl;

import dev.zprestige.ruby.Ruby;
import dev.zprestige.ruby.module.client.NewGui;
import dev.zprestige.ruby.setting.impl.StringSetting;
import dev.zprestige.ruby.ui.click.MainScreen;
import dev.zprestige.ruby.ui.click.setting.GuiSetting;
import dev.zprestige.ruby.util.AnimationUtil;
import dev.zprestige.ruby.util.RenderUtil;
import net.minecraft.util.ChatAllowedCharacters;
import org.lwjgl.input.Keyboard;

import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

public class GuiString extends GuiSetting {
    StringSetting setting;
    int hoverAnimWidth;

    public GuiString(StringSetting setting) {
        super(setting);
        this.setting = setting;
        this.hoverAnimWidth = 0;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY) {
        RenderUtil.drawRect(x, y, x + width, y + height, NewGui.Instance.backgroundColor.getValue().getRGB());
        RenderUtil.drawOutlineRect(x, y, x + width, y + height, NewGui.Instance.backgroundColor.getValue(), 1f);
        Ruby.rubyFont.drawStringWithShadow(setting.getName(), x, y + (height / 2f) - (Ruby.rubyFont.getHeight(setting.getName()) / 2f), -1);
        Ruby.rubyFont.drawStringWithShadow(setting.isOpen() ? setting.getValue() + MainScreen.idleSign() : setting.getValue(), x + width - Ruby.rubyFont.getStringWidth(setting.getValue()) - 1, y + (height / 2f) - (Ruby.rubyFont.getHeight(setting.getValue()) / 2f), Color.GRAY.getRGB());
        if (hoverAnimWidth > width - NewGui.Instance.animationSpeed.getValue() && hoverAnimWidth < width)
            hoverAnimWidth = width;
        if (isInside(mouseX, mouseY))
            hoverAnimWidth = AnimationUtil.increaseNumber(hoverAnimWidth, width, NewGui.Instance.animationSpeed.getValue());
        else
            hoverAnimWidth = AnimationUtil.decreaseNumber(hoverAnimWidth, 0, NewGui.Instance.animationSpeed.getValue());
        RenderUtil.drawRect(x, y, x + hoverAnimWidth, y + height, new Color(0, 0, 0, 50).getRGB());
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (isInside(mouseX, mouseY) && mouseButton == 0)
            setting.setOpen(!setting.isOpen());
    }

    @Override
    public void keyTyped(char typedChar, int keyCode) {
        if (!setting.isOpen())
            return;
        if (Keyboard.isKeyDown(17) && keyCode == 86){
            try {
                setting.setValue(readClipboard());
            } catch (Exception ignored){
            }
        }
        if (keyCode == 14) {
            if (Keyboard.isKeyDown(Keyboard.KEY_LCONTROL))
                setting.setValue("");
            if (setting.getValue() != null && setting.getValue().length() > 0)
                setting.setValue(setting.getValue().substring(0, setting.getValue().length() - 1));
        } else if (keyCode == 28 || keyCode == 27) {
            setting.setOpen(false);
        } else if (ChatAllowedCharacters.isAllowedCharacter(typedChar))
            setting.setValue(setting.getValue() + "" + typedChar);
    }

    public static String readClipboard() {
        try {
            return (String) Toolkit.getDefaultToolkit().getSystemClipboard().getData(DataFlavor.stringFlavor);
        } catch (IOException | UnsupportedFlavorException exception) {
            return null;
        }

    }
}
