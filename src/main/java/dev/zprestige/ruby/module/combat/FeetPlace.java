package dev.zprestige.ruby.module.combat;

import dev.zprestige.ruby.eventbus.annotation.RegisterListener;
import dev.zprestige.ruby.events.PacketEvent;
import dev.zprestige.ruby.module.Module;
import dev.zprestige.ruby.setting.impl.BooleanSetting;
import dev.zprestige.ruby.setting.impl.IntegerSetting;
import dev.zprestige.ruby.setting.impl.ModeSetting;
import dev.zprestige.ruby.setting.impl.ParentSetting;
import dev.zprestige.ruby.util.BlockUtil;
import dev.zprestige.ruby.util.EntityUtil;
import dev.zprestige.ruby.util.InventoryUtil;
import dev.zprestige.ruby.util.Timer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.network.play.server.SPacketBlockBreakAnim;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class FeetPlace extends Module {
    public static FeetPlace Instance;
    public ParentSetting placing = createSetting("Placing");
    public ModeSetting placeMode = createSetting("Place Mode", "Instant", Arrays.asList("Instant", "Gradually", "Linear", "Obscure")).setParent(placing);
    public ModeSetting block = createSetting("Blocks", "Obsidian", Arrays.asList("Obsidian", "EnderChests", "Fallback")).setParent(placing);
    public IntegerSetting placeDelay = createSetting("Place Delay", 30, 0, 500, (Predicate<Integer>) v -> !placeMode.getValue().equals("Instant")).setParent(placing);
    public ParentSetting misc = createSetting("Misc");
    public BooleanSetting inLiquids = createSetting("In Liquids", false).setParent(misc);
    public BooleanSetting packet = createSetting("Packet", false).setParent(misc);
    public BooleanSetting rotate = createSetting("Rotate", false).setParent(misc);
    public BooleanSetting onMoveCancel = createSetting("On Move Cancel", false).setParent(misc);
    public BooleanSetting support = createSetting("Support", false).setParent(misc);
    public BooleanSetting reCalcOnMove = createSetting("Re-Calc On Move", false).setParent(misc);
    public BooleanSetting smartExtend = createSetting("Smart Extend", false).setParent(misc);
    public BooleanSetting hitboxCheck = createSetting("Hitbox Check", false, v -> !smartExtend.getValue()).setParent(misc);
    public BooleanSetting retry = createSetting("Retry", false, v -> placeMode.getValue().equals("Instant")).setParent(misc);
    public IntegerSetting retries = createSetting("Retries", 1, 1, 10, (Predicate<Integer>) v -> placeMode.getValue().equals("Instant") && retry.getValue()).setParent(misc);
    public ArrayList<BlockPos> blockPosList = new ArrayList<>();
    public ArrayList<BlockPos> upperPosList = new ArrayList<>();
    public ArrayList<BlockPos> bottomPosList = new ArrayList<>();
    public ArrayList<BlockPos> extendBlocks = new ArrayList<>();
    public Timer placeTimer = new Timer();
    public BlockPos startPos = null;
    public BlockPos supportPos = null;

    public FeetPlace() {
        Instance = this;
    }

    @Override
    public void onEnable() {
        startPos = BlockUtil.getPlayerPos();
        setup();
    }

    public void setup() {
        BlockPos pos = BlockUtil.getPlayerPos();
        addPossesMainList(pos.down(), pos.down().north(), pos.down().east(), pos.down().south(), pos.down().west(), pos.north(), pos.east(), pos.south(), pos.west());
        addPossesUpperList(pos.north(), pos.east(), pos.south(), pos.west());
        addPossesBottomList(pos.down(), pos.down().north(), pos.down().east(), pos.down().south(), pos.down().west());
    }

    public void addPossesMainList(BlockPos... blockPos) {
        blockPosList.clear();
        blockPosList.addAll(Arrays.asList(blockPos));
    }

    public void addPossesBottomList(BlockPos... blockPos) {
        bottomPosList.clear();
        bottomPosList.addAll(Arrays.asList(blockPos));
    }

    public void addPossesUpperList(BlockPos... blockPos) {
        upperPosList.clear();
        upperPosList.addAll(Arrays.asList(blockPos));
    }

    @Override
    public void onTick() {
        if ((startPos != null && mc.player.getDistanceSq(startPos) > 1.0f) || mc.player.stepHeight > 0.6f || (startPos != null && mc.player.posY > startPos.y) || !mc.player.onGround) {
            disableModule();
            return;
        }
        if (onMoveCancel.getValue() && EntityUtil.isMoving())
            return;
        if (!placeTimer.getTime(placeDelay.getValue()))
            return;
        int slot = -1;
        switch (block.getValue()) {
            case "Obsidian":
                slot = InventoryUtil.getItemFromHotbar(Item.getItemFromBlock(Blocks.OBSIDIAN));
                break;
            case "EnderChests":
                slot = InventoryUtil.getItemFromHotbar(Item.getItemFromBlock(Blocks.ENDER_CHEST));
                break;
            case "Fallback":
                slot = InventoryUtil.getItemFromHotbar(Item.getItemFromBlock(Blocks.OBSIDIAN));
                if (slot == -1)
                    slot = InventoryUtil.getItemFromHotbar(Item.getItemFromBlock(Blocks.ENDER_CHEST));
                break;
        }
        if (slot == -1) {
            disableModule("No Blocks found in hotbar, disabling Feet Place.");
            return;
        }
        int finalSlot = slot;
        if (reCalcOnMove.getValue() && EntityUtil.isMoving()) {
            BlockPos pos = BlockUtil.getPlayerPos();
            addPossesMainList(pos.down(), pos.down().north(), pos.down().east(), pos.down().south(), pos.down().west(), pos.north(), pos.east(), pos.south(), pos.west());
        }
        switch (placeMode.getValue()) {
            case "Instant":
                for (int i = 0; i < (retry.getValue() ? retries.getValue() : 1); ++i) {
                    blockPosList.stream().filter(blockPos -> canPlace(blockPos) && ((!smartExtend.getValue() && !hitboxCheck.getValue()) || !mc.player.getEntityBoundingBox().intersects(new AxisAlignedBB(blockPos)))).forEach(blockPos -> BlockUtil.placeBlockWithSwitch(blockPos, EnumHand.MAIN_HAND, rotate.getValue(), packet.getValue(), finalSlot));
                    if (smartExtend.getValue()) {
                        extendBlocks.clear();
                        addExtendedPosses();
                        if (!extendBlocks.isEmpty()) {
                            extendBlocks.stream().filter(this::canPlace).forEach(blockPos -> BlockUtil.placeBlockWithSwitch(blockPos, EnumHand.MAIN_HAND, rotate.getValue(), packet.getValue(), finalSlot));
                        }
                    }
                }
                break;
            case "Gradually":
                if (getTargetPos() != null)
                    BlockUtil.placeBlockWithSwitch(getTargetPos(), EnumHand.MAIN_HAND, rotate.getValue(), packet.getValue(), slot, placeTimer);
                break;
            case "Linear":
                if (bottomBlocks() != null && !bottomBlocks().isEmpty())
                    bottomBlocks().forEach(blockPos -> BlockUtil.placeBlockWithSwitch(blockPos, EnumHand.MAIN_HAND, rotate.getValue(), packet.getValue(), finalSlot, placeTimer));
                else if (upperBlocks() != null && !upperBlocks().isEmpty())
                    upperBlocks().forEach(blockPos -> BlockUtil.placeBlockWithSwitch(blockPos, EnumHand.MAIN_HAND, rotate.getValue(), packet.getValue(), finalSlot, placeTimer));
                break;
            case "Obscure":
                if (obscureBottomToUpper() != null)
                    BlockUtil.placeBlockWithSwitch(obscureBottomToUpper(), EnumHand.MAIN_HAND, rotate.getValue(), packet.getValue(), slot, placeTimer);
                break;
        }
        if (!support.getValue())
            return;
        if (supportPos != null) {
            BlockUtil.placeBlockWithSwitch(supportPos, EnumHand.MAIN_HAND, rotate.getValue(), packet.getValue(), slot, placeTimer);
            if (!canPlace(supportPos))
                supportPos = null;
        }
    }

    public boolean canPlace(BlockPos pos) {
        return mc.world.getBlockState(pos).getBlock().equals(Blocks.AIR) || (inLiquids.getValue() && ((mc.world.getBlockState(pos).getBlock().equals(Blocks.WATER) || mc.world.getBlockState(pos).getBlock().equals(Blocks.FLOWING_WATER)) || (mc.world.getBlockState(pos).getBlock().equals(Blocks.LAVA) || mc.world.getBlockState(pos).getBlock().equals(Blocks.FLOWING_LAVA))));
    }

    public void addExtendedPosses() {
        for (BlockPos pos : blockPosList) {
            AxisAlignedBB bb = new AxisAlignedBB(pos);
            if (mc.player.getEntityBoundingBox().intersects(bb)) {
                if (!mc.player.getEntityBoundingBox().intersects(new AxisAlignedBB(pos.north()))) {
                    if (isPosSurroundedByBlocks(pos.down().north()) && canPlace(pos.down().north()))
                        extendBlocks.add(pos.down().north());
                    if (isPosSurroundedByBlocks(pos.north()) && canPlace(pos.north()))
                        extendBlocks.add(pos.north());
                }
                if (!mc.player.getEntityBoundingBox().intersects(new AxisAlignedBB(pos.east()))) {
                    if (isPosSurroundedByBlocks(pos.down().east()) && canPlace(pos.down().east()))
                        extendBlocks.add(pos.down().east());
                    if (isPosSurroundedByBlocks(pos.east()) && canPlace(pos.east()))
                        extendBlocks.add(pos.east());
                }
                if (!mc.player.getEntityBoundingBox().intersects(new AxisAlignedBB(pos.south()))) {
                    if (isPosSurroundedByBlocks(pos.down().south()) && canPlace(pos.down().south()))
                        extendBlocks.add(pos.down().south());
                    if (isPosSurroundedByBlocks(pos.south()) && canPlace(pos.south()))
                        extendBlocks.add(pos.south());
                }
                if (!mc.player.getEntityBoundingBox().intersects(new AxisAlignedBB(pos.west()))) {
                    if (isPosSurroundedByBlocks(pos.down().west()) && canPlace(pos.down().west()))
                        extendBlocks.add(pos.down().west());
                    if (isPosSurroundedByBlocks(pos.west()) && canPlace(pos.west()))
                        extendBlocks.add(pos.west());
                }
            }
        }
    }

    public boolean isPosSurroundedByBlocks(BlockPos blockPos) {
        return !canPlace(blockPos.north()) || !canPlace(blockPos.east()) || !canPlace(blockPos.south()) || !canPlace(blockPos.west()) || !canPlace(blockPos.down()) || !canPlace(blockPos.up());
    }

    @RegisterListener
    public void onPacketReceive(PacketEvent.PacketReceiveEvent event) {
        if (!(event.getPacket() instanceof SPacketBlockBreakAnim))
            return;
        SPacketBlockBreakAnim packet = (SPacketBlockBreakAnim) event.getPacket();
        if (upperPosList.contains(packet.getPosition())) {
            if (packet.getPosition().equals(startPos.north()) && !canPlace(startPos.north()) && canPlace(packet.getPosition().north())) {
                supportPos = packet.getPosition().north();
                return;
            }
            if (packet.getPosition().equals(startPos.east()) && !canPlace(startPos.east()) && canPlace(packet.getPosition().east())) {
                supportPos = packet.getPosition().east();
                return;
            }
            if (packet.getPosition().equals(startPos.south()) && !canPlace(startPos.south()) && canPlace(packet.getPosition().south())) {
                supportPos = packet.getPosition().south();
                return;
            }
            if (packet.getPosition().equals(startPos.west()) && !canPlace(startPos.west()) && canPlace(packet.getPosition().west())) {
                supportPos = packet.getPosition().west();
            }
        }
    }

    public List<BlockPos> bottomBlocks() {
        List<BlockPos> posList = bottomPosList.stream().filter(blockPos -> canPlace(blockPos) && ((hitboxCheck.getValue() && !mc.world.getEntitiesWithinAABBExcludingEntity(null, new AxisAlignedBB(blockPos)).isEmpty()) || smartExtend.getValue())).collect(Collectors.toList());
        if (!posList.isEmpty())
            return posList;
        return null;
    }

    public List<BlockPos> upperBlocks() {
        List<BlockPos> posList = upperPosList.stream().filter(blockPos -> canPlace(blockPos) && ((hitboxCheck.getValue() && !mc.world.getEntitiesWithinAABBExcludingEntity(null, new AxisAlignedBB(blockPos)).isEmpty()) || smartExtend.getValue())).collect(Collectors.toList());
        if (!posList.isEmpty())
            return posList;
        return null;
    }

    public BlockPos obscureBottomToUpper() {
        List<BlockPos> upper = upperPosList.stream().filter(blockPos -> canPlace(blockPos) && ((hitboxCheck.getValue() && !mc.world.getEntitiesWithinAABBExcludingEntity(null, new AxisAlignedBB(blockPos)).isEmpty()) || smartExtend.getValue())).collect(Collectors.toList());
        List<BlockPos> bottom = bottomPosList.stream().filter(blockPos -> canPlace(blockPos) && ((hitboxCheck.getValue() && !mc.world.getEntitiesWithinAABBExcludingEntity(null, new AxisAlignedBB(blockPos)).isEmpty()) || smartExtend.getValue())).collect(Collectors.toList());
        if (!bottom.isEmpty())
            return bottom.stream().findFirst().orElse(null);
        else if (!upper.isEmpty())
            return upper.stream().findFirst().orElse(null);
        return null;
    }

    public BlockPos getTargetPos() {
        return blockPosList.stream().filter(blockPos -> canPlace(blockPos) && ((hitboxCheck.getValue() && !mc.world.getEntitiesWithinAABBExcludingEntity(null, new AxisAlignedBB(blockPos)).isEmpty()) || smartExtend.getValue())).findFirst().orElse(null);
    }
}
