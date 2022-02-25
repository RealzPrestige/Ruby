package dev.zprestige.ruby.module.combat;

import dev.zprestige.ruby.Ruby;
import dev.zprestige.ruby.eventbus.annotation.Priority;
import dev.zprestige.ruby.eventbus.annotation.RegisterListener;
import dev.zprestige.ruby.events.ChorusEvent;
import dev.zprestige.ruby.events.MoveEvent;
import dev.zprestige.ruby.events.PacketEvent;
import dev.zprestige.ruby.module.Module;
import dev.zprestige.ruby.module.misc.RunDetect;
import dev.zprestige.ruby.settings.impl.*;
import dev.zprestige.ruby.util.Timer;
import dev.zprestige.ruby.util.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
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
import net.minecraft.util.math.Vec3i;

import java.awt.*;
import java.util.List;
import java.util.*;
import java.util.stream.Collectors;

public class AutoCrystal extends Module {
    public final Parent targets = Menu.Parent("Targets");
    public final Slider targetRange = Menu.Slider("Target Range", 0.0f, 15.0f).parent(targets);

    public final Parent others = Menu.Parent("Misc");
    public final ComboBox enumFacing = Menu.ComboBox("Enum Facing", new String[]{
            "Force Up",
            "Closest"
    }).parent(others);
    public final Switch rotations = Menu.Switch("Rotations").parent(others);
    public final Switch antiSelfHolePush = Menu.Switch("Anti Self Hole Push").parent(others);

    public final Parent placing = Menu.Parent("Placing");
    public final Slider placeRange = Menu.Slider("Place Range", 0.0f, 6.0f).parent(placing);
    public final Slider placeWallRange = Menu.Slider("Place Wall Range", 0.0f, 6.0f).parent(placing);
    public final Slider placeDelay = Menu.Slider("Place Delay", 0, 500).parent(placing);
    public final Slider placeCalcDelay = Menu.Slider("Place Calc Delay", 0, 500).parent(placing);
    public final Slider placeMinimumDamage = Menu.Slider("Place Minimum Damage", 0.0f, 36.0f).parent(placing);
    public final Slider placeMaximumSelfDamage = Menu.Slider("Place Maximum Self Damage", 0.0f, 36.0f).parent(placing);
    public final Switch placeIncludeMinOffset = Menu.Switch("Include Min Offset").parent(placing);
    public final Slider placeMinOffset = Menu.Slider("Place Min Offset", 0.0f, 15.0f).parent(placing);
    public final Switch placeIncludeMaxOffset = Menu.Switch("Include Max Offset").parent(placing);
    public final Slider placeMaxOffset = Menu.Slider("Place Max Offset", 0.0f, 15.0f).parent(placing);
    public final Switch placeSilentSwitch = Menu.Switch("Place Silent Switch").parent(placing);
    public final Switch placeAntiSuicide = Menu.Switch("Place Anti Suicide").parent(placing);
    public final Switch placePacket = Menu.Switch("Place Packet").parent(placing);
    public final Switch placeOnePointThirteen = Menu.Switch("One Point Thirteen").parent(placing);
    public final ComboBox placeCalculations = Menu.ComboBox("Place Calculations", new String[]{
            "Sync",
            "HighestEnemyDamage",
            "LowestSelfDamage",
            "HighestSelfDistance",
            "LowestEnemyDistance"
    }).parent(placing);
    public final ComboBox placeSyncCalc = Menu.ComboBox("Place Sync Calc", new String[]{"Autonomic", "Target"}).parent(placing);
    public final Switch placeFastCalc = Menu.Switch("Place Fast Calc").parent(placing);
    public final Slider placeFastCalcSpeed = Menu.Slider("Place Fast Calc Speed", 0, 100).parent(placing);
    public final Switch placeMotionPredict = Menu.Switch("Place Motion Predict").parent(placing);
    public final Slider placeMotionPredictAmount = Menu.Slider("Place Motion Predict Amount", 0, 5).parent(placing);
    public final Switch placeSwing = Menu.Switch("Place Swing").parent(placing);
    public final ComboBox placeSwingHand = Menu.ComboBox("Place Swing Hand", new String[]{"Mainhand", "Offhand", "Packet"}).parent(placing);

