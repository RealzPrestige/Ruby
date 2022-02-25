package dev.zprestige.ruby.mixins.entity;

import com.mojang.authlib.GameProfile;
import dev.zprestige.ruby.module.misc.NoInterpolation;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(value = EntityOtherPlayerMP.class)
public class MixinEntityOtherPlayerMP extends AbstractClientPlayer {

    @Shadow
    private int otherPlayerMPPosRotationIncrements;
    @Shadow
    private double otherPlayerMPX;
    @Shadow
    private double otherPlayerMPY;
    @Shadow
    private double otherPlayerMPZ;
    @Shadow
    private double otherPlayerMPYaw;
    @Shadow
    private double otherPlayerMPPitch;

    public MixinEntityOtherPlayerMP(World worldIn, GameProfile gameProfileIn) {
        super(worldIn, gameProfileIn);
    }

    /**
     * @author joe
     * @reason mama
     */

    @Overwrite
    public void onLivingUpdate() {
        if (otherPlayerMPPosRotationIncrements > 0) {
            double f1;
            double d1;
            double d2;

            if (NoInterpolation.Instance.isEnabled()) {
                f1 = (double) serverPosX / 4096.0D;
                d1 = (double) serverPosY / 4096.0D;
                d2 = (double) serverPosZ / 4096.0D;
            } else {
                f1 = posX + (otherPlayerMPX - posX) / (double) otherPlayerMPPosRotationIncrements;
                d1 = posY + (otherPlayerMPY - posY) / (double) otherPlayerMPPosRotationIncrements;
                d2 = posZ + (otherPlayerMPZ - posZ) / (double) otherPlayerMPPosRotationIncrements;
            }

            double d3;

            d3 = otherPlayerMPYaw - (double) rotationYaw;
            while (d3 < -180.0D)
                d3 += 360.0D;


            while (d3 >= 180.0D)
                d3 -= 360.0D;

            rotationYaw = (float) ((double) rotationYaw + d3 / (double) otherPlayerMPPosRotationIncrements);
            rotationPitch = (float) ((double) rotationPitch + (otherPlayerMPPitch - (double) rotationPitch) / (double) otherPlayerMPPosRotationIncrements);
            --otherPlayerMPPosRotationIncrements;
            setPosition(f1, d1, d2);
            setRotation(rotationYaw, rotationPitch);
        }

        prevCameraYaw = cameraYaw;
        updateArmSwingProgress();
        float f11 = MathHelper.sqrt(motionX * motionX + motionZ * motionZ);
        float f = (float) Math.atan(-motionY * 0.20000000298023224D) * 15.0F;

        if (f11 > 0.1F)
            f11 = 0.1F;

        if (!onGround || getHealth() <= 0.0F)
            f11 = 0.0F;

        if (onGround || getHealth() <= 0.0F)
            f = 0.0F;

        cameraYaw += (f11 - cameraYaw) * 0.4F;
        cameraPitch += (f - cameraPitch) * 0.8F;
        world.profiler.startSection("push");
        collideWithNearbyEntities();
        world.profiler.endSection();
    }
}