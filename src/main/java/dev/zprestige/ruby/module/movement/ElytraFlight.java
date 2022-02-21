package dev.zprestige.ruby.module.movement;

import dev.zprestige.ruby.events.MoveEvent;
import dev.zprestige.ruby.events.PacketEvent;
import dev.zprestige.ruby.module.Category;
import dev.zprestige.ruby.module.Module;
import dev.zprestige.ruby.module.ModuleInfo;
import dev.zprestige.ruby.setting.impl.BooleanSetting;
import dev.zprestige.ruby.setting.impl.FloatSetting;
import dev.zprestige.ruby.setting.impl.IntegerSetting;
import dev.zprestige.ruby.util.EntityUtil;
import dev.zprestige.ruby.util.InventoryUtil;
import dev.zprestige.ruby.util.Timer;
import net.minecraft.init.Items;
import net.minecraft.network.play.client.CPacketPlayerTryUseItem;
import net.minecraft.network.play.server.SPacketPlayerPosLook;
import net.minecraft.util.EnumHand;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.function.Predicate;

@ModuleInfo(name = "ElytraFlight", category = Category.Movement, description = "makes flying with easy")
public class ElytraFlight extends Module {
    public FloatSetting speed = createSetting("Speed", 1.0f, 0.1f, 10.0f);
    public FloatSetting verticalSpeed = createSetting("Vertical Speed", 1.0f, 0.1f, 10.0f);
    public BooleanSetting rocketOnRubberband = createSetting("Rocket On Rubberband", false);
    public BooleanSetting chinaSettingFor06d = createSetting("China Setting For 06d", false, v -> rocketOnRubberband.getValue());
    public FloatSetting pitchToChina = createSetting("Pitch To China", 10.0f, 0.1f, 90.0f, (Predicate<Float>) v -> rocketOnRubberband.getValue() && chinaSettingFor06d.getValue());
    public IntegerSetting lengthOfPitch = createSetting("Length Of Pitch", 100, 1, 1000, (Predicate<Integer>) v -> rocketOnRubberband.getValue() && chinaSettingFor06d.getValue());
    public boolean needsCorrection, needsCorrection2;
    public Timer pitchLength = new Timer();

    @SubscribeEvent
    public void onMove(MoveEvent event) {
        if (nullCheck() || !isEnabled() || !mc.player.isElytraFlying() || mc.player.movementInput.jump)
            return;
        if (needsCorrection2 && pitchLength.getTime(lengthOfPitch.getValue())) {
            needsCorrection = false;
            needsCorrection2 = false;
        }
        if (needsCorrection) {
            mc.player.rotationPitch = -pitchToChina.getValue();
            needsCorrection2 = true;
            return;
        }

        if (mc.player.movementInput.sneak) {
            mc.player.motionY = -verticalSpeed.getValue();
        } else {
            event.motionY = -0.001;
            mc.player.motionY = -0.001;
        }
        EntityUtil.setMoveSpeed(event, speed.getValue());
    }

    @SubscribeEvent
    public void onPacketReceive(PacketEvent.PacketReceiveEvent event) {
        if (nullCheck() || !isEnabled() || !rocketOnRubberband.getValue() || !(event.getPacket() instanceof SPacketPlayerPosLook))
            return;
        int slot = InventoryUtil.getItemFromHotbar(Items.FIREWORKS);
        if (slot == -1)
            return;
        int currentItem = mc.player.inventory.currentItem;
        InventoryUtil.switchToSlot(slot);
        mc.player.connection.sendPacket(new CPacketPlayerTryUseItem(EnumHand.MAIN_HAND));
        mc.player.inventory.currentItem = currentItem;
        mc.playerController.updateController();
        if (chinaSettingFor06d.getValue())
            needsCorrection = true;
        pitchLength.setTime(0);
    }
}