package dev.zprestige.ruby.module.combat;

import dev.zprestige.ruby.Ruby;
import dev.zprestige.ruby.events.PacketEvent;
import dev.zprestige.ruby.manager.HoleManager;
import dev.zprestige.ruby.module.Category;
import dev.zprestige.ruby.module.Module;
import dev.zprestige.ruby.module.ModuleInfo;
import dev.zprestige.ruby.module.movement.NoSlow;
import dev.zprestige.ruby.module.movement.Speed;
import dev.zprestige.ruby.module.movement.Step;
import dev.zprestige.ruby.module.player.FastExp;
import dev.zprestige.ruby.module.player.PacketMine;
import dev.zprestige.ruby.util.BlockUtil;
import dev.zprestige.ruby.util.EntityUtil;
import dev.zprestige.ruby.util.InventoryUtil;
import dev.zprestige.ruby.util.Timer;
import net.minecraft.client.renderer.DestroyBlockProgress;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.init.PotionTypes;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTippedArrow;
import net.minecraft.network.play.client.CPacketPlayerTryUseItem;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.network.play.server.SPacketPlayerPosLook;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@ModuleInfo(name = "Robot", category = Category.Combat, description = "robot")
public class Robot extends Module {
    public double prevHealth = 0.0, totalDamagePerSecond1 = 0.0, prevPosX = 0.0, prevPosZ = 0.0;
    public int prevGapples = 0, packets = 1, targetObsidian = 11, targetExpPercent, prevChorus = 0, xt = 0, zt = 0, st = 0, bowTicks = 0;
    public boolean isEating = false, isSafe = false, isExping, enabledFastExp = false, handOnly = false, hasMined = false, enabledPacketMine = false, enabledAura = false, isMoving = false, enabledStep = false, enabledTrap = false, value = false, didChorus = false, switchedToSword = false, switchedPickaxe = false, preGappled = false, needsUnSneak = false, needsOnGround = false, dropped = false, landed = false, forcedHole = false, cantStep = false;
    public String mode = "", triggerMode = "";
    HashMap<Long, Double> damagePerSecond = new HashMap<>();
    public HoleOperation holeOperation = null;
    public Timer mineTimer = new Timer();
    public Timer announceTimer = new Timer();
    public Timer holeTimer = new Timer();
    public BlockPos minedPos = null, lastHole = null, nextHole = null;

    @Override
    public void onEnable() {
        prevHealth = mc.player.getHealth() + mc.player.getAbsorptionAmount();
        totalDamagePerSecond1 = 0.0;
        prevPosX = 0.0;
        prevPosZ = 0.0;
        prevGapples = mc.player.inventory.mainInventory.stream().filter(itemStack -> (itemStack.getItem() == Items.GOLDEN_APPLE)).mapToInt(ItemStack::getCount).sum() + (mc.player.getHeldItemOffhand().getItem() == Items.GOLDEN_APPLE ? mc.player.getHeldItemOffhand().getCount() : 0);
        isEating = false;
        isSafe = false;
        isExping = false;
        enabledFastExp = false;
        handOnly = false;
        hasMined = false;
        enabledPacketMine = false;
        isMoving = false;
        enabledStep = false;
        enabledTrap = false;
        didChorus = false;
        switchedToSword = false;
        switchedPickaxe = false;
        preGappled = false;
        needsUnSneak = false;
        needsOnGround = false;
        dropped = false;
        landed = false;
        forcedHole = false;
        cantStep = false;
        mode = "";
        triggerMode = "";
        damagePerSecond.clear();
        packets = 1;
        targetObsidian = 11;
        prevChorus = 0;
        xt = 0;
        zt = 0;
        st = 0;
        bowTicks = 0;
        mineTimer.setTime(0);
        announceTimer.setTime(0);
        holeTimer.setTime(0);
        minedPos = null;
        lastHole = null;
        nextHole = null;
        NoSlow noSlow = NoSlow.Instance;
        value = noSlow.guiMove.getValue();
        noSlow.guiMove.setValue(false);
        if (!SimpleCa.Instance.isEnabled())
            SimpleCa.Instance.enableModule();
    }

    @Override
    public void onDisable() {
        mc.gameSettings.keyBindUseItem.pressed = false;
        resetMovement();
        NoSlow.Instance.guiMove.setValue(value);
        Filler.Instance.disableModule();
        if (SimpleCa.Instance.isEnabled())
            SimpleCa.Instance.disableModule();
    }

