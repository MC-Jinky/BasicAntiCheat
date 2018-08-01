package me.jinky.command;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import me.jinky.Cenix;
import me.jinky.Lag;
import me.jinky.Settings;
import me.jinky.fwk.Command;
import me.jinky.fwk.CommandArgs;
import me.jinky.util.UtilMath;
import me.jinky.util.UtilTime;

public class CenixCmd {

	@Command(name = "cenix")
	public void onCmd(CommandArgs a) {
		Player p = a.getPlayer();
		String[] args = a.getArgs();
		Cenix cenix = Cenix.getCenix();
		if (args.length == 0) {
			cenix.sendMessage(p, "Running Cenix version " + Settings.VARIABLE_COLOR
					+ cenix.getDescription().getVersion().replaceAll("\\[", "").replaceAll("\\]", "") + "§f by "
					+ Settings.VARIABLE_COLOR
					+ cenix.getDescription().getAuthors().get(0).replaceAll("\\[", "").replaceAll("\\]", "") + "§f.");
			if (p.hasPermission("cenix.admin")) {
				cenix.sendMessage(p,
						"Use " + Settings.VARIABLE_COLOR + "/cenix help§r for a list of available commands.");
			}
			return;
		}
		if (args[0].equalsIgnoreCase("help")) {
			boolean hasinfo = false;
			boolean hasnotify = false;
			boolean hasexempt = false;
			boolean hasvr = false;
			boolean hasrlcfg = false;
			if (p.hasPermission("cenix.reload")) {
				hasrlcfg = true;
			}
			if (p.hasPermission("cenix.info")) {
				hasinfo = true;
			}
			if (p.hasPermission("cenix.notify")) {
				hasnotify = true;
			}
			if (p.hasPermission("cenix.exempt")) {
				hasexempt = true;
			}
			if (p.hasPermission("cenix.viewreport")) {
				hasvr = true;
			}
			if (hasnotify || hasexempt || hasinfo || hasvr || hasrlcfg)
				cenix.sendMessage(p, "List of Available Commands: ");
			if (hasinfo)
				cenix.sendMessage(p, Settings.VARIABLE_COLOR + "/cenix info §7-§r Shows System / Server Info");
			if (hasnotify)
				cenix.sendMessage(p, Settings.VARIABLE_COLOR + "/cenix notify §7-§r Toggles notifications on and off.");
			if (hasexempt)
				cenix.sendMessage(p, Settings.VARIABLE_COLOR
						+ "/cenix exempt <player> <time> §7-§r Exempts a player for a specified time.");
			if (hasvr)
				cenix.sendMessage(p,
						Settings.VARIABLE_COLOR + "/cenix viewreport <id> §7-§r Views a report on a player kick.");
			if (hasrlcfg)
				cenix.sendMessage(p, Settings.VARIABLE_COLOR + "/cenix reload §7-§r Reloads the configuration file.");
			return;
		}
		if (args[0].equalsIgnoreCase("info") && p.hasPermission("cenix.info")) {
			double tps = UtilMath.trim(2, Lag.getTPS());
			String tps_real = "§c" + tps;
			if (tps >= 19) {
				tps_real = Settings.VARIABLE_COLOR + "" + tps;
			} else if (tps >= 18) {
				tps_real = "§e" + tps;
			}
			cenix.sendMessage(p, "Current TPS: " + tps_real);
			cenix.sendMessage(p, "Maximum Memory: " + Settings.VARIABLE_COLOR
					+ (Runtime.getRuntime().maxMemory() / 1024 / 1024) + "MB");
			cenix.sendMessage(p, "Free Memory: " + Settings.VARIABLE_COLOR
					+ (Runtime.getRuntime().freeMemory() / 1024 / 1024) + "MB");
			cenix.sendMessage(p, "Available Cores: §6" + Runtime.getRuntime().availableProcessors());
			cenix.sendMessage(p, "Operating System: §6" + System.getProperty("os.name") + " ("
					+ System.getProperty("os.version") + ")");
			cenix.sendMessage(p, "System Architecture: §6" + System.getProperty("os.arch"));
			String bukkitVersion = org.bukkit.Bukkit.getVersion();
			bukkitVersion = bukkitVersion.substring(bukkitVersion.indexOf("MC: ") + 4, bukkitVersion.length() - 1);
			cenix.sendMessage(p, "Server Version: §e" + bukkitVersion);
			cenix.sendMessage(p, "Java Runtime Version: §e" + System.getProperty("java.runtime.version"));
			return;
		}
		if (args[0].equalsIgnoreCase("notify") && p.hasPermission("cenix.notify")) {
			if (cenix.nonotify.contains(p)) {
				cenix.nonotify.remove(p);
				cenix.sendMessage(p, "Notifications are now turned " + Settings.VARIABLE_COLOR + "on§r.");
			} else {
				cenix.nonotify.add(p);
				cenix.sendMessage(p, "Notifications are now turned §4off§r.");
			}
			return;
		}
		if (args[0].equalsIgnoreCase("viewreport") && p.hasPermission("cenix.viewreport")) {
			if (args.length != 2) {
				cenix.sendMessage(p, "Usage: " + Settings.VARIABLE_COLOR + "/cenix viewreport <id>");
				return;
			}
			int rid = -1;
			try {
				rid = Integer.parseInt(args[1]);
			} catch (Exception e) {
			}
			if (rid == -1) {
				cenix.sendMessage(p, "That's not a valid report ID.");
				return;
			}
			if (!cenix.getConfig().contains("Reports." + rid)) {
				cenix.sendMessage(p, "That's not a valid report ID.");
				return;
			}
			List<String> rdata = cenix.getConfig().getStringList("Reports." + rid);
			for (String s : rdata) {
				cenix.sendMessage(p, s.replaceAll("[VC]", Settings.VARIABLE_COLOR));
			}
			return;
		}
		if (args[0].equalsIgnoreCase("exempt") && p.hasPermission("cenix.exempt")) {
			if (args.length != 3) {
				cenix.sendMessage(p, "Usage: " + Settings.VARIABLE_COLOR + "/cenix exempt <player> <time>");
				cenix.sendMessage(p, "§7Time Example: " + Settings.VARIABLE_COLOR + "1h30m");
				return;
			}
			Player t = Bukkit.getPlayer(args[1]);
			if (t == null || !t.isOnline()) {
				cenix.sendMessage(p, "That player is not online!");
				return;
			}
			long expire = UtilTime.parseDateDiff(args[2], true);
			if (expire == 0) {
				cenix.sendMessage(p, "That's not a valid time! Example: " + Settings.VARIABLE_COLOR + "1d5h3m");
				return;
			}
			cenix.addExemption(p, (expire - System.currentTimeMillis()));
			cenix.broadcast(Settings.VARIABLE_COLOR + "" + t.getDisplayName() + " §rwas added to the exempt list.",
					"This will expire in " + Settings.VARIABLE_COLOR
							+ UtilTime.MakeStr((expire - System.currentTimeMillis()), 2) + "§r.");
			return;
		}
		if (args[0].equalsIgnoreCase("reload") && p.hasPermission("cenix.reload")) {
			Settings.loadConfig();
			cenix.sendMessage(p, "Configuration reloaded.");
			return;
		}
		cenix.sendMessage(p, "Running Cenix version " + Settings.VARIABLE_COLOR
				+ cenix.getDescription().getVersion().replaceAll("\\[", "").replaceAll("\\]", "") + "§f by "
				+ Settings.VARIABLE_COLOR
				+ cenix.getDescription().getAuthors().get(0).replaceAll("\\[", "").replaceAll("\\]", "") + "§f.");
		if (p.hasPermission("cenix.admin")) {
			cenix.sendMessage(p, "Use " + Settings.VARIABLE_COLOR + "/cenix help§r for a list of available commands.");
		}
	}
}
