package dev.zprestige.ruby.module.movement;

import dev.zprestige.ruby.eventbus.annotation.RegisterListener;
import dev.zprestige.ruby.events.MoveEvent;
import dev.zprestige.ruby.events.PacketEvent;
import dev.zprestige.ruby.module.Module;
import dev.zprestige.ruby.newsettings.impl.Slider;
import dev.zprestige.ruby.newsettings.impl.Switch;
import dev.zprestige.ruby.util.EntityUtil;
import dev.zprestige.ruby.util.InventoryUtil;
import dev.zprestige.ruby.util.Timer;
import net.minecraft.init.Items;
import net.minecraft.network.play.client.CPacketPlayerTryUseItem;
import net.minecraft.network.play.server.SPacketPlayerPosLook;
import net.minecraft.util.EnumHand;

public class ElytraFlight extends Module {
    public final Slider speed = Menu.Slider("Speed", 0.1f, 10.0f);
    public final Slider verticalSpeed = Menu.Slider("Vertical Speed", 0.1f, 10.0f);
    public final Switch rocketOnRubberband = Menu.Switch("Rocket On Rubberband");
    public final Switch chinaSettingFor06d = Menu.Switch("China Setting For 06d");
    public final Slider pitchToChina = Menu.Slider("Pitch To China", 0.1f, 90.0f);
    public final Slider lengthOfPitch = Menu.Slider("Length Of Pitch", 1, 1000);
    public boolean needsCorrection, needsCorrection2;
    public Timer pitchLength = new Timer();

    @RegisterListener
    public void onMove(MoveEvent event) {
        if (nullCheck() || !isEnabled() || !mc.player.isElytraFlying() || mc.player.movementInput.jump)
            return;
        if (needsCorrection2 && pitchLength.getTime(lengthOfPitch.GetSlider())) {
            needsCorrection = false;
            needsCorrection2 = false;
        }
        if (needsCorrection) {
            mc.player.rotationPitch = -pitchToChina.GetSlider();
            needsCorrection2 = true;
            return;
        }

        if (mc.player.movementInput.sneak) {
            mc.player.motionY = -verticalSpeed.GetSlider();
        } else {
            event.motionY = -0.001;
            mc.player.motionY = -0.001;
        }
        EntityUtil.setMoveSpeed(event, speed.GetSlider());
    }

    @RegisterListener
    public void onPacketReceive(PacketEvent.PacketReceiveEvent event) {
        if (nullCheck() || !isEnabled() || !rocketOnRubberband.GetSwitch() || !(event.getPacket() instanceof SPacketPlayerPosLook))
            return;
        int slot = InventoryUtil.getItemFromHotbar(Items.FIREWORKS);
        if (slot == -1)
            return;
        int currentItem = mc.player.inventory.currentItem;
        InventoryUtil.switchToSlot(slot);
        mc.player.connection.sendPacket(new CPacketPlayerTryUseItem(EnumHand.MAIN_HAND));
        mc.player.inventory.currentItem = currentItem;
        mc.playerController.updateController();
        if (chinaSettingFor06d.GetSwitch())
            needsCorrection = true;
        pitchLength.setTime(0);
    }
}