package dev.zprestige.ruby.module.player;

import dev.zprestige.ruby.module.Module;
import dev.zprestige.ruby.setting.impl.BooleanSetting;
import dev.zprestige.ruby.setting.impl.IntegerSetting;
import dev.zprestige.ruby.setting.impl.ParentSetting;
import dev.zprestige.ruby.util.InventoryUtil;
import dev.zprestige.ruby.util.Timer;
import net.minecraft.init.Items;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketPlayerTryUseItem;
import net.minecraft.util.EnumHand;

import java.util.function.Predicate;
import java.util.stream.IntStream;

public class AutoSingleMend extends Module {
    public final Slider threshold = Menu.Switch("Threshold", 90, 1, 100);
    public final Slider actionDelay = Menu.Switch("Action Delay", 50, 0, 1000);
    public final Parent exp = Menu.Switch("Exp");
    public final Switch packetExp = Menu.Switch("Packet Exp").parent(exp);
    public final Switch rotateDown = Menu.Switch("Rotate Down", v -> packetExp.getValue()).parent(exp);
    public final Slider packetSpeed = Menu.Switch("Packet Speed", 1, 1, 10, (Predicate<Integer>) v -> packetExp.getValue()).parent(exp);
    public Timer timer = new Timer();
    public boolean turnedOffAutoArmor;

    @Override
    public void onEnable() {
        if (AutoArmor.Instance.isEnabled()) {
            AutoArmor.Instance.disableModule();
            turnedOffAutoArmor = true;
        }
    }

    @Override
    public void onDisable() {
        if (turnedOffAutoArmor && !AutoArmor.Instance.isEnabled()) {
            AutoArmor.Instance.enableModule();
            turnedOffAutoArmor = false;
        }
    }

    @Override
    public void onTick() {
        int mendableArmor = getMendableArmorInArmorSlots();
        if (timer.getTime(actionDelay.getValue()) && takeOff(mendableArmor)) {
            takeOff(mendableArmor);
            timer.setTime(0);
            return;
        }
        if (packetExp.getValue()) {
            float prevPitch = mc.player.rotationPitch;
            int slot = InventoryUtil.getItemFromHotbar(Items.EXPERIENCE_BOTTLE);
            if (slot == -1){
                disableModule("No exp found in hotbar, disabling AutoSingleMend.");
                return;
            }
            if (rotateDown.getValue())
                mc.player.connection.sendPacket(new CPacketPlayer.Rotation(mc.player.rotationYaw, 90, mc.player.onGround));
            mc.player.connection.sendPacket(new CPacketHeldItemChange(slot));
            IntStream.range(0, packetSpeed.getValue()).forEach(i -> mc.player.connection.sendPacket(new CPacketPlayerTryUseItem(EnumHand.MAIN_HAND)));
            mc.player.connection.sendPacket(new CPacketHeldItemChange(mc.player.inventory.currentItem));
            if (rotateDown.getValue())
                mc.player.connection.sendPacket(new CPacketPlayer.Rotation(mc.player.rotationYaw, prevPitch, mc.player.onGround));
        }
    }

    public void quickMovePiece(int i) {
        mc.playerController.windowClick(mc.player.inventoryContainer.windowId, i, 0, ClickType.QUICK_MOVE, mc.player);
    }

    public boolean takeOff(int i) {
        switch (i) {
            case 5:
                if (!mc.player.inventory.getStackInSlot(38).getItem().equals(Items.AIR)) {
                    quickMovePiece(6);
                    return true;
                }
                if (!mc.player.inventory.getStackInSlot(37).getItem().equals(Items.AIR)) {
                    quickMovePiece(7);
                    return true;
                }
                if (!mc.player.inventory.getStackInSlot(36).getItem().equals(Items.AIR)) {
                    quickMovePiece(8);
                    return true;
                }
                break;
            case 6:
                if (!mc.player.inventory.getStackInSlot(39).getItem().equals(Items.AIR)) {
                    quickMovePiece(5);
                    return true;
                }
                if (!mc.player.inventory.getStackInSlot(37).getItem().equals(Items.AIR)) {
                    quickMovePiece(7);
                    return true;
                }
                if (!mc.player.inventory.getStackInSlot(36).getItem().equals(Items.AIR)) {
                    quickMovePiece(8);
                    return true;
                }
                break;
            case 7:
                if (!mc.player.inventory.getStackInSlot(39).getItem().equals(Items.AIR)) {
                    quickMovePiece(5);
                    return true;
                }
                if (!mc.player.inventory.getStackInSlot(38).getItem().equals(Items.AIR)) {
                    quickMovePiece(6);
                    return true;
                }
                if (!mc.player.inventory.getStackInSlot(36).getItem().equals(Items.AIR)) {
                    quickMovePiece(8);
                    return true;
                }
                break;
            case 8:
                if (!mc.player.inventory.getStackInSlot(39).getItem().equals(Items.AIR)) {
                    quickMovePiece(5);
                    return true;
                }
                if (!mc.player.inventory.getStackInSlot(38).getItem().equals(Items.AIR)) {
                    quickMovePiece(6);
                    return true;
                }
                if (!mc.player.inventory.getStackInSlot(37).getItem().equals(Items.AIR)) {
                    quickMovePiece(7);
                    return true;
                }
                break;
        }
        return false;
    }


    public int getMendableArmorInArmorSlots() {
        ItemStack head = mc.player.inventory.getStackInSlot(39);
        ItemStack chest = mc.player.inventory.getStackInSlot(38);
        ItemStack leggings = mc.player.inventory.getStackInSlot(37);
        ItemStack feet = mc.player.inventory.getStackInSlot(36);

        if (!head.getItem().equals(Items.AIR) && getPercentage(head) < threshold.getValue()) {
            return 5;
        }
        if (!chest.getItem().equals(Items.AIR) && getPercentage(chest) < threshold.getValue()) {
            return 6;
        }
        if (!leggings.getItem().equals(Items.AIR) && getPercentage(leggings) < threshold.getValue()) {
            return 7;
        }
        if (!feet.getItem().equals(Items.AIR) && getPercentage(feet) < threshold.getValue()) {
            return 8;
        }


        return -1;

    }


    public static float getPercentage(ItemStack stack) {
        float durability = stack.getMaxDamage() - stack.getItemDamage();
        return (durability / (float) stack.getMaxDamage()) * 100F;
    }
}
