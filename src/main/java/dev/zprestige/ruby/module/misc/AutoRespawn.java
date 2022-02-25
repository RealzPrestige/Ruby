package dev.zprestige.ruby.module.misc;

import dev.zprestige.ruby.module.Module;
import dev.zprestige.ruby.settings.impl.ColorBox;
import dev.zprestige.ruby.settings.impl.Switch;
import dev.zprestige.ruby.util.BlockUtil;
import dev.zprestige.ruby.util.RenderUtil;
import net.minecraft.client.gui.GuiGameOver;
import net.minecraft.util.math.AxisAlignedBB;

public class AutoRespawn extends Module {
    public final Switch showDeath = Menu.Switch("Show Death");
    public final ColorBox color = Menu.Color("Color");
    public AxisAlignedBB bb;

    @Override
    public void onGlobalRenderTick() {
        if (bb != null) {
            RenderUtil.drawBBBoxWithHeightDepth(bb, color.GetColor(), color.GetColor().getAlpha(), (float) (256 - bb.minY));
        }
    }

    @Override
    public void onTick() {
        if (!(mc.currentScreen instanceof GuiGameOver))
            return;
        if (showDeath.GetSwitch())
            bb = new AxisAlignedBB(BlockUtil.getPlayerPos());
        mc.player.respawnPlayer();
        mc.displayGuiScreen(null);
    }
}
