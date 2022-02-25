package dev.zprestige.ruby.module.visual;

import dev.zprestige.ruby.module.Module;
import dev.zprestige.ruby.settings.impl.ColorBox;
import dev.zprestige.ruby.settings.impl.Slider;
import dev.zprestige.ruby.settings.impl.Switch;
import dev.zprestige.ruby.util.RenderUtil;
import dev.zprestige.ruby.util.Timer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderPearl;
import net.minecraft.entity.item.EntityExpBottle;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.util.List;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.lwjgl.opengl.GL11.glEnable;

public class EntityTrails extends Module {

    public final Switch self = Menu.Switch("Self");
    public final Slider lineWidth = Menu.Slider("LineWidth", 0.1f, 5.0f);
    public final Switch fade = Menu.Switch("Fade");
    public final Slider removeDelay = Menu.Slider("RemoveDelay", 0, 2000);
    public final ColorBox startColor = Menu.Color("StartColor");
    public final ColorBox endColor = Menu.Color("EndColor");

    public final Switch entities = Menu.Switch("Entities");
    public final Switch pearls = Menu.Switch("Pearls");
    public final Switch exp = Menu.Switch("Exp");
    public final ColorBox pearlColor = Menu.Color("EntityColor");
    public final Slider pearlLineWidth = Menu.Slider("PearlLineWidth", 0.0f, 10.0f);

    public HashMap<UUID, List<Vec3d>> pearlPos = new HashMap<>();
    public HashMap<UUID, Double> removeWait = new HashMap<>();
    public Map<UUID, ItemTrail> trails = new HashMap<>();

    @Override
    public void onTick() {
        if (entities.GetSwitch()) {
            UUID pearlPos = null;
            for (UUID uuid : removeWait.keySet())
                if (removeWait.get(uuid) <= 0) {
                    this.pearlPos.remove(uuid);
                    pearlPos = uuid;
                } else
                    removeWait.replace(uuid, removeWait.get(uuid) - 0.05);
            if (pearlPos != null)
                removeWait.remove(pearlPos);
            for (Entity entity : mc.world.getLoadedEntityList()) {
                if ((entity instanceof EntityEnderPearl && pearls.GetSwitch()) || (entity instanceof EntityExpBottle && exp.GetSwitch())) {
                    if (!this.pearlPos.containsKey(entity.getUniqueID())) {
                        this.pearlPos.put(entity.getUniqueID(), new ArrayList<>(Collections.singletonList(entity.getPositionVector())));
                        this.removeWait.put(entity.getUniqueID(), 0.1);
                    } else {
                        this.removeWait.replace(entity.getUniqueID(), 0.1);
                        List<Vec3d> v = this.pearlPos.get(entity.getUniqueID());
                        v.add(entity.getPositionVector());
                    }
                }
            }
        }
        if (self.GetSwitch()) {
            if (trails.containsKey(mc.player.getUniqueID())) {
                ItemTrail playerTrail = trails.get(mc.player.getUniqueID());
                playerTrail.timer.setTime(0);
                List<Position> toRemove = playerTrail.positions.stream().filter(position -> System.currentTimeMillis() - position.time > removeDelay.GetSlider()).collect(Collectors.toList());
                playerTrail.positions.removeAll(toRemove);
                playerTrail.positions.add(new Position(mc.player.getPositionVector()));
            } else
                trails.put(mc.player.getUniqueID(), new ItemTrail(mc.player));
        }
    }


