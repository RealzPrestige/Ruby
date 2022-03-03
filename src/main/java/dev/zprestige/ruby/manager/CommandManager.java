package dev.zprestige.ruby.manager;

import com.mojang.realmsclient.gui.ChatFormatting;
import dev.zprestige.ruby.Ruby;
import dev.zprestige.ruby.command.Command;
import dev.zprestige.ruby.command.impl.*;
import dev.zprestige.ruby.eventbus.annotation.RegisterListener;
import dev.zprestige.ruby.events.PacketEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.network.play.client.CPacketChatMessage;
import net.minecraft.util.text.TextComponentString;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class CommandManager {
    protected final ArrayList<Command> commands = new ArrayList<>();
    protected final Minecraft mc = Ruby.mc;
    protected String prefix = ".";

    public CommandManager() {
        Ruby.eventBus.register(this);
        commands.add(new ConfigCommand());
        commands.add(new PrefixCommand());
        commands.add(new NotifyColorCommand());
        commands.add(new FriendCommand());
        commands.add(new FakeHackerCommand());
        commands.add(new HelpCommand());
    }

    public ArrayList<Command> getCommands() {
        return commands;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    @RegisterListener
    public void onPacketSend(PacketEvent.PacketSendEvent event) {
        if (mc.world == null || mc.player == null || !(event.getPacket() instanceof CPacketChatMessage))
            return;
        CPacketChatMessage packet = (CPacketChatMessage) event.getPacket();
        if (packet.getMessage().startsWith(prefix)) {
            event.setCancelled(true);
        } else {
            return;
        }
        String first = packet.getMessage().split(" ")[0];
        String command = first.toLowerCase();
        ArrayList<Command> commands1 = commands.stream().filter(command1 -> command1.getText().equals(command.replace(prefix, ""))).collect(Collectors.toCollection(ArrayList::new));
        if (!commands1.isEmpty()) {
            commands1.forEach(command1 -> command1.listener(packet.getMessage().toLowerCase()));
            return;
        }
        mc.player.sendMessage(new TextComponentString(ChatFormatting.RED + "No such command found."));
    }
}
