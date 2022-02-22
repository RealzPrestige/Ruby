package dev.zprestige.ruby.manager;

import dev.zprestige.ruby.Ruby;
import dev.zprestige.ruby.module.Module;
import dev.zprestige.ruby.setting.Setting;
import dev.zprestige.ruby.setting.impl.*;

import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Objects;

public class ConfigManager {
    ArrayList<Module> modules = new ArrayList<>();
    File path;

    public ConfigManager() {
        path = new File(Ruby.mc.gameDir + File.separator + "Ruby" + File.separator + "Configs");
        if (!path.exists())
            path.mkdir();
        modules.addAll(Ruby.moduleManager.moduleList);
        if (!getActiveConfig().equals("0"))
            load(getActiveConfig());
    }

    public void deleteFolder(String name) {
        for (String file : Objects.requireNonNull(path.list())) {
            if (file.equals(name)) {
                File file1 = new File(path + File.separator + name);
                file1.delete();
            }
        }
    }

    public void save(String folder) {
        path = new File(Ruby.mc.gameDir + File.separator + "Ruby" + File.separator + "Configs" + File.separator + folder);
        if (!path.exists())
            path.mkdir();
        saveModuleFile();
        saveActiveConfig(folder);
    }

    public void load(String folder) {
        path = new File(Ruby.mc.gameDir + File.separator + "Ruby" + File.separator + "Configs" + File.separator + folder);
        if (!path.exists())
            return;
        setModuleValue();
        setModuleBind();
        setModuleSettingValues();
        saveActiveConfig(folder);
    }

    public File playerPath = new File(Ruby.mc.gameDir + File.separator + "Ruby" + File.separator + "Players");

    {
        if (!playerPath.exists())
            playerPath.mkdir();
    }

    public void savePlayer() {
        saveFriendList(playerPath);
        saveEnemyList(playerPath);
    }

    public void loadPlayer() {
        loadFriendList(playerPath);
        loadEnemyList(playerPath);
    }

