package dev.zprestige.ruby.module.player;

import dev.zprestige.ruby.module.Module;
import dev.zprestige.ruby.setting.impl.BooleanSetting;
import dev.zprestige.ruby.setting.impl.IntegerSetting;
import dev.zprestige.ruby.setting.impl.KeySetting;
import dev.zprestige.ruby.setting.impl.ModeSetting;
import dev.zprestige.ruby.util.InventoryUtil;
import net.minecraft.init.Items;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.network.play.client.CPacketPlayerTryUseItem;
import net.minecraft.util.EnumHand;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import java.util.Arrays;
import java.util.function.Predicate;
import java.util.stream.IntStream;

public class FastExp extends Module {
    public static FastExp Instance;
    public ModeSetting mode = createSetting("Mode", "Vanilla", Arrays.asList("Vanilla", "Packet"));
    public ModeSetting triggerMode = createSetting("Trigger Mode", "Custom", Arrays.asList("RightClick", "MiddleClick", "Custom"), v -> mode.getValue().equals("Packet"));
    public KeySetting customKey = createSetting("Custom Key", Keyboard.KEY_NONE, v -> mode.getValue().equals("Packet") && triggerMode.getValue().equals("Custom"));
    public IntegerSetting packets = createSetting("Packets", 1, 1, 10, (Predicate<Integer>) v -> mode.getValue().equals("Packet"));
    public BooleanSetting handOnly = createSetting("Hand Only", false);

    public FastExp(){
        Instance = this;
    }

    @Override
    public void onTick() {
        if (mc.currentScreen != null)
            return;
        switch (mode.getValue()) {
            case "Vanilla":
                if (handOnly.getValue() && !mc.player.getHeldItemMainhand().getItem().equals(Items.EXPERIENCE_BOTTLE))
                    return;
                mc.rightClickDelayTimer = 0;
                break;
            case "Packet":
                if (triggerMode.getValue().equals("RightClick") && !mc.gameSettings.keyBindUseItem.isKeyDown())
                    return;

                if (triggerMode.getValue().equals("MiddleClick") && !Mouse.isButtonDown(2))
                    return;

                if (triggerMode.getValue().equals("Custom") && !Keyboard.isKeyDown(customKey.getKey()))
                    return;

                if (handOnly.getValue() && !mc.player.getHeldItemMainhand().getItem().equals(Items.EXPERIENCE_BOTTLE))
                    return;

                if (InventoryUtil.getItemFromHotbar(Items.EXPERIENCE_BOTTLE) == -1)
                    return;

                mc.player.connection.sendPacket(new CPacketHeldItemChange(InventoryUtil.getItemFromHotbar(Items.EXPERIENCE_BOTTLE)));
                IntStream.range(0, packets.getValue()).forEach(i -> mc.player.connection.sendPacket(new CPacketPlayerTryUseItem(EnumHand.MAIN_HAND)));
                mc.player.connection.sendPacket(new CPacketHeldItemChange(mc.player.inventory.currentItem));
                break;
        }
    }
}
