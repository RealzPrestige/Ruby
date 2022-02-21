package dev.zprestige.ruby.module.visual;

import com.mojang.realmsclient.gui.ChatFormatting;
import dev.zprestige.ruby.module.Category;
import dev.zprestige.ruby.module.Module;
import dev.zprestige.ruby.module.ModuleInfo;
import dev.zprestige.ruby.setting.impl.ColorSetting;
import dev.zprestige.ruby.util.RenderUtil;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;

import java.awt.*;
import java.util.HashMap;

@ModuleInfo(name = "Waypoints", category = Category.Visual, description = "Shows waypoints n shit")
public class Waypoints extends Module {
    public static Waypoints Instance;
    public ColorSetting color = createSetting("Color", new Color(-1));
    public HashMap<String, Waypoint> waypointHashMap = new HashMap<>();

    public Waypoints(){
        Instance = this;
    }

    @Override
    public void onGlobalRenderTick() {
        HashMap<String, Waypoint> waypointHashMap1 = new HashMap<>(waypointHashMap);
        waypointHashMap1.forEach((key, value) -> value.render());
    }

    public static class Waypoint {
        public String name;
        public BlockPos pos;
        public Waypoints waypoints = Waypoints.Instance;

        public Waypoint(String name, BlockPos pos) {
            this.name = name;
            this.pos = pos;
        }

        public void render() {
            AxisAlignedBB bb = new AxisAlignedBB(pos);
            RenderUtil.drawBBBoxWithHeight(bb, waypoints.color.getValue(), waypoints.color.getValue().getAlpha(), (float) (256 - bb.minY));
            RenderUtil.drawText2(pos, name + ChatFormatting.GRAY + (" (" +( waypoints.mc.player.getDistanceSq(pos) / 2f) + "m)"));
        }
    }
}
