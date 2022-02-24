package dev.zprestige.ruby.module.client;

import com.mojang.realmsclient.gui.ChatFormatting;
import dev.zprestige.ruby.Ruby;
import dev.zprestige.ruby.eventbus.annotation.RegisterListener;
import dev.zprestige.ruby.events.ModuleToggleEvent;
import dev.zprestige.ruby.events.PacketEvent;
import dev.zprestige.ruby.module.Module;
import dev.zprestige.ruby.newsettings.impl.Switch;
import net.minecraft.network.play.server.SPacketChat;


public class Notify extends Module {
    public static Notify Instance;
    public final Switch modules = Menu.Switch("Modules");
    public final Switch totemPops = Menu.Switch("TotemPops");
    public final Switch zenovLolCounter = Menu.Switch("Zenov LOL counter");
    public int literalLOLS = 0, containingLOLS = 0;

    public Notify() {
        Instance = this;
    }

    @RegisterListener
    public void onPacketReceive(PacketEvent.PacketReceiveEvent event) {
        if (nullCheck() || !isEnabled() || !(event.getPacket() instanceof SPacketChat) || !zenovLolCounter.GetSwitch()) {
            return;
        }
        SPacketChat sPacketChat = (SPacketChat) event.getPacket();
        String chatMessage = sPacketChat.getChatComponent().getUnformattedText();
        boolean print = false;
        if (chatMessage.contains("ZenovJB")) {
            if (chatMessage.contains("LOL")) {
                containingLOLS++;
                print = true;
            }
            if (chatMessage.equals("<ZenovJB> LOL")) {
                literalLOLS++;
                print = true;
            }
            if (print) {
                Ruby.chatManager.sendMessage("ZenovJB LOL counter[Literal: " + literalLOLS + ", Containing: " + containingLOLS + "]");
            }
        }
    }

    @RegisterListener
    public void onModuleEnable(ModuleToggleEvent.Enable event) {
        if (!isEnabled() || nullCheck() || !modules.GetSwitch())
            return;
        Ruby.chatManager.sendRemovableMessage(ChatFormatting.WHITE + "" + ChatFormatting.BOLD + event.getModule().getName() + ChatFormatting.RESET + " has been toggled " + ChatFormatting.GREEN + "On" + ChatFormatting.RESET + ".", 1);
    }

    @RegisterListener
    public void onModuleDisable(ModuleToggleEvent.Disable event) {
        if (!isEnabled() || nullCheck() || !modules.GetSwitch())
            return;
        Ruby.chatManager.sendRemovableMessage(ChatFormatting.WHITE + "" + ChatFormatting.BOLD + event.getModule().getName() + ChatFormatting.RESET + " has been toggled " + ChatFormatting.RED + "Off" + ChatFormatting.RESET + ".", 1);
    }

}
