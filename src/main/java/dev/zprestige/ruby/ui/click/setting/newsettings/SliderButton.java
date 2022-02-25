package dev.zprestige.ruby.ui.click.setting.newsettings;

import dev.zprestige.ruby.Ruby;
import dev.zprestige.ruby.module.client.ClickGui;
import dev.zprestige.ruby.settings.impl.Slider;
import dev.zprestige.ruby.ui.click.setting.NewSetting;
import dev.zprestige.ruby.util.RenderUtil;
import org.lwjgl.input.Mouse;

import java.awt.*;
import java.math.BigDecimal;
import java.math.RoundingMode;

public class SliderButton extends NewSetting {
    protected Slider slider;
    protected int extension;

    public SliderButton(Slider setting) {
        super(setting);
        slider = setting;
    }

    @Override
    public void render(int mouseX, int mouseY) {
        super.render(mouseX, mouseY);
        dragSlider(mouseX, mouseY);
        float sliderWidth = width * sliderWidthValue();
        RenderUtil.drawRect(x, y, x + sliderWidth, y + height, ClickGui.Instance.color.GetColor().getRGB());
        final String name = slider.getName();
        Ruby.rubyFont.drawStringWithShadow(name, x + 2, getStringMiddle(name), -1);
        Ruby.rubyFont.drawStringWithShadow(slider.GetSlider() + "", x + 2 + Ruby.rubyFont.getStringWidth(name + " "), getStringMiddle(name), Color.GRAY.getRGB());
        hover(mouseX, mouseY);
    }

    protected float sliderWidthValue() {
        return (slider.GetSlider() - slider.getMin()) / (slider.getMax() - slider.getMin());
    }

    protected void dragSlider(int mouseX, int mouseY) {
        if (isInsideExtended(mouseX, mouseY) && Mouse.isButtonDown(0)) {
            setSliderValue(mouseX);
            extension = 400;
        } else {
            extension = 0;
        }
    }

    protected void setSliderValue(int mouseX) {
        slider.setValue(slider.getMin());
        final float diff = Math.min(width, Math.max(0, mouseX - x));
        final float min = slider.getMin();
        final float max = slider.getMax();
        slider.setValue(diff == 0 ? slider.getMin() : roundNumber(diff / width * (max - min) + min));
    }

    protected float roundNumber(double value) {
        BigDecimal decimal = BigDecimal.valueOf(value);
        decimal = decimal.setScale(1, RoundingMode.FLOOR);
        return decimal.floatValue();
    }

    protected boolean isInsideExtended(int mouseX, int mouseY) {
        return mouseX > x - extension && mouseX < x + width + extension && mouseY > y - extension && mouseY < y + height + extension;
    }

}