package dev.zprestige.ruby.util;

import com.mojang.realmsclient.gui.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.util.text.TextComponentString;

public class MessageUtil {
    static String prefix = ChatFormatting.RED + "[Ruby] ";

    public static void sendRawMessage(String message) {
        if (Minecraft.getMinecraft().player != null) {
            Minecraft.getMinecraft().player.sendMessage(new TextComponentString(message));
        }
    }

    public static void sendMessage(String message) {
        sendRawMessage(prefix + message);
    }

    public static void sendRemovableMessage(String message, int id) {
        Minecraft.getMinecraft().ingameGUI.getChatGUI().printChatMessageWithOptionalDeletion(new TextComponentString(prefix + message), id);
    }
}
