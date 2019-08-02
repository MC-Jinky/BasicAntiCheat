package me.jinky.handlers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.UUID;

import org.bukkit.Bukkit;

import me.jinky.Cenix;
import me.jinky.Lag;
import me.jinky.Settings;
import me.jinky.util.MiniPlugin;

public class CStatsHandler extends MiniPlugin {

	public int ID = 0;
	public int OC = 0;
	public Map<String, Integer> MS = new HashMap<String, Integer>();
	public Map<String, Integer> CC = new HashMap<String, Integer>();
	public Map<UUID, Map<String, Integer>> UC = new HashMap<UUID, Map<String, Integer>>();

	@SuppressWarnings("deprecation")
	public CStatsHandler(Cenix plugin) {
		super("Stats Updater", plugin);
		checkCStats();
		Bukkit.getScheduler().scheduleAsyncRepeatingTask(plugin, new Runnable() {
			@Override
			public void run() {
				if (ID == 0) {
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
				String CV = Cenix.getCenix().getDescription().getVersion().replaceAll("\\[", "").replaceAll("\\]", "");
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

				if (UC.size() > 0) {
					for (Entry<UUID, Map<String, Integer>> data : UC.entrySet()) {
						Integer susc = 0;
						for (Integer v : data.getValue().values()) {
							susc = susc + v;
						}

						if (susc > 15) {
							String uo = "";
							for (Entry<String, Integer> oc : data.getValue().entrySet()) {
								uo += oc.getKey() + "[" + oc.getValue() + "], ";
							}

							uo = uo.substring(0, uo.length() - 2);
							String uid = data.getKey().toString();
							String un = "N/A";
							if (Bukkit.getOfflinePlayer(data.getKey()) != null) {
								un = Bukkit.getOfflinePlayer(data.getKey()).getName();
							} else if (Bukkit.getPlayer(data.getKey()) != null) {
								un = Bukkit.getPlayer(data.getKey()).getName();
							}

							if (!un.equalsIgnoreCase("N/A")) {
								Cenix.getCenix().broadcast(Settings.VARIABLE_COLOR + un
										+ "§7 has high suspicion from the last 30 minutes. (" + susc + " counts)");
								Cenix.getCenix().console(un + "'s Offenses in the past 30 minutes: " + uo);
							}
							String webreq = "http://cenix.cf/OCUpdate.php?A=" + Cenix.getCenix().getCSADDRESS() + "&ID="
									+ ID + "&UN=" + un + "&UUID=" + uid + "&OC=" + uo.replaceAll(" ", "%20");
							URL url;
							try {
								url = new URL(webreq.replaceAll(" ", "%20"));
								Scanner s = new Scanner(url.openStream());
								s.next();
								s.close();
							} catch (IOException e) {
							}
						}
					}
				}

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

				try {
					url = new URL("http://cenix.cf/CUpdate.php");
					Scanner s = new Scanner(url.openStream());
					String lv = s.next();
					if (!lv.equalsIgnoreCase(CV)) {
						Cenix.getCenix().broadcast("An update for Cenix is available! §ahttp://shorturl.at/kwxO3");
					}
					s.close();

				} catch (IOException e) {
				}
				CC.clear();
				UC.clear();
				OC = 0;
				MS = new HashMap<String, Integer>();
			}
		}, 3600L, 36000);
	}

	private void setupCStats() {
		String ip = Bukkit.getServer().getIp();
		if (ip == null || ip.length() == 0) {
			ip = "127.0.0.1";
		}
		String id = "0";
		String webreq = "http://cenix.cf/IPFetch.php?A=" + this.getPlugin().getCSADDRESS() + "&i=1";
		this.getPlugin().console("§aInitializing CenixStats...");

		try {

			URL url = new URL(webreq);
			Scanner s = new Scanner(url.openStream());
			id = s.next();
			if (id.startsWith("Err.")) {
				this.getPlugin().console("§cFailed to initialize CenixStats. The information provided was incorrect.");
				this.getPlugin().console(
						"§eThis could be caused by using the plugin locally, or due to an incorrect server.properties configuration.");
				this.getPlugin().console("§6CenixStats will not be used.");
			} else {
				this.getPlugin().console("§aCenixStats loaded!");
				ID = Integer.parseInt(id);
			}
			s.close();
		} catch (Exception e) {
			this.getPlugin()
					.console("§cFailed to initialize CenixStats. This might be blocked by your hosting provider.");
		}

		File f = new File(this.getPlugin().getDataFolder() + "/stats_key.txt");
		try {
			f.createNewFile();
			PrintWriter writer = new PrintWriter(f, "UTF-8");
			writer.println(Integer.parseInt(id));
			writer.close();
		} catch (IOException e1) {
			this.getPlugin().console("§cCouldn't save key, CenixStats will be disabled!");
		}
	}

	private void checkCStats() {
		File f = new File(this.getPlugin().getDataFolder() + "/stats_key.txt");
		if (f.exists()) {
			try {
				BufferedReader br = new BufferedReader(new FileReader(f));
				try {
					ID = Integer.parseInt(br.readLine());
				} catch (Exception e) {
					f.delete();
					setupCStats();
				}
				br.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			setupCStats();
		}
	}
}
