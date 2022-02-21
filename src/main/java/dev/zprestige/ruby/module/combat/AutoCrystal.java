package dev.zprestige.ruby.module.combat;

import dev.zprestige.ruby.events.ChorusEvent;
import dev.zprestige.ruby.events.MoveEvent;
import dev.zprestige.ruby.events.PacketEvent;
import dev.zprestige.ruby.module.Category;
import dev.zprestige.ruby.module.Module;
import dev.zprestige.ruby.module.ModuleInfo;
import dev.zprestige.ruby.module.misc.RunDetect;
import dev.zprestige.ruby.setting.impl.*;
import dev.zprestige.ruby.util.Timer;
import dev.zprestige.ruby.util.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.network.play.client.CPacketAnimation;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.network.play.server.SPacketExplosion;
import net.minecraft.network.play.server.SPacketSoundEffect;
import net.minecraft.network.play.server.SPacketSpawnObject;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.awt.*;
import java.util.List;
import java.util.*;
import java.util.function.Predicate;

@ModuleInfo(name = "AutoCrystal", category = Category.Combat, description = "crazy ca")
public class AutoCrystal extends Module {
    public ParentSetting targets = createSetting("Targets");
    public FloatSetting targetRange = createSetting("Target Range", 10.0f, 0.0f, 15.0f).setParent(targets);

    public ParentSetting others = createSetting("Misc");
    public ModeSetting enumFacing = createSetting("Enum Facing", "Force Up", Arrays.asList("Force Up", "Closest")).setParent(others);
    public BooleanSetting rotations = createSetting("Rotations", false).setParent(others);
    public BooleanSetting onMoveCalc = createSetting("On Move Calc", false).setParent(others);
    public BooleanSetting onMoveCalcPlace = createSetting("On Move Calc Place", false, v -> onMoveCalc.getValue()).setParent(others);
    public BooleanSetting onMoveCalcExplode = createSetting("On Move Calc Explode", false, v -> onMoveCalc.getValue()).setParent(others);

    public ParentSetting placing = createSetting("Placing");
    public FloatSetting placeRange = createSetting("Place Range", 5.0f, 0.0f, 6.0f).setParent(placing);
    public FloatSetting placeWallRange = createSetting("Place Wall Range", 5.0f, 0.0f, 6.0f).setParent(placing);
    public IntegerSetting placeDelay = createSetting("Place Delay", 10, 0, 500).setParent(placing);
    public IntegerSetting placeCalcDelay = createSetting("Place Calc Delay", 1, 0, 500).setParent(placing);
    public FloatSetting placeMinimumDamage = createSetting("Place Minimum Damage", 8.0f, 0.0f, 36.0f).setParent(placing);
    public FloatSetting placeMaximumSelfDamage = createSetting("Place Maximum Self Damage", 8.0f, 0.0f, 36.0f).setParent(placing);
    public BooleanSetting placeIncludeMinOffset = createSetting("Include Min Offset", false).setParent(placing);
    public FloatSetting placeMinOffset = createSetting("Place Min Offset", 2.0f, 0.0f, 15.0f, (Predicate<Float>) v-> placeIncludeMinOffset.getValue());
    public BooleanSetting placeIncludeMaxOffset = createSetting("Include Max Offset", false).setParent(placing);
    public FloatSetting placeMaxOffset = createSetting("Place Max Offset", 2.0f, 0.0f, 15.0f, (Predicate<Float>) v-> placeIncludeMaxOffset.getValue());
    public BooleanSetting placeSilentSwitch = createSetting("Place Silent Switch", false).setParent(placing);
    public BooleanSetting placeAntiSuicide = createSetting("Place Anti Suicide", false).setParent(placing);
    public BooleanSetting placePacket = createSetting("Place Packet", false).setParent(placing);
    public ModeSetting placeCalculations = createSetting("Place Calculations", "HighestEnemyDamage", Arrays.asList("Sync", "HighestEnemyDamage", "LowestSelfDamage", "HighestSelfDistance", "LowestEnemyDistance")).setParent(placing);
    public ModeSetting placeSyncCalc = createSetting("Place Sync Calc", "Autonomic", Arrays.asList("Autonomic", "Target"), v -> placeCalculations.getValue().equals("Sync")).setParent(placing);
    public BooleanSetting placeFastCalc = createSetting("Place Fast Calc", false).setParent(placing);
    public IntegerSetting placeFastCalcSpeed = createSetting("Place Fast Calc Speed", 10, 0, 100, (Predicate<Integer>) v -> placeFastCalc.getValue()).setParent(placing);
    public BooleanSetting placeMotionPredict = createSetting("Place Motion Predict", false).setParent(placing);
    public IntegerSetting placeMotionPredictAmount = createSetting("Place Motion Predict Amount", 1, 0, 5, (Predicate<Integer>) v -> placeMotionPredict.getValue()).setParent(placing);
    public BooleanSetting placeSwing = createSetting("Place Swing", false).setParent(placing);
    public ModeSetting placeSwingHand = createSetting("Place Swing Hand", "Mainhand", Arrays.asList("Mainhand", "Offhand", "Packet"), v -> placeSwing.getValue()).setParent(placing);

