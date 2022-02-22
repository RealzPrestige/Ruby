package dev.zprestige.ruby.module.misc;

import com.mojang.realmsclient.gui.ChatFormatting;
import dev.zprestige.ruby.Ruby;
import dev.zprestige.ruby.eventbus.annotation.RegisterListener;
import dev.zprestige.ruby.events.EntityAddedEvent;
import dev.zprestige.ruby.events.PacketEvent;
import dev.zprestige.ruby.events.PlayerChangeEvent;
import dev.zprestige.ruby.module.Category;
import dev.zprestige.ruby.module.Module;
import dev.zprestige.ruby.module.ModuleInfo;
import dev.zprestige.ruby.setting.impl.BooleanSetting;
import dev.zprestige.ruby.util.EntityUtil;
import dev.zprestige.ruby.util.MessageUtil;
import dev.zprestige.ruby.util.RenderUtil;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.item.EntityExpBottle;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.awt.*;
import java.io.*;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

@ModuleInfo(name = "Analyzer", category = Category.Misc, description = "Analyzes people's crystal and exp usage")
public class Analyzer extends Module {
    public BooleanSetting announce = createSetting("Announce", false);
    public int exp = 0, crystals = 0, crystalStacks = 0, expStacks = 0;
    public EntityPlayer entityPlayer;
    public String playerName = "";
    public Set<BlockPos> placedPosses = new HashSet<>();
    public File path;
    public boolean hasSaved;

    public Analyzer() {
        path = new File(Ruby.mc.gameDir + File.separator + "Ruby" + File.separator + "Analyzer");
        if (!path.exists())
            path.mkdir();
    }


