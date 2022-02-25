package dev.zprestige.ruby.util;

import dev.zprestige.ruby.Ruby;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import java.util.*;

public class BlockUtil {
    public static Minecraft mc = Minecraft.getMinecraft();
    public static List<Block> blackList = Arrays.asList(Blocks.ENDER_CHEST, Blocks.CHEST, Blocks.TRAPPED_CHEST, Blocks.CRAFTING_TABLE, Blocks.ANVIL, Blocks.BREWING_STAND, Blocks.HOPPER, Blocks.DROPPER, Blocks.DISPENSER, Blocks.TRAPDOOR, Blocks.ENCHANTING_TABLE);
    public static List<Block> shulkerList = Arrays.asList(Blocks.WHITE_SHULKER_BOX, Blocks.ORANGE_SHULKER_BOX, Blocks.MAGENTA_SHULKER_BOX, Blocks.LIGHT_BLUE_SHULKER_BOX, Blocks.YELLOW_SHULKER_BOX, Blocks.LIME_SHULKER_BOX, Blocks.PINK_SHULKER_BOX, Blocks.GRAY_SHULKER_BOX, Blocks.SILVER_SHULKER_BOX, Blocks.CYAN_SHULKER_BOX, Blocks.PURPLE_SHULKER_BOX, Blocks.BLUE_SHULKER_BOX, Blocks.BROWN_SHULKER_BOX, Blocks.GREEN_SHULKER_BOX, Blocks.RED_SHULKER_BOX, Blocks.BLACK_SHULKER_BOX);

    public static boolean hasBlockEnumFacing(BlockPos pos) {
        return isAir(pos.up()) || isAir(pos.down()) || isAir(pos.north()) || isAir(pos.east()) || isAir(pos.south()) || isAir(pos.west());
    }

    public static EnumFacing getClosestEnumFacing(BlockPos pos) {
        TreeMap<Double, EnumFacing> facingTreeMap = new TreeMap<>();
        if (isAir(pos.up())) {
            facingTreeMap.put(mc.player.getDistanceSq(pos.up()), EnumFacing.UP);
        }
        if (isAir(pos.down())) {
            facingTreeMap.put(mc.player.getDistanceSq(pos.down()), EnumFacing.DOWN);
        }
        if (isAir(pos.north())) {
            facingTreeMap.put(mc.player.getDistanceSq(pos.north()), EnumFacing.NORTH);
        }
        if (isAir(pos.east())) {
            facingTreeMap.put(mc.player.getDistanceSq(pos.east()), EnumFacing.EAST);
        }
        if (isAir(pos.south())) {
            facingTreeMap.put(mc.player.getDistanceSq(pos.south()), EnumFacing.SOUTH);
        }
        if (isAir(pos.west())) {
            facingTreeMap.put(mc.player.getDistanceSq(pos.west()), EnumFacing.WEST);
        }
        if (!facingTreeMap.isEmpty())
            return facingTreeMap.firstEntry().getValue();
        return null;
    }

    public static boolean isAir(BlockPos pos) {
        return mc.world.getBlockState(pos).getBlock().equals(Blocks.AIR);
    }

    public static float[] calcAngle(Vec3d from, Vec3d to) {
        double difX = to.x - from.x;
        double difY = (to.y - from.y) * -1.0;
        double difZ = to.z - from.z;
        double dist = MathHelper.sqrt(difX * difX + difZ * difZ);
        return new float[]{(float) MathHelper.wrapDegrees(Math.toDegrees(Math.atan2(difZ, difX)) - 90.0), (float) MathHelper.wrapDegrees(Math.toDegrees(Math.atan2(difY, dist)))};
    }

    public static boolean rayTraceCheckPos(BlockPos pos) {
        return mc.world.rayTraceBlocks(new Vec3d(mc.player.posX, mc.player.posY + (double) mc.player.getEyeHeight(), mc.player.posZ), new Vec3d(pos.getX(), pos.getY(), pos.getZ()), false, true, false) != null;
    }

