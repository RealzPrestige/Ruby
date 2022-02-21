package dev.zprestige.ruby.ui.click;

import com.mojang.realmsclient.gui.ChatFormatting;
import dev.zprestige.ruby.Ruby;
import dev.zprestige.ruby.module.Module;
import dev.zprestige.ruby.module.client.NewGui;
import dev.zprestige.ruby.setting.impl.*;
import dev.zprestige.ruby.ui.click.setting.GuiSetting;
import dev.zprestige.ruby.ui.click.setting.impl.*;
import dev.zprestige.ruby.util.AnimationUtil;
import dev.zprestige.ruby.util.MessageUtil;
import dev.zprestige.ruby.util.RenderUtil;

import java.awt.*;
import java.util.ArrayList;

import static org.lwjgl.opengl.GL11.*;

public class GuiModule {
    public Module module;
    public int x, y, width, height, deltaY, animDeltaY;
    public boolean isOpened = false;
    public float animWidth, hoverAnimWidth;
    public ArrayList<GuiSetting> newSettings = new ArrayList<>();

    public GuiModule(Module module, int x, int y, int width, int height) {
        this.module = module;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.animWidth = module.isEnabled() ? width : 0.0f;
        this.hoverAnimWidth = 0.0f;
        module.getSettingList().stream().filter(setting -> !setting.getName().equals("Enabled")).forEach(setting -> {
            if (setting instanceof ParentSetting)
                newSettings.add(new GuiParent((ParentSetting) setting));
            if (setting instanceof BooleanSetting)
                newSettings.add(new GuiBoolean((BooleanSetting) setting));
            if (setting instanceof IntegerSetting)
                newSettings.add(new GuiInteger((IntegerSetting) setting));
            if (setting instanceof DoubleSetting)
                newSettings.add(new GuiDouble((DoubleSetting) setting));
            if (setting instanceof FloatSetting)
                newSettings.add(new GuiFloat((FloatSetting) setting));
            if (setting instanceof KeySetting)
                newSettings.add(new GuiKey((KeySetting) setting));
            if (setting instanceof ModeSetting)
                newSettings.add(new GuiMode((ModeSetting) setting));
            if (setting instanceof StringSetting)
                newSettings.add(new GuiString((StringSetting) setting));
            if (setting instanceof ColorSetting)
                newSettings.add(new GuiColor((ColorSetting) setting));
        });
        animDeltaY = height + 1;
    }

    public void drawScreen(int mouseX, int mouseY) {
        {
            RenderUtil.drawRect(x, y, x + width, y + height, NewGui.Instance.backgroundColor.getValue().getRGB());
            RenderUtil.drawOutlineRect(x, y, x + width, y + height, NewGui.Instance.backgroundColor.getValue(), 1f);
            if (module.isEnabled())
                animWidth = AnimationUtil.increaseNumber(animWidth, width, 1);
            else
                animWidth = AnimationUtil.decreaseNumber(animWidth, 0.0f, 1);
            if (animWidth > 0.0f)
                RenderUtil.drawRect(x, y, x + animWidth, y + height, NewGui.Instance.color.getValue().getRGB());
            Ruby.rubyFont.drawStringWithShadow(module.getName(), x + (isInside(mouseX, mouseY) ? 2 : 1), y + (height / 2f) - (Ruby.rubyFont.getHeight(module.getName()) / 2f), -1);
            if (isInside(mouseX, mouseY))
                hoverAnimWidth = AnimationUtil.increaseNumber(hoverAnimWidth, width, NewGui.Instance.animationSpeed.getValue());
            else
                hoverAnimWidth = AnimationUtil.decreaseNumber(hoverAnimWidth, 0, NewGui.Instance.animationSpeed.getValue());
            RenderUtil.drawRect(x, y, x + hoverAnimWidth, y + height, new Color(0, 0, 0, 50).getRGB());
        }
        {
            deltaY = 0;
            if (isOpened) {
                newSettings.stream().filter(GuiSetting::isVisible).forEach(newSetting -> {
                    newSetting.setX(x + 2 + (newSetting.getSetting().hasParentSetting ? 1 : 0));
                    newSetting.setY(y + (deltaY += height + 1) - (newSetting.getSetting().hasParentSetting && newSetting.getSetting().parentSetting.getValue() ? 1 : 0));
                    newSetting.setWidth(width - 4 - (newSetting.getSetting().hasParentSetting ? 2 : 0));
                    newSetting.setHeight(height);
                    if (newSetting instanceof GuiColor && newSetting.getSetting().isOpen)
                        deltaY += 109;
                    if (newSetting instanceof GuiParent)
                        deltaY += ((ParentSetting) newSetting.getSetting()).getValue() ? 2 : 1;
                });
                deltaY += height + 1;
                if (animDeltaY > deltaY - NewGui.Instance.animationSpeed.getValue() && animDeltaY < deltaY)
                    animDeltaY = deltaY;
                if (animDeltaY < deltaY)
                    animDeltaY = AnimationUtil.increaseNumber(animDeltaY, deltaY, NewGui.Instance.animationSpeed.getValue());
                else if (animDeltaY > deltaY)
                    animDeltaY = AnimationUtil.decreaseNumber(animDeltaY, deltaY, NewGui.Instance.animationSpeed.getValue());
            } else {
                animDeltaY = AnimationUtil.decreaseNumber(animDeltaY, height + 1, NewGui.Instance.animationSpeed.getValue());
            }
            if (y + animDeltaY > y + height + 1) {
                glPushMatrix();
                glPushAttrib(GL_SCISSOR_BIT);
                {
                    RenderUtil.scissor(x, y + height, x + 1000, y + animDeltaY);
                    glEnable(GL_SCISSOR_TEST);
                }
                RenderUtil.drawRect(x, y + height + 1, x + 1, y + animDeltaY, NewGui.Instance.color.getValue().getRGB());
                if (isOpened) {
                    newSettings.stream().filter(GuiSetting::isVisible).forEach(newSetting -> newSetting.drawScreen(mouseX, mouseY));
                }
                glDisable(GL_SCISSOR_TEST);
                glPopAttrib();
                glPopMatrix();
            }
        }
    }

    public void keyTyped(char typedChar, int keyCode) {
        if (isOpened)
            newSettings.stream().filter(GuiSetting::isVisible).forEach(newSetting -> newSetting.keyTyped(typedChar, keyCode));
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
                    MessageUtil.sendMessage(ChatFormatting.WHITE + "" + ChatFormatting.BOLD + module.getName() + ChatFormatting.WHITE + " drawn: " + module.drawn + ".");
                    break;
            }
        }
        if (isOpened)
            newSettings.stream().filter(GuiSetting::isVisible).forEach(newSetting -> newSetting.mouseClicked(mouseX, mouseY, mouseButton));
    }

    public void mouseReleased(int mouseX, int mouseY, int state) {
        if (isOpened)
            newSettings.stream().filter(GuiSetting::isVisible).forEach(newSetting -> newSetting.mouseReleased(mouseX, mouseY, state));
    }

    public boolean isInside(int mouseX, int mouseY) {
        return mouseX > x && mouseX < x + width && mouseY > y && mouseY < y + height;
    }
}
