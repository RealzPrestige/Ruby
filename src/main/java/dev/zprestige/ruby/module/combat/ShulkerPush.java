package dev.zprestige.ruby.module.combat;

import dev.zprestige.ruby.Ruby;
import net.minecraft.client.Minecraft;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;

public class ShulkerPush {
    Minecraft mc = Ruby.mc;
    public Side getShulkerSide(BlockPos pos) {
        if (mc.world.getBlockState(pos.up().north()).getBlock().equals(Blocks.AIR)) {
            if (mc.world.getBlockState(pos.up().north().north()).getBlock().equals(Blocks.AIR)) {
                if (mc.world.getBlockState(pos.up().north().north().north()).getBlock().equals(Blocks.AIR) || mc.world.getBlockState(pos.up().north().north().north()).getBlock().equals(Blocks.OBSIDIAN)) {
                    return Side.North;
                }
            }
            if (mc.world.getBlockState(pos.up().north().north().east()).getBlock().equals(Blocks.AIR)) {
                if (mc.world.getBlockState(pos.up().north().north().north().east()).getBlock().equals(Blocks.AIR) || mc.world.getBlockState(pos.up().north().north().north().east()).getBlock().equals(Blocks.OBSIDIAN)) {
                    return Side.NorthEast;
                }
            }
            if (mc.world.getBlockState(pos.up().north().north().west()).getBlock().equals(Blocks.AIR)) {
                if (mc.world.getBlockState(pos.up().north().north().north().west()).getBlock().equals(Blocks.AIR) || mc.world.getBlockState(pos.up().north().north().north().west()).getBlock().equals(Blocks.OBSIDIAN)) {
                    return Side.NorthWest;
                }
            }
            if (mc.world.getBlockState(pos.up().north().north().up()).getBlock().equals(Blocks.AIR)) {
                if (mc.world.getBlockState(pos.up().north().north().north().up()).getBlock().equals(Blocks.AIR) || mc.world.getBlockState(pos.up().north().north().north().up()).getBlock().equals(Blocks.OBSIDIAN)) {
                    return Side.NorthUp;
                }
            }
            if (mc.world.getBlockState(pos.up().north().north().east().up()).getBlock().equals(Blocks.AIR)) {
                if (mc.world.getBlockState(pos.up().north().north().north().east().up()).getBlock().equals(Blocks.AIR) || mc.world.getBlockState(pos.up().north().north().north().east().up()).getBlock().equals(Blocks.OBSIDIAN)) {
                    return Side.NorthEastUp;
                }
            }
            if (mc.world.getBlockState(pos.up().north().north().west().up()).getBlock().equals(Blocks.AIR)) {
                if (mc.world.getBlockState(pos.up().north().north().north().west().up()).getBlock().equals(Blocks.AIR) || mc.world.getBlockState(pos.up().north().north().north().west().up()).getBlock().equals(Blocks.OBSIDIAN)) {
                    return Side.NorthWestUp;
                }
            }
        }
        if (mc.world.getBlockState(pos.up().east()).getBlock().equals(Blocks.AIR)) {
            if (mc.world.getBlockState(pos.up().east().east()).getBlock().equals(Blocks.AIR)) {
                if (mc.world.getBlockState(pos.up().east().east().east()).getBlock().equals(Blocks.AIR) || mc.world.getBlockState(pos.up().east().east().east()).getBlock().equals(Blocks.OBSIDIAN)) {
                    return Side.East;
                }
            }
            if (mc.world.getBlockState(pos.up().east().east().east()).getBlock().equals(Blocks.AIR)) {
                if (mc.world.getBlockState(pos.up().east().east().east().north()).getBlock().equals(Blocks.AIR) || mc.world.getBlockState(pos.up().east().east().east().north()).getBlock().equals(Blocks.OBSIDIAN)) {
                    return Side.EastNorth;
                }
            }
            if (mc.world.getBlockState(pos.up().east().east().west()).getBlock().equals(Blocks.AIR)) {
                if (mc.world.getBlockState(pos.up().east().east().east().south()).getBlock().equals(Blocks.AIR) || mc.world.getBlockState(pos.up().east().east().east().south()).getBlock().equals(Blocks.OBSIDIAN)) {
                    return Side.EastSouth;
                }
            }
            if (mc.world.getBlockState(pos.up().east().east().up()).getBlock().equals(Blocks.AIR)) {
                if (mc.world.getBlockState(pos.up().east().east().east().up()).getBlock().equals(Blocks.AIR) || mc.world.getBlockState(pos.up().east().east().east().up()).getBlock().equals(Blocks.OBSIDIAN)) {
                    return Side.EastUp;
                }
            }
            if (mc.world.getBlockState(pos.up().east().east().east().up()).getBlock().equals(Blocks.AIR)) {
                if (mc.world.getBlockState(pos.up().east().east().east().north()).getBlock().equals(Blocks.AIR) || mc.world.getBlockState(pos.up().east().east().east().north().up()).getBlock().equals(Blocks.OBSIDIAN)) {
                    return Side.EastNorthUp;
                }
            }
            if (mc.world.getBlockState(pos.up().east().east().west().up()).getBlock().equals(Blocks.AIR)) {
                if (mc.world.getBlockState(pos.up().east().east().east().south().up()).getBlock().equals(Blocks.AIR) || mc.world.getBlockState(pos.up().east().east().east().south().up()).getBlock().equals(Blocks.OBSIDIAN)) {
                    return Side.EastSouthUp;
                }
            }
        }

        return null;
    }

    public enum Side {
        North,
        NorthEast,
        NorthWest,
        NorthUp,
        NorthEastUp,
        NorthWestUp,
        NorthUpInBlock,
        East,
        EastNorth,
        EastSouth,
        EastUp,
        EastNorthUp,
        EastSouthUp,
        EastUpInBlock,
        South,
        SouthEast,
        SouthWest,
        SouthUp,
        SouthEastUp,
        SouthWestUp,
        SouthUpInBlock,
        West,
        WestNorth,
        WestSouth,
        WestUp,
        WestNorthUp,
        WestSouthUp,
        WestUpInBlock
    }
}
