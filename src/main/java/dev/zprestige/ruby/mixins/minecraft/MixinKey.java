package dev.zprestige.ruby.mixins.minecraft;

import dev.zprestige.ruby.Ruby;
import dev.zprestige.ruby.events.KeyEvent;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value= {KeyBinding.class})
public class MixinKey {
    @Shadow
    public boolean pressed;

    @Inject(method={"isKeyDown"}, at={@At(value="RETURN")}, cancellable=true)
    private void isKeyDown(CallbackInfoReturnable<Boolean> info) {
        KeyEvent event = new KeyEvent(info.getReturnValue(), this.pressed);
        Ruby.RubyEventBus.post( event );
        info.setReturnValue(event.info);
    }
}