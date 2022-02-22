package dev.zprestige.ruby.mixins.render;

import dev.zprestige.ruby.Ruby;
import dev.zprestige.ruby.events.RenderItemInFirstPersonEvent;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemRenderer.class)
public abstract class MixinItemRenderer {

    @Redirect(method = "renderItemInFirstPerson(Lnet/minecraft/client/entity/AbstractClientPlayer;FFLnet/minecraft/util/EnumHand;FLnet/minecraft/item/ItemStack;F)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/ItemRenderer;renderItemSide(Lnet/minecraft/entity/EntityLivingBase;Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/renderer/block/model/ItemCameraTransforms$TransformType;Z)V"))
    public void renderItemInFirstPerson(ItemRenderer itemRenderer, EntityLivingBase entitylivingbaseIn, ItemStack heldStack, ItemCameraTransforms.TransformType transform, boolean leftHanded) {
        final RenderItemInFirstPersonEvent eventPre = new RenderItemInFirstPersonEvent(entitylivingbaseIn, heldStack, transform, leftHanded, true);
        Ruby.eventBus.post(eventPre);
        if (!eventPre.isCancelled()) {
            itemRenderer.renderItemSide(entitylivingbaseIn, eventPre.getStack(), eventPre.getTransformType(), leftHanded);
        }
        final RenderItemInFirstPersonEvent eventPost = new RenderItemInFirstPersonEvent(entitylivingbaseIn, heldStack, transform, leftHanded, false);
        Ruby.eventBus.post(eventPost);
    }
}
