package dev.zprestige.ruby.ui.click.setting.impl;

import dev.zprestige.ruby.Ruby;
import dev.zprestige.ruby.module.client.NewGui;
import dev.zprestige.ruby.setting.impl.BooleanSetting;
import dev.zprestige.ruby.ui.click.setting.GuiSetting;
import dev.zprestige.ruby.util.AnimationUtil;
import dev.zprestige.ruby.util.RenderUtil;

import java.awt.*;

public class GuiBoolean extends GuiSetting {
    BooleanSetting setting;
    int animWidth, hoverAnimWidth;

    public GuiBoolean(BooleanSetting setting) {
        super(setting);
        this.setting = setting;
        this.animWidth = setting.getValue() ? width : 0;
        this.hoverAnimWidth = 0;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY) {
        RenderUtil.drawRect(x, y, x + width, y + height, NewGui.Instance.backgroundColor.getValue().getRGB());
        RenderUtil.drawOutlineRect(x, y, x + width, y + height, NewGui.Instance.backgroundColor.getValue(), 1f);
        if (setting.getValue()) {
            animWidth = AnimationUtil.increaseNumber(animWidth, width, 2);
        } else {
            animWidth = AnimationUtil.increaseNumber(animWidth, 0, 2);
        }
        RenderUtil.drawRect(x, y, x + animWidth, y + height, NewGui.Instance.color.getValue().getRGB());
        Ruby.rubyFont.drawStringWithShadow(setting.getName(), x + 1, y + (height / 2f) - (Ruby.rubyFont.getHeight(setting.getName()) / 2f), -1);
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
        if (isInside(mouseX, mouseY) && clickedButton == 0)
            setting.setValue(!setting.getValue());
    }
}