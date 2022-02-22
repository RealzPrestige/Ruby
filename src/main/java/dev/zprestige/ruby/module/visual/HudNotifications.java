package dev.zprestige.ruby.module.visual;

import dev.zprestige.ruby.Ruby;
import dev.zprestige.ruby.eventbus.annotation.RegisterListener;
import dev.zprestige.ruby.events.PlayerChangeEvent;
import dev.zprestige.ruby.module.Category;
import dev.zprestige.ruby.module.Module;
import dev.zprestige.ruby.module.ModuleInfo;
import dev.zprestige.ruby.setting.impl.ColorSetting;
import dev.zprestige.ruby.setting.impl.FloatSetting;
import dev.zprestige.ruby.setting.impl.IntegerSetting;
import dev.zprestige.ruby.util.RenderUtil;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.Display;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

@ModuleInfo(name = "HudNotifications", category = Category.Visual, description = "huds the notifications")
public class HudNotifications extends Module {
    public ColorSetting backgroundColor = createSetting("Background Color", new Color(0x646363));
    public ColorSetting surroundColor = createSetting("Surround Color", new Color(0xA091FF));
    public IntegerSetting y = createSetting("Y", 400, 0, 600);
    public FloatSetting removeSpeed = createSetting("Remove Speed", 1.0f, 0.1f, 3.0f);
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

        notificationMap.put(string, Ruby.rubyFont.getStringWidth(string) / 2f);
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
            notificationMap.put(string, Ruby.rubyFont.getStringWidth(string) / 2f);
        }
    }

    @Override
    public void onOverlayTick() {
        int screenWidth = new ScaledResolution(mc).getScaledWidth();
        int i = y.getValue() - 17;
        if  (!Display.isActive() || !Display.isVisible()){
            notificationMap.clear();
            return;
        }
        HashMap<String, Float> notificationMap1 = new HashMap<>(notificationMap);
        for (Map.Entry<String, Float> entry : notificationMap1.entrySet()) {
            notificationMap1.put(entry.getKey(), entry.getValue() - removeSpeed.getValue());
            if (entry.getValue() > 1.0f) {
                RenderUtil.drawRect((screenWidth / 2f) - (Ruby.rubyFont.getStringWidth(entry.getKey()) / 2f) - 3, i += 17, ((screenWidth / 2f) - (Ruby.rubyFont.getStringWidth(entry.getKey()) / 2f) - 3) + entry.getValue(), i + 16, surroundColor.getValue().getRGB());
                RenderUtil.drawRect(screenWidth / 2f + (Ruby.rubyFont.getStringWidth(entry.getKey()) / 2f) - entry.getValue(), i, (screenWidth / 2f) + (Ruby.rubyFont.getStringWidth(entry.getKey()) / 2f) + 3, i + 16, surroundColor.getValue().getRGB());
                RenderUtil.drawRect((screenWidth / 2f) - (Ruby.rubyFont.getStringWidth(entry.getKey()) / 2f) - 2, i + 1, screenWidth / 2f + (Ruby.rubyFont.getStringWidth(entry.getKey()) / 2f) + 2, i + 15, backgroundColor.getValue().getRGB());
                Ruby.rubyFont.drawStringWithShadow(entry.getKey(), (screenWidth / 2f) - (Ruby.rubyFont.getStringWidth(entry.getKey()) / 2f), i + 7.5f - (Ruby.rubyFont.getHeight(entry.getKey()) / 2f), -1);
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
