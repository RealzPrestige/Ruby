package dev.zprestige.ruby.mixins.gui;

import dev.zprestige.ruby.util.RenderUtil;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiContainer.class)
public abstract class MixinGuiContainer extends GuiScreen {

    @Inject(method = "drawScreen", at = @At("TAIL"))
    private void drawScreen(int mouseX, int mouseY, float partialTicks, CallbackInfo ci) {
        RenderUtil.renderLogo();
    }
}
