package dev.zprestige.ruby.module.movement;

import dev.zprestige.ruby.events.MoveEvent;
import dev.zprestige.ruby.module.Category;
import dev.zprestige.ruby.module.Module;
import dev.zprestige.ruby.module.ModuleInfo;
import dev.zprestige.ruby.setting.impl.BooleanSetting;
import dev.zprestige.ruby.setting.impl.FloatSetting;
import dev.zprestige.ruby.setting.impl.ModeSetting;
import dev.zprestige.ruby.util.EntityUtil;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.Arrays;

@ModuleInfo(name = "Step" , category = Category.Movement, description = "steps you up block")
public class Step extends Module {
    public static Step Instance;
    public ModeSetting mode = createSetting("Mode", "Vanilla", Arrays.asList("Vanilla", "Ncp"));
    public FloatSetting stepHeight = createSetting("Height", 2.0f, 0.1f, 4.0f);
    public BooleanSetting autoMarkConveyerOnStep = createSetting("Auto Mark Conveyor On Step", false);

    public Step(){
        Instance = this;
    }

    @Override
    public void onDisable() {
        mc.player.stepHeight = 0.6f;
    }

    @SubscribeEvent
    public void onMove(MoveEvent event) {
        if (nullCheck() || !isEnabled())
            return;
        if (autoMarkConveyerOnStep.getValue() && mc.player.collidedHorizontally)
            TickShift.Instance.enableModule();
        switch (mode.getValue()) {
            case "Vanilla":
                mc.player.stepHeight = stepHeight.getValue();
                break;
            case "Ncp":
                 double[] forward = EntityUtil.getSpeed(0.1);
                boolean stage1 = false;
                boolean stage2 = false;
                boolean stage3 = false;
                boolean stage4 = false;
                if (mc.world.getCollisionBoxes(mc.player, mc.player.getEntityBoundingBox().offset(forward[0], 2.6, forward[1])).isEmpty() && !mc.world.getCollisionBoxes(mc.player, mc.player.getEntityBoundingBox().offset(forward[0], 2.4, forward[1])).isEmpty())
                    stage1 = true;
                if (mc.world.getCollisionBoxes(mc.player, mc.player.getEntityBoundingBox().offset(forward[0], 2.1, forward[1])).isEmpty())
                    if (!mc.world.getCollisionBoxes(mc.player, mc.player.getEntityBoundingBox().offset(forward[0], 1.9, forward[1])).isEmpty())
                        stage2 = true;
                if (mc.world.getCollisionBoxes(mc.player, mc.player.getEntityBoundingBox().offset(forward[0], 1.6, forward[1])).isEmpty() && !mc.world.getCollisionBoxes(mc.player, mc.player.getEntityBoundingBox().offset(forward[0], 1.4, forward[1])).isEmpty())
                    stage3 = true;

                if (mc.world.getCollisionBoxes(mc.player, mc.player.getEntityBoundingBox().offset(forward[0], 1.0, forward[1])).isEmpty() && !mc.world.getCollisionBoxes(mc.player, mc.player.getEntityBoundingBox().offset(forward[0], 0.6, forward[1])).isEmpty())
                    stage4 = true;

                if (mc.player.collidedHorizontally && (mc.player.moveForward != 0.0f || mc.player.moveStrafing != 0.0f)) {
                    if (mc.player.onGround) {
                        if (stage4 && stepHeight.getValue() >= 1.0) {
                             double[] array = {0.42, 0.753};
                            for (int length = array.length, i = 0; i < length; ++i) {
                                mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + array[i], mc.player.posZ, mc.player.onGround));
                            }
                            mc.player.setPosition(mc.player.posX, mc.player.posY + 1.0, mc.player.posZ);
                        }
                        if (stage3 && stepHeight.getValue() >= 1.5) {
                             double[] array2 = {0.42, 0.75, 1.0, 1.16, 1.23, 1.2};
                            for (double v : array2) {
                                mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + v, mc.player.posZ, mc.player.onGround));
                            }
                            mc.player.setPosition(mc.player.posX, mc.player.posY + 1.5, mc.player.posZ);
                        }
                        if (stage2 && stepHeight.getValue() >= 2.0) {
                             double[] array3 = {0.42, 0.78, 0.63, 0.51, 0.9, 1.21, 1.45, 1.43};
                            for (double v : array3) {
                                mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + v, mc.player.posZ, mc.player.onGround));
                            }
                            mc.player.setPosition(mc.player.posX, mc.player.posY + 2.0, mc.player.posZ);
                        }
                        if (stage1 && stepHeight.getValue() >= 2.5) {
                             double[] array4 = {0.425, 0.821, 0.699, 0.599, 1.022, 1.372, 1.652, 1.869, 2.019, 1.907};
                            for (double v : array4) {
                                mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + v, mc.player.posZ, mc.player.onGround));
                            }
                            mc.player.setPosition(mc.player.posX, mc.player.posY + 2.5, mc.player.posZ);
                        }
                    }
                }
                break;
        }
    }
}