    public ParentSetting exploding = createSetting("Exploding");
    public FloatSetting explodeRange = createSetting("Explode Range", 5.0f, 0.0f, 6.0f).setParent(exploding);
    public FloatSetting explodeWallRange = createSetting("Explode Wall Range", 5.0f, 0.0f, 6.0f).setParent(exploding);
    public IntegerSetting explodeDelay = createSetting("Explode Delay", 60, 0, 500).setParent(exploding);
    public IntegerSetting explodeCalcDelay = createSetting("Explode Calc Delay", 1, 0, 500).setParent(exploding);
    public FloatSetting explodeMinimumDamage = createSetting("Explode Minimum Damage", 8.0f, 0.0f, 36.0f).setParent(exploding);
    public BooleanSetting explodeIgnoreMinimumDamageAndTakeHighestDamageValueWhenever = createSetting("Explode Ignore Minimum Damage And Take Highest Damage Value Whenever", false).setParent(exploding);
    public FloatSetting explodeMaximumSelfDamage = createSetting("Explode Maximum Self Damage", 8.0f, 0.0f, 36.0f).setParent(exploding);
    public BooleanSetting explodeAntiStuck = createSetting("Explode Anti Stuck", false).setParent(exploding);
    public IntegerSetting explodeAntiStuckThreshold = createSetting("Explode Anti Stuck Threshold", 2, 1, 10, (Predicate<Integer>) v-> explodeAntiStuck.getValue()).setParent(exploding);
    public BooleanSetting explodeAntiSuicide = createSetting("Explode Anti Suicide", false).setParent(exploding);
    public BooleanSetting explodePacket = createSetting("Explode Packet", false).setParent(exploding);
    public BooleanSetting explodeInhibit = createSetting("Explode Inhibit", false).setParent(exploding);
    public BooleanSetting breakMotionPredict = createSetting("Break Motion Predict", false).setParent(exploding);
    public IntegerSetting breakMotionPredictAmount = createSetting("Break Motion Predict Amount", 1, 0, 5, (Predicate<Integer>) v -> breakMotionPredict.getValue()).setParent(exploding);
    public BooleanSetting explodeAntiWeakness = createSetting("Explode Anti Weakness", false).setParent(exploding);
    public BooleanSetting explodeSwing = createSetting("Explode Swing", false).setParent(exploding);
    public ModeSetting explodeSwingHand = createSetting("Explode Swing Hand", "Mainhand", Arrays.asList("Mainhand", "Offhand", "Packet"), v -> explodeSwing.getValue()).setParent(exploding);

    public ParentSetting facePlacing = createSetting("Face Placing");
    public FloatSetting facePlaceHp = createSetting("Face Place HP", 10.0f, 0.0f, 36.0f).setParent(facePlacing);
    public BooleanSetting runDetectFacePlace = createSetting("Run Detect Face Place", false).setParent(facePlacing);
    public BooleanSetting facePlaceSlowOnCrouch = createSetting("Face Place Slow On Crouch", false).setParent(facePlacing);

    public ParentSetting predicting = createSetting("Predicting");
    public BooleanSetting predict = createSetting("Predict", false).setParent(predicting);
    public IntegerSetting predictDelay = createSetting("Predict Delay", 60, 0, 500, (Predicate<Integer>) v -> predict.getValue()).setParent(predicting);
    public BooleanSetting predictSetDead = createSetting("Predict Set Dead", false, v -> predict.getValue()).setParent(predicting);
    public ModeSetting predictSetDeadMode = createSetting("Predict Set Dead Mode", "Packet-Confirm", Arrays.asList("Pre-Confirm", "Post-Confirm", "Packet-Confirm"), v-> predictSetDead.getValue()).setParent(predicting);
   public BooleanSetting predictChorus = createSetting("Predict Chorus", false, v -> predict.getValue()).setParent(predicting);

    public ParentSetting rendering = createSetting("Rendering");
    public ModeSetting renderMode = createSetting("Render Mode", "Static", Arrays.asList("Static", "Fade", "Shrink", "Moving")).setParent(rendering);

    public IntegerSetting fadeSpeed = createSetting("Fade Speed", 200, 100, 1000, (Predicate<Integer>) v -> renderMode.getValue().equals("Fade")).setParent(rendering);
    public IntegerSetting shrinkSpeed = createSetting("Shrink Speed", 50, 1, 100, (Predicate<Integer>) v -> renderMode.getValue().equals("Shrink")).setParent(rendering);
    public IntegerSetting moveSpeed = createSetting("Move Speed", 10, 1, 100, (Predicate<Integer>) v -> renderMode.getValue().equals("Moving")).setParent(rendering);

    public ModeSetting renderType = createSetting("Render Type", "Place", Arrays.asList("Place", "Explode", "Both")).setParent(rendering);

    public BooleanSetting placeBox = createSetting("Place Box", false, v -> renderType.getValue().equals("Place") || renderType.getValue().equals("Both")).setParent(rendering);
    public ColorSetting placeBoxColor = createSetting("Place Box Color", new Color(0xFFFFFF), v -> renderType.getValue().equals("Place") || renderType.getValue().equals("Both") && placeBox.getValue()).setParent(rendering);
    public BooleanSetting placeOutline = createSetting("Place Outline", false, v -> renderType.getValue().equals("Place") || renderType.getValue().equals("Both")).setParent(rendering);
    public ColorSetting placeOutlineColor = createSetting("Place Outline Color", new Color(0xFFFFFF), v -> renderType.getValue().equals("Place") || renderType.getValue().equals("Both") && placeOutline.getValue()).setParent(rendering);
    public FloatSetting placeLineWidth = createSetting("Place Line Width", 1.0f, 0.0f, 5.0f, (Predicate<Float>) v -> placeOutline.getValue()).setParent(rendering);
    public BooleanSetting placeText = createSetting("Place Text", false, v -> renderType.getValue().equals("Place") || renderType.getValue().equals("Both")).setParent(rendering);

