package dev.zprestige.ruby.module.combat;

import dev.zprestige.ruby.eventbus.annotation.RegisterListener;
import dev.zprestige.ruby.events.PacketEvent;
import dev.zprestige.ruby.module.Module;
import dev.zprestige.ruby.settings.impl.ComboBox;
import dev.zprestige.ruby.settings.impl.Parent;
import dev.zprestige.ruby.settings.impl.Slider;
import dev.zprestige.ruby.settings.impl.Switch;
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
import java.util.stream.Collectors;

public class FeetPlace extends Module {
    public static FeetPlace Instance;
    public final Parent placing = Menu.Parent("Placing");
    public final ComboBox placeMode = Menu.ComboBox("Place Mode", new String[]{"Instant", "Gradually", "Linear", "Obscure"}).parent(placing);
    public final ComboBox block = Menu.ComboBox("Blocks", new String[]{"Obsidian", "EnderChests", "Fallback"}).parent(placing);
    public final Slider placeDelay = Menu.Slider("Place Delay", 0, 500).parent(placing);
    public final Parent misc = Menu.Parent("Misc");
    public final Switch inLiquids = Menu.Switch("In Liquids").parent(misc);
    public final Switch packet = Menu.Switch("Packet").parent(misc);
    public final Switch rotate = Menu.Switch("Rotate").parent(misc);
    public final Switch onMoveCancel = Menu.Switch("On Move Cancel").parent(misc);
    public final Switch support = Menu.Switch("Support").parent(misc);
    public final Switch reCalcOnMove = Menu.Switch("Re-Calc On Move").parent(misc);
    public final Switch smartExtend = Menu.Switch("Smart Extend").parent(misc);
    public final Slider smartExtendSize = Menu.Slider("Smart Extend Size", 0.0f, 10.0f).parent(misc);
    public final Switch hitboxCheck = Menu.Switch("Hitbox Check").parent(misc);
    public final Switch retry = Menu.Switch("Retry").parent(misc);
    public final Slider retries = Menu.Slider("Retries", 1, 10).parent(misc);
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
        if (onMoveCancel.GetSwitch() && EntityUtil.isMoving())
            return;
        if (!placeTimer.getTime((long) placeDelay.GetSlider()))
            return;
        int slot = -1;
        switch (block.GetCombo()) {
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
        if (reCalcOnMove.GetSwitch() && EntityUtil.isMoving()) {
            BlockPos pos = BlockUtil.getPlayerPos();
            addPossesMainList(pos.down(), pos.down().north(), pos.down().east(), pos.down().south(), pos.down().west(), pos.north(), pos.east(), pos.south(), pos.west());
        }
        switch (placeMode.GetCombo()) {
            case "Instant":
                for (int i = 0; i < (retry.GetSwitch() ? retries.GetSlider() : 1); ++i) {
                    blockPosList.stream().filter(blockPos -> canPlace(blockPos) && ((!smartExtend.GetSwitch() && !hitboxCheck.GetSwitch()) || !mc.player.getEntityBoundingBox().intersects(new AxisAlignedBB(blockPos)))).forEach(blockPos -> BlockUtil.placeBlockWithSwitch(blockPos, EnumHand.MAIN_HAND, rotate.GetSwitch(), packet.GetSwitch(), finalSlot));
                    if (smartExtend.GetSwitch()) {
                        extendBlocks.clear();
                        addExtendedPosses();
                        if (!extendBlocks.isEmpty()) {
                            extendBlocks.stream().filter(this::canPlace).forEach(blockPos -> BlockUtil.placeBlockWithSwitch(blockPos, EnumHand.MAIN_HAND, rotate.GetSwitch(), packet.GetSwitch(), finalSlot));
                        }
                    }
                }
                break;
            case "Gradually":
                if (getTargetPos() != null)
                    BlockUtil.placeBlockWithSwitch(getTargetPos(), EnumHand.MAIN_HAND, rotate.GetSwitch(), packet.GetSwitch(), slot, placeTimer);
                break;
            case "Linear":
                if (bottomBlocks() != null && !bottomBlocks().isEmpty())
                    bottomBlocks().forEach(blockPos -> BlockUtil.placeBlockWithSwitch(blockPos, EnumHand.MAIN_HAND, rotate.GetSwitch(), packet.GetSwitch(), finalSlot, placeTimer));
                else if (upperBlocks() != null && !upperBlocks().isEmpty())
                    upperBlocks().forEach(blockPos -> BlockUtil.placeBlockWithSwitch(blockPos, EnumHand.MAIN_HAND, rotate.GetSwitch(), packet.GetSwitch(), finalSlot, placeTimer));
                break;
            case "Obscure":
                if (obscureBottomToUpper() != null)
                    BlockUtil.placeBlockWithSwitch(obscureBottomToUpper(), EnumHand.MAIN_HAND, rotate.GetSwitch(), packet.GetSwitch(), slot, placeTimer);
                break;
        }
        if (!support.GetSwitch())
            return;
        if (supportPos != null) {
            BlockUtil.placeBlockWithSwitch(supportPos, EnumHand.MAIN_HAND, rotate.GetSwitch(), packet.GetSwitch(), slot, placeTimer);
            if (!canPlace(supportPos))
                supportPos = null;
        }
    }

