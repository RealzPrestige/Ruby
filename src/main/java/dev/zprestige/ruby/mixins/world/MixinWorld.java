package dev.zprestige.ruby.mixins.world;

import dev.zprestige.ruby.Ruby;
import dev.zprestige.ruby.events.EntityAddedEvent;
import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = World.class)
public class MixinWorld {

    @Inject(method = {"onEntityAdded"}, at = {@At(value = "HEAD")})
    public void onEntityAdded(Entity entity, CallbackInfo ci) {
        Ruby.eventBus.post(new EntityAddedEvent(entity));
    }
}
