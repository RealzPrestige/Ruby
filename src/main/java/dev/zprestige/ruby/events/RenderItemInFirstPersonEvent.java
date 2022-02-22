package dev.zprestige.ruby.events;

import dev.zprestige.ruby.eventbus.event.Event;
import dev.zprestige.ruby.eventbus.event.IsCancellable;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;

@IsCancellable
public class RenderItemInFirstPersonEvent extends Event {

    public EntityLivingBase entity;
    public ItemStack stack;
    public ItemCameraTransforms.TransformType transformType;
    public boolean leftHanded;
    public boolean isPre;


    public RenderItemInFirstPersonEvent(EntityLivingBase entitylivingbaseIn, ItemStack heldStack, ItemCameraTransforms.TransformType transform, boolean leftHanded, boolean pre) {
        this.entity = entitylivingbaseIn;
        this.stack = heldStack;
        this.transformType = transform;
        this.leftHanded = leftHanded;
        this.isPre = pre;
    }

    public ItemCameraTransforms.TransformType getTransformType() {
        return transformType;
    }

    public ItemStack getStack() {
        return stack;
    }

    public void setStack(ItemStack stack) {
        this.stack = stack;
    }

    public EntityLivingBase getEntity() {
        return entity;
    }
}