    @Override
    public void onTick() {
        mc.player.rotationYaw = 180.0f;
        mc.player.rotationPitch = 0.0f;
        if (mc.player.ticksExisted > 20 && mc.player.posY >= 122) {
            if (mc.player.getHeldItemMainhand().getItem().equals(Items.AIR)) {
                mc.player.sendChatMessage("/kit bot");
            }
            if (mc.player.posZ > -5.4) {
                mc.gameSettings.keyBindForward.pressed = true;
            } else {
                mc.gameSettings.keyBindForward.pressed = false;
                if (mc.player.collidedHorizontally) {
                    if (!Step.Instance.isEnabled())
                        Step.Instance.enableModule();
                }
                mc.gameSettings.keyBindRight.pressed = true;
                if (mc.world.getBlockState(BlockUtil.getPlayerPos().down()).getBlock().equals(Blocks.AIR))
                    dropped = true;
            }
            return;
        } else if (dropped) {
            resetMovement();
            if (mc.player.onGround) {
                landed = true;
            }
        }
        if (landed) {
            if (mc.player.onGround) {
                moveToCenter();
            }
            toggleFeetPlace();
            landed = false;
            dropped = false;
        }
        if (needsUnSneak) {
            st++;
            if (st >= 10) {
                mc.gameSettings.keyBindSneak.pressed = false;
                needsUnSneak = false;
            }
        }
        cantStep = FeetPlace.Instance.isEnabled() && (holeOperation != null && !holeOperation.equals(HoleOperation.RunOut));
        isSafe = BlockUtil.isPlayerSafe2(mc.player);
        double health = mc.player.getHealth() + mc.player.getAbsorptionAmount();
        EntityPlayer entityPlayer = EntityUtil.getTarget(200.0f);
        handleDamage();
        if (isEating)
            setUseItemUnpressedIfNeeded();
        if (entityPlayer == null) {
            resetMovement();
            return;
        }
        if (mc.player.getDistance(entityPlayer) > 7.0f) {
            preGappled = false;
        }
        handleEat();
        totalDamagePerSecond1 = damagePerSecond.values().stream().mapToDouble(aDouble -> aDouble).sum();
        if (isMoving) {
            if (!Step.Instance.isEnabled() && !cantStep) {
                Step.Instance.enableModule();
                enabledStep = true;
            }
        } else {
            if (enabledStep) {
                Step.Instance.disableModule();
                enabledStep = false;
            }
        }
        boolean needsToGoIntoHole = (mc.player.getDistance(entityPlayer) < 10.0f && !BlockUtil.isPlayerSafe2(mc.player) && health < 17.5f) || totalDamagePerSecond1 > 10.0f;
        boolean isTowering = isTowering(entityPlayer);
        if (isSafe) {
            forcedHole = false;
            xt = 0;
            zt = 0;
            holeOperation = getHoleOperation(entityPlayer);
            lastHole = BlockUtil.getPlayerPos();
            performHoleOperation(holeOperation, entityPlayer);
        } else {
            if (prevPosX != 0.0 && prevPosZ != 0.0) {
                int i = 0;
                if (mc.player.posX == prevPosX) {
                    xt++;
                    i++;
                }
                if (mc.player.posZ == prevPosZ) {
                    zt++;
                    i++;
                }
                if (i == 2 && xt >= 5 && zt >= 5) {
                    toggleFeetPlace();
                    xt = 0;
                    zt = 0;
                }
            }
            holeTimer.setTime(0);
            Aura aura = Aura.Instance;
            Trap trap = Trap.Instance;
            if (aura.isEnabled() && enabledAura) {
                aura.disableModule();
                enabledAura = false;
            }
            if (trap.isEnabled() && enabledTrap) {
                trap.disableModule();
                enabledTrap = false;
            }
            if (Filler.Instance.isEnabled()) {
                Filler.Instance.disableModule();
            }
            needsOnGround = health <= 17.5f;
            if (needsOnGround) {
                Speed.Instance.speedMode.setValue("OnGround");
            }
            boolean isFilled = nextHole != null && !mc.world.getBlockState(nextHole).getBlock().equals(Blocks.AIR);
            nextHole = getNextHole(entityPlayer, false, 0.0);
            if (needsToGoIntoHole || forcedHole || isTowering) {
                BlockPos newNextHole = getNextHole(entityPlayer, true, 10.0);
                if (newNextHole != null) {
                    nextHole = newNextHole;
                    forcedHole = true;
                }
            }
            if (nextHole != null) {
                if (isFilled || nextHole.equals(BlockUtil.getPlayerPos())) {
                    toggleFeetPlace();
                    resetMovement();
                } else {
                    moveToNextHole(nextHole);
                }
            }
        }
        prevPosX = mc.player.posX;
        prevPosZ = mc.player.posZ;
    }

    public void toggleFeetPlace() {
        Speed.Instance.speedMode.setValue("OnGround");
        mc.player.setVelocity(0, mc.player.motionY, 0);
        Step.Instance.disableModule();
        if (mc.player.onGround && !FeetPlace.Instance.isEnabled()) {
            FeetPlace.Instance.enableModule();
            resetMovement();
        }
    }

    public void resetMovement() {
        mc.gameSettings.keyBindForward.pressed = false;
        mc.gameSettings.keyBindBack.pressed = false;
        mc.gameSettings.keyBindRight.pressed = false;
        mc.gameSettings.keyBindLeft.pressed = false;
    }

