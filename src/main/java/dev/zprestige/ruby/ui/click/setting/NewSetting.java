package dev.zprestige.ruby.ui.click.setting;

import dev.zprestige.ruby.module.Module;
import dev.zprestige.ruby.newsettings.Setting;

public class NewSetting {
    protected Setting setting;
    protected int x, y, width, height;

    public NewSetting(Setting setting) {
        this.setting = setting;
    }

    public void render(int mouseX, int mouseY) {
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

    public int getY() {
        return y;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public boolean isInside(int mouseX, int mouseY) {
        return (mouseX > x && mouseX < x + width) && (mouseY > y && mouseY < y + height);
    }
}
