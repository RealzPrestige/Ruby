package dev.zprestige.ruby.mixins.minecraft;

import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(value = {Minecraft.class})
public abstract class MixinMinecraft {
}