    public void moveToNextHole(BlockPos pos1) {
        BlockPos pos = pos1.up();
        Speed speed = Speed.Instance;
        if (!speed.isEnabled()) {
            speed.enableModule();
        }
        BlockPos excludeYPos = new BlockPos(pos.getX(), mc.player.posY, pos.getZ());
        if (mc.player.getDistanceSq(excludeYPos) < 6.0f) {
            speed.speedMode.setValue("OnGround");
        } else if (!needsOnGround) {
            speed.speedMode.setValue("Strafe");
        }
        AxisAlignedBB bb = new AxisAlignedBB(pos).shrink(0.5);
        if (mc.player.getDistanceSq(excludeYPos) < 4.0f && Step.Instance.isEnabled()) {
            Step.Instance.disableModule();
        }
        if (mc.player.posZ > bb.minZ + 0.125) {
            mc.gameSettings.keyBindForward.pressed = true;
            isMoving = true;
        } else {
            mc.gameSettings.keyBindForward.pressed = false;
        }
        if (mc.player.posZ < bb.minZ - 0.125) {
            mc.gameSettings.keyBindBack.pressed = true;
            isMoving = true;
        } else {
            mc.gameSettings.keyBindBack.pressed = false;
        }
        if (mc.player.posX > bb.minX + 0.125) {
            mc.gameSettings.keyBindLeft.pressed = true;
            isMoving = true;
        } else {
            mc.gameSettings.keyBindLeft.pressed = false;
        }
        if (mc.player.posX < bb.minX - 0.125) {
            mc.gameSettings.keyBindRight.pressed = true;
            isMoving = true;
        } else {
            mc.gameSettings.keyBindRight.pressed = false;
        }
    }

    public boolean moveOutHole() {
        BlockPos lastHole = BlockUtil.getPlayerPos().up();
        if (canEnter(lastHole.down())) {
            if ((isAir(lastHole.up()) && isAir(lastHole.north()) && isAir(lastHole.north().up())) || (isAir(lastHole.up()) && !isAir(lastHole.north()) && isAir(lastHole.north().up()) && isAir(lastHole.north().up().up()) && isAir(lastHole.up().up()))) {
                mc.gameSettings.keyBindForward.pressed = true;
                return true;
            }
            if ((isAir(lastHole.up()) && isAir(lastHole.east()) && isAir(lastHole.east().up())) || (isAir(lastHole.up()) && !isAir(lastHole.east()) && isAir(lastHole.east().up()) && isAir(lastHole.east().up().up()) && isAir(lastHole.up().up()))) {
                mc.gameSettings.keyBindRight.pressed = true;
                return true;
            }
            if ((isAir(lastHole.up()) && isAir(lastHole.south()) && isAir(lastHole.south().up())) || (isAir(lastHole.up()) && !isAir(lastHole.south()) && isAir(lastHole.south().up()) && isAir(lastHole.south().up().up()) && isAir(lastHole.up().up()))) {
                mc.gameSettings.keyBindBack.pressed = true;
                return true;
            }
            if ((isAir(lastHole.up()) && isAir(lastHole.west()) && isAir(lastHole.west().up())) || (isAir(lastHole.up()) && !isAir(lastHole.west()) && isAir(lastHole.west().up()) && isAir(lastHole.west().up().up()) && isAir(lastHole.up().up()))) {
                mc.gameSettings.keyBindLeft.pressed = true;
                return true;
            }
        }
        return false;
    }

    public Vec3d getCenter(double posX, double posY, double posZ) {
        double x = Math.floor(posX) + 0.5;
        double y = Math.floor(posY);
        double z = Math.floor(posZ) + 0.5;
        return new Vec3d(x, y, z);
    }

    public void moveToCenter() {
        if (mc.player.onGround) {
            Vec3d center = getCenter(mc.player.posX, mc.player.posY, mc.player.posZ);
            if (mc.player.getDistanceSq(new BlockPos(center.x, center.y, center.z)) > 0.1f)
                mc.player.setPosition(center.x, center.y, center.z);
        }
    }

    public BlockPos getNextHole(EntityPlayer entityPlayer, boolean force, double forceRadius) {
        ArrayList<HoleManager.HolePos> allHoles = Ruby.holeManager.holes;
        if (!allHoles.isEmpty()) {
            TreeMap<Double, BlockPos> posTreeMap;
            if (force) {
                posTreeMap = allHoles.stream().filter(holePos -> canEnter(holePos.pos) && (lastHole != null && !lastHole.equals(holePos.pos)) && mc.player.getDistanceSq(holePos.pos) < (forceRadius * forceRadius)).collect(Collectors.toMap(holePos -> mc.player.getDistanceSq(holePos.pos), holePos -> holePos.pos, (a, b) -> b, TreeMap::new));
            } else {
                posTreeMap = allHoles.stream().filter(holePos -> canEnter(holePos.pos) && (lastHole != null && !lastHole.equals(holePos.pos)) && entityPlayer.getDistanceSq(holePos.pos) > 4.0f && entityPlayer.getDistanceSq(holePos.pos) < 25.0f).collect(Collectors.toMap(holePos -> entityPlayer.getDistanceSq(holePos.pos), holePos -> holePos.pos, (a, b) -> b, TreeMap::new));
            }
            if (!posTreeMap.isEmpty())
                return posTreeMap.firstEntry().getValue();
        }
        return null;
    }

