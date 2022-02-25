package dev.zprestige.ruby.settings;

import dev.zprestige.ruby.module.Module;
import dev.zprestige.ruby.settings.impl.Parent;

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

    public Parent getParent() {
        return parent;
    }

    public void setParent(Parent parent) {
        parent.getChildren().add(this);
        this.parent = parent;
    }

    public String getName() {
        return name;
    }

    public boolean openedParent(){
        return !hasParent || parent.GetParent();
    }
}
