package dev.zprestige.ruby.mixins.render;

import dev.zprestige.ruby.Ruby;
import dev.zprestige.ruby.events.RenderItemEvent;
import dev.zprestige.ruby.module.visual.ItemModification;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.awt.*;

@Mixin(value = RenderItem.class, priority = 1001)
public class MixinRenderItem {

    @Shadow
    private void renderModel(IBakedModel model, int color, ItemStack stack) {
    }

    @Inject(method = {"renderItem(Lnet/minecraft/item/ItemStack;Lnet/minecraft/entity/EntityLivingBase;Lnet/minecraft/client/renderer/block/model/ItemCameraTransforms$TransformType;Z)V"}, at = {@At("INVOKE")})
    public void renderItem(final ItemStack stack, final EntityLivingBase entityLivingBaseIn, final ItemCameraTransforms.TransformType transform, final boolean leftHanded, final CallbackInfo ci) {
        if (transform == ItemCameraTransforms.TransformType.FIRST_PERSON_LEFT_HAND || transform == ItemCameraTransforms.TransformType.FIRST_PERSON_RIGHT_HAND) {
            if (transform.equals(ItemCameraTransforms.TransformType.FIRST_PERSON_LEFT_HAND))
                Ruby.eventBus.post(new RenderItemEvent.Offhand(stack, entityLivingBaseIn));
            else
                Ruby.eventBus.post(new RenderItemEvent.MainHand(stack, entityLivingBaseIn));
        }
    }

    @Redirect(method = "renderItem(Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/renderer/block/model/IBakedModel;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/RenderItem;renderModel(Lnet/minecraft/client/renderer/block/model/IBakedModel;Lnet/minecraft/item/ItemStack;)V"))
    public void renderModelColor(RenderItem renderItem, IBakedModel model, ItemStack stack) {
        renderModel(model, ItemModification.Instance.isEnabled() ? ItemModification.Instance.color.getValue().getRGB() : new Color(1.0f, 1.0f, 1.0f, 1.0f).getRGB(), stack);
    }
}