    public void performHoleOperation(HoleOperation holeOperation, EntityPlayer entityPlayer) {
        if (!FeetPlace.Instance.isEnabled())
            FeetPlace.Instance.enableModule();
        Aura aura = Aura.Instance;
        FastExp fastExp = FastExp.Instance;
        Trap trap = Trap.Instance;
        if (isExping && !holeOperation.equals(HoleOperation.Exp)) {
            mc.gameSettings.keyBindUseItem.pressed = false;
            isExping = false;
            fastExp.mode.setValue(mode);
            fastExp.triggerMode.setValue(triggerMode);
            fastExp.handOnly.setValue(handOnly);
            fastExp.packets.setValue(packets);
            if (enabledFastExp) {
                fastExp.disableModule();
            }
        }
        if (!holeOperation.equals(HoleOperation.RunOut)) {
            resetMovement();
            if (enabledStep) {
                Step.Instance.disableModule();
            }
            if (mc.player.onGround) {
                Vec3d center = getCenter(mc.player.posX, mc.player.posY, mc.player.posZ);
                if (mc.player.getDistanceSq(new BlockPos(center.x, center.y, center.z)) > 0.1f)
                    mc.player.setPosition(center.x, center.y, center.z);
                isMoving = false;
            }
        }
        if (!holeOperation.equals(HoleOperation.MineEchest)) {
            targetObsidian = 11;
        }
        if (!holeOperation.equals(HoleOperation.Exp)) {
            targetExpPercent = 50;
        }
        if (!holeOperation.equals(HoleOperation.Sword) || !mc.player.getHeldItemMainhand().getItem().equals(Items.DIAMOND_SWORD)) {
            switchedToSword = false;
        }
        if (!holeOperation.equals(HoleOperation.Sword)) {
            if (enabledTrap) {
                trap.disableModule();
                enabledTrap = false;
            }
            if (enabledAura) {
                aura.disableModule();
                enabledAura = false;
            }
        }
        if (!Filler.Instance.isEnabled()) {
            Filler.Instance.enableModule();
        }
        if (isBeingCevBreakered()) {
            BlockPos pos = BlockUtil.getPlayerPos().up().up().up();
            mc.world.loadedEntityList.stream().filter(entity -> entity.getDistanceSq(pos) < 1.0f).forEach(this::breakCrystal);
        }
        EnumFacing isBeingRussianed = isBeingRussianed();
        if (isBeingRussianed != null) {
            BlockPos pos = BlockUtil.getPlayerPos().up();
            boolean newPos = false;
            switch (isBeingRussianed) {
                case NORTH:
                    pos = pos.north();
                    newPos = true;
                    break;
                case EAST:
                    pos = pos.east();
                    newPos = true;
                    break;
                case SOUTH:
                    pos = pos.south();
                    newPos = true;
                    break;
                case WEST:
                    pos = pos.west();
                    newPos = true;
                    break;
            }
            if (newPos) {
                BlockPos finalPos = pos;
                mc.world.loadedEntityList.stream().filter(entity -> entity.getDistanceSq(finalPos) < 1.0f).forEach(this::breakCrystal);
            }
        }
        switch (holeOperation) {
            case Exp:
                if (isEating)
                    return;
                int expSlot = InventoryUtil.getItemFromHotbar(Items.EXPERIENCE_BOTTLE);
                if (expSlot != -1) {
                    InventoryUtil.switchToSlot(expSlot);
                    mode = fastExp.mode.getValue();
                    triggerMode = fastExp.triggerMode.getValue();
                    handOnly = fastExp.handOnly.getValue();
                    packets = fastExp.packets.getValue();
                    fastExp.mode.setValue("Packet");
                    fastExp.triggerMode.setValue("RightClick");
                    fastExp.handOnly.setValue(true);
                    fastExp.packets.setValue(2);
                    if (!fastExp.isEnabled()) {
                        fastExp.enableModule();
                        enabledFastExp = true;
                    }
                    mc.gameSettings.keyBindUseItem.pressed = true;
                    mc.player.rotationPitch = 90.0f;
                    isExping = true;
                    targetExpPercent = 80;
                }
                break;
            case MineEchest:
                if (!isEating)
                    handleMineEchest();
                break;
            case Sword:
                int swordSlot = InventoryUtil.getItemFromHotbar(Items.DIAMOND_SWORD);
                if (swordSlot != -1 && !switchedToSword && !isEating) {
                    InventoryUtil.switchToSlot(swordSlot);
                    if (!aura.isEnabled()) {
                        aura.enableModule();
                        enabledAura = true;
                    }
                    switchedToSword = true;
                }
                if (!isEnemyInSameHole(entityPlayer) && !isTrapped(entityPlayer) && !trap.isEnabled()) {
                    trap.enableModule();
                    enabledTrap = true;
                }
                break;
            case Quiver:
                doQuiver();
                break;
            case RunOut:
                if (holeTimer.getTimeSub(500))
                    return;
                double health = mc.player.getHealth() + mc.player.getAbsorptionAmount();
                if (health >= 20.0f) {
                    Speed.Instance.speedMode.setValue("OnGround");
                    if (moveOutHole() && !(needsObsidian() || needsMending() || needsEffect(entityPlayer))) {
                        moveOutHole();
                        isMoving = true;
                    } else {
                        if (isEnemyInSameHole(entityPlayer) && !trap.isEnabled()) {
                            trap.enableModule();
                            enabledTrap = true;
                        }
                        handleChorus();
                    }
                } else {
                    if (!isEating) {
                        doEat();
                    }
                }

                break;
            case CounterTower:
                handleTower();
                break;
            case Await:
                break;
        }
    }

    public void breakCrystal(Entity entity) {
        boolean switched = false;
        int currentItem = -1;
        PotionEffect weakness = mc.player.getActivePotionEffect(MobEffects.WEAKNESS);
        if (weakness != null && !mc.player.getHeldItemMainhand().getItem().equals(Items.DIAMOND_SWORD)) {
            int swordSlot = InventoryUtil.getItemFromHotbar(Items.DIAMOND_SWORD);
            currentItem = mc.player.inventory.currentItem;
            InventoryUtil.switchToSlot(swordSlot);
            switched = true;
        }
        Objects.requireNonNull(mc.getConnection()).sendPacket(new CPacketUseEntity(entity));
        if (switched) {
            mc.player.inventory.currentItem = currentItem;
            mc.playerController.updateController();
        }
    }