    public final Parent exploding = Menu.Parent("Exploding");
    public final Slider explodeRange = Menu.Slider("Explode Range", 0.0f, 6.0f).parent(exploding);
    public final Slider explodeWallRange = Menu.Slider("Explode Wall Range", 0.0f, 6.0f).parent(exploding);
    public final Slider explodeDelay = Menu.Slider("Explode Delay", 0, 500).parent(exploding);
    public final Slider explodeCalcDelay = Menu.Slider("Explode Calc Delay", 0, 500).parent(exploding);
    public final Slider explodeMinimumDamage = Menu.Slider("Explode Minimum Damage", 0.0f, 36.0f).parent(exploding);
    public final Switch explodeIgnoreMinimumDamageAndTakeHighestDamageValueWhenever = Menu.Switch("Explode Ignore Minimum Damage And Take Highest Damage Value Whenever").parent(exploding);
    public final Slider explodeMaximumSelfDamage = Menu.Slider("Explode Maximum Self Damage", 0.0f, 36.0f).parent(exploding);
    public final Switch explodeAntiStuck = Menu.Switch("Explode Anti Stuck").parent(exploding);
    public final Slider explodeAntiStuckThreshold = Menu.Slider("Explode Anti Stuck Threshold", 1, 10).parent(exploding);
    public final Switch explodeAntiSuicide = Menu.Switch("Explode Anti Suicide").parent(exploding);
    public final Switch explodePacket = Menu.Switch("Explode Packet").parent(exploding);
    public final Switch explodeInhibit = Menu.Switch("Explode Inhibit").parent(exploding);
    public final Switch breakMotionPredict = Menu.Switch("Break Motion Predict").parent(exploding);
    public final Slider breakMotionPredictAmount = Menu.Slider("Break Motion Predict Amount", 0, 5).parent(exploding);
    public final Switch explodeAntiWeakness = Menu.Switch("Explode Anti Weakness").parent(exploding);
    public final Switch explodeSwing = Menu.Switch("Explode Swing").parent(exploding);
    public final ComboBox explodeSwingHand = Menu.ComboBox("Explode Swing Hand", new String[]{"Mainhand", "Offhand", "Packet"}).parent(exploding);

    public final Parent facePlacing = Menu.Parent("Face Placing");
    public final Slider facePlaceHp = Menu.Slider("Face Place HP", 0.0f, 36.0f).parent(facePlacing);
    public final Switch runDetectFacePlace = Menu.Switch("Run Detect Face Place").parent(facePlacing);
    public final Switch facePlaceSlowOnCrouch = Menu.Switch("Face Place Slow On Crouch").parent(facePlacing);

    public final Parent predicting = Menu.Parent("Predicting");
    public final Switch predict = Menu.Switch("Predict").parent(predicting);
    public final Slider predictDelay = Menu.Slider("Predict Delay", 0, 500).parent(predicting);
    public final Switch predictSetDead = Menu.Switch("Predict Set Dead").parent(predicting);
    public final ComboBox predictSetDeadMode = Menu.ComboBox("Predict Set Dead Mode", new String[]{"Post-Confirm", "Packet-Confirm"}).parent(predicting);

    public final Parent rendering = Menu.Parent("Rendering");
    public final ComboBox renderMode = Menu.ComboBox("Render Mode", new String[]{"Static", "Fade", "Shrink", "Moving"}).parent(rendering);

    public final Slider fadeSpeed = Menu.Slider("Fade Speed", 100, 1000).parent(rendering);
    public final Slider shrinkSpeed = Menu.Slider("Shrink Speed", 1, 100).parent(rendering);
    public final Slider moveSpeed = Menu.Slider("Move Speed", 1, 100).parent(rendering);

    public final ColorSwitch placeBox = Menu.ColorSwitch("Place Box").parent(rendering);
    public final ColorSwitch placeOutline = Menu.ColorSwitch("Place Outline").parent(rendering);
    public final Slider placeLineWidth = Menu.Slider("Place Line Width", 0.0f, 5.0f).parent(rendering);
    public final Switch placeText = Menu.Switch("Place Text").parent(rendering);

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
    public void onEnable() {
        antiStuckHashMap.clear();
    }

