package dev.zprestige.ruby.command.impl;

import dev.zprestige.ruby.Ruby;
import dev.zprestige.ruby.command.Command;
import dev.zprestige.ruby.module.Module;
import org.lwjgl.input.Keyboard;

import java.io.File;

public class BindCommand extends Command {

    public BindCommand() {
        super("config", "Bind <Module> <Keybind>");
    }

    @Override
    public void listener(String string) {
        try {
            String[] split = string.split(" ");
            for (Module module : Ruby.moduleManager.moduleList){
                if (module.getName().equals(split[1])){
                    final int keybind = Keyboard.getKeyIndex(split[2]);
                    module.setKeybind(keybind);
                    completeMessage("keyinded " + module.getName() + "'s keybind to " + keybind);
                }
            }
        } catch (Exception ignored) {
            throwException(format);
        }
    }
}
