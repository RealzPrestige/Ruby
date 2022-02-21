package dev.zprestige.ruby.module.misc;

import dev.zprestige.ruby.events.PacketEvent;
import dev.zprestige.ruby.module.Category;
import dev.zprestige.ruby.module.Module;
import dev.zprestige.ruby.module.ModuleInfo;
import dev.zprestige.ruby.setting.impl.BooleanSetting;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@ModuleInfo(name = "PacketLogger" , category = Category.Misc, description = "logs packets to latest.txt")
public class PacketLogger extends Module {
    public static final Logger Logger = LogManager.getLogger("[PacketLogger] ");

    @Override
    public void onEnable() {
        Logger.info("Logger Started.");
    }

    @Override
    public void onDisable() {
        Logger.info("Logger Finished.");
    }

    @SubscribeEvent
    public void onPacketSend(PacketEvent.PacketSendEvent event) {
        if (!isEnabled())
            return;
        Logger.info("[Send] " + event.getPacket());
    }

    @SubscribeEvent
    public void onPacketReceive(PacketEvent.PacketReceiveEvent event) {
        if (!isEnabled())
            return;
        Logger.info("[Receive] " + event.getPacket());
    }
}
