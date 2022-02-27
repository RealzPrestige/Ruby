package dev.zprestige.ruby.module.movement;

import com.mojang.realmsclient.gui.ChatFormatting;
import dev.zprestige.ruby.Ruby;
import dev.zprestige.ruby.module.Module;
import dev.zprestige.ruby.module.client.Hud;
import dev.zprestige.ruby.settings.impl.Slider;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.tileentity.TileEntityChest;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Calendar;

public class StashHunter extends Module {
    protected final Slider leftRightSeconds = Menu.Slider("Left Right (S)", 1, 60);
    protected final Slider forwardsSeconds = Menu.Slider("Forwards (S)", 1, 10);
    protected final Slider minimumChests = Menu.Slider("Minimum Chests", 1, 100);
    protected final Calendar calendar = Calendar.getInstance();
    protected int ticks = 0;
    protected Stage stage = Stage.LeftRight;
    protected LeftRightStage leftRightStage = LeftRightStage.Right;

    @Override
    public void onEnable() {
        stage = Stage.LeftRight;
        leftRightStage = LeftRightStage.Right;
    }

    @Override
    public void onTick() {
        ticks++;
        final long chests = mc.world.loadedTileEntityList.stream().filter(e -> e instanceof TileEntityChest).count();
        if (chests >= minimumChests.GetSlider()) {
            Ruby.chatManager.sendMessage("[StashHunter] " + ChatFormatting.WHITE + "[" + calendar.getTime() + "] " + chests + " Chests found when flying at X: " + roundNumber(mc.player.posX, 1) + " | Z: " + roundNumber(mc.player.posZ, 1));
        }
        switch (stage) {
            case LeftRight:
                if (ticks / 20f >= leftRightSeconds.GetSlider()) {
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
                if (ticks / 20f >= forwardsSeconds.GetSlider()) {
                    stage = Stage.LeftRight;
                    ticks = 0;
                }
                mc.gameSettings.keyBindForward.pressed = true;
                break;
        }
    }

    @Override
    public void onFrame2D() {
        final String string = "Stage: " + stage.toString() + (stage.equals(Stage.LeftRight) ? " " + leftRightStage.toString() : "");
        Ruby.rubyFont.drawStringWithShadow(string, new ScaledResolution(mc).getScaledWidth() / 2f - (Ruby.rubyFont.getStringWidth(string) / 2f), Hud.Instance.welcomer.GetSwitch() ? 10 : 0, -1);
    }

    public float roundNumber(double value, int places) {
        if (places < 0)
            throw new IllegalArgumentException();
        BigDecimal decimal = BigDecimal.valueOf(value);
        decimal = decimal.setScale(places, RoundingMode.FLOOR);
        return decimal.floatValue();
    }

    public enum LeftRightStage {
        Left,
        Right
    }

    public enum Stage {
        LeftRight,
        Forwards
    }
}
