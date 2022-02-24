package dev.zprestige.ruby.module.misc;

import dev.zprestige.ruby.eventbus.annotation.RegisterListener;
import dev.zprestige.ruby.events.TurnEvent;
import dev.zprestige.ruby.module.Module;
import dev.zprestige.ruby.newsettings.impl.Key;
import dev.zprestige.ruby.setting.impl.KeySetting;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Keyboard;

public class FreeLook extends Module {
    public final Key holdBind = Menu.Key("Hold Bind", Keyboard.KEY_NONE);
    public float yaw = 0F;
    public float pitch = 0F;

    @Override
    public void onTick() {
        if (holdBind.GetKey() != -1 && Keyboard.isKeyDown(holdBind.GetKey())) {
            mc.gameSettings.thirdPersonView = 1;
        } else if (mc.gameSettings.thirdPersonView == 1){
            mc.gameSettings.thirdPersonView = 0;
            yaw = 0.0f;
            pitch = 0.0f;
        }
    }

    @SubscribeEvent
    public void onCameraSetup(EntityViewRenderEvent.CameraSetup event) {
        if (nullCheck() || !isEnabled() || holdBind.GetKey() == -1 || !Keyboard.isKeyDown(holdBind.GetKey()))
            return;
        event.setYaw(event.getYaw() + yaw);
        event.setPitch(event.getPitch() + pitch);
    }

    @RegisterListener
    public void onTurnEvent(TurnEvent event) {
        if (nullCheck() || !isEnabled() || holdBind.GetKey() == -1 || !Keyboard.isKeyDown(holdBind.GetKey()))
            return;
        yaw = (float) ((double) yaw + (double) event.getYaw() * 0.15D);
        pitch = (float) ((double) pitch - (double) event.getPitch() * 0.15D);
        pitch = MathHelper.clamp(pitch, -90.0F, 90.0F);
        event.setCancelled(true);
    }
}
