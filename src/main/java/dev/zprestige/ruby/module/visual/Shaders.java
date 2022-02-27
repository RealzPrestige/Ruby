package dev.zprestige.ruby.module.visual;

import dev.zprestige.ruby.eventbus.annotation.RegisterListener;
import dev.zprestige.ruby.events.Render3DEvent;
import dev.zprestige.ruby.events.RenderItemInFirstPersonEvent;
import dev.zprestige.ruby.mixins.render.IEntityRenderer;
import dev.zprestige.ruby.module.Module;
import dev.zprestige.ruby.settings.impl.ColorBox;
import dev.zprestige.ruby.settings.impl.Parent;
import dev.zprestige.ruby.settings.impl.Slider;
import dev.zprestige.ruby.settings.impl.Switch;
import dev.zprestige.ruby.util.shader.ItemShader;
import net.minecraft.client.gui.GuiDownloadTerrain;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.item.EntityExpBottle;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.opengl.Display;

public class Shaders extends Module {
    public final Parent targets = Menu.Parent("Targets");
    public final Switch players = Menu.Switch("Players").parent(targets);
    public final Switch crystals = Menu.Switch("Crystals").parent(targets);
    public final Switch experienceBottles = Menu.Switch("Experience Bottles").parent(targets);
    public final Switch items = Menu.Switch("Items").parent(targets);

    public final Parent shader = Menu.Parent("Shader");
    public final ColorBox color = Menu.Color("Color").parent(shader);
    public final Slider radius = Menu.Slider("Radius", 0.1f, 10.0f).parent(shader);
    public final Slider opacity = Menu.Slider("Opacity", 0.0f, 255.0f).parent(shader);
    public boolean forceRender = false;

    @RegisterListener
    public void renderItemInFirstPerson(RenderItemInFirstPersonEvent event) {
        if (nullCheck() || !isEnabled() || !event.isPre || forceRender || !items.GetSwitch())
            return;
        event.setCancelled(true);
    }

    @Override
    public void onFrame(float partialTicks) {
        if (nullCheck())
            return;
        if (((Display.isActive() || Display.isVisible()) && mc.gameSettings.thirdPersonView == 0) && !(mc.currentScreen instanceof GuiDownloadTerrain)) {
            GlStateManager.pushMatrix();
            GlStateManager.pushAttrib();
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
            GlStateManager.enableDepth();
            GlStateManager.depthMask(true);
            GlStateManager.enableAlpha();
            ItemShader shader = ItemShader.Instance;
            shader.mix = opacity.GetSlider() / 255.0f;
            shader.alpha = color.GetColor().getAlpha() / 255.0f;
            shader.startDraw(mc.getRenderPartialTicks());
            forceRender = true;
            mc.world.loadedEntityList.stream().filter(entity -> entity != null && ((entity != mc.player || entity != mc.getRenderViewEntity()) && mc.getRenderManager().getEntityRenderObject(entity) != null) && (entity instanceof EntityPlayer && players.GetSwitch() && !((EntityPlayer) entity).isSpectator() || entity instanceof EntityEnderCrystal && crystals.GetSwitch() || entity instanceof EntityExpBottle && experienceBottles.GetSwitch())).forEach(entity -> {
                Vec3d vector = getInterpolatedRenderPos(entity, partialTicks);
                if (entity instanceof EntityPlayer)
                    ((EntityPlayer) entity).hurtTime = 0;
                Render<Entity> render = mc.getRenderManager().getEntityRenderObject(entity);
                if (render != null) {
                    try {
                        render.doRender(entity, vector.x, vector.y, vector.z, entity.rotationYaw, partialTicks);
                    } catch (Exception ignored) {
                    }
                }
            });
            if (items.GetSwitch())
                ((IEntityRenderer) mc.entityRenderer).invokeRenderHand(mc.getRenderPartialTicks(), 2);
            forceRender = false;
            shader.stopDraw(color.GetColor(), radius.GetSlider(), 1.0f);
            GlStateManager.disableBlend();
            GlStateManager.disableAlpha();
            GlStateManager.disableDepth();
            GlStateManager.popAttrib();
            GlStateManager.popMatrix();
        }
    }

    public Vec3d getInterpolatedRenderPos(Entity entity, float ticks) {
        return interpolateEntity(entity, ticks).subtract(mc.getRenderManager().renderPosX, mc.getRenderManager().renderPosY, mc.getRenderManager().renderPosZ);
    }

    public Vec3d interpolateEntity(Entity entity, float time) {
        return new Vec3d(entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * time, entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * time, entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * time);
    }
}