    public static boolean isPlayerSafe(EntityPlayer target) {
        BlockPos playerPos = new BlockPos(Math.floor(target.posX), Math.floor(target.posY), Math.floor(target.posZ));
        return (mc.world.getBlockState(playerPos.down()).getBlock() == Blocks.OBSIDIAN || mc.world.getBlockState(playerPos.down()).getBlock() == Blocks.BEDROCK) &&
                (mc.world.getBlockState(playerPos.north()).getBlock() == Blocks.OBSIDIAN || mc.world.getBlockState(playerPos.north()).getBlock() == Blocks.BEDROCK) &&
                (mc.world.getBlockState(playerPos.east()).getBlock() == Blocks.OBSIDIAN || mc.world.getBlockState(playerPos.east()).getBlock() == Blocks.BEDROCK) &&
                (mc.world.getBlockState(playerPos.south()).getBlock() == Blocks.OBSIDIAN || mc.world.getBlockState(playerPos.south()).getBlock() == Blocks.BEDROCK) &&
                (mc.world.getBlockState(playerPos.west()).getBlock() == Blocks.OBSIDIAN || mc.world.getBlockState(playerPos.west()).getBlock() == Blocks.BEDROCK);
    }

    public static boolean isPlayerSafe2(EntityPlayer entityPlayer) {
        BlockPos pos = EntityUtil.getPlayerPos(entityPlayer);
        if (isNotIntersecting(entityPlayer)) {
            return isBedrockOrObsidianOrEchest(pos.north()) && isBedrockOrObsidianOrEchest(pos.east()) && isBedrockOrObsidianOrEchest(pos.south()) && isBedrockOrObsidianOrEchest(pos.west()) && isBedrockOrObsidianOrEchest(pos.down());
        } else {
            return isIntersectingSafe(entityPlayer);
        }
    }

    public static boolean isNotIntersecting(EntityPlayer entityPlayer) {
        BlockPos pos = EntityUtil.getPlayerPos(entityPlayer);
        AxisAlignedBB bb = entityPlayer.getEntityBoundingBox();
        return (!isAir(pos.north()) || !bb.intersects(new AxisAlignedBB(pos.north()))) && (!isAir(pos.east()) || !bb.intersects(new AxisAlignedBB(pos.east()))) && (!isAir(pos.south()) || !bb.intersects(new AxisAlignedBB(pos.south()))) && (!isAir(pos.west()) || !bb.intersects(new AxisAlignedBB(pos.west())));
    }

    public static boolean isIntersectingSafe(EntityPlayer entityPlayer) {
        BlockPos pos = EntityUtil.getPlayerPos(entityPlayer);
        AxisAlignedBB bb = entityPlayer.getEntityBoundingBox();
        if (isAir(pos.north()) && bb.intersects(new AxisAlignedBB(pos.north()))) {
            BlockPos pos1 = pos.north();
            if (!isBedrockOrObsidianOrEchest(pos1.north()) || !isBedrockOrObsidianOrEchest(pos1.east()) || !isBedrockOrObsidianOrEchest(pos1.west()) || !isBedrockOrObsidianOrEchest(pos1.down()))
                return false;
        }
        if (isAir(pos.east()) && bb.intersects(new AxisAlignedBB(pos.east()))) {
            BlockPos pos1 = pos.east();
            if (!isBedrockOrObsidianOrEchest(pos1.north()) || !isBedrockOrObsidianOrEchest(pos1.east()) || !isBedrockOrObsidianOrEchest(pos1.south()) || !isBedrockOrObsidianOrEchest(pos1.down()))
                return false;
        }
        if (isAir(pos.south()) && bb.intersects(new AxisAlignedBB(pos.south()))) {
            BlockPos pos1 = pos.south();
            if (!isBedrockOrObsidianOrEchest(pos1.east()) || !isBedrockOrObsidianOrEchest(pos1.south()) || !isBedrockOrObsidianOrEchest(pos1.west()) || !isBedrockOrObsidianOrEchest(pos1.down()))
                return false;
        }
        if (isAir(pos.west()) && bb.intersects(new AxisAlignedBB(pos.west()))) {
            BlockPos pos1 = pos.west();
            return isBedrockOrObsidianOrEchest(pos1.north()) && isBedrockOrObsidianOrEchest(pos1.south()) && isBedrockOrObsidianOrEchest(pos1.west()) && isBedrockOrObsidianOrEchest(pos1.down());
        }
        return true;
    }

