package dev.zprestige.ruby.manager;

import com.mojang.realmsclient.gui.ChatFormatting;
import dev.zprestige.ruby.Ruby;
import net.minecraft.client.Minecraft;
import net.minecraft.util.text.TextComponentString;

public class ChatManager {
    protected final Minecraft mc = Ruby.mc;
    public ChatFormatting prefixColor = ChatFormatting.RED;
    public String prefix = prefixColor + "[Ruby] " + ChatFormatting.GRAY;

    public void sendRawMessage(String message) {
        if (mc.player != null) {
            mc.player.sendMessage(new TextComponentString(message));
        }
    }

    public void sendMessage(String message) {
        sendRawMessage(prefix + message);
    }

    public void sendRemovableMessage(String message, int id) {
        mc.ingameGUI.getChatGUI().printChatMessageWithOptionalDeletion(new TextComponentString(prefix + message), id);
    }
}
