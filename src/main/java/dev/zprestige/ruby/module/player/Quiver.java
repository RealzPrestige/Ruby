package dev.zprestige.ruby.module.player;

import dev.zprestige.ruby.module.Category;
import dev.zprestige.ruby.module.Module;
import dev.zprestige.ruby.module.ModuleInfo;
import dev.zprestige.ruby.util.InventoryUtil;
import net.minecraft.init.MobEffects;
import net.minecraft.init.PotionTypes;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTippedArrow;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.math.BlockPos;

@ModuleInfo(name = "Quiver", category = Category.Player, description = "arrow suicide")
public class Quiver extends Module {
    public static Quiver Instance;
    public int timer = 0;
    public int stage = 1;
    public int returnSlot = -1;

    public Quiver() {
        Instance = this;
    }

    @Override
    public void onDisable() {
        timer = 0;
        stage = 0;
        mc.gameSettings.keyBindUseItem.pressed = false;
        if (returnSlot != -1) {
            mc.playerController.windowClick(mc.player.inventoryContainer.windowId, 9, 0, ClickType.PICKUP, mc.player);
            mc.playerController.windowClick(mc.player.inventoryContainer.windowId, returnSlot, 0, ClickType.PICKUP, mc.player);
            mc.playerController.windowClick(mc.player.inventoryContainer.windowId, 9, 0, ClickType.PICKUP, mc.player);
            returnSlot = -1;
        }
    }

    @Override
    public void onTick() {
        if (mc.currentScreen != null)
            return;
        InventoryUtil.switchToHotbarSlot(ItemBow.class, false);
        if (InventoryUtil.findHotbarBlock(ItemBow.class) == -1) {
            disableModule("No bow found, disabling Quiver.");
            return;
        }
        mc.player.connection.sendPacket(new CPacketPlayer.Rotation(mc.player.rotationYaw, -90, mc.player.onGround));
        if (stage == 0) {
            if (!swapArrows()) {
                disableModule();
                return;
            }
            stage++;
        } else if (stage == 1) {
            stage++;
            timer++;
            return;
        } else if (stage == 2) {
            mc.gameSettings.keyBindUseItem.pressed = true;
            timer = 0;
            stage++;
        } else if (stage == 3) {
            if (timer > 4) stage++;
        } else if (stage == 4) {
            mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, mc.player.getHorizontalFacing()));
            mc.player.resetActiveHand();
            mc.gameSettings.keyBindUseItem.pressed = false;
            timer = 0;
            stage++;
        } else if (stage == 5) {
            if (timer < 10) {
                timer++;
                return;
            }
            stage = 0;
            timer = 0;
        }
        timer++;
    }

    public boolean swapArrows() {
        for (int i = 9; i < 45; i++) {
            if (mc.player.inventoryContainer.getInventory().get(i).getItem() instanceof ItemTippedArrow) {
                final ItemStack arrow = mc.player.inventoryContainer.getInventory().get(i);
                if (PotionUtils.getPotionFromItem(arrow).equals(PotionTypes.STRENGTH) || PotionUtils.getPotionFromItem(arrow).equals(PotionTypes.STRONG_STRENGTH) || PotionUtils.getPotionFromItem(arrow).equals(PotionTypes.LONG_STRENGTH)) {
                    if (!mc.player.isPotionActive(MobEffects.STRENGTH)) {
                        swapSlots(i);
                        return true;
                    }
                }
                if (PotionUtils.getPotionFromItem(arrow).equals(PotionTypes.SWIFTNESS) || PotionUtils.getPotionFromItem(arrow).equals(PotionTypes.LONG_SWIFTNESS) || PotionUtils.getPotionFromItem(arrow).equals(PotionTypes.STRONG_SWIFTNESS)) {
                    if (!mc.player.isPotionActive(MobEffects.SPEED)) {
                        swapSlots(i);
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public void swapSlots(int from) {
        if (from == 9)
            return;
        returnSlot = from;
        mc.playerController.windowClick(mc.player.inventoryContainer.windowId, from, 0, ClickType.PICKUP, mc.player);
        mc.playerController.windowClick(mc.player.inventoryContainer.windowId, 9, 0, ClickType.PICKUP, mc.player);
        mc.playerController.windowClick(mc.player.inventoryContainer.windowId, from, 0, ClickType.PICKUP, mc.player);
        mc.playerController.updateController();
    }
}