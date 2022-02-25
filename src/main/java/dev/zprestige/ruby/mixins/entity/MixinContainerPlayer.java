package dev.zprestige.ruby.mixins.entity;

import dev.zprestige.ruby.Ruby;
import dev.zprestige.ruby.events.CloseInventoryEvent;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ContainerPlayer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = ContainerPlayer.class)
public class MixinContainerPlayer {

    @Inject(method = "onContainerClosed", at = @At("HEAD"), cancellable = true)
    protected void onContainerClosed(EntityPlayer entityPlayer, CallbackInfo callbackInfo) {
        CloseInventoryEvent event = new CloseInventoryEvent();
        Ruby.eventBus.post(event);
        if (event.isCancelled()) {
            callbackInfo.cancel();
        }
    }
}
