package dev.zprestige.ruby.module.combat;

import dev.zprestige.ruby.Ruby;
import dev.zprestige.ruby.module.Module;
import dev.zprestige.ruby.setting.impl.*;
import dev.zprestige.ruby.util.BlockUtil;
import dev.zprestige.ruby.util.EntityUtil;
import dev.zprestige.ruby.util.InventoryUtil;
import dev.zprestige.ruby.util.Timer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
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
import java.util.TreeMap;

public class TrapCrystal extends Module {

    public ParentSetting timing = createSetting("Timing");
    public IntegerSetting actionDelay = createSetting("Action Delay", 100, 0, 1000).setParent(timing);

    public ParentSetting ranges = createSetting("Ranges");
    public FloatSetting targetRange = createSetting("Target Range", 9.0f, 0.1f, 15.0f).setParent(ranges);
    public FloatSetting placeRange = createSetting("Place Range", 5.0f, 0.1f, 6.0f).setParent(ranges);

    public ParentSetting mining = createSetting("Mining");
    public ModeSetting mineMode = createSetting("Mine Mode", "Click", Arrays.asList("Click", "Packet", "ManualSwitchPacket", "Damage")).setParent(mining);

    public ParentSetting rotations = createSetting("Rotations");
    public BooleanSetting blockRotate = createSetting("Block Rotate", false).setParent(rotations);
    public BooleanSetting mineRotate = createSetting("Mine Rotate", false).setParent(rotations);
    public BooleanSetting crystalRotate = createSetting("Crystal Rotate", false).setParent(rotations);

    public ParentSetting packets = createSetting("Packets");
    public BooleanSetting blockPacket = createSetting("Block Packet", false).setParent(packets);
    public BooleanSetting crystalPacket = createSetting("Crystal Packet", false).setParent(packets);

    public int stage = 0;
    public boolean isMining = false, hasMined = false;
    public Timer miningTimer = new Timer(), actionTimer = new Timer();

    @Override
    public void onEnable() {
        isMining = false;
    }

    @Override
    public void onTick() {
        EntityPlayer entityPlayer = getUntrappedClosestEntityPlayer(targetRange.getValue());
        int crystalSlot = InventoryUtil.getItemFromHotbar(Items.END_CRYSTAL);
        int obsidianSlot = InventoryUtil.getItemFromHotbar(Item.getItemFromBlock(Blocks.OBSIDIAN));
        int pickSlot = InventoryUtil.getItemFromHotbar(Items.DIAMOND_PICKAXE);
        int currentItem = mc.player.inventory.currentItem;
        if (entityPlayer == null || obsidianSlot == -1 || crystalSlot == -1 || !actionTimer.getTime(actionDelay.getValue()) || pickSlot == -1)
            return;
        BlockPos pos = EntityUtil.getPlayerPos(entityPlayer).up().up();
        stage = getStage(pos);
        float[] rotations = new float[]{mc.player.rotationYaw, mc.player.rotationPitch};
        switch (stage) {
            case 0:
                BlockUtil.placeBlockWithSwitch(pos, EnumHand.MAIN_HAND, blockRotate.getValue(), blockPacket.getValue(), obsidianSlot, actionTimer);
                break;
            case 1:
                if (crystalRotate.getValue())
                    posRotate(pos);
                if (crystalPacket.getValue())
                    Objects.requireNonNull(mc.getConnection()).sendPacket(new CPacketPlayerTryUseItemOnBlock(pos, EnumFacing.UP, mc.player.getHeldItemOffhand().getItem() == Items.END_CRYSTAL ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND, 0.5f, 0.5f, 0.5f));
                else
                    mc.playerController.processRightClickBlock(mc.player, mc.world, pos, EnumFacing.UP, new Vec3d(mc.player.posX, -mc.player.posY, -mc.player.posZ), mc.player.getHeldItemOffhand().getItem().equals(Items.END_CRYSTAL) ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND);
                actionTimer.setTime(0);
                if (crystalRotate.getValue()) {
                    mc.player.rotationYaw = rotations[0];
                    mc.player.rotationPitch = rotations[1];
                }
                break;
            case 2:
                if (!hasMined) {
                    mineBlock(pos, EnumFacing.UP);
                    hasMined = true;
                }
                break;
            case 3:
                if (miningTimer.getTime(2000))
                    finishMining(pos, EnumFacing.UP, pickSlot, currentItem);
                break;
            case 4:
                for (Entity entity : mc.world.loadedEntityList) {
                    if (!(entity instanceof EntityEnderCrystal))
                        continue;
                    if (entity.getDistanceSq(pos) > 4.0f)
                        continue;
                    if (crystalRotate.getValue())
                        entityRotate(entity);
                    if (crystalPacket.getValue())
                        Objects.requireNonNull(mc.getConnection()).sendPacket(new CPacketUseEntity(entity));
                    else
                        mc.playerController.attackEntity(mc.player, entity);
                    if (crystalRotate.getValue()) {
                        mc.player.rotationYaw = rotations[0];
                        mc.player.rotationPitch = rotations[1];
                    }
                    isMining = false;
                }
        }
    }

