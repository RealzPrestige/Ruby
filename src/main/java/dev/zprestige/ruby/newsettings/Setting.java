package dev.zprestige.ruby.newsettings;

import dev.zprestige.ruby.module.Module;
import dev.zprestige.ruby.newsettings.impl.Parent;

public class Setting {
    protected Module module;
    protected String name;
    protected boolean hasParent = false;
    protected Parent parent;

    public Module getModule() {
        return module;
    }

    public void setModule(Module module) {
        this.module = module;
    }

    public void setHasParent(boolean hasParent) {
        this.hasParent = hasParent;
    }

    public boolean hasParent() {
        return hasParent;
    }

    public void setParent(Parent parent) {
        this.parent = parent;
    }

    public Parent getParent() {
        return parent;
    }

    public String getName() {
        return name;
    }
}
