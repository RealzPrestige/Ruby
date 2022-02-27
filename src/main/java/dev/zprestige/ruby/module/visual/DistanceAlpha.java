package dev.zprestige.ruby.module.visual;

import dev.zprestige.ruby.eventbus.annotation.RegisterListener;
import dev.zprestige.ruby.events.RenderLivingEntityEvent;
import dev.zprestige.ruby.module.Module;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.entity.player.EntityPlayer;
import org.lwjgl.opengl.GL11;

import java.util.Objects;

public class DistanceAlpha extends Module {
    public Frustum camera = new Frustum();

    @Override
    public void onFrame(float partialTicks) {
        camera.setPosition(Objects.requireNonNull(mc.getRenderViewEntity()).posX, mc.getRenderViewEntity().posY, mc.getRenderViewEntity().posZ);
    }

    @RegisterListener
    public void onRenderLivingEntity(RenderLivingEntityEvent event) {
        if (nullCheck() || !isEnabled() || event.getEntityLivingBase() == null || !(event.getEntityLivingBase() instanceof EntityPlayer) || mc.player.equals(event.getEntityLivingBase()) || !camera.isBoundingBoxInFrustum(event.getEntityLivingBase().getEntityBoundingBox().grow(2)))
            return;
        GL11.glColor4f(1.0f, 1.0f, 1.0f, Math.max(Math.min(mc.player.getDistance(event.getEntityLivingBase()) / 5, 1.0f), 0.2f));
    }
}