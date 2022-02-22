package dev.zprestige.ruby.module.combat;

import dev.zprestige.ruby.eventbus.annotation.Priority;
import dev.zprestige.ruby.eventbus.annotation.RegisterListener;
import dev.zprestige.ruby.events.PacketEvent;
import dev.zprestige.ruby.module.Module;
import dev.zprestige.ruby.setting.impl.FloatSetting;
import dev.zprestige.ruby.util.BlockUtil;
import dev.zprestige.ruby.util.EntityUtil;
import dev.zprestige.ruby.util.InventoryUtil;
import dev.zprestige.ruby.util.RenderUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.network.play.client.CPacketAnimation;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.network.play.server.SPacketSoundEffect;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.TreeMap;
import java.util.stream.Collectors;

public class SimpleCa extends Module {
    public static SimpleCa Instance;
    public FloatSetting targetRange = createSetting("Target Range", 9.0f, 0.1f, 15.0f);
    public FloatSetting minDamage = createSetting("Min Damage", 6.0f, 0.1f, 15.0f);
    public FloatSetting maxSelfDamage = createSetting("Max Self Damage", 6.0f, 0.1f, 15.0f);
    public ArrayList<Long> crystalsPerSecond = new ArrayList<>();
    public BlockPos pos = null;
    public boolean cantPlace = false;

    public SimpleCa(){
        Instance = this;
    }

