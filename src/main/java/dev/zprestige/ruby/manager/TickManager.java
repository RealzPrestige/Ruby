package dev.zprestige.ruby.manager;

import dev.zprestige.ruby.Ruby;
import dev.zprestige.ruby.events.PacketEvent;
import dev.zprestige.ruby.module.client.Hud;
import net.minecraft.network.play.server.SPacketTimeUpdate;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class TickManager {

    public long prevTime;
    public final float[] TPS = new float[20];
    public int currentTick;

    public TickManager() {
        prevTime = -1;
        for (int i = 0, len = TPS.length; i < len; i++)
            TPS[i] = 0;
        Ruby.eventBus.register(this);
    }

    public float getTPS() {
        int tickCount = 0;
        float tickRate = 0;
        for (float tick : TPS)
            if (tick > 0) {
                tickRate += tick;
                tickCount++;
            }

        return Ruby.mc.isSingleplayer() ? 20 : Hud.roundNumber(MathHelper.clamp((tickRate / tickCount), 0, 20), 2);
    }

    @SubscribeEvent
    public void onPacketReceive(PacketEvent.PacketReceiveEvent event) {
        if (event.getPacket() instanceof SPacketTimeUpdate) {
            if (prevTime != -1) {
                TPS[currentTick % TPS.length] = MathHelper.clamp((20 / ((float) (System.currentTimeMillis() - prevTime) / 1000)), 0, 20);
                currentTick++;
            }

            prevTime = System.currentTimeMillis();
        }
    }

}
