package dev.zprestige.ruby.ui.click;

import dev.zprestige.ruby.Ruby;
import dev.zprestige.ruby.util.RenderUtil;
import dev.zprestige.ruby.util.Timer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainScreen extends GuiScreen {
    public static Timer timer = new Timer();
    public ArrayList<GuiCategory> guiCategories = new ArrayList<>();
    public int deltaX;
    public HashMap<Float, String> copyPasteMap = new HashMap<>();
    protected float scaled = 0.0f;

    public MainScreen() {
        deltaX = 26;
        Ruby.moduleManager.getCategories().forEach(category -> guiCategories.add(new GuiCategory(category, deltaX += 101, 2, 100, 13)));
    }

    public static String idleSign() {
        if (timer.getTime(1000))
            timer.setTime(0);
        if (timer.getTime(500))
            return "_";
        return "";
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        scaled += (1.0f - scaled) / 25.0f;
        GlStateManager.scale(scaled, scaled, scaled);
        final ScaledResolution scaledResolution = new ScaledResolution(mc);
        {
            GlStateManager.enableAlpha();
            GlStateManager.color(1f, 1f, 1f);
            RenderUtil.image(new ResourceLocation("textures/icons/ruby.png"), (scaledResolution.getScaledWidth() / 2) - 34, 511, 68, 28);
            GlStateManager.disableAlpha();
        }
        guiCategories.forEach(newCategory -> newCategory.drawScreen(mouseX, mouseY));
        if (!copyPasteMap.isEmpty()) {
            final float centerX = scaledResolution.getScaledWidth() / 2f;
            final float height = scaledResolution.getScaledHeight();
            float deltaY = height - (height / 4f);
            for (Map.Entry<Float, String> entry : new HashMap<>(copyPasteMap).entrySet()) {
                if (entry.getKey() <= 50.0f) {
                    copyPasteMap.remove(entry.getKey());
                    continue;
                }
                final String entryValue = entry.getValue();
                Ruby.rubyFont.drawStringWithShadow(entryValue, centerX - (Ruby.rubyFont.getStringWidth(entryValue) / 2f), deltaY -= Math.min(10, entry.getKey() / 5.0f), new Color(1, 1, 1, entry.getKey() / 255.0f).getRGB());
                copyPasteMap.put(entry.getKey() - (entry.getKey() / 100.0f), entryValue);
                copyPasteMap.remove(entry.getKey());
            }
        }
    }

    @Override
    public void keyTyped(char typedChar, int keyCode) throws IOException {
        super.keyTyped(typedChar, keyCode);
        guiCategories.forEach(newCategory -> newCategory.keyTyped(typedChar, keyCode));
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        guiCategories.forEach(newCategory -> newCategory.mouseClicked(mouseX, mouseY, mouseButton));
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int state) {
        guiCategories.forEach(newCategory -> newCategory.mouseReleased(mouseX, mouseY, state));
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }
}
