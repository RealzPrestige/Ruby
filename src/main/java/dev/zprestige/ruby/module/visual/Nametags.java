package dev.zprestige.ruby.module.visual;

import com.mojang.realmsclient.gui.ChatFormatting;
import dev.zprestige.ruby.Ruby;
import dev.zprestige.ruby.events.Render3DEvent;
import dev.zprestige.ruby.module.Module;
import dev.zprestige.ruby.settings.impl.Switch;
import dev.zprestige.ruby.util.EntityUtil;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTool;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.opengl.GL11;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.lwjgl.opengl.GL11.*;

public class Nametags extends Module {
    public static Nametags Instance;
    public final Switch multiThreaded = Menu.Switch("Multi Threaded");
    public final Switch health = Menu.Switch("Health");
    public final Switch ping = Menu.Switch("Ping");
    public final Switch totemPops = Menu.Switch("Totem Pops");
    public final Switch inFrustum = Menu.Switch("In Frustum");
    public List<EntityPlayer> entityPlayers = new ArrayList<>();
    public ICamera camera = new Frustum();

    public Nametags() {
        Instance = this;
    }

    public static float roundNumber(double value, int places) {
        if (places < 0)
            throw new IllegalArgumentException();
        BigDecimal decimal = BigDecimal.valueOf(value);
        decimal = decimal.setScale(places, RoundingMode.FLOOR);
        return decimal.floatValue();
    }

    @Override
    public void onFrame(float partialTicks) {
        if (multiThreaded.GetSwitch()) {
            Ruby.threadManager.run(() -> entityPlayers = mc.world.playerEntities);
        } else {
            entityPlayers = mc.world.playerEntities;
        }
        if (!entityPlayers.isEmpty()) {
            if (inFrustum.GetSwitch())
                camera.setPosition(Objects.requireNonNull(mc.getRenderViewEntity()).posX, mc.getRenderViewEntity().posY, mc.getRenderViewEntity().posZ);
            for (EntityPlayer entity : entityPlayers) {
                if (entity.isSpectator())
                    continue;
                if (inFrustum.GetSwitch() && !camera.isBoundingBoxInFrustum(entity.getEntityBoundingBox().grow(2)))
                    continue;
                if (entity.getName().equals("FakePlayer"))
                    continue;
                if (entity.equals(mc.player))
                    continue;
                glPushMatrix();
                try {
                    renderFullNametag(entity, interpolate(entity.lastTickPosX, entity.posX, partialTicks) - mc.getRenderManager().renderPosX, interpolate(entity.lastTickPosY, entity.posY, partialTicks) - mc.getRenderManager().renderPosY, interpolate(entity.lastTickPosZ, entity.posZ, partialTicks) - mc.getRenderManager().renderPosZ, partialTicks);
                } catch (Exception ignored) {
                }
                glColor4f(1f, 1f, 1f, 1f);
                glPopMatrix();
            }
        }
    }

    public ChatFormatting getPopsColor(int i) {
        if (i > 8)
            return ChatFormatting.DARK_RED;
        if (i > 6)
            return ChatFormatting.RED;
        if (i > 4)
            return ChatFormatting.GOLD;
        if (i > 2)
            return ChatFormatting.YELLOW;
        if (i > 0)
            return ChatFormatting.GREEN;
        return ChatFormatting.GRAY;
    }

    public ChatFormatting getHealthColor(EntityPlayer entityPlayer) {
        float health = entityPlayer.getHealth() + entityPlayer.getAbsorptionAmount();
        if (health > 20)
            return ChatFormatting.GREEN;
        if (health > 16)
            return ChatFormatting.GOLD;
        if (health > 10)
            return ChatFormatting.YELLOW;
        if (health > 5)
            return ChatFormatting.RED;
        if (health > 0)
            return ChatFormatting.DARK_RED;
        return null;
    }

    public ChatFormatting getPingColor(EntityPlayer entityPlayer) {
        try {
            float ping = Objects.requireNonNull(mc.getConnection()).getPlayerInfo(entityPlayer.getGameProfile().getId()).getResponseTime();
            if (ping > 200)
                return ChatFormatting.DARK_RED;
            if (ping > 150)
                return ChatFormatting.RED;
            if (ping > 100)
                return ChatFormatting.YELLOW;
            if (ping > 50)
                return ChatFormatting.GOLD;
            if (ping > 0)
                return ChatFormatting.GREEN;
        } catch (Exception ignored) {
        }
        return null;
    }

