package dev.zprestige.ruby.manager;

import dev.zprestige.ruby.module.Category;
import dev.zprestige.ruby.module.Module;
import dev.zprestige.ruby.module.client.*;
import dev.zprestige.ruby.module.combat.*;
import dev.zprestige.ruby.module.exploit.*;
import dev.zprestige.ruby.module.misc.*;
import dev.zprestige.ruby.module.movement.*;
import dev.zprestige.ruby.module.player.*;
import dev.zprestige.ruby.module.visual.*;
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
        moduleList.add(new ClickGui().withSuper("ClickGui", Category.Client));
        moduleList.add(new Configs().withSuper("Configs", Category.Client));
        moduleList.add(new Enemies().withSuper("Enemies", Category.Client));
        moduleList.add(new Friends().withSuper("Friends", Category.Client));
        moduleList.add(new Hud().withSuper("Hud", Category.Client));
        moduleList.add(new Notify().withSuper("Notify", Category.Client));

        moduleList.add(new AntiCity().withSuper("AntiCity", Category.Combat));
        moduleList.add(new AntiLavaFag().withSuper("AntiLavaFag", Category.Combat));
        moduleList.add(new AntiTrap().withSuper("AntiTrap", Category.Combat));
        moduleList.add(new Aura().withSuper("Aura", Category.Combat));
        moduleList.add(new AutoCrystal().withSuper("AutoCrystal", Category.Combat));
        moduleList.add(new AutoEcMeMainFucker().withSuper("AutoEcMeMainFucker", Category.Combat));
        moduleList.add(new AutoWeb().withSuper("AutoWeb", Category.Combat));
        moduleList.add(new CevBreaker().withSuper("CevBreaker", Category.Combat));
        moduleList.add(new FeetPlace().withSuper("FeetPlace", Category.Combat));
        moduleList.add(new Filler().withSuper("Filler", Category.Combat));
        moduleList.add(new HolePush().withSuper("HolePush", Category.Combat));
        moduleList.add(new Offhand().withSuper("Offhand", Category.Combat));
        moduleList.add(new Robot().withSuper("Robot", Category.Combat));
        moduleList.add(new SimpleCa().withSuper("SimpleCa", Category.Combat));
        moduleList.add(new Trap().withSuper("Trap", Category.Combat));

        moduleList.add(new AntiLog4j().withSuper("AntiLog4j", Category.Exploit));
        moduleList.add(new BlockClip().withSuper("BlockClip", Category.Exploit));
        moduleList.add(new ChorusManipulator().withSuper("ChorusManipulator", Category.Exploit));
        moduleList.add(new ChorusPredict().withSuper("ChorusPredict", Category.Exploit));
        moduleList.add(new HandMine().withSuper("HandMine", Category.Exploit));
        moduleList.add(new LavaFlight().withSuper("LavaFlight", Category.Exploit));
        moduleList.add(new McPvp().withSuper("McPvp", Category.Exploit));
        moduleList.add(new Phase().withSuper("Phase", Category.Exploit));
        moduleList.add(new SupplyDrop().withSuper("SupplyDrop", Category.Exploit));
        moduleList.add(new Teleport().withSuper("Teleport", Category.Exploit));
        moduleList.add(new Timer().withSuper("Timer", Category.Exploit));
        moduleList.add(new WallTeleport().withSuper("WallTeleport", Category.Exploit));

        moduleList.add(new AutoMine().withSuper("AutoMine", Category.Misc));
        moduleList.add(new AutoRespawn().withSuper("AutoRespawn", Category.Misc));
        moduleList.add(new AutoWither().withSuper("AutoWither", Category.Misc));
        moduleList.add(new FakePlayer().withSuper("FakePlayer", Category.Misc));
        moduleList.add(new FreeLook().withSuper("FreeLook", Category.Misc));
        moduleList.add(new InventoryCleaner().withSuper("MiddleClick", Category.Misc));
        moduleList.add(new NoInterpolation().withSuper("NoInterpolation", Category.Misc));
        moduleList.add(new PacketLogger().withSuper("PacketLogger", Category.Misc));
        moduleList.add(new RunDetect().withSuper("RunDetect", Category.Misc));
        moduleList.add(new TabList().withSuper("TabList", Category.Misc));
        moduleList.add(new VanillaSpoof().withSuper("VanillaSpoof", Category.Misc));

        moduleList.add(new AntiWeb().withSuper("AntiWeb", Category.Movement));
        moduleList.add(new ElytraFlight().withSuper("ElytraFlight", Category.Movement));
        moduleList.add(new FastFall().withSuper("FastFall", Category.Movement));
        moduleList.add(new HoleDrag().withSuper("HoleDrag", Category.Movement));
        moduleList.add(new LiquidSpeed().withSuper("LiquidSpeed", Category.Movement));
        moduleList.add(new LongJump().withSuper("LongJump", Category.Movement));
        moduleList.add(new NoSlow().withSuper("NoSlow", Category.Movement));
        moduleList.add(new Speed().withSuper("Speed", Category.Movement));
        moduleList.add(new Sprint().withSuper("Sprint", Category.Movement));
        moduleList.add(new StashHunter().withSuper("StashHunter", Category.Movement));
        moduleList.add(new Step().withSuper("Step", Category.Movement));
        moduleList.add(new TickShift().withSuper("TickShift", Category.Movement));
        moduleList.add(new Velocity().withSuper("Velocity", Category.Movement));

        moduleList.add(new AutoArmor().withSuper("AutoArmor", Category.Player));
        moduleList.add(new AutoEcMeDupe().withSuper("AutoEcMeDupe", Category.Player));
        moduleList.add(new AutoSingleMend().withSuper("AutoSingleMend", Category.Player));
        moduleList.add(new CraftingSlots().withSuper("CraftingSlots", Category.Player));
        moduleList.add(new Criticals().withSuper("Criticals", Category.Player));
        moduleList.add(new FakeHacker().withSuper("FakeHacker", Category.Player));
        moduleList.add(new FastExp().withSuper("FastExp", Category.Player));
        moduleList.add(new HotbarFiller().withSuper("HotbarFiller", Category.Player));
        moduleList.add(new NoEntityTrace().withSuper("NoEntityTrace", Category.Player));
        moduleList.add(new NoRotations().withSuper("NoRotations", Category.Player));
        moduleList.add(new PacketMine().withSuper("PacketMine", Category.Player));
        moduleList.add(new Quiver().withSuper("Quiver", Category.Player));
        moduleList.add(new SelfFiller().withSuper("SelfFiller", Category.Player));
        moduleList.add(new Suicide().withSuper("Suicide", Category.Player));

        moduleList.add(new Ambience().withSuper("Ambience", Category.Visual));
        moduleList.add(new Crosshair().withSuper("Crosshair", Category.Visual));
        moduleList.add(new CrystalChams().withSuper("CrystalChams", Category.Visual));
        moduleList.add(new DistanceAlpha().withSuper("DistanceAlpha", Category.Visual));
        moduleList.add(new EntityTrails().withSuper("EntityTrails", Category.Visual));
        moduleList.add(new ESP().withSuper("ESP", Category.Visual));
        moduleList.add(new HoleESP().withSuper("HoleESP", Category.Visual));
        moduleList.add(new HudNotifications().withSuper("HudNotifications", Category.Visual));
        moduleList.add(new Interactions().withSuper("Interactions", Category.Visual));
        moduleList.add(new ItemModification().withSuper("ItemModification", Category.Visual));
        moduleList.add(new Nametags().withSuper("Nametags", Category.Visual));
        moduleList.add(new NoRender().withSuper("NoRender", Category.Visual));
        moduleList.add(new PopChams().withSuper("PopChams", Category.Visual));
        moduleList.add(new Shaders().withSuper("Shaders", Category.Visual));
        moduleList.add(new ShulkerPeek().withSuper("ShulkerPeek", Category.Visual));
        moduleList.add(new WorldTweaks().withSuper("WorldTweaks", Category.Visual));

        //addModules("client");
        //addModules("combat");
        //addModules("exploit");
        //addModules("misc");
        //addModules("movement");
        //addModules("player");
        //addModules("visual");
    }

    public void addModules(String folder) {
        try {
            List<Class<?>> classes = ClassFinder.from("dev.zprestige.ruby.module." + folder);
            if (classes == null) return;
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
