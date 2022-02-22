package dev.zprestige.ruby.module.player;

import com.mojang.realmsclient.gui.ChatFormatting;
import dev.zprestige.ruby.eventbus.annotation.RegisterListener;
import dev.zprestige.ruby.events.PacketEvent;
import dev.zprestige.ruby.module.Category;
import dev.zprestige.ruby.module.Module;
import dev.zprestige.ruby.module.ModuleInfo;
import dev.zprestige.ruby.setting.impl.BooleanSetting;
import dev.zprestige.ruby.setting.impl.IntegerSetting;
import dev.zprestige.ruby.util.BlockUtil;
import dev.zprestige.ruby.util.InventoryUtil;
import dev.zprestige.ruby.util.MessageUtil;
import dev.zprestige.ruby.util.Timer;
import net.minecraft.block.Block;
import net.minecraft.block.BlockShulkerBox;
import net.minecraft.client.gui.inventory.GuiScreenHorseInventory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.EntityDonkey;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.util.EnumHand;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.TreeMap;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@ModuleInfo(name = "AutoEcMeDupe", category = Category.Player, description = "automatically dupes on endcrystal.me")
public class AutoEcMeDupe extends Module {
    public IntegerSetting actionDelay = createSetting("Action Delay (MS)", 100, 1, 1000);
    public IntegerSetting timeoutTime = createSetting("Timeout Time (S)", 3, 1, 15);
    public IntegerSetting restartTimeout = createSetting("Restart Timeout (S)", 2, 1, 15);
    public BooleanSetting afkScreenFix = createSetting("Afk Screen Fix", false);
    public BooleanSetting autoDismount = createSetting("Auto Dismount", false);
    public IntegerSetting dismountRetryDelay = createSetting("Dismount Retry Delay (MS)", 100, 1, 1000, (Predicate<Integer>) v-> autoDismount.getValue());
    public Timer timer = new Timer(), dismountTimer = new Timer();
    public int stage = 0, shulkers = 0;
    public boolean restart, bok, joe;

    @Override
    public void onEnable() {
        timer.setTime(0);
        stage = 0;
        shulkers = 0;
        restart = false;
        bok = false;
    }

