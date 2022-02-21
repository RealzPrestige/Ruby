package dev.zprestige.ruby.mixins.world;

import dev.zprestige.ruby.Ruby;
import dev.zprestige.ruby.events.ParticleEvent;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleManager;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ParticleManager.class)
public class MixinParticleManager {

    @Inject(method = "addEffect", at = @At("HEAD"), cancellable = true)
    public void addEffect(Particle effect, CallbackInfo ci) {
        ParticleEvent event = new ParticleEvent();
        Ruby.RubyEventBus.post(event);
        if (event.isCanceled()) {
            ci.cancel();
        }
    }

}