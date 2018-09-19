package net.porillo.engine.api;

import lombok.Getter;
import net.porillo.GlobalWarming;
import net.porillo.effect.EffectModel;
import net.porillo.engine.models.*;
import net.porillo.objects.Contribution;
import net.porillo.objects.Reduction;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public abstract class Model {

	@Getter private final String worldName;
	@Getter private final String modelName;

	private Path modelsPath;

	public Model(String worldName, String modelName) {
		this.modelName = modelName;
		this.worldName = worldName;

		if (GlobalWarming.getInstance() != null) {
			this.modelsPath = GlobalWarming.getInstance().getDataFolder().toPath().resolve("models");
		} else {
			try {
				this.modelsPath =  Paths.get(getClass().getResource("/models").toURI());
			} catch (URISyntaxException e) {
				e.printStackTrace();
			}
		}
	}

	public Path getPath() {
		return this.modelsPath.resolve(worldName).resolve(modelName);
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

	public void writeContents(String data){
		clearFileForNewWrite();

		try {
			Files.write(getPath(), data.getBytes());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void createIfNotExists() {
		Path file = getPath();

		if (!Files.exists(file)) {
			GlobalWarming.getInstance().getLogger().info("Model " + modelName + " does not exist, creating.");

			try {
				// Copy resource from JAR to the correct path
				Files.createDirectories(modelsPath.resolve(worldName));
				Files.copy(GlobalWarming.getInstance().getResource(String.format("models/%s", modelName)), getPath());
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

	public abstract void loadModel();

	public enum ModelType {
		CARBON_INDEX,
		CONTRIBUTION,
		EFFECT,
		ENTITY_FITNESS,
		REDUCTION,
		SCORE_TEMP;
	}

}
