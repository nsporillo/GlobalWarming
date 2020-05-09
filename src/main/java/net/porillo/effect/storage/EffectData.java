package net.porillo.effect.storage;

import net.porillo.GlobalWarming;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class EffectData {

    private final String worldName;
    private final String effectName;
    private Path effectPath;

    public EffectData(String worldName, String effectName) {
        this.worldName = worldName;
        this.effectName = effectName;
        this.effectPath = GlobalWarming.getInstance().getDataFolder().toPath().resolve("effects");
    }

    public String getEffectName() {
        return effectName;
    }

    public Path getPath() {
        return this.effectPath.resolve(worldName).resolve(effectName);
    }

    public String getContents() {
        createIfNotExists();

        try {
            return new String(Files.readAllBytes(getPath()));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "";
    }

    public void writeContents(String data) {
        clearFileForNewWrite();

        try {
            Files.write(getPath(), data.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void createIfNotExists() {
        Path path = getPath();
        if (!Files.exists(path)) {
            GlobalWarming.getInstance().getLogger().info(
                    String.format("EffectData: [%s] does not exist at: [%s], creating.", effectName, path));

            try {
                Files.createDirectories(effectPath.resolve(worldName));
                Files.createFile(path);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void clearFileForNewWrite() {
        Path file = getPath();
        try {
            if (Files.exists(file)) {
                Files.delete(file);
                Files.createFile(file);
            } else {
                Files.createFile(file);
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
