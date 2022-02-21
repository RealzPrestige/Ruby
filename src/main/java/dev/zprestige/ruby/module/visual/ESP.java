package dev.zprestige.ruby.module.visual;

import dev.zprestige.ruby.Ruby;
import dev.zprestige.ruby.events.RenderLivingEntityEvent;
import dev.zprestige.ruby.module.Category;
import dev.zprestige.ruby.module.Module;
import dev.zprestige.ruby.module.ModuleInfo;
import dev.zprestige.ruby.setting.impl.*;
import dev.zprestige.ruby.util.RenderUtil;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.EXTFramebufferObject;
import org.lwjgl.opengl.EXTPackedDepthStencil;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

import static org.lwjgl.opengl.GL11.*;

@ModuleInfo(name = "ESP", category = Category.Visual, description = "renders stuff for things ")
public class ESP extends Module {
    public static ESP Instance;
    public ParentSetting hole = createSetting("Hole");
    public BooleanSetting holes = createSetting("Holes", false).setParent(hole);
    public BooleanSetting distanceFade = createSetting("Distance Fade", false, v -> holes.getValue()).setParent(hole);
    public FloatSetting distanceDivision = createSetting("Distance Division", 20.0f, 0.1f, 500.0f, (Predicate<Float>) v -> distanceFade.getValue()).setParent(hole);
    public FloatSetting holeHeight = createSetting("Hole Height", 1.0f, 0.0f, 2.0f, (Predicate<Float>) v -> holes.getValue()).setParent(hole);
    public IntegerSetting holeRadius = createSetting("Radius", 10, 1, 50, (Predicate<Integer>) v -> holes.getValue()).setParent(hole);
    public BooleanSetting bedrockBox = createSetting("Bedrock Box", false, v -> holes.getValue()).setParent(hole);
    public ColorSetting bedrockBoxColor = createSetting("Bedrock Box Color", new Color(0, 255, 0), v -> holes.getValue() && bedrockBox.getValue()).setParent(hole);
    public BooleanSetting bedrockOutline = createSetting("Bedrock Outline", false, v -> holes.getValue()).setParent(hole);
    public ColorSetting bedrockOutlineColor = createSetting("Bedrock Outline Color", new Color(0, 255, 0), v -> holes.getValue() && bedrockOutline.getValue()).setParent(hole);
    public FloatSetting bedrockOutlineWidth = createSetting("Bedrock Outline Width", 1.0f, 0.1f, 5.0f, (Predicate<Float>) v -> holes.getValue() && bedrockOutline.getValue()).setParent(hole);
    public BooleanSetting obsidianBox = createSetting("Obsidian Box", false, v -> holes.getValue()).setParent(hole);
    public ColorSetting obsidianBoxColor = createSetting("Obsidian Box Color", new Color(255, 0, 0), v -> holes.getValue() && obsidianBox.getValue()).setParent(hole);
    public BooleanSetting obsidianOutline = createSetting("Obsidian Outline", false, v -> holes.getValue()).setParent(hole);
    public ColorSetting obsidianOutlineColor = createSetting("Obsidian Outline Color", new Color(255, 0, 0), v -> holes.getValue() && obsidianOutline.getValue()).setParent(hole);
    public FloatSetting obsidianOutlineWidth = createSetting("Obsidian Outline Width", 1.0f, 0.1f, 5.0f, (Predicate<Float>) v -> holes.getValue() && obsidianOutline.getValue()).setParent(hole);

    public ParentSetting items = createSetting("Items");
    public BooleanSetting itemNames = createSetting("Item Names", false).setParent(items);

    public ParentSetting player = createSetting("Player");
    public BooleanSetting players = createSetting("Players", false).setParent(player);
    public BooleanSetting playerMoveCancel = createSetting("Move Cancel", false, v -> players.getValue()).setParent(player);
    public ColorSetting playerColor = createSetting("Player Color", new Color(-1), v -> players.getValue()).setParent(player);
    public FloatSetting playerLineWidth = createSetting("Player Line Width", 1.0f, 0.1f, 5.0f, (Predicate<Float>) v -> players.getValue()).setParent(player);

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

    @Override
    public void onThreadReset() {
        thread3.stop();
        thread3 = new Thread(() -> {
            while (true) {
                try {
                    playerList = mc.world.playerEntities;
                } catch (Exception ignored) {
                }
            }
        });
    }