    public BooleanSetting explodeBox = createSetting("Explode Box", false, v -> renderType.getValue().equals("Explode") || renderType.getValue().equals("Both")).setParent(rendering);
    public ColorSetting explodeBoxColor = createSetting("Explode Box Color", new Color(0xFFFFFF), v -> renderType.getValue().equals("Explode") || renderType.getValue().equals("Both") && explodeBox.getValue()).setParent(rendering);
    public BooleanSetting explodeOutline = createSetting("Explode Outline", false, v -> renderType.getValue().equals("Explode") || renderType.getValue().equals("Both")).setParent(rendering);
    public ColorSetting explodeOutlineColor = createSetting("Explode Outline Color", new Color(0xFFFFFF), v -> renderType.getValue().equals("Explode") || renderType.getValue().equals("Both") && explodeOutline.getValue()).setParent(rendering);
    public FloatSetting explodeLineWidth = createSetting("Explode Line Width", 1.0f, 0.0f, 5.0f, (Predicate<Float>) v -> explodeOutline.getValue()).setParent(rendering);
    public BooleanSetting explodeText = createSetting("Explode Text", false, v -> renderType.getValue().equals("Explode") || renderType.getValue().equals("Both")).setParent(rendering);

    public PlacePosition placePosition = new PlacePosition(null, 0);
    public ExplodePosition explodePosition = new ExplodePosition(null, 0);
    public EntityPlayer target;
    public Timer placeTimer = new Timer();
    public Timer explodeTimer = new Timer();
    public Timer predictTimer = new Timer();
    public Timer placeCalcTimer = new Timer();
    public Timer explodeCalcTimer = new Timer();
    public Timer fastCalcTimer = new Timer();
    public HashMap<BlockPos, Integer> fadePosses = new HashMap<>();
    public TreeMap<Float, PlacePosition> syncPossesDamage = new TreeMap<>();
    public TreeMap<Float, PlacePosition> syncPossesDistance = new TreeMap<>();
    public HashMap<Integer, Integer> antiStuckHashMap = new HashMap<>();
    public ArrayList<Entity> inhibitCrystal = new ArrayList<>();
    public BlockPos pos = null;
    public float yaw = 0.0f;
    public float pitch = 0.0f;
    public boolean needsRotations = false;
    public AxisAlignedBB bb = null;
    public AxisAlignedBB chorusBB = null;
    public double cx, cy, cz;

    @Override
    public void onEnable(){
        antiStuckHashMap.clear();
    }

    @SubscribeEvent
    public void onChorus(ChorusEvent event) {
        if (nullCheck() || !isEnabled() || !predict.getValue() || !predictChorus.getValue())
            return;
        double x = cx = event.x;
        double y = cy = event.y;
        double z = cz = event.z;
        if (mc.player.getDistanceSq(new BlockPos(x, y, z)) < 4.0f || mc.player.getDistanceSq(new BlockPos(x, y, z)) > 400.0)
            return;
        chorusBB = new AxisAlignedBB(new BlockPos(x, y, z));
    }

    @SubscribeEvent
    public void onMove(MoveEvent event) {
        if (nullCheck() || !isEnabled() || !onMoveCalc.getValue())
            return;
        target = EntityUtil.getTarget(targetRange.getValue());
        if (target == null) {
            setHudString("");
            return;
        }
        if (onMoveCalcPlace.getValue()) {
            BlockPos currPos = null;
            if (placePosition != null)
                currPos = placePosition.getBlockPos();
            BlockPos prevPos = null;
            if (placePosition != null && placePosition.getBlockPos() != null)
                prevPos = placePosition.getBlockPos();
            if (placeCalcTimer.getTime(placeCalcDelay.getValue())) {
                placePosition = searchPosition(prevPos);
                placeCalcTimer.setTime(0);
            }
            if (placePosition != null && placePosition.getBlockPos() != currPos && !fadePosses.containsKey(placePosition.getBlockPos()))
                fadePosses.put(placePosition.getBlockPos(), placeBoxColor.getValue().getAlpha());
            if (placePosition != null && pos == null)
                pos = placePosition.getBlockPos();
            if (placePosition != null && placePosition.getBlockPos() != null && bb == null)
                bb = new AxisAlignedBB(placePosition.getBlockPos());
        }
        if (onMoveCalcExplode.getValue() && explodeCalcTimer.getTime(explodeCalcDelay.getValue())) {
            explodePosition = searchCrystal();
            explodeCalcTimer.setTime(0);
        }
    }

    @Override
    public void onTick() {
        setup();
        if (target == null)
            return;
        if (target.getDistanceSq(cx, cy, cz) < 4.0f)
            chorusBB = null;
        if (placePosition != null && placeTimer.getTime(placeDelay.getValue()))
            placeCrystal(placePosition.getBlockPos());
        if (explodePosition != null && explodeTimer.getTime(facePlaceSlowOnCrouch.getValue() && mc.gameSettings.keyBindSneak.isKeyDown() ? 500 : explodeDelay.getValue()))
            explodeCrystal();
        if (runDetectFacePlace.getValue() && !RunDetect.Instance.isEnabled()) {
            MessageUtil.sendMessage("Run Detect Face Place turned off, RunDetect needs to be enabled!");
            runDetectFacePlace.setValue(false);
        }
    }

