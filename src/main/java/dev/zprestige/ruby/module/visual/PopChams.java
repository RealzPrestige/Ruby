package dev.zprestige.ruby.module.visual;

import dev.zprestige.ruby.eventbus.annotation.RegisterListener;
import dev.zprestige.ruby.events.PlayerChangeEvent;
import dev.zprestige.ruby.module.Category;
import dev.zprestige.ruby.module.Module;
import dev.zprestige.ruby.module.ModuleInfo;
import dev.zprestige.ruby.setting.impl.*;
import dev.zprestige.ruby.util.RenderUtil;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

/**
 * creds to le phobos
 */
@ModuleInfo(name = "PopChams", category = Category.Visual, description = "pops the chams")
public class PopChams extends Module {
    public ParentSetting misc = createSetting("Misc");
    public IntegerSetting fadeTime = createSetting("Fade Time", 1500, 0, 5000).setParent(misc);
    public BooleanSetting selfPop = createSetting("Self Pop", false).setParent(misc);
    public BooleanSetting onDeath = createSetting("On Death", false).setParent(misc);
    public BooleanSetting travel = createSetting("Travel", false).setParent(misc);
    public FloatSetting travelSpeed = createSetting("Travel Speed", 1.0f, -10.0f, 10.0f, (Predicate<Float>) v -> travel.getValue()).setParent(misc);
    public ParentSetting rendering = createSetting("Rendering");
    public ColorSetting solidColor = createSetting("Solid Color", new Color(-1)).setParent(rendering);
    public ColorSetting outlineColor = createSetting("Outline Color", new Color(-1)).setParent(rendering);
    public FloatSetting outlineWidth = createSetting("Outline Width", 1.0f, 0.1f, 5.0f).setParent(rendering);
    public BooleanSetting differDeaths = createSetting("Differ Deaths", false).setParent(rendering);
    public ColorSetting deathSolidColor = createSetting("Death Solid Color", new Color(-1), v -> differDeaths.getValue()).setParent(rendering);
    public ColorSetting deathOutlineColor = createSetting("Death Outline Color", new Color(-1), v -> differDeaths.getValue()).setParent(rendering);
    public FloatSetting deathOutlineWidth = createSetting("Death Outline Width", 1.0f, 0.1f, 5.0f, (Predicate<Float>) v -> differDeaths.getValue()).setParent(rendering);
    public HashMap<String, PopData> popDataHashMap = new HashMap<>();

    public static double HEAD_X = -0.2;
    public static double HEAD_Y = 1.5;
    public static double HEAD_Z = -0.25;
    public static double HEAD_X1 = 0.2;
    public static double HEAD_Y1 = 1.95;
    public static double HEAD_Z1 = 0.25;
    public static double CHEST_X = -0.18;
    public static double CHEST_Y = 0.8;
    public static double CHEST_Z = -0.275;
    public static double CHEST_X1 = 0.18;
    public static double CHEST_Y1 = 1.5;
    public static double CHEST_Z1 = 0.275;
    public static double ARM1_X = -0.1;
    public static double ARM1_Y = 0.75;
    public static double ARM1_Z = 0.275;
    public static double ARM1_X1 = 0.1;
    public static double ARM1_Y1 = 1.5;
    public static double ARM1_Z1 = 0.5;
    public static double ARM2_X = -0.1;
    public static double ARM2_Y = 0.75;
    public static double ARM2_Z = -0.275;
    public static double ARM2_X1 = 0.1;
    public static double ARM2_Y1 = 1.5;
    public static double ARM2_Z1 = -0.5;
    public static double LEG1_X = -0.15;
    public static double LEG1_Y = 0.0;
    public static double LEG1_Z = 0.0;
    public static double LEG1_X1 = 0.15;
    public static double LEG1_Y1 = 0.8;
    public static double LEG1_Z1 = 0.25;
    public static double LEG2_X = -0.15;
    public static double LEG2_Y = 0.0;
    public static double LEG2_Z = 0.0;
    public static double LEG2_X1 = 0.15;
    public static double LEG2_Y1 = 0.8;
    public static double LEG2_Z1 = -0.25;

    @RegisterListener
    public void onTotemPop(PlayerChangeEvent.TotemPop event) {
        if (!isEnabled() || nullCheck())
            return;
        if (event.entityPlayer.equals(mc.player) && !selfPop.getValue())
            return;
        popDataHashMap.put(event.entityPlayer.getName(), new PopChams.PopData(event.entityPlayer, System.currentTimeMillis(), event.entityPlayer.rotationYaw, event.entityPlayer.rotationPitch, event.entityPlayer.posX, event.entityPlayer.posY, event.entityPlayer.posZ, false));
    }

    @RegisterListener
    public void onDeath(PlayerChangeEvent.Death event) {
        if (!isEnabled() || nullCheck() || !onDeath.getValue())
            return;
        if (event.entityPlayer.equals(mc.player) && !selfPop.getValue())
            return;
        popDataHashMap.put(event.entityPlayer.getName(), new PopChams.PopData(event.entityPlayer, System.currentTimeMillis(), event.entityPlayer.rotationYaw, event.entityPlayer.rotationPitch, event.entityPlayer.posX, event.entityPlayer.posY, event.entityPlayer.posZ, true));
    }


