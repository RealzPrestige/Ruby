package dev.zprestige.ruby.module.misc;

import dev.zprestige.ruby.module.Category;
import dev.zprestige.ruby.module.Module;
import dev.zprestige.ruby.module.ModuleInfo;
import dev.zprestige.ruby.setting.impl.BooleanSetting;
import dev.zprestige.ruby.setting.impl.ColorSetting;
import dev.zprestige.ruby.util.BlockUtil;
import dev.zprestige.ruby.util.RenderUtil;
import net.minecraft.client.gui.GuiGameOver;
import net.minecraft.util.math.AxisAlignedBB;

import java.awt.*;

@ModuleInfo(name = "AutoRespawn" , category = Category.Misc, description = "automaticclee keeps you aliv")
public class AutoRespawn extends Module {
    public BooleanSetting showDeath = createSetting("Show Death", false);
    public ColorSetting color = createSetting("Color", new Color(-1), v -> showDeath.getValue());
    public AxisAlignedBB bb;

    @Override
    public void onGlobalRenderTick() {
        if (bb != null) {
            RenderUtil.drawBBBoxWithHeightDepth(bb, color.getValue(), color.getValue().getAlpha(), (float) (256 - bb.minY));
        }
    }

    @Override
    public void onTick() {
        if (!(mc.currentScreen instanceof GuiGameOver))
            return;
        if (showDeath.getValue())
            bb = new AxisAlignedBB(BlockUtil.getPlayerPos());
        mc.player.respawnPlayer();
        mc.displayGuiScreen(null);
    }
}
