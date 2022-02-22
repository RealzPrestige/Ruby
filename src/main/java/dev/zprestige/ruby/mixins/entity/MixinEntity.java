package dev.zprestige.ruby.mixins.entity;

import dev.zprestige.ruby.Ruby;
import dev.zprestige.ruby.events.TurnEvent;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = Entity.class)
public class MixinEntity {

    @Inject(method = "turn", at = @At("HEAD"), cancellable = true)
    protected void onTurn(float yaw, float pitch, CallbackInfo ci) {
        TurnEvent event = new TurnEvent(yaw, pitch);
        Ruby.eventBus.post(event);
        if (event.isCancelled()) {
            ci.cancel();
        }
    }
}
