package dev.zprestige.ruby.module.combat;

import dev.zprestige.ruby.Ruby;
import dev.zprestige.ruby.module.Module;
import dev.zprestige.ruby.settings.impl.ComboBox;
import dev.zprestige.ruby.settings.impl.Parent;
import dev.zprestige.ruby.settings.impl.Slider;
import dev.zprestige.ruby.settings.impl.Switch;
import dev.zprestige.ruby.util.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;

import java.awt.*;
import java.util.ArrayList;
import java.util.TreeMap;
import java.util.stream.Collectors;

public class HolePush extends Module {

    public final Parent ranges = Menu.Parent("Ranges");
    public final Slider targetRange = Menu.Slider("Target Range", 0.1f, 15.0f).parent(ranges);
    public final Slider placeRange = Menu.Slider("Place Range", 0.1f, 6.0f).parent(ranges);

    public final Parent timing = Menu.Parent("Timing");
    public final Slider placeDelay = Menu.Slider("Place Delay", 0, 1000).parent(timing);

    public final Parent rotations = Menu.Parent("Rotations");
    public final ComboBox rotationMode = Menu.ComboBox("Rotation Mode", new String[]{"Packet", "Vanilla", "TickWait"}).parent(rotations);
    public final Switch rotateBack = Menu.Switch("Rotate Back").parent(rotations);

    public final Parent placements = Menu.Parent("Placements");
    public final Switch inLiquids = Menu.Switch("In Liquids").parent(placements);
    public final Switch packet = Menu.Switch("Packet").parent(placements);

    public final Parent mining = Menu.Parent("Mining");
    public final Switch mineRedstone = Menu.Switch("Mine Redstone").parent(mining);
    public final ComboBox mineMode = Menu.ComboBox("Mine Mode", new String[]{"Packet", "Click", "Vanilla"}).parent(mining);
    public final Switch consistent = Menu.Switch("Consistent").parent(mining);
    public final Switch silentSwitch = Menu.Switch("Silent Switch").parent(mining);

    public final Parent rendering = Menu.Parent("Rendering");
    public final Switch render = Menu.Switch("Render Bounding Boxes").parent(rendering);
    public final Slider lineWidth = Menu.Slider("Line Width", 0.1f, 5.0f).parent(rendering);

    public Side side = null;
    public EntityPlayer entityPlayer = null;
    public BlockPos entityPlayerPos = null, placedRedstonePos = null, placedPistonPos = null;
    public Timer timer = new Timer();
    public boolean rotated = false, mined = false;

    @Override
    public void onEnable() {
        entityPlayer = getUntrappedClosestEntityPlayer(targetRange.GetSlider(), true);
        if (entityPlayer == null) {
            disableModule("No safe Targets found, disabling HolePushRewrite!");
            return;
        }
        entityPlayerPos = EntityUtil.getPlayerPos(entityPlayer).up();
        side = getSide(entityPlayerPos);
        if (side == null) {
            disableModule("No possible place sides found, disabling HolePushRewrite!");
            return;
        }
        rotated = false;
        mined = false;
        placedRedstonePos = null;
    }