    public void saveActiveConfig(String folder) {
        try {
            File file = new File(Ruby.mc.gameDir + File.separator + "Ruby" + File.separator + "ActiveConfig.txt");
            if (!file.exists())
                file.createNewFile();
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file));
            bufferedWriter.write(folder);
            bufferedWriter.close();
        } catch (Exception ignored) {
        }
    }

    public String getActiveConfig() {
        try {
            File file = new File(Ruby.mc.gameDir + File.separator + "Ruby" + File.separator + "ActiveConfig.txt");
            if (!file.exists())
                return "0";
            FileInputStream fileInputStream = new FileInputStream(file.getAbsolutePath());
            DataInputStream dataInputStream = new DataInputStream(fileInputStream);
            BufferedReader bufferReader = new BufferedReader(new InputStreamReader(dataInputStream));
            String line = bufferReader.readLine();
            bufferReader.close();
            return line;
        } catch (Exception ignored) {
        }
        return "0";
    }

    public void saveFriendList(File path) {
        try {
            File file = new File(path + File.separator + File.separator + "Friends.txt");
            if (!file.exists())
                file.createNewFile();
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file));
            for (FriendManager.FriendPlayer friendPlayer : Ruby.friendManager.getFriendList()) {
                bufferedWriter.write(friendPlayer.getName());
                bufferedWriter.write("\r\n");
            }
            bufferedWriter.close();
        } catch (Exception ignored) {
        }
    }

    public void loadFriendList(File path) {
        try {
            File file = new File(path + File.separator + "Friends.txt");
            if (!file.exists())
                return;
            FileInputStream fileInputStream = new FileInputStream(file.getAbsolutePath());
            DataInputStream dataInputStream = new DataInputStream(fileInputStream);
            BufferedReader bufferReader = new BufferedReader(new InputStreamReader(dataInputStream));
            bufferReader.lines().forEach(line -> Ruby.friendManager.addFriend(line));
            bufferReader.close();
        } catch (Exception ignored) {
        }
    }

    public void saveEnemyList(File path) {
        try {
            File file = new File(path + File.separator + "Enemies.txt");
            if (!file.exists())
                file.createNewFile();
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file));
            for (EnemyManager.EnemyPlayer enemyPlayer : Ruby.enemyManager.getEnemyList()) {
                bufferedWriter.write(enemyPlayer.getName());
                bufferedWriter.write("\r\n");
            }
            bufferedWriter.close();
        } catch (Exception ignored) {
        }
    }

    public void loadEnemyList(File path) {
        try {
            File file = new File(path + File.separator + "Enemies.txt");
            if (!file.exists())
                return;
            FileInputStream fileInputStream = new FileInputStream(file.getAbsolutePath());
            DataInputStream dataInputStream = new DataInputStream(fileInputStream);
            BufferedReader bufferReader = new BufferedReader(new InputStreamReader(dataInputStream));
            bufferReader.lines().forEach(line -> {
                String name = line;
                Ruby.enemyManager.addEnemy(name);
            });
            bufferReader.close();
        } catch (Exception ignored) {
        }
    }

    public void saveModuleFile() {
        try {
            for (Module module : modules) {
                File categoryPath = new File(path + File.separator + module.getCategory().toString());
                if (!categoryPath.exists())
                    categoryPath.mkdir();
                File file = new File(categoryPath.getAbsolutePath(), module.getName() + ".txt");
                if (!file.exists())
                    file.createNewFile();
                BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file));
                bufferedWriter.write("State : " + (module.isEnabled() ? "Enabled" : "Disabled"));
                bufferedWriter.write("\r\n");
                for (Setting setting : module.getSettingList()) {
                    if (setting.getName().equals("Keybind") || setting.getName().equals("Enabled"))
                        continue;
                    if (setting instanceof StringSetting) {
                        bufferedWriter.write(setting.getName() + " : " + setting.getValue());
                        bufferedWriter.write("\r\n");
                        continue;
                    }
                    if (setting instanceof ColorSetting) {
                        bufferedWriter.write(setting.getName() + " : " + ((ColorSetting) setting).getValue().getRGB());
                        bufferedWriter.write("\r\n");
                        continue;
                    }
                    bufferedWriter.write(setting.getName() + " : " + setting.getValue());
                    bufferedWriter.write("\r\n");
                }
                bufferedWriter.write("Keybind : " + module.getKeybind());
                bufferedWriter.close();
            }
        } catch (Exception ignored) {
        }
    }

    public void setModuleValue() {
        for (Module module : modules) {
            try {
                File categoryPath = new File(path + File.separator + module.getCategory().toString());
                if (!categoryPath.exists())
                    continue;
                File file = new File(categoryPath.getAbsolutePath(), module.getName() + ".txt");
                if (!file.exists())
                    continue;
                FileInputStream fileInputStream = new FileInputStream(file.getAbsolutePath());
                DataInputStream dataInputStream = new DataInputStream(fileInputStream);
                BufferedReader bufferReader = new BufferedReader(new InputStreamReader(dataInputStream));
                bufferReader.lines().forEach(line -> {
                    String clarification = line.split(" : ")[0];
                    String state = line.split(" : ")[1];
                    if (clarification.equals("State"))
                        if (state.equals("Enabled"))
                            module.enableModule();
                        else if (state.equals("Disabled"))
                            module.disableModule();
                });
                bufferReader.close();
            } catch (Exception ignored) {
            }
        }
    }

    public void setModuleBind() {
        for (Module module : modules) {
            try {
                File categoryPath = new File(path + File.separator + module.getCategory().toString());
                if (!categoryPath.exists())
                    continue;
                File file = new File(categoryPath.getAbsolutePath(), module.getName() + ".txt");
                if (!file.exists())
                    continue;
                FileInputStream fileInputStream = new FileInputStream(file.getAbsolutePath());
                DataInputStream dataInputStream = new DataInputStream(fileInputStream);
                BufferedReader bufferReader = new BufferedReader(new InputStreamReader(dataInputStream));
                bufferReader.lines().forEach(line -> {
                    String clarification = line.split(" : ")[0];
                    String state = line.split(" : ")[1];
                    if (clarification.equals("Keybind")) {
                        if (state.equals("0"))
                            return;
                        module.setKeybind(Integer.parseInt(state));
                    }
                });
                bufferReader.close();
            } catch (Exception ignored) {
            }
        }
    }

    public void setModuleSettingValues() {
        for (Module module : modules) {
            try {
                File categoryPath = new File(path.getAbsolutePath() + File.separator + module.getCategory().toString());
                if (!categoryPath.exists())
                    continue;
                File file = new File(categoryPath.getAbsolutePath(), module.getName() + ".txt");
                if (!file.exists())
                    continue;
                FileInputStream fileInputStream = new FileInputStream(file.getAbsolutePath());
                DataInputStream dataInputStream = new DataInputStream(fileInputStream);
                BufferedReader bufferReader = new BufferedReader(new InputStreamReader(dataInputStream));
                bufferReader.lines().forEach(line -> {
                    String clarification = line.split(" : ")[0];
                    String state = line.split(" : ")[1];
                    for (Setting setting : module.getSettingList()) {
                        if (setting.getName().equals(clarification)) {
                            if (setting instanceof StringSetting) {
                                setting.setValue(state);
                            }
                            if (setting instanceof IntegerSetting) {
                                setting.setValue(Integer.parseInt(state));
                            }
                            if (setting instanceof FloatSetting) {
                                setting.setValue(Float.parseFloat(state));
                            }
                            if (setting instanceof DoubleSetting) {
                                setting.setValue(Double.parseDouble(state));
                            }
                            if (setting instanceof BooleanSetting) {
                                setting.setValue(Boolean.parseBoolean(state));
                            }
                            if (setting instanceof KeySetting) {
                                setting.setValue(Integer.parseInt(state));
                            }
                            if (setting instanceof ColorSetting) {
                                ((ColorSetting) setting).setColor(new Color(Integer.parseInt(state), true));
                            }
                            if (setting instanceof ModeSetting) {
                                setting.setValue(state);
                            }
                        }
                    }
                });
                bufferReader.close();
            } catch (Exception ignored) {
            }
        }
    }
}