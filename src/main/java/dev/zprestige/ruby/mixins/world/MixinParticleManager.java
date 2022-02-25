package dev.zprestige.ruby.mixins.world;

import dev.zprestige.ruby.Ruby;
import dev.zprestige.ruby.events.ParticleEvent;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ParticleManager.class)
public class MixinParticleManager {

    @Inject(method = "addEffect", at = @At("HEAD"), cancellable = true)
    public void addEffect(Particle effect, CallbackInfo ci) {
        final ParticleEvent event = new ParticleEvent();
        Ruby.eventBus.post(event);
        if (event.isCancelled()) {
            ci.cancel();
        }
    }

}