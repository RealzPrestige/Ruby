package dev.zprestige.ruby.module.misc;

import dev.zprestige.ruby.module.Module;
import dev.zprestige.ruby.setting.impl.BooleanSetting;
import dev.zprestige.ruby.setting.impl.ColorSetting;
import dev.zprestige.ruby.setting.impl.FloatSetting;
import dev.zprestige.ruby.setting.impl.ModeSetting;
import dev.zprestige.ruby.util.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Objects;

public class AutoMine extends Module {
    public ModeSetting mineMode = createSetting("Mine Mode", "Vanilla", Arrays.asList("Vanilla", "Packet", "EcMe"));
    public ModeSetting priority = createSetting("Priority", "City > Surround > AnvilBurrow", Arrays.asList(
            "City > Surround > AnvilBurrow",
            "City > AnvilBurrow > Surround",
            "Surround > City > AnvilBurrow",
            "Surround > AnvilBurrow > City",
            "AnvilBurrow > City > Surround",
            "AnvilBurrow > Surround > City"
    ), v -> !mineMode.getValue().equals("EcMe"));
    public FloatSetting targetRange = createSetting("Target Range", 9.0f, 0.1f, 15.0f);
    public FloatSetting breakRange = createSetting("Break Range", 5.0f, 0.1f, 6.0f);
    public BooleanSetting rotateToPos = createSetting("Rotate To Pos", false, v -> mineMode.getValue().equals("EcMe"));
    public BooleanSetting silentSwitch = createSetting("Silent Switch", false, v -> mineMode.getValue().equals("EcMe"));
    public BooleanSetting preSwitch = createSetting("Pre Switch", false);
    public BooleanSetting renderPacket = createSetting("Render Packet", false, v -> mineMode.getValue().equals("Packet"));
    public ColorSetting packetColor = createSetting("Packet Color", new Color(-1), v -> mineMode.getValue().equals("Packet") && renderPacket.getValue());
    public Timer timer = new Timer();
    public ArrayList<BlockPos> surround = new ArrayList<>();
    public ArrayList<BlockPos> perfectCityPosses = new ArrayList<>();
    public BlockPos burrowPos = null, currentMinePos = null, prevMinePosEcMe = null;
    public float size = 0.0f;

    public void setup(BlockPos pos) {
        if (mc.world.getBlockState(pos.north()).getBlock().equals(Blocks.OBSIDIAN) || mc.world.getBlockState(pos.north()).getBlock().equals(Blocks.ENDER_CHEST))
            addToSurround(pos.north());
        if (mc.world.getBlockState(pos.east()).getBlock().equals(Blocks.OBSIDIAN) || mc.world.getBlockState(pos.east()).getBlock().equals(Blocks.ENDER_CHEST))
            addToSurround(pos.east());
        if (mc.world.getBlockState(pos.south()).getBlock().equals(Blocks.OBSIDIAN) || mc.world.getBlockState(pos.south()).getBlock().equals(Blocks.ENDER_CHEST))
            addToSurround(pos.south());
        if (mc.world.getBlockState(pos.west()).getBlock().equals(Blocks.OBSIDIAN) || mc.world.getBlockState(pos.west()).getBlock().equals(Blocks.ENDER_CHEST))
            addToSurround(pos.west());
        if (mc.world.getBlockState(pos.north()).getBlock().equals(Blocks.OBSIDIAN) && mc.world.getBlockState(pos.north().north()).getBlock().equals(Blocks.AIR) && mc.world.getBlockState(pos.north().north().up()).getBlock().equals(Blocks.AIR) && (mc.world.getBlockState(pos.north().north().down()).getBlock().equals(Blocks.OBSIDIAN) || mc.world.getBlockState(pos.north().north().down()).getBlock().equals(Blocks.BEDROCK)))
            addToCity(pos.north());
        if (mc.world.getBlockState(pos.east()).getBlock().equals(Blocks.OBSIDIAN) && mc.world.getBlockState(pos.east().east()).getBlock().equals(Blocks.AIR) && mc.world.getBlockState(pos.east().east().up()).getBlock().equals(Blocks.AIR) && (mc.world.getBlockState(pos.east().east().down()).getBlock().equals(Blocks.OBSIDIAN) || mc.world.getBlockState(pos.east().east().down()).getBlock().equals(Blocks.BEDROCK)))
            addToCity(pos.east());
        if (mc.world.getBlockState(pos.south()).getBlock().equals(Blocks.OBSIDIAN) && mc.world.getBlockState(pos.south().south()).getBlock().equals(Blocks.AIR) && mc.world.getBlockState(pos.south().south().up()).getBlock().equals(Blocks.AIR) && (mc.world.getBlockState(pos.south().south().down()).getBlock().equals(Blocks.OBSIDIAN) || mc.world.getBlockState(pos.south().south().down()).getBlock().equals(Blocks.BEDROCK)))
            addToCity(pos.south());
        if (mc.world.getBlockState(pos.west()).getBlock().equals(Blocks.OBSIDIAN) && mc.world.getBlockState(pos.west().west()).getBlock().equals(Blocks.AIR) && mc.world.getBlockState(pos.west().west().up()).getBlock().equals(Blocks.AIR) && (mc.world.getBlockState(pos.west().west().down()).getBlock().equals(Blocks.OBSIDIAN) || mc.world.getBlockState(pos.west().west().down()).getBlock().equals(Blocks.BEDROCK)))
            addToCity(pos.west());

        if (!mc.world.getBlockState(pos).getBlock().equals(Blocks.AIR))
            burrowPos = pos;
    }

