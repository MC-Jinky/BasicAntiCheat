package me.jinky;

import java.io.File;
import java.io.IOException;

import org.bukkit.configuration.file.FileConfiguration;

import com.google.common.io.Files;

import net.md_5.bungee.api.ChatColor;

public class Settings {

	public static boolean ENABLED = true;
	public static boolean PUNISH = false;
	public static int PUNISH_OFFENSE_COUNT = 7;
	public static String PUNISH_COMMAND = "";

	public static boolean LOG_OFFENSES = true;
	public static boolean ABN = true;

	public static int OFFENSE_EXPIRE_TIME = 25;
	public static double TPS_LAG_THRESHOLD = 17.5;

	public static String PREFIX = "§8[§dCenix§8]§r";
	public static String VARIABLE_COLOR = "§a";
	public static String SUSPICION_ALERT = "[VARIABLE_COLOR] [DISPLAYNAME] §freceived suspicion for §6[SUSPICION]§f. ([COUNT])";

	public static String TIMEZONE = "America/New_York";

	public static boolean CANCEL_ON_OFFENSE = true;

	public static void loadConfig() {
		Cenix c = Cenix.getCenix();
		c.reloadConfig();
		c.saveDefaultConfig();
		FileConfiguration cf = c.getConfig();
		PREFIX = ChatColor.translateAlternateColorCodes('&', cf.getString("prefix"));
		c.console("§2Loading configuration...");
		try {
			ENABLED = Boolean.parseBoolean(cf.getString("enabled"));
		} catch (Exception e) {
			c.console("§cThere was a problem loading the configuration!");
			c.console("§c'enabled' is not a valid boolean! Defaulting to " + ENABLED + ".");
		}

		try {
			CANCEL_ON_OFFENSE = Boolean.parseBoolean(cf.getString("cancel-on-offense"));
		} catch (Exception e) {
			c.console("§cThere was a problem loading the configuration!");
			c.console("§cancel-on-offense' is not a valid boolean, or doesn't exist! Defaulting to " + ENABLED + ".");
		}
		try {
			PUNISH = Boolean.parseBoolean(cf.getString("punish"));
		} catch (Exception e) {
			c.console("§cThere was a problem loading the configuration!");
			c.console("§c'punish' is not a valid boolean! Defaulting to " + PUNISH + ".");
		}
		VARIABLE_COLOR = ChatColor.translateAlternateColorCodes('&', cf.getString("variable-color"));
		try {
			TPS_LAG_THRESHOLD = Double.parseDouble(cf.getString("tps-lag-threshold"));
		} catch (Exception e) {
			c.console("§cThere was a problem loading the configuration!");
			c.console("§c'tps-lag-threshold' is not a valid number! Defaulting to " + TPS_LAG_THRESHOLD + ".");
		}
		try {
			if (!cf.contains("action-bar-notifications")) {
				cf.set("action-bar-notifications", true);
			}
			ABN = cf.getBoolean("action-bar-notifications");
		} catch (Exception e) {
			c.console("§cThere was a problem loading the configuration!");
			c.console("§c'action-bar-notifications' is not a valid boolean! Defaulting to " + ABN + ".");
		}
		try {
			PUNISH_OFFENSE_COUNT = Integer.parseInt(cf.getString("punish-offense-count"));
		} catch (Exception e) {
			c.console("§cThere was a problem loading the configuration!");
			c.console("§c'punish-offense-count' is not a valid number! Defaulting to " + PUNISH_OFFENSE_COUNT + ".");
		}
		PUNISH_COMMAND = ChatColor.translateAlternateColorCodes('&', cf.getString("punish-command"));
		try {
			OFFENSE_EXPIRE_TIME = Integer.parseInt(cf.getString("offense-expire-time"));
		} catch (Exception e) {
			c.console("§cThere was a problem loading the configuration!");
			c.console("§c'offense-expire-time' is not a valid integer! Defaulting to " + OFFENSE_EXPIRE_TIME + ".");
		}
		SUSPICION_ALERT = ChatColor.translateAlternateColorCodes('&', cf.getString("suspicion-alert"));
		TIMEZONE = cf.getString("timezone");

		File offenses = new File(c.getDataFolder(), "offenses.txt");
		c.saveConfig();
		if (Settings.LOG_OFFENSES) {
			if (!offenses.exists()) {
				try {
					offenses.createNewFile();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		} else if (offenses.exists()) {
			File oldoffenses = new File(c.getDataFolder(), "offenses.txt.old");
			if (oldoffenses.exists()) {
				oldoffenses.delete();
			}
			try {
				Files.copy(offenses, oldoffenses);
			} catch (IOException e) {
				c.console("§cThere was a problem with moving the old 'offenses.txt' file.");
			}
			offenses.delete();
		}

		c.console("§aConfiguration loaded!");
	}
}
