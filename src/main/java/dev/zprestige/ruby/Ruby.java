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
    public static ThreadManager threadManager;
    public static HoleManager holeManager;
    public static EventListener eventListener;
    public static ModuleManager moduleManager;
    public static FriendManager friendManager;
    public static EnemyManager enemyManager;
    public static TickManager tickManager;
    public static TotemPopManager totemPopManager;
    public static ConfigManager configManager;
    public static RubyFont rubyFont = new RubyFont("Font", 17.0f);

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        RubyEventBus = MinecraftForge.EVENT_BUS;
        mc = Minecraft.getMinecraft();
        threadManager = new ThreadManager();
        holeManager = new HoleManager();
        eventListener = new EventListener();
        moduleManager = new ModuleManager();
        friendManager = new FriendManager();
        enemyManager = new EnemyManager();
        tickManager = new TickManager();
        totemPopManager = new TotemPopManager();
        configManager = new ConfigManager();
    }
}

