package dev.zprestige.ruby.module.misc;

import dev.zprestige.ruby.eventbus.annotation.RegisterListener;
import dev.zprestige.ruby.ui.buttons.MiddleClickInterface;
import dev.zprestige.ruby.events.PacketEvent;
import dev.zprestige.ruby.module.Module;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.server.SPacketChat;
import net.minecraft.util.math.RayTraceResult;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Mouse;

import java.util.ArrayList;

public class MiddleClick extends Module {
    public ArrayList<EntityPlayer> blockedList = new ArrayList<>();
    public static MiddleClick Instance;
    public MiddleClick() {
        Instance = this;
    }

    @Override
    public void onTick() {
        RayTraceResult result = mc.objectMouseOver;
        if (result != null && result.typeOfHit == RayTraceResult.Type.ENTITY && result.entityHit instanceof EntityPlayer && Mouse.isButtonDown(2))
            mc.displayGuiScreen(new MiddleClickInterface(new ScaledResolution(mc), result.entityHit));
        if (mc.currentScreen instanceof MiddleClickInterface && !Mouse.isButtonDown(2))
            mc.displayGuiScreen(null);
    }

    @RegisterListener
    public void onPacketReceive(PacketEvent.PacketReceiveEvent event) {
        if (nullCheck() || !isEnabled() || !(event.getPacket() instanceof SPacketChat))
            return;
        blockedList.stream().filter(player -> ((SPacketChat) event.getPacket()).getChatComponent().getUnformattedText().contains(player.getName())).map(player -> true).forEach(event::setCancelled);
    }

    @SubscribeEvent
    public void onRenderOverlay(RenderGameOverlayEvent event) {
        if (isEnabled() && mc.currentScreen instanceof MiddleClickInterface && event.getType().equals(RenderGameOverlayEvent.ElementType.ALL))
            event.setCanceled(true);
    }
}
