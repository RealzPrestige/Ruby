package dev.zprestige.ruby.module.misc;

import dev.zprestige.ruby.eventbus.annotation.RegisterListener;
import dev.zprestige.ruby.events.LogoutEvent;
import dev.zprestige.ruby.events.Render3DEvent;
import dev.zprestige.ruby.events.SelfLogoutEvent;
import dev.zprestige.ruby.module.Category;
import dev.zprestige.ruby.module.Module;
import dev.zprestige.ruby.module.ModuleInfo;
import dev.zprestige.ruby.util.RenderUtil;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Objects;

@ModuleInfo(name = "LogoutSpots", category = Category.Misc, description = "shows where shitters log to not die ezz")
public class LogoutSpots extends Module {
    public ArrayList<LoggedKid> loggedKids = new ArrayList<>();
    public ICamera camera = new Frustum();

    @Override
    public void onEnable() {
        loggedKids.clear();
    }

    @RegisterListener
    public void onSelfLogout(SelfLogoutEvent event) {
        if (nullCheck() || !isEnabled())
            return;
        loggedKids.clear();
    }

    @Override
    public void onGlobalRenderTick(Render3DEvent event) {
        camera.setPosition(Objects.requireNonNull(mc.getRenderViewEntity()).posX, mc.getRenderViewEntity().posY, mc.getRenderViewEntity().posZ);
        ArrayList<LoggedKid> loggedKids1 = new ArrayList<>(loggedKids);
        for (LoggedKid loggedKid : loggedKids1) {
            if (camera.isBoundingBoxInFrustum(loggedKid.entityPlayer.getEntityBoundingBox().grow(2))) {
                mc.getRenderManager().renderEntityStatic(loggedKid.entityPlayer, event.partialTicks, false);
                RenderUtil.drawText(loggedKid.entityPlayer.getPosition().up().up(), loggedKid.entityPlayer.getName() + " " + ((-loggedKid.currentTimeMillis + System.currentTimeMillis()) / 1000) + "s " + (roundNumber(mc.player.getDistanceSq(loggedKid.entityPlayer.getPosition()) / 2, 0)) + "m");
            }
        }
    }

    @RegisterListener
    public void onEntityLogout(LogoutEvent event) {
        if (nullCheck() || !isEnabled())
            return;
        loggedKids.add(new LoggedKid(event.entityPlayer, event.pos, event.currentTimeMillis, event.entityId));
    }

    @RegisterListener
    public void onLoginEvent(LogoutEvent.LoginEvent event) {
        if (nullCheck() || !isEnabled())
            return;
        ArrayList<LoggedKid> currentLoggedKids = new ArrayList<>(loggedKids);
        currentLoggedKids.stream().filter(loggedKid -> loggedKid.entityPlayer.equals(event.entityPlayer)).forEach(loggedKid -> loggedKids.remove(loggedKid));
    }

    public static class LoggedKid {
        public EntityPlayer entityPlayer;
        public BlockPos pos;
        public int entityId;
        public long currentTimeMillis;

        public LoggedKid(EntityPlayer entityPlayer, BlockPos pos, long currentTimeMillis, int entityId) {
            this.entityPlayer = entityPlayer;
            this.pos = pos;
            this.currentTimeMillis = currentTimeMillis;
            this.entityId = entityId;
        }
    }

    public static float roundNumber(double value, int places) {
        if (places < 0)
            throw new IllegalArgumentException();
        BigDecimal decimal = BigDecimal.valueOf(value);
        decimal = decimal.setScale(places, RoundingMode.FLOOR);
        return decimal.floatValue();
    }
}
