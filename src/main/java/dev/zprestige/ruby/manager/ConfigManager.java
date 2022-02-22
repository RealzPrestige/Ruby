package dev.zprestige.ruby.manager;

import dev.zprestige.ruby.Ruby;
import dev.zprestige.ruby.module.Category;
import dev.zprestige.ruby.module.Module;
import dev.zprestige.ruby.setting.Setting;
import dev.zprestige.ruby.setting.impl.*;
import net.minecraft.client.Minecraft;
import org.lwjgl.input.Keyboard;

import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.stream.Collectors;

@SuppressWarnings("ResultOfMethodCallIgnored")
public class ConfigManager {
    protected final Minecraft mc = Ruby.mc;
    protected final String separator = File.separator;
    protected final ArrayList<Module> moduleList = Ruby.moduleManager.moduleList;
    protected File configPath = new File(mc.gameDir + separator + "Ruby" + separator + "Configs");

    public ConfigManager loadFromActiveConfig() {
        String activeConfig = readActiveConfig();
        if (!activeConfig.equals("NONE")) {
            configPath = new File(configPath + separator + activeConfig);
            loadModules(false);
        }
        return this;
    }

    public void load(String folder, boolean onlyVisuals) {
        configPath = new File(configPath + separator + folder);
        loadModules(onlyVisuals);
        if (!onlyVisuals) {
            saveActiveConfig(folder);
        }
    }

    public void save(String folder, boolean onlyVisuals) {
        configPath = new File(configPath + separator + folder);
        saveModules(onlyVisuals);
        if (!onlyVisuals) {
            saveActiveConfig(folder);
        }
    }

