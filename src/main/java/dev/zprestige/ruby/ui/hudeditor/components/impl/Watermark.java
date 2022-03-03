package dev.zprestige.ruby.ui.hudeditor.components.impl;

import dev.zprestige.ruby.Ruby;
import dev.zprestige.ruby.module.client.ClickGui;
import dev.zprestige.ruby.ui.hudeditor.components.HudComponent;
import dev.zprestige.ruby.util.RenderUtil;

public class Watermark extends HudComponent {

    public Watermark() {
        super("Watermark", 0, 0, Ruby.fontManager.getStringWidth("Ruby"), Ruby.fontManager.getFontHeight());
    }

    @Override
    public void render() {
        final String text = "Ruby";
        Ruby.fontManager.drawStringWithShadow(text, x, y, -1);
        RenderUtil.drawRect(x, y, x + width,y + height, ClickGui.Instance.color.GetColor());
        setWidth(Ruby.fontManager.getStringWidth(text));
        setHeight(Ruby.fontManager.getFontHeight());
    }
}
