package dev.zprestige.ruby.module.player;

import dev.zprestige.ruby.module.Module;
import dev.zprestige.ruby.settings.impl.ComboBox;
import dev.zprestige.ruby.settings.impl.Key;
import dev.zprestige.ruby.settings.impl.Slider;
import dev.zprestige.ruby.settings.impl.Switch;
import dev.zprestige.ruby.util.InventoryUtil;
import net.minecraft.init.Items;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.network.play.client.CPacketPlayerTryUseItem;
import net.minecraft.util.EnumHand;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import java.util.stream.IntStream;

public class FastExp extends Module {
    public static FastExp Instance;
    public final ComboBox mode = Menu.ComboBox("Mode", new String[]{"Vanilla", "Packet"});
    public final ComboBox triggerMode = Menu.ComboBox("Trigger Mode", new String[]{"RightClick", "MiddleClick", "Custom"});
    public final Key customKey = Menu.Key("Custom Key", Keyboard.KEY_NONE);
    public final Slider packets = Menu.Slider("Packets", 1, 10);
    public final Switch handOnly = Menu.Switch("Hand Only");

    public FastExp() {
        Instance = this;
    }

    @Override
    public void onTick() {
        if (mc.currentScreen != null)
            return;
        switch (mode.GetCombo()) {
            case "Vanilla":
                if (handOnly.GetSwitch() && !mc.player.getHeldItemMainhand().getItem().equals(Items.EXPERIENCE_BOTTLE))
                    return;
                mc.rightClickDelayTimer = 0;
                break;
            case "Packet":
                if (triggerMode.GetCombo().equals("RightClick") && !mc.gameSettings.keyBindUseItem.isKeyDown())
                    return;

                if (triggerMode.GetCombo().equals("MiddleClick") && !Mouse.isButtonDown(2))
                    return;

                if (triggerMode.GetCombo().equals("Custom") && !Keyboard.isKeyDown(customKey.GetKey()))
                    return;

                if (handOnly.GetSwitch() && !mc.player.getHeldItemMainhand().getItem().equals(Items.EXPERIENCE_BOTTLE))
                    return;

                if (InventoryUtil.getItemFromHotbar(Items.EXPERIENCE_BOTTLE) == -1)
                    return;

                mc.player.connection.sendPacket(new CPacketHeldItemChange(InventoryUtil.getItemFromHotbar(Items.EXPERIENCE_BOTTLE)));
                IntStream.range(0, (int) packets.GetSlider()).forEach(i -> mc.player.connection.sendPacket(new CPacketPlayerTryUseItem(EnumHand.MAIN_HAND)));
                mc.player.connection.sendPacket(new CPacketHeldItemChange(mc.player.inventory.currentItem));
                break;
        }
    }
}