    public void setup() {
        if (nullCheck())
            return;
        target = EntityUtil.getTarget(targetRange.getValue());
        if (target == null) {
            setHudString("");
            return;
        }
        BlockPos currPos = null;
        if (placePosition != null)
            currPos = placePosition.getBlockPos();
        BlockPos prevPos = null;
        if (placePosition != null && placePosition.getBlockPos() != null)
            prevPos = placePosition.getBlockPos();
        if (placeCalcTimer.getTime(placeCalcDelay.getValue())) {
            placePosition = searchPosition(prevPos);
            placeCalcTimer.setTime(0);
        }
        if (explodeCalcTimer.getTime(explodeCalcDelay.getValue())) {
            explodePosition = searchCrystal();
            explodeCalcTimer.setTime(0);
        }
        if (placePosition != null && placePosition.getBlockPos() != currPos && !fadePosses.containsKey(placePosition.getBlockPos()))
            fadePosses.put(placePosition.getBlockPos(), placeBoxColor.getValue().getAlpha());
        if (placePosition != null && pos == null)
            pos = placePosition.getBlockPos();
        if (placePosition != null && placePosition.getBlockPos() != null && bb == null)
            bb = new AxisAlignedBB(placePosition.getBlockPos());
        setHudString(target.getName());
        setHudStringColor(new Color(255, 255, 255));
    }

    public void explodeCrystal() {
        if (explodeInhibit.getValue() && inhibitCrystal.contains(explodePosition.getEntity()))
            return;
        boolean switched = false;
        int currentItem = -1;
        if (explodeAntiWeakness.getValue()) {
            PotionEffect weakness = mc.player.getActivePotionEffect(MobEffects.WEAKNESS);
            if (weakness != null && !mc.player.getHeldItemMainhand().getItem().equals(Items.DIAMOND_SWORD)) {
                int swordSlot = InventoryUtil.getItemFromHotbar(Items.DIAMOND_SWORD);
                currentItem = mc.player.inventory.currentItem;
                InventoryUtil.switchToSlot(swordSlot);
                switched = true;
            }
        }
        if (rotations.getValue())
            entityRotate(explodePosition.entity);
        if (predictSetDead.getValue() && predictSetDeadMode.getValue().equals("Pre-Confirm"))
            explodePosition.getEntity().setDead();

        if (explodePacket.getValue())
            Objects.requireNonNull(mc.getConnection()).sendPacket(new CPacketUseEntity(explodePosition.getEntity()));
        else
            mc.playerController.attackEntity(mc.player, explodePosition.getEntity());
        if (explodeAntiStuck.getValue()) {
            int i = 1;
            i += antiStuckHashMap.entrySet().stream().filter(entry -> entry.getKey().equals(explodePosition.getEntity().entityId)).mapToInt(Map.Entry::getValue).sum();
            antiStuckHashMap.put(explodePosition.getEntity().entityId, i);
        }

        if (predictSetDead.getValue() && predictSetDeadMode.getValue().equals("Post-Confirm"))
            explodePosition.getEntity().setDead();
        if (switched) {
            mc.player.inventory.currentItem = currentItem;
            mc.playerController.updateController();
        }
        if (explodeSwing.getValue())
            EntityUtil.swingArm(explodeSwingHand.getValue().equals("Mainhand") ? EntityUtil.SwingType.MainHand : explodeSwingHand.getValue().equals("Offhand") ? EntityUtil.SwingType.OffHand : EntityUtil.SwingType.Packet);
        explodeTimer.setTime(0);
        if (explodeInhibit.getValue())
            inhibitCrystal.add(explodePosition.getEntity());
    }

    public void placeCrystal(BlockPos pos) {
        if (!placeSilentSwitch.getValue() && !mc.player.getHeldItemMainhand().getItem().equals(Items.END_CRYSTAL) && !mc.player.getHeldItemOffhand().getItem().equals(Items.END_CRYSTAL))
            return;
        int slot = InventoryUtil.getItemFromHotbar(Items.END_CRYSTAL);
        int currentItem = mc.player.inventory.currentItem;
        if (placeSilentSwitch.getValue() && slot != -1 && !mc.player.getHeldItemMainhand().getItem().equals(Items.END_CRYSTAL) && !mc.player.getHeldItemOffhand().getItem().equals(Items.END_CRYSTAL))
            InventoryUtil.switchToSlot(slot);
        if (rotations.getValue())
            posRotate(pos);
        EnumFacing facing = null;
        try {
            if (BlockUtil.hasBlockEnumFacing(pos)) {
                facing = BlockUtil.getFirstFacing(pos);
            }
        } catch (Exception ignored){
            System.out.println("06d is a pedo");
        }
        if (placePacket.getValue())
            Objects.requireNonNull(mc.getConnection()).sendPacket(new CPacketPlayerTryUseItemOnBlock(pos, enumFacing.getValue().equals("Closest") && facing != null  ? facing : EnumFacing.UP, mc.player.getHeldItemOffhand().getItem() == Items.END_CRYSTAL ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND, 0.5f, 0.5f, 0.5f));
        else
            mc.playerController.processRightClickBlock(mc.player, mc.world, pos, enumFacing.getValue().equals("Closest") && facing != null  ? facing : EnumFacing.UP, new Vec3d(mc.player.posX, -mc.player.posY, -mc.player.posZ), mc.player.getHeldItemOffhand().getItem().equals(Items.END_CRYSTAL) ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND);
        if (placeSilentSwitch.getValue()) {
            mc.player.inventory.currentItem = currentItem;
            mc.playerController.updateController();
        }
        if (placeSwing.getValue())
            EntityUtil.swingArm(placeSwingHand.getValue().equals("Mainhand") ? EntityUtil.SwingType.MainHand : placeSwingHand.getValue().equals("Offhand") ? EntityUtil.SwingType.OffHand : EntityUtil.SwingType.Packet);
        placeTimer.setTime(0);

    }

