package dev.zprestige.ruby.module.misc;

import dev.zprestige.ruby.module.Module;
import dev.zprestige.ruby.newsettings.impl.Parent;
import dev.zprestige.ruby.newsettings.impl.Slider;
import dev.zprestige.ruby.newsettings.impl.Switch;
import dev.zprestige.ruby.util.InventoryUtil;
import dev.zprestige.ruby.util.Timer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.Item;

public class InventoryCleaner extends Module {
    public final Slider throwDelay = Menu.Slider("Throw Delay", 0, 500);
    public final Parent items = Menu.Parent("Items");
    public final Switch chorusFruits = Menu.Switch("Chorus Fruits").parent(items);
    public final Switch obsidian = Menu.Switch("Obsidian").parent(items);
    public final Switch enderChests = Menu.Switch("EnderChest").parent(items);
    public final Switch swords = Menu.Switch("Swords").parent(items);
    public final Switch pickaxe = Menu.Switch("Pickaxe").parent(items);
    public final Switch pearls = Menu.Switch("Pearls").parent(items);
    public final Switch bows = Menu.Switch("Bows").parent(items);
    public Timer throwTimer = new Timer();

    @Override
    public void onTick() {
        if (throwTimer.getTime(throwDelay.GetSlider()) && getItemSlot() != -1)
            throwAway(getItemSlot());
    }

    public int getItemSlot() {
        if (chorusFruits.GetSwitch()) {
            int hotbarSlot = InventoryUtil.getItemFromHotbar(Items.CHORUS_FRUIT);
            if (hotbarSlot != -1) {
                int nonHotbarSlot = InventoryUtil.getItemSlotNonHotbar(Items.CHORUS_FRUIT);
                if (nonHotbarSlot != -1) {
                    return nonHotbarSlot;
                }
            }
        }
        if (obsidian.GetSwitch()) {
            int hotbarSlot = InventoryUtil.getItemFromHotbar(Item.getItemFromBlock(Blocks.OBSIDIAN));
            if (hotbarSlot != -1) {
                int nonHotbarSlot = InventoryUtil.getItemSlotNonHotbar(Item.getItemFromBlock(Blocks.OBSIDIAN));
                if (nonHotbarSlot != -1) {
                    return nonHotbarSlot;
                }
            }
        }
        if (enderChests.GetSwitch()) {
            int hotbarSlot = InventoryUtil.getItemFromHotbar(Item.getItemFromBlock(Blocks.ENDER_CHEST));
            if (hotbarSlot != -1) {
                int nonHotbarSlot = InventoryUtil.getItemSlotNonHotbar(Item.getItemFromBlock(Blocks.ENDER_CHEST));
                if (nonHotbarSlot != -1) {
                    return nonHotbarSlot;
                }
            }
        }
        if (swords.GetSwitch()) {
            int hotbarSlot = InventoryUtil.getItemFromHotbar(Items.DIAMOND_SWORD);
            if (hotbarSlot != -1) {
                int nonHotbarSlot = InventoryUtil.getItemSlotNonHotbar(Items.DIAMOND_SWORD);
                if (nonHotbarSlot != -1) {
                    return nonHotbarSlot;
                }
            }
        }
        if (pickaxe.GetSwitch()) {
            int hotbarSlot = InventoryUtil.getItemFromHotbar(Items.DIAMOND_PICKAXE);
            if (hotbarSlot != -1) {
                int nonHotbarSlot = InventoryUtil.getItemSlotNonHotbar(Items.DIAMOND_PICKAXE);
                if (nonHotbarSlot != -1) {
                    return nonHotbarSlot;
                }
            }
        }
        if (pearls.GetSwitch()) {
            int hotbarSlot = InventoryUtil.getItemFromHotbar(Items.ENDER_PEARL);
            if (hotbarSlot != -1) {
                int nonHotbarSlot = InventoryUtil.getItemSlotNonHotbar(Items.ENDER_PEARL);
                if (nonHotbarSlot != -1) {
                    return nonHotbarSlot;
                }
            }
        }
        if (bows.GetSwitch()) {
            int hotbarSlot = InventoryUtil.getItemFromHotbar(Items.BOW);
            if (hotbarSlot != -1) {
                return InventoryUtil.getItemSlotNonHotbar(Items.BOW);
            }
        }
        return -1;
    }

    public void throwAway(int i) {
        mc.playerController.windowClick(0, i, 0, ClickType.PICKUP, mc.player);
        mc.playerController.windowClick(0, -999, 0, ClickType.PICKUP, mc.player);
        throwTimer.setTime(0);
    }

}