    @Override
    public void onGlobalRenderTick() {
        HashMap<String, PopChams.PopData> save = new HashMap<>(popDataHashMap);
        for (Map.Entry<String, PopChams.PopData> entry : save.entrySet()) {
            PopChams.PopData data = entry.getValue();
            if (travel.getValue())
                data.y += travelSpeed.getValue() / 100.0f;
            double x = data.getX() - mc.getRenderManager().viewerPosX;
            double y = data.getY() - mc.getRenderManager().viewerPosY;
            double z = data.getZ() - mc.getRenderManager().viewerPosZ;
            float yaw = data.getYaw();
            float pitch = data.getPitch();
            AxisAlignedBB head = new AxisAlignedBB(x + HEAD_X, y + HEAD_Y, z + HEAD_Z, x + HEAD_X1, y + HEAD_Y1, z + HEAD_Z1);
            AxisAlignedBB chest = new AxisAlignedBB(x + CHEST_X, y + CHEST_Y, z + CHEST_Z, x + CHEST_X1, y + CHEST_Y1, z + CHEST_Z1);
            AxisAlignedBB arm1 = new AxisAlignedBB(x + ARM1_X, y + ARM1_Y, z + ARM1_Z, x + ARM1_X1, y + ARM1_Y1, z + ARM1_Z1);
            AxisAlignedBB arm2 = new AxisAlignedBB(x + ARM2_X, y + ARM2_Y, z + ARM2_Z, x + ARM2_X1, y + ARM2_Y1, z + ARM2_Z1);
            AxisAlignedBB leg1 = new AxisAlignedBB(x + LEG1_X, y + LEG1_Y, z + LEG1_Z, x + LEG1_X1, y + LEG1_Y1, z + LEG1_Z1);
            AxisAlignedBB leg2 = new AxisAlignedBB(x + LEG2_X, y + LEG2_Y, z + LEG2_Z, x + LEG2_X1, y + LEG2_Y1, z + LEG2_Z1);
            GlStateManager.pushMatrix();
            GlStateManager.translate(x, y, z);
            GlStateManager.rotate(180 + (-(yaw + 90)), 0.0f, 1.0f, 0.0f);
            GlStateManager.translate(-x, -y, -z);
            Color boxColor = differDeaths.getValue() && entry.getValue().isDeath ? deathSolidColor.getValue() : solidColor.getValue();
            Color outlineColor = differDeaths.getValue() && entry.getValue().isDeath ? deathOutlineColor.getValue() : this.outlineColor.getValue();
            float maxBoxAlpha = boxColor.getAlpha();
            float maxOutlineAlpha = outlineColor.getAlpha();
            float alphaBoxAmount = maxBoxAlpha / fadeTime.getValue();
            float alphaOutlineAmount = maxOutlineAlpha / fadeTime.getValue();
            int fadeBoxAlpha = MathHelper.clamp((int) (alphaBoxAmount * (data.getTime() + fadeTime.getValue() - System.currentTimeMillis())), 0, (int) maxBoxAlpha);
            int fadeOutlineAlpha = MathHelper.clamp((int) (alphaOutlineAmount * (data.getTime() + fadeTime.getValue() - System.currentTimeMillis())), 0, (int) maxOutlineAlpha);
            Color box = new Color(boxColor.getRed(), boxColor.getGreen(), boxColor.getBlue(), fadeBoxAlpha);
            Color out = new Color(outlineColor.getRed(), outlineColor.getGreen(), outlineColor.getBlue(), fadeOutlineAlpha);
            renderAxis(chest, box, out, entry.getValue().isDeath);
            renderAxis(arm1, box, out, entry.getValue().isDeath);
            renderAxis(arm2, box, out, entry.getValue().isDeath);
            renderAxis(leg1, box, out, entry.getValue().isDeath);
            renderAxis(leg2, box, out, entry.getValue().isDeath);
            GlStateManager.translate(x, y + 1.5, z);
            GlStateManager.rotate(pitch, 0.0f, 0.0f, 1.0f);
            GlStateManager.translate(-x, -y - 1.5, -z);
            renderAxis(head, box, out, entry.getValue().isDeath);
            GlStateManager.popMatrix();
        }

        popDataHashMap.entrySet().removeIf(e -> e.getValue().getTime() + fadeTime.getValue() < System.currentTimeMillis());
    }

    private void renderAxis(AxisAlignedBB bb, Color color, Color outline, boolean isDeath) {
        RenderUtil.renderBox(bb, color, outline, differDeaths.getValue() && isDeath ? deathOutlineWidth.getValue() :  outlineWidth.getValue());
    }

    public static class PopData {
        public EntityPlayer player;
        public long time;
        public float yaw;
        public float pitch;
        public double x;
        public double y;
        public double z;
        public boolean isDeath;

        public PopData(EntityPlayer player, long time, float yaw, float pitch, double x, double y, double z, boolean isDeath) {
            this.player = player;
            this.time = time;
            this.yaw = yaw;
            this.pitch = pitch;
            this.x = x;
            this.y = y;
            this.z = z;
            this.isDeath = isDeath;
        }

        public EntityPlayer getPlayer() {
            return player;
        }

        public long getTime() {
            return time;
        }

        public float getYaw() {
            return yaw;
        }

        public float getPitch() {
            return pitch;
        }

        public double getX() {
            return x;
        }

        public double getY() {
            return y;
        }

        public double getZ() {
            return z;
        }
    }
}
