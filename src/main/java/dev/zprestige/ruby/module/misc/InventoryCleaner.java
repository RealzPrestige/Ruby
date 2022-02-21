package dev.zprestige.ruby.module.misc;

import dev.zprestige.ruby.module.Category;
import dev.zprestige.ruby.module.Module;
import dev.zprestige.ruby.module.ModuleInfo;
import dev.zprestige.ruby.setting.impl.BooleanSetting;
import dev.zprestige.ruby.setting.impl.IntegerSetting;
import dev.zprestige.ruby.setting.impl.ParentSetting;
import dev.zprestige.ruby.util.InventoryUtil;
import dev.zprestige.ruby.util.Timer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.Item;

@ModuleInfo(name = "InventoryCleaner", category = Category.Misc, description = "Removes useless shit from inventory")
public class InventoryCleaner extends Module {
    public IntegerSetting throwDelay = createSetting("Throw Delay", 100, 0, 500);
    public ParentSetting items = createSetting("Items");
    public BooleanSetting chorusFruits = createSetting("Chorus Fruits", false).setParent(items);
    public BooleanSetting obsidian = createSetting("Obsidian", false).setParent(items);
    public BooleanSetting enderChests = createSetting("EnderChest", false).setParent(items);
    public BooleanSetting swords = createSetting("Swords", false).setParent(items);
    public BooleanSetting pickaxe = createSetting("Pickaxe", false).setParent(items);
    public BooleanSetting pearls = createSetting("Pearls", false).setParent(items);
    public BooleanSetting bows = createSetting("Bows", false).setParent(items);
    public Timer throwTimer = new Timer();

    @Override
    public void onTick() {
        if (throwTimer.getTime(throwDelay.getValue()) && getItemSlot() != -1)
            throwAway(getItemSlot());
    }

    public int getItemSlot() {
        if (chorusFruits.getValue()) {
            int hotbarSlot = InventoryUtil.getItemFromHotbar(Items.CHORUS_FRUIT);
            if (hotbarSlot != -1) {
                int nonHotbarSlot = InventoryUtil.getItemSlotNonHotbar(Items.CHORUS_FRUIT);
                if (nonHotbarSlot != -1) {
                    return nonHotbarSlot;
                }
            }
        }
        if (obsidian.getValue()) {
            int hotbarSlot = InventoryUtil.getItemFromHotbar(Item.getItemFromBlock(Blocks.OBSIDIAN));
            if (hotbarSlot != -1) {
                int nonHotbarSlot = InventoryUtil.getItemSlotNonHotbar(Item.getItemFromBlock(Blocks.OBSIDIAN));
                if (nonHotbarSlot != -1) {
                    return nonHotbarSlot;
                }
            }
        }
        if (enderChests.getValue()) {
            int hotbarSlot = InventoryUtil.getItemFromHotbar(Item.getItemFromBlock(Blocks.ENDER_CHEST));
            if (hotbarSlot != -1) {
                int nonHotbarSlot = InventoryUtil.getItemSlotNonHotbar(Item.getItemFromBlock(Blocks.ENDER_CHEST));
                if (nonHotbarSlot != -1) {
                    return nonHotbarSlot;
                }
            }
        }
        if (swords.getValue()) {
            int hotbarSlot = InventoryUtil.getItemFromHotbar(Items.DIAMOND_SWORD);
            if (hotbarSlot != -1) {
                int nonHotbarSlot = InventoryUtil.getItemSlotNonHotbar(Items.DIAMOND_SWORD);
                if (nonHotbarSlot != -1) {
                    return nonHotbarSlot;
                }
            }
        }
        if (pickaxe.getValue()) {
            int hotbarSlot = InventoryUtil.getItemFromHotbar(Items.DIAMOND_PICKAXE);
            if (hotbarSlot != -1) {
                int nonHotbarSlot = InventoryUtil.getItemSlotNonHotbar(Items.DIAMOND_PICKAXE);
                if (nonHotbarSlot != -1) {
                    return nonHotbarSlot;
                }
            }
        }
        if (pearls.getValue()) {
            int hotbarSlot = InventoryUtil.getItemFromHotbar(Items.ENDER_PEARL);
            if (hotbarSlot != -1) {
                int nonHotbarSlot = InventoryUtil.getItemSlotNonHotbar(Items.ENDER_PEARL);
                if (nonHotbarSlot != -1) {
                    return nonHotbarSlot;
                }
            }
        }
        if (bows.getValue()) {
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
