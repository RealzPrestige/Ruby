package dev.zprestige.ruby.ui.click.setting;

import dev.zprestige.ruby.Ruby;
import dev.zprestige.ruby.module.Module;
import dev.zprestige.ruby.module.client.ClickGui;
import dev.zprestige.ruby.settings.Setting;
import dev.zprestige.ruby.util.RenderUtil;

import java.awt.*;

public class Button {
    protected Setting setting;
    protected int x, y, width, height;

    public Button(Setting setting) {
        this.setting = setting;
    }

    public void render(int mouseX, int mouseY) {
        RenderUtil.drawRect(x, y, x + width, y + height, ClickGui.Instance.backgroundColor.GetColor().getRGB());
        RenderUtil.drawOutlineRect(x, y, x + width, y + height, ClickGui.Instance.backgroundColor.GetColor(), 1f);
        if (isInside(mouseX, mouseY)) {
            RenderUtil.drawRect(x, y, x + width, y + height, ClickGui.Instance.backgroundColor.GetColor().getRGB());
        }
    }

    public void hover(int mouseX, int mouseY) {
        if (isInside(mouseX, mouseY)) {
            RenderUtil.drawRect(x, y, x + width, y + height, new Color(0, 0, 0, 50).getRGB());
        }
    }

    public void click(int mouseX, int mouseY, int clickedButton) {
    }

    public void type(char typedChar, int keyCode) {
    }

    public void release(int mouseX, int mouseY, int releaseButton) {
    }

    public Setting getSetting() {
        return setting;
    }

    public Module getModule() {
        return setting.getModule();
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public boolean isInside(int mouseX, int mouseY) {
        return (mouseX > x && mouseX < x + width) && (mouseY > y && mouseY < y + height);
    }

    public float getStringMiddle(String string) {
        return y + (height / 2f) - (Ruby.fontManager.getFontHeight() / 2f);
    }
}
