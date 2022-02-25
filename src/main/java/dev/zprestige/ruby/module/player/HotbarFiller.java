package dev.zprestige.ruby.module.player;

import dev.zprestige.ruby.module.Module;
import dev.zprestige.ruby.newsettings.impl.Slider;
import dev.zprestige.ruby.setting.impl.IntegerSetting;
import dev.zprestige.ruby.util.Timer;
import net.minecraft.init.Items;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.ItemStack;

import java.util.stream.IntStream;

public class HotbarFiller extends Module {
    public final Slider delay = Menu.Slider("Delay", 0, 500);
    public final Slider fillAt = Menu.Slider("Fill At", 1, 64);
    public Timer timer = new Timer();

    @Override
    public void onTick() {
        if (mc.currentScreen != null || !timer.getTime(delay.GetSlider()))
            return;
        if (IntStream.range(0, 9).anyMatch(this::refillSlot))
            timer.setTime(0);
    }

    public boolean refillSlot(int slot) {
        ItemStack stack = mc.player.inventory.getStackInSlot(slot);
        if ((stack.isEmpty() || stack.getItem() == Items.AIR) || !stack.isStackable() || stack.getCount() >= stack.getMaxStackSize() || (stack.getItem().equals(Items.GOLDEN_APPLE) && stack.getCount() >= fillAt.GetSlider()) || (stack.getItem().equals(Items.EXPERIENCE_BOTTLE) && stack.getCount() > fillAt.GetSlider()))
            return false;
        for (int i = 9; i < 36; ++i) {
            ItemStack item = mc.player.inventory.getStackInSlot(i);
            if (!item.isEmpty() && (stack.getItem() == item.getItem() && stack.getDisplayName().equals(item.getDisplayName()))) {
                mc.playerController.windowClick(mc.player.inventoryContainer.windowId, i, 0, ClickType.QUICK_MOVE, mc.player);
                mc.playerController.updateController();
                return true;
            }
        }
        return false;
    }
}