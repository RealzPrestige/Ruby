package dev.zprestige.ruby.module.misc;

import dev.zprestige.ruby.module.Module;
import dev.zprestige.ruby.setting.impl.BooleanSetting;
import dev.zprestige.ruby.setting.impl.ColorSetting;
import dev.zprestige.ruby.util.BlockUtil;
import dev.zprestige.ruby.util.RenderUtil;
import net.minecraft.client.gui.GuiGameOver;
import net.minecraft.util.math.AxisAlignedBB;

import java.awt.*;

public class AutoRespawn extends Module {
    public final Switch showDeath = Menu.Switch("Show Death");
    public final ColorBox color = Menu.Switch("Color", new Color(-1), v -> showDeath.getValue());
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
