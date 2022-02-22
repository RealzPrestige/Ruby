package dev.zprestige.ruby.mixins.gui;

import dev.zprestige.ruby.module.client.Hud;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.gui.ScaledResolution;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = GuiIngame.class)
public class MixinGuiIngame {

    @Inject(method = "renderPotionEffects", at = @At("HEAD"), cancellable = true)
    protected void renderPotionEffects(ScaledResolution scaledRes, CallbackInfo ci) {
        if (Hud.Instance.nullCheck() && Hud.Instance.isEnabled()) {
            ci.cancel();
        }
    }
}