    public static boolean isBedrockOrObsidianOrEchest(BlockPos pos) {
        return mc.world.getBlockState(pos).getBlock().equals(Blocks.BEDROCK) || mc.world.getBlockState(pos).getBlock().equals(Blocks.OBSIDIAN) || mc.world.getBlockState(pos).getBlock().equals(Blocks.ENDER_CHEST);
    }

    public static boolean isPosValidForCrystal(BlockPos pos, boolean onepointthirteen) {
        if (mc.world.getBlockState(pos).getBlock() != Blocks.BEDROCK && mc.world.getBlockState(pos).getBlock() != Blocks.OBSIDIAN)
            return false;

        if (mc.world.getBlockState(pos.up()).getBlock() != Blocks.AIR || (!onepointthirteen && mc.world.getBlockState(pos.up().up()).getBlock() != Blocks.AIR))
            return false;

        for (Entity entity : mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(pos.up()))) {

            if (entity.isDead || entity instanceof EntityEnderCrystal)
                continue;

            return false;
        }

        for (Entity entity : mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(pos.up().up()))) {

            if (entity.isDead || entity instanceof EntityEnderCrystal)

                continue;

            return false;
        }

        return true;
    }


    public static List<BlockPos> getSphereAutoCrystal(double radius, boolean noAir) {
        ArrayList<BlockPos> posList = new ArrayList<>();
        BlockPos pos = new BlockPos(Math.floor(mc.player.posX), Math.floor(mc.player.posY), Math.floor(mc.player.posZ));
        for (int x = pos.getX() - (int) radius; x <= pos.getX() + radius; ++x) {
            for (int y = pos.getY() - (int) radius; y < pos.getY() + radius; ++y) {
                for (int z = pos.getZ() - (int) radius; z <= pos.getZ() + radius; ++z) {
                    double distance = (pos.getX() - x) * (pos.getX() - x) + (pos.getZ() - z) * (pos.getZ() - z) + (pos.getY() - y) * (pos.getY() - y);
                    BlockPos position = new BlockPos(x, y, z);
                    if (distance < radius * radius && (noAir && !mc.world.getBlockState(position).getBlock().equals(Blocks.AIR))) {
                        posList.add(position);
                    }
                }
            }
        }
        return posList;
    }


    public static BlockPos getClosestHoleToPlayer(EntityPlayer entityPlayer, float radius, boolean doubleHoles) {
        if (entityPlayer == null || entityPlayer.isDead || Ruby.friendManager.isFriend(entityPlayer.getName()) || entityPlayer.equals(mc.player))
            return null;
        TreeMap<Double, Hole> holes = new TreeMap<>();
        for (BlockPos pos : getSphere(radius, AirType.OnlyAir, entityPlayer)) {
            if (Ruby.mc.world.getBlockState(pos.up()).getBlock() == Blocks.AIR && Ruby.mc.world.getBlockState(pos).getBlock() == Blocks.AIR && Ruby.mc.world.getBlockState(pos.down()).getBlock() == Blocks.BEDROCK && Ruby.mc.world.getBlockState(pos.north()).getBlock() == Blocks.BEDROCK && Ruby.mc.world.getBlockState(pos.south()).getBlock() == Blocks.BEDROCK && Ruby.mc.world.getBlockState(pos.west()).getBlock() == Blocks.BEDROCK && Ruby.mc.world.getBlockState(pos.east()).getBlock() == Blocks.BEDROCK)
                holes.put(entityPlayer.getDistanceSq(pos), new Hole(pos, entityPlayer.getDistanceSq(pos)));
            else if (Ruby.mc.world.getBlockState(pos.up()).getBlock() == Blocks.AIR && Ruby.mc.world.getBlockState(pos).getBlock() == Blocks.AIR && (Ruby.mc.world.getBlockState(pos.down()).getBlock() == Blocks.BEDROCK || Ruby.mc.world.getBlockState(pos.down()).getBlock() == Blocks.OBSIDIAN) && (Ruby.mc.world.getBlockState(pos.north()).getBlock() == Blocks.OBSIDIAN || Ruby.mc.world.getBlockState(pos.north()).getBlock() == Blocks.BEDROCK) && (Ruby.mc.world.getBlockState(pos.south()).getBlock() == Blocks.OBSIDIAN || Ruby.mc.world.getBlockState(pos.south()).getBlock() == Blocks.BEDROCK) && (Ruby.mc.world.getBlockState(pos.west()).getBlock() == Blocks.OBSIDIAN || Ruby.mc.world.getBlockState(pos.west()).getBlock() == Blocks.BEDROCK) && (Ruby.mc.world.getBlockState(pos.east()).getBlock() == Blocks.OBSIDIAN || Ruby.mc.world.getBlockState(pos.east()).getBlock() == Blocks.BEDROCK))
                holes.put(entityPlayer.getDistanceSq(pos), new Hole(pos, entityPlayer.getDistanceSq(pos)));
            if (doubleHoles) {
                if (Ruby.mc.world.getBlockState(pos.up()).getBlock() == Blocks.AIR && Ruby.mc.world.getBlockState(pos.north().up()).getBlock() == Blocks.AIR && Ruby.mc.world.getBlockState(pos).getBlock() == Blocks.AIR && Ruby.mc.world.getBlockState(pos.down()).getBlock() == Blocks.BEDROCK && Ruby.mc.world.getBlockState(pos.north().down()).getBlock() == Blocks.BEDROCK && Ruby.mc.world.getBlockState(pos.north()).getBlock() == Blocks.AIR && Ruby.mc.world.getBlockState(pos.north().north()).getBlock() == Blocks.BEDROCK && Ruby.mc.world.getBlockState(pos.east()).getBlock() == Blocks.BEDROCK && Ruby.mc.world.getBlockState(pos.north().east()).getBlock() == Blocks.BEDROCK && Ruby.mc.world.getBlockState(pos.south()).getBlock() == Blocks.BEDROCK && Ruby.mc.world.getBlockState(pos.north().west()).getBlock() == Blocks.BEDROCK && Ruby.mc.world.getBlockState(pos.west()).getBlock() == Blocks.BEDROCK && Ruby.mc.world.getBlockState(pos.east()).getBlock() == Blocks.BEDROCK) {
                    holes.put(entityPlayer.getDistanceSq(pos), new Hole(pos, entityPlayer.getDistanceSq(pos)));
                    holes.put(entityPlayer.getDistanceSq(pos.north()), new Hole(pos, entityPlayer.getDistanceSq(pos.north())));
                } else if (Ruby.mc.world.getBlockState(pos.up()).getBlock() == Blocks.AIR && Ruby.mc.world.getBlockState(pos.north().up()).getBlock() == Blocks.AIR && Ruby.mc.world.getBlockState(pos).getBlock() == Blocks.AIR && (Ruby.mc.world.getBlockState(pos.down()).getBlock() == Blocks.BEDROCK || Ruby.mc.world.getBlockState(pos.down()).getBlock() == Blocks.OBSIDIAN) && Ruby.mc.world.getBlockState(pos.north()).getBlock() == Blocks.AIR && (Ruby.mc.world.getBlockState(pos.south()).getBlock() == Blocks.OBSIDIAN || Ruby.mc.world.getBlockState(pos.south()).getBlock() == Blocks.BEDROCK) && (Ruby.mc.world.getBlockState(pos.west()).getBlock() == Blocks.OBSIDIAN || Ruby.mc.world.getBlockState(pos.west()).getBlock() == Blocks.BEDROCK) && (Ruby.mc.world.getBlockState(pos.east()).getBlock() == Blocks.OBSIDIAN || Ruby.mc.world.getBlockState(pos.east()).getBlock() == Blocks.BEDROCK) && (Ruby.mc.world.getBlockState(pos.north().north()).getBlock() == Blocks.BEDROCK || Ruby.mc.world.getBlockState(pos.north().north()).getBlock() == Blocks.OBSIDIAN) && (Ruby.mc.world.getBlockState(pos.north().east()).getBlock() == Blocks.BEDROCK || Ruby.mc.world.getBlockState(pos.north().east()).getBlock() == Blocks.OBSIDIAN) && (Ruby.mc.world.getBlockState(pos.north().west()).getBlock() == Blocks.BEDROCK || Ruby.mc.world.getBlockState(pos.north().west()).getBlock() == Blocks.OBSIDIAN) && (Ruby.mc.world.getBlockState(pos.north().down()).getBlock() == Blocks.OBSIDIAN || Ruby.mc.world.getBlockState(pos.north().down()).getBlock() == Blocks.BEDROCK)) {
                    holes.put(entityPlayer.getDistanceSq(pos), new Hole(pos, entityPlayer.getDistanceSq(pos)));
                    holes.put(entityPlayer.getDistanceSq(pos.north()), new Hole(pos, entityPlayer.getDistanceSq(pos.north())));
                } else if (Ruby.mc.world.getBlockState(pos.up()).getBlock() == Blocks.AIR && Ruby.mc.world.getBlockState(pos.west().up()).getBlock() == Blocks.AIR && Ruby.mc.world.getBlockState(pos).getBlock() == Blocks.AIR && Ruby.mc.world.getBlockState(pos.down()).getBlock() == Blocks.BEDROCK && Ruby.mc.world.getBlockState(pos.west().down()).getBlock() == Blocks.BEDROCK && Ruby.mc.world.getBlockState(pos.north()).getBlock() == Blocks.BEDROCK && Ruby.mc.world.getBlockState(pos.south()).getBlock() == Blocks.BEDROCK && Ruby.mc.world.getBlockState(pos.west()).getBlock() == Blocks.AIR && Ruby.mc.world.getBlockState(pos.east()).getBlock() == Blocks.BEDROCK && Ruby.mc.world.getBlockState(pos.west().south()).getBlock() == Blocks.BEDROCK && Ruby.mc.world.getBlockState(pos.west().north()).getBlock() == Blocks.BEDROCK && Ruby.mc.world.getBlockState(pos.west().west()).getBlock() == Blocks.BEDROCK) {
                    holes.put(entityPlayer.getDistanceSq(pos), new Hole(pos, entityPlayer.getDistanceSq(pos)));
                    holes.put(entityPlayer.getDistanceSq(pos.west()), new Hole(pos, entityPlayer.getDistanceSq(pos.west())));
                } else if (Ruby.mc.world.getBlockState(pos.up()).getBlock() == Blocks.AIR && Ruby.mc.world.getBlockState(pos.west().up()).getBlock() == Blocks.AIR && Ruby.mc.world.getBlockState(pos).getBlock() == Blocks.AIR && (Ruby.mc.world.getBlockState(pos.down()).getBlock() == Blocks.BEDROCK || Ruby.mc.world.getBlockState(pos.down()).getBlock() == Blocks.OBSIDIAN) && (Ruby.mc.world.getBlockState(pos.west().down()).getBlock() == Blocks.BEDROCK || Ruby.mc.world.getBlockState(pos.west().down()).getBlock() == Blocks.OBSIDIAN) && (Ruby.mc.world.getBlockState(pos.north()).getBlock() == Blocks.BEDROCK || Ruby.mc.world.getBlockState(pos.north()).getBlock() == Blocks.OBSIDIAN) && (Ruby.mc.world.getBlockState(pos.south()).getBlock() == Blocks.BEDROCK || Ruby.mc.world.getBlockState(pos.south()).getBlock() == Blocks.OBSIDIAN) && Ruby.mc.world.getBlockState(pos.west()).getBlock() == Blocks.AIR && (Ruby.mc.world.getBlockState(pos.east()).getBlock() == Blocks.BEDROCK || Ruby.mc.world.getBlockState(pos.east()).getBlock() == Blocks.OBSIDIAN) && (Ruby.mc.world.getBlockState(pos.west().south()).getBlock() == Blocks.BEDROCK || Ruby.mc.world.getBlockState(pos.west().south()).getBlock() == Blocks.OBSIDIAN) && (Ruby.mc.world.getBlockState(pos.west().north()).getBlock() == Blocks.BEDROCK || Ruby.mc.world.getBlockState(pos.west().north()).getBlock() == Blocks.OBSIDIAN) && (Ruby.mc.world.getBlockState(pos.west().west()).getBlock() == Blocks.BEDROCK || Ruby.mc.world.getBlockState(pos.west().west()).getBlock() == Blocks.OBSIDIAN)) {
                    holes.put(entityPlayer.getDistanceSq(pos), new Hole(pos, entityPlayer.getDistanceSq(pos)));
                    holes.put(entityPlayer.getDistanceSq(pos.west()), new Hole(pos, entityPlayer.getDistanceSq(pos.west())));
                }
            }
        }
        if (!holes.isEmpty())
            return holes.lastEntry().getValue().pos;
        return null;
    }

    public static List<BlockPos> nearbyAirPosses(final double radius) {
        ArrayList<BlockPos> posses = new ArrayList<>();
        BlockPos pos = new BlockPos(mc.player.getPositionVector());
        int posX = pos.getX();
        int posY = pos.getY();
        int posZ = pos.getZ();
        int radiuss = (int) radius;
        for (int x = posX - radiuss; x <= posX + radius; ++x) {
            for (int z = posZ - radiuss; z <= posZ + radius; ++z) {
                for (int y = posY - radiuss; y < posY + radius; ++y) {
                    double dist = (posX - x) * (posX - x) + (posZ - z) * (posZ - z) + (posY - y) * (posY - y);
                    BlockPos position = new BlockPos(x, y, z);
                    if (dist < radius * radius) {
                        if (mc.world.getBlockState(position).getBlock().equals(Blocks.AIR)) {
                            posses.add(position);
                        }
                    }
                }
            }
        }
        return posses;
    }

    public static List<BlockPos> getSphere(final double radius, AirType airType, EntityPlayer entityPlayer) {
        ArrayList<BlockPos> sphere = new ArrayList<>();
        BlockPos pos = new BlockPos(entityPlayer.getPositionVector());
        int posX = pos.getX();
        int posY = pos.getY();
        int posZ = pos.getZ();
        int radiuss = (int) radius;
        for (int x = posX - radiuss; x <= posX + radius; ++x) {
            for (int z = posZ - radiuss; z <= posZ + radius; ++z) {
                for (int y = posY - radiuss; y < posY + radius; ++y) {
                    double dist = (posX - x) * (posX - x) + (posZ - z) * (posZ - z) + (posY - y) * (posY - y);
                    BlockPos position;
                    if (dist < radius * radius) {
                        position = new BlockPos(x, y, z);
                        if ((mc.world.getBlockState(position).getBlock().equals(Blocks.AIR) && airType.equals(AirType.IgnoreAir)) || (!mc.world.getBlockState(position).getBlock().equals(Blocks.AIR) && airType.equals(AirType.OnlyAir)))
                            continue;

                        sphere.add(position);
                    }
                }
            }
        }
        return sphere;
    }

    public static void placeBlockWithSwitch(BlockPos pos, EnumHand hand, boolean rotate, boolean packet, int slot) {
        int currentItem = mc.player.inventory.currentItem;
        InventoryUtil.switchToSlot(slot);
        placeBlock(pos, hand, rotate, packet);
        mc.player.inventory.currentItem = currentItem;
        mc.playerController.updateController();
    }

    public static void placeBlockWithSwitch(BlockPos pos, EnumHand hand, boolean rotate, boolean packet, int slot, Timer timer) {
        int currentItem = mc.player.inventory.currentItem;
        InventoryUtil.switchToSlot(slot);
        placeBlock(pos, hand, rotate, packet);
        mc.player.inventory.currentItem = currentItem;
        mc.playerController.updateController();
        timer.setTime(0);
    }

    public static void placeBlock(BlockPos pos, EnumHand hand, boolean rotate, boolean packet) {
        EnumFacing side = getFirstFacing(pos);
        if (side == null) {
            return;
        }
        BlockPos neighbour = pos.offset(side);
        EnumFacing opposite = side.getOpposite();
        Vec3d hitVec = new Vec3d(neighbour).add(0.5, 0.5, 0.5).add(new Vec3d(opposite.getDirectionVec()).scale(0.5));
        Block neighbourBlock = mc.world.getBlockState(neighbour).getBlock();
        if (!mc.player.isSneaking() && (blackList.contains(neighbourBlock) || shulkerList.contains(neighbourBlock))) {
            mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_SNEAKING));
            mc.player.setSneaking(true);
        }
        if (rotate) {
            faceVector(hitVec, true);
        }
        rightClickBlock(neighbour, hitVec, hand, opposite, packet);
        mc.player.swingArm(EnumHand.MAIN_HAND);
        mc.rightClickDelayTimer = 4;
    }

    public static void rightClickBlock(BlockPos pos, Vec3d vec, EnumHand hand, EnumFacing direction, boolean packet) {
        if (packet) {
            float f = (float) (vec.x - (double) pos.getX());
            float f1 = (float) (vec.y - (double) pos.getY());
            float f2 = (float) (vec.z - (double) pos.getZ());
            mc.player.connection.sendPacket(new CPacketPlayerTryUseItemOnBlock(pos, direction, hand, f, f1, f2));
        } else {
            mc.playerController.processRightClickBlock(mc.player, mc.world, pos, direction, vec, hand);
        }
        mc.player.swingArm(EnumHand.MAIN_HAND);
        mc.rightClickDelayTimer = 4;
    }

    public static EnumFacing getFirstFacing(BlockPos pos) {
        Iterator<EnumFacing> iterator = getPossibleSides(pos).iterator();
        if (iterator.hasNext()) {
            return iterator.next();
        }
        return null;
    }

    public static List<EnumFacing> getPossibleSides(BlockPos pos) {
        ArrayList<EnumFacing> facings = new ArrayList<>();
        for (EnumFacing side : EnumFacing.values()) {
            BlockPos neighbour = pos.offset(side);
            if (!mc.world.getBlockState(neighbour).getBlock().canCollideCheck(mc.world.getBlockState(neighbour), false) || mc.world.getBlockState(neighbour).getMaterial().isReplaceable())
                continue;
            facings.add(side);
        }
        return facings;
    }

    public static void faceVector(Vec3d vec, boolean normalizeAngle) {
        float[] rotations = getLegitRotations(vec);
        mc.player.connection.sendPacket(new CPacketPlayer.Rotation(rotations[0], normalizeAngle ? (float) MathHelper.normalizeAngle((int) rotations[1], 360) : rotations[1], mc.player.onGround));
    }

    public static float[] getLegitRotations(Vec3d vec) {
        Vec3d eyesPos = getEyesPos();
        double diffX = vec.x - eyesPos.x;
        double diffY = vec.y - eyesPos.y;
        double diffZ = vec.z - eyesPos.z;
        double diffXZ = Math.sqrt(diffX * diffX + diffZ * diffZ);
        float yaw = (float) Math.toDegrees(Math.atan2(diffZ, diffX)) - 90.0f;
        float pitch = (float) (-Math.toDegrees(Math.atan2(diffY, diffXZ)));
        return new float[]{mc.player.rotationYaw + MathHelper.wrapDegrees(yaw - mc.player.rotationYaw), mc.player.rotationPitch + MathHelper.wrapDegrees(pitch - mc.player.rotationPitch)};
    }

    public static Vec3d getEyesPos() {
        return new Vec3d(mc.player.posX, mc.player.posY + (double) mc.player.getEyeHeight(), mc.player.posZ);
    }

    public static BlockPos getPlayerPos() {
        return new BlockPos(Math.floor(mc.player.posX), Math.floor(mc.player.posY), Math.floor(mc.player.posZ));
    }

    public enum AirType {
        OnlyAir,
        IgnoreAir,
        None
    }

    public static class Hole {
        public BlockPos pos;
        public double range;

        public Hole(BlockPos pos, double range) {
            this.pos = pos;
            this.range = range;
        }
    }

}
