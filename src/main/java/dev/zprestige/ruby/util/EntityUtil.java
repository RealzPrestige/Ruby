package dev.zprestige.ruby.util;

import dev.zprestige.ruby.Ruby;
import dev.zprestige.ruby.events.MoveEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.item.*;
import net.minecraft.network.play.client.CPacketAnimation;
import net.minecraft.potion.Potion;
import net.minecraft.util.CombatRules;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Explosion;

import java.util.List;
import java.util.Objects;

public class EntityUtil {
    public static Minecraft mc = Minecraft.getMinecraft();

    public static void swingArm(SwingType swingType) {
        if (swingType.equals(SwingType.MainHand))
            mc.player.swingArm(EnumHand.MAIN_HAND);
        else if (swingType.equals(SwingType.OffHand))
            mc.player.swingArm(EnumHand.OFF_HAND);
        else if (swingType.equals(SwingType.Packet))
            mc.player.connection.sendPacket(new CPacketAnimation(mc.player.getHeldItemMainhand().getItem().equals(Items.END_CRYSTAL) ? EnumHand.MAIN_HAND : EnumHand.OFF_HAND));
    }

    public static void setSpeed(final EntityLivingBase entity, final double speed) {
        double[] dir = getSpeed(speed);
        entity.motionX = dir[0];
        entity.motionZ = dir[1];
    }

    public static void setMoveSpeed(MoveEvent event, double speed) {
        double forward = mc.player.movementInput.moveForward;
        double strafe = mc.player.movementInput.moveStrafe;
        float yaw = mc.player.rotationYaw;
        if (forward == 0.0 && strafe == 0.0) {
            event.motionX = 0.0;
            event.motionZ = 0.0;
            mc.player.motionX = 0.0;
            mc.player.motionZ = 0.0;
        } else {
            if (forward != 0.0) {
                if (strafe > 0.0) {
                    yaw += (float) (forward > 0.0 ? -45 : 45);
                } else if (strafe < 0.0) {
                    yaw += (float) (forward > 0.0 ? 45 : -45);
                }
                strafe = 0.0;
                if (forward > 0.0) {
                    forward = 1.0;
                } else if (forward < 0.0) {
                    forward = -1.0;
                }
            }
            double x = forward * speed * -Math.sin(Math.toRadians(yaw)) + strafe * speed * Math.cos(Math.toRadians(yaw));
            double z = forward * speed * Math.cos(Math.toRadians(yaw)) - strafe * speed * -Math.sin(Math.toRadians(yaw));
            event.motionX = x;
            event.motionZ = z;
            mc.player.motionX = x;
            mc.player.motionZ = z;
        }
    }

    public static double getDefaultSpeed() {
        double defaultSpeed = 0.2873;
        if (mc.player.isPotionActive(MobEffects.SPEED)) {
            final int amplifier = Objects.requireNonNull(mc.player.getActivePotionEffect(MobEffects.SPEED)).getAmplifier();
            defaultSpeed *= 1.0 + 0.2 * (amplifier + 1);
        }
        if (mc.player.isPotionActive(MobEffects.JUMP_BOOST)) {
            final int amplifier = Objects.requireNonNull(mc.player.getActivePotionEffect(MobEffects.SPEED)).getAmplifier();
            defaultSpeed /= 1.0 + 0.2 * (amplifier + 1);
        }
        return defaultSpeed;
    }

