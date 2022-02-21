package dev.zprestige.ruby.module.client;

import com.mojang.realmsclient.gui.ChatFormatting;
import dev.zprestige.ruby.events.ModuleToggleEvent;
import dev.zprestige.ruby.events.PacketEvent;
import dev.zprestige.ruby.module.Category;
import dev.zprestige.ruby.module.Module;
import dev.zprestige.ruby.module.ModuleInfo;
import dev.zprestige.ruby.setting.impl.BooleanSetting;
import dev.zprestige.ruby.util.MessageUtil;
import net.minecraft.network.play.server.SPacketChat;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;


@ModuleInfo(name = "Notify", category = Category.Client, description = "Notifies when modules get enabled.")
public class Notify extends Module {
    public static Notify Instance;
    public BooleanSetting modules = createSetting("Modules", false);
    public BooleanSetting totemPops = createSetting("TotemPops", false);
    public BooleanSetting zenovLolCounter = createSetting("Zenov LOL counter", false);
    public int literalLOLS = 0, containingLOLS = 0;

    public Notify() {
        Instance = this;
    }

    @SubscribeEvent
    public void onPacketReceive(PacketEvent.PacketReceiveEvent event) {
        if (nullCheck() || !isEnabled() || !(event.getPacket() instanceof SPacketChat) || !zenovLolCounter.getValue()) {
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
                MessageUtil.sendMessage("ZenovJB LOL counter[Literal: " + literalLOLS + ", Containing: " + containingLOLS + "]");
            }
        }
    }

    @SubscribeEvent
    public void onModuleEnable(ModuleToggleEvent.Enable event) {
        if (!isEnabled() || nullCheck() || !modules.getValue())
            return;
        MessageUtil.sendRemovableMessage(ChatFormatting.WHITE + "" + ChatFormatting.BOLD + event.getModule().getName() + ChatFormatting.RESET + " has been toggled " + ChatFormatting.GREEN + "On" + ChatFormatting.RESET + ".", 1);
    }

    @SubscribeEvent
    public void onModuleDisable(ModuleToggleEvent.Disable event) {
        if (!isEnabled() || nullCheck() || !modules.getValue())
            return;
        MessageUtil.sendRemovableMessage(ChatFormatting.WHITE + "" + ChatFormatting.BOLD + event.getModule().getName() + ChatFormatting.RESET + " has been toggled " + ChatFormatting.RED + "Off" + ChatFormatting.RESET + ".", 1);
    }

}
