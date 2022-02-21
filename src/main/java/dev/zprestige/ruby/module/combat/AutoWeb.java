package dev.zprestige.ruby.module.combat;

import dev.zprestige.ruby.module.Category;
import dev.zprestige.ruby.module.Module;
import dev.zprestige.ruby.module.ModuleInfo;
import dev.zprestige.ruby.setting.impl.BooleanSetting;
import dev.zprestige.ruby.setting.impl.FloatSetting;
import dev.zprestige.ruby.setting.impl.IntegerSetting;
import dev.zprestige.ruby.util.BlockUtil;
import dev.zprestige.ruby.util.EntityUtil;
import dev.zprestige.ruby.util.InventoryUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;

@ModuleInfo(name = "AutoWeb", category = Category.Combat, description = "autowebbers team web")
public class AutoWeb extends Module {
    public FloatSetting targetRange = createSetting("Target Range", 9.0f, 0.1f, 15.0f);
    public FloatSetting placeRange = createSetting("Place Range", 5.0f, 0.1f, 6.0f);
    public BooleanSetting predict = createSetting("Predict", false);
    public IntegerSetting predictTicks = createSetting("Predict Ticks", 1, 1, 5);
    public BooleanSetting packet = createSetting("Packet", false);
    public BooleanSetting rotate = createSetting("Rotate", false);
    public BooleanSetting onGroundOnly = createSetting("On Ground Only", false);
    public BooleanSetting onMoveCancel = createSetting("On Move Cancel", false);

    @Override
    public void onTick() {
        EntityPlayer entityPlayer = EntityUtil.getTarget(targetRange.getValue());
        if (entityPlayer == null)
            return;
        if (predict.getValue())
            entityPlayer.setEntityBoundingBox(new AxisAlignedBB(new BlockPos(EntityUtil.getPlayerPos((EntityPlayer) EntityUtil.getPredictedPosition(entityPlayer, predictTicks.getValue())))));
        BlockPos pos = EntityUtil.getPlayerPos(entityPlayer);
        if (mc.player.getDistanceSq(pos) > (placeRange.getValue() * placeRange.getValue()) || !mc.world.getBlockState(pos).getBlock().equals(Blocks.AIR))
            return;
        if ((onGroundOnly.getValue() && !mc.player.onGround) || (onMoveCancel.getValue() && EntityUtil.isMoving()))
            return;
        int slot = InventoryUtil.getItemFromHotbar(Item.getItemFromBlock(Blocks.WEB));
        if (slot == -1)
            return;
        BlockUtil.placeBlockWithSwitch(pos, EnumHand.MAIN_HAND, rotate.getValue(), packet.getValue(), slot);
    }
}
