package dev.zprestige.ruby.command.impl;

import com.mojang.realmsclient.gui.ChatFormatting;
import dev.zprestige.ruby.Ruby;
import dev.zprestige.ruby.command.Command;
import net.minecraft.util.text.TextComponentString;

public class HelpCommand extends Command {

    public HelpCommand() {
        super("help", "Help");
    }

    @Override
    public void listener(String string) {
        try {
            mc.player.sendMessage(new TextComponentString(ChatFormatting.RED + "Ruby Help:"));
            mc.player.sendMessage(new TextComponentString(ChatFormatting.GRAY + "===================="));
            Ruby.commandManager.getCommands().forEach(command -> Ruby.chatManager.sendMessage(ChatFormatting.GRAY + "\u2022 " + ChatFormatting.WHITE + command.getFormat()));
            mc.player.sendMessage(new TextComponentString(ChatFormatting.GRAY + "===================="));
        } catch (Exception ignored) {
            throwException(format);
        }
    }
}
