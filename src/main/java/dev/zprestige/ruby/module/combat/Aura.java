package dev.zprestige.ruby.module.combat;

import dev.zprestige.ruby.Ruby;
import dev.zprestige.ruby.module.Module;
import dev.zprestige.ruby.settings.impl.ComboBox;
import dev.zprestige.ruby.settings.impl.Parent;
import dev.zprestige.ruby.settings.impl.Slider;
import dev.zprestige.ruby.settings.impl.Switch;
import dev.zprestige.ruby.util.EntityUtil;
import dev.zprestige.ruby.util.InventoryUtil;
import dev.zprestige.ruby.util.Timer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.network.play.client.CPacketUseEntity;

import java.util.TreeMap;

public class Aura extends Module {
    public static Aura Instance;
    public final Parent ranges = Menu.Parent("Ranges");
    public final Slider range = Menu.Slider("Range", 0.1f, 6.0f).parent(ranges);
    public final Slider wallRange = Menu.Slider("Through Wall Range", 0.1f, 6.0f).parent(ranges);
    public final Parent misc = Menu.Parent("Misc");
    public final Switch swordOnly = Menu.Switch("Sword Only").parent(misc);
    public final Switch eggOnly = Menu.Switch("Egg Only").parent(misc);
    public final Switch autoSwitch = Menu.Switch("Auto Switch").parent(misc);
    public final Switch autoDelay = Menu.Switch("Delay").parent(misc);
    public final Slider hitDelay = Menu.Slider("Hit Delay", 1, 1000);
    public final Switch packet = Menu.Switch("Packet").parent(misc);
    public final Switch swing = Menu.Switch("Swing").parent(misc);
    public final ComboBox swingHand = Menu.ComboBox("Swing Hand", new String[]{
            "Mainhand",
            "Offhand",
            "Packet"
    }).parent(misc);
    public Timer timer = new Timer();

    public Aura() {
        Instance = this;
    }

    @Override
    public void onTick() {
        if (timer.getTime(autoDelay.GetSwitch() ? 600 : (long) hitDelay.GetSlider())) {
            EntityPlayer target = getTarget();
            if (target == null)
                return;
            int swordSlot = InventoryUtil.getItemFromHotbar(Items.DIAMOND_SWORD);
            if (autoSwitch.GetSwitch() && !mc.player.getHeldItemMainhand().getItem().equals(Items.DIAMOND_SWORD) && swordSlot != -1)
                InventoryUtil.switchToSlot(swordSlot);

            if (!swordOnly.GetSwitch()) {
                if (eggOnly.GetSwitch()) {
                    if (!mc.player.getHeldItemMainhand().getItem().equals(Items.EGG))
                        return;
                }
            } else {
                if (!mc.player.getHeldItemMainhand().getItem().equals(Items.DIAMOND_SWORD)) {
                    return;
                }
            }

            if (packet.GetSwitch())
                mc.player.connection.sendPacket(new CPacketUseEntity(target));
            else
                mc.playerController.attackEntity(mc.player, target);
            if (swing.GetSwitch())
                EntityUtil.swingArm(swingHand.GetCombo().equals("Mainhand") ? EntityUtil.SwingType.MainHand : swingHand.GetCombo().equals("Offhand") ? EntityUtil.SwingType.OffHand : EntityUtil.SwingType.Packet);
            timer.setTime(0);
        }
    }

    public EntityPlayer getTarget() {
        TreeMap<Float, EntityPlayer> target = new TreeMap<>();
        for (EntityPlayer entityPlayer : mc.world.playerEntities) {
            if (entityPlayer.equals(mc.player))
                continue;
            if (Ruby.friendManager.isFriend(entityPlayer.getName()))
                continue;
            if (mc.player.getDistance(entityPlayer) > range.GetSlider())
                continue;
            if (mc.player.canEntityBeSeen(entityPlayer) && mc.player.getDistance(entityPlayer) > wallRange.GetSlider())
                continue;
            target.put(mc.player.getDistance(entityPlayer), entityPlayer);
        }
        if (!target.isEmpty())
            return target.firstEntry().getValue();
        return null;
    }
}