    @Override
    public void onTick() {
        setup();
        if (target == null)
            return;
        if (target.getDistanceSq(cx, cy, cz) < 4.0f)
            chorusBB = null;
        if (placePosition != null && placeTimer.getTime((long) placeDelay.GetSlider()))
            placeCrystal(placePosition.getBlockPos());
        if (explodePosition != null && explodeTimer.getTime(facePlaceSlowOnCrouch.GetSwitch() && mc.gameSettings.keyBindSneak.isKeyDown() ? 500 : (long) explodeDelay.GetSlider()))
            explodeCrystal();
        if (runDetectFacePlace.GetSwitch() && !RunDetect.Instance.isEnabled()) {
            Ruby.chatManager.sendMessage("Run Detect Face Place turned off, RunDetect needs to be enabled!");
            runDetectFacePlace.setValue(false);
        }
    }

    public void setup() {
        if (nullCheck())
            return;
        target = EntityUtil.getTarget(targetRange.GetSlider());
        if (target == null) {
            return;
        }
        BlockPos currPos = null;
        if (placePosition != null)
            currPos = placePosition.getBlockPos();
        BlockPos prevPos = null;
        if (placePosition != null && placePosition.getBlockPos() != null)
            prevPos = placePosition.getBlockPos();
        if (placeCalcTimer.getTime((long) placeCalcDelay.GetSlider())) {
            placePosition = searchPosition(prevPos);
            placeCalcTimer.setTime(0);
        }
        if (explodeCalcTimer.getTime((long) explodeCalcDelay.GetSlider())) {
            explodePosition = searchCrystal();
            explodeCalcTimer.setTime(0);
        }
        if (placePosition != null && placePosition.getBlockPos() != currPos && !fadePosses.containsKey(placePosition.getBlockPos()))
            fadePosses.put(placePosition.getBlockPos(), placeBox.GetColor().getAlpha());
        if (placePosition != null && pos == null)
            pos = placePosition.getBlockPos();
        if (placePosition != null && placePosition.getBlockPos() != null && bb == null)
            bb = new AxisAlignedBB(placePosition.getBlockPos());
    }

    public void explodeCrystal() {
        if (explodeInhibit.GetSwitch() && inhibitCrystal.contains(explodePosition.getEntity()))
            return;
        boolean switched = false;
        int currentItem = -1;
        if (explodeAntiWeakness.GetSwitch()) {
            PotionEffect weakness = mc.player.getActivePotionEffect(MobEffects.WEAKNESS);
            if (weakness != null && !mc.player.getHeldItemMainhand().getItem().equals(Items.DIAMOND_SWORD)) {
                int swordSlot = InventoryUtil.getItemFromHotbar(Items.DIAMOND_SWORD);
                currentItem = mc.player.inventory.currentItem;
                InventoryUtil.switchToSlot(swordSlot);
                switched = true;
            }
        }
        if (rotations.GetSwitch())
            entityRotate(explodePosition.entity);
        if (predictSetDead.GetSwitch() && predictSetDeadMode.GetCombo().equals("Pre-Confirm"))
            explodePosition.getEntity().setDead();

        if (explodePacket.GetSwitch())
            Objects.requireNonNull(mc.getConnection()).sendPacket(new CPacketUseEntity(explodePosition.getEntity()));
        else
            mc.playerController.attackEntity(mc.player, explodePosition.getEntity());
        if (explodeAntiStuck.GetSwitch()) {
            int i = 1;
            i += antiStuckHashMap.entrySet().stream().filter(entry -> entry.getKey().equals(explodePosition.getEntity().entityId)).mapToInt(Map.Entry::getValue).sum();
            antiStuckHashMap.put(explodePosition.getEntity().entityId, i);
        }

        if (predictSetDead.GetSwitch() && predictSetDeadMode.GetCombo().equals("Post-Confirm"))
            explodePosition.getEntity().setDead();
        if (switched) {
            mc.player.inventory.currentItem = currentItem;
            mc.playerController.updateController();
        }
        if (explodeSwing.GetSwitch())
            EntityUtil.swingArm(explodeSwingHand.GetCombo().equals("Mainhand") ? EntityUtil.SwingType.MainHand : explodeSwingHand.GetCombo().equals("Offhand") ? EntityUtil.SwingType.OffHand : EntityUtil.SwingType.Packet);
        explodeTimer.setTime(0);
        if (explodeInhibit.GetSwitch())
            inhibitCrystal.add(explodePosition.getEntity());
    }

