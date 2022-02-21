package dev.zprestige.ruby.module.misc;

import dev.zprestige.ruby.module.Category;
import dev.zprestige.ruby.module.Module;
import dev.zprestige.ruby.module.ModuleInfo;
import dev.zprestige.ruby.setting.impl.FloatSetting;
import dev.zprestige.ruby.util.BlockUtil;
import dev.zprestige.ruby.util.EntityUtil;
import dev.zprestige.ruby.util.RenderUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;

import static org.lwjgl.opengl.GL11.*;

@ModuleInfo(name = "RunDetect" , category = Category.Misc, description = "detects when fat kids who previously sworded go run vroom vroom and make m kaboom")
public class RunDetect extends Module {
    public static RunDetect Instance;
    public FloatSetting radius = createSetting("Radius", 5.0f, 0.1f, 15.0f);

    public ArrayList<EntityPlayer> potentialRunnersList = new ArrayList<>();
    public ArrayList<EntityPlayer> swordedPotentialRunnersList = new ArrayList<>();
    public ArrayList<EntityPlayer> gappledPreviouslySwordedPotentialRunnerList = new ArrayList<>();

    public RunDetect() {
        Instance = this;
    }

    @Override
    public void onTick() {
        mc.world.playerEntities.stream().filter(player -> !player.equals(mc.player) && !potentialRunnersList.contains(player) && mc.player.getDistanceSq(EntityUtil.getPlayerPos(player)) < (radius.getValue() * radius.getValue())).forEach(player -> potentialRunnersList.add(player));
        potentialRunnersList.stream().filter(entityPlayer -> !swordedPotentialRunnersList.contains(entityPlayer) && entityPlayer.getHeldItemMainhand().getItem().equals(Items.DIAMOND_SWORD)).forEach(entityPlayer -> swordedPotentialRunnersList.add(entityPlayer));
        swordedPotentialRunnersList.stream().filter(entityPlayer -> !gappledPreviouslySwordedPotentialRunnerList.contains(entityPlayer) && entityPlayer.getHeldItemMainhand().getItem().equals(Items.GOLDEN_APPLE)).forEach(entityPlayer -> gappledPreviouslySwordedPotentialRunnerList.add(entityPlayer));
        potentialRunnersList.stream().filter(entityPlayer -> mc.player.getDistanceSq(EntityUtil.getPlayerPos(entityPlayer)) > (radius.getValue() * radius.getValue())).findFirst().ifPresent(entityPlayer -> potentialRunnersList.remove(entityPlayer));
        swordedPotentialRunnersList.stream().filter(entityPlayer -> mc.player.getDistanceSq(EntityUtil.getPlayerPos(entityPlayer)) > (radius.getValue() * radius.getValue())).findFirst().ifPresent(entityPlayer -> potentialRunnersList.remove(entityPlayer));
    }

    @Override
    public void onGlobalRenderTick() {
        for (EntityPlayer entityPlayer : gappledPreviouslySwordedPotentialRunnerList) {
            glPushMatrix();
            Vec3d i = RenderUtil.interpolateEntity(entityPlayer);
            RenderUtil.drawNametag("Potentially running.", i.x, i.y + 1, i.z, 0.005, -1);
            glColor4f(1f, 1f, 1f, 1f);
            glPopMatrix();
            if (!BlockUtil.isPlayerSafe(entityPlayer) || !entityPlayer.getHeldItemMainhand().getItem().equals(Items.GOLDEN_APPLE) || mc.player.getDistanceSq(EntityUtil.getPlayerPos(entityPlayer)) > (radius.getValue() * radius.getValue())) {
                potentialRunnersList.remove(entityPlayer);
                swordedPotentialRunnersList.remove(entityPlayer);
                gappledPreviouslySwordedPotentialRunnerList.remove(entityPlayer);
                return;
            }
        }
    }
}
