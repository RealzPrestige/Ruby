package dev.zprestige.ruby.module.player;

import dev.zprestige.ruby.module.Category;
import dev.zprestige.ruby.module.Module;
import dev.zprestige.ruby.module.ModuleInfo;
import dev.zprestige.ruby.setting.impl.BooleanSetting;
import dev.zprestige.ruby.setting.impl.StringSetting;
import dev.zprestige.ruby.util.BlockUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumHand;

@ModuleInfo(name = "FakeHacker", category = Category.Player, description = "makes people fake haxxors")
public class FakeHacker extends Module {
    public StringSetting target = createSetting("Target", "John");
    public BooleanSetting rotate = createSetting("Rotate", false);
    public BooleanSetting swing = createSetting("Swing", false);
    public EntityPlayer targetPlayer = null;

    @Override
    public void onEnable() {
        targetPlayer = null;
    }

    @Override
    public void onTick() {
        if (target.getValue().equals("zPrestige_")) {
            disableModule("Fuck u piece of shit nice try bitch");
            return;
        }
        EntityPlayer targetPlayer = null;
        for (EntityPlayer entityPlayer : mc.world.playerEntities) {
            if (entityPlayer.getName().equals(target.getValue())) {
                targetPlayer = entityPlayer;
            }
        }
        if (targetPlayer != null) {
            if (mc.player.getDistance(targetPlayer) < mc.playerController.getBlockReachDistance()) {
                if (rotate.getValue()) {
                    float[] angle = BlockUtil.calcAngle(targetPlayer.getPositionEyes(mc.getRenderPartialTicks()), mc.player.getPositionVector());
                    targetPlayer.rotationYaw = angle[0];
                    targetPlayer.rotationYawHead = angle[0];
                    targetPlayer.rotationPitch = angle[1];
                }
                if (swing.getValue()) {
                    targetPlayer.swingArm(EnumHand.MAIN_HAND);
                }
            }
        }
    }
}
