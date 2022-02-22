package dev.zprestige.ruby.mixins.gui;

import dev.zprestige.ruby.ui.altening.AlteningGuiScreen;
import dev.zprestige.ruby.util.RenderUtil;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = GuiMainMenu.class)
public class MixinGuiMainMenu extends GuiScreen {

    @Inject(method = {"initGui"}, at = {@At("RETURN")})
    protected void initGui(CallbackInfo callbackInfo) {
        buttonList.add(new GuiButton(36, width / 2 - 100, (height / 4 + 48) + 118, "Altening Manager"));
    }

    @Inject(method = {"actionPerformed"}, at = {@At("RETURN")})
    protected void actionPerformed(GuiButton button, CallbackInfo callbackInfo) {
        if (button.id == 36) {
            mc.displayGuiScreen(new AlteningGuiScreen(this));
        }
    }
}
