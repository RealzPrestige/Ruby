package dev.zprestige.ruby.ui.click.setting.impl;

import dev.zprestige.ruby.Ruby;
import dev.zprestige.ruby.module.client.NewGui;
import dev.zprestige.ruby.setting.Setting;
import dev.zprestige.ruby.setting.impl.ColorSetting;
import dev.zprestige.ruby.setting.impl.ParentSetting;
import dev.zprestige.ruby.ui.click.setting.GuiSetting;
import dev.zprestige.ruby.util.AnimationUtil;
import dev.zprestige.ruby.util.RenderUtil;

import java.awt.*;

public class GuiParent extends GuiSetting {
    ParentSetting setting;
    int hoverAnimWidth;

    public GuiParent(ParentSetting setting) {
        super(setting);
        this.setting = setting;
        this.hoverAnimWidth = 0;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY) {
        RenderUtil.drawRect(x, y, x + width, y + height, NewGui.Instance.color.getValue().getRGB());
        int i = 0;
        for (Setting<?> setting : setting.getChildren()) {
            if (!setting.isVisibleExcludingParent())
                continue;
            i += height + 1;
            if (setting instanceof ColorSetting && setting.isOpen)
                i += 109;
        }
        RenderUtil.drawOutlineRect(x, y, x + width, y + height + (setting.getValue() ? i + 1 : 0), NewGui.Instance.color.getValue(), 2f);
        Ruby.rubyFont.drawStringWithShadow(setting.getName(), x + 1, y + (height / 2f) - (Ruby.rubyFont.getHeight(setting.getName()) / 2f), -1);
        if (!setting.getValue())
            Ruby.rubyFont.drawStringWithShadow("...", x + width - 6, y + height - Ruby.rubyFont.getHeight("...") - 3, -1);
        {
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
    public void mouseClicked(int mouseX, int mouseY, int clickedButton) {
        if (isInside(mouseX, mouseY) && clickedButton == 1)
            setting.setValue(!setting.getValue());
    }
}
