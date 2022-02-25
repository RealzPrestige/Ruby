package dev.zprestige.ruby.module.movement;

import dev.zprestige.ruby.eventbus.annotation.RegisterListener;
import dev.zprestige.ruby.events.MoveEvent;
import dev.zprestige.ruby.module.Module;
import dev.zprestige.ruby.settings.impl.ComboBox;
import dev.zprestige.ruby.settings.impl.Slider;
import dev.zprestige.ruby.settings.impl.Switch;
import dev.zprestige.ruby.util.BlockUtil;
import dev.zprestige.ruby.util.EntityUtil;
import net.minecraft.init.Blocks;

import java.util.HashMap;

public class LiquidSpeed extends Module {
    public final ComboBox mode = Menu.ComboBox("Mode", new String[]{"Vanilla", "Factor", "Teleport"});
    public final Slider horizontalSpeed = Menu.Slider("Horizontal Factor", 0.1f, 10.0f);
    public final Slider downFactor = Menu.Slider("Down Factor", 0.1f, 10.0f);
    public final Slider upFactor = Menu.Slider("Up Factor", 0.1f, 10.0f);
    public final ComboBox downMode = Menu.ComboBox("Down Mode", new String[]{"None", "NoDownForce", "NCP-Bypass"});
    public final Switch boost = Menu.Switch("Boost");
    public final Switch onlyBoostOnHoldSprint = Menu.Switch("Only Boost On Hold Sprint");
    public final Slider boostReduction = Menu.Slider("Boost Reduction", 0.1f, 10.0f);
    public HashMap<Long, Double> damagePerSecond = new HashMap<>();
    public double lastTickHealth = 0.0;

    @Override
    public void onTick() {
        if (!mc.player.isInLava() && !mc.player.isInWater() || mc.world.getBlockState(BlockUtil.getPlayerPos()).getBlock().equals(Blocks.AIR) || mc.world.getBlockState(BlockUtil.getPlayerPos().up()).getBlock().equals(Blocks.AIR))
            return;
        double boostAmount;
        long currentTimeMillis = System.currentTimeMillis();
        new HashMap<>(damagePerSecond).entrySet().stream().filter(entry -> entry.getKey() < currentTimeMillis).forEach(entry -> damagePerSecond.remove(entry.getKey()));
        boostAmount = damagePerSecond.values().stream().mapToDouble(aDouble -> aDouble).sum();
        double[] motion = EntityUtil.getSpeed(horizontalSpeed.GetSlider() / 10f);
        switch (mode.GetCombo()) {
            case "Vanilla":
                double value = horizontalSpeed.GetSlider() + (boost.GetSwitch() && (!onlyBoostOnHoldSprint.GetSwitch() || mc.gameSettings.keyBindSprint.isKeyDown()) ? boostAmount / (boostReduction.GetSlider() * 10) : 0);
                mc.player.motionX *= value;
                mc.player.motionZ *= value;
                break;
            case "Factor":
                mc.player.motionX = motion[0];
                mc.player.motionZ = motion[1];
                break;
            case "Teleport":
                mc.player.setPosition(mc.player.posX + motion[0], mc.player.posY, mc.player.posZ + motion[1]);
                break;
        }
        if (mode.GetCombo().equals("Teleport")) {
            if (mc.gameSettings.keyBindJump.isKeyDown())
                mc.player.setPosition(mc.player.posX, mc.player.posY + (upFactor.GetSlider() / 10f), mc.player.posZ);
            if (mc.gameSettings.keyBindSneak.isKeyDown())
                mc.player.setPosition(mc.player.posX, mc.player.posY - (downFactor.GetSlider() / 10f), mc.player.posZ);
        } else {
            if (mc.gameSettings.keyBindJump.isKeyDown())
                mc.player.motionY += upFactor.GetSlider() / 100f;
            if (mc.gameSettings.keyBindSneak.isKeyDown())
                mc.player.motionY -= downFactor.GetSlider() / 100f;
        }
        if (!mc.gameSettings.keyBindJump.isKeyDown() && !mc.gameSettings.keyBindSneak.isKeyDown()) {
            switch (downMode.GetCombo()) {
                case "NoDownForce":
                    mc.player.motionY = 0;
                    break;
                case "NCP-Bypass":
                    mc.player.motionY = -0.005;
                    break;
            }
        }
        double health = mc.player.getHealth() + mc.player.getAbsorptionAmount();
        double damage = lastTickHealth - health;
        if (health < lastTickHealth) {
            damagePerSecond.put(currentTimeMillis + 1000L, damage);
        }
        lastTickHealth = health;
    }

    @RegisterListener
    public void onMove(MoveEvent event) {
        if (nullCheck() || !isEnabled() || (!mc.player.isInLava() && !mc.player.isInWater() || mc.world.getBlockState(BlockUtil.getPlayerPos()).getBlock().equals(Blocks.AIR) || mc.world.getBlockState(BlockUtil.getPlayerPos().up()).getBlock().equals(Blocks.AIR)))
            return;
        event.setCancelled(true);
    }
}