    public boolean canPlace(BlockPos pos) {
        return mc.world.getBlockState(pos).getBlock().equals(Blocks.AIR) || (inLiquids.GetSwitch() && ((mc.world.getBlockState(pos).getBlock().equals(Blocks.WATER) || mc.world.getBlockState(pos).getBlock().equals(Blocks.FLOWING_WATER)) || (mc.world.getBlockState(pos).getBlock().equals(Blocks.LAVA) || mc.world.getBlockState(pos).getBlock().equals(Blocks.FLOWING_LAVA))));
    }

    public void addExtendedPosses() {
        for (BlockPos pos : blockPosList) {
            final AxisAlignedBB bb = new AxisAlignedBB(pos).grow(smartExtendSize.GetSlider() / 20.0f);
            final AxisAlignedBB playerBox = mc.player.getEntityBoundingBox();
            if (playerBox.intersects(bb)) {
                if (!playerBox.intersects(new AxisAlignedBB(pos.north()))) {
                    if (isPosSurroundedByBlocks(pos.down().north()) && canPlace(pos.down().north()))
                        extendBlocks.add(pos.down().north());
                    if (isPosSurroundedByBlocks(pos.north()) && canPlace(pos.north()))
                        extendBlocks.add(pos.north());
                }
                if (!playerBox.intersects(new AxisAlignedBB(pos.east()))) {
                    if (isPosSurroundedByBlocks(pos.down().east()) && canPlace(pos.down().east()))
                        extendBlocks.add(pos.down().east());
                    if (isPosSurroundedByBlocks(pos.east()) && canPlace(pos.east()))
                        extendBlocks.add(pos.east());
                }
                if (!playerBox.intersects(new AxisAlignedBB(pos.south()))) {
                    if (isPosSurroundedByBlocks(pos.down().south()) && canPlace(pos.down().south()))
                        extendBlocks.add(pos.down().south());
                    if (isPosSurroundedByBlocks(pos.south()) && canPlace(pos.south()))
                        extendBlocks.add(pos.south());
                }
                if (!playerBox.intersects(new AxisAlignedBB(pos.west()))) {
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
        List<BlockPos> posList = bottomPosList.stream().filter(blockPos -> canPlace(blockPos) && ((hitboxCheck.GetSwitch() && !mc.world.getEntitiesWithinAABBExcludingEntity(null, new AxisAlignedBB(blockPos)).isEmpty()) || smartExtend.GetSwitch())).collect(Collectors.toList());
        if (!posList.isEmpty())
            return posList;
        return null;
    }

    public List<BlockPos> upperBlocks() {
        List<BlockPos> posList = upperPosList.stream().filter(blockPos -> canPlace(blockPos) && ((hitboxCheck.GetSwitch() && !mc.world.getEntitiesWithinAABBExcludingEntity(null, new AxisAlignedBB(blockPos)).isEmpty()) || smartExtend.GetSwitch())).collect(Collectors.toList());
        if (!posList.isEmpty())
            return posList;
        return null;
    }

    public BlockPos obscureBottomToUpper() {
        List<BlockPos> upper = upperPosList.stream().filter(blockPos -> canPlace(blockPos) && ((hitboxCheck.GetSwitch() && !mc.world.getEntitiesWithinAABBExcludingEntity(null, new AxisAlignedBB(blockPos)).isEmpty()) || smartExtend.GetSwitch())).collect(Collectors.toList());
        List<BlockPos> bottom = bottomPosList.stream().filter(blockPos -> canPlace(blockPos) && ((hitboxCheck.GetSwitch() && !mc.world.getEntitiesWithinAABBExcludingEntity(null, new AxisAlignedBB(blockPos)).isEmpty()) || smartExtend.GetSwitch())).collect(Collectors.toList());
        if (!bottom.isEmpty())
            return bottom.stream().findFirst().orElse(null);
        else if (!upper.isEmpty())
            return upper.stream().findFirst().orElse(null);
        return null;
    }

    public BlockPos getTargetPos() {
        return blockPosList.stream().filter(blockPos -> canPlace(blockPos) && ((hitboxCheck.GetSwitch() && !mc.world.getEntitiesWithinAABBExcludingEntity(null, new AxisAlignedBB(blockPos)).isEmpty()) || smartExtend.GetSwitch())).findFirst().orElse(null);
    }
}
