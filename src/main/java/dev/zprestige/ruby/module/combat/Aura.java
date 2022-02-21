package dev.zprestige.ruby.module.combat;

import dev.zprestige.ruby.Ruby;
import dev.zprestige.ruby.module.Category;
import dev.zprestige.ruby.module.Module;
import dev.zprestige.ruby.module.ModuleInfo;
import dev.zprestige.ruby.setting.impl.*;
import dev.zprestige.ruby.util.EntityUtil;
import dev.zprestige.ruby.util.InventoryUtil;
import dev.zprestige.ruby.util.Timer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.network.play.client.CPacketUseEntity;

import java.util.Arrays;
import java.util.TreeMap;
import java.util.function.Predicate;

@ModuleInfo(name = "Aura" , category = Category.Combat, description = "kill people with big stick")
public class Aura extends Module {
    public static Aura Instance;
    public ParentSetting ranges = createSetting("Ranges");
    public FloatSetting range = createSetting("Range", 5.0f, 0.1f, 6.0f).setParent(ranges);
    public FloatSetting wallRange = createSetting("Through Wall Range", 5.0f, 0.1f, 6.0f).setParent(ranges);
    public ParentSetting misc = createSetting("Misc");
    public BooleanSetting swordOnly = createSetting("Sword Only", true).setParent(misc);
    public BooleanSetting eggOnly = createSetting("Egg Only", true, v-> !swordOnly.getValue()).setParent(misc);
    public BooleanSetting autoSwitch = createSetting("Auto Switch", true).setParent(misc);
    public BooleanSetting autoDelay = createSetting("Delay", true).setParent(misc);
    public IntegerSetting hitDelay = createSetting("Hit Delay", 600, 1, 1000, (Predicate<Integer>) v-> !autoDelay.getValue());
    public BooleanSetting packet = createSetting("Packet", false).setParent(misc);
    public BooleanSetting swing = createSetting("Swing", false).setParent(misc);
    public ModeSetting swingHand = createSetting("Swing Hand", "Mainhand", Arrays.asList("Mainhand", "Offhand", "Packet"), v -> swing.getValue()).setParent(misc);
    public Timer timer = new Timer();

    public Aura(){
        Instance = this;
    }

    @Override
    public void onTick() {
        if (timer.getTime(autoDelay.getValue() ? 600 : hitDelay.getValue())) {
            EntityPlayer target = getTarget();
            if (target == null)
                return;
            int swordSlot = InventoryUtil.getItemFromHotbar(Items.DIAMOND_SWORD);
            if (autoSwitch.getValue() && !mc.player.getHeldItemMainhand().getItem().equals(Items.DIAMOND_SWORD) && swordSlot != -1)
                InventoryUtil.switchToSlot(swordSlot);

            if (!swordOnly.getValue()) {
                if (eggOnly.getValue() ){
                    if (!mc.player.getHeldItemMainhand().getItem().equals(Items.EGG))
                        return;
                }
            } else {
                if (!mc.player.getHeldItemMainhand().getItem().equals(Items.DIAMOND_SWORD)) {
                    return;
                }
            }

            if (packet.getValue())
                mc.player.connection.sendPacket(new CPacketUseEntity(target));
            else
                mc.playerController.attackEntity(mc.player, target);
            if (swing.getValue())
                EntityUtil.swingArm(swingHand.getValue().equals("Mainhand") ? EntityUtil.SwingType.MainHand : swingHand.getValue().equals("Offhand") ? EntityUtil.SwingType.OffHand : EntityUtil.SwingType.Packet);
            timer.setTime(0);
        }
    }

    public EntityPlayer getTarget() {
        TreeMap<Float, EntityPlayer> target = new TreeMap<>();
        for (EntityPlayer entityPlayer : mc.world.playerEntities) {
            if (entityPlayer.equals(mc.player))
                continue;
            if (Ruby.friendInitializer.isFriend(entityPlayer.getName()))
                continue;
            if (mc.player.getDistance(entityPlayer) > range.getValue())
                continue;
            if (mc.player.canEntityBeSeen(entityPlayer) && mc.player.getDistance(entityPlayer) > wallRange.getValue())
                continue;
            target.put(mc.player.getDistance(entityPlayer), entityPlayer);
        }
        if (!target.isEmpty())
            return target.firstEntry().getValue();
        return null;
    }
}