    public void renderFullNametag(EntityPlayer entityPlayer, double x, double y, double z, float delta) {
        double tempY = y;
        tempY += entityPlayer.isSneaking() ? 0.5 : 0.7;
        Entity camera = mc.getRenderViewEntity();
        assert (camera != null);
        double originalPositionX = camera.posX;
        double originalPositionY = camera.posY;
        double originalPositionZ = camera.posZ;
        camera.posX = interpolate(camera.prevPosX, camera.posX, delta);
        camera.posY = interpolate(camera.prevPosY, camera.posY, delta);
        camera.posZ = interpolate(camera.prevPosZ, camera.posZ, delta);
        double distance = camera.getDistance(x + mc.getRenderManager().viewerPosX, y + mc.getRenderManager().viewerPosY, z + mc.getRenderManager().viewerPosZ);
        double scale = distance <= 8.0 ? 0.0245 : (0.0018 + (double) 10 * (distance * 0.3)) / 1000.0;
        GlStateManager.pushMatrix();
        RenderHelper.enableStandardItemLighting();
        GlStateManager.enablePolygonOffset();
        GlStateManager.doPolygonOffset(1.0f, -1500000.0f);
        GlStateManager.disableLighting();
        GlStateManager.translate((float) x, (float) tempY + 1.4f, (float) z);
        GlStateManager.rotate(-mc.getRenderManager().playerViewY, 0.0f, 1.0f, 0.0f);
        GlStateManager.rotate(mc.getRenderManager().playerViewX, mc.gameSettings.thirdPersonView == 2 ? -1.0f : 1.0f, 0.0f, 0.0f);
        GlStateManager.scale(-scale, -scale, scale);
        GlStateManager.disableDepth();
        GlStateManager.enableBlend();
        GlStateManager.enableBlend();
        GlStateManager.disableBlend();
        ItemStack renderMainHand = entityPlayer.getHeldItemMainhand().copy();
        if (renderMainHand.hasEffect() && (renderMainHand.getItem() instanceof ItemTool || renderMainHand.getItem() instanceof ItemArmor))
            renderMainHand.stackSize = 1;
        GL11.glPushMatrix();
        GL11.glScalef(0.75f, 0.75f, 0.0f);
        GL11.glScalef(1.5f, 1.5f, 1.0f);
        GL11.glPopMatrix();
        GlStateManager.pushMatrix();
        int xOffset = -8;
        for (ItemStack stack : entityPlayer.inventory.armorInventory) {
            if (stack != null)
                xOffset -= 8;
        }
        xOffset -= 8;
        ItemStack renderOffhand = entityPlayer.getHeldItemOffhand().copy();
        if (renderOffhand.hasEffect() && (renderOffhand.getItem() instanceof ItemTool || renderOffhand.getItem() instanceof ItemArmor)) {
            renderOffhand.stackSize = 1;
        }
        renderItemStack(renderOffhand, xOffset);
        xOffset += 16;
        for (ItemStack stack : entityPlayer.inventory.armorInventory) {
            if (stack == null)
                continue;
            ItemStack armourStack = stack.copy();
            if (armourStack.hasEffect() && (armourStack.getItem() instanceof ItemTool || armourStack.getItem() instanceof ItemArmor))
                armourStack.stackSize = 1;
            renderItemStack(armourStack, xOffset);
            xOffset += 16;
        }
        renderItemStack(renderMainHand, xOffset);
        GlStateManager.popMatrix();
        int pops = Ruby.totemPopManager.getPopsByPlayer(entityPlayer.getName());
        Ruby.mc.fontRenderer.drawStringWithShadow((Ruby.friendManager.isFriend(entityPlayer.getName()) ? ChatFormatting.AQUA : Ruby.enemyManager.isEnemy(entityPlayer.getName()) ? ChatFormatting.RED : "") + entityPlayer.getName() + (health.GetSwitch() ? " " + getHealthColor(entityPlayer) + roundNumber(entityPlayer.getHealth() + entityPlayer.getAbsorptionAmount(), 1) : "") + (ping.GetSwitch() ? getPingColor(entityPlayer) + " " + Objects.requireNonNull(mc.getConnection()).getPlayerInfo(entityPlayer.getGameProfile().getId()).getResponseTime() + "ms" : "") + (totemPops.GetSwitch() ? " " + getPopsColor(pops) + pops : ""), -Ruby.fontManager.getStringWidth(entityPlayer.getName() + (health.GetSwitch() ? " " + getHealthColor(entityPlayer) + roundNumber(entityPlayer.getHealth() + entityPlayer.getAbsorptionAmount(), 1) : "") + (ping.GetSwitch() ? getPingColor(entityPlayer) + " " + roundNumber(Objects.requireNonNull(mc.getConnection()).getPlayerInfo(entityPlayer.getGameProfile().getId()).getResponseTime(), 0) + "ms" : "") + (totemPops.GetSwitch() ? " " + getPopsColor(pops) + pops : "")) / 2, -8, -1);
        camera.posX = originalPositionX;
        camera.posY = originalPositionY;
        camera.posZ = originalPositionZ;
        GlStateManager.enableDepth();
        GlStateManager.disableBlend();
        GlStateManager.disablePolygonOffset();
        GlStateManager.doPolygonOffset(1.0f, 1500000.0f);
        GlStateManager.popMatrix();
    }

    private void renderItemStack(ItemStack stack, int x) {
        GlStateManager.pushMatrix();
        GlStateManager.depthMask(true);
        GlStateManager.clear(256);
        RenderHelper.enableStandardItemLighting();
        mc.getRenderItem().zLevel = -150.0f;
        GlStateManager.disableAlpha();
        GlStateManager.enableDepth();
        GlStateManager.disableCull();
        mc.getRenderItem().renderItemAndEffectIntoGUI(stack, x, -26);
        mc.getRenderItem().renderItemOverlays(mc.fontRenderer, stack, x, -26);
        mc.getRenderItem().zLevel = 0.0f;
        RenderHelper.disableStandardItemLighting();
        GlStateManager.enableCull();
        GlStateManager.enableAlpha();
        GlStateManager.scale(0.5f, 0.5f, 0.5f);
        GlStateManager.disableDepth();
        renderEnchantmentText(stack, x);
        GlStateManager.enableDepth();
        GlStateManager.scale(2.0f, 2.0f, 2.0f);
        GlStateManager.popMatrix();
    }

    private void renderEnchantmentText(ItemStack stack, int x) {
        if (EntityUtil.hasDurability(stack)) {
            int percent = EntityUtil.getRoundedDamage(stack);
            String color = percent >= 60 ? "\u00a7a" : (percent >= 25 ? "\u00a7e" : "\u00a7c");
            mc.fontRenderer.drawStringWithShadow(color + percent + "%", x * 2, -26, -1);
        }
    }

    private double interpolate(double previous, double current, float delta) {
        return previous + (current - previous) * (double) delta;
    }
}
