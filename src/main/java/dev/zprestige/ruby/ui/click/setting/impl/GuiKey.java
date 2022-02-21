package dev.zprestige.ruby.ui.click.setting.impl;

import dev.zprestige.ruby.Ruby;
import dev.zprestige.ruby.module.client.NewGui;
import dev.zprestige.ruby.setting.impl.KeySetting;
import dev.zprestige.ruby.ui.click.MainScreen;
import dev.zprestige.ruby.ui.click.setting.GuiSetting;
import dev.zprestige.ruby.util.AnimationUtil;
import dev.zprestige.ruby.util.RenderUtil;
import org.lwjgl.input.Keyboard;

import java.awt.*;

public class GuiKey extends GuiSetting {
    KeySetting setting;
    int hoverAnimWidth;

    public GuiKey(KeySetting setting) {
        super(setting);
        this.setting = setting;
        this.hoverAnimWidth = 0;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY) {
        RenderUtil.drawRect(x, y, x + width, y + height, NewGui.Instance.backgroundColor.getValue().getRGB());
        RenderUtil.drawOutlineRect(x, y, x + width, y + height, NewGui.Instance.backgroundColor.getValue(), 1f);
        Ruby.rubyFont.drawStringWithShadow(setting.getName(), x + 1, y + (height / 2f) - (Ruby.rubyFont.getHeight(setting.getName()) / 2f), -1);
        if (setting.isOpen)
            Ruby.rubyFont.drawStringWithShadow(" " + MainScreen.idleSign(), x + width - Ruby.rubyFont.getStringWidth(setting.getValue().equals(-1) ? "None" : Keyboard.getKeyName(setting.getValue())) - 1, y + (height / 2f) - (Ruby.rubyFont.getHeight(setting.getValue().equals(-1) ? "None" : Keyboard.getKeyName(setting.getValue())) / 2f), Color.GRAY.getRGB());
        else
            Ruby.rubyFont.drawStringWithShadow(setting.getValue().equals(-1) ? "None" : Keyboard.getKeyName(setting.getValue()), x + width - Ruby.rubyFont.getStringWidth(setting.getValue().equals(-1) ? "None" : Keyboard.getKeyName(setting.getValue())) - 1, y + (height / 2f) - (Ruby.rubyFont.getHeight(setting.getValue().equals(-1) ? "None" : Keyboard.getKeyName(setting.getValue())) / 2f), Color.GRAY.getRGB());
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
        if (mouseButton == 0 && isInside(mouseX, mouseY))
            setting.isOpen = !setting.isOpen;
    }

    @Override
    public void keyTyped(char typedChar, int keyCode) {
        if (!setting.isOpen)
            return;
        if (keyCode == Keyboard.KEY_DELETE || keyCode == Keyboard.KEY_ESCAPE)
            setting.setValue(0);
        else
            setting.setValue(keyCode);

        setting.isOpen = !setting.isOpen;
    }
}