    public void addToSurround(BlockPos blockPos) {
        surround.addAll(Collections.singletonList(blockPos));
    }

    public void addToCity(BlockPos blockPos) {
        perfectCityPosses.addAll(Collections.singletonList(blockPos));
    }

    @Override
    public void onGlobalRenderTick() {
        if (currentMinePos != null && !mc.world.getBlockState(currentMinePos).getBlock().equals(Blocks.AIR) && renderPacket.getValue() && mineMode.getValue().equals("Packet")) {
            RenderUtil.drawFullBox(true, true, packetColor.getValue(), packetColor.getValue(), 1f, currentMinePos);
        }
    }

    public void posRotate(BlockPos pos) {
        float[] angle = BlockUtil.calcAngle(mc.player.getPositionEyes(mc.getRenderPartialTicks()), new Vec3d((float) pos.getX() + 0.5f, (float) pos.getY() - 0.5f, (float) pos.getZ() + 0.5f));
        mc.player.rotationYaw = angle[0];
        mc.player.rotationPitch = angle[1];
    }

    @Override
    public void onTick() {
        EntityPlayer entityPlayer = EntityUtil.getTarget(targetRange.getValue());
        int pickSlot = InventoryUtil.getItemFromHotbar(Items.DIAMOND_PICKAXE);
        if (entityPlayer == null || pickSlot == -1)
            return;
        BlockPos targetPos = EntityUtil.getPlayerPos(entityPlayer);
        surround.clear();
        perfectCityPosses.clear();
        burrowPos = null;
        setup(targetPos);
        if (mc.player.getDistanceSq(targetPos.north()) > (breakRange.getValue() * breakRange.getValue()) || mc.player.getDistanceSq(targetPos.east()) > (breakRange.getValue() * breakRange.getValue()) || mc.player.getDistanceSq(targetPos.south()) > (breakRange.getValue() * breakRange.getValue()) || mc.player.getDistanceSq(targetPos.west()) > (breakRange.getValue() * breakRange.getValue()) || mc.player.getDistanceSq(targetPos) > (breakRange.getValue() * breakRange.getValue()))
            return;
        if (mineMode.getValue().equals("EcMe")) {
            if (!surround.isEmpty()) {
                BlockPos pos = surround.get(0);
                if (pos != prevMinePosEcMe)
                    size = 0.0f;
                float[] rotations = new float[]{mc.player.rotationYaw, mc.player.rotationPitch};
                if (rotateToPos.getValue())
                    posRotate(pos);
                int slot = InventoryUtil.getItemFromHotbar(Items.DIAMOND_PICKAXE);
                int currentItem = mc.player.inventory.currentItem;
                if (silentSwitch.getValue() && slot != -1)
                    InventoryUtil.switchToSlot(slot);
                if (mc.player.getHeldItemMainhand().getItem().equals(Items.DIAMOND_PICKAXE) || silentSwitch.getValue()) {
                    mc.playerController.onPlayerDamageBlock(pos, mc.objectMouseOver.sideHit);
                    mc.player.swingArm(EnumHand.MAIN_HAND);
                }
                size += 0.25f;
                if (silentSwitch.getValue() && slot != -1) {
                    mc.player.inventory.currentItem = currentItem;
                    mc.playerController.updateController();
                }
                if (rotateToPos.getValue()) {
                    mc.player.rotationYaw = rotations[0];
                    mc.player.rotationPitch = rotations[1];
                }
                prevMinePosEcMe = pos;
            }
            return;
        }
        if (currentMinePos != null) {
            if (timer.getTime(2000)) {
                int slot = InventoryUtil.getItemFromHotbar(Items.DIAMOND_PICKAXE);
                if (!mc.player.getHeldItemMainhand().getItem().equals(Items.DIAMOND_PICKAXE) && slot != -1 && currentMinePos != null) {
                    int currentItem = mc.player.inventory.currentItem;
                    InventoryUtil.switchToSlot(slot);
                    Objects.requireNonNull(mc.getConnection()).sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, currentMinePos, EnumFacing.UP));
                    mc.player.inventory.currentItem = currentItem;
                    mc.playerController.updateController();
                }
                currentMinePos = null;
            }
            return;
        }
        switch (priority.getValue()) {
            case "City > Surround > AnvilBurrow":
                if (!perfectCityPosses.isEmpty()) {
                    BlockPos pos = perfectCityPosses.get(0);
                    mineBlock(pos);
                } else if (!surround.isEmpty()) {
                    BlockPos pos = surround.get(0);
                    mineBlock(pos);
                } else if (burrowPos != null)
                    mineBlock(burrowPos);
                break;
            case "City > AnvilBurrow > Surround":
                if (!perfectCityPosses.isEmpty()) {
                    BlockPos pos = perfectCityPosses.get(0);
                    mineBlock(pos);
                } else if (burrowPos != null)
                    mineBlock(burrowPos);
                else if (!surround.isEmpty()) {
                    BlockPos pos = surround.get(0);
                    mineBlock(pos);
                }
                break;
            case "Surround > City > AnvilBurrow":
                if (!surround.isEmpty()) {
                    BlockPos pos = surround.get(0);
                    mineBlock(pos);
                } else if (!perfectCityPosses.isEmpty()) {
                    BlockPos pos = perfectCityPosses.get(0);
                    mineBlock(pos);
                } else if (burrowPos != null)
                    mineBlock(burrowPos);
                break;
            case "Surround > AnvilBurrow > City":
                if (!surround.isEmpty()) {
                    BlockPos pos = surround.get(0);
                    mineBlock(pos);
                } else if (burrowPos != null)
                    mineBlock(burrowPos);
                else if (!perfectCityPosses.isEmpty()) {
                    BlockPos pos = perfectCityPosses.get(0);
                    mineBlock(pos);
                }
                break;
            case "AnvilBurrow > City > Surround":
                if (burrowPos != null)
                    mineBlock(burrowPos);
                else if (!perfectCityPosses.isEmpty()) {
                    BlockPos pos = perfectCityPosses.get(0);
                    mineBlock(pos);
                } else if (!surround.isEmpty()) {
                    BlockPos pos = surround.get(0);
                    mineBlock(pos);
                }
                break;
            case "AnvilBurrow > Surround > City":
                if (burrowPos != null)
                    mineBlock(burrowPos);
                else if (!surround.isEmpty()) {
                    BlockPos pos = surround.get(0);
                    mineBlock(pos);
                } else if (!perfectCityPosses.isEmpty()) {
                    BlockPos pos = perfectCityPosses.get(0);

                    mineBlock(pos);
                }
                break;
        }
    }

    public void mineBlock(BlockPos pos) {
        int currentItem = mc.player.inventory.currentItem;
        if (preSwitch.getValue()) {
            int slot = InventoryUtil.getItemFromHotbar(Items.DIAMOND_PICKAXE);
            InventoryUtil.switchToSlot(slot);
        }
        switch (mineMode.getValue()) {
            case "Packet":
                mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.START_DESTROY_BLOCK, pos, EnumFacing.UP));
                EntityUtil.swingArm(EntityUtil.SwingType.MainHand);
                mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, pos, EnumFacing.UP));
                timer.setTime(0);
                currentMinePos = pos;
                break;
            case "Vanilla":
                mc.playerController.onPlayerDamageBlock(pos, mc.player.getHorizontalFacing());
                EntityUtil.swingArm(EntityUtil.SwingType.MainHand);
                timer.setTime(0);
                currentMinePos = pos;
                break;
        }
        if (preSwitch.getValue()) {
            mc.player.inventory.currentItem = currentItem;
            mc.playerController.updateController();
        }
    }
}
