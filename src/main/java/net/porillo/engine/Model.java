package net.porillo.engine;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

public abstract class Model {

	private String modelName;

	public Model(String modelName) {
		this.modelName = modelName;
	}

	public BufferedReader getReader() throws FileNotFoundException {
		return new BufferedReader(new FileReader(new File(new File("models"), modelName)));
	}

	public abstract void loadModel();
}
