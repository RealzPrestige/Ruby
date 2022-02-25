package dev.zprestige.ruby.module.combat;

import dev.zprestige.ruby.module.Module;
import dev.zprestige.ruby.settings.impl.Slider;
import dev.zprestige.ruby.settings.impl.Switch;
import dev.zprestige.ruby.util.BlockUtil;
import dev.zprestige.ruby.util.EntityUtil;
import dev.zprestige.ruby.util.InventoryUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;

public class AutoWeb extends Module {
    public final Slider targetRange = Menu.Slider("Target Range", 0.1f, 15.0f);
    public final Slider placeRange = Menu.Slider("Place Range", 0.1f, 6.0f);
    public final Switch predict = Menu.Switch("Predict");
    public final Slider predictTicks = Menu.Slider("Predict Ticks", 1, 5);
    public final Switch packet = Menu.Switch("Packet");
    public final Switch rotate = Menu.Switch("Rotate");
    public final Switch onGroundOnly = Menu.Switch("On Ground Only");
    public final Switch onMoveCancel = Menu.Switch("On Move Cancel");

    @Override
    public void onTick() {
        EntityPlayer entityPlayer = EntityUtil.getTarget(targetRange.GetSlider());
        if (entityPlayer == null)
            return;
        if (predict.GetSwitch())
            entityPlayer.setEntityBoundingBox(new AxisAlignedBB(new BlockPos(EntityUtil.getPlayerPos((EntityPlayer) EntityUtil.getPredictedPosition(entityPlayer, predictTicks.GetSlider())))));
        BlockPos pos = EntityUtil.getPlayerPos(entityPlayer);
        if (mc.player.getDistanceSq(pos) > (placeRange.GetSlider() * placeRange.GetSlider()) || !mc.world.getBlockState(pos).getBlock().equals(Blocks.AIR))
            return;
        if ((onGroundOnly.GetSwitch() && !mc.player.onGround) || (onMoveCancel.GetSwitch() && EntityUtil.isMoving()))
            return;
        int slot = InventoryUtil.getItemFromHotbar(Item.getItemFromBlock(Blocks.WEB));
        if (slot == -1)
            return;
        BlockUtil.placeBlockWithSwitch(pos, EnumHand.MAIN_HAND, rotate.GetSwitch(), packet.GetSwitch(), slot);
    }
}
