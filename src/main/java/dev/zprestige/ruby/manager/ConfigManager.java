package dev.zprestige.ruby.manager;

import dev.zprestige.ruby.Ruby;
import dev.zprestige.ruby.module.Module;
import dev.zprestige.ruby.settings.Setting;
import dev.zprestige.ruby.settings.impl.*;
import net.minecraft.client.Minecraft;
import org.lwjgl.input.Keyboard;

import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicReference;

@SuppressWarnings("ResultOfMethodCallIgnored")
public class ConfigManager {
    protected final Minecraft mc = Ruby.mc;
    protected final String separator = File.separator;
    protected final ArrayList<Module> moduleList = Ruby.moduleManager.moduleList;
    protected File configPath = new File(mc.gameDir + separator + "Ruby" + separator + "Configs");

    public ConfigManager() {
        if (!configPath.exists())
            configPath.mkdirs();
    }

    public ConfigManager loadFromActiveConfig() {
        String activeConfig = readActiveConfig();
        if (!activeConfig.equals("NONE") && !activeConfig.equals("")) {
            configPath = new File(mc.gameDir + separator + "Ruby" + separator + "Configs" + separator + activeConfig);
            loadModules();
        }
        return this;
    }

    public void load(String folder) {
        configPath = new File(mc.gameDir + separator + "Ruby" + separator + "Configs" + separator + folder);
        if (configPath.exists()) {
            loadModules();
            saveActiveConfig(folder);
        }
    }

    public void save(String folder) {
        configPath = new File(mc.gameDir + separator + "Ruby" + separator + "Configs" + separator + folder);
        saveModules();
    }

    public void saveSocials() {
        final File file = registerPathAndCreate(mc.gameDir + separator + "Ruby" + separator + "Socials");
        final File friends = registerFileAndCreate(file + separator + "Friends.txt");
        final File enemies = registerFileAndCreate(file + separator + "Enemies.txt");
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

    public ConfigManager readAndSetSocials() {
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
        final File file = registerFileAndCreate(mc.gameDir + separator + "Ruby" + separator + "ActiveConfig.txt");
        if (!file.exists()) {
            return "NONE";
        }
        try {
            final BufferedReader bufferReader = new BufferedReader(new InputStreamReader(new DataInputStream(new FileInputStream(file))));
            AtomicReference<String> activeConfig = new AtomicReference<>("");
            bufferReader.lines().forEach(line -> activeConfig.set(line.replace("\"", "")));
            bufferReader.close();
            return activeConfig.get();
        } catch (IOException ignored) {
        }
        return "NONE";
    }

    protected void saveActiveConfig(String folder) {
        final File file = registerFileAndCreate(mc.gameDir + separator + "Ruby" + separator + "ActiveConfig.txt");
        try {
            final BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file));
            writeLine(bufferedWriter, "\"" + folder + "\"");
            bufferedWriter.close();
        } catch (IOException ignored) {
        }
    }

    protected void loadModules() {
        moduleList.forEach(module -> {
            final File path = new File(configPath + separator + module.getCategory().toString());
            final File file = new File(path + separator + module.getName() + ".txt");
            try {
                final BufferedReader bufferReader = new BufferedReader(new InputStreamReader(new DataInputStream(new FileInputStream(file))));
                bufferReader.lines().forEach(line -> {
                    final String[] s = line.replace("\"", "").replace(" ", "").split(":");
                    final String split = s[0];
                    final Setting setting = getSettingByNameAndModule(module, split);
                    String split2 = "null";
                    if (setting instanceof ColorSwitch) {
                        split2 = s[2];
                    }
                    setValueFromSetting(setting, s[1], split.equals("Enabled"), split2);
                });
                bufferReader.close();
            } catch (IOException ignored) {
            }
        });
    }

    @SuppressWarnings("ALL")
    protected void setValueFromSetting(Setting setting, String line, boolean enabled, String colorSwitch) {
        if (enabled) {
            final Module module = setting.getModule();
            if (line.equals("true") && !module.isEnabled()) {
                module.enableModule();
            } else if (line.equals("false") && module.isEnabled()) {
                module.disableModule();
            }
            return;
        }
        if (setting instanceof ColorBox) {
            ((ColorBox) setting).setValue(new Color(Integer.parseInt(line), true));
            return;
        }
        if (setting instanceof ColorSwitch) {
            ((ColorSwitch) setting).setSwitchValue(Boolean.parseBoolean(line));
            ((ColorSwitch) setting).setColor(new Color(Integer.parseInt(colorSwitch), true));
            return;
        }
        if (setting instanceof ComboBox) {
            ((ComboBox) setting).setValue(line);
            return;
        }
        if (setting instanceof Slider) {
            ((Slider) setting).setValue(Float.parseFloat(line));
            return;
        }
        if (setting instanceof Key) {
            ((Key) setting).setValue(Keyboard.getKeyIndex(line));
            return;
        }

        if (setting instanceof Switch) {
            ((Switch) setting).setValue(Boolean.parseBoolean(line));
        }
    }

    protected Setting getSettingByNameAndModule(Module module, String name) {
        for (Setting setting : module.getSettings()) {
            if (setting.getName().replace(" ", "").equals(name.replace(" ", ""))) {
                return setting;
            }
        }
        return null;
    }

    protected void saveModules() {
        moduleList.forEach(module -> {
            final File path = registerPathAndCreate(configPath + separator + module.getCategory().toString());
            final File file = registerFileAndCreate(path + separator + module.getName() + ".txt");
            try {
                final BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file));
                module.getSettings().stream().filter(setting -> !(setting instanceof Parent)).forEach(setting -> {
                    if (setting instanceof ColorBox) {
                        ColorBox colorBox = (ColorBox) setting;
                        writeLine(bufferedWriter, "\"" + colorBox.getName() + "\": \"" + colorBox.GetColor().getRGB() + "\"");
                    } else if (setting instanceof ColorSwitch) {
                        ColorSwitch colorSwitch = (ColorSwitch) setting;
                        writeLine(bufferedWriter, "\"" + colorSwitch.getName() + "\": \"" + colorSwitch.GetSwitch() + "\":\"" + colorSwitch.GetColor().getRGB() + "\"");
                    } else if (setting instanceof ComboBox) {
                        ComboBox comboBox = (ComboBox) setting;
                        writeLine(bufferedWriter, "\"" + comboBox.getName() + "\": \"" + comboBox.GetCombo() + "\"");
                    } else if (setting instanceof Key) {
                        Key key = (Key) setting;
                        writeLine(bufferedWriter, "\"" + key.getName() + "\": \"" + Keyboard.getKeyName((key.GetKey())) + "\"");
                    } else if (setting instanceof Slider) {
                        Slider slider = (Slider) setting;
                        writeLine(bufferedWriter, "\"" + slider.getName() + "\": \"" + slider.GetSlider() + "\"");
                    } else if (setting instanceof Switch) {
                        Switch aSwitch = (Switch) setting;
                        writeLine(bufferedWriter, "\"" + aSwitch.getName() + "\": \"" + aSwitch.GetSwitch() + "\"");
                    }
                });
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