    public void saveSocials() {
        final File file = registerPathAndCreate(mc.gameDir + separator + "Ruby" + separator + "Socials");
        final File friends = registerPathAndCreate(file + separator + "Friends.txt");
        final File enemies = registerPathAndCreate(file + separator + "Enemies.txt");
        try {
            final BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(friends));
            Ruby.friendManager.getFriendList().forEach(friendPlayer -> writeLine(bufferedWriter, friendPlayer.getName()));
            bufferedWriter.close();
            final BufferedWriter bufferedWriter2 = new BufferedWriter(new FileWriter(enemies));
            Ruby.enemyManager.getEnemyList().forEach(enemyPlayer -> writeLine(bufferedWriter2, enemyPlayer.getName()));
            bufferedWriter2.close();
        } catch (IOException ignored) {
        }
    }

    public ConfigManager readAndSetSocials(){
        final File file = registerPathAndCreate(mc.gameDir + separator + "Ruby" + separator + "Socials");
        final File friends = registerPathAndCreate(file + separator + "Friends.txt");
        final File enemies = registerPathAndCreate(file + separator + "Enemies.txt");
        try {
            final BufferedReader bufferReader = new BufferedReader(new InputStreamReader(new DataInputStream(new FileInputStream(friends))));
            final BufferedReader bufferReader2 = new BufferedReader(new InputStreamReader(new DataInputStream(new FileInputStream(enemies))));
            Ruby.friendManager.getFriendList().clear();
            Ruby.enemyManager.getEnemyList().clear();
            bufferReader.lines().forEach(line -> Ruby.friendManager.addFriend(line));
            bufferReader2.lines().forEach(line -> Ruby.enemyManager.addEnemy(line));
        } catch (IOException ignored) {
        }
        return this;
    }

    protected String readActiveConfig() {
        final File file = registerFileAndCreate(mc.gameDir + separator + "Ruby");
        try {
            final BufferedReader bufferReader = new BufferedReader(new InputStreamReader(new DataInputStream(new FileInputStream(file))));
            String activeConfig = bufferReader.readLine().replace("\"", "");
            bufferReader.close();
            return activeConfig;
        } catch (IOException ignored) {
        }
        return "NONE";
    }

    protected void saveActiveConfig(String folder) {
        final File file = registerFileAndCreate(mc.gameDir + separator + "Ruby");
        try {
            final BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file));
            writeLine(bufferedWriter, "\"" + folder + "\"");
            bufferedWriter.close();
        } catch (IOException ignored) {
        }
    }

    protected void loadModules(boolean onlyVisuals) {
        ArrayList<Module> modules = onlyVisuals ? moduleList.stream().filter(module -> module.getCategory().equals(Category.Visual)).collect(Collectors.toCollection(ArrayList::new)) : moduleList;
        modules.forEach(module -> {
            final File path = new File(configPath + separator + module.getCategory().toString());
            final File file = new File(path + separator + module.getName() + ".txt");
            try {
                final BufferedReader bufferReader = new BufferedReader(new InputStreamReader(new DataInputStream(new FileInputStream(file))));
                bufferReader.lines().forEach(line -> {
                    final String[] split = line.replace("\"", "").replace(" ", "").split(":");
                    setValueFromSetting(getSettingByNameAndModule(module, split[0]), split[1], split[0].equals("Enabled"));
                });
                bufferReader.close();
            } catch (IOException ignored) {
            }
        });
    }

    @SuppressWarnings("ALL")
    protected void setValueFromSetting(Setting setting, String line, boolean enabled) {
        if (enabled) {
            final Module module = setting.getModule();
            if (line.equals("true") && !module.isEnabled()) {
                module.enableModule();
            } else if (module.isEnabled()) {
                module.disableModule();
            }
        }
        if (setting instanceof StringSetting || setting instanceof ModeSetting) {
            setting.setValue(line);
        }
        if (setting instanceof IntegerSetting) {
            setting.setValue(Integer.parseInt(line));
        }
        if (setting instanceof FloatSetting) {
            setting.setValue(Float.parseFloat(line));
        }
        if (setting instanceof DoubleSetting) {
            setting.setValue(Double.parseDouble(line));
        }
        if (setting instanceof BooleanSetting) {
            setting.setValue(Boolean.parseBoolean(line));
        }
        if (setting instanceof KeySetting) {
            setting.setValue(Keyboard.getKeyIndex(line));
        }
        if (setting instanceof ColorSetting) {
            ((ColorSetting) setting).setColor(new Color(Integer.parseInt(line), true));
        }
    }

    protected Setting<?> getSettingByNameAndModule(Module module, String name) {
        return module.getSettingList().stream().filter(setting -> setting.getName().equals(name)).findFirst().orElse(null);
    }

    protected void saveModules(boolean onlyVisuals) {
        ArrayList<Module> modules = onlyVisuals ? moduleList.stream().filter(module -> module.getCategory().equals(Category.Visual)).collect(Collectors.toCollection(ArrayList::new)) : moduleList;
        modules.forEach(module -> {
            final File path = registerPathAndCreate(configPath + separator + module.getCategory().toString());
            final File file = registerFileAndCreate(path + separator + module.getName() + ".txt");
            try {
                final BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file));
                module.getSettingList().stream().filter(setting -> !(setting instanceof ParentSetting)).forEach(setting -> writeLine(bufferedWriter, "\"" + setting.getName() + "\": \"" + (setting instanceof ColorSetting ? ((ColorSetting) setting).getValue().getRGB() : setting instanceof KeySetting ? Keyboard.getKeyName(((KeySetting) setting).getKey()) : setting.getValue()) + "\""));
                bufferedWriter.close();
            } catch (IOException ignored) {
            }
        });
    }

    protected void writeLine(BufferedWriter bufferedWriter, String line) {
        try {
            bufferedWriter.write(line + "\r\n");
        } catch (IOException ignored) {
        }
    }

    protected File registerFileAndCreate(final String file) {
        final File file1 = new File(file);
        try {
            file1.createNewFile();
        } catch (IOException ignored) {
        }
        return file1;
    }

    protected File registerPathAndCreate(final String file) {
        final File file1 = new File(file);
        file1.mkdirs();
        return file1;
    }
}
