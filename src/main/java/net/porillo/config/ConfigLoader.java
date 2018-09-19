package net.porillo.config;

import net.porillo.GlobalWarming;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.*;

public abstract class ConfigLoader {

    public FileConfiguration conf;
    public String fileName;
    private File configFile;

    ConfigLoader(String fileName, String resource) {
        this.fileName = fileName;
        File dataFolder = GlobalWarming.getInstance().getDataFolder();
        configFile = new File(dataFolder, File.separator + fileName);

        if (!configFile.exists()) {
            if (!dataFolder.exists()) {
                dataFolder.mkdir();
            }

            try {
                configFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }

            writeConfig(GlobalWarming.getInstance().getResource(resource));
        }

        conf = YamlConfiguration.loadConfiguration(configFile);
    }

    ConfigLoader(String fileName) {
        this(fileName, fileName);
    }

    private void addDefaults() {
        conf.options().copyDefaults(true);
        saveConfig();
    }

    public void load() {
        if (!configFile.exists()) {
            GlobalWarming.getInstance().getDataFolder().mkdir();
            saveConfig();
        }
        addDefaults();
        loadKeys();
    }

    protected abstract void loadKeys();

    protected void reload() {
        rereadFromDisk();
        load();
    }

    void rereadFromDisk() {
        conf = YamlConfiguration.loadConfiguration(configFile);
    }

    void saveConfig() {
        try {
            conf.save(configFile);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    void saveIfNotExist() {
        if (!configFile.exists())
            if (GlobalWarming.getInstance().getResource(fileName) != null) {
                GlobalWarming.getInstance().getLogger().info("Saving " + fileName + " to disk");
                GlobalWarming.getInstance().saveResource(fileName, false);
            }
        rereadFromDisk();
    }

    void set(String key, Object value) {
        conf.set(key, value);
    }

    private void writeConfig(InputStream in) {
        OutputStream out = null;
        try {
            out = new FileOutputStream(configFile);
            int read = 0;
            byte[] bytes = new byte[1024];
            while ((read = in.read(bytes)) != -1) {
                out.write(bytes, 0, read);
            }
            out.flush();
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
                if (out != null) {
                    out.close();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }
}