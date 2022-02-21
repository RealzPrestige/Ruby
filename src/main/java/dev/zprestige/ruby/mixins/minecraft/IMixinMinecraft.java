package dev.zprestige.ruby.mixins.minecraft;

import net.minecraft.client.Minecraft;
import net.minecraft.util.Session;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin({Minecraft.class})
public interface IMixinMinecraft {
    @Accessor("session")
    void setSession(final Session p0);
}