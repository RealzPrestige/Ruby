package dev.zprestige.ruby.ui.click.setting.impl;

import dev.zprestige.ruby.Ruby;
import dev.zprestige.ruby.module.client.NewGui;
import dev.zprestige.ruby.setting.impl.DoubleSetting;
import dev.zprestige.ruby.ui.click.setting.GuiSetting;
import dev.zprestige.ruby.util.AnimationUtil;
import dev.zprestige.ruby.util.RenderUtil;
import org.lwjgl.input.Mouse;

import java.awt.*;
import java.math.BigDecimal;
import java.math.RoundingMode;

public class GuiDouble extends GuiSetting {
    DoubleSetting setting;
    int hoverAnimWidth;

    public GuiDouble(DoubleSetting setting) {
        super(setting);
        this.setting = setting;
        this.hoverAnimWidth = 0;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY) {
        dragSlider(mouseX, mouseY);
        RenderUtil.drawRect(x, y, x + width, y + height, NewGui.Instance.backgroundColor.getValue().getRGB());
        RenderUtil.drawOutlineRect(x, y, x + width, y + height, NewGui.Instance.backgroundColor.getValue(), 1f);
        RenderUtil.drawRect(x, y, x + (((Number) setting.getValue()).floatValue() <= setting.getMinimum() ? 0 : ((float) width) * sliderWidthValue()), y + height, NewGui.Instance.color.getValue().getRGB());
        Ruby.rubyFont.drawStringWithShadow(setting.getName(), x + 1, y + (height / 2f) - (Ruby.rubyFont.getHeight(setting.getName() + " " + setting.getValue()) / 2f), -1);
        Ruby.rubyFont.drawStringWithShadow(roundNumber(setting.getValue(), 2) + "", x + width - Ruby.rubyFont.getStringWidth(setting.getValue() + "") - 1, y + (height / 2f) - (Ruby.rubyFont.getHeight(setting.getName() + " " + setting.getValue()) / 2f), Color.GRAY.getRGB());
        if (hoverAnimWidth > width - NewGui.Instance.animationSpeed.getValue() && hoverAnimWidth < width)
            hoverAnimWidth = width;
        if (isInside(mouseX, mouseY))
            hoverAnimWidth = AnimationUtil.increaseNumber(hoverAnimWidth, width, NewGui.Instance.animationSpeed.getValue());
        else
            hoverAnimWidth = AnimationUtil.decreaseNumber(hoverAnimWidth, 0, NewGui.Instance.animationSpeed.getValue());
        RenderUtil.drawRect(x, y, x + hoverAnimWidth, y + height, new Color(0, 0, 0, 50).getRGB());
    }

    public float sliderWidthValue() {
        return (float) ((((Number) setting.getValue()).floatValue() - setting.getMinimum()) / (setting.getMaximum() - setting.getMinimum()));
    }

    public void dragSlider(int mouseX, int mouseY) {
        if (isInsideExtended(mouseX, mouseY) && Mouse.isButtonDown(0))
            setSliderValue(mouseX);
    }

    public boolean isInsideExtended(int mouseX, int mouseY) {
        return mouseX > x - 4 && mouseX < x + width + 4 && mouseY > y && mouseY < y + height;
    }


    public void setSliderValue(int mouseX) {
        setting.setValue(setting.getMinimum());
        float diff = Math.min(width, Math.max(0, mouseX - x));
        float min = (float) setting.getMinimum();
        float max = (float) setting.getMaximum();
        if (diff == 0)
            setting.setValue(setting.getMinimum());
        else {
            float value = roundNumber(diff / width * (max - min) + min, 1);
            setting.setValue((double) value);
        }
    }

    public static float roundNumber(double value, int places) {
        if (places < 0)
            throw new IllegalArgumentException();
        BigDecimal decimal = BigDecimal.valueOf(value);
        decimal = decimal.setScale(places, RoundingMode.FLOOR);
        return decimal.floatValue();
    }
}
