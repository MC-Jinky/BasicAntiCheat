package me.jinky.handlers;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.UUID;

import org.bukkit.Bukkit;

import me.jinky.BAC;
import me.jinky.Lag;
import me.jinky.Settings;
import me.jinky.util.MiniPlugin;

public class CStatsHandler extends MiniPlugin {

	public static String ID = null;
	public static int OC = 0;
	public static Map<String, Integer> MS = new HashMap<String, Integer>();
	public static Map<String, Integer> CC = new HashMap<String, Integer>();
	public static Map<UUID, Map<String, Integer>> UC = new HashMap<UUID, Map<String, Integer>>();

	@SuppressWarnings("deprecation")
	public CStatsHandler(BAC plugin) {
		super("Stats Updater", plugin);
		setupCStats();
		Bukkit.getScheduler().scheduleAsyncRepeatingTask(plugin, new Runnable() {
			@Override
			public void run() {
				if (!Settings.ENABLED) {
					return;
				}
				if (ID == null) {
					return;
				}
				String ts = "N/A";
				Integer tsc = 0;
				for (Entry<String, Integer> cs : MS.entrySet()) {
					if (cs.getValue() > tsc) {
						ts = cs.getKey();
						tsc = cs.getValue();
					}
				}
				Double TPS = Lag.getNiceTPS();
				String SV = Bukkit.getServer().getVersion();
				String CV = BAC.getBAC().getDescription().getVersion().replaceAll("\\[", "").replaceAll("\\]", "");
				Integer PC = Bukkit.getOnlinePlayers().size();
				String MSP = ts;
				String detectorcalls = "N/A";
				for (Entry<String, Integer> cf : CC.entrySet()) {
					detectorcalls = cf.getKey() + "[" + cf.getValue() + "], " + detectorcalls;
				}

				if (detectorcalls.length() > 0)
					detectorcalls = detectorcalls.substring(0, detectorcalls.length() - 2);

				if (CC.size() == 0)
					detectorcalls = "N/A";

				String webreq = "http://cenix.cf/StatUpdate.php?A=" + plugin.getCSADDRESS() + "&ID=" + ID + "&TPS="
						+ TPS + "&SV=" + SV + "&CV=" + CV + "&OC=" + OC + "&PC=" + PC + "&MS=" + MSP + "&TC="
						+ detectorcalls;
				URL url;
				try {
					url = new URL(webreq.replaceAll(" ", "%20"));
					Scanner s = new Scanner(url.openStream());
					s.next();
					s.close();
				} catch (IOException e) {
				}

				if (Settings.UPDATECHECK) {
					try {
						url = new URL("http://cenix.cf/CUpdate.php");
						Scanner s = new Scanner(url.openStream());
						String lv = s.next();
						if (!lv.equalsIgnoreCase(CV)) {
							BAC.getBAC().broadcast(null, Settings.UPDATE_AVAILABLE);
						}
						s.close();

					} catch (IOException e) {
					}
				}
				CC.clear();
				UC.clear();
				OC = 0;
				MS = new HashMap<String, Integer>();
			}
		}, 36000, 36000);
	}

	public static String Punish(UUID uuid) {
		Map<String, Integer> pdata = UC.get(uuid);
		Integer susc = 0;
		for (Integer v : pdata.values()) {
			susc += v;
		}

		String uo = "";
		Boolean punishsusc = false;
		for (Entry<String, Integer> oc : pdata.entrySet()) {
			uo += oc.getKey() + "[" + oc.getValue() + "], ";
			if (BAC.getBAC().getConfig().contains(oc.getKey() + "-punish-count")) {
				Integer cc = oc.getValue();
				Integer limit = BAC.getBAC().getConfig().getInt(oc.getKey() + "-punish-count");
				if (limit <= cc) {
					punishsusc = true;
				}
			}
		}

		if (punishsusc && Settings.PUNISH) {
			uo = uo.substring(0, uo.length() - 2);
			String uid = uuid.toString();
			String un = "N/A";
			if (Bukkit.getOfflinePlayer(uuid) != null) {
				un = Bukkit.getOfflinePlayer(uuid).getName();
			} else if (Bukkit.getPlayer(uuid) != null) {
				un = Bukkit.getPlayer(uuid).getName();
			}
			String webreq = "http://cenix.cf/OCUpdate.php?A=" + BAC.getBAC().getCSADDRESS() + "&ID=" + ID + "&UN=" + un
					+ "&UUID=" + uid + "&OD=" + uo.replaceAll(" ", "%20");
			URL url;
			try {
				url = new URL(webreq.replaceAll(" ", "%20"));
				Scanner s = new Scanner(url.openStream());
				String ret = s.nextLine();
				s.close();
				UC.remove(uuid);
				return ret;
			} catch (IOException e) {
				UC.remove(uuid);
				e.printStackTrace();
				return ("Error SCR");
			}

		} else {
			UC.remove(uuid);
			return "Error NPA";
		}
	}

	private void setupCStats() {
		Bukkit.getScheduler().runTaskAsynchronously(this.getPlugin(), () -> {
			File oldstats = new File(this.getPlugin().getDataFolder() + "/statistics.key");
			if (oldstats.exists()) {
				oldstats.delete();
			}
			String ip = Bukkit.getServer().getIp();
			if (ip == null || ip.length() == 0) {
				ip = "127.0.0.1";
			}
			String id = "0";
			String webreq = "http://cenix.cf/IPFetch.php?A=" + this.getPlugin().getCSADDRESS() + "&i=1";
			this.getPlugin().console("§aInitializing Cenix Stats...");

			try {

				URL url = new URL(webreq);
				Scanner s = new Scanner(url.openStream());
				id = s.next();
				if (id.startsWith("Err.")) {
					this.getPlugin()
							.console("§cFailed to initialize BACStats. The information provided was incorrect.");
					this.getPlugin().console(
							"§eThis could be caused by using the plugin locally, or due to an incorrect server.properties configuration.");
					this.getPlugin().console("§6BACStats will not be used.");
				} else {
					this.getPlugin().console("§aBACStats loaded!");
					ID = id;
				}
				s.close();
			} catch (Exception e) {
				this.getPlugin()
						.console("§cFailed to initialize BACStats. This might be blocked by your hosting provider.");
			}

			File f = new File(this.getPlugin().getDataFolder() + "/statistics.key");
			try {
				if (!f.exists()) {
					f.createNewFile();
				}
				PrintWriter writer = new PrintWriter(f, "UTF-8");
				writer.println(id);
				writer.close();
			} catch (IOException e1) {
				this.getPlugin().console("§cCouldn't save key, BACStats will be disabled!");
			}
		});
	}
}
