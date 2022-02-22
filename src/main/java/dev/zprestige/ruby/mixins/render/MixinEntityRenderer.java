package dev.zprestige.ruby.mixins.render;

import dev.zprestige.ruby.Ruby;
import dev.zprestige.ruby.events.MouseOverEvent;
import dev.zprestige.ruby.module.visual.Ambience;
import dev.zprestige.ruby.module.visual.NoRender;
import net.minecraft.client.renderer.EntityRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = EntityRenderer.class)
public abstract class MixinEntityRenderer implements IEntityRenderer{
    @Shadow
    protected abstract void renderHand(float partialTicks, int pass);

    @Override
    public void invokeRenderHand(float partialTicks, int pass) {
        renderHand(partialTicks, pass);
    }

    @Shadow
    protected abstract void setupCameraTransform(float partialTicks, int pass);

    @Override
    public void invokeSetupCameraTransform(float partialTicks, int pass) {
        setupCameraTransform(partialTicks, pass);
    }

    @Inject(method = "getMouseOver", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/WorldClient;getEntitiesInAABBexcluding(Lnet/minecraft/entity/Entity;Lnet/minecraft/util/math/AxisAlignedBB;Lcom/google/common/base/Predicate;)Ljava/util/List;"), cancellable = true)
    protected void mouseOver(float partialTicks, CallbackInfo ci) {
        final MouseOverEvent event = new MouseOverEvent();
        Ruby.eventBus.post(event);
        if (event.isCancelled()) {
            ci.cancel();
        }
    }

    @Inject(method = {"hurtCameraEffect"}, at = {@At(value = "HEAD")}, cancellable = true)
    protected void hurtCameraEffectHook(float ticks, CallbackInfo info) {
        if (!NoRender.Instance.nullCheck() && NoRender.Instance.isEnabled() && NoRender.Instance.hurtCam.getValue())
            info.cancel();
    }

    @ModifyVariable(method = "updateLightmap", at = @At(value = "STORE"), index = 20)
    protected int red(int red) {
        if (Ambience.Instance.isEnabled()) {
            red = Ambience.Instance.color.getValue().getRed();
        }
        return red;
    }

    @ModifyVariable(method = "updateLightmap", at = @At(value = "STORE"), index = 21)
    protected int green(int green) {
        if (Ambience.Instance.isEnabled()) {
            green = Ambience.Instance.color.getValue().getGreen();
        }
        return green;
    }

    @ModifyVariable(method = "updateLightmap", at = @At(value = "STORE"), index = 22)
    protected int blue(int blue) {
        if (Ambience.Instance.isEnabled()) {
            blue = Ambience.Instance.color.getValue().getBlue();
        }
        return blue;
    }
}
