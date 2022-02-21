package dev.zprestige.ruby.module.visual;

import dev.zprestige.ruby.module.Category;
import dev.zprestige.ruby.module.Module;
import dev.zprestige.ruby.module.ModuleInfo;

@ModuleInfo(name = "ShulkerPeek", category = Category.Visual, description = "Shows whats inside shulkers")
public class ShulkerPeek extends Module {
    public static ShulkerPeek Instance;

    public ShulkerPeek(){
        Instance = this;
    }
}
