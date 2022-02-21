package dev.zprestige.ruby.events;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;

@Cancelable
public class BlockInteractEvent extends Event {
    public BlockPos pos;
    public EnumFacing facing;

    public BlockInteractEvent(BlockPos pos, EnumFacing facing) {
        this.pos = pos;
        this.facing = facing;
    }

    @Cancelable
    public static class ClickBlock extends BlockInteractEvent {
        public ClickBlock(BlockPos pos, EnumFacing facing) {
            super(pos, facing);
        }
    }

    @Cancelable
    public static class DamageBlock extends BlockInteractEvent {
        public DamageBlock(BlockPos pos, EnumFacing facing) {
            super(pos, facing);
        }
    }
}