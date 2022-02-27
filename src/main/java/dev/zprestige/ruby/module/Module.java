package dev.zprestige.ruby.module;

import dev.zprestige.ruby.Ruby;
import dev.zprestige.ruby.events.ModuleToggleEvent;
import dev.zprestige.ruby.events.Render3DEvent;
import dev.zprestige.ruby.settings.Setting;
import dev.zprestige.ruby.settings.impl.Key;
import dev.zprestige.ruby.settings.impl.Switch;
import net.minecraft.client.Minecraft;
import net.minecraftforge.common.MinecraftForge;
import org.lwjgl.input.Keyboard;

import java.util.ArrayList;
import java.util.List;

public class Module {
    public final Menu Menu = new Menu(this);
    protected final List<Setting> newSettings = new ArrayList<>();
    protected final Minecraft mc = Minecraft.getMinecraft();
    protected final Key keybind = Menu.Key("Keybind", Keyboard.KEY_NONE);
    protected final Switch enabled = Menu.Switch("Enabled");
    public boolean drawn = true;
    public int scrollY;
    protected String name;
    protected Category category;

    public void onEnable() {
    }

    public void onDisable() {
    }

    public void onTick() {
    }

    public void onOverlayTick() {
    }


    public void onGlobalRenderTick() {
    }

    public void onGlobalRenderTick(Render3DEvent event) {
    }

    public void enableModule() {
        setEnabled(true);
        onEnable();
        Ruby.eventBus.post(new ModuleToggleEvent.Enable(this));
        MinecraftForge.EVENT_BUS.register(this);
        Ruby.eventBus.register(this);
    }

    public void disableModule() {
        setEnabled(false);
        onDisable();
        Ruby.eventBus.post(new ModuleToggleEvent.Disable(this));
        MinecraftForge.EVENT_BUS.unregister(this);
        Ruby.eventBus.unregister(this);
    }

    public void disableModule(String message) {
        disableModule();
        Ruby.chatManager.sendMessage(message);
    }

    public boolean isEnabled() {
        return enabled.GetSwitch();
    }

    public void setEnabled(boolean enabled) {
        this.enabled.setValue(enabled);
    }

    public String getName() {
        return name;
    }

    public Category getCategory() {
        return category;
    }

    public Integer getKeybind() {
        return keybind.GetKey();
    }

    public void setKeybind(Integer keybind) {
        this.keybind.setValue(keybind);
    }

    public boolean nullCheck() {
        return mc.world == null || mc.player == null;
    }

    public List<Setting> getSettings() {
        return newSettings;
    }

    public Module withSuper(String name, Category category) {
        this.name = name;
        this.category = category;
        return this;
    }
}