    public void doQuiver() {
        int slot = InventoryUtil.getItemFromHotbar(Items.BOW);
        if (slot != -1 && bowTicks == 0) {
            mc.player.rotationPitch = -90.0f;
            if (!mc.player.getHeldItemMainhand().getItem().equals(Items.BOW))
                InventoryUtil.switchToSlot(slot);
            mc.gameSettings.keyBindUseItem.pressed = true;
            if (mc.player.getItemInUseMaxCount() >= 3) {
                mc.gameSettings.keyBindUseItem.pressed = false;
                bowTicks = 20;
            }
            if (bowTicks > 0)
                bowTicks--;
        }
    }

    public void handleTower() {
        BlockPos pos = BlockUtil.getPlayerPos().up();
        BlockPos pos2 = BlockUtil.getPlayerPos().up().up();
        int slot = InventoryUtil.getItemFromHotbar(Item.getItemFromBlock(Blocks.OBSIDIAN));
        if (slot != -1) {
            if (!hasTowered()) {
                EnumFacing face = getTowerSide();
                if (face != null) {
                    BlockPos p = pos;
                    switch (getTowerSide()) {
                        case NORTH:
                            p = pos.north();
                            break;
                        case EAST:
                            p = pos.east();
                            break;
                        case SOUTH:
                            p = pos.south();
                            break;
                        case WEST:
                            p = pos.west();
                            break;
                    }
                    if (isAir(p)) {
                        BlockUtil.placeBlockWithSwitch(p, EnumHand.MAIN_HAND, false, true, slot);
                        return;
                    }
                    if (isAir(p.up())) {
                        BlockUtil.placeBlockWithSwitch(p.up(), EnumHand.MAIN_HAND, false, true, slot);
                    }
                }
            } else {
                if (isAir(pos2.north().east())) {
                    BlockUtil.placeBlockWithSwitch(pos2.north().east(), EnumHand.MAIN_HAND, false, true, slot);
                    return;
                }
                if (isAir(pos2.north())) {
                    BlockUtil.placeBlockWithSwitch(pos2.north(), EnumHand.MAIN_HAND, false, true, slot);
                    return;
                }
                if (isAir(pos2.north().west())) {
                    BlockUtil.placeBlockWithSwitch(pos2.north().west(), EnumHand.MAIN_HAND, false, true, slot);
                    return;
                }
                if (isAir(pos2)) {
                    BlockUtil.placeBlockWithSwitch(pos2, EnumHand.MAIN_HAND, false, true, slot);
                    return;
                }
                if (isAir(pos2.east())) {
                    BlockUtil.placeBlockWithSwitch(pos2.east(), EnumHand.MAIN_HAND, false, true, slot);
                    return;
                }
                if (isAir(pos2.west())) {
                    BlockUtil.placeBlockWithSwitch(pos2.west(), EnumHand.MAIN_HAND, false, true, slot);
                    return;
                }
                if (isAir(pos2.south())) {
                    BlockUtil.placeBlockWithSwitch(pos2.south(), EnumHand.MAIN_HAND, false, true, slot);
                    return;
                }
                if (isAir(pos2.south().east())) {
                    BlockUtil.placeBlockWithSwitch(pos2.south().east(), EnumHand.MAIN_HAND, false, true, slot);
                    return;
                }
                if (isAir(pos2.south().west())) {
                    BlockUtil.placeBlockWithSwitch(pos2.south().west(), EnumHand.MAIN_HAND, false, true, slot);
                }
            }
        }
    }

    public boolean hasTowered() {
        BlockPos pos = BlockUtil.getPlayerPos().up();
        return (gud(pos.north()) && gud(pos.north().up())) || (gud(pos.east()) && gud(pos.east().up())) || (gud(pos.south()) && gud(pos.south().up())) || (gud(pos.west()) && gud(pos.west().up()));
    }

    public boolean gud(BlockPos pos) {
        return mc.world.getBlockState(pos).getBlock().equals(Blocks.OBSIDIAN) || mc.world.getBlockState(pos).getBlock().equals(Blocks.BEDROCK);
    }

    public EnumFacing getTowerSide() {
        BlockPos pos = BlockUtil.getPlayerPos().up();
        if (isGoodForTower(pos.north()) && isGoodForTower(pos.north().up())) {
            return EnumFacing.NORTH;
        }
        if (isGoodForTower(pos.east()) && isGoodForTower(pos.east().up())) {
            return EnumFacing.EAST;
        }
        if (isGoodForTower(pos.south()) && isGoodForTower(pos.south().up())) {
            return EnumFacing.SOUTH;
        }
        if (isGoodForTower(pos.west()) && isGoodForTower(pos.west().up())) {
            return EnumFacing.WEST;
        }
        return null;
    }

    public boolean isGoodForTower(BlockPos pos) {
        return mc.world.getBlockState(pos).getBlock().equals(Blocks.AIR) || mc.world.getBlockState(pos).getBlock().equals(Blocks.BEDROCK) || mc.world.getBlockState(pos).getBlock().equals(Blocks.OBSIDIAN);
    }

