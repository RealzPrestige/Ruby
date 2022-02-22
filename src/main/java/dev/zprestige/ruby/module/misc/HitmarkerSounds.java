package dev.zprestige.ruby.module.misc;

import com.google.common.io.ByteStreams;
import dev.zprestige.ruby.eventbus.annotation.RegisterListener;
import dev.zprestige.ruby.events.PacketEvent;
import dev.zprestige.ruby.module.Category;
import dev.zprestige.ruby.module.Module;
import dev.zprestige.ruby.module.ModuleInfo;
import dev.zprestige.ruby.setting.impl.FloatSetting;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

@ModuleInfo(name = "HitmarkerSounds" , category = Category.Misc, description = "plays hitmarker soudn when u hit sumn")
public class HitmarkerSounds extends Module {
    public FloatSetting volume = createSetting("Volume", 2.0f, 0.1f, 5.0f);
    public File hitFile = new File(mc.gameDir + File.separator + "Ruby" + File.separator + "hitmarker.wav");

    public HitmarkerSounds() {
        try {
            if (!hitFile.exists()) {
                InputStream stream = getClass().getClassLoader().getResourceAsStream("assets/sounds/hitmarker.wav");
                FileOutputStream outputStream = new FileOutputStream(hitFile);
                ByteStreams.copy(stream, outputStream);
            }
        } catch (Exception ignored) {
        }
    }


    private void playSound() {
        if (!hitFile.exists())
            return;
        try {
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(hitFile);
            Clip clip = AudioSystem.getClip();
            clip.open(audioInputStream);
            FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
            gainControl.setValue(-50F + volume.getValue() * 10F);
            clip.start();
        } catch (Exception ignored) {
        }
    }

    @RegisterListener
    public void onPacketSend(PacketEvent.PacketSendEvent event) {
        if (nullCheck() || !isEnabled() || !(event.getPacket() instanceof CPacketUseEntity) || !(((CPacketUseEntity) event.getPacket()).getAction() == CPacketUseEntity.Action.ATTACK))
            return;
        try {
            playSound();
        } catch (Exception ignored) {
        }
    }

}
