package dev.zprestige.ruby.module.combat;

import dev.zprestige.ruby.module.Module;
import dev.zprestige.ruby.setting.impl.*;
import dev.zprestige.ruby.util.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import java.util.Arrays;
import java.util.Objects;

public class AutoEcMeMainFucker extends Module {
    public ParentSetting timing = createSetting("Timing");
    public IntegerSetting actionDelay = createSetting("Action Delay", 50, 0, 1000).setParent(timing);
    public IntegerSetting extraRedstoneDelay = createSetting("Extra Redstone Delay", 10, 0, 1000).setParent(timing);
    public IntegerSetting postCompleteNewPosDelay = createSetting("Post Complete New Pos Delay", 500, 0, 1000).setParent(timing);

    public ParentSetting ranges = createSetting("Ranges");
    public FloatSetting targetRange = createSetting("Target Range", 9.0f, 0.1f, 15.0f).setParent(ranges);
    public FloatSetting placeRange = createSetting("Place Range", 5.0f, 0.1f, 6.0f).setParent(ranges);

    public ParentSetting misc = createSetting("Misc");
    public BooleanSetting autoMine = createSetting("Auto Mine Redstone", false).setParent(misc);
    public BooleanSetting pickSwitch = createSetting("Pick Switch", false, v-> autoMine.getValue()).setParent(misc);
    public BooleanSetting attemptSelfDestruct = createSetting("Attempt Self Destruct", false).setParent(misc);
    public BooleanSetting packet = createSetting("Packet", false).setParent(misc);
    public BooleanSetting rotate = createSetting("Rotate", false).setParent(misc);
    public BooleanSetting crystalRotate = createSetting("Crystal Rotate", false).setParent(misc);
    public BooleanSetting placeSwing = createSetting("Place Swing", false).setParent(misc);
    public ModeSetting placeSwingHand = createSetting("Place Swing Hand", "Mainhand", Arrays.asList("Mainhand", "Offhand", "Packet"), v -> placeSwing.getValue()).setParent(misc);
    public Timer timer = new Timer(), post = new Timer();
    public int boob = -1;

    @Override
    public void onEnable() {
        boob = -1;
    }

    @Override
    public void onTick() {
        EntityPlayer target = EntityUtil.getTarget(targetRange.getValue());
        if (target == null) {
            disableModule("No target found, disabling AutoEcMeMainFucker.");
            return;
        }
        if (!isFaggot(target))
            return;
        BlockPos pos = EntityUtil.getPlayerPos(target).up();
        if (boob == -1 && post.getTime(postCompleteNewPosDelay.getValue())) {
            boob = getPossiblePosition(target);
        }
        if (timer.getTime(actionDelay.getValue() + (isOperatingRedstone(pos, boob) ? extraRedstoneDelay.getValue() : 0))) {
            switch (boob) {
                case 1:
                    execute(pos.north().north(), pos.north().north().up(), 1, pos.north(), pos.north().north().north().up(), pos.up(), pos.up().south());
                    timer.setTime(0);
                    break;
                case 2:
                    execute(pos.east().east(), pos.east().east().up(), 2, pos.east(), pos.east().east().east().up(), pos.up(), pos.up().west());
                    timer.setTime(0);
                    break;
                case 3:
                    execute(pos.south().south(), pos.south().south().up(), 3, pos.south(), pos.south().south().south().up(), pos.up(), pos.up().north());
                    timer.setTime(0);
                    break;
                case 4:
                    execute(pos.west().west(), pos.west().west().up(), 4, pos.west(), pos.west().west().west().up(), pos.up(), pos.up().east());
                    timer.setTime(0);
                    break;
            }
        }
    }

    public boolean isOperatingRedstone(BlockPos pos, int i) {
        switch (i) {
            case 1:
                return !mc.world.getEntitiesWithinAABBExcludingEntity(null, new AxisAlignedBB(pos.north().north().up()).shrink(0.5)).isEmpty();
            case 2:
                return !mc.world.getEntitiesWithinAABBExcludingEntity(null, new AxisAlignedBB(pos.east().east().up()).shrink(0.5)).isEmpty();
            case 3:
                return !mc.world.getEntitiesWithinAABBExcludingEntity(null, new AxisAlignedBB(pos.south().south().up()).shrink(0.5)).isEmpty();
            case 4:
                return !mc.world.getEntitiesWithinAABBExcludingEntity(null, new AxisAlignedBB(pos.west().west().up()).shrink(0.5)).isEmpty();
        }
        return false;
    }