    public boolean isTrapped(EntityPlayer entityPlayer) {
        BlockPos pos = EntityUtil.getPlayerPos(entityPlayer).up();
        return !isAir(pos.north()) && !isAir(pos.east()) && !isAir(pos.south()) && !isAir(pos.west()) && !isAir(pos.up()) && !isAir(pos.up().up());
    }

    public boolean isntAirAndEchest(BlockPos pos) {
        return !isAir(pos) && isntEchest(pos);
    }

    public void handleMineEchest() {
        BlockPos pos = BlockUtil.getPlayerPos().up();
        if (isntAirAndEchest(pos.north()) && isntAirAndEchest(pos.east()) && isntAirAndEchest(pos.south()) && isntAirAndEchest(pos.west())) {
            mineBlockForEchest(pos, true);
            return;
        }
        if (isntEchest(pos.north()) && isntEchest(pos.east()) && isntEchest(pos.south()) && isntEchest(pos.west())) {
            placeEchest(pos);
            return;
        }
        mineBlockForEchest(pos, false);
    }

    public void mineBlockForEchest(BlockPos pos, boolean obsidian) {
        PacketMine packetMine = PacketMine.Instance;
        int pickaxeSlot = InventoryUtil.getItemFromHotbar(Items.DIAMOND_PICKAXE);
        if (hasMined) {
            if (mineTimer.getTime(obsidian ? 2000 : 1000)) {
                if (!mc.player.getHeldItemMainhand().getItem().equals(Items.DIAMOND_PICKAXE) && !isEating && pickaxeSlot != -1) {
                    InventoryUtil.switchToSlot(pickaxeSlot);
                }
                mc.playerController.onPlayerDamageBlock(minedPos, mc.player.getHorizontalFacing());
                EntityUtil.swingArm(EntityUtil.SwingType.MainHand);
                hasMined = false;
                if (packetMine.isEnabled() && enabledPacketMine) {
                    packetMine.disableModule();
                    enabledPacketMine = false;
                }
            }
            return;
        }
        if (pickaxeSlot != -1 && !isEating) {
            BlockPos pos1 = obsidian ? (mc.world.getBlockState(pos.north()).getBlock().equals(Blocks.OBSIDIAN) ? pos.north() : mc.world.getBlockState(pos.east()).getBlock().equals(Blocks.OBSIDIAN) ? pos.east() : mc.world.getBlockState(pos.south()).getBlock().equals(Blocks.OBSIDIAN) ? pos.south() : pos.west()) : findEchest(pos);
            if (pos1 == null)
                return;
            EnumFacing enumFacing = BlockUtil.getClosestEnumFacing(pos1);
            if (enumFacing == null)
                return;
            if (!packetMine.isEnabled()) {
                packetMine.enableModule();
                enabledPacketMine = true;
            }
            InventoryUtil.switchToSlot(pickaxeSlot);
            mc.playerController.onPlayerDamageBlock(pos1, mc.player.getHorizontalFacing());
            EntityUtil.swingArm(EntityUtil.SwingType.MainHand);
            minedPos = pos1;
            mineTimer.setTime(0);
            targetObsidian = 48;
            hasMined = true;
        }
    }

    public BlockPos findEchest(BlockPos pos) {
        if (!isntEchest(pos.north()))
            return pos.north();
        if (!isntEchest(pos.east()))
            return pos.east();
        if (!isntEchest(pos.south()))
            return pos.south();
        if (!isntEchest(pos.west()))
            return pos.west();
        return null;
    }

    public void placeEchest(BlockPos pos) {
        int echestSlot = InventoryUtil.getItemFromHotbar(Item.getItemFromBlock(Blocks.ENDER_CHEST));
        if (echestSlot == -1)
            return;
        if (isAir(pos.north())) {
            BlockUtil.placeBlockWithSwitch(pos.north(), EnumHand.MAIN_HAND, false, true, echestSlot);
            return;
        }
        if (isAir(pos.east())) {
            BlockUtil.placeBlockWithSwitch(pos.east(), EnumHand.MAIN_HAND, false, true, echestSlot);
            return;
        }
        if (isAir(pos.south())) {
            BlockUtil.placeBlockWithSwitch(pos.south(), EnumHand.MAIN_HAND, false, true, echestSlot);
            return;
        }
        if (isAir(pos.west())) {
            BlockUtil.placeBlockWithSwitch(pos.west(), EnumHand.MAIN_HAND, false, true, echestSlot);
        }
    }

    public boolean canEnter(BlockPos pos) {
        return isAir(pos.up()) && isAir(pos.up().up()) && isAir(pos.up().up().up()) && isAir(pos.up().up().up().up());
    }

    public boolean isAir(BlockPos pos) {
        return mc.world.getBlockState(pos).getBlock().equals(Blocks.AIR);
    }

    public boolean isntEchest(BlockPos pos) {
        return !mc.world.getBlockState(pos).getBlock().equals(Blocks.ENDER_CHEST);
    }

