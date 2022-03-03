package dev.zprestige.ruby.ui.hudeditor;

import dev.zprestige.ruby.Ruby;
import dev.zprestige.ruby.module.client.ClickGui;
import dev.zprestige.ruby.util.RenderUtil;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;

import java.util.ArrayList;

public class HudEditorScreen extends GuiScreen {
    protected final ArrayList<HudComponentScreen> hudComponentScreens = new ArrayList<>();
    protected float x, y, width, height, dragX, dragY;
    protected boolean corrected, dragging;

    public HudEditorScreen() {
        width = 100;
        height = 13;
        Ruby.hudManager.getHudComponents().forEach(hudComponent -> hudComponentScreens.add(new HudComponentScreen(hudComponent, 0, 0, 100, 13)));
    }

    protected void drag(int mouseX, int mouseY) {
        x = dragX + mouseX;
        y = dragY + mouseY;
        float deltaY = y;
        for (HudComponentScreen hudComponentScreen : hudComponentScreens) {
            hudComponentScreen.setY(deltaY += 14);
            hudComponentScreen.setX(x);
        }
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        if (dragging) {
            drag(mouseX, mouseY);
        }
        if (!corrected) {
            final ScaledResolution scaledResolution = new ScaledResolution(mc);
            x = (scaledResolution.getScaledWidth() / 2f) - 50;
            y = (scaledResolution.getScaledHeight() / 2f) - (hudComponentScreens.size() * 7.5f) - 7.5f;
            float deltaY = y;
            for (HudComponentScreen hudComponentScreen : hudComponentScreens) {
                hudComponentScreen.setY(deltaY += 14);
                hudComponentScreen.setX(x);
            }
            corrected = true;
        }
        RenderUtil.drawRect(x, y, x + width, y + height, ClickGui.Instance.color.GetColor().getRGB());
        float deltaY = y;
        for (HudComponentScreen ignored : hudComponentScreens) {
            deltaY += 14;
        }
        RenderUtil.drawOutlineRect(x, y, x + width, deltaY, ClickGui.Instance.backgroundColor.GetColor(), 1.0f);
        RenderUtil.drawRect(x, y, x + width,  deltaY, ClickGui.Instance.backgroundColor.GetColor().getRGB());
        RenderUtil.drawOutlineRect(x, y, x + width, y + height, ClickGui.Instance.backgroundColor.GetColor(), 1.0f);
        final String text = "HudEditor";
        Ruby.fontManager.drawStringWithShadow(text, x + (width / 2f) - (Ruby.fontManager.getStringWidth(text) / 2f), y +( height / 2f) - (Ruby.fontManager.getFontHeight() / 2f), -1);
        hudComponentScreens.forEach(hudComponentScreen -> hudComponentScreen.draw(mouseX, mouseY));
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (inside(mouseX, mouseY) && mouseButton == 0) {
            dragX = x - mouseX;
            dragY = y - mouseY;
            dragging = true;
        }
        hudComponentScreens.forEach(hudComponentScreen -> hudComponentScreen.click(mouseX, mouseY, mouseButton));
    }

    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        if (state == 0) {
            dragging = false;
        }
        hudComponentScreens.forEach(hudComponentScreen -> hudComponentScreen.release(state));
    }

    protected boolean inside(int mouseX, int mouseY) {
        return mouseX > x && mouseX < x + width && mouseY > y && mouseY < y + height;
    }
}
