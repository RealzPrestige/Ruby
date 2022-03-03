package dev.zprestige.ruby.ui.hudeditor;

import dev.zprestige.ruby.Ruby;
import dev.zprestige.ruby.module.client.ClickGui;
import dev.zprestige.ruby.ui.hudeditor.components.HudComponent;
import dev.zprestige.ruby.util.RenderUtil;

public class HudComponentScreen {
    protected final HudComponent hudComponent;
    protected float x, y, width, height;

    public HudComponentScreen(HudComponent hudComponent, float x, float y, float width, float height) {
        this.hudComponent = hudComponent;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public void release(int button) {
        if (check()){
            hudComponent.release(button);
        }
    }

    public void click(int mouseX, int mouseY, int button) {
        if (check()){
            hudComponent.click(mouseX, mouseY, button);
        }
        if (inside(mouseX, mouseY) && button == 0){
            hudComponent.setEnabled(!hudComponent.isEnabled());
        }
    }

    public void draw(int mouseX, int mouseY) {
        if (check()) {
            hudComponent.update(mouseX, mouseY);
        }
        RenderUtil.drawRect(x, y, x + width, y + height, hudComponent.isEnabled() ? ClickGui.Instance.color.GetColor().getRGB() : ClickGui.Instance.backgroundColor.GetColor().getRGB());
        RenderUtil.drawOutlineRect(x, y, x + width, y + height, ClickGui.Instance.backgroundColor.GetColor(), 1f);
        if (inside(mouseX, mouseY)){
            RenderUtil.drawRect(x, y, x + width, y + height, ClickGui.Instance.backgroundColor.GetColor().getRGB());
        }
        Ruby.fontManager.drawStringWithShadow(hudComponent.getName(), x + 2, y + height - Ruby.fontManager.getFontHeight() * 2, -1);
    }

    protected boolean inside(int mouseX, int mouseY) {
        return mouseX > x && mouseX < x + width && mouseY > y && mouseY < y + height;
    }

    protected boolean check() {
        return hudComponent.isEnabled();
    }

    public void setX(float x) {
        this.x = x;
    }

    public void setY(float y) {
        this.y = y;
    }
}
