package dev.zprestige.ruby.module.movement;

import dev.zprestige.ruby.events.MoveEvent;
import dev.zprestige.ruby.module.Category;
import dev.zprestige.ruby.module.Module;
import dev.zprestige.ruby.module.ModuleInfo;
import dev.zprestige.ruby.setting.impl.BooleanSetting;
import dev.zprestige.ruby.setting.impl.FloatSetting;
import dev.zprestige.ruby.setting.impl.ModeSetting;
import dev.zprestige.ruby.util.BlockUtil;
import dev.zprestige.ruby.util.EntityUtil;
import net.minecraft.init.Blocks;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.Arrays;
import java.util.HashMap;
import java.util.function.Predicate;

@ModuleInfo(name = "LiquidSpeed", category = Category.Movement, description = "")
public class LiquidSpeed extends Module {
    public ModeSetting mode = createSetting("Mode", "Vanilla", Arrays.asList("Vanilla", "Factor", "Teleport"));
    public FloatSetting horizontalSpeed = createSetting("Horizontal Factor", 1.0f, 0.1f, 10.0f);
    public FloatSetting downFactor = createSetting("Down Factor", 1.0f, 0.1f, 10.0f);
    public FloatSetting upFactor = createSetting("Up Factor", 1.0f, 0.1f, 10.0f);
    public ModeSetting downMode = createSetting("Down Mode", "None", Arrays.asList("None", "NoDownForce", "NCP-Bypass"));
    public BooleanSetting boost = createSetting("Boost", false, v -> mode.getValue().equals("Vanilla"));
    public BooleanSetting onlyBoostOnHoldSprint = createSetting("Only Boost On Hold Sprint", false, v -> mode.getValue().equals("Vanilla"));
    public FloatSetting boostReduction = createSetting("Boost Reduction", 1.0f, 0.1f, 10.0f, (Predicate<Float>) v -> mode.getValue().equals("Vanilla") && boost.getValue());
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
        double[] motion = EntityUtil.getSpeed(horizontalSpeed.getValue() / 10);
        switch (mode.getValue()) {
            case "Vanilla":
                double value = horizontalSpeed.getValue() + (boost.getValue() && (!onlyBoostOnHoldSprint.getValue() ||  mc.gameSettings.keyBindSprint.isKeyDown()) ? boostAmount / (boostReduction.getValue() * 10) : 0);
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
        if (mode.getValue().equals("Teleport")) {
            if (mc.gameSettings.keyBindJump.isKeyDown())
                mc.player.setPosition(mc.player.posX, mc.player.posY + (upFactor.getValue() / 10), mc.player.posZ);
            if (mc.gameSettings.keyBindSneak.isKeyDown())
                mc.player.setPosition(mc.player.posX, mc.player.posY - (downFactor.getValue() / 10), mc.player.posZ);
        } else {
            if (mc.gameSettings.keyBindJump.isKeyDown())
                mc.player.motionY += upFactor.getValue() / 100;
            if (mc.gameSettings.keyBindSneak.isKeyDown())
                mc.player.motionY -= downFactor.getValue() / 100;
        }
        if (!mc.gameSettings.keyBindJump.isKeyDown() && !mc.gameSettings.keyBindSneak.isKeyDown()) {
            switch (downMode.getValue()) {
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

    @SubscribeEvent
    public void onMove(MoveEvent event) {
        if (nullCheck() || !isEnabled() || (!mc.player.isInLava() && !mc.player.isInWater() || mc.world.getBlockState(BlockUtil.getPlayerPos()).getBlock().equals(Blocks.AIR) || mc.world.getBlockState(BlockUtil.getPlayerPos().up()).getBlock().equals(Blocks.AIR)))
            return;
        event.setCanceled(true);
    }
}
