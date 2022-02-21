package dev.zprestige.ruby.mixins.gui;

import dev.zprestige.ruby.util.RenderUtil;
import net.minecraft.client.gui.GuiNewChat;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.awt.*;

@Mixin(GuiNewChat.class)
public class MixinGuiNewChat {

    @Redirect(method = "drawChat", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiNewChat;drawRect(IIIII)V", ordinal = 0))
    public void drawRect(final int left, final int top, final int right, final int bottom, final int color) {
        RenderUtil.drawRect(left, top, right, bottom, new Color(0, 0, 0, 50).getRGB());
    }
}