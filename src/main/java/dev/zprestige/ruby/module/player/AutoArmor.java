package dev.zprestige.ruby.module.player;

import dev.zprestige.ruby.module.Module;
import dev.zprestige.ruby.settings.impl.Slider;
import dev.zprestige.ruby.util.InventoryUtil;
import dev.zprestige.ruby.util.Timer;
import net.minecraft.init.Items;
import net.minecraft.inventory.ClickType;

public class AutoArmor extends Module {
    public static AutoArmor Instance;
    public final Slider takeOnDelay = Menu.Slider("Take On Delay", 0, 500);
    public Timer timer = new Timer();

    public AutoArmor() {
        Instance = this;
    }

    @Override
    public void onTick() {
        if (mc.currentScreen != null)
            return;
        if (getSlot() != -1 && timer.getTime((long) takeOnDelay.GetSlider())) {
            mc.playerController.windowClick(mc.player.inventoryContainer.windowId, getSlot(), 0, ClickType.QUICK_MOVE, mc.player);
            timer.setTime(0);
        }
    }

    public int getSlot() {
        if (mc.player.inventory.getStackInSlot(39).getItem().equals(Items.AIR) && InventoryUtil.getItemSlot(Items.DIAMOND_HELMET) != -1)
            return InventoryUtil.getItemSlot(Items.DIAMOND_HELMET);
        if (mc.player.inventory.getStackInSlot(38).getItem().equals(Items.AIR) && InventoryUtil.getItemSlot(Items.DIAMOND_CHESTPLATE) != -1)
            return InventoryUtil.getItemSlot(Items.DIAMOND_CHESTPLATE);
        if (mc.player.inventory.getStackInSlot(37).getItem().equals(Items.AIR) && InventoryUtil.getItemSlot(Items.DIAMOND_LEGGINGS) != -1)
            return InventoryUtil.getItemSlot(Items.DIAMOND_LEGGINGS);
        if (mc.player.inventory.getStackInSlot(36).getItem().equals(Items.AIR) && InventoryUtil.getItemSlot(Items.DIAMOND_BOOTS) != -1)
            return InventoryUtil.getItemSlot(Items.DIAMOND_BOOTS);
        return -1;
    }
}
