package dev.zprestige.ruby.ui.click.setting.newsettings;

import dev.zprestige.ruby.Ruby;
import dev.zprestige.ruby.module.client.ClickGui;
import dev.zprestige.ruby.newsettings.impl.ComboBox;
import dev.zprestige.ruby.ui.click.setting.NewSetting;
import dev.zprestige.ruby.util.RenderUtil;

import java.awt.*;
import java.util.stream.IntStream;

public class ComboBoxButton extends NewSetting {
    protected ComboBox comboBox;

    public ComboBoxButton(ComboBox setting) {
        super(setting);
        this.comboBox = setting;
    }

    @Override
    public void render(int mouseX, int mouseY) {
        super.render(mouseX, mouseY);
        final String name = comboBox.getName();
        final float middleHeight = getStringMiddle(name);
        Ruby.rubyFont.drawStringWithShadow(name, x, middleHeight, -1);
        final String value = comboBox.GetCombo();
        Ruby.rubyFont.drawStringWithShadow(value, x + Ruby.rubyFont.getStringWidth(name + "  "), getStringMiddle(value), Color.GRAY.getRGB());
        float start = x + width / 2f;
        for (String ignored : comboBox.getValues()){
            start -= 3.0f;
        }
        float deltaX = start;
        for (String string : comboBox.getValues()){
            RenderUtil.drawRect(deltaX, y + height - 1, deltaX + 5, y + height, ClickGui.Instance.color.GetColor().getRGB());
            if (comboBox.GetCombo().equals(string)){
                RenderUtil.drawOutlineRect(deltaX, y + height -1, deltaX + 5, y + height, Color.WHITE, 1.0f);
            }
            deltaX += 6.0f;
        }
    }

    public int getIndex() {
        return IntStream.range(0, comboBox.getValues().length).filter(i -> comboBox.getValues()[i].equals(comboBox.GetCombo())).findFirst().orElse(-1);
    }

    @Override
    public void click(int mouseX, int mouseY, int clickedButton) {
        if (isInside(mouseX, mouseY)) {
            int max = comboBox.getValues().length;
            int index = getIndex();
            switch (clickedButton) {
                case 0:
                    if (index + 1 >= max) {
                        index = 0;
                    } else {
                        ++index;
                    }
                    try {
                        comboBox.setValue(comboBox.getValues()[index]);
                    } catch (Exception ignored) {
                    }
                    break;
                case 1:
                    if (index - 1 < 0) {
                        index = max - 1;
                    } else {
                        --index;
                    }
                    try {
                        comboBox.setValue(comboBox.getValues()[index]);
                    } catch (Exception ignored) {
                    }
                    break;
            }
        }
    }
}
