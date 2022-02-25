package dev.zprestige.ruby.mixins.render;

import dev.zprestige.ruby.module.visual.CrystalChams;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderEnderCrystal;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static org.lwjgl.opengl.GL11.*;

@Mixin(value = RenderEnderCrystal.class)
public abstract class MixinRenderEnderCrystal extends Render<EntityEnderCrystal> {
    @Final
    @Shadow
    private static ResourceLocation ENDER_CRYSTAL_TEXTURES;
    @Final
    @Shadow
    private ModelBase modelEnderCrystal;
    @Final
    @Shadow
    private ModelBase modelEnderCrystalNoBase;

    protected MixinRenderEnderCrystal(RenderManager renderManager) {
        super(renderManager);
    }

    @SuppressWarnings("NullableProblems")
    @Shadow
    public abstract void doRender(EntityEnderCrystal entity, double x, double y, double z, float entityYaw, float partialTicks);

    @Redirect(method = {"doRender(Lnet/minecraft/entity/item/EntityEnderCrystal;DDDFF)V"}, at = @At(value = "INVOKE", target = "Lnet/minecraft/client/model/ModelBase;render(Lnet/minecraft/entity/Entity;FFFFFF)V"))
    protected void bottomRenderRedirect(ModelBase modelBase, Entity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        if (!CrystalChams.Instance.isEnabled())
            modelBase.render(entityIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
    }

    @Inject(method = {"doRender(Lnet/minecraft/entity/item/EntityEnderCrystal;DDDFF)V"}, at = {@At("RETURN")})
    protected void doRenderCrystal(EntityEnderCrystal entity, double x, double y, double z, float entityYaw, float partialTicks, CallbackInfo ci) {
        if (CrystalChams.Instance.isEnabled()) {
            if (CrystalChams.Instance.fill.GetSwitch()) {
                float f3 = entity.innerRotation + partialTicks;
                GlStateManager.pushMatrix();
                GlStateManager.translate(x, y, z);
                Minecraft.getMinecraft().getRenderManager().renderEngine.bindTexture(ENDER_CRYSTAL_TEXTURES);
                float f4 = MathHelper.sin(f3 * 0.2f) / 2.0f + 0.5f;
                f4 += f4 * f4;
                GL11.glPushAttrib(1048575);
                GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL11.GL_FILL);
                GL11.glDisable(GL11.GL_TEXTURE_2D);
                if (CrystalChams.Instance.fillLighting.GetSwitch()) {
                    GL11.glEnable(GL11.GL_LIGHTING);
                } else {
                    GL11.glDisable(GL11.GL_LIGHTING);
                }
                GL11.glDisable(GL11.GL_DEPTH_TEST);
                GL11.glEnable(GL11.GL_LINE_SMOOTH);
                GL11.glEnable(GL11.GL_BLEND);
                GL11.glBlendFunc(770, 771);
                GL11.glColor4f(CrystalChams.Instance.fillColor.GetColor().getRed() / 255F, CrystalChams.Instance.fillColor.GetColor().getGreen() / 255F, CrystalChams.Instance.fillColor.GetColor().getBlue() / 255F, CrystalChams.Instance.fillColor.GetColor().getAlpha() / 255F);
                GL11.glScalef(CrystalChams.Instance.scale.GetSlider(), CrystalChams.Instance.scale.GetSlider(), CrystalChams.Instance.scale.GetSlider());
                if (CrystalChams.Instance.fillDepth.GetSwitch()) {
                    GL11.glDepthMask(true);
                    GL11.glEnable(GL_DEPTH_TEST);
                }
                if (entity.shouldShowBottom()) {
                    this.modelEnderCrystal.render(entity, 0.0f, f3 * 3.0f * CrystalChams.Instance.rotationSpeed.GetSlider(), f4 * 0.2f * CrystalChams.Instance.verticalSpeed.GetSlider(), 0.0f, 0.0f, 0.0625f);
                } else {
                    this.modelEnderCrystalNoBase.render(entity, 0.0f, f3 * 3.0f * CrystalChams.Instance.rotationSpeed.GetSlider(), f4 * 0.2f * CrystalChams.Instance.verticalSpeed.GetSlider(), 0.0f, 0.0f, 0.0625f);
                }
                if (CrystalChams.Instance.fillDepth.GetSwitch()) {
                    GL11.glDisable(GL_DEPTH_TEST);
                    GL11.glDepthMask(false);
                }
                GL11.glScalef(1F / CrystalChams.Instance.scale.GetSlider(), 1F / CrystalChams.Instance.scale.GetSlider(), 1F / CrystalChams.Instance.scale.GetSlider());
                GL11.glPopAttrib();
                GL11.glPopMatrix();
            }
            if (CrystalChams.Instance.outline.GetSwitch()) {
                float f3 = entity.innerRotation + partialTicks;
                GlStateManager.pushMatrix();
                GlStateManager.translate(x, y, z);
                Minecraft.getMinecraft().getRenderManager().renderEngine.bindTexture(ENDER_CRYSTAL_TEXTURES);
                float f4 = MathHelper.sin(f3 * 0.2f) / 2.0f + 0.5f;
                f4 += f4 * f4;
                GL11.glPushAttrib(1048575);
                GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL_LINE);
                GL11.glDisable(GL11.GL_TEXTURE_2D);
                GL11.glDisable(GL11.GL_LIGHTING);
                GL11.glDisable(GL11.GL_DEPTH_TEST);
                GL11.glEnable(GL11.GL_LINE_SMOOTH);
                GL11.glEnable(GL11.GL_BLEND);
                GL11.glBlendFunc(770, 771);
                GL11.glLineWidth(CrystalChams.Instance.outlineWidth.GetSlider());
                GL11.glColor4f(CrystalChams.Instance.outlineColor.GetColor().getRed() / 255F, CrystalChams.Instance.outlineColor.GetColor().getGreen() / 255F, CrystalChams.Instance.outlineColor.GetColor().getBlue() / 255F, CrystalChams.Instance.outlineColor.GetColor().getAlpha() / 255F);
                GL11.glScalef(CrystalChams.Instance.scale.GetSlider(), CrystalChams.Instance.scale.GetSlider(), CrystalChams.Instance.scale.GetSlider());
                if (CrystalChams.Instance.outlineDepth.GetSwitch()) {
                    GL11.glDepthMask(true);
                    GL11.glEnable(GL_DEPTH_TEST);
                }
                if (entity.shouldShowBottom()) {
                    this.modelEnderCrystal.render(entity, 0.0f, f3 * 3.0f * CrystalChams.Instance.rotationSpeed.GetSlider(), f4 * 0.2f * CrystalChams.Instance.verticalSpeed.GetSlider(), 0.0f, 0.0f, 0.0625f);
                } else {
                    this.modelEnderCrystalNoBase.render(entity, 0.0f, f3 * 3.0f * CrystalChams.Instance.rotationSpeed.getMin(), f4 * 0.2f * CrystalChams.Instance.verticalSpeed.GetSlider(), 0.0f, 0.0f, 0.0625f);
                }
                if (CrystalChams.Instance.outlineDepth.GetSwitch()) {
                    GL11.glDisable(GL_DEPTH_TEST);
                    GL11.glDepthMask(false);
                }
                GL11.glScalef(1F / CrystalChams.Instance.scale.GetSlider(), 1F / CrystalChams.Instance.scale.GetSlider(), 1F / CrystalChams.Instance.scale.GetSlider());
                GL11.glPopAttrib();
                GL11.glPopMatrix();
            }
            if (CrystalChams.Instance.glint.GetSwitch()) {
                float f3 = entity.innerRotation + partialTicks;
                GlStateManager.pushMatrix();
                GlStateManager.translate(x, y, z);
                Minecraft.getMinecraft().getRenderManager().renderEngine.bindTexture(CrystalChams.ENCHANTED_ITEM_GLINT_RES);
                float f4 = MathHelper.sin(f3 * 0.2f) / 2.0f + 0.5f;
                f4 += f4 * f4;
                GL11.glPushAttrib(1048575);
                GL11.glPolygonMode(GL11.GL_FRONT_AND_BACK, GL_FILL);
                GL11.glDisable(GL11.GL_LIGHTING);
                GL11.glDisable(GL11.GL_DEPTH_TEST);
                GL11.glEnable(GL11.GL_BLEND);
                GL11.glColor4f(CrystalChams.Instance.glintColor.GetColor().getRed() / 255F, CrystalChams.Instance.glintColor.GetColor().getGreen() / 255F, CrystalChams.Instance.glintColor.GetColor().getBlue() / 255F, CrystalChams.Instance.glintColor.GetColor().getAlpha() / 255F);
                GL11.glScalef(CrystalChams.Instance.scale.GetSlider(), CrystalChams.Instance.scale.GetSlider(), CrystalChams.Instance.scale.GetSlider());
                GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_COLOR, GlStateManager.DestFactor.ONE);
                for (int i = 0; i < 2; ++i) {
                    GlStateManager.matrixMode(GL11.GL_TEXTURE);
                    GlStateManager.loadIdentity();
                    float tScale = 0.33333334F * CrystalChams.Instance.glintScale.GetSlider();
                    GlStateManager.scale(tScale, tScale, tScale);
                    GlStateManager.rotate(30.0F - (float) i * 60.0F, 0.0F, 0.0F, 1.0F);
                    GlStateManager.translate(0.0F, (entity.ticksExisted + partialTicks) * (0.001F + (float) i * 0.003F) * CrystalChams.Instance.glintSpeed.GetSlider(), 0.0F);
                    GlStateManager.matrixMode(GL11.GL_MODELVIEW);
                    if (CrystalChams.Instance.glintDepth.GetSwitch()) {
                        GL11.glDepthMask(true);
                        GL11.glEnable(GL_DEPTH_TEST);
                    }
                    if (entity.shouldShowBottom())
                        this.modelEnderCrystal.render(entity, 0.0f, f3 * 3.0f * CrystalChams.Instance.rotationSpeed.GetSlider(), f4 * 0.2f * CrystalChams.Instance.verticalSpeed.GetSlider(), 0.0f, 0.0f, 0.0625f);
                    else
                        this.modelEnderCrystalNoBase.render(entity, 0.0f, f3 * 3.0f * CrystalChams.Instance.rotationSpeed.GetSlider(), f4 * 0.2f * CrystalChams.Instance.verticalSpeed.GetSlider(), 0.0f, 0.0f, 0.0625f);
                    if (CrystalChams.Instance.glintDepth.GetSwitch()) {
                        GL11.glDisable(GL_DEPTH_TEST);
                        GL11.glDepthMask(false);
                    }
                }
                GlStateManager.matrixMode(5890);
                GlStateManager.loadIdentity();
                GlStateManager.matrixMode(5888);
                GlStateManager.blendFunc(GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
                GL11.glScalef(1F / CrystalChams.Instance.scale.GetSlider(), 1F / CrystalChams.Instance.scale.GetSlider(), 1F / CrystalChams.Instance.scale.GetSlider());
                GL11.glPopAttrib();
                GL11.glPopMatrix();
            }
        }
    }
}
