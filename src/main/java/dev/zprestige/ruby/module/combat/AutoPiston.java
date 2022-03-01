package dev.zprestige.ruby.module.combat;

import dev.zprestige.ruby.module.Module;
import dev.zprestige.ruby.settings.impl.Slider;
import dev.zprestige.ruby.settings.impl.Switch;
import dev.zprestige.ruby.util.EntityUtil;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.TreeMap;
import java.util.stream.Collectors;

public class AutoPiston extends Module {
    public final Slider placeRange = Menu.Slider("Place Range", 0.0f, 6.0f);
    public final Slider placeWallRange = Menu.Slider("Place Wall Range", 0.0f, 6.0f);
    public final Switch onePointThirteen = Menu.Switch("One Point Thirteen");

    protected final Position[] pistonPosses = new Position[]{
            new Position(1, 1, 2, 3, true),
            new Position(1, -1, -2, -3, true),
            new Position(1, 1, 2, 3, false),
            new Position(1, -1, -2, -3, false),
            new Position(2, 1, 2, 3, true),
            new Position(2, -1, -2, -3, true),
            new Position(2, 1, 2, 3, false),
            new Position(2, -1, -2, -3, false),
    };

    protected final Vec3i[] surroundingBlocks = new Vec3i[]{
            new Vec3i(0, 0, 1),
            new Vec3i(0, 0, -1),
            new Vec3i(1, 0, 0),
            new Vec3i(-1, 0, 0),
            new Vec3i(0, 1, 0),
            new Vec3i(0, -1, 0),
    };

    protected Dir getDirByPosition(Position position){
        return position.isX() ? position.getPistonPos() < 0 ? Dir.West : Dir.East : position.getPistonPos() < 0 ? Dir.North : Dir.South;
    }
    protected Position findClosestPistonPos(EntityPlayer entityPlayer, Position[] posses) {
        final BlockPos pos1 = EntityUtil.getPlayerPos(entityPlayer);
        final ArrayList<Position> possesArrayList = new ArrayList<>();
        for (Position position : posses){
            final BlockPos piston = position.isX() ? pos1.add(position.getPistonPos(), position.getY(), 0) : pos1.add(0, position.getY(), (position.getPistonPos()));
            final BlockPos redstone = position.isX() ? pos1.add(position.getRedstonePos(), position.getY(), 0) : pos1.add(0, position.getY(), (position.getRedstonePos()));
            final BlockPos crystal = position.isX() ? pos1.add(position.getCrystalPos(), position.getY() - 1, 0) : pos1.add(0, position.getY() - 1, (position.getCrystalPos()));
            if (inRange(piston) && inRange(redstone) && inRange(crystal) && ((air(piston) && isSurrounded(piston)) || piston(piston)) && ((air(redstone) && isSurrounded(redstone)) || redstone(redstone)) && canPlaceCrystal(crystal)) {
                possesArrayList.add(position);
            }
        }
        if (!possesArrayList.isEmpty()){
            final TreeMap<Double, Position> positionTreeMap = possesArrayList.stream().collect(Collectors.toMap(position -> mc.player.getDistanceSq(position.isX() ? pos1.add(position.getRedstonePos(), position.getY(), 0) : pos1.add(0, position.getY(), (position.getRedstonePos()))), position -> position, (a, b) -> b, TreeMap::new));
            return positionTreeMap.firstEntry().getValue();
        }
        return null;
    }

    protected Stage stage(Dir dir, EntityPlayer entityPlayer, BlockPos pos) {
        final AxisAlignedBB bb = new AxisAlignedBB(pos);
        final BlockPos pos1 = EntityUtil.getPlayerPos(entityPlayer).up();
        final AxisAlignedBB bb1 = new AxisAlignedBB(pos1);
        final BlockPos pos2 = pos.offset(dir.getEnumFacing(), dir.getI());
        if (!piston(pos)) {
            if (Arrays.stream(surroundingBlocks).map(pos2::add).anyMatch(this::redstone)) {
                return Stage.BreakRedstone;
            }
            return Stage.Piston;
        }
        if (emptyExcludingEntityPlayer(entityPlayer, bb1) && emptyExcludingEntityPlayer(entityPlayer, bb)) {
            return Stage.Crystal;
        }
        final BlockPos pos3 = pos.offset(dir.getEnumFacing(), dir.getI());
        if (isntSurroundedByBlock(pos3, Blocks.REDSTONE_BLOCK) && isntSurroundedByBlock(pos3, Blocks.REDSTONE_TORCH)) {
            return Stage.Redstone;
        }
        if (!emptyExcludingEntityPlayer(entityPlayer, bb1)) {
            return Stage.CrystalBreak;
        }
        return null;
    }