    @Override
    public void onTick() {
        EntityDonkey entityDonkey = getClosestDonkey();
        if (entityDonkey == null) {
            disableModule("No donkey found in rage, disabling AutoEcMeDupe.");
            return;
        }
        int slot = InventoryUtil.getItemFromHotbar(Item.getItemFromBlock(Blocks.CHEST));
        if (slot == -1) {
            disableModule("No chest found in hotbar, disabling AutoEcMeDupe.");
            return;
        }
        if(afkScreenFix.getValue() && mc.currentScreen != null && !(mc.currentScreen instanceof GuiScreenHorseInventory))
            mc.currentScreen = null;
        if (joe){
            mc.gameSettings.keyBindSneak.pressed = false;
            joe = false;
        }
        if (autoDismount.getValue() && dismountTimer.getTime(dismountRetryDelay.getValue()) && mc.player.isRiding()) {
            mc.gameSettings.keyBindSneak.pressed = true;
            dismountTimer.setTime(0);
            joe = true;
        }
        switch (stage) {
            case 0:
                if (timer.getTime(restart ? (restartTimeout.getValue() * 1000) : actionDelay.getValue())) {
                    if (restart)
                        restart = false;
                    if (entityDonkey.hasChest()) {
                        stage = 1;
                    } else {
                        InventoryUtil.switchToSlot(slot);
                        MessageUtil.sendMessage("[AutoEcMeDupe]" + ChatFormatting.GRAY + "[Stage][" + ChatFormatting.WHITE + stage + "]" + ChatFormatting.WHITE + " rotating to donkey.");
                        entityRotate(entityDonkey);
                        mc.playerController.interactWithEntity(mc.player, entityDonkey, EnumHand.MAIN_HAND);
                        MessageUtil.sendMessage("[AutoEcMeDupe]" + ChatFormatting.GRAY + "[Stage][" + ChatFormatting.WHITE + stage + "]" + ChatFormatting.WHITE + " chesting donkey.");
                    }
                    timer.setTime(0);
                }
                break;
            case 1:
                if (timer.getTime(actionDelay.getValue())) {
                    mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_SNEAKING));
                    entityRotate(entityDonkey);
                    mc.playerController.interactWithEntity(mc.player, entityDonkey, EnumHand.MAIN_HAND);
                    MessageUtil.sendMessage("[AutoEcMeDupe]" + ChatFormatting.GRAY + "[Stage][" + ChatFormatting.WHITE + stage + "]" + ChatFormatting.WHITE + " opening donkey.");
                    mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
                    stage = 2;
                    timer.setTime(0);
                }
                break;
            case 2:
                if (shulkers >= 15) {
                    stage = 3;
                } else if (timer.getTime(actionDelay.getValue())) {
                    if (mc.currentScreen instanceof GuiScreenHorseInventory) {
                        MessageUtil.sendMessage("[AutoEcMeDupe]" + ChatFormatting.GRAY + "[Stage][" + ChatFormatting.WHITE + stage + "]" + ChatFormatting.WHITE + " transferring items started.");
                        GuiScreenHorseInventory chest = (GuiScreenHorseInventory) mc.currentScreen;
                        for (int i = 0; i < mc.player.inventoryContainer.inventorySlots.size(); ++i) {
                            ItemStack itemStack = mc.player.inventoryContainer.getSlot(i).getStack();
                            if (itemStack.isEmpty() || itemStack.getItem() == Items.AIR || !(Block.getBlockFromItem(itemStack.getItem()) instanceof BlockShulkerBox))
                                continue;
                            mc.playerController.windowClick(chest.inventorySlots.windowId, i, 0, ClickType.QUICK_MOVE, mc.player);
                            MessageUtil.sendMessage("[AutoEcMeDupe]" + ChatFormatting.GRAY + "[Stage][" + ChatFormatting.WHITE + stage + "]" + ChatFormatting.WHITE + " transferring slot " + ChatFormatting.GRAY + i + ChatFormatting.WHITE + ".");
                            shulkers++;
                        }
                        MessageUtil.sendMessage("[AutoEcMeDupe]" + ChatFormatting.GRAY + "[Stage][" + ChatFormatting.WHITE + stage + "]" + ChatFormatting.WHITE + " transferring items finished.");
                    }
                }
                break;
            case 3:
                if (timer.getTime(actionDelay.getValue())) {
                    MessageUtil.sendMessage("[AutoEcMeDupe]" + ChatFormatting.GRAY + "[Stage][" + ChatFormatting.WHITE + stage + "]" + ChatFormatting.WHITE + " closing donkey.");
                    mc.displayGuiScreen(null);
                    stage = 4;
                    timer.setTime(0);
                }
                break;
            case 4:
                if (restart) {
                    MessageUtil.sendMessage("[AutoEcMeDupe]" + ChatFormatting.GRAY + "[Stage][" + ChatFormatting.WHITE + stage + "]" + ChatFormatting.WHITE + " setting use item not pressed.");
                    mc.gameSettings.keyBindUseItem.pressed = false;
                    MessageUtil.sendMessage("[AutoEcMeDupe]" + ChatFormatting.GRAY + "[Stage][" + ChatFormatting.WHITE + stage + "]" + ChatFormatting.WHITE + " finishing up, setting stage to 0.");
                    stage = 0;
                }
                if (timer.getTime(timeoutTime.getValue() * 1000)) {
                    MessageUtil.sendMessage("[AutoEcMeDupe]" + ChatFormatting.GRAY + "[Stage][" + ChatFormatting.WHITE + stage + "]" + ChatFormatting.WHITE + " rotating to donkey.");
                    entityRotate(entityDonkey);
                    MessageUtil.sendMessage("[AutoEcMeDupe]" + ChatFormatting.GRAY + "[Stage][" + ChatFormatting.WHITE + stage + "]" + ChatFormatting.WHITE + " setting vanilla use item pressed.");
                    mc.gameSettings.keyBindUseItem.pressed = true;
                    timer.setTime(0);
                    shulkers = 0;
                    restart = true;
                }
                break;

        }
    }

    @RegisterListener
    public void onPacketSend(PacketEvent.PacketSendEvent event) {
        if (nullCheck() || !isEnabled() || !(event.getPacket() instanceof CPacketPlayerTryUseItemOnBlock))
            return;
        if (restart && stage == 4) {
            event.setCancelled(true);
        }
    }

    public EntityDonkey getClosestDonkey() {
        TreeMap<Float, EntityDonkey> entityDonkeyTreeMap = mc.world.loadedEntityList.stream().filter(entity -> !(mc.player.getDistance(entity) > 5.0f) && entity instanceof EntityDonkey).collect(Collectors.toMap(entity -> mc.player.getDistance(entity), entity -> (EntityDonkey) entity, (a, b) -> b, TreeMap::new));
        if (!entityDonkeyTreeMap.isEmpty())
            return entityDonkeyTreeMap.firstEntry().getValue();
        return null;
    }

    public void entityRotate(Entity entity) {
        float[] angle = BlockUtil.calcAngle(mc.player.getPositionEyes(mc.getRenderPartialTicks()), entity.getPositionVector());
        mc.player.rotationYaw = angle[0];
        mc.player.rotationPitch = angle[1];
    }
}
