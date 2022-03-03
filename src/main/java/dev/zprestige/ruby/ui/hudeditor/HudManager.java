package dev.zprestige.ruby.ui.hudeditor;

import dev.zprestige.ruby.Ruby;
import dev.zprestige.ruby.ui.hudeditor.components.HudComponent;
import dev.zprestige.ruby.ui.hudeditor.components.impl.Watermark;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.ArrayList;

public class HudManager {
    protected final Minecraft mc = Ruby.mc;
    protected final ArrayList<HudComponent> hudComponents = new ArrayList<>();

    public HudManager() {
        MinecraftForge.EVENT_BUS.register(this);
        hudComponents.add(new Watermark());
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onRenderGameOverlayTextEvent(RenderGameOverlayEvent.Text event) {
        if (safe()) {
            hudComponents.stream().filter(HudComponent::isEnabled).forEach(HudComponent::render);
        }
    }

    public ArrayList<HudComponent> getHudComponents() {
        return hudComponents;
    }

    protected boolean safe() {
        return mc.world != null && mc.player != null;
    }
}
