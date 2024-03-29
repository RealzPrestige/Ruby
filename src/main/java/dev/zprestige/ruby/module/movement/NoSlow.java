package dev.zprestige.ruby.module.movement;

import dev.zprestige.ruby.eventbus.annotation.RegisterListener;
import dev.zprestige.ruby.events.KeyEvent;
import dev.zprestige.ruby.module.Module;
import dev.zprestige.ruby.settings.impl.Switch;
import net.minecraft.client.gui.*;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.client.event.InputUpdateEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Keyboard;

import java.util.Arrays;

public class NoSlow extends Module {
    public static NoSlow Instance;
    public final Switch items = Menu.Switch("Items");
    public final Switch guiMove = Menu.Switch("Inventory");
    public KeyBinding[] keys = new KeyBinding[]{mc.gameSettings.keyBindForward, mc.gameSettings.keyBindBack, mc.gameSettings.keyBindLeft, mc.gameSettings.keyBindRight, mc.gameSettings.keyBindJump, mc.gameSettings.keyBindSprint};

    public NoSlow() {
        Instance = this;
    }

    @Override
    public void onTick() {
        if (guiMove.GetSwitch()) {
            if (mc.currentScreen instanceof GuiOptions || mc.currentScreen instanceof GuiVideoSettings || mc.currentScreen instanceof GuiScreenOptionsSounds || mc.currentScreen instanceof GuiContainer || mc.currentScreen instanceof GuiIngameMenu)
                Arrays.stream(keys).forEach(bind -> KeyBinding.setKeyBindState(bind.getKeyCode(), Keyboard.isKeyDown(bind.getKeyCode())));
            else if (mc.currentScreen == null) {
                for (KeyBinding bind : keys) {
                    if (Keyboard.isKeyDown(bind.getKeyCode()))
                        continue;
                    KeyBinding.setKeyBindState(bind.getKeyCode(), false);
                }
            }
        }
    }

    @SubscribeEvent
    public void onItemEat(InputUpdateEvent event) {
        if (items.GetSwitch() && mc.player.isHandActive()) {
            event.getMovementInput().moveStrafe *= 5.0f;
            event.getMovementInput().moveForward *= 5.0f;
        }
    }

    @RegisterListener
    public void onKeyEvent(KeyEvent event) {
        if (guiMove.GetSwitch() && !(mc.currentScreen instanceof GuiChat)) {
            event.info = event.pressed;
        }
    }
}