    @Override
    public void onTick() {
        EntityPlayer entityPlayer = EntityUtil.getTarget(targetRange.getValue());
        pos = null;
        if (entityPlayer == null) {
            return;
        }
        TreeMap<Double, BlockPos> posses = new TreeMap<>();
        for (BlockPos pos : BlockUtil.getSphere(mc.playerController.getBlockReachDistance(), BlockUtil.AirType.IgnoreAir, mc.player)) {
            if (!mc.world.getBlockState(pos.up()).getBlock().equals(Blocks.AIR) || !mc.world.getBlockState(pos.up().up()).getBlock().equals(Blocks.AIR) || (!mc.world.getBlockState(pos).getBlock().equals(Blocks.OBSIDIAN) && !mc.world.getBlockState(pos).getBlock().equals(Blocks.BEDROCK)))
                continue;
            double selfDamage = EntityUtil.calculatePosDamage(pos, mc.player);
            double enemyDamage = EntityUtil.calculatePosDamage(pos, entityPlayer);
            ArrayList<Entity> intersecting = mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(pos.up())).stream().filter(entity -> !(entity instanceof EntityEnderCrystal)).collect(Collectors.toCollection(ArrayList::new));
            if (selfDamage < maxSelfDamage.getValue() && minDamage.getValue() < enemyDamage && intersecting.isEmpty()) {
                posses.put(enemyDamage - selfDamage, pos);
            }
        }
        cantPlace = posses.isEmpty();
        if (!posses.isEmpty()) {
            BlockPos pos = posses.lastEntry().getValue();
            int slot = InventoryUtil.getItemFromHotbar(Items.END_CRYSTAL);
            int currentItem = mc.player.inventory.currentItem;
            if (slot != -1) {
                InventoryUtil.switchToSlot(slot);
            }
            EnumFacing facing = null;
            if (BlockUtil.hasBlockEnumFacing(pos))
                facing = BlockUtil.getFirstFacing(pos);
            Objects.requireNonNull(mc.getConnection()).sendPacket(new CPacketPlayerTryUseItemOnBlock(pos, facing != null ? facing : EnumFacing.UP, mc.player.getHeldItemOffhand().getItem() == Items.END_CRYSTAL ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND, 0.5f, 0.5f, 0.5f));
            if (slot != -1) {
                mc.player.inventory.currentItem = currentItem;
                mc.playerController.updateController();
            }
            this.pos = pos;
        }
    }

    @RegisterListener(priority = Priority.HIGHEST)
    public void onPacketReceive(PacketEvent.PacketReceiveEvent event) {
        if (nullCheck() || !isEnabled())
            return;
        if (event.getPacket() instanceof SPacketSoundEffect) {
            SPacketSoundEffect sPacketSoundEffect = new SPacketSoundEffect();
            if (sPacketSoundEffect.getCategory() == SoundCategory.BLOCKS && sPacketSoundEffect.getSound() == SoundEvents.ENTITY_GENERIC_EXPLODE) {
                List<Entity> loadedEntityList = mc.world.loadedEntityList;
                loadedEntityList.stream().filter(entity -> entity instanceof EntityEnderCrystal && entity.getDistanceSq(sPacketSoundEffect.getX(), sPacketSoundEffect.getY(), sPacketSoundEffect.getZ()) < (mc.playerController.getBlockReachDistance() * mc.playerController.getBlockReachDistance())).forEach(entity -> {
                    Objects.requireNonNull(mc.world.getEntityByID(entity.getEntityId())).setDead();
                    mc.world.removeEntityFromWorld(entity.entityId);
                });
            }
        }
    }

    @RegisterListener(priority = Priority.HIGHEST)
    public void onPacketSend(PacketEvent.PacketSendEvent event) {
        if (nullCheck() || !isEnabled())
            return;
        if (event.getPacket() instanceof CPacketPlayerTryUseItemOnBlock) {
            EntityEnderCrystal entityEnderCrystal = null;
            for (BlockPos pos : BlockUtil.getSphere(mc.playerController.getBlockReachDistance(), BlockUtil.AirType.IgnoreAir, mc.player)) {
                if (!mc.world.getBlockState(pos.up()).getBlock().equals(Blocks.AIR) || !mc.world.getBlockState(pos.up().up()).getBlock().equals(Blocks.AIR))
                    continue;
                for (Entity entity : mc.world.loadedEntityList) {
                    if (entity instanceof EntityEnderCrystal && entity.getDistanceSq(pos.up()) < 1.0f) {
                        entityEnderCrystal = (EntityEnderCrystal) entity;
                    }
                }
            }
            if (entityEnderCrystal == null) {
                return;
            }
            boolean switched = false;
            int currentItem = -1;
            PotionEffect weakness = mc.player.getActivePotionEffect(MobEffects.WEAKNESS);
            if (weakness != null && !mc.player.getHeldItemMainhand().getItem().equals(Items.DIAMOND_SWORD)) {
                int swordSlot = InventoryUtil.getItemFromHotbar(Items.DIAMOND_SWORD);
                currentItem = mc.player.inventory.currentItem;
                InventoryUtil.switchToSlot(swordSlot);
                switched = true;
            }
            CPacketUseEntity cPacketUseEntity = new CPacketUseEntity();
            cPacketUseEntity.entityId = entityEnderCrystal.getEntityId();
            cPacketUseEntity.action = CPacketUseEntity.Action.ATTACK;
            mc.player.connection.sendPacket(new CPacketAnimation(EnumHand.MAIN_HAND));
            mc.player.connection.sendPacket(cPacketUseEntity);
            if (switched) {
                mc.player.inventory.currentItem = currentItem;
                mc.playerController.updateController();
            }
            entityEnderCrystal.setDead();
            crystalsPerSecond.add(System.currentTimeMillis() + 1000L);
        }
    }

    @Override
    public void onGlobalRenderTick() {
        Long currentTime = System.currentTimeMillis();
        int i = 0;
        ArrayList<Long> crystalsPerSecond1 = new ArrayList<>(crystalsPerSecond);
        for (Long currentTimeMillis : crystalsPerSecond1) {
            if (currentTimeMillis < currentTime)
                crystalsPerSecond.remove(currentTimeMillis);
            else i++;
        }
        if (pos != null) {
            AxisAlignedBB bb = new AxisAlignedBB(pos);
            RenderUtil.drawBBBox(bb, Color.WHITE, 100);
            RenderUtil.drawBlockOutlineBB(bb, Color.WHITE, 1.0f);
            RenderUtil.drawText(pos, i + "");
        }
    }
}