    public void savePlayer(String playerName) {
        int i = (int) Arrays.stream(Objects.requireNonNull(path.list())).filter(file -> file.contains(playerName)).count();
        try {
            File file = new File(Ruby.mc.gameDir + File.separator + "Ruby" + File.separator + "Analyzer" + File.separator + playerName + (i != 0 ? i + "" : "") + ".txt");
            if (!file.exists())
                file.createNewFile();
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file));
            bufferedWriter.write("Stacks Exp: " + expStacks);
            bufferedWriter.write("\r\n");
            bufferedWriter.write("Stacks Crystals: " + crystalStacks);
            bufferedWriter.close();
        } catch (Exception ignored) {
        }
    }

    @Override
    public void onEnable() {
        exp = 0;
        crystals = 0;
        entityPlayer = null;
        placedPosses.clear();
    }


    @Override
    public void onDisable() {
        if (!hasSaved) {
            if (!playerName.equals(""))
                mc.displayGuiScreen(new SaveGuiScreen(playerName));
            exp = 0;
            crystals = 0;
            entityPlayer = null;
            placedPosses.clear();
        }
    }

    @Override
    public void onTick() {
        if (entityPlayer == null) {
            entityPlayer = EntityUtil.getTarget(500.0f);
            if (entityPlayer == null)
                return;
            playerName = entityPlayer.getName();
            MessageUtil.sendMessage("[Analyzer] " + ChatFormatting.WHITE + "Started analyzing " + ChatFormatting.BOLD + playerName + ChatFormatting.WHITE + ".");
            int i = (int) Arrays.stream(Objects.requireNonNull(path.list())).filter(file -> file.contains(playerName)).count();
            if (i == 0) {
                MessageUtil.sendMessage("[Analyzer] " + ChatFormatting.WHITE + "no results found for " + ChatFormatting.BOLD + playerName + ChatFormatting.WHITE + ".");
                return;
            }
            MessageUtil.sendMessage("[Analyzer] " + ChatFormatting.WHITE + "results for " + ChatFormatting.BOLD + playerName + ChatFormatting.WHITE + ":");
            Arrays.stream(Objects.requireNonNull(path.list())).filter(file -> file.contains(playerName)).forEach(file -> {
                try {
                    BufferedReader bufferReader = new BufferedReader(new InputStreamReader(new DataInputStream(new FileInputStream(file))));
                    MessageUtil.sendMessage("[Analyzer] " + ChatFormatting.WHITE + "Results in " + ChatFormatting.BOLD + file + ChatFormatting.WHITE + ":");
                    AtomicInteger j = new AtomicInteger();
                    bufferReader.lines().forEach(line -> MessageUtil.sendMessage("[Analyzer] " + ChatFormatting.GRAY + "[" + (j.addAndGet(1)) + "] " + ChatFormatting.WHITE + line));
                    bufferReader.close();
                } catch (Exception ignored) {
                }
            });
        }
        if (entityPlayer == null)
            return;
        if (announce.getValue()) {
            if (exp != 0 && expStacks != exp / 64)
                MessageUtil.sendMessage("[Analyzer] " + ChatFormatting.WHITE + "" + ChatFormatting.BOLD + entityPlayer.getName() + ChatFormatting.WHITE + " has used " + ChatFormatting.RED + (expStacks = exp / 64) + ChatFormatting.WHITE + " stacks of exp!");
            if (crystals != 0 && crystalStacks != crystals / 64)
                MessageUtil.sendMessage("[Analyzer] " + ChatFormatting.WHITE + "" + ChatFormatting.BOLD + entityPlayer.getName() + ChatFormatting.WHITE + " has used " + ChatFormatting.RED + (crystalStacks = crystals / 64) + ChatFormatting.WHITE + " stacks of crystals!");
        }
    }

    @RegisterListener
    public void onPacketSend(PacketEvent.PacketSendEvent event) {
        if (nullCheck() || !isEnabled() || !(event.getPacket() instanceof CPacketPlayerTryUseItemOnBlock))
            return;
        if (mc.player.getHeldItem(((CPacketPlayerTryUseItemOnBlock) event.getPacket()).hand).getItem() == Items.END_CRYSTAL)
            placedPosses.add(((CPacketPlayerTryUseItemOnBlock) event.getPacket()).position);
    }

    @RegisterListener
    public void onDeath(PlayerChangeEvent.Death event) {
        if (nullCheck() || !isEnabled() || !(entityPlayer != null && entityPlayer.equals(event.entityPlayer)))
            return;
        if (!playerName.equals(""))
            mc.displayGuiScreen(new SaveGuiScreen(playerName));
        exp = 0;
        crystals = 0;
        hasSaved = true;
        disableModule(ChatFormatting.WHITE + "Finished analyzing " + ChatFormatting.BOLD + playerName + ChatFormatting.WHITE + ", disabling Analyzer.");
    }

    @RegisterListener
    public void onEntityAdded(EntityAddedEvent event) {
        if (nullCheck() || !isEnabled() || entityPlayer == null)
            return;
        if (event.entity instanceof EntityEnderCrystal) {
            if (placedPosses.contains(event.entity.getPosition().down())) {
                placedPosses.remove(event.entity.getPosition().down());
            } else {
                ++crystals;
            }
        }
        if (event.entity instanceof EntityExpBottle && Objects.equals(mc.world.getClosestPlayerToEntity(event.entity, 3.0), entityPlayer))
            ++exp;
    }

    public class SaveGuiScreen extends GuiScreen {
        String entityPlayer;
        ScaledResolution scaledResolution;

        public SaveGuiScreen(String entityPlayer) {
            this.entityPlayer = entityPlayer;
        }

        @Override
        public void drawScreen(int mouseX, int mouseY, float partialTicks) {
            scaledResolution = new ScaledResolution(mc);
            drawDefaultBackground();
            Ruby.rubyFont.drawStringWithShadow("Do you wish to save Analytics for " + entityPlayer, (scaledResolution.getScaledWidth() / 2f) - (Ruby.rubyFont.getStringWidth("Do you wish to save Analytics for " + entityPlayer) / 2f), (scaledResolution.getScaledHeight() / 2f) - 10, -1);
            RenderUtil.drawRect((scaledResolution.getScaledWidth() / 2f) - 50, scaledResolution.getScaledHeight() / 2f, (scaledResolution.getScaledWidth() / 2f) - 1, (scaledResolution.getScaledHeight() / 2f) + 13, new Color(0, 0, 0, 50).getRGB());
            RenderUtil.drawRect((scaledResolution.getScaledWidth() / 2f) + 1, scaledResolution.getScaledHeight() / 2f, (scaledResolution.getScaledWidth() / 2f) + 50, (scaledResolution.getScaledHeight() / 2f) + 13, new Color(0, 0, 0, 50).getRGB());
            Ruby.rubyFont.drawStringWithShadow("Yes", (scaledResolution.getScaledWidth() / 2f) - 25 - (Ruby.rubyFont.getStringWidth("Yes") / 2f), (scaledResolution.getScaledHeight() / 2f) + (13 / 2f) - (Ruby.rubyFont.getHeight("Yes") / 2f), -1);
            Ruby.rubyFont.drawStringWithShadow("No", (scaledResolution.getScaledWidth() / 2f) + 25 - (Ruby.rubyFont.getStringWidth("No") / 2f), (scaledResolution.getScaledHeight() / 2f) + (13 / 2f) - (Ruby.rubyFont.getHeight("No") / 2f), -1);
            if (isInsideYes(mouseX, mouseY))
                RenderUtil.drawRect((scaledResolution.getScaledWidth() / 2f) - 50, scaledResolution.getScaledHeight() / 2f, (scaledResolution.getScaledWidth() / 2f) - 1, (scaledResolution.getScaledHeight() / 2f) + 13, new Color(0, 0, 0, 50).getRGB());
            if (isInsideNo(mouseX, mouseY))
                RenderUtil.drawRect((scaledResolution.getScaledWidth() / 2f) + 1, scaledResolution.getScaledHeight() / 2f, (scaledResolution.getScaledWidth() / 2f) + 50, (scaledResolution.getScaledHeight() / 2f) + 13, new Color(0, 0, 0, 50).getRGB());
        }

        @Override
        public void mouseClicked(int mouseX, int mouseY, int mouseButton) {
            if (mouseButton == 0) {
                if (isInsideYes(mouseX, mouseY)) {
                    savePlayer(entityPlayer);
                    mc.displayGuiScreen(null);
                }
                if (isInsideNo(mouseX, mouseY))
                    mc.displayGuiScreen(null);
            }
        }

        public boolean isInsideYes(int mouseX, int mouseY) {
            return mouseX > (scaledResolution.getScaledWidth() / 2f) - 50 && mouseX < (scaledResolution.getScaledWidth() / 2f) - 1 && mouseY > scaledResolution.getScaledHeight() / 2f && mouseY < (scaledResolution.getScaledHeight() / 2f) + 13;
        }

        public boolean isInsideNo(int mouseX, int mouseY) {
            return mouseX > (scaledResolution.getScaledWidth() / 2f) + 1 && mouseX < (scaledResolution.getScaledWidth() / 2f) + 50 && mouseY > scaledResolution.getScaledHeight() / 2f && mouseY < (scaledResolution.getScaledHeight() / 2f) + 13;
        }
    }
}