    public static Entity getPredictedPosition(Entity entity, double x) {
        if (x == 0) return entity;
        EntityPlayer e = null;
        double motionX = entity.posX - entity.lastTickPosX;
        double motionY = entity.posY - entity.lastTickPosY;
        double motionZ = entity.posZ - entity.lastTickPosZ;
        boolean shouldPredict = false;
        boolean shouldStrafe = false;
        double motion = Math.sqrt(Math.pow(motionX, 2) + Math.pow(motionZ, 2) + Math.pow(motionY, 2));
        if (motion > 0.1) {
            shouldPredict = true;
        }
        if (!shouldPredict) {
            return entity;
        }
        if (motion > 0.31) {
            shouldStrafe = true;
        }
        for (int i = 0; i < x; i++) {
            if (e == null) {
                if (isOnGround(0, 0, 0, entity)) {
                    motionY = shouldStrafe ? 0.4 : -0.07840015258789;
                } else {
                    motionY -= 0.08;
                    motionY *= 0.9800000190734863D;
                }
                e = placeValue(motionX, motionY, motionZ, (EntityPlayer) entity);
            } else {
                if (isOnGround(0, 0, 0, e)) {
                    motionY = shouldStrafe ? 0.4 : -0.07840015258789;
                } else {
                    motionY -= 0.08;
                    motionY *= 0.9800000190734863D;
                }
                e = placeValue(motionX, motionY, motionZ, e);
            }
        }
        return e;
    }

    public static boolean isOnGround(double x, double y, double z, Entity entity) {
        try {
            double d3 = y;
            List<AxisAlignedBB> list1 = mc.world.getCollisionBoxes(entity, entity.getEntityBoundingBox().expand(x, y, z));
            if (y != 0.0D) {
                int k = 0;
                for (int l = list1.size(); k < l; ++k) {
                    y = (list1.get(k)).calculateYOffset(entity.getEntityBoundingBox(), y);
                }
            }
            return d3 != y && d3 < 0.0D;
        } catch (Exception ignored) {
            return false;
        }
    }

    public static EntityPlayer placeValue(double x, double y, double z, EntityPlayer entity) {
        List<AxisAlignedBB> list1 = mc.world.getCollisionBoxes(entity, entity.getEntityBoundingBox().expand(x, y, z));

        if (y != 0.0D) {
            int k = 0;
            for (int l = list1.size(); k < l; ++k) {
                y = (list1.get(k)).calculateYOffset(entity.getEntityBoundingBox(), y);
            }
            if (y != 0.0D) {
                entity.setEntityBoundingBox(entity.getEntityBoundingBox().offset(0.0D, y, 0.0D));
            }
        }

        if (x != 0.0D) {
            int j5 = 0;
            for (int l5 = list1.size(); j5 < l5; ++j5) {
                x = calculateXOffset(entity.getEntityBoundingBox(), x, list1.get(j5));
            }
            if (x != 0.0D) {
                entity.setEntityBoundingBox(entity.getEntityBoundingBox().offset(x, 0.0D, 0.0D));
            }
        }

        if (z != 0.0D) {
            int k5 = 0;
            for (int i6 = list1.size(); k5 < i6; ++k5) {
                z = calculateZOffset(entity.getEntityBoundingBox(), z, list1.get(k5));
            }
            if (z != 0.0D) {
                entity.setEntityBoundingBox(entity.getEntityBoundingBox().offset(0.0D, 0.0D, z));
            }
        }
        return entity;
    }

    public static double calculateXOffset(AxisAlignedBB other, double offsetX, AxisAlignedBB this1) {
        if (other.maxY > this1.minY && other.minY < this1.maxY && other.maxZ > this1.minZ && other.minZ < this1.maxZ) {
            if (offsetX > 0.0D && other.maxX <= this1.minX) {
                double d1 = (this1.minX - 0.3) - other.maxX;

                if (d1 < offsetX) {
                    offsetX = d1;
                }
            } else if (offsetX < 0.0D && other.minX >= this1.maxX) {
                double d0 = (this1.maxX + 0.3) - other.minX;

                if (d0 > offsetX) {
                    offsetX = d0;
                }
            }
        }
        return offsetX;
    }

    public static double calculateZOffset(AxisAlignedBB other, double offsetZ, AxisAlignedBB this1) {
        if (other.maxX > this1.minX && other.minX < this1.maxX && other.maxY > this1.minY && other.minY < this1.maxY) {
            if (offsetZ > 0.0D && other.maxZ <= this1.minZ) {
                double d1 = (this1.minZ - 0.3) - other.maxZ;
                if (d1 < offsetZ) {
                    offsetZ = d1;
                }
            } else if (offsetZ < 0.0D && other.minZ >= this1.maxZ) {
                double d0 = (this1.maxZ + 0.3) - other.minZ;
                if (d0 > offsetZ) {
                    offsetZ = d0;
                }
            }

        }
        return offsetZ;
    }

