package dev.zprestige.ruby.module.client;

import dev.zprestige.ruby.Ruby;
import dev.zprestige.ruby.eventbus.annotation.RegisterListener;
import dev.zprestige.ruby.events.PacketEvent;
import dev.zprestige.ruby.module.Module;
import dev.zprestige.ruby.setting.impl.BooleanSetting;
import dev.zprestige.ruby.setting.impl.ColorSetting;
import dev.zprestige.ruby.setting.impl.IntegerSetting;
import dev.zprestige.ruby.util.GraphUtil;
import dev.zprestige.ruby.util.RenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;

import java.awt.*;
import java.io.File;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.*;
import java.util.function.Predicate;

public class Hud extends Module {
    public static Hud Instance;
    public ColorSetting color = createSetting("Color", new Color(-1));
    public BooleanSetting watermark = createSetting("Watermark", false);
    public BooleanSetting ping = createSetting("Ping", false);
    public BooleanSetting fps = createSetting("Fps", false);
    public BooleanSetting tps = createSetting("Tps", false);
    public BooleanSetting coords = createSetting("Coords", false);
    public BooleanSetting welcomer = createSetting("Welcomer", false);
    public BooleanSetting armor = createSetting("Armor", false);
    public BooleanSetting noRegularArmorHud = createSetting("No Regular Armor Hud", false);
    public BooleanSetting totems = createSetting("Totems", false);
    public BooleanSetting packetGraph = createSetting("Packet Graph", false);
    public IntegerSetting graphX = createSetting("Graph X", 0, 0, 1000, (Predicate<Integer>) v -> packetGraph.getValue());
    public IntegerSetting graphY = createSetting("Graph Y", 200, 0, 1000, (Predicate<Integer>) v -> packetGraph.getValue());
    public HashMap<Integer, Integer> placedCrystals = new HashMap<>();
    public GraphUtil receivedPackets = new GraphUtil();
    public List<Double> receivedPacketsList = new ArrayList<>();
    public GraphUtil sentPackets = new GraphUtil();
    public List<Double> sentPacketsList = new ArrayList<>();

    public Hud() {
        Instance = this;
    }

    @RegisterListener
    public void onPacketReceive(PacketEvent.PacketReceiveEvent packetEvent) {
        if (nullCheck() || !isEnabled() || !packetGraph.getValue())
            return;
        receivedPackets.addItem();
    }

    @RegisterListener
    public void onPacketSend(PacketEvent.PacketSendEvent packetEvent) {
        if (nullCheck() || !isEnabled() || !packetGraph.getValue())
            return;
        sentPackets.addItem();
    }

    public void makeGraph(int x, int y, List<Double> list, double counter, boolean sent) {
        y += 35;
        double n7 = 17.5 / list.stream().max(Double::compareTo).orElse(1.0);
        list.add(counter + 1.0);
        while (list.size() > 200)
            list.remove(0);
        int n8 = 0;
        if (sent)
            GL11.glColor4f(0.0f, 1.0f, 0.0f, 1.0f);
        else
            GL11.glColor4f(1.0f, 0.0f, 0.0f, 1.0f);
        GL11.glLineWidth(2.0f);
        GL11.glDisable(3553);
        GL11.glBegin(3);
        n8 += 3;
        double n9 = (100 - n8) / (double) list.size();
        for (int i = 0; i < list.size(); ++i)
            GL11.glVertex2d(n8 + i * n9 + x, y + n7 - n7 * list.get(i));
        GL11.glEnd();
        GL11.glEnable(3553);
    }

    @Override
    public void onTick() {
        for (Map.Entry<Integer, Integer> entry : placedCrystals.entrySet()) {
            placedCrystals.put(entry.getKey(), entry.getValue() - 1);
            if (entry.getValue() <= 0) {
                placedCrystals.remove(entry.getKey());
                return;
            }
        }
    }

    @SubscribeEvent
    public void onRenderHud(RenderGameOverlayEvent event) {
        if (nullCheck() || !isEnabled())
            return;
        if (event.getType().equals(RenderGameOverlayEvent.ElementType.POTION_ICONS) || (noRegularArmorHud.getValue() && event.getType().equals(RenderGameOverlayEvent.ElementType.ARMOR)))
            event.setCanceled(true);
    }

