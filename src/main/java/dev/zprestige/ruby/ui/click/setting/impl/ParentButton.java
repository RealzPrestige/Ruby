package dev.zprestige.ruby.ui.click.setting.impl;

import dev.zprestige.ruby.Ruby;
import dev.zprestige.ruby.module.client.ClickGui;
import dev.zprestige.ruby.settings.impl.Parent;
import dev.zprestige.ruby.ui.click.setting.Button;
import dev.zprestige.ruby.util.RenderUtil;

public class ParentButton extends Button {
    protected Parent parent;

    public ParentButton(Parent setting) {
        super(setting);
        this.parent = setting;
    }

    @Override
    public void render(int mouseX, int mouseY) {
        super.render(mouseX, mouseY);
        RenderUtil.drawRect(x, y, x + width, y + height, ClickGui.Instance.color.GetColor().getRGB());
        Ruby.fontManager.drawStringWithShadow(parent.getName(), x + (width / 2f) - (Ruby.fontManager.getStringWidth(parent.getName()) / 2f), getStringMiddle(parent.getName()), -1);
        Ruby.fontManager.drawStringWithShadow("...", x + width - 6, y + height - Ruby.fontManager.getFontHeight() - 3, -1);
        int i = parent.getChildren().stream().mapToInt(setting -> height + 1).sum();
        RenderUtil.drawOutlineRect(x, y, x + width, y + height + (parent.GetParent() ? i + 1 : 0), ClickGui.Instance.color.GetColor(), 2f);
        hover(mouseX, mouseY);
    }

    @Override
    public void click(int mouseX, int mouseY, int clickedButton) {
        if (isInside(mouseX, mouseY) && clickedButton == 1) {
            parent.setValue(!parent.GetParent());
        }
    }
}
