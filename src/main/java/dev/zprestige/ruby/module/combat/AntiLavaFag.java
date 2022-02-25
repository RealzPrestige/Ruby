package dev.zprestige.ruby.module.combat;

import dev.zprestige.ruby.module.Module;
import dev.zprestige.ruby.settings.impl.Slider;
import dev.zprestige.ruby.settings.impl.Switch;
import dev.zprestige.ruby.util.BlockUtil;
import dev.zprestige.ruby.util.InventoryUtil;
import dev.zprestige.ruby.util.Timer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class AntiLavaFag extends Module {
    public final Slider placeDelay = Menu.Slider("Place Delay", 0, 500);
    public final Slider targetRange = Menu.Slider("Target Range", 0.1f, 15.0f);
    public final Slider placeRange = Menu.Slider("Place Range", 0.1f, 6.0f);
    public final Switch packet = Menu.Switch("Packet");
    public final Switch rotate = Menu.Switch("Rotate");
    public final Timer timer = new Timer();
    public final Vec3i[] vec3is = new Vec3i[]{
            new Vec3i(0, 0, -1),
            new Vec3i(0, 0, 1),
            new Vec3i(-1, 0, 0),
            new Vec3i(1, 0, 0),
    };
    public BlockPos targetPos = null;

    @Override
    public void onEnable() {
        timer.setTime(0);
        targetPos = null;
        BlockPos pos = BlockUtil.getPlayerPos();
        for (Vec3i vec3i : vec3is) {
            BlockPos pos1 = pos.add(vec3i);
            if (isPlaceable(pos1) && isValid(pos1.up()) && isValid(pos1.up().up()) && isValid(pos1.up().up().up()) && mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(pos1.up())).stream().filter(entity -> entity instanceof EntityPlayer).collect(Collectors.toCollection(ArrayList::new)).isEmpty()) {
                targetPos = pos1;
                return;
            }
        }
        if (targetPos == null) {
            disableModule("No valid placement(s) found, disabling Anti Lava Fag.");
        }
    }

    @Override
    public void onTick() {
        int netherrackSlot = InventoryUtil.getItemFromHotbar(Item.getItemFromBlock(Blocks.NETHERRACK));
        int pickaxeSlot = InventoryUtil.getItemFromHotbar(Items.DIAMOND_PICKAXE);
        int obsidianSlot = InventoryUtil.getItemFromHotbar(Item.getItemFromBlock(Blocks.OBSIDIAN));
        if (netherrackSlot == -1 || pickaxeSlot == -1 || obsidianSlot == -1) {
            disableModule("Not all materials found, disabling Anti Lava Fag.");
            return;
        }
        if (timer.getTime((long) placeDelay.GetSlider())) {
            Stage stage = searchStage(targetPos);
            if (stage != null) {
                switch (stage) {
                    case PlaceFirstNetherrack:
                        BlockUtil.placeBlockWithSwitch(targetPos.up(), EnumHand.MAIN_HAND, rotate.GetSwitch(), packet.GetSwitch(), netherrackSlot, timer);
                        break;
                    case PlaceSecondNetherrack:
                        BlockUtil.placeBlockWithSwitch(targetPos.up().up(), EnumHand.MAIN_HAND, rotate.GetSwitch(), packet.GetSwitch(), netherrackSlot, timer);
                        break;
                    case PlaceObsidian:
                        BlockUtil.placeBlockWithSwitch(targetPos.up().up().up(), EnumHand.MAIN_HAND, rotate.GetSwitch(), packet.GetSwitch(), obsidianSlot, timer);
                        break;
                    case BreakFirstNetherrack:
                        mc.playerController.onPlayerDamageBlock(targetPos.up().up(), mc.objectMouseOver.sideHit);
                        mc.player.swingArm(EnumHand.MAIN_HAND);
                        break;
                    case BreakSecondNetherrack:
                        mc.playerController.onPlayerDamageBlock(targetPos.up(), mc.objectMouseOver.sideHit);
                        mc.player.swingArm(EnumHand.MAIN_HAND);
                        break;
                    case Crystal:
                        break;
                }
            }
        }
    }

    protected Stage searchStage(BlockPos pos) {
        if (isValid(pos.up()) && !mc.world.getBlockState(pos.up()).getBlock().equals(Blocks.AIR)) {
            return Stage.PlaceFirstNetherrack;
        }
        if (isValid(pos.up().up()) && !mc.world.getBlockState(pos.up()).getBlock().equals(Blocks.AIR)) {
            return Stage.PlaceSecondNetherrack;
        }
        if (isValid(pos.up().up().up())) {
            return Stage.PlaceObsidian;
        }
        if (mc.world.getBlockState(pos.up().up()).getBlock().equals(Blocks.NETHERRACK)) {
            return Stage.BreakFirstNetherrack;
        }
        if (mc.world.getBlockState(pos.up()).getBlock().equals(Blocks.NETHERRACK)) {
            return Stage.BreakSecondNetherrack;
        }
        return Stage.Crystal;
    }

    protected boolean isPlaceable(BlockPos pos) {
        return mc.world.getBlockState(pos).getBlock().equals(Blocks.OBSIDIAN) || mc.world.getBlockState(pos).getBlock().equals(Blocks.BEDROCK);
    }

    protected boolean isValid(BlockPos pos) {
        return !(mc.player.getDistanceSq(pos) / 2 > placeRange.GetSlider()) && (mc.world.getBlockState(pos).getBlock().equals(Blocks.WATER) || mc.world.getBlockState(pos).getBlock().equals(Blocks.FLOWING_WATER) || mc.world.getBlockState(pos).getBlock().equals(Blocks.LAVA) || mc.world.getBlockState(pos).getBlock().equals(Blocks.FLOWING_LAVA));
    }

    protected enum Stage {
        PlaceFirstNetherrack,
        PlaceSecondNetherrack,
        PlaceObsidian,
        BreakFirstNetherrack,
        BreakSecondNetherrack,
        Crystal
    }
}
