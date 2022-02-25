package dev.zprestige.ruby.module.client;

import dev.zprestige.ruby.Ruby;
import dev.zprestige.ruby.module.Module;
import dev.zprestige.ruby.settings.impl.ColorBox;
import dev.zprestige.ruby.settings.impl.Slider;
import dev.zprestige.ruby.settings.impl.Switch;
import dev.zprestige.ruby.ui.click.MainScreen;
import dev.zprestige.ruby.ui.config.ConfigGuiScreen;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Keyboard;

public class ClickGui extends Module {
    public static ClickGui Instance;
    public final ColorBox color = Menu.Color("Color");
    public final ColorBox backgroundColor = Menu.Color("Background Color");
    public final Switch icons = Menu.Switch("Icons");
    public final Slider scrollSpeed = Menu.Slider("Scroll Speed", 1, 20);
    public final Slider animationSpeed = Menu.Slider("Animation Speed", 1, 20);
    public MainScreen mainScreen = null;


    public ClickGui() {
        Instance = this;
        setKeybind(Keyboard.KEY_O);
    }

    @Override
    public void onEnable() {
        final MainScreen mainScreen = new MainScreen();
        mc.displayGuiScreen(mainScreen);
        this.mainScreen = mainScreen;
    }

    @Override
    public void onDisable() {
        mc.displayGuiScreen(null);
        mainScreen = null;
        Ruby.configManager.saveSocials();
    }

    @Override
    public void onTick() {
        if (!(mc.currentScreen instanceof MainScreen) && !(mc.currentScreen instanceof ConfigGuiScreen))
            disableModule();
    }


    @SubscribeEvent
    public void onRenderOverlay(RenderGameOverlayEvent event) {
        if (isEnabled() && event.getType().equals(RenderGameOverlayEvent.ElementType.ALL))
            event.setCanceled(true);
    }
}