    public static double yawDist(Entity e) {
        if (e != null) {
            Vec3d difference = e.getPositionVector().add(0.0, e.getEyeHeight() / 2.0f, 0.0).subtract(mc.player.getPositionEyes(mc.getRenderPartialTicks()));
            double d = Math.abs((double) mc.player.rotationYaw - (Math.toDegrees(Math.atan2(difference.z, difference.x)) - 90.0)) % 360.0;
            return d > 180.0 ? 360.0 - d : d;
        }
        return 0.0;
    }

    public static int getRoundedDamage(ItemStack stack) {
        return (int) getDamageInPercent(stack);
    }

    public static boolean hasDurability(ItemStack stack) {
        Item item = stack.getItem();
        return item instanceof ItemArmor || item instanceof ItemSword || item instanceof ItemTool || item instanceof ItemShield;
    }

    public static float getDamageInPercent(final ItemStack stack) {
        final float green = (stack.getMaxDamage() - (float) stack.getItemDamage()) / stack.getMaxDamage();
        final float red = 1.0f - green;
        return (float) (100 - (int) (red * 100.0f));
    }

    public static float getHealth(Entity entity) {
        if (EntityUtil.isLiving(entity)) {
            EntityLivingBase livingBase = (EntityLivingBase) entity;
            return livingBase.getHealth() + livingBase.getAbsorptionAmount();
        }
        return 0.0f;
    }

    public static BlockPos getPlayerPos(EntityPlayer player) {
        return new BlockPos(Math.floor(player.posX), Math.floor(player.posY), Math.floor(player.posZ));
    }

    public static float calculateEntityDamage(EntityEnderCrystal crystal, EntityPlayer player) {
        return calculatePosDamage(crystal.posX, crystal.posY, crystal.posZ, player);
    }

    public static float calculatePosDamage(BlockPos position, EntityPlayer player) {
        return calculatePosDamage(position.getX() + 0.5, position.getY() + 1.0, position.getZ() + 0.5, player);
    }

    public static float calculatePosDamage(double posX, double posY, double posZ, Entity entity) {
        float doubleSize = 12.0F;
        double size = entity.getDistance(posX, posY, posZ) / (double) doubleSize;
        Vec3d vec3d = new Vec3d(posX, posY, posZ);
        double blockDensity = entity.world.getBlockDensity(vec3d, entity.getEntityBoundingBox());
        double value = (1.0D - size) * blockDensity;
        float damage = (float) ((int) ((value * value + value) / 2.0D * 7.0D * (double) doubleSize + 1.0D));
        double finalDamage = 1.0D;

        if (entity instanceof EntityLivingBase) {
            finalDamage = getBlastReduction((EntityLivingBase) entity, getMultipliedDamage(damage), new Explosion(mc.world, null, posX, posY, posZ, 6.0F, false, true));
        }

        return (float) finalDamage;
    }

    private static float getMultipliedDamage(float damage) {
        return damage * (mc.world.getDifficulty().getId() == 0 ? 0.0F : (mc.world.getDifficulty().getId() == 2 ? 1.0F : (mc.world.getDifficulty().getId() == 1 ? 0.5F : 1.5F)));
    }

    public static float getBlastReduction(EntityLivingBase entity, float damageI, Explosion explosion) {
        float damage = damageI;
        final DamageSource ds = DamageSource.causeExplosionDamage(explosion);
        damage = CombatRules.getDamageAfterAbsorb(damage, entity.getTotalArmorValue(), (float) entity.getEntityAttribute(SharedMonsterAttributes.ARMOR_TOUGHNESS).getAttributeValue());

        final int k = EnchantmentHelper.getEnchantmentModifierDamage(entity.getArmorInventoryList(), ds);
        damage = damage * (1.0F - MathHelper.clamp(k, 0.0F, 20.0F) / 25.0F);

        if (entity.isPotionActive(MobEffects.RESISTANCE)) {
            damage = damage - (damage / 4);
        }

        return damage;
    }

