package dev.zprestige.ruby.module.movement;

import com.mojang.realmsclient.gui.ChatFormatting;
import dev.zprestige.ruby.Ruby;
import dev.zprestige.ruby.module.Category;
import dev.zprestige.ruby.module.Module;
import dev.zprestige.ruby.module.ModuleInfo;
import dev.zprestige.ruby.module.client.Hud;
import dev.zprestige.ruby.setting.impl.IntegerSetting;
import dev.zprestige.ruby.util.MessageUtil;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.tileentity.TileEntityChest;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Calendar;

@ModuleInfo(name = "StashHunter", category = Category.Movement, description = "Finds stash for u wu")
public class StashHunter extends Module {
    public IntegerSetting leftRightSeconds = createSetting("Left Right (S)", 10, 1, 60);
    public IntegerSetting forwardsSeconds = createSetting("Forwards (S)", 3, 1, 10);
    public IntegerSetting minimumChests = createSetting("Minimum Chests", 10, 1, 100);
    public int ticks = 0;
    public Stage stage = Stage.LeftRight;
    public LeftRightStage leftRightStage = LeftRightStage.Right;
    File path;
    File currentFile;
    BufferedWriter bufferedWriter;
    Calendar calendar = Calendar.getInstance();

    public StashHunter() {
        path = new File(Ruby.mc.gameDir + File.separator + "Ruby" + File.separator + "StashHunter");
        if (!path.exists())
            path.mkdir();
    }

    @Override
    public void onEnable() {
        currentFile = new File(Ruby.mc.gameDir + File.separator + "Ruby" + File.separator + "StashHunter" + File.separator + "latest.txt");
        try {
            currentFile.createNewFile();
            bufferedWriter = new BufferedWriter(new FileWriter(currentFile));
        } catch (IOException e) {
            e.printStackTrace();
        }
        stage = Stage.LeftRight;
        leftRightStage = LeftRightStage.Right;
    }

    @Override
    public void onDisable() {
        try {
            bufferedWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onTick() {
        ticks++;
        long chests = mc.world.loadedTileEntityList.stream().filter(e -> e instanceof TileEntityChest).count();
        if (chests >= minimumChests.getValue()) {
            try {
                FileWriter writer = new FileWriter(currentFile, true);
                writer.write("[" + calendar.getTime() + "] " + chests + " Chests found when flying at X: " + roundNumber(mc.player.posX, 1) + " | Z: " + roundNumber(mc.player.posZ, 1) + "\n");
                writer.close();
                MessageUtil.sendMessage("[StashHunter] " + ChatFormatting.WHITE + "[" + calendar.getTime() + "] " + chests + " Chests found when flying at X: " + roundNumber(mc.player.posX, 1) + " | Z: " + roundNumber(mc.player.posZ, 1));
            } catch (Exception ignored) {
            }
        }
        switch (stage) {
            case LeftRight:
                if (ticks / 20 >= leftRightSeconds.getValue()) {
                    stage = Stage.Forwards;
                    switch (leftRightStage) {
                        case Right:
                            leftRightStage = LeftRightStage.Left;
                            break;
                        case Left:
                            leftRightStage = LeftRightStage.Right;
                            break;
                    }
                    ticks = 0;
                }
                switch (leftRightStage) {
                    case Right:
                        mc.gameSettings.keyBindForward.pressed = false;
                        mc.gameSettings.keyBindLeft.pressed = false;
                        mc.gameSettings.keyBindRight.pressed = true;
                        break;
                    case Left:
                        mc.gameSettings.keyBindForward.pressed = false;
                        mc.gameSettings.keyBindRight.pressed = false;
                        mc.gameSettings.keyBindLeft.pressed = true;
                        break;
                }
                break;
            case Forwards:
                if (ticks / 20 >= forwardsSeconds.getValue()) {
                    stage = Stage.LeftRight;
                    ticks = 0;
                }
                mc.gameSettings.keyBindForward.pressed = true;
                break;
        }
    }

    @Override
    public void onOverlayTick() {
        String string = "Stage: " + stage.toString() + (stage.equals(Stage.LeftRight) ? " " + leftRightStage.toString() : "");
        Ruby.rubyFont.drawStringWithShadow(string, new ScaledResolution(mc).getScaledWidth() / 2f - (Ruby.rubyFont.getStringWidth(string) / 2f), Hud.Instance.welcomer.getValue() ? 10 : 0, -1);
    }

    public enum LeftRightStage {
        Left,
        Right
    }

    public enum Stage {
        LeftRight,
        Forwards
    }

    public float roundNumber(double value, int places) {
        if (places < 0)
            throw new IllegalArgumentException();
        BigDecimal decimal = BigDecimal.valueOf(value);
        decimal = decimal.setScale(places, RoundingMode.FLOOR);
        return decimal.floatValue();
    }
}
