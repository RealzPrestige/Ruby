package dev.zprestige.ruby.manager;

import dev.zprestige.ruby.module.Category;
import dev.zprestige.ruby.module.Module;
import dev.zprestige.ruby.util.ClassFinder;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class ModuleManager {
    public ArrayList<Module> orderedModuleList = new ArrayList<>();
    public ArrayList<Module> moduleList = new ArrayList<>();

    public ModuleManager() {
        addModules("client");
        addModules("combat");
        addModules("exploit");
        addModules("misc");
        addModules("movement");
        addModules("player");
        addModules("visual");
        moduleList.sort(Comparator.comparing(Module::getName));
        moduleList.sort(Comparator.comparing(Module::getCategory));
        orderedModuleList.addAll(moduleList);
    }

    public void addModules(String folder) {
        try {
            List<Class<?>> classes = ClassFinder.from("dev.zprestige.ruby.module." + folder);
            if (classes == null)
                return;
            for (Class<?> clazz : classes) {
                if (!Modifier.isAbstract(clazz.getModifiers()) && Module.class.isAssignableFrom(clazz)) {
                    for (Constructor<?> constructor : clazz.getConstructors()) {
                        Module instance = (Module) constructor.newInstance();
                        Arrays.stream(instance.getClass().getDeclaredFields()).filter(field -> !field.isAccessible()).forEach(field -> field.setAccessible(true));
                        moduleList.add(instance);
                    }
                }
            }

        } catch (Exception ignored) {
        }
    }

    public ArrayList<Module> getOrderedModuleList() {
        return orderedModuleList;
    }

    public ArrayList<Module> getOrderedModuleListByLength() {
        ArrayList<Module> lengthOrdered = new ArrayList<>(moduleList);
        lengthOrdered.sort(Comparator.comparing(Module::getModuleNameWidth).reversed());
        return lengthOrdered;
    }

    public List<Module> getModulesInCategory(Category category) {
        ArrayList<Module> modulesInCategory = new ArrayList<>();
        for (Module module : orderedModuleList)
            if (module.getCategory().equals(category))
                modulesInCategory.add(module);
        return modulesInCategory;
    }

    public List<Category> getCategories() {
        return Arrays.asList(Category.values());
    }
}