    @Override
    public void onGlobalRenderTick() {
        if (self.GetSwitch()) {
            trails.forEach((key, value) -> {
                if (value.entity.isDead || mc.world.getEntityByID(value.entity.getEntityId()) == null) {
                    if (value.timer.isPaused())
                        value.timer.setTime(0);

                    value.timer.setPaused(false);
                }
                if (!value.timer.isPassed())
                    drawTrail(value);

            });
        }
        if (pearlPos.isEmpty() || !entities.GetSwitch())
            return;
        GL11.glPushMatrix();
        GL11.glBlendFunc(770, 771);
        GL11.glEnable(3042);
        GL11.glDisable(3553);
        GL11.glDisable(2929);
        GL11.glDepthMask(false);
        GL11.glLineWidth(pearlLineWidth.GetSlider());
        pearlPos.keySet().stream().filter(uuid -> pearlPos.get(uuid).size() > 2).forEach(uuid -> {
            GL11.glBegin(1);
            IntStream.range(1, pearlPos.get(uuid).size()).forEach(i -> {
                Color color = pearlColor.GetColor();
                GL11.glColor3d(color.getRed() / 255f, color.getGreen() / 255f, color.getBlue() / 255f);
                List<Vec3d> pos = pearlPos.get(uuid);
                GL11.glVertex3d(pos.get(i).x - mc.getRenderManager().viewerPosX, pos.get(i).y - mc.getRenderManager().viewerPosY, pos.get(i).z - mc.getRenderManager().viewerPosZ);
                GL11.glVertex3d(pos.get(i - 1).x - mc.getRenderManager().viewerPosX, pos.get(i - 1).y - mc.getRenderManager().viewerPosY, pos.get(i - 1).z - mc.getRenderManager().viewerPosZ);
            });
            GL11.glEnd();
        });
        GL11.glEnable(3553);
        GL11.glEnable(2929);
        GL11.glDepthMask(true);
        GL11.glDisable(3042);
        GL11.glPopMatrix();
    }

    void drawTrail(ItemTrail trail) {
        Color fadeColor = endColor.GetColor();
        GlStateManager.pushMatrix();
        GlStateManager.disableDepth();
        GlStateManager.disableLighting();
        GlStateManager.depthMask(false);
        GlStateManager.disableAlpha();
        GlStateManager.disableCull();
        GlStateManager.enableBlend();
        GL11.glDisable(3553);
        glEnable(2848);
        GL11.glBlendFunc(770, 771);
        GL11.glLineWidth(lineWidth.GetSlider());
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        (RenderUtil.builder = RenderUtil.tessellator.getBuffer()).begin(3, DefaultVertexFormats.POSITION_COLOR);
        buildBuffer(RenderUtil.builder, trail, startColor.GetColor(), fade.GetSwitch() ? fadeColor : startColor.GetColor());
        RenderUtil.tessellator.draw();
        GlStateManager.depthMask(true);
        GlStateManager.enableLighting();
        GlStateManager.enableDepth();
        GlStateManager.enableAlpha();
        GlStateManager.popMatrix();
        glEnable(3553);
        GL11.glPolygonMode(1032, 6914);
    }

    void buildBuffer(BufferBuilder builder, ItemTrail trail, Color start, Color end) {
        for (Position p : trail.positions) {
            Vec3d pos = RenderUtil.updateToCamera(p.pos);
            double value = normalize(trail.positions.indexOf(p), trail.positions.size());
            RenderUtil.addBuilderVertex(builder, pos.x, pos.y, pos.z, RenderUtil.interpolateColor((float) value, start, end));
        }
    }

    double normalize(double value, double max) {
        return (value - 0.0) / (max - 0.0);
    }

    static class ItemTrail {
        public Entity entity;
        public List<Position> positions;
        public Timer timer;

        ItemTrail(Entity entity) {
            this.entity = entity;
            positions = new ArrayList<>();
            (timer = new dev.zprestige.ruby.util.Timer()).setDelay(1000);
            timer.setPaused(true);
        }
    }

    static class Position {
        public Vec3d pos;
        public long time;

        public Position(Vec3d pos) {
            this.pos = pos;
            time = System.currentTimeMillis();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            Position position = (Position) o;
            return time == position.time && Objects.equals(pos, position.pos);
        }

        @Override
        public int hashCode() {
            return Objects.hash(pos, time);
        }
    }
}
