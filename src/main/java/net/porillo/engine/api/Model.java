package net.porillo.engine.api;

import net.porillo.GlobalWarming;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public abstract class Model {

	private String modelName;

	public Model(String modelName) {
		this.modelName = modelName;
	}

	public Path getPath() {
		return GlobalWarming.getInstance().getDataFolder().toPath().resolve("models").resolve(modelName);
	}

	public List<String> getLines() {
		List<String> modelLines = new ArrayList<>();
		createIfNotExists();
		
		try (BufferedReader reader = new BufferedReader(new InputStreamReader(Files.newInputStream(getPath())))) {
			String line = null;
			while ((line = reader.readLine()) != null) {
				modelLines.add(line);
			}
		} catch (IOException x) {
			x.printStackTrace();
		}

		return modelLines;
	}

	public void writeLines(List<String> lines) {
		clearFileForNewWrite();
		
		try {
			Files.write(getPath(), lines, Charset.forName("UTF-8"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public String getContents() {
		byte[] content = null;
		createIfNotExists();
		
		try {
			Path file = getPath();
			int size = (int) Files.size(file);
			content = new byte[size];
			InputStream in = Files.newInputStream(file);
			int offset = 0;

			while (offset < size) {
				offset += in.read(content, offset, (size - offset));
			}

			in.close();
		} catch (IOException ex) {
			ex.printStackTrace();
		}

		return new String(content, Charset.forName("UTF-8"));
	}

	public void writeContents(String data) {
		clearFileForNewWrite();
		
		try (BufferedWriter writer = Files.newBufferedWriter(getPath(), Charset.forName("UTF-8"))) {
			writer.write(data, 0, data.length());
		} catch (IOException x) {
			x.printStackTrace();
		}
	}
	
	private void createIfNotExists() {
		Path file = getPath();
		
		if (!Files.exists(file)) {
			GlobalWarming.getInstance().getLogger().info("Model " + modelName + " does not exist, creating.");
			GlobalWarming.getInstance().saveResource("models/" + modelName, false);
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

	public String getName() {
		return modelName;
	}

	public abstract void loadModel();
}