    @Override
    public void onTick() {
        if (!timer.getTime((long) placeDelay.GetSlider()))
            return;
        entityPlayer = getUntrappedClosestEntityPlayer(targetRange.GetSlider(), false);
        if (entityPlayer == null) {
            disableModule("No safe Targets found, disabling HolePushRewrite!");
            return;
        }
        BlockPos pistonPos = getPistonPos(entityPlayerPos, side);
        BlockPos redstonePos = getRedstonePos(entityPlayerPos, side);
        if (!BlockUtil.isPlayerSafe(entityPlayer)) {
            disableModule("Target no longer safe, disabling HolePushRewrite!");
            return;
        }
        if (!placedPiston(entityPlayerPos)) {
            float rotationYaw = mc.player.rotationYaw;
            switch (rotationMode.GetCombo()) {
                case "Packet":
                    rotatePacket(side);
                    break;
                case "Vanilla":
                    rotateVanilla(side);
                    break;
                case "TickWait":
                    if (!rotated) {
                        rotateVanilla(side);
                        rotated = true;
                        return;
                    }
                    break;
            }
            int slot = InventoryUtil.getItemFromHotbar(Item.getItemFromBlock(Blocks.PISTON));
            if (slot == -1) {
                disableModule("No Pistons found, disabling HolePushRewrite.");
                return;
            }
            BlockUtil.placeBlockWithSwitch(pistonPos, EnumHand.MAIN_HAND, false, packet.GetSwitch(), slot, timer);
            placedPistonPos = pistonPos;
            if (rotateBack.GetSwitch() && !rotationMode.GetCombo().equals("Packet"))
                mc.player.rotationYaw = rotationYaw;
            rotated = false;
            return;
        }
        if (!isPistonTriggered(pistonPos) && redstonePos != null) {
            float rotationYaw = mc.player.rotationYaw;
            switch (rotationMode.GetCombo()) {
                case "Packet":
                    rotatePacket(side);
                    break;
                case "Vanilla":
                    rotateVanilla(side);
                    break;
                case "TickWait":
                    if (!rotated) {
                        rotateVanilla(side);
                        rotated = true;
                        return;
                    }
                    break;
            }
            int slot = InventoryUtil.getItemFromHotbar(Item.getItemFromBlock(Blocks.REDSTONE_BLOCK));
            if (slot == -1) {
                disableModule("No redstone blocks found, disabling HolePushRewrite.");
                return;
            }
            BlockUtil.placeBlockWithSwitch(redstonePos, EnumHand.MAIN_HAND, false, packet.GetSwitch(), slot, timer);
            if (rotateBack.GetSwitch() && !rotationMode.GetCombo().equals("Packet"))
                mc.player.rotationYaw = rotationYaw;
            placedRedstonePos = redstonePos;
            rotated = false;
            mined = false;
            return;
        }
        if (isPistonTriggered(pistonPos) && mineRedstone.GetSwitch() && !mined) {
            if (!mc.player.getHeldItemMainhand().getItem().equals(Items.DIAMOND_PICKAXE))
                return;
            switch (mineMode.GetCombo()) {
                case "Packet":
                    mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.START_DESTROY_BLOCK, placedRedstonePos, EnumFacing.UP));
                    mc.player.swingArm(EnumHand.MAIN_HAND);
                    mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, placedRedstonePos, EnumFacing.UP));
                    mined = !consistent.GetSwitch();
                    return;
                case "Click":
                    mc.playerController.onPlayerDamageBlock(placedRedstonePos, mc.player.getHorizontalFacing());
                    EntityUtil.swingArm(EntityUtil.SwingType.MainHand);
                    mined = !consistent.GetSwitch();
                    return;
                case "Vanilla":
                    int currentItem = mc.player.inventory.currentItem;
                    int slot = InventoryUtil.getItemFromHotbar(Items.DIAMOND_PICKAXE);
                    if (silentSwitch.GetSwitch()) {
                        if (slot == -1)
                            return;
                        InventoryUtil.switchToSlot(slot);
                    }
                    mc.playerController.onPlayerDamageBlock(placedRedstonePos, mc.objectMouseOver.sideHit);
                    mc.player.swingArm(EnumHand.MAIN_HAND);
                    if (silentSwitch.GetSwitch() && slot != -1) {
                        mc.player.inventory.currentItem = currentItem;
                        mc.playerController.updateController();
                    }
            }
        }
    }

    @Override
    public void onGlobalRenderTick() {
        if (render.GetSwitch()) {
            if (placedPistonPos != null)
                RenderUtil.drawBlockOutlineBB(new AxisAlignedBB(placedPistonPos), new Color(0xFFFFFF), lineWidth.GetSlider());
            if (placedRedstonePos != null)
                RenderUtil.drawBlockOutlineBB(new AxisAlignedBB(placedRedstonePos), new Color(0xFFFFFF), lineWidth.GetSlider());
        }
    }

    public EntityPlayer getUntrappedClosestEntityPlayer(float range, boolean pistonCheck) {
        TreeMap<Float, EntityPlayer> treeMap = new TreeMap<>();
        for (EntityPlayer entityPlayer : mc.world.playerEntities) {
            if (!entityPlayer.equals(mc.player) && !Ruby.friendManager.isFriend(entityPlayer.getName()) && mc.player.getDistance(entityPlayer) < range && BlockUtil.isPlayerSafe(entityPlayer) && canPlace(EntityUtil.getPlayerPos(entityPlayer).up().up()) && (!pistonCheck || mc.world.getBlockState(EntityUtil.getPlayerPos(entityPlayer).up()).getBlock().equals(Blocks.AIR))) {
                treeMap.put(mc.player.getDistance(entityPlayer), entityPlayer);
            }
        }
        if (!treeMap.isEmpty())
            return treeMap.firstEntry().getValue();
        return null;
    }

    public BlockPos getPistonPos(BlockPos pos, Side side) {
        switch (side) {
            case North:
                return pos.north();
            case East:
                return pos.east();
            case South:
                return pos.south();
            case West:
                return pos.west();
        }
        return null;
    }

    public BlockPos getRedstonePos(BlockPos pos, Side side) {
        switch (side) {
            case North:
                if (canPlace(pos.north().north()))
                    return pos.north().north();
                if (canPlace(pos.north().up()))
                    return pos.north().up();
                if (canPlace(pos.north().east()))
                    return pos.north().east();
                if (canPlace(pos.north().west()))
                    return pos.north().west();
                break;
            case East:
                if (canPlace(pos.east().east()))
                    return pos.east().east();
                if (canPlace(pos.east().up()))
                    return pos.east().up();
                if (canPlace(pos.east().north()))
                    return pos.east().north();
                if (canPlace(pos.east().south()))
                    return pos.east().south();
                break;
            case South:
                if (canPlace(pos.south().south()))
                    return pos.south().south();
                if (canPlace(pos.south().up()))
                    return pos.south().up();
                if (canPlace(pos.south().east()))
                    return pos.south().east();
                if (canPlace(pos.south().west()))
                    return pos.south().west();
                break;
            case West:
                if (canPlace(pos.west().west()))
                    return pos.west().west();
                if (canPlace(pos.west().up()))
                    return pos.west().up();
                if (canPlace(pos.west().north()))
                    return pos.west().north();
                if (canPlace(pos.west().south()))
                    return pos.west().south();
                break;
        }
        return null;
    }

    public boolean isPistonTriggered(BlockPos pos) {
        return isRedstone(pos.north()) || isRedstone(pos.east()) || isRedstone(pos.south()) || isRedstone(pos.west()) || isRedstone(pos.up());
    }

    public boolean isRedstone(BlockPos pos) {
        return mc.world.getBlockState(pos).getBlock().equals(Blocks.REDSTONE_BLOCK);
    }

    public boolean isPiston(BlockPos pos) {
        return mc.world.getBlockState(pos).getBlock().equals(Blocks.PISTON);
    }

    public boolean canPlace(BlockPos pos) {
        ArrayList<Entity> intersecting = mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(pos)).stream().filter(entity -> !(entity instanceof EntityEnderCrystal)).collect(Collectors.toCollection(ArrayList::new));
        return intersecting.isEmpty() && (mc.player.getDistanceSq(pos) < (placeRange.GetSlider() * placeRange.GetSlider())) && mc.world.getEntitiesWithinAABBExcludingEntity(null, new AxisAlignedBB(pos)).isEmpty() && (mc.world.getBlockState(pos).getBlock().equals(Blocks.AIR) || (inLiquids.GetSwitch() && ((mc.world.getBlockState(pos).getBlock().equals(Blocks.WATER) || mc.world.getBlockState(pos).getBlock().equals(Blocks.FLOWING_WATER)) || (mc.world.getBlockState(pos).getBlock().equals(Blocks.LAVA) || mc.world.getBlockState(pos).getBlock().equals(Blocks.FLOWING_LAVA)))));
    }

    public boolean canPlacePiston(BlockPos pos) {
        return (mc.player.getDistanceSq(pos) < (placeRange.GetSlider() * placeRange.GetSlider())) && mc.world.getEntitiesWithinAABBExcludingEntity(null, new AxisAlignedBB(pos)).isEmpty() && (mc.world.getBlockState(pos).getBlock().equals(Blocks.PISTON) || mc.world.getBlockState(pos).getBlock().equals(Blocks.AIR) || (inLiquids.GetSwitch() && ((mc.world.getBlockState(pos).getBlock().equals(Blocks.WATER) || mc.world.getBlockState(pos).getBlock().equals(Blocks.FLOWING_WATER)) || (mc.world.getBlockState(pos).getBlock().equals(Blocks.LAVA) || mc.world.getBlockState(pos).getBlock().equals(Blocks.FLOWING_LAVA)))));
    }

    public void rotatePacket(Side side) {
        switch (side) {
            case North:
                mc.player.connection.sendPacket(new CPacketPlayer.Rotation(180, mc.player.rotationPitch, mc.player.onGround));
                break;
            case East:
                mc.player.connection.sendPacket(new CPacketPlayer.Rotation(-90, mc.player.rotationPitch, mc.player.onGround));
                break;
            case South:
                mc.player.connection.sendPacket(new CPacketPlayer.Rotation(0, mc.player.rotationPitch, mc.player.onGround));
                break;
            case West:
                mc.player.connection.sendPacket(new CPacketPlayer.Rotation(90, mc.player.rotationPitch, mc.player.onGround));
                break;
        }
    }

    public void rotateVanilla(Side side) {
        switch (side) {
            case North:
                mc.player.rotationYaw = 180;
                break;
            case East:
                mc.player.rotationYaw = -90;
                break;
            case South:
                mc.player.rotationYaw = 0;
                break;
            case West:
                mc.player.rotationYaw = 90;
                break;
        }
    }

    public Side getSide(BlockPos pos) {
        if (canPlacePiston(pos.north()) && canPlace(pos.south()) && canPlace(pos.south().up()) && (canPlace(pos.north().north()) || canPlace(pos.north().east()) || canPlace(pos.north().west()) || canPlace(pos.north().up())))
            return Side.North;
        if (canPlacePiston(pos.east()) && canPlace(pos.west()) && canPlace(pos.west().up()) && (canPlace(pos.east().east()) || canPlace(pos.east().north()) || canPlace(pos.east().south()) || canPlace(pos.east().up())))
            return Side.East;
        if (canPlacePiston(pos.south()) && canPlace(pos.north()) && canPlace(pos.north().up()) && (canPlace(pos.south().south()) || canPlace(pos.south().east()) || canPlace(pos.south().west()) || canPlace(pos.south().up())))
            return Side.South;
        if (canPlacePiston(pos.west()) && canPlace(pos.east()) && canPlace(pos.east().up()) && (canPlace(pos.west().west()) || canPlace(pos.west().north()) || canPlace(pos.west().east()) || canPlace(pos.west().up())))
            return Side.West;
        return null;
    }

    public boolean placedPiston(BlockPos pos) {
        switch (side) {
            case North:
                return isPiston(pos.north());
            case East:
                return isPiston(pos.east());
            case South:
                return isPiston(pos.south());
            case West:
                return isPiston(pos.west());
        }
        return false;
    }

    public enum Side {
        North,
        East,
        South,
        West
    }
}
