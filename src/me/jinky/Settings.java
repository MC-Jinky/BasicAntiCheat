package me.jinky;

import java.io.File;
import java.io.IOException;

import org.bukkit.configuration.file.FileConfiguration;

import com.google.common.io.Files;

import net.md_5.bungee.api.ChatColor;

public class Settings {

	public static boolean PUNISH = false;
	public static int PUNISH_OFFENSE_COUNT = 5;
	public static String PUNISH_COMMAND = "";

	public static boolean LOG_REPORTS = true;
	public static boolean LOG_OFFENSES = true;

	public static int OFFENSE_EXPIRE_TIME = 180;
	public static double TPS_LAG_THRESHOLD = 17.5;

	public static String PREFIX = "§8[§dCenix§8]§r";
	public static String VARIABLE_COLOR = "§a";
	public static String SUSPICION_ALERT = "[VARIABLE_COLOR] [DISPLAYNAME] §freceived suspicion for §6[SUSPICION]§f. ([COUNT])";
	public static String SUSPICION_ALERT_IGNORE_TPS = "[VARIABLE_COLOR] [DISPLAYNAME] §freceived suspicion for §6[SUSPICION]§f, but it's being ignored because of bad TPS ([TPS])";
	public static String REPORT_SAVED_ALERT = "§fReport for [VARIABLE_COLOR][DISPLAYNAME] §fsaved. ([VARIABLE_COLOR][REPORT_ID]§f)";

	public static String TIMEZONE = "America/New_York";

	public static void loadConfig() {
		Cenix c = Cenix.getCenix();
		c.reloadConfig();
		c.saveDefaultConfig();
		FileConfiguration cf = c.getConfig();
		PREFIX = ChatColor.translateAlternateColorCodes('&', cf.getString("prefix"));
		c.console("§2Loading configuration...");
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
			PUNISH_OFFENSE_COUNT = Integer.parseInt(cf.getString("punish-offense-count"));
		} catch (Exception e) {
			c.console("§cThere was a problem loading the configuration!");
			c.console("§c'punish-offense-count' is not a valid number! Defaulting to " + PUNISH_OFFENSE_COUNT + ".");
		}
		PUNISH_COMMAND = ChatColor.translateAlternateColorCodes('&', cf.getString("punish-command"));
		try {
			LOG_REPORTS = Boolean.parseBoolean(cf.getString("log-reports"));
		} catch (Exception e) {
			c.console("§cThere was a problem loading the configuration!");
			c.console("§c'log-reports' is not a valid boolean! Defaulting to " + LOG_REPORTS + ".");
		}
		try {
			LOG_OFFENSES = Boolean.parseBoolean(cf.getString("log-offenses"));
		} catch (Exception e) {
			c.console("§cThere was a problem loading the configuration!");
			c.console("§c'log-offenses' is not a valid boolean! Defaulting to " + LOG_OFFENSES + ".");
		}
		try {
			OFFENSE_EXPIRE_TIME = Integer.parseInt(cf.getString("offense-expire-time"));
		} catch (Exception e) {
			c.console("§cThere was a problem loading the configuration!");
			c.console("§c'offense-expire-time' is not a valid integer! Defaulting to " + OFFENSE_EXPIRE_TIME + ".");
		}
		SUSPICION_ALERT = ChatColor.translateAlternateColorCodes('&', cf.getString("suspicion-alert"));
		SUSPICION_ALERT_IGNORE_TPS = ChatColor.translateAlternateColorCodes('&',
				cf.getString("suspicion-alert-ignore-tps"));
		REPORT_SAVED_ALERT = ChatColor.translateAlternateColorCodes('&', cf.getString("report-saved-alert"));
		if (SUSPICION_ALERT_IGNORE_TPS.toUpperCase().contains("[COUNT]")) {
			c.console(
					"§eWarning: The 'suspicion-alert-ignore-tps' message has the [COUNT] variable defined, but the count isn't active in this message, it will be blank.");
		}
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