    public PlacePosition searchPosition(BlockPos previousPos) {
        TreeMap<Float, PlacePosition> posList = new TreeMap<>();
        TreeMap<Float, PlacePosition> posListDistance = new TreeMap<>();
        for (BlockPos pos : BlockUtil.getSphereAutoCrystal(placeRange.getValue(), true)) {
            if (BlockUtil.isPosValidForCrystal(pos, false)) {
                if (placeMotionPredict.getValue()) {
                    Entity j = EntityUtil.getPredictedPosition(target, placeMotionPredictAmount.getValue());
                    target.setEntityBoundingBox(j.getEntityBoundingBox());
                }
                if (chorusBB != null)
                    target.setEntityBoundingBox(chorusBB);
                float targetDamage = EntityUtil.calculatePosDamage(pos, target);
                float selfDamage = EntityUtil.calculatePosDamage(pos, mc.player);
                float selfHealth = mc.player.getHealth() + mc.player.getAbsorptionAmount();
                float targetHealth = target.getHealth() + target.getAbsorptionAmount();
                float minimumDamageValue = placeMinimumDamage.getValue();

                if (mc.player.getDistanceSq(pos.getX() + 0.5f, pos.getY(), pos.getZ() + 0.5f) > (placeRange.getValue() * placeRange.getValue()))
                    continue;

                if (previousPos != null){
                    if (placeIncludeMinOffset.getValue() && pos.getDistance(previousPos.getX(), previousPos.getY(), previousPos.getZ()) < placeMinOffset.getValue())
                        continue;
                    if (placeIncludeMaxOffset.getValue() && pos.getDistance(previousPos.getX(), previousPos.getY(), previousPos.getZ()) > placeMaxOffset.getValue())
                        continue;
                }

                if (placeFastCalc.getValue() && fastCalcTimer.getTime(1000 - (placeFastCalcSpeed.getValue() * 10)))
                    if (!mc.world.getEntitiesWithinAABBExcludingEntity(null, new AxisAlignedBB(new BlockPos(pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5))).isEmpty())
                        continue;

                if (BlockUtil.rayTraceCheckPos(new BlockPos(pos.getX() + 0.5f, pos.getY(), pos.getZ() + 0.5f)) && mc.player.getDistance(pos.getX() + 0.5f, pos.getY(), pos.getZ() + 0.5f) > (placeWallRange.getValue() * placeWallRange.getValue()))
                    continue;

                if (BlockUtil.isPlayerSafe(target) && targetHealth < facePlaceHp.getValue())
                    minimumDamageValue = 2;

                if (runDetectFacePlace.getValue() && BlockUtil.isPlayerSafe(target) && RunDetect.Instance.gappledPreviouslySwordedPotentialRunnerList.contains(target))
                    minimumDamageValue = 2;

                if (facePlaceSlowOnCrouch.getValue() && mc.gameSettings.keyBindSneak.isKeyDown())
                    minimumDamageValue = 2;

                if (targetDamage < minimumDamageValue)
                    continue;

                if (selfDamage > placeMaximumSelfDamage.getValue())
                    continue;

                if (placeAntiSuicide.getValue() && selfDamage > selfHealth)
                    continue;
                switch (placeCalculations.getValue()) {
                    case "Sync":
                        posList.put(targetDamage, new PlacePosition(pos, targetDamage));
                        switch (placeSyncCalc.getValue()) {
                            case "Autonomic":
                                posListDistance.put((float) (mc.player.getDistanceSq(pos.getX() + 0.5f, pos.getY(), pos.getZ() + 0.5f) / 2), new PlacePosition(pos, targetDamage));
                                break;
                            case "Target":
                                posListDistance.put((float) (target.getDistanceSq(pos.getX() + 0.5f, pos.getY(), pos.getZ() + 0.5f) / 2), new PlacePosition(pos, targetDamage));
                                break;
                        }
                        break;
                    case "HighestEnemyDamage":
                        posList.put(targetDamage, new PlacePosition(pos, targetDamage));
                        break;
                    case "LowestSelfDamage":
                        posList.put(selfDamage, new PlacePosition(pos, targetDamage));
                        break;
                    case "HighestSelfDistance":
                        posList.put((float) (mc.player.getDistanceSq(pos.getX() + 0.5f, pos.getY(), pos.getZ() + 0.5f) / 2), new PlacePosition(pos, targetDamage));
                        break;
                    case "LowestEnemyDistance":
                        posList.put((float) (target.getDistanceSq(pos.getX() + 0.5f, pos.getY(), pos.getZ() + 0.5f) / 2), new PlacePosition(pos, targetDamage));
                        break;
                }
            }
        }
        if (!posList.isEmpty()) {
            switch (placeCalculations.getValue()) {
                case "Sync":
                    syncPossesDamage = posList;
                    syncPossesDistance = posListDistance;
                    if (placeSyncCalc.getValue().equals("Autonomic") ? syncPossesDistance.lastEntry().getValue().getBlockPos().equals(syncPossesDamage.lastEntry().getValue().getBlockPos()) : syncPossesDistance.firstEntry().getValue().getBlockPos().equals(syncPossesDamage.lastEntry().getValue().getBlockPos()))
                        return syncPossesDamage.lastEntry().getValue();
                case "HighestEnemyDamage":
                case "HighestSelfDistance":
                    return posList.lastEntry().getValue();
                case "LowestSelfDamage":
                case "LowestEnemyDistance":
                    return posList.firstEntry().getValue();
            }
        }
        if (placeFastCalc.getValue() && fastCalcTimer.getTime(1000 - (placeFastCalcSpeed.getValue() * 10)))
            fastCalcTimer.setTime(0);
        return null;
    }

