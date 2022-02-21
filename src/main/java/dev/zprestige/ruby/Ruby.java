package dev.zprestige.ruby;

import dev.zprestige.ruby.events.listener.EventListener;
import dev.zprestige.ruby.manager.*;
import dev.zprestige.ruby.ui.font.RubyFont;
import net.minecraft.client.Minecraft;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.EventBus;

@Mod(modid = "ruby", name = "Ruby", version = "0.1")
public class Ruby {
    public static EventBus RubyEventBus;
    public static Minecraft mc;
    public static HoleManager holeManager;
    public static EventListener eventListener;
    public static ModuleManager moduleManager;
    public static FriendManager friendInitializer;
    public static EnemyManager enemyInitializer;
    public static ConfigManager configInitializer;
    public static TickManager tickInitializer;
    public static TotemPopManager totemPopManager;
    public static RubyFont rubyFont = new RubyFont("Font", 17.0f);

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        RubyEventBus = MinecraftForge.EVENT_BUS;
        mc = Minecraft.getMinecraft();
        holeManager = new HoleManager();
        eventListener = new EventListener();
        moduleManager = new ModuleManager();
        friendInitializer = new FriendManager();
        enemyInitializer = new EnemyManager();
        configInitializer = new ConfigManager();
        tickInitializer = new TickManager();
        totemPopManager = new TotemPopManager();
        configInitializer.loadPlayer();
    }
}

