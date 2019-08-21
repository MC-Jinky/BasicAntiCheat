package me.jinky.command;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import me.jinky.BAC;
import me.jinky.Lag;
import me.jinky.Settings;
import me.jinky.fwk.Command;
import me.jinky.fwk.CommandArgs;
import me.jinky.handlers.CStatsHandler;
import me.jinky.util.UtilMath;
import me.jinky.util.UtilTime;

public class BACCmd {

	@Command(name = "bac")
	public void onCmd(CommandArgs a) {
		Player p = a.getPlayer();
		String[] args = a.getArgs();
		BAC bac = BAC.getBAC();
		if (args.length == 0) {
			bac.sendMessage(p, "§rRunning BAC version " + Settings.VARIABLE_COLOR
					+ bac.getDescription().getVersion().replaceAll("\\[", "").replaceAll("\\]", "") + "§f by "
					+ Settings.VARIABLE_COLOR
					+ bac.getDescription().getAuthors().get(0).replaceAll("\\[", "").replaceAll("\\]", "") + "§f.",
					false);
			if (p.hasPermission("bac.admin")) {
				bac.sendMessage(p, "§fUse " + Settings.VARIABLE_COLOR + "/BAC help§f for a list of available commands.",
						false);
			}
			return;
		}
		if (args[0].equalsIgnoreCase("tac") && p.hasPermission("bac.tac")) {
			if (Settings.ENABLED == true) {
				Settings.ENABLED = false;
				bac.sendMessage(p,
						"§cBasicAntiCheat functionality has been disabled! §cChanging this disables it for the session only, restarting the server will re-enable BAC!",
						false);
			} else {
				Settings.ENABLED = true;
				bac.sendMessage(p,
						"§aBasicAntiCheat functionality has been enabled! §cChanging this disables it for the session only, restarting the server will re-enable BAC!",
						false);
			}
			return;
		}
		if (args[0].equalsIgnoreCase("help")) {
			boolean hasinfo = false;
			boolean hasnotify = false;
			boolean hasexempt = false;
			boolean hasvr = false;
			boolean hasrlcfg = false;
			boolean hastac = false;
			if (p.hasPermission("bac.tac")) {
				hastac = true;
			}
			if (p.hasPermission("bac.reload")) {
				hasrlcfg = true;
			}
			if (p.hasPermission("bac.info")) {
				hasinfo = true;
			}
			if (p.hasPermission("bac.verbose")) {
				hasnotify = true;
			}
			if (p.hasPermission("bac.exempt")) {
				hasexempt = true;
			}
			if (hasnotify || hasexempt || hasinfo || hasvr || hasrlcfg)
				bac.sendMessage(p, "List of Available Commands: ", false);
			if (hasinfo)
				bac.sendMessage(p, Settings.VARIABLE_COLOR + "/BAC info §7-§r Shows System / Server Info", false);
			if (hasnotify)
				bac.sendMessage(p, Settings.VARIABLE_COLOR + "/BAC verbose §7-§r Toggles Verbose on and off.", false);
			if (hasexempt)
				bac.sendMessage(p, Settings.VARIABLE_COLOR
						+ "/BAC exempt <player> <time> §7-§r Exempts a player for a specified time.", false);
			if (hasvr)
				bac.sendMessage(p,
						Settings.VARIABLE_COLOR + "/BAC viewreport <id> §7-§r Views a report on a player kick.", false);
			if (hasrlcfg)
				bac.sendMessage(p, Settings.VARIABLE_COLOR + "/BAC reload §7-§r Reloads the configuration file.",
						false);
			if (hastac)
				bac.sendMessage(p, Settings.VARIABLE_COLOR + "/BAC tac - §cThis will disable/enable the anticheat.",
						false);

			return;
		}
		if (args[0].equalsIgnoreCase("statskey") && p.hasPermission("bac.statskey")) {
			bac.sendMessage(p, "Statistics Key: " + CStatsHandler.ID, false);
			return;
		}
		if (args[0].equalsIgnoreCase("info") && p.hasPermission("bac.info")) {
			double tps = UtilMath.trim(2, Lag.getTPS());
			String tps_real = "§c" + tps;
			if (tps >= 19) {
				tps_real = Settings.VARIABLE_COLOR + "" + tps;
			} else if (tps >= 18) {
				tps_real = "§e" + tps;
			}
			bac.sendMessage(p, "Current TPS: " + tps_real, false);
			bac.sendMessage(p, "Maximum Memory: " + Settings.VARIABLE_COLOR
					+ (Runtime.getRuntime().maxMemory() / 1024 / 1024) + "MB", false);
			bac.sendMessage(p, "Free Memory: " + Settings.VARIABLE_COLOR
					+ (Runtime.getRuntime().freeMemory() / 1024 / 1024) + "MB", false);
			bac.sendMessage(p, "Available Cores: §6" + Runtime.getRuntime().availableProcessors(), false);
			bac.sendMessage(p, "Operating System: §6" + System.getProperty("os.name") + " ("
					+ System.getProperty("os.version") + ")", false);
			bac.sendMessage(p, "System Architecture: §6" + System.getProperty("os.arch"), false);
			String bukkitVersion = org.bukkit.Bukkit.getVersion();
			bukkitVersion = bukkitVersion.substring(bukkitVersion.indexOf("MC: ") + 4, bukkitVersion.length() - 1);
			bac.sendMessage(p, "Server Version: §e" + bukkitVersion, false);
			bac.sendMessage(p, "Java Runtime Version: §e" + System.getProperty("java.runtime.version"), false);
			return;
		}
		if (args[0].equalsIgnoreCase("verbose") && p.hasPermission("bac.verbose")) {
			if (bac.nonotify.contains(p)) {
				bac.nonotify.remove(p);
				bac.sendMessage(p, "Verbose is now turned " + Settings.VARIABLE_COLOR + "on§r.", false);
			} else {
				bac.nonotify.add(p);
				bac.sendMessage(p, "Verbose is now turned §4off§r.", false);
			}
			return;
		}
		if (args[0].equalsIgnoreCase("exempt") && p.hasPermission("bac.exempt")) {
			if (args.length != 3) {
				bac.sendMessage(p, "Usage: " + Settings.VARIABLE_COLOR + "/BAC exempt <player> <time>", false);
				bac.sendMessage(p, "§7Time Example: " + Settings.VARIABLE_COLOR + "1h30m", false);
				return;
			}
			Player t = Bukkit.getPlayer(args[1]);
			if (t == null || !t.isOnline()) {
				bac.sendMessage(p, "That player is not online!", false);
				return;
			}
			long expire = UtilTime.parseDateDiff(args[2], true);
			if (expire == 0) {
				bac.sendMessage(p, "That's not a valid time! Example: " + Settings.VARIABLE_COLOR + "1d5h3m", false);
				return;
			}
			BAC.getBAC().EXEMPTHANDLER.addExemption(p, (int) (expire - System.currentTimeMillis()),
					"command exempt req");
			bac.broadcast(null, Settings.VARIABLE_COLOR + "" + t.getDisplayName() + " §rwas added to the exempt list.",
					"This will expire in " + Settings.VARIABLE_COLOR
							+ UtilTime.MakeStr((expire - System.currentTimeMillis()), 2) + "§r.");
			return;
		}
		if (args[0].equalsIgnoreCase("reload") && p.hasPermission("bac.reload")) {
			Settings.loadConfig();
			bac.sendMessage(p, "Configuration reloaded.", false);
			return;
		}
		bac.sendMessage(p,
				"Running BAC version " + Settings.VARIABLE_COLOR
						+ bac.getDescription().getVersion().replaceAll("\\[", "").replaceAll("\\]", "") + "§f by "
						+ Settings.VARIABLE_COLOR
						+ bac.getDescription().getAuthors().get(0).replaceAll("\\[", "").replaceAll("\\]", "") + "§f.",
				false);
		if (p.hasPermission("bac.admin")) {
			bac.sendMessage(p, "Use " + Settings.VARIABLE_COLOR + "/BAC help§r for a list of available commands.",
					false);
		}
	}
}
