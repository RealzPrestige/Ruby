package dev.zprestige.ruby.events;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.eventhandler.Event;

public class LogoutEvent extends Event {
    public EntityPlayer entityPlayer;
    public BlockPos pos;
    public int entityId;
    public long currentTimeMillis;

    public LogoutEvent(EntityPlayer entityPlayer, BlockPos pos, long currentTimeMillis, int entityId) {
        this.entityPlayer = entityPlayer;
        this.pos = pos;
        this.currentTimeMillis = currentTimeMillis;
        this.entityId = entityId;
    }

    public static class LoginEvent extends Event {
        public EntityPlayer entityPlayer;

        public LoginEvent(EntityPlayer entityPlayer) {
            this.entityPlayer = entityPlayer;
        }
    }
}
