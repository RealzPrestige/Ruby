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
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = EntityPlayerSP.class, priority = 999)
public class MixinEntityPlayerSP extends AbstractClientPlayer {
    protected MotionUpdateEvent motionEvent;

    public MixinEntityPlayerSP(World worldIn, GameProfile playerProfile) {
        super(worldIn, playerProfile);
    }

    @SuppressWarnings("NullableProblems")
    public void move(MoverType type, double x, double y, double z) {
        final MoveEvent event = new MoveEvent(type, x, y, z);
        Ruby.eventBus.post(event);
        super.move(type, event.getMotionX(), event.getMotionY(), event.getMotionZ());
    }

    @Inject(method = "move", at = @At(value = "HEAD"), cancellable = true)
    protected void move(MoverType type, double x, double y, double z, CallbackInfo ci) {
        final MoveEvent event = new MoveEvent(type, x, y, z);
        Ruby.eventBus.post(event);
        if (event.motionX != x || event.motionY != y || event.motionZ != z) {
            super.move(type, event.motionX, event.motionY, event.motionZ);
            ci.cancel();
        }
    }

    @Inject(method = {"pushOutOfBlocks"}, at = {@At("HEAD")}, cancellable = true)
    protected void pushOutOfBlocksHook(double x, double y, double z, CallbackInfoReturnable<Boolean> info) {
        final BlockPushEvent event = new BlockPushEvent();
        Ruby.eventBus.post(event);
        if (event.isCancelled()) {
            info.setReturnValue(false);
        }
    }


    @Inject(method = {"onUpdateWalkingPlayer"}, at = {@At("HEAD")}, cancellable = true)
    protected void onUpdateWalkingPlayer_Head(CallbackInfo callbackInfo) {
        motionEvent = new MotionUpdateEvent(1, this.posX, this.getEntityBoundingBox().minY, this.posZ, this.rotationYaw, this.rotationPitch, this.onGround);
        Ruby.eventBus.post(motionEvent);
        if (motionEvent.isCancelled()) {
            callbackInfo.cancel();
        }
    }

    @Inject(method = {"onUpdateWalkingPlayer"}, at = {@At("RETURN")})
    protected void onUpdateWalkingPlayer_Return(CallbackInfo callbackInfo) {
        final MotionUpdateEvent event = new MotionUpdateEvent(2, motionEvent);
        event.setCancelled(motionEvent.isCancelled());
        Ruby.eventBus.post(event);
    }
}