package dev.zprestige.ruby.module.player;

import dev.zprestige.ruby.module.Module;
import dev.zprestige.ruby.settings.impl.Switch;
import dev.zprestige.ruby.util.BlockUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumHand;

public class FakeHacker extends Module {
    public static String target = "";
    public final Switch rotate = Menu.Switch("Rotate");
    public final Switch swing = Menu.Switch("Swing");
    public EntityPlayer targetPlayer = null;

    @Override
    public void onEnable() {
        targetPlayer = null;
    }

    @Override
    public void onTick() {
        if (target.equals("zPrestige_")) {
            disableModule("Fuck u piece of shit nice try bitch");
            return;
        }
        EntityPlayer targetPlayer = null;
        for (EntityPlayer entityPlayer : mc.world.playerEntities) {
            if (entityPlayer.getName().equals(target)) {
                targetPlayer = entityPlayer;
            }
        }
        if (targetPlayer != null) {
            if (mc.player.getDistance(targetPlayer) < mc.playerController.getBlockReachDistance()) {
                if (rotate.GetSwitch()) {
                    final float[] angle = BlockUtil.calcAngle(targetPlayer.getPositionEyes(mc.getRenderPartialTicks()), mc.player.getPositionVector());
                    targetPlayer.rotationYaw = angle[0];
                    targetPlayer.rotationYawHead = angle[0];
                    targetPlayer.rotationPitch = angle[1];
                }
                if (swing.GetSwitch()) {
                    targetPlayer.swingArm(EnumHand.MAIN_HAND);
                }
            }
        }
    }
}
