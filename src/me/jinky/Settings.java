package me.jinky;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.configuration.file.FileConfiguration;

import me.jinky.util.Cfg;
import net.md_5.bungee.api.ChatColor;

public class Settings {

	public static boolean ENABLED = true;
	public static boolean PUNISH = false;
	public static List<String> PUNISH_COMMAND = new ArrayList<String>();

	public static boolean UPDATECHECK = true;
	public static boolean ABN = true;

	public static int OFFENSE_EXPIRE_TIME = 25;
	public static double TPS_LAG_THRESHOLD = 17.5;

	public static String PREFIX = "§8[§aBAC§8]§r";
	public static String VARIABLE_COLOR = "§a";
	public static String SUSPICION_ALERT = "[VARIABLE_COLOR] [DISPLAYNAME] §freceived suspicion for §6[SUSPICION]§f. ([COUNT])";
	public static String UPDATE_AVAILABLE = "§aAn update is available for Basic Anti Cheat! Check the SpigotMC page!";
	public static List<String> DISABLED_CHECKS;
	public static List<String> NO_PUNISH_CHECKS;

	public static boolean CANCEL_ON_OFFENSE = true;

	public static Map<String, Integer> ALL_CHECKS = new HashMap<String, Integer>();

	public static void loadConfig() {
		BAC c = BAC.getBAC();
		c.reloadConfig();
		c.saveDefaultConfig();

		FileConfiguration cf = c.getConfig();
		NO_PUNISH_CHECKS = new ArrayList<String>();
		DISABLED_CHECKS = new ArrayList<String>();
		c.console("§2Loading configuration...");
		try {
			DISABLED_CHECKS = cf.getStringList("disabled-checks");
		} catch (Exception e) {
			c.console("§eNo checks listed for 'disabled-checks', all checks will be processed.");
		}

		try {
			NO_PUNISH_CHECKS = cf.getStringList("no-punish-cmd");
		} catch (Exception e) {
			c.console("§eNo punishments declared for 'no-punish-cmd', all checks will be punishable.");
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
		try {
			UPDATECHECK = cf.getBoolean("check-update");
		} catch (Exception e) {
			c.console("§cThere was a problem loading the configuration!");
			c.console("§c'check-update' is not a valid boolean! Defaulting to " + UPDATECHECK + ".");
		}
		try {
			TPS_LAG_THRESHOLD = Double.parseDouble(cf.getString("tps-lag-threshold"));
		} catch (Exception e) {
			c.console("§cThere was a problem loading the configuration!");
			c.console("§c'tps-lag-threshold' is not a valid number! Defaulting to " + TPS_LAG_THRESHOLD + ".");
		}
		try {
			ABN = cf.getBoolean("action-bar-notifications");
		} catch (Exception e) {
			c.console("§cThere was a problem loading the configuration!");
			c.console("§c'action-bar-notifications' is not a valid boolean! Defaulting to " + ABN + ".");
		}
		PUNISH_COMMAND.clear();
		if (!cf.isString("punish-command")) {
			for (String s : cf.getStringList("punish-command")) {
				PUNISH_COMMAND.add(ChatColor.translateAlternateColorCodes('&', s));
			}
		} else {
			PUNISH_COMMAND.add(ChatColor.translateAlternateColorCodes('&', cf.getString("punish-command")));
		}
		try {
			OFFENSE_EXPIRE_TIME = Integer.parseInt(cf.getString("offense-expire-time"));
		} catch (Exception e) {
			c.console("§cThere was a problem loading the configuration!");
			c.console("§c'offense-expire-time' is not a valid integer! Defaulting to " + OFFENSE_EXPIRE_TIME + ".");
		}
		List<String> checks = new ArrayList<String>();
		checks.add("Flight");
		checks.add("Speed");
		checks.add("WaterWalk");
		checks.add("Glide/SlowFall");
		checks.add("Spider");
		checks.add("FastClimb");
		checks.add("Boat Fly");
		checks.add("Kill Aura");
		checks.add("Multi Aura");
		checks.add("Reach");
		checks.add("Impossible Break");
		checks.add("Impossible Place");
		checks.add("Fast Place");
		checks.add("Fast Break");
		checks.add("XRay");
		checks.add("Anti-Cactus");
		checks.add("Anti-BerryBush");
		checks.add("MorePackets (Timer)");
		checks.add("MorePackets (Nuker)");
		checks.add("Criticals");
		checks.add("Step");
		for (String s : checks) {
			if (!cf.contains(s + "-punish-count")) {
				ALL_CHECKS.put(s, 50000);
				c.console("§cCouldn't find a valid number at '" + s
						+ "-punish-count' in the configuration! Punishments will be disabled for this check.");
			} else {
				try {
					ALL_CHECKS.put(s, cf.getInt(s + "-punish-count"));
				} catch (Exception e) {
					ALL_CHECKS.put(s, 50000);
					c.console("§cCouldn't find a valid number at '" + s
							+ "-punish-count' in the configuration! Punishments will be disabled for this check.");
				}
			}
		}

		Cfg msgs = new Cfg("messages.properties", c);
		if (!msgs.contains("prefix")) {
			msgs.set("prefix", PREFIX.replaceAll("§", "&"));
		} else {
			PREFIX = ChatColor.translateAlternateColorCodes('&', msgs.getString("prefix"));
		}
		if (!msgs.contains("variable-color")) {
			msgs.set("variable-color", "&a");
		} else {
			VARIABLE_COLOR = ChatColor.translateAlternateColorCodes('&', msgs.getString("variable-color"));
		}
		if (!msgs.contains("suspicion-alert")) {
			msgs.set("suspicion-alert", SUSPICION_ALERT);
		} else {
			SUSPICION_ALERT = ChatColor.translateAlternateColorCodes('&', msgs.getString("suspicion-alert"));
		}
		if (!msgs.contains("update-available")) {
			msgs.set("update-available", UPDATE_AVAILABLE);
		} else {
			UPDATE_AVAILABLE = ChatColor.translateAlternateColorCodes('&', msgs.getString("update-available"));
		}
		msgs.save();
		c.console("§aConfiguration loaded!");
	}
}
