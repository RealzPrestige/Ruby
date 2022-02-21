package dev.zprestige.ruby.manager;

import dev.zprestige.ruby.Ruby;
import dev.zprestige.ruby.module.Module;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

@SuppressWarnings("ResultOfMethodCallIgnored")
public class Configu {
    protected static final String separator = File.separator;
    protected static final File configPath = new File(Ruby.mc.gameDir + separator + "Ruby" + separator + "Configs");

    public static void saveModules() {
        for (Module module : Ruby.moduleManager.moduleList) {
            final File path = registerPathAndCreate(configPath + separator + module.getCategory().toString());
            final File file = registerFileAndCreate(path + separator + module.getName() + ".txt");
            try {
                final BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file));
                module.getSettingList().forEach(setting -> {
                    writeLine(bufferedWriter, setting.getName() + " {");
                    writeLine(bufferedWriter, "  " + setting.getValue());
                    writeLine(bufferedWriter, "};");
                });
                bufferedWriter.close();
            } catch (IOException ignored) {
            }
        }
    }

    //        final BufferedReader bufferReader = new BufferedReader(new InputStreamReader(new DataInputStream(new FileInputStream(path.getAbsolutePath()))));

    protected static void writeLine(BufferedWriter bufferedWriter, String line) {
        try {
            bufferedWriter.write(line + "\r\n");
        } catch (IOException ignored) {
        }
    }

    protected static File registerFileAndCreate(final String file) {
        final File file1 = new File(file);
        try {
            file1.createNewFile();
        } catch (IOException ignored) {
        }
        return file1;
    }

    protected static File registerPathAndCreate(final String file) {
        final File file1 = new File(file);
        file1.mkdirs();
        return file1;
    }
}
