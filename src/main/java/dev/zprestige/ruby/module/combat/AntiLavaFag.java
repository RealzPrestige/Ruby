package dev.zprestige.ruby.module.combat;

import dev.zprestige.ruby.module.Category;
import dev.zprestige.ruby.module.Module;
import dev.zprestige.ruby.module.ModuleInfo;
import dev.zprestige.ruby.setting.impl.BooleanSetting;
import dev.zprestige.ruby.setting.impl.FloatSetting;
import dev.zprestige.ruby.setting.impl.IntegerSetting;
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

@ModuleInfo(name = "AntiLavaFag", description = "Fuck lava pvpers", category = Category.Combat)
public class AntiLavaFag extends Module {
    protected final IntegerSetting placeDelay = createSetting("Place Delay", 50, 0, 500);
    protected final FloatSetting targetRange = createSetting("Target Range", 10.0f, 0.1f, 15.0f);
    protected final FloatSetting placeRange = createSetting("Place Range", 5.0f, 0.1f, 6.0f);
    protected final BooleanSetting packet = createSetting("Packet", false);
    protected final BooleanSetting rotate = createSetting("Rotate", false);
    protected final Timer timer = new Timer();
    protected BlockPos targetPos = null;
    protected final Vec3i[] vec3is = new Vec3i[]{
            new Vec3i(0, 0, -1),
            new Vec3i(0, 0, 1),
            new Vec3i(-1, 0, 0),
            new Vec3i(1, 0, 0),
    };

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
        if (timer.getTime(placeDelay.getValue())) {
            Stage stage = searchStage(targetPos);
            if (stage != null) {
                switch (stage) {
                    case PlaceFirstNetherrack:
                        BlockUtil.placeBlockWithSwitch(targetPos.up(), EnumHand.MAIN_HAND, rotate.getValue(), packet.getValue(), netherrackSlot, timer);
                        break;
                    case PlaceSecondNetherrack:
                        BlockUtil.placeBlockWithSwitch(targetPos.up().up(), EnumHand.MAIN_HAND, rotate.getValue(), packet.getValue(), netherrackSlot, timer);
                        break;
                    case PlaceObsidian:
                        BlockUtil.placeBlockWithSwitch(targetPos.up().up().up(), EnumHand.MAIN_HAND, rotate.getValue(), packet.getValue(), obsidianSlot, timer);
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
        return !(mc.player.getDistanceSq(pos) / 2 > placeRange.getValue()) && (mc.world.getBlockState(pos).getBlock().equals(Blocks.WATER) || mc.world.getBlockState(pos).getBlock().equals(Blocks.FLOWING_WATER) || mc.world.getBlockState(pos).getBlock().equals(Blocks.LAVA) || mc.world.getBlockState(pos).getBlock().equals(Blocks.FLOWING_LAVA));
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
