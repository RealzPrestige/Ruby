package dev.zprestige.ruby.mixins.gui;

import dev.zprestige.ruby.Ruby;
import dev.zprestige.ruby.module.visual.ShulkerPeek;
import dev.zprestige.ruby.util.RenderUtil;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.ItemShulkerBox;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.awt.*;

@Mixin({GuiScreen.class})
public class MixinGuiScreen extends Gui {

    @Inject(method = {"renderToolTip"}, at = {@At("HEAD")}, cancellable = true)
    public void renderToolTipHook(ItemStack stack, int x, int y, CallbackInfo info) {
        if (stack.getItem() instanceof ItemShulkerBox && ShulkerPeek.Instance.isEnabled()) {
            renderShulkerToolTip(stack, x, y, null);
            info.cancel();
        }
    }

    public void renderShulkerToolTip(ItemStack stack, int x, int y, String name) {
        NBTTagCompound tagCompound = stack.getTagCompound();
        if (tagCompound != null && tagCompound.hasKey("BlockEntityTag", 10)) {
            NBTTagCompound blockEntityTag = tagCompound.getCompoundTag("BlockEntityTag");
            if (blockEntityTag.hasKey("Items", 9)) {
                GlStateManager.enableTexture2D();
                GlStateManager.disableLighting();
                GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
                GlStateManager.enableBlend();
                GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
                Ruby.mc.getTextureManager().bindTexture(new ResourceLocation("textures/gui/container/shulker_box.png"));
                drawTexturedRect(x, y, 0, 0, 176, 16, 500);
                drawTexturedRect(x, y + 16, 0, 16, 176, 54, 500);
                drawTexturedRect(x, y + 16 + 54, 0, 160, 176, 8, 500);
                GlStateManager.disableDepth();
                Ruby.mc.fontRenderer.drawStringWithShadow((name == null) ? stack.getDisplayName() : name, (float) (x + 8), (float) (y + 6), new Color(1.0f, 1.0f, 1.0f, 1.0f).getRGB());
                GlStateManager.enableDepth();
                RenderHelper.enableGUIStandardItemLighting();
                GlStateManager.enableRescaleNormal();
                GlStateManager.enableColorMaterial();
                GlStateManager.enableLighting();
                NonNullList<ItemStack> nonnulllist = NonNullList.withSize(27, ItemStack.EMPTY);
                ItemStackHelper.loadAllItems(blockEntityTag, nonnulllist);
                for (int i = 0; i < nonnulllist.size(); ++i) {
                    int iX = x + i % 9 * 18 + 8;
                    int iY = y + i / 9 * 18 + 18;
                    ItemStack itemStack = nonnulllist.get(i);
                    Ruby.mc.getRenderItem().zLevel = 501.0f;
                    RenderUtil.itemRender.renderItemAndEffectIntoGUI(itemStack, iX, iY);
                    RenderUtil.itemRender.renderItemOverlayIntoGUI(Ruby.mc.fontRenderer, itemStack, iX, iY, null);
                    Ruby.mc.getRenderItem().zLevel = 0.0f;
                }
                GlStateManager.disableLighting();
                GlStateManager.disableBlend();
                GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
            }
        }
    }

    public void drawTexturedRect(int x, int y, int textureX, int textureY, int width, int height, int zLevel) {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder BufferBuilder2 = tessellator.getBuffer();
        BufferBuilder2.begin(7, DefaultVertexFormats.POSITION_TEX);
        BufferBuilder2.pos(x, y + height, zLevel).tex((float) (textureX) * 0.00390625f, (float) (textureY + height) * 0.00390625f).endVertex();
        BufferBuilder2.pos(x + width, y + height, zLevel).tex((float) (textureX + width) * 0.00390625f, (float) (textureY + height) * 0.00390625f).endVertex();
        BufferBuilder2.pos(x + width, y, zLevel).tex((float) (textureX + width) * 0.00390625f, (float) (textureY) * 0.00390625f).endVertex();
        BufferBuilder2.pos(x, y, zLevel).tex((float) (textureX) * 0.00390625f, (float) (textureY) * 0.00390625f).endVertex();
        tessellator.draw();
    }


    @Redirect(method = "drawBackground", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/texture/TextureManager;bindTexture(Lnet/minecraft/util/ResourceLocation;)V"))
    public void drawBackground(TextureManager instance, ResourceLocation resource){

    }
}