package dev.zprestige.ruby.ui.click.setting.newsettings;

import dev.zprestige.ruby.Ruby;
import dev.zprestige.ruby.settings.impl.Key;
import dev.zprestige.ruby.ui.click.MainScreen;
import dev.zprestige.ruby.ui.click.setting.NewSetting;
import org.lwjgl.input.Keyboard;

import java.awt.*;

public class KeyButton extends NewSetting {
    protected Key key;
    protected boolean opened;

    public KeyButton(Key setting) {
        super(setting);
        key = setting;
    }

    @Override
    public void render(int mouseX, int mouseY) {
        super.render(mouseX, mouseY);
        final String name = key.getName();
        Ruby.rubyFont.drawStringWithShadow(name, x + 2, getStringMiddle(name), -1);
        final String closedText = opened ? " " + MainScreen.idleSign() : key.GetKey() == -1 ? "None" : Keyboard.getKeyName(key.GetKey());
        final float nameWidth = Ruby.rubyFont.getStringWidth(name + " ");
        Ruby.rubyFont.drawStringWithShadow(closedText, x + 2 + nameWidth, getStringMiddle(closedText), Color.GRAY.getRGB());
        hover(mouseX, mouseY);
    }

    @Override
    public void click(int mouseX, int mouseY, int mouseButton) {
        if (mouseButton == 0 && isInside(mouseX, mouseY)) {
            opened = !opened;
        }
    }


    @Override
    public void type(char typedChar, int keyCode) {
        if (opened) {
            key.setValue(keyCode == Keyboard.KEY_DELETE || keyCode == Keyboard.KEY_ESCAPE ? -1 : keyCode);
            opened = !opened;
        }
    }

}
