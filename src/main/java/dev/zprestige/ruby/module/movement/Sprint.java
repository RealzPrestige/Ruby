package dev.zprestige.ruby.module.movement;

import dev.zprestige.ruby.module.Category;
import dev.zprestige.ruby.module.Module;
import dev.zprestige.ruby.module.ModuleInfo;
import dev.zprestige.ruby.util.EntityUtil;

@ModuleInfo(name = "Sprint" , category = Category.Movement, description = "Sprints automatically for u")
public class Sprint extends Module {

    @Override
    public void onTick() {
        if (EntityUtil.isMoving())
            mc.player.setSprinting(true);
    }
}
