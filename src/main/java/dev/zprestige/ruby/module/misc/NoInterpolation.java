package dev.zprestige.ruby.module.misc;

import dev.zprestige.ruby.module.Category;
import dev.zprestige.ruby.module.Module;
import dev.zprestige.ruby.module.ModuleInfo;

@ModuleInfo(name = "NoInterpolation" , category = Category.Misc, description = "no smart maths on entities")
public class NoInterpolation extends Module {
    public static NoInterpolation Instance;

    public NoInterpolation() {
        Instance = this;
    }
}
