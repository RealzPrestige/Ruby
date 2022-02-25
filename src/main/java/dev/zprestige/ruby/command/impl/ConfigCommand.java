package dev.zprestige.ruby.command.impl;

import dev.zprestige.ruby.Ruby;
import dev.zprestige.ruby.command.Command;

import java.io.File;

public class ConfigCommand extends Command {

    public ConfigCommand() {
        super("config", "Config <Save/Load/Delete> <Folder> (OnlyVisuals) <True/False>");
    }

    @Override
    public void listener(String string) {
        try {
            String[] split = string.split(" ");
            if (split[1].equals("save")) {
                Ruby.configManager.save(split[2], split[3].equals("true"));
                completeMessage("saved config " + split[2]);
            }
            if (split[1].equals("load")) {
                Ruby.configManager.load(split[2], split[3].equals("true"));
                completeMessage("loaded config " + split[2]);
            }
            if (split[1].equals("delete")) {
                File path = new File(mc.gameDir + File.separator + "ClientRewrite" + File.separator + "Configs" + split[2]);
                if (path.exists()) {
                    //noinspection ResultOfMethodCallIgnored
                    path.delete();
                }
            }
        } catch (Exception ignored) {
            throwException(format);
        }
    }
}
