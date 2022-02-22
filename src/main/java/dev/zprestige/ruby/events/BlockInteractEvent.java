package dev.zprestige.ruby.events;

import dev.zprestige.ruby.eventbus.event.Event;
import dev.zprestige.ruby.eventbus.event.IsCancellable;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;

@IsCancellable
public class BlockInteractEvent extends Event {
    public BlockPos pos;
    public EnumFacing facing;

    public BlockInteractEvent(BlockPos pos, EnumFacing facing) {
        this.pos = pos;
        this.facing = facing;
    }

    @IsCancellable
    public static class ClickBlock extends BlockInteractEvent {
        public ClickBlock(BlockPos pos, EnumFacing facing) {
            super(pos, facing);
        }
    }

    @IsCancellable
    public static class DamageBlock extends BlockInteractEvent {
        public DamageBlock(BlockPos pos, EnumFacing facing) {
            super(pos, facing);
        }
    }
}