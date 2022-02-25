package dev.zprestige.ruby.module.player;

import dev.zprestige.ruby.eventbus.annotation.RegisterListener;
import dev.zprestige.ruby.events.MouseOverEvent;
import dev.zprestige.ruby.module.Module;
import dev.zprestige.ruby.newsettings.impl.Switch;
import dev.zprestige.ruby.setting.impl.BooleanSetting;
import net.minecraft.init.Items;

public class NoEntityTrace extends Module {
    public final Switch pickaxe = Menu.Switch("Pickaxe");
    public final Switch gapple = Menu.Switch("Gapple");

    @RegisterListener
    public void mouseOverEvent(MouseOverEvent event) {
        if (nullCheck() || !isEnabled())
            return;
        if ((pickaxe.GetSwitch() && mc.player.getHeldItemMainhand().getItem().equals(Items.DIAMOND_PICKAXE)) || (gapple.GetSwitch() && mc.player.getHeldItemMainhand().getItem().equals(Items.GOLDEN_APPLE)))
            event.setCancelled(true);
    }
}
