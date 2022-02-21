package dev.zprestige.ruby.mixins.render;

import dev.zprestige.ruby.Ruby;
import dev.zprestige.ruby.events.ItemEatEvent;
import dev.zprestige.ruby.events.RenderItemInFirstPersonEvent;
import dev.zprestige.ruby.module.visual.ESP;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumHandSide;
import net.minecraftforge.common.MinecraftForge;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.awt.*;

import static org.lwjgl.opengl.GL11.*;

@Mixin(ItemRenderer.class)
public abstract class MixinItemRenderer {

    @Inject(method = "transformEatFirstPerson", at = @At("HEAD"), cancellable = true)
    public void onTransformEatFirstPerson(CallbackInfo callbackInfo) {
        ItemEatEvent event = new ItemEatEvent();
        Ruby.RubyEventBus.post(event);
        if (event.isCanceled())
            callbackInfo.cancel();
    }

    @Redirect(method = "renderItemInFirstPerson(Lnet/minecraft/client/entity/AbstractClientPlayer;FFLnet/minecraft/util/EnumHand;FLnet/minecraft/item/ItemStack;F)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/ItemRenderer;renderItemSide(Lnet/minecraft/entity/EntityLivingBase;Lnet/minecraft/item/ItemStack;Lnet/minecraft/client/renderer/block/model/ItemCameraTransforms$TransformType;Z)V"))
    public void renderItemInFirstPerson(ItemRenderer itemRenderer, EntityLivingBase entitylivingbaseIn, ItemStack heldStack, ItemCameraTransforms.TransformType transform, boolean leftHanded) {
        RenderItemInFirstPersonEvent pre = new RenderItemInFirstPersonEvent(entitylivingbaseIn, heldStack, transform, leftHanded, true);
        Ruby.RubyEventBus.post(pre);
        if (!pre.isCanceled())
            itemRenderer.renderItemSide(entitylivingbaseIn, pre.getStack(), pre.getTransformType(), leftHanded);
        RenderItemInFirstPersonEvent post = new RenderItemInFirstPersonEvent(entitylivingbaseIn, heldStack, transform, leftHanded, false);
        Ruby.RubyEventBus.post(post);
    }
}
