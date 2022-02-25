package dev.zprestige.ruby.module.combat;

import dev.zprestige.ruby.eventbus.annotation.RegisterListener;
import dev.zprestige.ruby.events.PlayerChangeEvent;
import dev.zprestige.ruby.module.Module;
import dev.zprestige.ruby.settings.impl.ComboBox;
import dev.zprestige.ruby.settings.impl.Parent;
import dev.zprestige.ruby.settings.impl.Slider;
import dev.zprestige.ruby.settings.impl.Switch;
import dev.zprestige.ruby.util.BlockUtil;
import dev.zprestige.ruby.util.EntityUtil;
import dev.zprestige.ruby.util.InventoryUtil;
import dev.zprestige.ruby.util.Timer;
import net.minecraft.init.Items;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.Item;

public class Offhand extends Module {
    public final ComboBox item = Menu.ComboBox("Item", new String[]{"Crystal", "Totem"});

    public final Parent crystalMisc = Menu.Parent("Crystal Misc");
    public final Switch crystalOnSword = Menu.Switch("Sword Crystal").parent(crystalMisc);
    public final Switch crystalOnPickaxe = Menu.Switch("Pickaxe Crystal").parent(crystalMisc);

    public final Parent health = Menu.Parent("Health");
    public final Slider totemHealth = Menu.Slider("Totem Health", 0f, 20f).parent(health);
    public final Switch hole = Menu.Switch("Hole Check").parent(health);
    public final Slider holeHealth = Menu.Slider("Totem Hole Health", 0f, 20f).parent(health);

    public final Parent timing = Menu.Parent("Timing");
    public final Slider switchDelay = Menu.Slider("Switch Delay", 0, 200).parent(timing);

    public final Parent forcing = Menu.Parent("Forcing");
    public final Switch postPopForceTotem = Menu.Switch("Post Pop Force Totem").parent(forcing);
    public final Slider forceTime = Menu.Slider("Force Time", 0, 3000).parent(forcing);
    public final Switch fallDistance = Menu.Switch("Fall Distance Check").parent(forcing);
    public final Slider minDistance = Menu.Slider("Min Distance", 1f, 100f).parent(forcing);

    public final Parent misc = Menu.Parent("Misc");
    public final Switch fallBack = Menu.Switch("FallBack").parent(misc);
    public final Switch gapple = Menu.Switch("Gapple Switch").parent(misc);
    public final Switch rightClick = Menu.Switch("Right Click Only").parent(misc);
    public final Parent threading = Menu.Parent("Threading");
    public final Switch threadSwap = Menu.Switch("Thread Swap").parent(threading);
    public final Slider threadSwapAmount = Menu.Slider("Thread Swap Amount", 1, 10).parent(threading);
    public final Switch threadFindingItem = Menu.Switch("Thread Finding Item").parent(threading);
    public final Slider threadFindingItemAmount = Menu.Slider("Thread Finding Item Amount", 1, 10).parent(threading);
    public Timer switchTimer = new Timer(), postPopTimer = new Timer();
    public int offhandSlot = -1;
    public Thread itemThread = new Thread(() -> offhandSlot = InventoryUtil.getItemSlot(getOffhandItem()));
    public Thread offhandThread = new Thread(this::execute);

    @Override
    public void onTick() {
        if (mc.currentScreen != null)
            return;
        if (threadFindingItem.GetSwitch()) {
            for (int i = 0; i < threadFindingItemAmount.GetSlider(); i++) {
                Thread thread = new Thread(itemThread);
                thread.start();
            }
        } else {
            offhandSlot = InventoryUtil.getItemSlot(getOffhandItem());
        }
        if (threadSwap.GetSwitch()) {
            for (int i = 0; i < threadSwapAmount.GetSlider(); i++) {
                Thread thread = new Thread(offhandThread);
                thread.start();
            }
        } else {
            execute();
        }
    }

    public void execute() {
        if (mc.player.getHeldItemOffhand().getItem() != getOffhandItem() && offhandSlot != -1 && switchTimer.getTime((long) switchDelay.GetSlider())) {
            int slot = offhandSlot < 9 ? offhandSlot + 36 : offhandSlot;
            mc.playerController.windowClick(0, slot, 0, ClickType.PICKUP, mc.player);
            mc.playerController.windowClick(0, 45, 0, ClickType.PICKUP, mc.player);
            mc.playerController.windowClick(0, slot, 0, ClickType.PICKUP, mc.player);
            mc.playerController.updateController();
            switchTimer.setTime(0);
        }
    }

    public Item getOffhandItem() {
        boolean safeToSwap = safeToSwap();
        if (postPopForceTotem.GetSwitch() && postPopTimer.getTimeSub((long) forceTime.GetSlider()))
            return Items.TOTEM_OF_UNDYING;
        switch (item.GetCombo()) {
            case "Totem":
                if (safeToSwap) {
                    if (gapple.GetSwitch() && ((rightClick.GetSwitch() && mc.player.getHeldItemMainhand().getItem() == Items.DIAMOND_SWORD && mc.gameSettings.keyBindUseItem.isKeyDown()) || (!rightClick.GetSwitch() && mc.player.getHeldItemMainhand().getItem() == Items.DIAMOND_SWORD)))
                        return Items.GOLDEN_APPLE;

                    if (crystalOnSword.GetSwitch() && mc.player.getHeldItemMainhand().getItem() == Items.DIAMOND_SWORD)
                        return Items.END_CRYSTAL;

                    if (crystalOnPickaxe.GetSwitch() && mc.player.getHeldItemMainhand().getItem() == Items.DIAMOND_PICKAXE)
                        return Items.END_CRYSTAL;

                    if (fallBack.GetSwitch() && InventoryUtil.getStackCount(Items.TOTEM_OF_UNDYING) == 0)
                        return Items.END_CRYSTAL;
                }
                return Items.TOTEM_OF_UNDYING;
            case "Crystal":
                if (fallBack.GetSwitch() && InventoryUtil.getStackCount(Items.END_CRYSTAL) == 0)
                    return Items.TOTEM_OF_UNDYING;

                if (fallDistance.GetSwitch() && mc.player.fallDistance > minDistance.GetSlider())
                    return Items.TOTEM_OF_UNDYING;
                if (safeToSwap) {
                    if (gapple.GetSwitch() && ((rightClick.GetSwitch() && mc.player.getHeldItemMainhand().getItem() == Items.DIAMOND_SWORD && mc.gameSettings.keyBindUseItem.isKeyDown()) || (!rightClick.GetSwitch() && mc.player.getHeldItemMainhand().getItem() == Items.DIAMOND_SWORD)))
                        return Items.GOLDEN_APPLE;

                    return Items.END_CRYSTAL;
                }
                return Items.TOTEM_OF_UNDYING;
        }
        return null;
    }

    public boolean safeToSwap() {
        if (hole.GetSwitch() && BlockUtil.isPlayerSafe(mc.player) && mc.player.onGround && EntityUtil.getHealth(mc.player) < holeHealth.GetSlider())
            return false;
        return !(EntityUtil.getHealth(mc.player) < totemHealth.GetSlider());
    }

    @RegisterListener
    public void onTotemPop(PlayerChangeEvent.TotemPop event) {
        if (nullCheck() || !isEnabled() || !postPopForceTotem.GetSwitch() || !event.entityPlayer.equals(mc.player))
            return;
        postPopTimer.setTime(0);
    }
}