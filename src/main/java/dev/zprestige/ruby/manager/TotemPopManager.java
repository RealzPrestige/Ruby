package dev.zprestige.ruby.manager;

import com.mojang.realmsclient.gui.ChatFormatting;
import dev.zprestige.ruby.Ruby;
import dev.zprestige.ruby.eventbus.annotation.RegisterListener;
import dev.zprestige.ruby.events.PacketEvent;
import dev.zprestige.ruby.events.PlayerChangeEvent;
import dev.zprestige.ruby.module.client.Notify;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.server.SPacketEntityStatus;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.HashMap;
import java.util.Map;
public class TotemPopManager {
    public HashMap<String, Integer> popMap = new HashMap<>();

    public TotemPopManager(){
        Ruby.eventBus.register(this);
    }
    @SubscribeEvent
    public void onLivingUpdateEvent(LivingEvent.LivingUpdateEvent event) {
        if (Ruby.mc.player != null && Ruby.mc.world != null) {
            Ruby.mc.world.playerEntities.stream().filter(entityPlayer -> entityPlayer != null && !(entityPlayer.getHealth() > 0.0f)).map(PlayerChangeEvent.Death::new).forEach(Ruby.eventBus::post);
        }
    }

    @RegisterListener
    public void onPacketReceive(PacketEvent.PacketReceiveEvent event) {
        if (Ruby.mc.world == null || Ruby.mc.player == null)
            return;
        if (event.getPacket() instanceof SPacketEntityStatus) {
            Entity entity = ((SPacketEntityStatus) event.getPacket()).getEntity(Ruby.mc.world);
            if (!(entity instanceof EntityPlayer))
                return;
            if (((SPacketEntityStatus) event.getPacket()).getOpCode() == 35)
                Ruby.eventBus.post(new PlayerChangeEvent.TotemPop((EntityPlayer) entity));
        }
    }

    @RegisterListener
    public void onDeath(PlayerChangeEvent.Death event) {
        if (popMap.containsKey(event.entityPlayer.getName())) {
            int pops = popMap.get(event.entityPlayer.getName());
            popMap.remove(event.entityPlayer.getName());
            int line = 0;
            for (char character : event.entityPlayer.getName().toCharArray()) {
                line += character;
                line *= 10;
            }
            if (Notify.Instance.isEnabled() && Notify.Instance.totemPops.getValue())
                Ruby.chatManager.sendRemovableMessage(ChatFormatting.WHITE + "" + ChatFormatting.BOLD + event.entityPlayer.getName() + ChatFormatting.WHITE + " has died after popping " + ChatFormatting.RED + pops + ChatFormatting.WHITE + (pops == 1 ? " totem." : " totems."), line);
        }
    }

    @RegisterListener
    public void onTotemPop(PlayerChangeEvent.TotemPop event) {
        int pops = 1;
        if (popMap.containsKey(event.entityPlayer.getName())) {
            pops = popMap.get(event.entityPlayer.getName());
            popMap.put(event.entityPlayer.getName(), ++pops);
        } else
            popMap.put(event.entityPlayer.getName(), pops);
        if (popMap.containsKey(event.entityPlayer.getName())) {
            int line = 0;
            for (char character : event.entityPlayer.getName().toCharArray()) {
                line += character;
                line *= 10;
            }
            if (Notify.Instance.isEnabled() && Notify.Instance.totemPops.getValue())
                Ruby.chatManager.sendRemovableMessage(ChatFormatting.WHITE + "" + ChatFormatting.BOLD + event.entityPlayer.getName() + ChatFormatting.WHITE + " has popped " + ChatFormatting.RED + pops + ChatFormatting.WHITE + (pops == 1 ? " totem." : " totems."), line);
        }
    }

    public int getPopsByPlayer(String name) {
        return popMap.entrySet().stream().filter(entry -> popMap.containsKey(name)).findFirst().map(Map.Entry::getValue).orElse(0);
    }
}
