package dev.zprestige.ruby.command.impl;

import com.mojang.realmsclient.gui.ChatFormatting;
import dev.zprestige.ruby.Ruby;
import dev.zprestige.ruby.command.Command;

import java.util.Arrays;

public class NotifyColorCommand extends Command {

    public NotifyColorCommand() {
        super("notifycolor", "NotifyColor <ColorCode>");
    }

    @Override
    public void listener(String string) {
        try {
            String[] split = string.split(" ");
            Arrays.stream(ChatFormatting.values()).filter(chatFormatting -> split[1].charAt(0) == chatFormatting.getChar()).forEach(chatFormatting -> {
                Ruby.chatManager.prefixColor = chatFormatting;
                completeMessage("set Notify Color to " + split[1]);
            });
        } catch (Exception ignored) {
            throwException(format);
        }
    }
}