    public void placeCrystal(BlockPos pos) {
        if (!placeSilentSwitch.GetSwitch() && !mc.player.getHeldItemMainhand().getItem().equals(Items.END_CRYSTAL) && !mc.player.getHeldItemOffhand().getItem().equals(Items.END_CRYSTAL))
            return;
        int slot = InventoryUtil.getItemFromHotbar(Items.END_CRYSTAL);
        int currentItem = mc.player.inventory.currentItem;
        if (placeSilentSwitch.GetSwitch() && slot != -1 && !mc.player.getHeldItemMainhand().getItem().equals(Items.END_CRYSTAL) && !mc.player.getHeldItemOffhand().getItem().equals(Items.END_CRYSTAL))
            InventoryUtil.switchToSlot(slot);
        if (rotations.GetSwitch())
            posRotate(pos);
        EnumFacing facing = null;
        try {
            if (BlockUtil.hasBlockEnumFacing(pos)) {
                facing = BlockUtil.getFirstFacing(pos);
            }
        } catch (Exception ignored) {
            System.out.println("06d is a pedo");
        }
        if (placePacket.GetSwitch())
            Objects.requireNonNull(mc.getConnection()).sendPacket(new CPacketPlayerTryUseItemOnBlock(pos, enumFacing.GetCombo().equals("Closest") && facing != null ? facing : EnumFacing.UP, mc.player.getHeldItemOffhand().getItem() == Items.END_CRYSTAL ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND, 0.5f, 0.5f, 0.5f));
        else
            mc.playerController.processRightClickBlock(mc.player, mc.world, pos, enumFacing.GetCombo().equals("Closest") && facing != null ? facing : EnumFacing.UP, new Vec3d(mc.player.posX, -mc.player.posY, -mc.player.posZ), mc.player.getHeldItemOffhand().getItem().equals(Items.END_CRYSTAL) ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND);
        if (placeSilentSwitch.GetSwitch()) {
            mc.player.inventory.currentItem = currentItem;
            mc.playerController.updateController();
        }
        if (placeSwing.GetSwitch())
            EntityUtil.swingArm(placeSwingHand.GetCombo().equals("Mainhand") ? EntityUtil.SwingType.MainHand : placeSwingHand.GetCombo().equals("Offhand") ? EntityUtil.SwingType.OffHand : EntityUtil.SwingType.Packet);
        placeTimer.setTime(0);

    }

    protected final Vec3i[] offsets = new Vec3i[]{
      new Vec3i(0, 1, 1),
      new Vec3i(0, 1, -1),
      new Vec3i(1, 1, 0),
      new Vec3i(-1, 1, 0)
    };

