package dev.zprestige.ruby.module.combat;

import dev.zprestige.ruby.events.PlayerChangeEvent;
import dev.zprestige.ruby.module.Category;
import dev.zprestige.ruby.module.Module;
import dev.zprestige.ruby.module.ModuleInfo;
import dev.zprestige.ruby.setting.impl.*;
import dev.zprestige.ruby.util.BlockUtil;
import dev.zprestige.ruby.util.EntityUtil;
import dev.zprestige.ruby.util.InventoryUtil;
import dev.zprestige.ruby.util.Timer;
import net.minecraft.init.Items;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.Item;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.Arrays;
import java.util.function.Predicate;

@ModuleInfo(name = "Offhand", category = Category.Combat, description = "Offhand Items")
public class Offhand extends Module {
    public ModeSetting item = createSetting("Item", "Crystal", Arrays.asList("Crystal", "Totem"));

    public ParentSetting crystalMisc = createSetting("Crystal Misc");
    public BooleanSetting crystalOnSword = createSetting("Sword Crystal", false, v -> item.getValue().equals("Crystal")).setParent(crystalMisc);
    public BooleanSetting crystalOnPickaxe = createSetting("Pickaxe Crystal", false, v -> item.getValue().equals("Crystal")).setParent(crystalMisc);

    public ParentSetting health = createSetting("Health");
    public FloatSetting totemHealth = createSetting("Totem Health", 10f, 0f, 20f).setParent(health);
    public BooleanSetting hole = createSetting("Hole Check", false).setParent(health);
    public FloatSetting holeHealth = createSetting("Totem Hole Health", 10f, 0f, 20f, (Predicate<Float>) v -> hole.getValue()).setParent(health);

    public ParentSetting timing = createSetting("Timing");
    public IntegerSetting switchDelay = createSetting("Switch Delay", 50, 0, 200).setParent(timing);

    public ParentSetting forcing = createSetting("Forcing");
    public BooleanSetting postPopForceTotem = createSetting("Post Pop Force Totem", false).setParent(forcing);
    public IntegerSetting forceTime = createSetting("Force Time", 1000, 0, 3000, (Predicate<Integer>) v -> postPopForceTotem.getValue()).setParent(forcing);
    public BooleanSetting fallDistance = createSetting("Fall Distance Check", false, v -> item.getValue().equals("Crystal")).setParent(forcing);
    public FloatSetting minDistance = createSetting("Min Distance", 10f, 1f, 100f, (Predicate<Float>) v -> fallDistance.getValue() && item.getValue().equals("Crystal")).setParent(forcing);

    public ParentSetting misc = createSetting("Misc");
    public BooleanSetting fallBack = createSetting("FallBack", false).setParent(misc);
    public BooleanSetting gapple = createSetting("Gapple Switch", false).setParent(misc);
    public BooleanSetting rightClick = createSetting("Right Click Only", false, v -> gapple.getValue()).setParent(misc);

    public ParentSetting threading = createSetting("Threading");
    public BooleanSetting threadSwap = createSetting("Thread Swap", false).setParent(threading);
    public IntegerSetting threadSwapAmount = createSetting("Thread Swap Amount", 1, 1, 10, (Predicate<Integer>) v -> threadSwap.getValue()).setParent(threading);
    public BooleanSetting threadFindingItem = createSetting("Thread Finding Item", false).setParent(threading);
    public IntegerSetting threadFindingItemAmount = createSetting("Thread Finding Item Amount", 1, 1, 10, (Predicate<Integer>) v -> threadFindingItem.getValue()).setParent(threading);

    public Timer switchTimer = new Timer(), postPopTimer = new Timer();
    public int offhandSlot = -1;
    public Thread itemThread = new Thread(() -> offhandSlot = InventoryUtil.getItemSlot(getOffhandItem()));
    public Thread offhandThread = new Thread(this::execute);

    @Override
    public void onTick() {
        if (mc.currentScreen != null)
            return;
        if (threadFindingItem.getValue()) {
            for (int i = 0; i < threadFindingItemAmount.getValue(); i++) {
                Thread thread = new Thread(itemThread);
                thread.start();
            }
        } else {
            offhandSlot = InventoryUtil.getItemSlot(getOffhandItem());
        }
        if (threadSwap.getValue()) {
            for (int i = 0; i < threadSwapAmount.getValue(); i++) {
                Thread thread = new Thread(offhandThread);
                thread.start();
            }
        } else {
            execute();
        }
    }

    public void execute() {
        if (mc.player.getHeldItemOffhand().getItem() != getOffhandItem() && offhandSlot != -1 && switchTimer.getTime(switchDelay.getValue())) {
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
        if (postPopForceTotem.getValue() && postPopTimer.getTimeSub(forceTime.getValue()))
            return Items.TOTEM_OF_UNDYING;
        switch (item.getValue()) {
            case "Totem":
                if (safeToSwap) {
                    if (gapple.getValue() && ((rightClick.getValue() && mc.player.getHeldItemMainhand().getItem() == Items.DIAMOND_SWORD && mc.gameSettings.keyBindUseItem.isKeyDown()) || (!rightClick.getValue() && mc.player.getHeldItemMainhand().getItem() == Items.DIAMOND_SWORD)))
                        return Items.GOLDEN_APPLE;

                    if (crystalOnSword.getValue() && mc.player.getHeldItemMainhand().getItem() == Items.DIAMOND_SWORD)
                        return Items.END_CRYSTAL;

                    if (crystalOnPickaxe.getValue() && mc.player.getHeldItemMainhand().getItem() == Items.DIAMOND_PICKAXE)
                        return Items.END_CRYSTAL;

                    if (fallBack.getValue() && InventoryUtil.getStackCount(Items.TOTEM_OF_UNDYING) == 0)
                        return Items.END_CRYSTAL;
                }
                return Items.TOTEM_OF_UNDYING;
            case "Crystal":
                if (fallBack.getValue() && InventoryUtil.getStackCount(Items.END_CRYSTAL) == 0)
                    return Items.TOTEM_OF_UNDYING;

                if (fallDistance.getValue() && mc.player.fallDistance > minDistance.getValue())
                    return Items.TOTEM_OF_UNDYING;
                if (safeToSwap) {
                    if (gapple.getValue() && ((rightClick.getValue() && mc.player.getHeldItemMainhand().getItem() == Items.DIAMOND_SWORD && mc.gameSettings.keyBindUseItem.isKeyDown()) || (!rightClick.getValue() && mc.player.getHeldItemMainhand().getItem() == Items.DIAMOND_SWORD)))
                        return Items.GOLDEN_APPLE;

                    return Items.END_CRYSTAL;
                }
                return Items.TOTEM_OF_UNDYING;
        }
        return null;
    }

    public boolean safeToSwap() {
        if (hole.getValue() && BlockUtil.isPlayerSafe(mc.player) && mc.player.onGround && EntityUtil.getHealth(mc.player) < holeHealth.getValue())
            return false;
        return !(EntityUtil.getHealth(mc.player) < totemHealth.getValue());
    }

    @SubscribeEvent
    public void onTotemPop(PlayerChangeEvent.TotemPop event) {
        if (nullCheck() || !isEnabled() || !postPopForceTotem.getValue() || !event.entityPlayer.equals(mc.player))
            return;
        postPopTimer.setTime(0);
    }
}