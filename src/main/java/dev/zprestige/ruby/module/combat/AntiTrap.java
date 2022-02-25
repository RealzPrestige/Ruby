package dev.zprestige.ruby.module.combat;

import dev.zprestige.ruby.module.Module;
import dev.zprestige.ruby.settings.impl.Slider;
import dev.zprestige.ruby.settings.impl.Switch;
import dev.zprestige.ruby.util.BlockUtil;
import dev.zprestige.ruby.util.EntityUtil;
import dev.zprestige.ruby.util.InventoryUtil;
import dev.zprestige.ruby.util.Timer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.TreeMap;

public class AntiTrap extends Module {
    public final Slider placeDelay = Menu.Slider("Place Delay", 0, 500);
    public final Slider targetRange = Menu.Slider("Target Range", 0.1f, 15.0f);
    public final Switch rotate = Menu.Switch("Rotate");
    public final Switch packet = Menu.Switch("Packet");
    public ArrayList<BlockPos> placedPosses = new ArrayList<>();
    public Timer timer = new Timer();

    @Override
    public void onTick() {
        EntityPlayer entityPlayer = EntityUtil.getTarget(targetRange.GetSlider());
        BlockPos pos = EntityUtil.getPlayerPos(mc.player).up();
        if (entityPlayer == null)
            return;
        BlockPos targetPos = getBestSide(entityPlayer);
        if (!BlockUtil.isPlayerSafe(mc.player) || !isTrapped() || targetPos == null || !timer.getTime((long) placeDelay.GetSlider())) {
            if (!placedPosses.isEmpty())
                placedPosses.clear();
            return;
        }
        if (placedPosses.contains(pos))
            return;
        int slot = InventoryUtil.getItemFromHotbar(Items.END_CRYSTAL);
        int currentSlot = mc.player.inventory.currentItem;
        if (slot == -1)
            return;
        InventoryUtil.switchToSlot(slot);
        mc.player.connection.sendPacket(new CPacketPlayerTryUseItemOnBlock(targetPos, EnumFacing.UP, mc.player.getHeldItemOffhand().getItem().equals(Items.END_CRYSTAL) ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND, 0.5f, 0.5f, 0.5f));
        mc.player.inventory.currentItem = currentSlot;
        mc.playerController.updateController();
    }

    public BlockPos getBestSide(EntityPlayer entityPlayer) {
        BlockPos pos = EntityUtil.getPlayerPos(mc.player).up();
        TreeMap<Double, BlockPos> posTreeMap = new TreeMap<>();
        if (isntAir(pos.north()) && !isntAir(pos.north().up()) && !isntAir(pos.north().up().up()))
            posTreeMap.put(entityPlayer.getDistanceSq(pos.north()), pos.north());
        if (isntAir(pos.east()) && !isntAir(pos.east().up()) && !isntAir(pos.east().up().up()))
            posTreeMap.put(entityPlayer.getDistanceSq(pos.east()), pos.east());
        if (isntAir(pos.south()) && !isntAir(pos.south().up()) && !isntAir(pos.south().up().up()))
            posTreeMap.put(entityPlayer.getDistanceSq(pos.south()), pos.south());
        if (isntAir(pos.west()) && !isntAir(pos.west().up()) && !isntAir(pos.west().up().up()))
            posTreeMap.put(entityPlayer.getDistanceSq(pos.west()), pos.west());
        if (!posTreeMap.isEmpty())
            return posTreeMap.lastEntry().getValue();
        return null;
    }

    public boolean isTrapped() {
        BlockPos pos = EntityUtil.getPlayerPos(mc.player).up();
        return isntAir(pos.north()) && isntAir(pos.east()) && isntAir(pos.south()) && isntAir(pos.west());
    }

    public boolean isntAir(BlockPos pos) {
        return !mc.world.getBlockState(pos).getBlock().equals(Blocks.AIR);
    }
}