    public HoleOperation getHoleOperation(EntityPlayer entityPlayer) {
        boolean isEnemyInSameHole = isEnemyInSameHole(entityPlayer);
        if (isTowering(entityPlayer))
            return HoleOperation.CounterTower;
        if (!isEnemyInSameHole) {
            if (needsMending())
                return HoleOperation.Exp;
            if (needsObsidian())
                return HoleOperation.MineEchest;
            if (needsEffect(entityPlayer))
                return HoleOperation.Quiver;
        }
        if (isEnemyInSameHole) {
            if (needsObsidian() || needsMending() || needsEffect(entityPlayer))
                return HoleOperation.RunOut;
            if (mc.player.getDistance(entityPlayer) < mc.playerController.getBlockReachDistance())
                return HoleOperation.Sword;
        }
        if (BlockUtil.isPlayerSafe2(entityPlayer) && mc.player.getDistance(entityPlayer) < mc.playerController.getBlockReachDistance()) {
            return HoleOperation.Sword;
        } else if (BlockUtil.isPlayerSafe2(entityPlayer)) {
            return HoleOperation.RunOut;
        }
        if (!BlockUtil.isPlayerSafe2(entityPlayer) && mc.player.getDistance(entityPlayer) > mc.playerController.getBlockReachDistance()) {
            return HoleOperation.RunOut;
        }
        if (!BlockUtil.isPlayerSafe2(entityPlayer) && SimpleCa.Instance.cantPlace) {
            if (mc.player.getDistance(entityPlayer) < mc.playerController.getBlockReachDistance())
                return HoleOperation.Sword;
        }
        return HoleOperation.Await;
    }

    public void setUseItemUnpressedIfNeeded() {
        int currentChorus = mc.player.inventory.mainInventory.stream().filter(itemStack -> (itemStack.getItem() == Items.CHORUS_FRUIT)).mapToInt(ItemStack::getCount).sum() + (mc.player.getHeldItemOffhand().getItem() == Items.CHORUS_FRUIT ? mc.player.getHeldItemOffhand().getCount() : 0);
        int currentGapples = mc.player.inventory.mainInventory.stream().filter(itemStack -> (itemStack.getItem() == Items.GOLDEN_APPLE)).mapToInt(ItemStack::getCount).sum() + (mc.player.getHeldItemOffhand().getItem() == Items.GOLDEN_APPLE ? mc.player.getHeldItemOffhand().getCount() : 0);
        Item mainhandItem = mc.player.getHeldItemMainhand().getItem();
        if ((currentGapples < prevGapples || currentChorus < prevChorus) || (!mainhandItem.equals(Items.CHORUS_FRUIT) && !mainhandItem.equals(Items.GOLDEN_APPLE))) {
            mc.gameSettings.keyBindUseItem.pressed = false;
            isEating = false;
        }
    }

    public void handleEat() {
        double totalDamagePerSecond = damagePerSecond.values().stream().mapToDouble(aDouble -> aDouble).sum();
        double currentHealth = mc.player.getHealth() + mc.player.getAbsorptionAmount();
        if (!isEating && (!BlockUtil.isPlayerSafe2(mc.player) || ((totalDamagePerSecond >= 10.0f || currentHealth < (isSafe ? isBeingCevBreakered() ? 20.0f : 10.0f : 15.0f))))) {
            doEat();
        }
    }

    public void handleChorus() {
        if (!isEating) {
            doChorus();
        }
    }

    public void doChorus() {
        int chorusSlot = InventoryUtil.getItemFromHotbar(Items.CHORUS_FRUIT);
        if (chorusSlot != -1) {
            InventoryUtil.switchToSlot(chorusSlot);
            mc.gameSettings.keyBindUseItem.pressed = true;
            prevChorus = mc.player.inventory.mainInventory.stream().filter(itemStack -> (itemStack.getItem() == Items.CHORUS_FRUIT)).mapToInt(ItemStack::getCount).sum() + (mc.player.getHeldItemOffhand().getItem() == Items.CHORUS_FRUIT ? mc.player.getHeldItemOffhand().getCount() : 0);
            isEating = true;
        }
    }

    public boolean needsMending() {
        return mc.player.inventory.armorInventory.stream().filter(is -> InventoryUtil.getItemFromHotbar(Items.EXPERIENCE_BOTTLE) != -1 && !is.isEmpty()).mapToInt(is -> 100 - (int) ((1.0f - ((is.getMaxDamage() - (float) is.getItemDamage()) / is.getMaxDamage())) * 100.0f)).anyMatch(percentage -> percentage < targetExpPercent);
    }

    public boolean needsObsidian() {
        return (mc.player.inventory.mainInventory.stream().filter(itemStack -> (itemStack.getItem() == Item.getItemFromBlock(Blocks.OBSIDIAN))).mapToInt(ItemStack::getCount).sum() + (mc.player.getHeldItemOffhand().getItem() == Item.getItemFromBlock(Blocks.OBSIDIAN) ? mc.player.getHeldItemOffhand().getCount() : 0)) < targetObsidian;
    }

    public void doEat() {
        int gappleSlot = InventoryUtil.getItemFromHotbar(Items.GOLDEN_APPLE);
        if (gappleSlot != -1) {
            InventoryUtil.switchToSlot(gappleSlot);
            mc.gameSettings.keyBindUseItem.pressed = true;
            prevGapples = mc.player.inventory.mainInventory.stream().filter(itemStack -> (itemStack.getItem() == Items.GOLDEN_APPLE)).mapToInt(ItemStack::getCount).sum() + (mc.player.getHeldItemOffhand().getItem() == Items.GOLDEN_APPLE ? mc.player.getHeldItemOffhand().getCount() : 0);
            isEating = true;
        }
    }

