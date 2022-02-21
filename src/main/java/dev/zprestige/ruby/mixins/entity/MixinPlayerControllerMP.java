package dev.zprestige.ruby.mixins.entity;

import dev.zprestige.ruby.Ruby;
import dev.zprestige.ruby.events.BlockInteractEvent;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = {PlayerControllerMP.class})
public class MixinPlayerControllerMP {
    @Inject(method = {"clickBlock"}, at = {@At(value = "HEAD")}, cancellable = true)
    private void clickBlockHook(BlockPos pos, EnumFacing face, CallbackInfoReturnable<Boolean> info) {
        Ruby.RubyEventBus.post(new BlockInteractEvent.ClickBlock(pos, face));
    }

    @Inject(method = {"onPlayerDamageBlock"}, at = {@At(value = "HEAD")}, cancellable = true)
    private void onPlayerDamageBlockHook(BlockPos pos, EnumFacing face, CallbackInfoReturnable<Boolean> info) {
        Ruby.RubyEventBus.post(new BlockInteractEvent.DamageBlock(pos, face));
    }
}