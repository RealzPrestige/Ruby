package dev.zprestige.ruby.module.misc;

import dev.zprestige.ruby.eventbus.annotation.RegisterListener;
import dev.zprestige.ruby.events.PacketEvent;
import dev.zprestige.ruby.module.Module;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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

    @RegisterListener
    public void onPacketSend(PacketEvent.PacketSendEvent event) {
        if (!isEnabled())
            return;
        Logger.info("[Send] " + event.getPacket());
    }

    @RegisterListener
    public void onPacketReceive(PacketEvent.PacketReceiveEvent event) {
        if (!isEnabled())
            return;
        Logger.info("[Receive] " + event.getPacket());
    }
}