    public void handleDamage() {
        double currentHealth = mc.player.getHealth() + mc.player.getAbsorptionAmount();
        long currentTimeMillis = System.currentTimeMillis();
        for (Map.Entry<Long, Double> entry : damagePerSecond.entrySet()) {
            if (entry.getKey() < currentTimeMillis) {
                damagePerSecond.remove(entry.getKey());
                return;
            }
        }
        if (currentHealth < prevHealth)
            damagePerSecond.put(currentTimeMillis + 1000L, prevHealth - currentHealth);
        prevHealth = mc.player.getHealth() + mc.player.getAbsorptionAmount();
    }

    public boolean isEnemyInSameHole(EntityPlayer entityPlayer) {
        return isSafe && BlockUtil.isPlayerSafe2(entityPlayer) && mc.player.getDistance(entityPlayer) < 1.0f;
    }

    public boolean isBeingCevBreakered() {
        BlockPos pos = BlockUtil.getPlayerPos();
        return !mc.world.getEntitiesWithinAABBExcludingEntity(null, new AxisAlignedBB(pos.up().up().up())).isEmpty();
    }

    public boolean needsEffect(EntityPlayer entityPlayer) {
        return mc.world.getBlockState(BlockUtil.getPlayerPos().up().up()).getBlock().equals(Blocks.AIR) && mc.world.getBlockState(BlockUtil.getPlayerPos().up().up().up()).getBlock().equals(Blocks.AIR) && !isEnemyInSameHole(entityPlayer) && canQuiver() && !mc.player.isPotionActive(MobEffects.STRENGTH);
    }

    public boolean canQuiver() {
        return InventoryUtil.getItemFromHotbar(Items.BOW) != -1 && IntStream.range(9, 45).filter(i -> mc.player.inventoryContainer.getInventory().get(i).getItem() instanceof ItemTippedArrow).mapToObj(i -> mc.player.inventoryContainer.getInventory().get(i)).filter(arrow -> PotionUtils.getPotionFromItem(arrow).equals(PotionTypes.STRENGTH) || PotionUtils.getPotionFromItem(arrow).equals(PotionTypes.STRONG_STRENGTH) || PotionUtils.getPotionFromItem(arrow).equals(PotionTypes.LONG_STRENGTH)).anyMatch(arrow -> !mc.player.isPotionActive(MobEffects.STRENGTH));
    }

    public boolean isTowering(EntityPlayer entityPlayer) {
        return (entityPlayer.posY - mc.player.posY) > 15;
    }

    public EnumFacing isBeingRussianed() {
        if (!BlockUtil.isPlayerSafe2(mc.player))
            return null;
        for (Map.Entry<Integer, DestroyBlockProgress> entry : mc.renderGlobal.damagedBlocks.entrySet()) {
            BlockPos pos = entry.getValue().getPosition();
            if (isObsidian(pos.north()) && mc.world.getEntitiesWithinAABBExcludingEntity(null, new AxisAlignedBB(pos.north().up())).isEmpty()) {
                return EnumFacing.NORTH;
            }
            if (isObsidian(pos.east()) && mc.world.getEntitiesWithinAABBExcludingEntity(null, new AxisAlignedBB(pos.east().up())).isEmpty()) {
                return EnumFacing.EAST;
            }
            if (isObsidian(pos.south()) && mc.world.getEntitiesWithinAABBExcludingEntity(null, new AxisAlignedBB(pos.south().up())).isEmpty()) {
                return EnumFacing.SOUTH;
            }
            if (isObsidian(pos.west()) && mc.world.getEntitiesWithinAABBExcludingEntity(null, new AxisAlignedBB(pos.west().up())).isEmpty()) {
                return EnumFacing.WEST;
            }
        }
        return null;
    }

    public boolean isObsidian(BlockPos pos) {
        return mc.world.getBlockState(pos).getBlock().equals(Blocks.OBSIDIAN);
    }

    public enum HoleOperation {
        Sword,
        Exp,
        Await,
        MineEchest,
        RunOut,
        Quiver,
        CounterTower,
        City
    }

    @SubscribeEvent
    public void onPacketSend(PacketEvent.PacketSendEvent event) {
        if (nullCheck() || !isEnabled())
            return;
        try {
            if (event.getPacket() instanceof CPacketPlayerTryUseItemOnBlock && (mc.player.getHeldItemMainhand().getItem().equals(Items.GOLDEN_APPLE) || mc.player.getHeldItemMainhand().getItem().equals(Items.CHORUS_FRUIT))) {
                RayTraceResult rayTraceResult = mc.objectMouseOver;
                if (rayTraceResult == null || !mc.gameSettings.keyBindUseItem.isKeyDown() || !mc.world.getBlockState(rayTraceResult.getBlockPos()).getBlock().equals(Blocks.ENDER_CHEST))
                    return;
                event.setCanceled(true);
                Objects.requireNonNull(mc.getConnection()).sendPacket(new CPacketPlayerTryUseItem(EnumHand.MAIN_HAND));
            }
        } catch (Exception ignored) {
        }
    }

    @SubscribeEvent
    public void onPacketReceive(PacketEvent.PacketReceiveEvent event) {
        if (nullCheck() || !isEnabled() || !(event.getPacket() instanceof SPacketPlayerPosLook))
            return;
        mc.gameSettings.keyBindSneak.pressed = true;
        needsUnSneak = true;
    }
}
