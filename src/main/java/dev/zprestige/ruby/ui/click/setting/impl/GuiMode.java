package dev.zprestige.ruby.ui.click.setting.impl;

import dev.zprestige.ruby.Ruby;
import dev.zprestige.ruby.module.client.NewGui;
import dev.zprestige.ruby.setting.impl.ModeSetting;
import dev.zprestige.ruby.ui.click.setting.GuiSetting;
import dev.zprestige.ruby.util.AnimationUtil;
import dev.zprestige.ruby.util.RenderUtil;

import java.awt.*;

public class GuiMode extends GuiSetting {
    ModeSetting setting;
    int index;
    int hoverAnimWidth;

    public GuiMode(ModeSetting setting) {
        super(setting);
        this.setting = setting;
        this.index = setting.getModes().indexOf(setting.getValue());
        this.hoverAnimWidth = 0;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY) {
        RenderUtil.drawRect(x, y, x + width, y + height, NewGui.Instance.backgroundColor.getValue().getRGB());
        RenderUtil.drawOutlineRect(x, y, x + width, y + height, NewGui.Instance.backgroundColor.getValue(), 1f);
        Ruby.rubyFont.drawStringWithShadow(setting.getName(), x + 1, y + (height / 2f) - (Ruby.rubyFont.getHeight(setting.getName()) / 2f), -1);
        Ruby.rubyFont.drawStringWithShadow(setting.getValue(), x + width - Ruby.rubyFont.getStringWidth(setting.getValue()) - 1, y + (height / 2f) - (Ruby.rubyFont.getHeight(setting.getValue()) / 2f), Color.GRAY.getRGB());
        if (hoverAnimWidth > width - NewGui.Instance.animationSpeed.getValue() && hoverAnimWidth < width)
            hoverAnimWidth = width;
        if (isInside(mouseX, mouseY))
            hoverAnimWidth = AnimationUtil.increaseNumber(hoverAnimWidth, width, NewGui.Instance.animationSpeed.getValue());
        else
            hoverAnimWidth = AnimationUtil.decreaseNumber(hoverAnimWidth, 0, NewGui.Instance.animationSpeed.getValue());
        RenderUtil.drawRect(x, y, x + hoverAnimWidth, y + height, new Color(0, 0, 0, 50).getRGB());
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int clickedButton) {
        if (isInside(mouseX, mouseY) && clickedButton == 0) {
            int max = setting.getModes().size();
            if (index + 1 >= max)
                index = 0;
            else
                ++index;
            try {
                setting.setValue(setting.getModes().get(index));
            } catch (Exception ignored) {
            }
        }
    }
}