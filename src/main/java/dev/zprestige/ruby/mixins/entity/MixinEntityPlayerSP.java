package dev.zprestige.ruby.mixins.entity;

import com.mojang.authlib.GameProfile;
import dev.zprestige.ruby.Ruby;
import dev.zprestige.ruby.events.BlockPushEvent;
import dev.zprestige.ruby.events.MotionUpdateEvent;
import dev.zprestige.ruby.events.MoveEvent;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.MoverType;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = {EntityPlayerSP.class}, priority = 999)
public abstract class MixinEntityPlayerSP extends AbstractClientPlayer {
    MotionUpdateEvent motionEvent;

    public MixinEntityPlayerSP(World worldIn, GameProfile playerProfile) {
        super(worldIn, playerProfile);
    }

    public void move(MoverType type, double x, double y, double z) {
        MoveEvent event = new MoveEvent(type, x, y, z);
        Ruby.RubyEventBus.post(event);
        super.move(type, event.getMotionX(), event.getMotionY(), event.getMotionZ());
    }

    @Inject(method = "move", at = @At(value = "HEAD"), cancellable = true)
    public void move(MoverType type, double x, double y, double z, CallbackInfo ci) {
        MoveEvent event = new MoveEvent(type, x, y, z);
        Ruby.RubyEventBus.post(event);
        if (event.motionX != x || event.motionY != y || event.motionZ != z) {
            super.move(type, event.motionX, event.motionY, event.motionZ);
            ci.cancel();
        }
    }

    @Inject(method = {"pushOutOfBlocks"}, at = {@At("HEAD")}, cancellable = true)
    private void pushOutOfBlocksHook(double x, double y, double z, CallbackInfoReturnable<Boolean> info) {
        BlockPushEvent event = new BlockPushEvent();
        Ruby.RubyEventBus.post(event);
        if (event.isCanceled())
            info.setReturnValue(false);
    }


    @Inject(method = {"onUpdateWalkingPlayer"}, at = {@At("HEAD")}, cancellable = true)
    public void onUpdateWalkingPlayer_Head(CallbackInfo callbackInfo) {
        motionEvent = new MotionUpdateEvent(1, this.posX, this.getEntityBoundingBox().minY, this.posZ, this.rotationYaw, this.rotationPitch, this.onGround);
        Ruby.RubyEventBus.post(motionEvent);
        if (motionEvent.isCanceled())
            callbackInfo.cancel();
    }

    @Inject(method = {"onUpdateWalkingPlayer"}, at = {@At("RETURN")})
    public void onUpdateWalkingPlayer_Return(CallbackInfo callbackInfo) {
        MotionUpdateEvent event = new MotionUpdateEvent(2, motionEvent);
        event.setCanceled(motionEvent.isCanceled());
        Ruby.RubyEventBus.post(event);
    }
}