    @Override
    public void onOverlayTick() {
        if (packetGraph.getValue()) {
            RenderUtil.drawRect(graphX.getValue(), graphY.getValue(), graphX.getValue() + 100, graphY.getValue() + 35, new Color(0, 0, 0, 100).getRGB());
            RenderUtil.drawOutlineRect(graphX.getValue(), graphY.getValue(), graphX.getValue() + 100, graphY.getValue() + 35, color.getValue(), 1.0f);
            Ruby.rubyFont.drawString("Packets", graphX.getValue() + 2, graphY.getValue(), -1);
            makeGraph(graphX.getValue(), graphY.getValue(), receivedPacketsList, receivedPackets.getCount(), false);
            makeGraph(graphX.getValue(), graphY.getValue(), sentPacketsList, sentPackets.getCount(), true);
        }
        int i = -10;
        if (watermark.getValue()) {
            i += 10;
            File path = new File(Ruby.mc.gameDir + File.separator + "mods");
            String string = "";
            for (String file : Objects.requireNonNull(path.list())) {
                if (!file.contains("ruby"))
                    continue;
                string = file.replace("-main.jar", "");
            }
            RenderUtil.drawRect(1, i, 1 + Ruby.rubyFont.getStringWidth(string) + 2, i + 10, new Color(0, 0, 0, 100).getRGB());
            Ruby.rubyFont.drawStringWithShadow("Ruby", 1, i, new Color(0x5D5D5D).getRGB());
            Ruby.rubyFont.drawStringWithShadow(string.replace("ruby", ""), 1 + Ruby.rubyFont.getStringWidth("Ruby"), i, color.getValue().getRGB());
            RenderUtil.drawRect(0, i, 1, i + 10, color.getValue().getRGB());
        }
        if (ping.getValue()) {
            i += 10;
            try {
                RenderUtil.drawRect(1, i, 1 + Ruby.rubyFont.getStringWidth("Ping " + Objects.requireNonNull(mc.getConnection()).getPlayerInfo(mc.getConnection().getGameProfile().getId()).getResponseTime()) + 2, i + 10, new Color(0, 0, 0, 100).getRGB());
                Ruby.rubyFont.drawStringWithShadow("Ping", 1, i, new Color(0x5D5D5D).getRGB());
                Ruby.rubyFont.drawStringWithShadow(" " + mc.getConnection().getPlayerInfo(mc.getConnection().getGameProfile().getId()).getResponseTime(), 1 + Ruby.rubyFont.getStringWidth("Ping"), i, color.getValue().getRGB());
                RenderUtil.drawRect(0, i, 1, i + 10, color.getValue().getRGB());
            } catch (Exception ignored) {
            }
        }
        if (tps.getValue()) {
            i += 10;
            try {
                RenderUtil.drawRect(1, i, 1 + Ruby.rubyFont.getStringWidth("Tps " + Ruby.tickManager.getTPS()) + 2, i + 10, new Color(0, 0, 0, 100).getRGB());
                Ruby.rubyFont.drawStringWithShadow("Tps", 1, i, new Color(0x5D5D5D).getRGB());
                Ruby.rubyFont.drawStringWithShadow(" " + Ruby.tickManager.getTPS(), 1 + Ruby.rubyFont.getStringWidth("Tps"), i, color.getValue().getRGB());
                RenderUtil.drawRect(0, i, 1, i + 10, color.getValue().getRGB());
            } catch (Exception ignored) {
            }
        }
        if (fps.getValue()) {
            i += 10;
            RenderUtil.drawRect(1, i, 1 + Ruby.rubyFont.getStringWidth("Fps " + Minecraft.getDebugFPS()) + 2, i + 10, new Color(0, 0, 0, 100).getRGB());
            Ruby.rubyFont.drawStringWithShadow("Fps", 1, i, new Color(0x5D5D5D).getRGB());
            Ruby.rubyFont.drawStringWithShadow(" " + Minecraft.getDebugFPS(), 1 + Ruby.rubyFont.getStringWidth("Fps"), i, color.getValue().getRGB());
            RenderUtil.drawRect(0, i, 1, i + 10, color.getValue().getRGB());
        }
        if (coords.getValue()) {
            boolean inNether = mc.world.getBiome(mc.player.getPosition()).getBiomeName().equals("Hell");
            int screenHeight = new ScaledResolution(mc).getScaledHeight() - (mc.currentScreen instanceof GuiChat ? 14 : 0);
            RenderUtil.drawRect(1, screenHeight - 10, 1 + Ruby.rubyFont.getStringWidth("XYZ " + roundNumber(mc.player.posX, 1) + ", " + roundNumber(mc.player.posY, 1) + ", " + roundNumber(mc.player.posZ, 1) + " [" + roundNumber(inNether ? mc.player.posX * 8 : mc.player.posX / 8, 1) + ", " + roundNumber(inNether ? mc.player.posZ * 8 : mc.player.posZ / 8, 1) + "]"), screenHeight, new Color(0, 0, 0, 100).getRGB());
            Ruby.rubyFont.drawStringWithShadow("XYZ", 1, screenHeight - 10, new Color(0x5D5D5D).getRGB());
            Ruby.rubyFont.drawStringWithShadow(String.valueOf(roundNumber(mc.player.posX, 1)), 1 + Ruby.rubyFont.getStringWidth("XYZ "), screenHeight - 10, color.getValue().getRGB());
            Ruby.rubyFont.drawStringWithShadow(",", 1 + Ruby.rubyFont.getStringWidth("XYZ " + roundNumber(mc.player.posX, 1)), screenHeight - 10, new Color(0x5D5D5D).getRGB());
            Ruby.rubyFont.drawStringWithShadow(String.valueOf(roundNumber(mc.player.posY, 1)), 1 + Ruby.rubyFont.getStringWidth("XYZ " + roundNumber(mc.player.posX, 1) + ", "), screenHeight - 10, color.getValue().getRGB());
            Ruby.rubyFont.drawStringWithShadow(",", 1 + Ruby.rubyFont.getStringWidth("XYZ " + roundNumber(mc.player.posX, 1) + ", " + roundNumber(mc.player.posY, 1)), screenHeight - 10, new Color(0x5D5D5D).getRGB());
            Ruby.rubyFont.drawStringWithShadow(String.valueOf(roundNumber(mc.player.posZ, 1)), 1 + Ruby.rubyFont.getStringWidth("XYZ " + roundNumber(mc.player.posX, 1) + ", " + roundNumber(mc.player.posY, 1) + ", "), screenHeight - 10, color.getValue().getRGB());
            Ruby.rubyFont.drawStringWithShadow("[", 1 + Ruby.rubyFont.getStringWidth("XYZ " + roundNumber(mc.player.posX, 1) + ", " + roundNumber(mc.player.posY, 1) + ", " + roundNumber(mc.player.posZ, 1) + " "), screenHeight - 10, new Color(0x5D5D5D).getRGB());
            Ruby.rubyFont.drawStringWithShadow(String.valueOf(roundNumber(inNether ? mc.player.posX * 8 : mc.player.posX / 8, 1)), 1 + Ruby.rubyFont.getStringWidth("XYZ " + roundNumber(mc.player.posX, 1) + ", " + roundNumber(mc.player.posY, 1) + ", " + roundNumber(mc.player.posZ, 1) + " ["), screenHeight - 10, color.getValue().getRGB());
            Ruby.rubyFont.drawStringWithShadow(",", 1 + Ruby.rubyFont.getStringWidth("XYZ " + roundNumber(mc.player.posX, 1) + ", " + roundNumber(mc.player.posY, 1) + ", " + roundNumber(mc.player.posZ, 1) + " [" + roundNumber(inNether ? mc.player.posX * 8 : mc.player.posX / 8, 1)), screenHeight - 10, new Color(0x5D5D5D).getRGB());
            Ruby.rubyFont.drawStringWithShadow(String.valueOf(roundNumber(inNether ? mc.player.posZ * 8 : mc.player.posZ / 8, 1)), 1 + Ruby.rubyFont.getStringWidth("XYZ " + roundNumber(mc.player.posX, 1) + ", " + roundNumber(mc.player.posY, 1) + ", " + roundNumber(mc.player.posZ, 1) + " [" + roundNumber(inNether ? mc.player.posX * 8 : mc.player.posX / 8, 1) + ", "), screenHeight - 10, color.getValue().getRGB());
            Ruby.rubyFont.drawStringWithShadow("]", 1 + Ruby.rubyFont.getStringWidth("XYZ " + roundNumber(mc.player.posX, 1) + ", " + roundNumber(mc.player.posY, 1) + ", " + roundNumber(mc.player.posZ, 1) + " [" + roundNumber(inNether ? mc.player.posX * 8 : mc.player.posX / 8, 1) + ", " + roundNumber(inNether ? mc.player.posZ * 8 : mc.player.posZ / 8, 1)), screenHeight - 10, new Color(0x5D5D5D).getRGB());
            RenderUtil.drawRect(1, screenHeight - 20, 1 + Ruby.rubyFont.getStringWidth("Facing " + (mc.player.getHorizontalFacing().equals(EnumFacing.NORTH) ? "North" : mc.player.getHorizontalFacing().equals(EnumFacing.EAST) ? "East" : mc.player.getHorizontalFacing().equals(EnumFacing.SOUTH) ? "South" : "West") + "  [" + (mc.player.getHorizontalFacing().equals(EnumFacing.NORTH) ? "-Z" : mc.player.getHorizontalFacing().equals(EnumFacing.EAST) ? "+X" : mc.player.getHorizontalFacing().equals(EnumFacing.SOUTH) ? "+Z" : "-X") + "]"), screenHeight - 10, new Color(0, 0, 0, 100).getRGB());
            Ruby.rubyFont.drawStringWithShadow("Facing", 1, screenHeight - 20, new Color(0x5D5D5D).getRGB());
            Ruby.rubyFont.drawStringWithShadow(mc.player.getHorizontalFacing().equals(EnumFacing.NORTH) ? "North" : mc.player.getHorizontalFacing().equals(EnumFacing.EAST) ? "East" : mc.player.getHorizontalFacing().equals(EnumFacing.SOUTH) ? "South" : "West", 1 + Ruby.rubyFont.getStringWidth("Facing "), screenHeight - 20, color.getValue().getRGB());
            Ruby.rubyFont.drawStringWithShadow("[", 1 + Ruby.rubyFont.getStringWidth("Facing " + (mc.player.getHorizontalFacing().equals(EnumFacing.NORTH) ? "North" : mc.player.getHorizontalFacing().equals(EnumFacing.EAST) ? "East" : mc.player.getHorizontalFacing().equals(EnumFacing.SOUTH) ? "South" : "West") + " "), screenHeight - 20, new Color(0x5D5D5D).getRGB());
            Ruby.rubyFont.drawStringWithShadow(mc.player.getHorizontalFacing().equals(EnumFacing.NORTH) ? "-Z" : mc.player.getHorizontalFacing().equals(EnumFacing.EAST) ? "+X" : mc.player.getHorizontalFacing().equals(EnumFacing.SOUTH) ? "+Z" : "-X", 1 + Ruby.rubyFont.getStringWidth("Facing " + (mc.player.getHorizontalFacing().equals(EnumFacing.NORTH) ? "North" : mc.player.getHorizontalFacing().equals(EnumFacing.EAST) ? "East" : mc.player.getHorizontalFacing().equals(EnumFacing.SOUTH) ? "South" : "West") + "  ["), screenHeight - 20, color.getValue().getRGB());
            Ruby.rubyFont.drawStringWithShadow("]", 1 + Ruby.rubyFont.getStringWidth("Facing " + (mc.player.getHorizontalFacing().equals(EnumFacing.NORTH) ? "North" : mc.player.getHorizontalFacing().equals(EnumFacing.EAST) ? "East" : mc.player.getHorizontalFacing().equals(EnumFacing.SOUTH) ? "South" : "West") + "  [" + (mc.player.getHorizontalFacing().equals(EnumFacing.NORTH) ? "-Z" : mc.player.getHorizontalFacing().equals(EnumFacing.EAST) ? "+X" : mc.player.getHorizontalFacing().equals(EnumFacing.SOUTH) ? "+Z" : "-X")), screenHeight - 20, new Color(0x5D5D5D).getRGB());
            RenderUtil.drawRect(0, screenHeight - 20, 1, screenHeight, color.getValue().getRGB());
        }
        if (welcomer.getValue()) {
            int screenWidth = new ScaledResolution(mc).getScaledWidth();
            Ruby.rubyFont.drawStringWithShadow("Welcome, ", (screenWidth / 2f) - (Ruby.rubyFont.getStringWidth("Welcome, " + mc.player.getName()) / 2f), 0, new Color(0x5D5D5D).getRGB());
            Ruby.rubyFont.drawStringWithShadow(mc.player.getName(), (screenWidth / 2f) - (Ruby.rubyFont.getStringWidth("Welcome, " + mc.player.getName()) / 2f) + Ruby.rubyFont.getStringWidth("Welcome, "), 0, color.getValue().getRGB());
        }
        if (armor.getValue())
            renderArmorHUD();
        if (totems.getValue())
            renderTotemHUD();
    }

