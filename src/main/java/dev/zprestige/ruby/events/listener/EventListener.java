package dev.zprestige.ruby.events.listener;

import com.google.common.base.Strings;
import dev.zprestige.ruby.Ruby;
import dev.zprestige.ruby.eventbus.annotation.RegisterListener;
import dev.zprestige.ruby.events.*;
import dev.zprestige.ruby.module.Module;
import dev.zprestige.ruby.module.client.ClickGui;
import dev.zprestige.ruby.module.visual.Nametags;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.network.play.server.SPacketPlayerListItem;
import net.minecraft.network.play.server.SPacketSoundEffect;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;
import org.lwjgl.input.Keyboard;

import java.util.ArrayList;

public class EventListener {
    protected final Minecraft mc = Ruby.mc;
    protected final ArrayList<Module> moduleList = new ArrayList<>(Ruby.moduleManager.moduleList);

    public EventListener() {
        MinecraftForge.EVENT_BUS.register(this);
        Ruby.eventBus.register(this);
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onLivingUpdateEvent(LivingEvent.LivingUpdateEvent event) {
        if (checkNull() && event.getEntity().getEntityWorld().isRemote && event.getEntityLiving().equals(mc.player)) {
            moduleList.stream().filter(Module::isEnabled).forEach(Module::onTick);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onRenderGameOverlayTextEvent(RenderGameOverlayEvent.Text event) {
        if (checkNull())
            moduleList.stream().filter(Module::isEnabled).forEach(Module::onOverlayTick);
    }

    @SubscribeEvent
    public void onClientConnect(FMLNetworkEvent.ClientDisconnectionFromServerEvent event) {
        Ruby.eventBus.post(new SelfLogoutEvent());
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onRenderWorldLastEvent(RenderWorldLastEvent event) {
        mc.profiler.startSection("ruby");
        if (checkNull()) {
            Render3DEvent render3DEvent = new Render3DEvent(event.getPartialTicks());
            Ruby.holeManager.onRenderWorldLastEvent();
            if (Nametags.Instance.isEnabled())
                Nametags.Instance.onGlobalRenderTick(render3DEvent);
            moduleList.stream().filter(Module::isEnabled).forEach(Module::onGlobalRenderTick);
            moduleList.stream().filter(module -> module.isEnabled() && !(module instanceof Nametags)).forEach(module -> module.onGlobalRenderTick(render3DEvent));
        }
        mc.profiler.endSection();
    }


    @SubscribeEvent
    public void onRenderOverlay(RenderGameOverlayEvent event) {
        if (ClickGui.Instance.isEnabled()){
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void onKeyInput(InputEvent.KeyInputEvent event) {
        if (checkNull()) {
            Ruby.moduleManager.moduleList.stream().filter(module -> Keyboard.getEventKeyState() && module.getKeybind().equals(Keyboard.getEventKey())).forEach(module -> {
                if (module.isEnabled()) {
                    module.disableModule();
                } else {
                    module.enableModule();
                }
            });
        }
    }

    @RegisterListener
    public void onPacketReceive(PacketEvent.PacketReceiveEvent event) {
        if (!checkNull())
            return;
        if (event.getPacket() instanceof SPacketSoundEffect && ((SPacketSoundEffect) event.getPacket()).getSound() == SoundEvents.ITEM_CHORUS_FRUIT_TELEPORT)
            Ruby.eventBus.post(new ChorusEvent(((SPacketSoundEffect) event.getPacket()).getX(), ((SPacketSoundEffect) event.getPacket()).getY(), ((SPacketSoundEffect) event.getPacket()).getZ()));
        if (event.getPacket() instanceof SPacketPlayerListItem) {
            for (SPacketPlayerListItem.AddPlayerData data : ((SPacketPlayerListItem) event.getPacket()).getEntries()) {
                if (data != null && (!Strings.isNullOrEmpty(data.getProfile().getName()) || data.getProfile().getId() != null)) {
                    EntityPlayer entity = mc.world.getPlayerEntityByUUID(data.getProfile().getId());
                    if (((SPacketPlayerListItem) event.getPacket()).getAction().equals(SPacketPlayerListItem.Action.ADD_PLAYER)) {
                        Ruby.eventBus.post(new LogoutEvent.LoginEvent(entity));
                    } else if (((SPacketPlayerListItem) event.getPacket()).getAction().equals(SPacketPlayerListItem.Action.REMOVE_PLAYER)) {

                        if (entity != null)
                            Ruby.eventBus.post(new LogoutEvent(entity, entity.getPosition(), System.currentTimeMillis(), entity.getEntityId()));
                    }
                }
            }
        }
    }

    protected boolean checkNull() {
        return mc.player != null && mc.world != null;
    }
}
