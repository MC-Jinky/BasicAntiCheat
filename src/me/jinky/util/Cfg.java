package me.jinky.util;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import me.jinky.BAC;

public class Cfg {

	private File file;
	private FileConfiguration config;

	public Cfg(String key, BAC plugin) {
		file = new File(plugin.getDataFolder() + File.separator + key);
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		config = YamlConfiguration.loadConfiguration(file);
	}

	public void set(String path, Object value) {
		config.set(path, value);
	}

	public boolean contains(String path) {
		return config.contains(path);
	}

	public String getString(String path) {
		return config.getString(path);
	}

	public Integer getInteger(String path) {
		return config.getInt(path);
	}

	public List<String> getStringList(String path) {
		return config.getStringList(path);
	}

	public ConfigurationSection getConfigurationSection(String path) {
		return config.getConfigurationSection(path);
	}

	public double getDouble(String path) {
		return config.getDouble(path);
	}

	public void save() {
		try {
			config.save(file);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
