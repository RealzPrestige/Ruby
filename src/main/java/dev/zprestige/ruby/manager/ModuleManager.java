package dev.zprestige.ruby.manager;

import dev.zprestige.ruby.module.Category;
import dev.zprestige.ruby.module.Module;
import dev.zprestige.ruby.util.ClassFinder;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ModuleManager {
    public ArrayList<Module> moduleList = new ArrayList<>();

    public ModuleManager() {
        addModules("client");
        addModules("combat");
        addModules("exploit");
        addModules("misc");
        addModules("movement");
        addModules("player");
        addModules("visual");
    }

    public void addModules(String folder) {
        try {
            List<Class<?>> classes = ClassFinder.from("dev.zprestige.ruby.module." + folder);
            if (classes == null)
                return;
            for (Class<?> clazz : classes) {
                if (!Modifier.isAbstract(clazz.getModifiers()) && Module.class.isAssignableFrom(clazz)) {
                    for (Constructor<?> constructor : clazz.getConstructors()) {
                        final String moduleName = clazz.getName().split("\\.")[5];
                        final Module instance = ((Module) constructor.newInstance()).withSuper(moduleName, getCategoryByName(folder));
                        Arrays.stream(instance.getClass().getDeclaredFields()).filter(field -> !field.isAccessible()).forEach(field -> field.setAccessible(true));
                        moduleList.add(instance);
                    }
                }
            }

        } catch (Exception ignored) {
        }
    }

    public List<Module> getModulesInCategory(Category category) {
        return moduleList.stream().filter(module -> module.getCategory().equals(category)).collect(Collectors.toList());
    }

    public Category getCategoryByName(String name) {
        return Arrays.stream(Category.values()).filter(category -> category.toString().toLowerCase().equals(name)).findFirst().orElse(null);
    }

    public List<Category> getCategories() {
        return Arrays.asList(Category.values());
    }
}