    public static EntityPlayer getTarget(float range) {
        EntityPlayer currentTarget = null;
        for (int size = mc.world.playerEntities.size(), i = 0; i < size; ++i) {
            EntityPlayer player = mc.world.playerEntities.get(i);
            if (!EntityUtil.isntValid(player, range)) {
                if (currentTarget == null) {
                    currentTarget = player;
                } else if (mc.player.getDistanceSq(player) < mc.player.getDistanceSq(currentTarget)) {
                    currentTarget = player;
                }
            }
        }
        return currentTarget;
    }

    public static EntityPlayer getTargetSafe(float range) {
        EntityPlayer currentTarget = null;
        for (int size = mc.world.playerEntities.size(), i = 0; i < size; ++i) {
            EntityPlayer player = mc.world.playerEntities.get(i);
            if (!EntityUtil.isntValid(player, range)) {
                if (!BlockUtil.isPlayerSafe(player))
                    continue;
                if (currentTarget == null) {
                    currentTarget = player;
                } else if (mc.player.getDistanceSq(player) < mc.player.getDistanceSq(currentTarget)) {
                    currentTarget = player;
                }
            }
        }
        return currentTarget;
    }

    public static boolean isntValid(Entity entity, double range) {
        return entity == null || isDead(entity) || entity.equals(mc.player) || entity instanceof EntityPlayer && Ruby.friendManager.isFriend(entity.getName()) || mc.player.getDistanceSq(entity) > (range * range);
    }

    public static boolean isAlive(Entity entity) {
        return isLiving(entity) && !entity.isDead && ((EntityLivingBase) entity).getHealth() > 0.0f;
    }

    public static boolean isDead(Entity entity) {
        return !isAlive(entity);
    }

    public static boolean isLiving(Entity entity) {
        return entity instanceof EntityLivingBase;
    }

    public static boolean isMoving() {
        return (double) mc.player.moveForward != 0.0 || (double) mc.player.moveStrafing != 0.0;
    }

    public static double getBaseMotionSpeed() {
        double event = 0.272D;
        if (mc.player.isPotionActive(MobEffects.SPEED)) {
            int var3 = Objects.requireNonNull(mc.player.getActivePotionEffect(MobEffects.SPEED)).getAmplifier();
            event *= 1.0D + 0.2D * (double) var3;
        }
        return event;
    }

    public static double getMaxSpeed() {
        double maxModifier = 0.2873;
        if (mc.player.isPotionActive(Objects.requireNonNull(Potion.getPotionById(1)))) {
            maxModifier *= 1.0 + 0.2 * (Objects.requireNonNull(mc.player.getActivePotionEffect(Objects.requireNonNull(Potion.getPotionById(1)))).getAmplifier() + 1);
        }
        return maxModifier;
    }

    public static double[] getSpeed(double speed) {
        float moveForward = mc.player.movementInput.moveForward;
        float moveStrafe = mc.player.movementInput.moveStrafe;
        float rotationYaw = mc.player.prevRotationYaw + (mc.player.rotationYaw - mc.player.prevRotationYaw) * mc.getRenderPartialTicks();
        if (moveForward != 0.0f) {
            if (moveStrafe > 0.0f)
                rotationYaw += (float) (moveForward > 0.0f ? -45 : 45);
            else if (moveStrafe < 0.0f)
                rotationYaw += (float) (moveForward > 0.0f ? 45 : -45);
            moveStrafe = 0.0f;
            if (moveForward > 0.0f)
                moveForward = 1.0f;
            else if (moveForward < 0.0f)
                moveForward = -1.0f;
        }
        double posX = (double) moveForward * speed * -Math.sin(Math.toRadians(rotationYaw)) + (double) moveStrafe * speed * Math.cos(Math.toRadians(rotationYaw));
        double posZ = (double) moveForward * speed * Math.cos(Math.toRadians(rotationYaw)) - (double) moveStrafe * speed * -Math.sin(Math.toRadians(rotationYaw));
        return new double[]{posX, posZ};
    }

    public enum SwingType {
        MainHand, OffHand, Packet
    }
}
