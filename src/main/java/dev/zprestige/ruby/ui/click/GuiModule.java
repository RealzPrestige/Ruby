package dev.zprestige.ruby.ui.click;

import com.mojang.realmsclient.gui.ChatFormatting;
import dev.zprestige.ruby.Ruby;
import dev.zprestige.ruby.module.Module;
import dev.zprestige.ruby.module.client.ClickGui;
import dev.zprestige.ruby.newsettings.impl.*;
import dev.zprestige.ruby.ui.click.setting.NewSetting;
import dev.zprestige.ruby.ui.click.setting.newsettings.*;
import dev.zprestige.ruby.util.AnimationUtil;
import dev.zprestige.ruby.util.RenderUtil;

import java.awt.*;
import java.util.ArrayList;

import static org.lwjgl.opengl.GL11.*;

public class GuiModule {
    public Module module;
    public int x, y, width, height, deltaY, animDeltaY;
    public boolean isOpened = false;
    public float animWidth, hoverAnimWidth;
    public ArrayList<NewSetting> settings = new ArrayList<>();

    public GuiModule(Module module, int x, int y, int width, int height) {
        this.module = module;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.animWidth = module.isEnabled() ? width : 0.0f;
        this.hoverAnimWidth = 0.0f;
        module.getNewSettings().stream().filter(setting -> !setting.getName().equals("Enabled")).forEach(setting -> {
            if (setting instanceof Switch) {
                settings.add(new SwitchButton((Switch) setting));
            }
            if (setting instanceof Slider) {
                settings.add(new SliderButton((Slider) setting));
            }
            if (setting instanceof Key) {
                settings.add(new KeyButton((Key) setting));
            }
            if (setting instanceof ColorBox) {
                settings.add(new ColorButton((ColorBox) setting));
            }
            if (setting instanceof ComboBox) {
                settings.add(new ComboBoxButton((ComboBox) setting));
            }
            if (setting instanceof ColorSwitch) {
                settings.add(new ColorSwitchButton((ColorSwitch) setting));
            }
            if (setting instanceof Parent){
                settings.add(new ParentButton((Parent) setting));
            }
        });
        animDeltaY = height + 1;
    }

    public void drawScreen(int mouseX, int mouseY) {
        {
            RenderUtil.drawRect(x, y, x + width, y + height, ClickGui.Instance.backgroundColor.GetColor().getRGB());
            RenderUtil.drawOutlineRect(x, y, x + width, y + height, ClickGui.Instance.backgroundColor.GetColor(), 1f);
            if (module.isEnabled())
                animWidth = AnimationUtil.increaseNumber(animWidth, width, 1);
            else
                animWidth = AnimationUtil.decreaseNumber(animWidth, 0, 1);
            if (animWidth > 0.0f)
                RenderUtil.drawRect(x, y, x + animWidth, y + height, ClickGui.Instance.color.GetColor().getRGB());
            Ruby.rubyFont.drawStringWithShadow(module.getName(), x + (isInside(mouseX, mouseY) ? 2 : 1), y + (height / 2f) - (Ruby.rubyFont.getHeight(module.getName()) / 2f), -1);
            if (isInside(mouseX, mouseY))
                hoverAnimWidth = AnimationUtil.increaseNumber(hoverAnimWidth, width, ClickGui.Instance.animationSpeed.GetSlider());
            else
                hoverAnimWidth = AnimationUtil.decreaseNumber(hoverAnimWidth, 0, ClickGui.Instance.animationSpeed.GetSlider());
            RenderUtil.drawRect(x, y, x + hoverAnimWidth, y + height, new Color(0, 0, 0, 50).getRGB());
        }
        {
            deltaY = 0;
            if (isOpened) {
                settings.forEach(setting -> {
                    setting.setX(x + (setting.getSetting().hasParent() ? 3 : 2));
                    setting.setY(y + (deltaY += height + 1));
                    setting.setWidth(width - (setting.getSetting().hasParent() ? 6 : 4));
                    setting.setHeight(height);
                });
                deltaY += height + 1;
                if (animDeltaY > deltaY - ClickGui.Instance.animationSpeed.GetSlider() && animDeltaY < deltaY)
                    animDeltaY = deltaY;
                if (animDeltaY < deltaY)
                    animDeltaY = AnimationUtil.increaseNumber(animDeltaY, deltaY, (int) ClickGui.Instance.animationSpeed.GetSlider());
                else if (animDeltaY > deltaY)
                    animDeltaY = AnimationUtil.decreaseNumber(animDeltaY, deltaY, (int) ClickGui.Instance.animationSpeed.GetSlider());
            } else {
                animDeltaY = AnimationUtil.decreaseNumber(animDeltaY, height + 1, (int) ClickGui.Instance.animationSpeed.GetSlider());
            }
            if (y + animDeltaY > y + height + 1) {
                glPushMatrix();
                glPushAttrib(GL_SCISSOR_BIT);
                {
                    RenderUtil.scissor(x, y + height, x + 1000, y + animDeltaY);
                    glEnable(GL_SCISSOR_TEST);
                }
                RenderUtil.drawRect(x, y + height + 1, x + 1, y + animDeltaY, ClickGui.Instance.color.GetColor().getRGB());
                settings.forEach(setting -> setting.render(mouseX, mouseY));
                glDisable(GL_SCISSOR_TEST);
                glPopAttrib();
                glPopMatrix();
            }
        }
    }

    public void keyTyped(char typedChar, int keyCode) {
        if (isOpened) {
            settings.forEach(setting -> setting.type(typedChar, keyCode));
        }
    }

    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (isInside(mouseX, mouseY)) {
            switch (mouseButton) {
                case 0:
                    if (module.isEnabled())
                        module.disableModule();
                    else
                        module.enableModule();
                    break;
                case 1:
                    isOpened = !isOpened;
                    break;
                case 2:
                    module.drawn = !module.drawn;
                    Ruby.chatManager.sendMessage(ChatFormatting.WHITE + "" + ChatFormatting.BOLD + module.getName() + ChatFormatting.WHITE + " drawn: " + module.drawn + ".");
                    break;
            }
        }
        if (isOpened) {
            settings.forEach(setting -> setting.click(mouseX, mouseY, mouseButton));
        }
    }

    public void mouseReleased(int mouseX, int mouseY, int state) {
        if (isOpened) {
            settings.forEach(setting -> setting.release(mouseX, mouseY, state));
        }
    }

    public boolean isInside(int mouseX, int mouseY) {
        return mouseX > x && mouseX < x + width && mouseY > y && mouseY < y + height;
    }
}
