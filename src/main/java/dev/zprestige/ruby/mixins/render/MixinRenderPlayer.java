package dev.zprestige.ruby.mixins.render;

import dev.zprestige.ruby.module.visual.Nametags;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.RenderPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = {RenderPlayer.class})
public class MixinRenderPlayer {
    @Inject(method={"renderEntityName*"}, at={@At(value="HEAD")}, cancellable=true)
    protected void renderEntityNameHook(AbstractClientPlayer entityIn, double x, double y, double z, String name, double distanceSq, CallbackInfo info) {
        if (Nametags.Instance.isEnabled()) {
            info.cancel();
        }
    }
}