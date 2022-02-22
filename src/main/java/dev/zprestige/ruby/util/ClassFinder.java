package dev.zprestige.ruby.util;

import com.google.common.reflect.ClassPath;
import net.minecraft.launchwrapper.Launch;

import java.util.List;
import java.util.stream.Collectors;

@SuppressWarnings("ALL")
public class ClassFinder {

    public static List<Class<?>> from(String packageName) {
        try {
            return ClassPath.from(Launch.classLoader).getAllClasses().stream().filter(info -> info.getName().startsWith(packageName)).map(ClassPath.ClassInfo::load).collect(Collectors.toList());
        } catch (Exception ignored) {
            return null;
        }
    }
}