    protected boolean inRange(BlockPos pos){
        return (mc.player.getDistanceSq(pos) / 2f) <( cantSeePos(pos) ? placeWallRange.GetSlider() : placeRange.GetSlider());
    }

    protected boolean cantSeePos(BlockPos pos) {
        return mc.world.rayTraceBlocks(new Vec3d(mc.player.posX, mc.player.posY + mc.player.getEyeHeight(), mc.player.posZ), new Vec3d(pos.getX(), pos.getY(), pos.getZ()), false, true, false) != null;
    }

    protected boolean canPlaceCrystal(BlockPos pos){
        return (mc.world.getBlockState(pos).getBlock().equals(Blocks.OBSIDIAN) || mc.world.getBlockState(pos).getBlock().equals(Blocks.BEDROCK)) && air(pos.up()) && (onePointThirteen.GetSwitch() || air(pos.up().up()));
    }

    protected boolean isntSurroundedByBlock(BlockPos pos, Block block) {
        return Arrays.stream(surroundingBlocks).map(pos::add).allMatch(pos1 -> mc.world.getBlockState(pos1).getBlock().equals(block));
    }

    protected boolean isSurrounded(BlockPos pos) {
        return Arrays.stream(surroundingBlocks).map(pos::add).anyMatch(pos1 -> !mc.world.getBlockState(pos1).getBlock().equals(Blocks.AIR));
    }

    protected boolean emptyExcludingEntityPlayer(EntityPlayer entityPlayer, AxisAlignedBB bb) {
        return mc.world.getEntitiesWithinAABBExcludingEntity(entityPlayer, bb).isEmpty();
    }

    protected boolean piston(BlockPos pos) {
        return mc.world.getBlockState(pos).getBlock().equals(Blocks.PISTON);
    }

    protected boolean air(BlockPos pos) {
        return mc.world.getBlockState(pos).getBlock().equals(Blocks.AIR);
    }

    protected boolean redstone(BlockPos pos) {
        return mc.world.getBlockState(pos).getBlock().equals(Blocks.REDSTONE_BLOCK) || mc.world.getBlockState(pos).getBlock().equals(Blocks.REDSTONE_TORCH);
    }

    protected enum Dir {
        North(EnumFacing.NORTH, -1),
        East(EnumFacing.EAST, 1),
        South(EnumFacing.SOUTH, 1),
        West(EnumFacing.WEST, -1);

        private final EnumFacing enumFacing;
        private final int i;

        Dir(EnumFacing enumFacing, int i) {
            this.enumFacing = enumFacing;
            this.i = i;
        }

        public EnumFacing getEnumFacing() {
            return enumFacing;
        }

        public int getI() {
            return i;
        }
    }

    protected enum Stage {
        Piston,
        Crystal,
        Redstone,
        CrystalBreak,
        BreakRedstone
    }

    protected static class Position {
        private final int y, crystalPos, pistonPos, redstonePos;
        private final boolean x;

        public Position(int y, int crystalPos, int pistonPos, int redstonePos, boolean x) {
            this.y = y;
            this.crystalPos = crystalPos;
            this.pistonPos = pistonPos;
            this.redstonePos = redstonePos;
            this.x = x;
        }

        public int getY() {
            return y;
        }

        public int getCrystalPos() {
            return crystalPos;
        }

        public int getPistonPos() {
            return pistonPos;
        }

        public int getRedstonePos() {
            return redstonePos;
        }

        public boolean isX() {
            return x;
        }
    }
}
