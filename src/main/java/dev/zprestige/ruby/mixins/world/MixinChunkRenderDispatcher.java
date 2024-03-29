package dev.zprestige.ruby.mixins.world;

import dev.zprestige.ruby.module.visual.WorldTweaks;
import net.minecraft.client.renderer.chunk.ChunkCompileTaskGenerator;
import net.minecraft.client.renderer.chunk.ChunkRenderDispatcher;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = ChunkRenderDispatcher.class)
public class MixinChunkRenderDispatcher {
    @Inject(method = "getNextChunkUpdate", at = @At("HEAD"))
    protected void limitChunkUpdates(final CallbackInfoReturnable<ChunkCompileTaskGenerator> cir) throws InterruptedException {
        if (WorldTweaks.Instance.isEnabled()) {
            Thread.sleep((long) WorldTweaks.Instance.chunkLoadDelay.GetSlider());
        }
    }
}