    public void execute(BlockPos obsidian, BlockPos piston, int i, BlockPos crystal, BlockPos redstoneBlock, BlockPos top, BlockPos opposite) {
        if (!isntAir(obsidian)) {
            int obsidianSlot = InventoryUtil.getItemFromHotbar(Item.getItemFromBlock(Blocks.OBSIDIAN));
            if (obsidianSlot == -1) {
                disableModule("No obsidian found, disabling AutoEcMeMainFucker.");
                return;
            }
            BlockUtil.placeBlockWithSwitch(obsidian, EnumHand.MAIN_HAND, rotate.getValue(), packet.getValue(), obsidianSlot);
            return;
        }
        if (!isntAir(piston)) {
            int pistonSlot = InventoryUtil.getItemFromHotbar(Item.getItemFromBlock(Blocks.PISTON));
            if (pistonSlot == -1) {
                disableModule("No piston found, disabling AutoEcMeMainFucker.");
                return;
            }
            switch (i) {
                case 1:
                    mc.player.connection.sendPacket(new CPacketPlayer.Rotation(180, mc.player.rotationPitch, mc.player.onGround));
                    break;
                case 2:
                    mc.player.connection.sendPacket(new CPacketPlayer.Rotation(-90, mc.player.rotationPitch, mc.player.onGround));
                    break;
                case 3:
                    mc.player.connection.sendPacket(new CPacketPlayer.Rotation(0, mc.player.rotationPitch, mc.player.onGround));
                    break;
                case 4:
                    mc.player.connection.sendPacket(new CPacketPlayer.Rotation(90, mc.player.rotationPitch, mc.player.onGround));
                    break;
            }
            BlockUtil.placeBlockWithSwitch(piston, EnumHand.MAIN_HAND, rotate.getValue(), packet.getValue(), pistonSlot);
            return;
        }
        if (mc.world.getEntitiesWithinAABBExcludingEntity(null, new AxisAlignedBB(crystal.up())).isEmpty()) {
            int slot = InventoryUtil.getItemFromHotbar(Items.END_CRYSTAL);
            if (slot == -1) {
                disableModule("No end crystals found, disabling AutoEcMeMainFucker.");
                return;
            }
            int currentItem = mc.player.inventory.currentItem;
            InventoryUtil.switchToSlot(slot);
            float[] rotation = new float[]{mc.player.rotationYaw, mc.player.rotationPitch};
            if (crystalRotate.getValue()) {
                posRotate(crystal);
            }
            Objects.requireNonNull(mc.getConnection()).sendPacket(new CPacketPlayerTryUseItemOnBlock(crystal, EnumFacing.UP, mc.player.getHeldItemOffhand().getItem() == Items.END_CRYSTAL ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND, 0.5f, 0.5f, 0.5f));
            if (crystalRotate.getValue()) {
                mc.player.rotationYaw = rotation[0];
                mc.player.rotationPitch = rotation[1];
            }
            mc.player.inventory.currentItem = currentItem;
            mc.playerController.updateController();
            if (placeSwing.getValue())
                EntityUtil.swingArm(placeSwingHand.getValue().equals("Mainhand") ? EntityUtil.SwingType.MainHand : placeSwingHand.getValue().equals("Offhand") ? EntityUtil.SwingType.OffHand : EntityUtil.SwingType.Packet);
            return;
        }
        if (attemptSelfDestruct.getValue() && !isntAir(opposite)){
            int obsidianSlot = InventoryUtil.getItemFromHotbar(Item.getItemFromBlock(Blocks.OBSIDIAN));
            if (obsidianSlot == -1) {
                disableModule("No obsidian found, disabling AutoEcMeMainFucker.");
                return;
            }
            BlockUtil.placeBlockWithSwitch(opposite, EnumHand.MAIN_HAND, rotate.getValue(), packet.getValue(), obsidianSlot);
            return;
        }
        if (!isntAir(redstoneBlock)) {
            if (timer.getTime(actionDelay.getValue() + extraRedstoneDelay.getValue())) {
                int redstoneSlot = InventoryUtil.getItemFromHotbar(Item.getItemFromBlock(Blocks.REDSTONE_BLOCK));
                if (redstoneSlot == -1) {
                    disableModule("No redstone blocks found, disabling AutoEcMeMainFucker.");
                    return;
                }
                BlockUtil.placeBlockWithSwitch(redstoneBlock, EnumHand.MAIN_HAND, rotate.getValue(), packet.getValue(), redstoneSlot);
                if (autoMine.getValue()) {
                    if (pickSwitch.getValue()) {
                        int slot = InventoryUtil.getItemFromHotbar(Items.DIAMOND_PICKAXE);
                        if (slot == -1)
                            return;
                        InventoryUtil.switchToSlot(slot);
                    }
                    EntityUtil.swingArm(EntityUtil.SwingType.MainHand);
                    mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.START_DESTROY_BLOCK, redstoneBlock, EnumFacing.UP));
                    mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, redstoneBlock, EnumFacing.UP));
                }
            }
            return;
        }
        if (!mc.world.getEntitiesWithinAABBExcludingEntity(null, new AxisAlignedBB(top)).isEmpty()) {
            for (Entity entity : mc.world.loadedEntityList) {
                if (entity instanceof EntityEnderCrystal && mc.player.getDistance(entity) < placeRange.getValue()) {
                    float[] rotation = new float[]{mc.player.rotationYaw, mc.player.rotationPitch};
                    if (crystalRotate.getValue()) {
                        entityRotate(entity);
                    }
                    Objects.requireNonNull(mc.getConnection()).sendPacket(new CPacketUseEntity(entity));
                    if (crystalRotate.getValue()) {
                        mc.player.rotationYaw = rotation[0];
                        mc.player.rotationPitch = rotation[1];
                    }
                    boob = -1;
                    post.setTime(0);
                }
            }
        }
    }

    public void entityRotate(Entity entity) {
        float[] angle = BlockUtil.calcAngle(mc.player.getPositionEyes(mc.getRenderPartialTicks()), entity.getPositionVector());
        mc.player.rotationYaw = angle[0];
        mc.player.rotationPitch = angle[1];
    }

    public void posRotate(BlockPos pos) {
        float[] angle = BlockUtil.calcAngle(mc.player.getPositionEyes(mc.getRenderPartialTicks()), new Vec3d((float) pos.getX() + 0.5f, (float) pos.getY() - 0.5f, (float) pos.getZ() + 0.5f));
        mc.player.rotationYaw = angle[0];
        mc.player.rotationPitch = angle[1];
    }

    public int getPossiblePosition(EntityPlayer entityPlayer) {
        BlockPos pos = EntityUtil.getPlayerPos(entityPlayer).up();
        if ((mc.world.getBlockState(pos.north().north()).getBlock().equals(Blocks.OBSIDIAN) || mc.world.getBlockState(pos.north().north()).getBlock().equals(Blocks.BEDROCK) || mc.world.getBlockState(pos.north().north()).getBlock().equals(Blocks.AIR)) && mc.world.getBlockState(pos.north().up()).getBlock().equals(Blocks.AIR) && mc.world.getBlockState(pos.north().up().up()).getBlock().equals(Blocks.AIR) && (mc.world.getBlockState(pos.north().north().up()).getBlock().equals(Blocks.AIR) || mc.world.getBlockState(pos.north().north().up()).getBlock().equals(Blocks.PISTON)) && mc.world.getBlockState(pos.north().north().north().up()).getBlock().equals(Blocks.AIR))
            if (mc.player.getDistanceSq(pos.up()) < (placeRange.getValue() * placeRange.getValue()) && mc.player.getDistanceSq(pos.north().north()) < (placeRange.getValue() * placeRange.getValue()) && mc.player.getDistanceSq(pos.north().up()) < (placeRange.getValue() * placeRange.getValue()) && mc.player.getDistanceSq(pos.north().up().up()) < (placeRange.getValue() * placeRange.getValue()) && mc.player.getDistanceSq(pos.north().north().up()) < (placeRange.getValue() * placeRange.getValue()) && mc.player.getDistanceSq(pos.north().north().north().up()) < (placeRange.getValue() * placeRange.getValue()))
                return 1;
        if ((mc.world.getBlockState(pos.east().east()).getBlock().equals(Blocks.OBSIDIAN) || mc.world.getBlockState(pos.east().east()).getBlock().equals(Blocks.BEDROCK) || mc.world.getBlockState(pos.east().east()).getBlock().equals(Blocks.AIR)) && mc.world.getBlockState(pos.east().up()).getBlock().equals(Blocks.AIR) && mc.world.getBlockState(pos.east().up().up()).getBlock().equals(Blocks.AIR) && (mc.world.getBlockState(pos.east().east().up()).getBlock().equals(Blocks.AIR) || mc.world.getBlockState(pos.east().east().up()).getBlock().equals(Blocks.PISTON)) && mc.world.getBlockState(pos.east().east().east().up()).getBlock().equals(Blocks.AIR))
            if (mc.player.getDistanceSq(pos.up()) < (placeRange.getValue() * placeRange.getValue()) && mc.player.getDistanceSq(pos.east().east()) < (placeRange.getValue() * placeRange.getValue()) && mc.player.getDistanceSq(pos.east().up()) < (placeRange.getValue() * placeRange.getValue()) && mc.player.getDistanceSq(pos.east().up().up()) < (placeRange.getValue() * placeRange.getValue()) && mc.player.getDistanceSq(pos.east().east().up()) < (placeRange.getValue() * placeRange.getValue()) && mc.player.getDistanceSq(pos.east().east().east().up()) < (placeRange.getValue() * placeRange.getValue()))
                return 2;
        if ((mc.world.getBlockState(pos.south().south()).getBlock().equals(Blocks.OBSIDIAN) || mc.world.getBlockState(pos.south().south()).getBlock().equals(Blocks.BEDROCK) || mc.world.getBlockState(pos.south().south()).getBlock().equals(Blocks.AIR)) && mc.world.getBlockState(pos.south().up()).getBlock().equals(Blocks.AIR) && mc.world.getBlockState(pos.south().up().up()).getBlock().equals(Blocks.AIR) && (mc.world.getBlockState(pos.south().south().up()).getBlock().equals(Blocks.AIR) || mc.world.getBlockState(pos.south().south().up()).getBlock().equals(Blocks.PISTON)) && mc.world.getBlockState(pos.south().south().south().up()).getBlock().equals(Blocks.AIR))
            if (mc.player.getDistanceSq(pos.up()) < (placeRange.getValue() * placeRange.getValue()) && mc.player.getDistanceSq(pos.south().south()) < (placeRange.getValue() * placeRange.getValue()) && mc.player.getDistanceSq(pos.south().up()) < (placeRange.getValue() * placeRange.getValue()) && mc.player.getDistanceSq(pos.south().up().up()) < (placeRange.getValue() * placeRange.getValue()) && mc.player.getDistanceSq(pos.south().south().up()) < (placeRange.getValue() * placeRange.getValue()) && mc.player.getDistanceSq(pos.south().south().south().up()) < (placeRange.getValue() * placeRange.getValue()))
                return 3;
        if ((mc.world.getBlockState(pos.west().west()).getBlock().equals(Blocks.OBSIDIAN) || mc.world.getBlockState(pos.west().west()).getBlock().equals(Blocks.BEDROCK) || mc.world.getBlockState(pos.west().west()).getBlock().equals(Blocks.AIR)) && mc.world.getBlockState(pos.west().up()).getBlock().equals(Blocks.AIR) && mc.world.getBlockState(pos.west().up().up()).getBlock().equals(Blocks.AIR) && (mc.world.getBlockState(pos.west().west().up()).getBlock().equals(Blocks.AIR) || mc.world.getBlockState(pos.west().west().up()).getBlock().equals(Blocks.PISTON)) && mc.world.getBlockState(pos.west().west().west().up()).getBlock().equals(Blocks.AIR))
            if (mc.player.getDistanceSq(pos.up()) < (placeRange.getValue() * placeRange.getValue()) && mc.player.getDistanceSq(pos.west().west()) < (placeRange.getValue() * placeRange.getValue()) && mc.player.getDistanceSq(pos.west().up()) < (placeRange.getValue() * placeRange.getValue()) && mc.player.getDistanceSq(pos.west().up().up()) < (placeRange.getValue() * placeRange.getValue()) && mc.player.getDistanceSq(pos.west().west().up()) < (placeRange.getValue() * placeRange.getValue()) && mc.player.getDistanceSq(pos.west().west().west().up()) < (placeRange.getValue() * placeRange.getValue()))
                return 4;
        return -1;
    }

    public boolean isFaggot(EntityPlayer entityPlayer) {
        BlockPos playerPosUp = EntityUtil.getPlayerPos(entityPlayer).up();
        return BlockUtil.isPlayerSafe(entityPlayer) && isntAir(playerPosUp.north()) && isntAir(playerPosUp.east()) && isntAir(playerPosUp.south()) && isntAir(playerPosUp.west()) && isntAir(playerPosUp.up());
    }

    public boolean isntAir(BlockPos pos) {
        return !mc.world.getBlockState(pos).getBlock().equals(Blocks.AIR);
    }
}