    public static float roundNumber(double value, int places) {
        if (places < 0)
            throw new IllegalArgumentException();
        BigDecimal decimal = BigDecimal.valueOf(value);
        decimal = decimal.setScale(places, RoundingMode.FLOOR);
        return decimal.floatValue();
    }

    public void renderArmorHUD() {
        int width = new ScaledResolution(mc).getScaledWidth();
        int height = new ScaledResolution(mc).getScaledHeight();
        GlStateManager.enableTexture2D();
        int i = width / 2;
        int iteration = 0;
        int y = height - 55 - ((mc.player.isInWater() && mc.playerController.gameIsSurvivalOrAdventure()) ? 10 : 0);
        for (ItemStack is : mc.player.inventory.armorInventory) {
            ++iteration;
            if (is.isEmpty())
                continue;
            int x = i - 90 + (9 - iteration) * 20 + 2;
            GlStateManager.enableDepth();
            RenderUtil.itemRender.zLevel = 200.0f;
            RenderUtil.itemRender.renderItemAndEffectIntoGUI(is, x, y);
            RenderUtil.itemRender.renderItemOverlayIntoGUI(mc.fontRenderer, is, x, y, "");
            RenderUtil.itemRender.zLevel = 0.0f;
            GlStateManager.enableTexture2D();
            GlStateManager.disableLighting();
            GlStateManager.disableDepth();
            String s = (is.getCount() > 1) ? (is.getCount() + "") : "";
            mc.fontRenderer.drawStringWithShadow(s, x + 19 - 2 - mc.fontRenderer.getStringWidth(s), (float) (y + 9), 16777215);
            int dmg;
            float green = (is.getMaxDamage() - (float) is.getItemDamage()) / is.getMaxDamage();
            float red = 1.0f - green;
            dmg = 100 - (int) (red * 100.0f);
            mc.fontRenderer.drawStringWithShadow(dmg + "", x + 8 - mc.fontRenderer.getStringWidth(dmg + "") / 2f, (float) (y - 11), new Color((int) (red * 255.0f), (int) (green * 255.0f), 0).getRGB());
        }
        GlStateManager.enableDepth();
        GlStateManager.disableLighting();
    }

