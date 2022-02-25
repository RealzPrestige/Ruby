package dev.zprestige.ruby.module.visual;

import dev.zprestige.ruby.module.Module;
import dev.zprestige.ruby.settings.impl.ColorBox;
import dev.zprestige.ruby.settings.impl.Slider;
import dev.zprestige.ruby.settings.impl.Switch;
import dev.zprestige.ruby.util.AnimationUtil;
import dev.zprestige.ruby.util.EntityUtil;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;

public class Crosshair extends Module {
    public final Slider distance = Menu.Slider("Gap", 0, 100);
    public final Slider length = Menu.Slider("Length", 0, 100);
    public final Slider thickness = Menu.Slider("Thickness", 0, 50);
    public final Switch dynamic = Menu.Switch("Dynamic");
    public final Switch dynamicAnimated = Menu.Switch("Dynamic Animated");
    public final Slider dynamicGap = Menu.Slider("Dynamic Gap", 0, 100);
    public final Slider dynamicAnimationSpeed = Menu.Slider("Dynamic Animation Speed", 0.1f, 5.0f);
    public final ColorBox color = Menu.Color("Color");
    float newGap;

    @Override
    public void onTick() {
        if (dynamicAnimated.GetSwitch()) {
            if (EntityUtil.isMoving())
                newGap = AnimationUtil.increaseNumber(newGap, dynamicGap.GetSlider() / 10f, dynamicAnimationSpeed.GetSlider());
            else
                newGap = AnimationUtil.decreaseNumber(newGap, 0, dynamicAnimationSpeed.GetSlider());
        } else {
            if (EntityUtil.isMoving())
                newGap = dynamicGap.GetSlider() / 10f;
            else
                newGap = 0;
        }
    }

    @SubscribeEvent
    public void onRenderGameOverlay(RenderGameOverlayEvent event) {
        if (event.getType() == RenderGameOverlayEvent.ElementType.CROSSHAIRS && isEnabled()) {
            event.setCanceled(true);
            drawCrosshairs(distance.GetSlider() / 10f, length.GetSlider() / 10f, thickness.GetSlider() / 10f, dynamic.GetSwitch(), newGap, color.GetColor().getRGB());
        }
    }

    public void drawCrosshairs(double gap, double width, double thickness, boolean dynamic, float dynamicSeparation, int color) {
        int scaledScreenWidth = new ScaledResolution(mc).getScaledWidth();
        int scaledScreenHeight = new ScaledResolution(mc).getScaledHeight();
        if (dynamic)
            gap += dynamicSeparation;
        drawLine((float) ((scaledScreenWidth / 2) - gap), (float) ((scaledScreenHeight / 2)), (float) ((scaledScreenWidth / 2) - gap - width), (float) ((scaledScreenHeight / 2)), (int) thickness, color);
        drawLine((float) ((scaledScreenWidth / 2) + gap), (float) ((scaledScreenHeight / 2)), (float) ((scaledScreenWidth / 2) + gap + width), (float) ((scaledScreenHeight / 2)), (int) thickness, color);
        drawLine((float) ((scaledScreenWidth / 2)), (float) ((scaledScreenHeight / 2) - gap), (float) ((scaledScreenWidth / 2)), (float) ((scaledScreenHeight / 2) - gap - width), (int) thickness, color);
        drawLine((float) ((scaledScreenWidth / 2)), (float) ((scaledScreenHeight / 2) + gap), (float) ((scaledScreenWidth / 2)), (float) ((scaledScreenHeight / 2) + gap + width), (int) thickness, color);
    }

    public void drawLine(float x, float y, float x1, float y1, float thickness, int hex) {
        float red = (float) (hex >> 16 & 0xFF) / 255.0f;
        float green = (float) (hex >> 8 & 0xFF) / 255.0f;
        float blue = (float) (hex & 0xFF) / 255.0f;
        float alpha = (float) (hex >> 24 & 0xFF) / 255.0f;
        GlStateManager.pushMatrix();
        GlStateManager.disableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.disableAlpha();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.shadeModel(7425);
        GL11.glLineWidth(thickness);
        GL11.glEnable(2848);
        GL11.glHint(3154, 4354);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(3, DefaultVertexFormats.POSITION_COLOR);
        bufferbuilder.pos(x, y, 0.0).color(red, green, blue, alpha).endVertex();
        bufferbuilder.pos(x1, y1, 0.0).color(red, green, blue, alpha).endVertex();
        tessellator.draw();
        GlStateManager.shadeModel(7424);
        GL11.glDisable(2848);
        GlStateManager.disableBlend();
        GlStateManager.enableAlpha();
        GlStateManager.enableTexture2D();
        GlStateManager.popMatrix();
    }
}
