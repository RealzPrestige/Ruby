package dev.zprestige.ruby.module.visual;

import dev.zprestige.ruby.Ruby;
import dev.zprestige.ruby.eventbus.annotation.RegisterListener;
import dev.zprestige.ruby.events.PlayerChangeEvent;
import dev.zprestige.ruby.module.Module;
import dev.zprestige.ruby.settings.impl.ColorBox;
import dev.zprestige.ruby.settings.impl.Slider;
import dev.zprestige.ruby.util.RenderUtil;
import net.minecraft.client.gui.ScaledResolution;
import org.lwjgl.opengl.Display;

import java.util.HashMap;
import java.util.Map;

public class HudNotifications extends Module {
    public final ColorBox backgroundColor = Menu.Color("Background Color");
    public final ColorBox surroundColor = Menu.Color("Surround Color");
    public final Slider y = Menu.Slider("Y", 0, 600);
    public final Slider removeSpeed = Menu.Slider("Remove Speed", 0.1f, 3.0f);
    public HashMap<String, Integer> totemPopMap = new HashMap<>();
    public HashMap<String, Float> notificationMap = new HashMap<>();

    @RegisterListener
    public void onTotemPop(PlayerChangeEvent.TotemPop event) {
        if (nullCheck() || !isEnabled() || event.entityPlayer.equals(mc.player))
            return;
        int pops = 1;
        if (totemPopMap.containsKey(event.entityPlayer.getName())) {
            pops = totemPopMap.get(event.entityPlayer.getName());
            totemPopMap.put(event.entityPlayer.getName(), ++pops);
        } else
            totemPopMap.put(event.entityPlayer.getName(), pops);

        String string;
        if (pops == 1)
            string = event.entityPlayer.getName() + " has popped 1 totem";
        else
            string = event.entityPlayer.getName() + " has popped " + pops + " totems";

        notificationMap.put(string, Ruby.fontManager.getStringWidth(string) / 2f);
    }

    @RegisterListener
    public void onDeath(PlayerChangeEvent.Death event) {
        if (nullCheck() || !isEnabled() || event.entityPlayer.equals(mc.player))
            return;
        if (totemPopMap.containsKey(event.entityPlayer.getName())) {
            int pops = totemPopMap.get(event.entityPlayer.getName());
            totemPopMap.remove(event.entityPlayer.getName());
            String string;
            if (pops == 1)
                string = event.entityPlayer.getName() + " has died after popping 1 totem";
            else
                string = event.entityPlayer.getName() + " has died after popping " + pops + " totems";
            notificationMap.put(string, Ruby.fontManager.getStringWidth(string) / 2f);
        }
    }

    @Override
    public void onFrame2D() {
        int screenWidth = new ScaledResolution(mc).getScaledWidth();
        int i = (int) (y.GetSlider() - 17);
        if (!Display.isActive() || !Display.isVisible()) {
            notificationMap.clear();
            return;
        }
        HashMap<String, Float> notificationMap1 = new HashMap<>(notificationMap);
        for (Map.Entry<String, Float> entry : notificationMap1.entrySet()) {
            notificationMap1.put(entry.getKey(), entry.getValue() - (entry.getValue() > 1.0f ? (entry.getValue() /  removeSpeed.GetSlider()) : removeSpeed.GetSlider()));
            if (entry.getValue() > 1.0f) {
                RenderUtil.drawRect((screenWidth / 2f) - (Ruby.fontManager.getStringWidth(entry.getKey()) / 2f) - 3, i += 17, ((screenWidth / 2f) - (Ruby.fontManager.getStringWidth(entry.getKey()) / 2f) - 3) + entry.getValue(), i + 16, surroundColor.GetColor().getRGB());
                RenderUtil.drawRect(screenWidth / 2f + (Ruby.fontManager.getStringWidth(entry.getKey()) / 2f) - entry.getValue(), i, (screenWidth / 2f) + (Ruby.fontManager.getStringWidth(entry.getKey()) / 2f) + 3, i + 16, surroundColor.GetColor().getRGB());
                RenderUtil.drawRect((screenWidth / 2f) - (Ruby.fontManager.getStringWidth(entry.getKey()) / 2f) - 2, i + 1, screenWidth / 2f + (Ruby.fontManager.getStringWidth(entry.getKey()) / 2f) + 2, i + 15, backgroundColor.GetColor().getRGB());
                Ruby.fontManager.drawStringWithShadow(entry.getKey(), (screenWidth / 2f) - (Ruby.fontManager.getStringWidth(entry.getKey()) / 2f), i + 7.5f - (Ruby.fontManager.getFontHeight() / 2f), -1);
            } else if (entry.getValue() > -16.0)
                i += 16 + entry.getValue();
            else {
                notificationMap.remove(entry.getKey());
                return;
            }
        }
        notificationMap = notificationMap1;
    }
}
