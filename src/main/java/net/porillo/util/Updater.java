package net.porillo.util;

import net.porillo.GlobalWarming;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;

public class Updater {

	private static Updater instance;
	public static final String LIBDIR = "plugins/GlobalWarming/lib/";
	private static final String sqlLocation = "https://s3.amazonaws.com/mcgw.tech/assets/";

	public boolean hasJar(String name) {
		return new File("plugins/GlobalWarming/lib/" + name).exists();
	}

	public void download(String name) throws IOException {
		URL url = new URL(sqlLocation + name);
		GlobalWarming.getInstance().getLogger().info("Downloading " + url.getPath() + " ...");
		ReadableByteChannel readableByteChannel = Channels.newChannel(url.openStream());

		File libDir = new File(LIBDIR);
		if (!libDir.exists()) {
			libDir.mkdirs();
		}

		FileOutputStream fileOutputStream = new FileOutputStream("plugins/GlobalWarming/lib/" + name);
		fileOutputStream.getChannel().transferFrom(readableByteChannel, 0, Long.MAX_VALUE);
	}

	public String getNativeDirectory() {
		final String os = System.getProperty("os.name").toLowerCase();

		if (os.contains("windows")) {
			return "native/Windows/";
		} else if (os.contains("mac")) {
			return "native/Mac/";
		} else {
			return "native/Linux/";
		}
	}

	public String getNativeFile() {
		final String os = System.getProperty("os.name").toLowerCase();

		if (os.contains("windows")) {
			return "sqlitejdbc.dll";
		} else if (os.contains("mac")) {
			return "libsqlitejdbc.jnilib";
		} else {
			return "libsqlitejdbc.so";
		}
	}

	public static Updater getInstance() {
		if (instance == null) {
			instance = new Updater();
		}

		return instance;
	}
}
