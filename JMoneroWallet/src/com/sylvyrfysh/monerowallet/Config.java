package com.sylvyrfysh.monerowallet;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONObject;

public class Config {

	private static final Logger logger = LogManager.getLogger();

	private static final String CONFIG_FILE_NAME = "jmwallet.config";
	private static final Path CONFIG_PATH;
	private static JSONObject config;
	static {
		File directory = new File(System.getProperty("user.home"), "jmonerowallet");
		if(!directory.exists()) {
			if(!directory.mkdir())
				throw new RuntimeException(String.format("Could not create config directory %s",directory.getAbsolutePath()));
		}
		File actualFile = new File(directory, CONFIG_FILE_NAME);
		CONFIG_PATH = Paths.get(actualFile.toURI());
		logger.info("Using file {} as config", CONFIG_PATH.toString());
		if (actualFile.exists()) {
			loadConfig();
		} else {
			try {
				if(!actualFile.createNewFile())
					throw new RuntimeException(String.format("Could not create config file %s",CONFIG_PATH.toString()));
			} catch (IOException e) {
				throw new RuntimeException(e);
			}
			config = new JSONObject();
			config.put("config-version", 1);
			writeConfig();
		}
	}

	public static JSONObject getJSON() {
		return config;
	}

	public static void writeConfig() {
		try {
			Files.write(CONFIG_PATH, config.toString().getBytes(), new OpenOption[] {});
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private static void loadConfig() {
		try {
			byte[] file = Files.readAllBytes(CONFIG_PATH);
			config = new JSONObject(new String(file));
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}
