package dev.zprestige.ruby.mixins.gui;

import dev.zprestige.ruby.Ruby;
import dev.zprestige.ruby.util.RenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.awt.*;

@Mixin({GuiButton.class})
public abstract class MixinGuiButton {
    @Shadow
    public int x;
    @Shadow
    public int y;
    @Shadow
    public int width;
    @Shadow
    public int height;
    @Shadow
    public boolean visible;
    @Shadow
    public boolean enabled;
    @Shadow
    protected boolean hovered;
    @Shadow
    public String displayString;

    @Shadow
    protected abstract void mouseDragged(Minecraft mc, int mouseX, int mouseY);

    @Inject(method = {"drawButton"}, at = {@At("HEAD")}, cancellable = true)
    public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks, CallbackInfo ci) {
        if (visible) {
            hovered = (mouseX >= x && mouseY >= y && mouseX < x + width && mouseY < y + height);
            RenderUtil.drawRect(x, y, x + width, y + height, new Color(0,0, 0, 50).getRGB());
            if (hovered)
                RenderUtil.drawRect(x, y, x + width, y + height, new Color(0,0, 0, 50).getRGB());
            Ruby.rubyFont.drawStringWithShadow(displayString, x + (width / 2f) - (Ruby.rubyFont.getStringWidth(displayString) / 2f), y + (height - 10) / 2f, enabled ? (hovered ? 16777120 : 14737632) : 10526880);
            mouseDragged(mc, mouseX, mouseY);
        }
        ci.cancel();
    }
}