package dev.zprestige.ruby.ui.click.setting;

import dev.zprestige.ruby.setting.Setting;

public class GuiSetting {
    public int x;
    public int y;
    public int width;
    public int height;
    Setting<?> setting;

    public GuiSetting(Setting<?> setting){
        this.setting = setting;
    }

    public void drawScreen(int mouseX, int mouseY) {
    }

    public void mouseClicked(int mouseX, int mouseY, int clickedButton) {
    }

    public void keyTyped(char typedChar, int keyCode) {
    }

    public void mouseReleased(int mouseX, int mouseY, int releaseButton) {
    }

    public void setX(int x){
        this.x = x;
    }

    public void setY(int y){
        this.y = y;
    }

    public void setWidth(int width){
        this.width = width;
    }

    public void setHeight(int height){
        this.height = height;
    }

    public boolean isVisible() {
        return setting.isVisible();
    }

    public Setting<?> getSetting(){
        return setting;
    }

    public boolean isInside(int mouseX, int mouseY) {
        return (mouseX > x && mouseX < x + width) && (mouseY > y && mouseY < y + height);
    }
}