    public ExplodePosition searchCrystal() {
        TreeMap<Float, ExplodePosition> crystalList = new TreeMap<>();
        for (Entity entity : mc.world.loadedEntityList) {
            if (!(entity instanceof EntityEnderCrystal))
                continue;
            if (breakMotionPredict.getValue()) {
                Entity j = EntityUtil.getPredictedPosition(target, breakMotionPredictAmount.getValue());
                target.setEntityBoundingBox(j.getEntityBoundingBox());
            }
            if (chorusBB != null)
                target.setEntityBoundingBox(chorusBB);
            float selfHealth = mc.player.getHealth() + mc.player.getAbsorptionAmount();
            float selfDamage = EntityUtil.calculateEntityDamage((EntityEnderCrystal) entity, mc.player);
            float targetDamage = EntityUtil.calculateEntityDamage((EntityEnderCrystal) entity, target);
            float targetHealth = target.getHealth() + target.getAbsorptionAmount();
            float minimumDamageValue = explodeMinimumDamage.getValue();

            if (explodeAntiStuck.getValue()){
                int i = 0;
                for (Map.Entry<Integer, Integer> entry : antiStuckHashMap.entrySet())
                    if (entry.getKey().equals(entity.entityId) && entry.getValue() > explodeAntiStuckThreshold.getValue())
                        i = 1;
                if (i == 1)
                    continue;
            }


            if (entity.getDistanceSq(EntityUtil.getPlayerPos(mc.player)) > (explodeRange.getValue() * explodeRange.getValue()))
                continue;

            if (BlockUtil.rayTraceCheckPos(new BlockPos(Math.floor(entity.posX), Math.floor(entity.posY), Math.floor(entity.posZ))) && mc.player.getDistanceSq(new BlockPos(Math.floor(entity.posX), Math.floor(entity.posY), Math.floor(entity.posZ))) > (explodeWallRange.getValue() * explodeWallRange.getValue()))
                continue;

            if (BlockUtil.isPlayerSafe(target) && targetHealth < facePlaceHp.getValue())
                minimumDamageValue = 2;

            if (runDetectFacePlace.getValue() && BlockUtil.isPlayerSafe(target) && RunDetect.Instance.gappledPreviouslySwordedPotentialRunnerList.contains(target))
                minimumDamageValue = 2;

            if (facePlaceSlowOnCrouch.getValue() && mc.gameSettings.keyBindSneak.isKeyDown())
                minimumDamageValue = 2;

            if (targetDamage < minimumDamageValue && !explodeIgnoreMinimumDamageAndTakeHighestDamageValueWhenever.getValue())
                continue;

            if (selfDamage > explodeMaximumSelfDamage.getValue())
                continue;

            if (explodeAntiSuicide.getValue() && selfDamage > selfHealth)
                continue;
            crystalList.put(targetDamage, new ExplodePosition(entity, targetDamage));
        }
        if (!crystalList.isEmpty())
            return crystalList.lastEntry().getValue();
        return null;
    }