    protected boolean canPlace(BlockPos pos) {
        ArrayList<Entity> intersecting = mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(pos)).stream().filter(entity -> !(entity instanceof EntityEnderCrystal)).collect(Collectors.toCollection(ArrayList::new));
        return intersecting.isEmpty() && (mc.player.getDistanceSq(pos) < (placeRange.GetSlider() * placeRange.GetSlider())) && (mc.world.getBlockState(pos).getBlock().equals(Blocks.OBSIDIAN) || mc.world.getBlockState(pos).getBlock().equals(Blocks.BEDROCK)) && mc.world.getBlockState(pos.up()).getBlock().equals(Blocks.AIR) && (mc.world.getBlockState(pos.up().up()).getBlock().equals(Blocks.AIR) || placeOnePointThirteen.GetSwitch());
    }

    public PlacePosition searchPosition(BlockPos previousPos) {
        TreeMap<Float, PlacePosition> posList = new TreeMap<>();
        TreeMap<Float, PlacePosition> posListDistance = new TreeMap<>();
        BlockPos selfPos = BlockUtil.getPlayerPos();
        if (antiSelfHolePush.GetSwitch()){
            for (Vec3i vec3i : offsets){
                BlockPos pos = selfPos.add(vec3i);
                if (mc.world.getBlockState(pos).getBlock().equals(Blocks.PISTON)){
                    for (Vec3i vec3i1 : offsets){
                        BlockPos pos1 = pos.add(vec3i1);
                        if (canPlace(pos1)){
                            return new PlacePosition(pos1, 1000);
                        }
                    }
                }
            }
        }
        for (BlockPos pos : BlockUtil.getSphereAutoCrystal(placeRange.GetSlider(), true)) {
            if (BlockUtil.isPosValidForCrystal(pos, placeOnePointThirteen.GetSwitch())) {
                if (placeMotionPredict.GetSwitch()) {
                    Entity j = EntityUtil.getPredictedPosition(target, placeMotionPredictAmount.GetSlider());
                    target.setEntityBoundingBox(j.getEntityBoundingBox());
                }
                if (chorusBB != null)
                    target.setEntityBoundingBox(chorusBB);
                float targetDamage = EntityUtil.calculatePosDamage(pos, target);
                float selfDamage = EntityUtil.calculatePosDamage(pos, mc.player);
                float selfHealth = mc.player.getHealth() + mc.player.getAbsorptionAmount();
                float targetHealth = target.getHealth() + target.getAbsorptionAmount();
                float minimumDamageValue = placeMinimumDamage.GetSlider();

                if (mc.player.getDistanceSq(pos.getX() + 0.5f, pos.getY(), pos.getZ() + 0.5f) > (placeRange.GetSlider() * placeRange.GetSlider()))
                    continue;

                if (previousPos != null) {
                    if (placeIncludeMinOffset.GetSwitch() && pos.getDistance(previousPos.getX(), previousPos.getY(), previousPos.getZ()) < placeMinOffset.GetSlider())
                        continue;
                    if (placeIncludeMaxOffset.GetSwitch() && pos.getDistance(previousPos.getX(), previousPos.getY(), previousPos.getZ()) > placeMaxOffset.GetSlider())
                        continue;
                }

                if (placeFastCalc.GetSwitch() && fastCalcTimer.getTime((long) (1000 - (placeFastCalcSpeed.GetSlider() * 10))))
                    if (!mc.world.getEntitiesWithinAABBExcludingEntity(null, new AxisAlignedBB(new BlockPos(pos.getX() + 0.5, pos.getY() + 1.0, pos.getZ() + 0.5))).isEmpty())
                        continue;

                if (BlockUtil.rayTraceCheckPos(new BlockPos(pos.getX() + 0.5f, pos.getY(), pos.getZ() + 0.5f)) && mc.player.getDistance(pos.getX() + 0.5f, pos.getY(), pos.getZ() + 0.5f) > (placeWallRange.GetSlider() * placeWallRange.GetSlider()))
                    continue;

                if (BlockUtil.isPlayerSafe(target) && targetHealth < facePlaceHp.GetSlider())
                    minimumDamageValue = 2;

                if (runDetectFacePlace.GetSwitch() && BlockUtil.isPlayerSafe(target) && RunDetect.Instance.gappledPreviouslySwordedPotentialRunnerList.contains(target))
                    minimumDamageValue = 2;

                if (facePlaceSlowOnCrouch.GetSwitch() && mc.gameSettings.keyBindSneak.isKeyDown())
                    minimumDamageValue = 2;

                if (targetDamage < minimumDamageValue)
                    continue;

                if (selfDamage > placeMaximumSelfDamage.GetSlider())
                    continue;

                if (placeAntiSuicide.GetSwitch() && selfDamage > selfHealth)
                    continue;
                switch (placeCalculations.GetCombo()) {
                    case "Sync":
                        posList.put(targetDamage, new PlacePosition(pos, targetDamage));
                        switch (placeSyncCalc.GetCombo()) {
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
            switch (placeCalculations.GetCombo()) {
                case "Sync":
                    syncPossesDamage = posList;
                    syncPossesDistance = posListDistance;
                    if (placeSyncCalc.GetCombo().equals("Autonomic") ? syncPossesDistance.lastEntry().getValue().getBlockPos().equals(syncPossesDamage.lastEntry().getValue().getBlockPos()) : syncPossesDistance.firstEntry().getValue().getBlockPos().equals(syncPossesDamage.lastEntry().getValue().getBlockPos()))
                        return syncPossesDamage.lastEntry().getValue();
                case "HighestEnemyDamage":
                case "HighestSelfDistance":
                    return posList.lastEntry().getValue();
                case "LowestSelfDamage":
                case "LowestEnemyDistance":
                    return posList.firstEntry().getValue();
            }
        }
        if (placeFastCalc.GetSwitch() && fastCalcTimer.getTime((long) (1000 - (placeFastCalcSpeed.GetSlider() * 10))))
            fastCalcTimer.setTime(0);
        return null;
    }

    public ExplodePosition searchCrystal() {
        TreeMap<Float, ExplodePosition> crystalList = new TreeMap<>();
        for (Entity entity : mc.world.loadedEntityList) {
            if (!(entity instanceof EntityEnderCrystal))
                continue;
            if (breakMotionPredict.GetSwitch()) {
                Entity j = EntityUtil.getPredictedPosition(target, breakMotionPredictAmount.GetSlider());
                target.setEntityBoundingBox(j.getEntityBoundingBox());
            }
            if (chorusBB != null)
                target.setEntityBoundingBox(chorusBB);
            float selfHealth = mc.player.getHealth() + mc.player.getAbsorptionAmount();
            float selfDamage = EntityUtil.calculateEntityDamage((EntityEnderCrystal) entity, mc.player);
            float targetDamage = EntityUtil.calculateEntityDamage((EntityEnderCrystal) entity, target);
            float targetHealth = target.getHealth() + target.getAbsorptionAmount();
            float minimumDamageValue = explodeMinimumDamage.GetSlider();

            if (explodeAntiStuck.GetSwitch()) {
                int i = 0;
                for (Map.Entry<Integer, Integer> entry : antiStuckHashMap.entrySet())
                    if (entry.getKey().equals(entity.entityId) && entry.getValue() > explodeAntiStuckThreshold.GetSlider())
                        i = 1;
                if (i == 1)
                    continue;
            }


            if (entity.getDistanceSq(EntityUtil.getPlayerPos(mc.player)) > (explodeRange.GetSlider() * explodeRange.GetSlider()))
                continue;

            if (BlockUtil.rayTraceCheckPos(new BlockPos(Math.floor(entity.posX), Math.floor(entity.posY), Math.floor(entity.posZ))) && mc.player.getDistanceSq(new BlockPos(Math.floor(entity.posX), Math.floor(entity.posY), Math.floor(entity.posZ))) > (explodeWallRange.GetSlider() * explodeWallRange.GetSlider()))
                continue;

            if (BlockUtil.isPlayerSafe(target) && targetHealth < facePlaceHp.GetSlider())
                minimumDamageValue = 2;

            if (runDetectFacePlace.GetSwitch() && BlockUtil.isPlayerSafe(target) && RunDetect.Instance.gappledPreviouslySwordedPotentialRunnerList.contains(target))
                minimumDamageValue = 2;

            if (facePlaceSlowOnCrouch.GetSwitch() && mc.gameSettings.keyBindSneak.isKeyDown())
                minimumDamageValue = 2;

            if (targetDamage < minimumDamageValue && !explodeIgnoreMinimumDamageAndTakeHighestDamageValueWhenever.GetSwitch())
                continue;

            if (selfDamage > explodeMaximumSelfDamage.GetSlider())
                continue;

            if (explodeAntiSuicide.GetSwitch() && selfDamage > selfHealth)
                continue;
            crystalList.put(targetDamage, new ExplodePosition(entity, targetDamage));
        }
        if (!crystalList.isEmpty())
            return crystalList.lastEntry().getValue();
        return null;
    }

    @RegisterListener(priority = Priority.HIGHEST)
    public void onPacketReceive(PacketEvent.PacketReceiveEvent event) {
        if (nullCheck() || !isEnabled())
            return;
        if (event.getPacket() instanceof SPacketSpawnObject && ((SPacketSpawnObject) event.getPacket()).getType() == 51 && predict.GetSwitch() && target != null && mc.world.getEntityByID(((SPacketSpawnObject) event.getPacket()).getEntityID()) instanceof EntityEnderCrystal) {
            CPacketUseEntity predict = new CPacketUseEntity();
            predict.entityId = ((SPacketSpawnObject) event.getPacket()).getEntityID();
            predict.action = CPacketUseEntity.Action.ATTACK;
            mc.player.connection.sendPacket(new CPacketAnimation(EnumHand.MAIN_HAND));
            mc.player.connection.sendPacket(predict);
            if (explodeInhibit.GetSwitch())
                inhibitCrystal.add(explodePosition.getEntity());
            predictTimer.setTime(0);
        }
        if (event.getPacket() instanceof SPacketSoundEffect && predict.GetSwitch() && predictSetDead.GetSwitch()) {
            SPacketSoundEffect packet = (SPacketSoundEffect) event.getPacket();
            try {
                if (packet.getCategory() == SoundCategory.BLOCKS && packet.getSound() == SoundEvents.ENTITY_GENERIC_EXPLODE) {
                    List<Entity> loadedEntityList = mc.world.loadedEntityList;
                    loadedEntityList.stream().filter(entity -> entity instanceof EntityEnderCrystal && entity.getDistanceSq(packet.getX(), packet.getY(), packet.getZ()) < (explodeRange.GetSlider() * explodeRange.GetSlider())).forEach(entity -> {
                        Objects.requireNonNull(mc.world.getEntityByID(entity.getEntityId())).setDead();
                        mc.world.removeEntityFromWorld(entity.entityId);
                    });
                }
            } catch (Exception ignored) {
            }
        }
        if (event.getPacket() instanceof SPacketExplosion && predict.GetSwitch() && predictSetDead.GetSwitch()) {
            try {
                SPacketExplosion packet = (SPacketExplosion) event.getPacket();
                mc.world.loadedEntityList.stream().filter(entity -> entity instanceof EntityEnderCrystal && entity.getDistanceSq(packet.getX(), packet.getY(), packet.getZ()) < (explodeRange.GetSlider() * explodeRange.GetSlider())).forEach(entity -> {
                    Objects.requireNonNull(mc.world.getEntityByID(entity.getEntityId())).setDead();
                    mc.world.removeEntityFromWorld(entity.entityId);
                });
            } catch (Exception ignored) {
            }
        }
    }

    @RegisterListener(priority = Priority.HIGHEST)
    public void onPacketSend(PacketEvent.PacketSendEvent event) {
        if (nullCheck() || !isEnabled())
            return;
        if (rotations.GetSwitch() && needsRotations && event.getPacket() instanceof CPacketPlayer) {
            ((CPacketPlayer) event.getPacket()).yaw = yaw;
            ((CPacketPlayer) event.getPacket()).pitch = pitch;
            needsRotations = false;
        }
        if (event.getPacket() instanceof CPacketUseEntity && predict.GetSwitch() && predictTimer.getTime(facePlaceSlowOnCrouch.GetSwitch() && mc.gameSettings.keyBindSneak.isKeyDown() ? 500 : (long) predictDelay.GetSlider())) {
            CPacketUseEntity packet = (CPacketUseEntity) event.getPacket();
            if (packet.getAction() == CPacketUseEntity.Action.ATTACK && packet.getEntityFromWorld(mc.world) instanceof EntityEnderCrystal) {
                if (predictSetDead.GetSwitch() && predictSetDeadMode.GetCombo().equals("Packet-Confirm"))
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
        switch (renderMode.GetCombo()) {
            case "Static":
                if (placePosition != null) {

                    RenderUtil.drawBoxESP(placePosition.getBlockPos(), placeBox.GetColor(), true, placeOutline.GetColor(), placeLineWidth.GetSlider(), placeOutline.GetSwitch(), placeBox.GetSwitch(), placeBox.GetColor().getAlpha(), true);
                    double damage = EntityUtil.calculatePosDamage(placePosition.getBlockPos().getX() + 0.5, placePosition.getBlockPos().getY() + 1.0, placePosition.getBlockPos().getZ() + 0.5, target);
                    if (placeText.GetSwitch())
                        RenderUtil.drawText(placePosition.getBlockPos(), ((Math.floor(damage) == damage) ? Integer.valueOf((int) damage) : String.format("%.1f", damage)) + "");
                }
                break;
            case "Fade":
                for (Map.Entry<BlockPos, Integer> entry : fadePosses.entrySet()) {
                    if (placePosition != null && placePosition.getBlockPos() != null) {
                        if (!placePosition.getBlockPos().equals(entry.getKey()))
                            fadePosses.put(entry.getKey(), (int) (entry.getValue() - (fadeSpeed.GetSlider() / 200)));
                        else {
                            double damage = EntityUtil.calculatePosDamage(placePosition.getBlockPos().getX() + 0.5, placePosition.getBlockPos().getY() + 1.0, placePosition.getBlockPos().getZ() + 0.5, target);
                            if (placeText.GetSwitch())
                                RenderUtil.drawText(placePosition.getBlockPos(), ((Math.floor(damage) == damage) ? Integer.valueOf((int) damage) : String.format("%.1f", damage)) + "");
                        }
                    } else {
                        fadePosses.put(entry.getKey(), (int) (entry.getValue() - (fadeSpeed.GetSlider() / 200)));
                    }
                    if (entry.getValue() <= 20) {
                        fadePosses.remove(entry.getKey());
                        return;
                    } else try {
                        RenderUtil.drawBoxESP(entry.getKey(), new Color(placeBox.GetColor().getRed(), placeBox.GetColor().getGreen(), placeBox.GetColor().getBlue(), entry.getValue()), true, new Color(placeOutline.GetColor().getRed(), placeOutline.GetColor().getGreen(), placeOutline.GetColor().getBlue(), entry.getValue() * 2), placeLineWidth.GetSlider(), placeOutline.GetSwitch(), placeBox.GetSwitch(), entry.getValue(), true);
                    } catch (Exception exception) {
                        Ruby.chatManager.sendRemovableMessage("Alpha parameter out of range (Choose a different Alpha)" + exception, 1);
                    }
                }
                break;
            case "Shrink":
                for (Map.Entry<BlockPos, Integer> entry : fadePosses.entrySet()) {
                    AxisAlignedBB bb = mc.world.getBlockState(entry.getKey()).getSelectedBoundingBox(mc.world, entry.getKey());
                    bb = bb.shrink(Math.max(Math.min(RenderUtil.normalize(entry.getValue(), 100 - shrinkSpeed.GetSlider() / 100f), 1.0), 0.0));
                    if (placePosition != null && placePosition.getBlockPos() != null) {
                        if (!placePosition.getBlockPos().equals(entry.getKey()))
                            fadePosses.put(entry.getKey(), (int) (entry.getValue() - Math.max(Math.min(RenderUtil.normalize(entry.getValue(), 100 - (shrinkSpeed.GetSlider() / 200f)), 1.0), 0.0)));
                        else {
                            fadePosses.put(entry.getKey(), 100);
                            double damage = EntityUtil.calculatePosDamage(placePosition.getBlockPos().getX() + 0.5, placePosition.getBlockPos().getY() + 1.0, placePosition.getBlockPos().getZ() + 0.5, target);
                            if (placeText.GetSwitch())
                                RenderUtil.drawText(placePosition.getBlockPos(), ((Math.floor(damage) == damage) ? Integer.valueOf((int) damage) : String.format("%.1f", damage)) + "");
                        }
                    } else {
                        fadePosses.put(entry.getKey(), (int) (entry.getValue() - Math.max(Math.min(RenderUtil.normalize(entry.getValue(), 100 - (shrinkSpeed.GetSlider() / 100f)), 1.0), 0.0)));
                    }
                    if (entry.getValue() <= 0) {
                        fadePosses.remove(entry.getKey());
                        return;
                    } else {
                        if (placeBox.GetSwitch())
                            RenderUtil.drawBBBox(bb, placeBox.GetColor(), placeBox.GetColor().getAlpha());
                        if (placeOutline.GetSwitch())
                            RenderUtil.drawBlockOutlineBB(bb, placeOutline.GetColor(), 1f);
                    }
                }
                break;
            case "Moving":
                if (bb != null) {
                    if (placePosition != null && placePosition.getBlockPos() != null) {
                        AxisAlignedBB cc = new AxisAlignedBB(placePosition.getBlockPos());
                        if (!bb.equals(cc)) {
                            bb = bb.offset((placePosition.getBlockPos().getX() - bb.minX) * (moveSpeed.GetSlider() / 1000f), (placePosition.getBlockPos().getY() - bb.minY) * (moveSpeed.GetSlider() / 1000f), (placePosition.getBlockPos().getZ() - bb.minZ) * (moveSpeed.GetSlider() / 1000f));
                            if (placeBox.GetSwitch())
                                RenderUtil.drawBBBox(bb, placeBox.GetColor(), placeBox.GetColor().getAlpha());
                            if (placeOutline.GetSwitch())
                                RenderUtil.drawBlockOutlineBB(bb, placeOutline.GetColor(), 1f);
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
