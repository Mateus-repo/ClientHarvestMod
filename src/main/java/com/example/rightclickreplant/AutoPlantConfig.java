package com.example.rightclickreplant;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import net.fabricmc.loader.api.FabricLoader;

public class AutoPlantConfig {
	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
	private static final File CONFIG_FILE = new File(FabricLoader.getInstance().getConfigDir().toFile(), "rightclick_replant.json");

	private static AutoPlantConfig INSTANCE;

	public boolean enabled = true;
	public boolean replantAutomatically = true;
	public boolean onlyWithEmptyHand = false;

	public static AutoPlantConfig get() {
		if (INSTANCE == null) {
			INSTANCE = load();
		}
		return INSTANCE;
	}

	public static AutoPlantConfig load() {
		if (CONFIG_FILE.exists()) {
			try (FileReader reader = new FileReader(CONFIG_FILE)) {
				AutoPlantConfig config = GSON.fromJson(reader, AutoPlantConfig.class);
				if (config == null) {
					config = new AutoPlantConfig();
				}
				config.save();
				return config;
			} catch (IOException e) {
				// Fall through to defaults
			}
		}
		AutoPlantConfig config = new AutoPlantConfig();
		config.save();
		return config;
	}

	public void save() {
		try {
			CONFIG_FILE.getParentFile().mkdirs();
			try (FileWriter writer = new FileWriter(CONFIG_FILE)) {
				GSON.toJson(this, writer);
			}
		} catch (IOException e) {
			// Ignore write errors
		}
	}
}