    @Override
    public void onGlobalRenderTick() {
        camera.setPosition(Objects.requireNonNull(mc.getRenderViewEntity()).posX, mc.getRenderViewEntity().posY, mc.getRenderViewEntity().posZ);
        if (players.getValue() && playerMoveCancel.getValue()) {
            mc.world.playerEntities.stream().filter(entityPlayer -> !entityPlayer.equals(mc.player)).forEach(entityPlayer -> {
                entityPlayer.limbSwing = 0;
                entityPlayer.limbSwingAmount = 0;
                entityPlayer.prevLimbSwingAmount = 0;
                entityPlayer.rotationYawHead = 0;
                entityPlayer.rotationPitch = 0;
                entityPlayer.rotationYaw = 0;
            });
        }
        if (itemNames.getValue()) {
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
        if (!holes.getValue())
            return;
        ArrayList<BlockPos> bedrockHoles = Ruby.holeManager.getBedrockHoles(holeRadius.getValue());
        if (bedrockHoles != null) {
            ArrayList<BlockPos> bedrockHoles2 = new ArrayList<>(bedrockHoles);
            for (BlockPos pos : bedrockHoles2) {
                AxisAlignedBB bb = new AxisAlignedBB(pos);
                if (!camera.isBoundingBoxInFrustum(bb.grow(2.0)))
                    continue;
                int alpha = (int) Math.min(bedrockBoxColor.getValue().getAlpha() / (Math.max(1.0, mc.player.getDistanceSq(pos) / distanceDivision.getValue())), bedrockBoxColor.getValue().getAlpha());
                if (bedrockBox.getValue()) {
                    RenderUtil.drawBBBoxWithHeight(bb, bedrockBoxColor.getValue(), distanceFade.getValue() ? alpha : bedrockBoxColor.getValue().getAlpha(), holeHeight.getValue());
                }
                if (bedrockOutline.getValue()) {
                    RenderUtil.drawBlockOutlineBBWithHeight(bb, distanceFade.getValue() ? new Color(bedrockOutlineColor.getValue().getRed() / 255.0f, bedrockOutlineColor.getValue().getGreen() / 255.0f, bedrockOutlineColor.getValue().getBlue() / 255.0f, alpha / 255.0f) : bedrockOutlineColor.getValue(), bedrockOutlineWidth.getValue(), holeHeight.getValue());
                }
            }
        }
        ArrayList<BlockPos> obsidianHoles = Ruby.holeManager.getObsidianHoles(holeRadius.getValue());
        if (obsidianHoles != null) {
            ArrayList<BlockPos> obsidianHoles2 = new ArrayList<>(obsidianHoles);
            for (BlockPos pos : obsidianHoles2) {
                AxisAlignedBB bb = new AxisAlignedBB(pos);
                if (!camera.isBoundingBoxInFrustum(bb.grow(2.0)))
                    continue;
                int alpha = (int) Math.min(obsidianBoxColor.getValue().getAlpha() / (Math.max(1.0, mc.player.getDistanceSq(pos) / distanceDivision.getValue())), obsidianBoxColor.getValue().getAlpha());
                if (bedrockBox.getValue()) {
                    RenderUtil.drawBBBoxWithHeight(bb, obsidianBoxColor.getValue(), distanceFade.getValue() ? alpha : obsidianBoxColor.getValue().getAlpha(), holeHeight.getValue());
                }
                if (bedrockOutline.getValue()) {
                    RenderUtil.drawBlockOutlineBBWithHeight(bb, distanceFade.getValue() ? new Color(obsidianOutlineColor.getValue().getRed() / 255.0f, obsidianOutlineColor.getValue().getGreen() / 255.0f, obsidianOutlineColor.getValue().getBlue() / 255.0f, alpha / 255.0f) : obsidianOutlineColor.getValue(), obsidianOutlineWidth.getValue(), holeHeight.getValue());
                }
            }
        }
    }

    @SubscribeEvent
    public void onRenderLivingEntity(RenderLivingEntityEvent event) {
        if (nullCheck() || !isEnabled() || !(event.getEntityLivingBase() instanceof EntityPlayer) || !players.getValue() || !camera.isBoundingBoxInFrustum(event.getEntityLivingBase().getEntityBoundingBox().grow(2)))
            return;
        if (!thread3.isAlive() || thread3.isInterrupted())
            thread3.start();
        event.getEntityLivingBase().hurtTime = 0;
        event.getModelBase().render(event.getEntityLivingBase(), event.getLimbSwing(), event.getLimbSwingAmount(), event.getAgeInTicks(), event.getNetHeadYaw(), event.getHeadPitch(), event.getScaleFactor());
        renderOne(playerLineWidth.getValue());
        event.getModelBase().render(event.getEntityLivingBase(), event.getLimbSwing(), event.getLimbSwingAmount(), event.getAgeInTicks(), event.getNetHeadYaw(), event.getHeadPitch(), event.getScaleFactor());
        renderTwo();
        event.getModelBase().render(event.getEntityLivingBase(), event.getLimbSwing(), event.getLimbSwingAmount(), event.getAgeInTicks(), event.getNetHeadYaw(), event.getHeadPitch(), event.getScaleFactor());
        renderThree();
        renderFour();
        setColor(playerColor.getValue());
        event.getModelBase().render(event.getEntityLivingBase(), event.getLimbSwing(), event.getLimbSwingAmount(), event.getAgeInTicks(), event.getNetHeadYaw(), event.getHeadPitch(), event.getScaleFactor());
        renderFive();
        setColor(Color.WHITE);
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
}
