package dev.zprestige.ruby.mixins.entity;

import dev.zprestige.ruby.Ruby;
import dev.zprestige.ruby.events.EntityPushEvent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = EntityPlayer.class)
public abstract class MixinEntityPlayer extends EntityLivingBase {
    public MixinEntityPlayer(World worldIn) {
        super(worldIn);
    }

    @Inject(method = {"applyEntityCollision"}, at = {@At("HEAD")}, cancellable = true)
    protected void applyEntityCollision(Entity entity, CallbackInfo info) {
        EntityPushEvent event = new EntityPushEvent();
        Ruby.eventBus.post(event);
        if (event.isCancelled())
            info.cancel();
    }
}
