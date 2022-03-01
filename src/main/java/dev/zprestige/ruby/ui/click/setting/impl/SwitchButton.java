package dev.zprestige.ruby.ui.click.setting.impl;

import dev.zprestige.ruby.Ruby;
import dev.zprestige.ruby.module.client.ClickGui;
import dev.zprestige.ruby.settings.impl.Switch;
import dev.zprestige.ruby.ui.click.setting.Button;
import dev.zprestige.ruby.util.RenderUtil;

public class SwitchButton extends Button {
    protected Switch aSwitch;

    public SwitchButton(Switch setting) {
        super(setting);
        this.aSwitch = setting;
    }

    @Override
    public void render(int mouseX, int mouseY) {
        super.render(mouseX, mouseY);
        RenderUtil.drawOutlineRect(x + width - 11, y + 2, x + width - 2, y + height - 2, ClickGui.Instance.color.GetColor(), 1.0f);
        if (aSwitch.GetSwitch()) {
            RenderUtil.drawRect(x + width - 10, y + 3, x + width - 3, y + height - 3, ClickGui.Instance.color.GetColor().getRGB());
            RenderUtil.drawOutlineRect(x + width - 10, y + 3, x + width - 3, y + height - 3, ClickGui.Instance.backgroundColor.GetColor(), 1.0f);
        }
        final String name = aSwitch.getName();
        Ruby.fontManager.drawStringWithShadow(name, x + 2, y + (height / 2f) - (Ruby.fontManager.getFontHeight() / 2f), -1);
        hover(mouseX, mouseY);
    }

    @Override
    public void click(int mouseX, int mouseY, int clickedButton) {
        if (clickedButton == 0 && insideBox(mouseX, mouseY)) {
            aSwitch.setValue(!aSwitch.GetSwitch());
        }
    }

    protected boolean insideBox(int mouseX, int mouseY) {
        return mouseX > x + width - 12 && mouseX < x + width - 2 && mouseY > y + 2 && mouseY < y + height - 2;
    }
}
