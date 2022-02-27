package dev.zprestige.ruby.module.visual;

import dev.zprestige.ruby.eventbus.annotation.RegisterListener;
import dev.zprestige.ruby.events.PlayerChangeEvent;
import dev.zprestige.ruby.module.Module;
import dev.zprestige.ruby.settings.impl.ColorBox;
import dev.zprestige.ruby.settings.impl.Parent;
import dev.zprestige.ruby.settings.impl.Slider;
import dev.zprestige.ruby.settings.impl.Switch;
import dev.zprestige.ruby.util.RenderUtil;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

/**
 * creds to le phobos
 */
public class PopChams extends Module {
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
    public final Parent misc = Menu.Parent("Misc");
    public final Slider fadeTime = Menu.Slider("Fade Time", 0, 5000).parent(misc);
    public final Switch selfPop = Menu.Switch("Self Pop").parent(misc);
    public final Switch onDeath = Menu.Switch("On Death").parent(misc);
    public final Switch travel = Menu.Switch("Travel").parent(misc);
    public final Slider travelSpeed = Menu.Slider("Travel Speed", -10.0f, 10.0f).parent(misc);
    public final Parent rendering = Menu.Parent("Rendering");
    public final ColorBox solidColor = Menu.Color("Solid Color").parent(rendering);
    public final ColorBox outlineColor = Menu.Color("Outline Color").parent(rendering);
    public final Slider outlineWidth = Menu.Slider("Outline Width", 0.1f, 5.0f).parent(rendering);
    public final Switch differDeaths = Menu.Switch("Differ Deaths").parent(rendering);
    public final ColorBox deathSolidColor = Menu.Color("Death Solid Color").parent(rendering);
    public final ColorBox deathOutlineColor = Menu.Color("Death Outline Color").parent(rendering);
    public final Slider deathOutlineWidth = Menu.Slider("Death Outline Width", 0.1f, 5.0f).parent(rendering);
    public HashMap<String, PopData> popDataHashMap = new HashMap<>();

    @RegisterListener
    public void onTotemPop(PlayerChangeEvent.TotemPop event) {
        if (!isEnabled() || nullCheck())
            return;
        if (event.entityPlayer.equals(mc.player) && !selfPop.GetSwitch())
            return;
        popDataHashMap.put(event.entityPlayer.getName(), new PopChams.PopData(event.entityPlayer, System.currentTimeMillis(), event.entityPlayer.rotationYaw, event.entityPlayer.rotationPitch, event.entityPlayer.posX, event.entityPlayer.posY, event.entityPlayer.posZ, false));
    }

    @RegisterListener
    public void onDeath(PlayerChangeEvent.Death event) {
        if (!isEnabled() || nullCheck() || !onDeath.GetSwitch())
            return;
        if (event.entityPlayer.equals(mc.player) && !selfPop.GetSwitch())
            return;
        popDataHashMap.put(event.entityPlayer.getName(), new PopChams.PopData(event.entityPlayer, System.currentTimeMillis(), event.entityPlayer.rotationYaw, event.entityPlayer.rotationPitch, event.entityPlayer.posX, event.entityPlayer.posY, event.entityPlayer.posZ, true));
    }


    @Override
    public void onFrame(float partialTicks) {
        HashMap<String, PopChams.PopData> save = new HashMap<>(popDataHashMap);
        for (Map.Entry<String, PopChams.PopData> entry : save.entrySet()) {
            PopChams.PopData data = entry.getValue();
            if (travel.GetSwitch())
                data.y += travelSpeed.GetSlider() / 100.0f;
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
            Color boxColor = differDeaths.GetSwitch() && entry.getValue().isDeath ? deathSolidColor.GetColor() : solidColor.GetColor();
            Color outlineColor = differDeaths.GetSwitch() && entry.getValue().isDeath ? deathOutlineColor.GetColor() : this.outlineColor.GetColor();
            float maxBoxAlpha = boxColor.getAlpha();
            float maxOutlineAlpha = outlineColor.getAlpha();
            float alphaBoxAmount = maxBoxAlpha / fadeTime.GetSlider();
            float alphaOutlineAmount = maxOutlineAlpha / fadeTime.GetSlider();
            int fadeBoxAlpha = MathHelper.clamp((int) (alphaBoxAmount * (data.getTime() + fadeTime.GetSlider() - System.currentTimeMillis())), 0, (int) maxBoxAlpha);
            int fadeOutlineAlpha = MathHelper.clamp((int) (alphaOutlineAmount * (data.getTime() + fadeTime.GetSlider() - System.currentTimeMillis())), 0, (int) maxOutlineAlpha);
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

        popDataHashMap.entrySet().removeIf(e -> e.getValue().getTime() + fadeTime.GetSlider() < System.currentTimeMillis());
    }

    private void renderAxis(AxisAlignedBB bb, Color color, Color outline, boolean isDeath) {
        RenderUtil.renderBox(bb, color, outline, differDeaths.GetSwitch() && isDeath ? deathOutlineWidth.GetSlider() : outlineWidth.GetSlider());
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
