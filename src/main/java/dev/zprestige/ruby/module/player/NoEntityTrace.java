package dev.zprestige.ruby.module.player;

import dev.zprestige.ruby.eventbus.annotation.RegisterListener;
import dev.zprestige.ruby.events.MouseOverEvent;
import dev.zprestige.ruby.module.Category;
import dev.zprestige.ruby.module.Module;
import dev.zprestige.ruby.module.ModuleInfo;
import dev.zprestige.ruby.setting.impl.BooleanSetting;
import net.minecraft.init.Items;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
@ModuleInfo(name = "NoEntityTrace" , category = Category.Player, description = "cancels entity trace hit")
public class NoEntityTrace extends Module {
    public BooleanSetting pickaxe = createSetting("Pickaxe", false);
    public BooleanSetting gapple = createSetting("Gapple", false);

    @RegisterListener
    public void mouseOverEvent(MouseOverEvent event) {
        if (nullCheck() || !isEnabled())
            return;
        if ((pickaxe.getValue() && mc.player.getHeldItemMainhand().getItem().equals(Items.DIAMOND_PICKAXE)) || (gapple.getValue() && mc.player.getHeldItemMainhand().getItem().equals(Items.GOLDEN_APPLE)))
            event.setCancelled(true);
    }
}