    public int getStage(BlockPos pos) {
        if (canPlace(pos) && stage != 3)
            return 0;
        if (mc.world.getEntitiesWithinAABB(EntityEnderCrystal.class, new AxisAlignedBB(pos.up())).isEmpty())
            return 1;
        if (!isMining) {
            return 2;
        }
        if (canPlace(pos)) {
            return 4;
        }
        if (isMining) {
            return 3;
        }
        return -1;
    }


    public void finishMining(BlockPos pos, EnumFacing face, int slot, int currentItem) {
        float[] rotations = new float[]{mc.player.rotationYaw, mc.player.rotationPitch};
        if (mineRotate.getValue())
            posRotate(pos);
        switch (mineMode.getValue()) {
            case "Click":
                mc.playerController.clickBlock(pos, face);
                break;
            case "Packet":
            case "Damage":
                InventoryUtil.switchToSlot(slot);
                Objects.requireNonNull(mc.getConnection()).sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, pos, EnumFacing.UP));
                mc.player.inventory.currentItem = currentItem;
                mc.playerController.updateController();
        }
        if (mineRotate.getValue()) {
            mc.player.rotationYaw = rotations[0];
            mc.player.rotationPitch = rotations[1];
        }
        hasMined = false;
        actionTimer.setTime(0);
    }

    public void mineBlock(BlockPos pos, EnumFacing face) {
        float[] rotations = new float[]{mc.player.rotationYaw, mc.player.rotationPitch};
        if (mineRotate.getValue())
            posRotate(pos);
        switch (mineMode.getValue()) {
            case "Click":
                mc.playerController.clickBlock(pos, face);
                break;
            case "ManualSwitchPacket":
            case "Packet":
                mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.START_DESTROY_BLOCK, pos, face));
                EntityUtil.swingArm(EntityUtil.SwingType.MainHand);
                mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, pos, face));
                break;
            case "Damage":
                mc.playerController.onPlayerDamageBlock(pos, mc.player.getHorizontalFacing());
                EntityUtil.swingArm(EntityUtil.SwingType.MainHand);
                break;
        }
        if (mineRotate.getValue()) {
            mc.player.rotationYaw = rotations[0];
            mc.player.rotationPitch = rotations[1];
        }
        actionTimer.setTime(0);
        miningTimer.setTime(0);
        isMining = true;
    }

    public EntityPlayer getUntrappedClosestEntityPlayer(float range) {
        TreeMap<Float, EntityPlayer> treeMap = new TreeMap<>();
        for (EntityPlayer entityPlayer : mc.world.playerEntities) {
            BlockPos upUpPos = EntityUtil.getPlayerPos(entityPlayer).up().up();
            if (!entityPlayer.equals(mc.player) && !Ruby.friendManager.isFriend(entityPlayer.getName()) && mc.player.getDistance(entityPlayer) < range && BlockUtil.isPlayerSafe(entityPlayer) && canPlaceInclObsidian(upUpPos) && canPlace(upUpPos.up()) && canPlace(upUpPos.up().up())) {
                treeMap.put(mc.player.getDistance(entityPlayer), entityPlayer);
            }
        }
        if (!treeMap.isEmpty())
            return treeMap.firstEntry().getValue();
        return null;
    }

    public boolean canPlaceInclObsidian(BlockPos pos) {
        return mc.world.getEntitiesWithinAABB(EntityPlayer.class, new AxisAlignedBB(pos)).isEmpty() && mc.player.getDistanceSq(pos) < (placeRange.getValue() * placeRange.getValue()) && (mc.world.getBlockState(pos).getBlock().equals(Blocks.AIR) || mc.world.getBlockState(pos).getBlock().equals(Blocks.OBSIDIAN));
    }

    public boolean canPlace(BlockPos pos) {
        return mc.world.getEntitiesWithinAABB(EntityPlayer.class, new AxisAlignedBB(pos)).isEmpty() && mc.player.getDistanceSq(pos) < (placeRange.getValue() * placeRange.getValue()) && mc.world.getBlockState(pos).getBlock().equals(Blocks.AIR);
    }

    public void posRotate(BlockPos pos) {
        float[] angle = BlockUtil.calcAngle(mc.player.getPositionEyes(mc.getRenderPartialTicks()), new Vec3d((float) pos.getX() + 0.5f, (float) pos.getY() - 0.5f, (float) pos.getZ() + 0.5f));
        mc.player.rotationYaw = angle[0];
        mc.player.rotationPitch = angle[1];
    }

    public void entityRotate(Entity entity) {
        float[] angle = BlockUtil.calcAngle(mc.player.getPositionEyes(mc.getRenderPartialTicks()), entity.getPositionVector());
        mc.player.rotationYaw = angle[0];
        mc.player.rotationPitch = angle[1];
    }
}
