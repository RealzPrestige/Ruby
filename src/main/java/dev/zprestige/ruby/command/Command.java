package dev.zprestige.ruby.command;

import com.mojang.realmsclient.gui.ChatFormatting;
import dev.zprestige.ruby.Ruby;
import net.minecraft.client.Minecraft;
import net.minecraft.util.text.TextComponentString;

public class Command {
    protected final Minecraft mc = Ruby.mc;
    protected String text;
    protected String format;

    public Command(String text, String format) {
        this.text = text;
        this.format = format;
    }

    public String getText() {
        return text;
    }

    public String getFormat() {
        return format;
    }

    public void listener(String string) {
    }

    public void completeMessage(String format) {
        mc.player.sendMessage(new TextComponentString(ChatFormatting.GREEN + "Successfully " + format + "."));
    }

    public void throwException(String format) {
        mc.player.sendMessage(new TextComponentString(ChatFormatting.RED + "Invalid command, try " + format + "."));
    }
}