    public void renderTotemHUD() {
        int width = new ScaledResolution(mc).getScaledWidth();
        int height = new ScaledResolution(mc).getScaledHeight();
        int totems = mc.player.inventory.mainInventory.stream().filter(itemStack -> (itemStack.getItem() == Items.TOTEM_OF_UNDYING)).mapToInt(ItemStack::getCount).sum();
        if (mc.player.getHeldItemOffhand().getItem() == Items.TOTEM_OF_UNDYING)
            totems += mc.player.getHeldItemOffhand().getCount();
        if (totems > 0) {
            GlStateManager.enableTexture2D();
            int i = width / 2;
            int y = height - 55 - ((mc.player.isInWater() && mc.playerController.gameIsSurvivalOrAdventure()) ? 10 : 0);
            int x = i - 189 + 180 + 2;
            GlStateManager.enableDepth();
            RenderUtil.itemRender.zLevel = 200.0F;
            RenderUtil.itemRender.renderItemAndEffectIntoGUI(new ItemStack(Items.TOTEM_OF_UNDYING), x, y);
            RenderUtil.itemRender.renderItemOverlayIntoGUI(mc.fontRenderer, new ItemStack(Items.TOTEM_OF_UNDYING), x, y, "");
            RenderUtil.itemRender.zLevel = 0.0F;
            GlStateManager.enableTexture2D();
            GlStateManager.disableLighting();
            GlStateManager.disableDepth();
            mc.fontRenderer.drawStringWithShadow(totems + "", (x + 19 - 2 - mc.fontRenderer.getStringWidth(totems + "")), (y + 9), 16777215);
            GlStateManager.enableDepth();
            GlStateManager.disableLighting();
        }
    }
}
