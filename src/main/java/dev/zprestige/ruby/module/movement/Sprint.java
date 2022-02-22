package dev.zprestige.ruby.module.movement;

import dev.zprestige.ruby.module.Module;
import dev.zprestige.ruby.util.EntityUtil;

public class Sprint extends Module {

    @Override
    public void onTick() {
        if (EntityUtil.isMoving())
            mc.player.setSprinting(true);
    }
}
