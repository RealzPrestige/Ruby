package dev.zprestige.ruby.module.client;

import dev.zprestige.ruby.Ruby;
import dev.zprestige.ruby.module.Category;
import dev.zprestige.ruby.module.Module;
import dev.zprestige.ruby.module.ModuleInfo;
import dev.zprestige.ruby.setting.impl.BooleanSetting;
import dev.zprestige.ruby.setting.impl.ColorSetting;
import dev.zprestige.ruby.setting.impl.IntegerSetting;
import dev.zprestige.ruby.ui.click.MainScreen;
import dev.zprestige.ruby.ui.config.ConfigGuiScreen;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Keyboard;

import java.awt.*;

@ModuleInfo(name = "NewGui", category = Category.Client, description = "show new gui")
public class NewGui extends Module {
    public static NewGui Instance;
    public ColorSetting color = createSetting("Color", new Color(0x65A778));
    public ColorSetting backgroundColor = createSetting("Background Color", new Color(0, 0, 0, 50));
    public BooleanSetting icons = createSetting("Icons", false);
    public IntegerSetting scrollSpeed = createSetting("Scroll Speed", 5, 1, 20);
    public IntegerSetting animationSpeed = createSetting("Animation Speed", 4, 1, 20);

    public NewGui() {
        Instance = this;
        setKeybind(Keyboard.KEY_O);
    }

    @Override
    public void onEnable() {
        mc.displayGuiScreen(new MainScreen());
    }

    @Override
    public void onDisable() {
        mc.displayGuiScreen(null);
        Ruby.configManager.savePlayer();
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
