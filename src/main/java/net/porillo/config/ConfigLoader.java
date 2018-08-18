package net.porillo.config;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.*;

public abstract class ConfigLoader {

    public FileConfiguration conf;
    public String fileName;
    public Plugin plugin;
    private File configFile;

    ConfigLoader(Plugin plugin, String fileName) {
        this.plugin = plugin;
        this.fileName = fileName;
        File dataFolder = plugin.getDataFolder();
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

            writeConfig(plugin.getResource("config.yml"));
        }

        conf = YamlConfiguration.loadConfiguration(configFile);
    }

    private void addDefaults() {
        conf.options().copyDefaults(true);
        saveConfig();
    }

    void load() {
        if (!configFile.exists()) {
            plugin.getDataFolder().mkdir();
            saveConfig();
        }
        addDefaults();
        loadKeys();
    }

    protected abstract void loadKeys();

    protected abstract void reload();

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
            if (plugin.getResource(fileName) != null) {
                plugin.getLogger().info("Saving " + fileName + " to disk");
                plugin.saveResource(fileName, false);
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