    @SubscribeEvent
    public void onPacketReceive(PacketEvent.PacketReceiveEvent event) {
        if (nullCheck() || !isEnabled())
            return;
        if (event.getPacket() instanceof SPacketSpawnObject && ((SPacketSpawnObject) event.getPacket()).getType() == 51 && predict.getValue() && target != null && mc.world.getEntityByID(((SPacketSpawnObject) event.getPacket()).getEntityID()) instanceof EntityEnderCrystal) {
            CPacketUseEntity predict = new CPacketUseEntity();
            predict.entityId = ((SPacketSpawnObject) event.getPacket()).getEntityID();
            predict.action = CPacketUseEntity.Action.ATTACK;
            mc.player.connection.sendPacket(new CPacketAnimation(EnumHand.MAIN_HAND));
            mc.player.connection.sendPacket(predict);
            if (explodeInhibit.getValue())
                inhibitCrystal.add(explodePosition.getEntity());
            predictTimer.setTime(0);
        }
        if (event.getPacket() instanceof SPacketSoundEffect && predict.getValue() && predictSetDead.getValue()) {
            SPacketSoundEffect packet = (SPacketSoundEffect) event.getPacket();
            try {
                if (packet.getCategory() == SoundCategory.BLOCKS && packet.getSound() == SoundEvents.ENTITY_GENERIC_EXPLODE) {
                    List<Entity> loadedEntityList = mc.world.loadedEntityList;
                    loadedEntityList.stream().filter(entity -> entity instanceof EntityEnderCrystal && entity.getDistanceSq(packet.getX(), packet.getY(), packet.getZ()) < (explodeRange.getValue() * explodeRange.getValue())).forEach(entity -> {
                        Objects.requireNonNull(mc.world.getEntityByID(entity.getEntityId())).setDead();
                        mc.world.removeEntityFromWorld(entity.entityId);
                    });
                }
            } catch (Exception ignored) {
            }
        }
        if (event.getPacket() instanceof SPacketExplosion && predict.getValue() && predictSetDead.getValue()) {
            try {
                SPacketExplosion packet = (SPacketExplosion) event.getPacket();
                mc.world.loadedEntityList.stream().filter(entity -> entity instanceof EntityEnderCrystal && entity.getDistanceSq(packet.getX(), packet.getY(), packet.getZ()) < (explodeRange.getValue() * explodeRange.getValue())).forEach(entity -> {
                    Objects.requireNonNull(mc.world.getEntityByID(entity.getEntityId())).setDead();
                    mc.world.removeEntityFromWorld(entity.entityId);
                });
            } catch (Exception ignored) {
            }
        }
    }

    @SubscribeEvent
    public void onPacketSend(PacketEvent.PacketSendEvent event) {
        if (nullCheck() || !isEnabled())
            return;
        if (rotations.getValue() && needsRotations && event.getPacket() instanceof CPacketPlayer) {
            ((CPacketPlayer) event.getPacket()).yaw = yaw;
            ((CPacketPlayer) event.getPacket()).pitch = pitch;
            needsRotations = false;
        }
        if (event.getPacket() instanceof CPacketUseEntity && predict.getValue() && predictTimer.getTime(facePlaceSlowOnCrouch.getValue() && mc.gameSettings.keyBindSneak.isKeyDown() ? 500 : predictDelay.getValue())) {
            CPacketUseEntity packet = (CPacketUseEntity) event.getPacket();
            if (packet.getAction() == CPacketUseEntity.Action.ATTACK && packet.getEntityFromWorld(mc.world) instanceof EntityEnderCrystal) {
                if (predictSetDead.getValue() && predictSetDeadMode.getValue().equals("Packet-Confirm"))
                    Objects.requireNonNull(packet.getEntityFromWorld(mc.world)).setDead();
                if (placePosition != null)
                    placeCrystal(placePosition.getBlockPos());
                predictTimer.setTime(0);
            }
        }
    }

