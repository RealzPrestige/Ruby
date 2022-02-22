package dev.zprestige.ruby.command.impl;

import dev.zprestige.ruby.Ruby;
import dev.zprestige.ruby.command.Command;

public class PrefixCommand extends Command {

    public PrefixCommand() {
        super("prefix", "Prefix <Prefix>");
    }

    @Override
    public void listener(String string) {
        try {
            String[] split = string.split(" ");
            Ruby.commandManager.setPrefix(split[1]);
            completeMessage("set prefix to " + split[1]);
        } catch (Exception ignored) {
            throwException(format);
        }
    }
}
