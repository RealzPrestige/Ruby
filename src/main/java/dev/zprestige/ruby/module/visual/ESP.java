package dev.zprestige.ruby.module.visual;

import dev.zprestige.ruby.Ruby;
import dev.zprestige.ruby.eventbus.annotation.RegisterListener;
import dev.zprestige.ruby.events.RenderLivingEntityEvent;
import dev.zprestige.ruby.module.Module;
import dev.zprestige.ruby.settings.impl.ColorBox;
import dev.zprestige.ruby.settings.impl.Parent;
import dev.zprestige.ruby.settings.impl.Slider;
import dev.zprestige.ruby.settings.impl.Switch;
import dev.zprestige.ruby.util.RenderUtil;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.opengl.EXTFramebufferObject;
import org.lwjgl.opengl.EXTPackedDepthStencil;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.lwjgl.opengl.GL11.*;

public class ESP extends Module {
    public static ESP Instance;

    public final Parent items = Menu.Parent("Items");
    public final Switch itemNames = Menu.Switch("Item Names").parent(items);

    public final Parent player = Menu.Parent("Player");
    public final Switch players = Menu.Switch("Players").parent(player);
    public final Switch playerMoveCancel = Menu.Switch("Move Cancel").parent(player);
    public final ColorBox playerColor = Menu.Color("Player Color").parent(player);
    public final Slider playerLineWidth = Menu.Slider("Player Line Width", 0.1f, 5.0f).parent(player);

    public ArrayList<Entity> entityList = new ArrayList<>();
    public List<EntityPlayer> playerList = new ArrayList<>();
    public ArrayList<BlockPos> obsidianHoles = new ArrayList<>();
    public ArrayList<BlockPos> bedrockHoles = new ArrayList<>();
    public ICamera camera = new Frustum();
    public Thread thread3 = new Thread(() -> {
        while (true) {
            if (nullCheck() || !isEnabled())
                continue;
            try {
                playerList = mc.world.playerEntities;
            } catch (Exception ignored) {
            }
        }
    });

    public ESP() {
        Instance = this;
    }