    @Override
    public void onGlobalRenderTick() {
        if (target == null)
            return;
        switch (renderMode.getValue()) {
            case "Static":
                if (placePosition != null)
                    if (renderType.getValue().equals("Place") || renderType.getValue().equals("Both")) {
                        RenderUtil.drawBoxESP(placePosition.getBlockPos(), placeBoxColor.getValue(), true, placeOutlineColor.getValue(), placeLineWidth.getValue(), placeOutline.getValue(), placeBox.getValue(), placeBoxColor.getValue().getAlpha(), true);
                        double damage = EntityUtil.calculatePosDamage(placePosition.getBlockPos().getX() + 0.5, placePosition.getBlockPos().getY() + 1.0, placePosition.getBlockPos().getZ() + 0.5, target);
                        if (placeText.getValue())
                            RenderUtil.drawText(placePosition.getBlockPos(), ((Math.floor(damage) == damage) ? Integer.valueOf((int) damage) : String.format("%.1f", damage)) + "");
                    }
                if (explodePosition != null)
                    if (renderType.getValue().equals("Explode") || renderType.getValue().equals("Both")) {
                        RenderUtil.drawBoxESP(explodePosition.getEntity().getPosition(), explodeBoxColor.getValue(), true, explodeOutlineColor.getValue(), explodeLineWidth.getValue(), explodeOutline.getValue(), explodeBox.getValue(), explodeBoxColor.getValue().getAlpha(), true);
                        double damage = EntityUtil.calculatePosDamage(Math.floor(explodePosition.getEntity().getPosition().getX()), Math.floor(explodePosition.getEntity().getPosition().getY()), Math.floor(explodePosition.getEntity().getPosition().getZ()), target);
                        if (explodeText.getValue())
                            RenderUtil.drawText(explodePosition.getEntity().getPosition(), ((Math.floor(damage) == damage) ? Integer.valueOf((int) damage) : String.format("%.1f", damage)) + "");
                    }
                break;
            case "Fade":
                for (Map.Entry<BlockPos, Integer> entry : fadePosses.entrySet()) {
                    if (placePosition != null && placePosition.getBlockPos() != null) {
                        if (!placePosition.getBlockPos().equals(entry.getKey()))
                            fadePosses.put(entry.getKey(), entry.getValue() - (fadeSpeed.getValue() / 200));
                        else {
                            double damage = EntityUtil.calculatePosDamage(placePosition.getBlockPos().getX() + 0.5, placePosition.getBlockPos().getY() + 1.0, placePosition.getBlockPos().getZ() + 0.5, target);
                            if (placeText.getValue())
                                RenderUtil.drawText(placePosition.getBlockPos(), ((Math.floor(damage) == damage) ? Integer.valueOf((int) damage) : String.format("%.1f", damage)) + "");
                        }
                    } else {
                        fadePosses.put(entry.getKey(), entry.getValue() - (fadeSpeed.getValue() / 200));
                    }
                    if (entry.getValue() <= 20) {
                        fadePosses.remove(entry.getKey());
                        return;
                    } else try {
                        RenderUtil.drawBoxESP(entry.getKey(), new Color(placeBoxColor.getValue().getRed(), placeBoxColor.getValue().getGreen(), placeBoxColor.getValue().getBlue(), entry.getValue()), true, new Color(placeOutlineColor.getValue().getRed(), placeOutlineColor.getValue().getGreen(), placeOutlineColor.getValue().getBlue(), entry.getValue() * 2), placeLineWidth.getValue(), placeOutline.getValue(), placeBox.getValue(), entry.getValue(), true);
                    } catch (Exception exception) {
                        MessageUtil.sendRemovableMessage("Alpha parameter out of range (Choose a different Alpha)" + exception, 1);
                    }
                }
                break;
            case "Shrink":
                for (Map.Entry<BlockPos, Integer> entry : fadePosses.entrySet()) {
                    AxisAlignedBB bb = mc.world.getBlockState(entry.getKey()).getSelectedBoundingBox(mc.world, entry.getKey());
                    bb = bb.shrink(Math.max(Math.min(RenderUtil.normalize(entry.getValue(), 100 - shrinkSpeed.getValue() / 100f), 1.0), 0.0));
                    if (placePosition != null && placePosition.getBlockPos() != null) {
                        if (!placePosition.getBlockPos().equals(entry.getKey()))
                            fadePosses.put(entry.getKey(), (int) (entry.getValue() - Math.max(Math.min(RenderUtil.normalize(entry.getValue(), 100 - (shrinkSpeed.getValue() / 200f)), 1.0), 0.0)));
                        else {
                            fadePosses.put(entry.getKey(), 100);
                            double damage = EntityUtil.calculatePosDamage(placePosition.getBlockPos().getX() + 0.5, placePosition.getBlockPos().getY() + 1.0, placePosition.getBlockPos().getZ() + 0.5, target);
                            if (placeText.getValue())
                                RenderUtil.drawText(placePosition.getBlockPos(), ((Math.floor(damage) == damage) ? Integer.valueOf((int) damage) : String.format("%.1f", damage)) + "");
                        }
                    } else {
                        fadePosses.put(entry.getKey(), (int) (entry.getValue() - Math.max(Math.min(RenderUtil.normalize(entry.getValue(), 100 - (shrinkSpeed.getValue() / 100f)), 1.0), 0.0)));
                    }
                    if (entry.getValue() <= 0) {
                        fadePosses.remove(entry.getKey());
                        return;
                    } else {
                        if (placeBox.getValue())
                            RenderUtil.drawBBBox(bb, placeBoxColor.getValue(), placeBoxColor.getValue().getAlpha());
                        if (placeOutline.getValue())
                            RenderUtil.drawBlockOutlineBB(bb, placeOutlineColor.getValue(), 1f);
                    }
                }
                break;
            case "Moving":
                if (bb != null) {
                    if (placePosition != null && placePosition.getBlockPos() != null) {
                        AxisAlignedBB cc = new AxisAlignedBB(placePosition.getBlockPos());
                        if (!bb.equals(cc)) {
                            bb = bb.offset((placePosition.getBlockPos().getX() - bb.minX) * (moveSpeed.getValue() / 1000f), (placePosition.getBlockPos().getY() - bb.minY) * (moveSpeed.getValue() / 1000f), (placePosition.getBlockPos().getZ() - bb.minZ) * (moveSpeed.getValue() / 1000f));
                            if (placeBox.getValue())
                                RenderUtil.drawBBBox(bb, placeBoxColor.getValue(), placeBoxColor.getValue().getAlpha());
                            if (placeOutline.getValue())
                                RenderUtil.drawBlockOutlineBB(bb, placeOutlineColor.getValue(), 1f);
                        }
                    }
                }
                break;
        }
    }

    public void entityRotate(Entity entity) {
        float[] angle = BlockUtil.calcAngle(mc.player.getPositionEyes(mc.getRenderPartialTicks()), entity.getPositionVector());
        yaw = angle[0];
        pitch = angle[1];
        needsRotations = true;
    }

    public void posRotate(BlockPos pos) {
        float[] angle = BlockUtil.calcAngle(mc.player.getPositionEyes(mc.getRenderPartialTicks()), new Vec3d((float) pos.getX() + 0.5f, (float) pos.getY() - 0.5f, (float) pos.getZ() + 0.5f));
        yaw = angle[0];
        pitch = angle[1];
        needsRotations = true;
    }

    public static class ExplodePosition {
        Entity entity;
        float targetDamage;

        public ExplodePosition(Entity entity, float targetDamage) {
            this.entity = entity;
            this.targetDamage = targetDamage;
        }

        public Entity getEntity() {
            return entity;
        }
    }

    public static class PlacePosition {
        BlockPos blockPos;
        float targetDamage;

        public PlacePosition(BlockPos blockPos, float targetDamage) {
            this.blockPos = blockPos;
            this.targetDamage = targetDamage;
        }

        public BlockPos getBlockPos() {
            return blockPos;
        }
    }
}