    public static void renderOne(float width) {
        checkSetupFBO();
        glPushAttrib(GL_ALL_ATTRIB_BITS);
        glDisable(GL_ALPHA_TEST);
        glDisable(GL_TEXTURE_2D);
        glDisable(GL_LIGHTING);
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glLineWidth(width);
        glEnable(GL_LINE_SMOOTH);
        glEnable(GL_STENCIL_TEST);
        glClear(GL_STENCIL_BUFFER_BIT);
        glClearStencil(0xF);
        glStencilFunc(GL_NEVER, 1, 0xF);
        glStencilOp(GL_REPLACE, GL_REPLACE, GL_REPLACE);
        glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);
    }

    public static void renderTwo() {
        glStencilFunc(GL_NEVER, 0, 0xF);
        glStencilOp(GL_REPLACE, GL_REPLACE, GL_REPLACE);
        glPolygonMode(GL_FRONT_AND_BACK, GL_FILL);
    }

    public static void renderThree() {
        glStencilFunc(GL_EQUAL, 1, 0xF);
        glStencilOp(GL_KEEP, GL_KEEP, GL_KEEP);
        glPolygonMode(GL_FRONT_AND_BACK, GL_LINE);
    }

    public static void renderFour() {
        setColor(new Color(255, 255, 255));
        glDepthMask(false);
        glDisable(GL_DEPTH_TEST);
        glEnable(GL_POLYGON_OFFSET_LINE);
        glPolygonOffset(1.0F, -2000000F);
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240.0F, 240.0F);
    }

    public static void renderFive() {
        glPolygonOffset(1.0F, 2000000F);
        glDisable(GL_POLYGON_OFFSET_LINE);
        glEnable(GL_DEPTH_TEST);
        glDepthMask(true);
        glDisable(GL_STENCIL_TEST);
        glDisable(GL_LINE_SMOOTH);
        glHint(GL_LINE_SMOOTH_HINT, GL_DONT_CARE);
        glEnable(GL_BLEND);
        glEnable(GL_LIGHTING);
        glEnable(GL_TEXTURE_2D);
        glEnable(GL_ALPHA_TEST);
        glPopAttrib();
    }

    public static void checkSetupFBO() {
        Framebuffer fbo = Ruby.mc.getFramebuffer();
        if (fbo.depthBuffer > -1) {
            setupFBO(fbo);
            fbo.depthBuffer = -1;
        }
    }

    public static void setupFBO(Framebuffer fbo) {
        EXTFramebufferObject.glDeleteRenderbuffersEXT(fbo.depthBuffer);
        int stencil_depth_buffer_ID = EXTFramebufferObject.glGenRenderbuffersEXT();
        EXTFramebufferObject.glBindRenderbufferEXT(EXTFramebufferObject.GL_RENDERBUFFER_EXT, stencil_depth_buffer_ID);
        EXTFramebufferObject.glRenderbufferStorageEXT(EXTFramebufferObject.GL_RENDERBUFFER_EXT, EXTPackedDepthStencil.GL_DEPTH_STENCIL_EXT, Ruby.mc.displayWidth, Ruby.mc.displayHeight);
        EXTFramebufferObject.glFramebufferRenderbufferEXT(EXTFramebufferObject.GL_FRAMEBUFFER_EXT, EXTFramebufferObject.GL_STENCIL_ATTACHMENT_EXT, EXTFramebufferObject.GL_RENDERBUFFER_EXT, stencil_depth_buffer_ID);
        EXTFramebufferObject.glFramebufferRenderbufferEXT(EXTFramebufferObject.GL_FRAMEBUFFER_EXT, EXTFramebufferObject.GL_DEPTH_ATTACHMENT_EXT, EXTFramebufferObject.GL_RENDERBUFFER_EXT, stencil_depth_buffer_ID);
    }

    public static void setColor(Color c) {
        glColor4d(c.getRed() / 255f, c.getGreen() / 255f, c.getBlue() / 255f, c.getAlpha() / 255f);
    }

    @Override
    public void onGlobalRenderTick() {
        Ruby.threadManager.run(() -> playerList = mc.world.playerEntities);
        camera.setPosition(Objects.requireNonNull(mc.getRenderViewEntity()).posX, mc.getRenderViewEntity().posY, mc.getRenderViewEntity().posZ);
        if (players.GetSwitch() && playerMoveCancel.GetSwitch()) {
            mc.world.playerEntities.stream().filter(entityPlayer -> !entityPlayer.equals(mc.player)).forEach(entityPlayer -> {
                entityPlayer.limbSwing = 0;
                entityPlayer.limbSwingAmount = 0;
                entityPlayer.prevLimbSwingAmount = 0;
                entityPlayer.rotationYawHead = 0;
                entityPlayer.rotationPitch = 0;
                entityPlayer.rotationYaw = 0;
            });
        }
        if (itemNames.GetSwitch()) {
            boolean fancyGraphics = mc.gameSettings.fancyGraphics;
            mc.gameSettings.fancyGraphics = false;
            float gammaSetting = mc.gameSettings.gammaSetting;
            mc.gameSettings.gammaSetting = 100.0f;
            entityList.clear();
            mc.world.loadedEntityList.stream().filter(Objects::nonNull).forEach(entity -> entityList.add(entity));
            entityList.stream().filter(entity -> entity instanceof EntityItem && !((EntityItem) entity).getItem().equals(mc.player.getHeldItemMainhand()) && camera.isBoundingBoxInFrustum(entity.getEntityBoundingBox().grow(2))).filter(entity -> mc.player.getDistanceSq(entity.getPosition()) < 1000 && !entity.isDead).forEach(entity -> {
                glPushMatrix();
                Vec3d i = RenderUtil.interpolateEntity(entity);
                RenderUtil.drawNametag(((EntityItem) entity).getItem().getDisplayName() + " x" + ((EntityItem) entity).getItem().getCount(), i.x, i.y, i.z, 0.005, -1);
                glColor4f(1f, 1f, 1f, 1f);
                glPopMatrix();
            });
            mc.gameSettings.gammaSetting = gammaSetting;
            mc.gameSettings.fancyGraphics = fancyGraphics;
        }
    }

    @RegisterListener
    public void onRenderLivingEntity(RenderLivingEntityEvent event) {
        if (nullCheck() || !isEnabled() || !(event.getEntityLivingBase() instanceof EntityPlayer) || !players.GetSwitch() || !camera.isBoundingBoxInFrustum(event.getEntityLivingBase().getEntityBoundingBox().grow(2)))
            return;
        if (!thread3.isAlive() || thread3.isInterrupted())
            thread3.start();
        event.getEntityLivingBase().hurtTime = 0;
        event.getModelBase().render(event.getEntityLivingBase(), event.getLimbSwing(), event.getLimbSwingAmount(), event.getAgeInTicks(), event.getNetHeadYaw(), event.getHeadPitch(), event.getScaleFactor());
        renderOne(playerLineWidth.GetSlider());
        event.getModelBase().render(event.getEntityLivingBase(), event.getLimbSwing(), event.getLimbSwingAmount(), event.getAgeInTicks(), event.getNetHeadYaw(), event.getHeadPitch(), event.getScaleFactor());
        renderTwo();
        event.getModelBase().render(event.getEntityLivingBase(), event.getLimbSwing(), event.getLimbSwingAmount(), event.getAgeInTicks(), event.getNetHeadYaw(), event.getHeadPitch(), event.getScaleFactor());
        renderThree();
        renderFour();
        setColor(playerColor.GetColor());
        event.getModelBase().render(event.getEntityLivingBase(), event.getLimbSwing(), event.getLimbSwingAmount(), event.getAgeInTicks(), event.getNetHeadYaw(), event.getHeadPitch(), event.getScaleFactor());
        renderFive();
        setColor(Color.WHITE);
    }
}
