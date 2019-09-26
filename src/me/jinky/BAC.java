package me.jinky;

import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.Vector;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import me.jinky.checks.Check;
import me.jinky.checks.combat.CriticalCheck;
import me.jinky.checks.combat.KillAuraCheck;
import me.jinky.checks.combat.MultiAuraCheck;
import me.jinky.checks.combat.ReachCheck;
import me.jinky.checks.movement.BoatCheck;
import me.jinky.checks.movement.EntitySpeedCheck;
import me.jinky.checks.movement.FlightFCheck;
import me.jinky.checks.movement.FloatCheck;
import me.jinky.checks.movement.HoverCheck;
import me.jinky.checks.movement.SmartFlightCheck;
import me.jinky.checks.movement.SmartSpeedCheck;
import me.jinky.checks.movement.SpeedCheck;
import me.jinky.checks.movement.WaterCheck;
import me.jinky.checks.world.AntiCactusBerryCheck;
import me.jinky.checks.world.BreakCheck;
import me.jinky.checks.world.PlaceCheck;
import me.jinky.checks.world.XRayCheck;
import me.jinky.command.BACCmd;
import me.jinky.fwk.CommandFramework;
import me.jinky.handlers.BlockHandler;
import me.jinky.handlers.CStatsHandler;
import me.jinky.handlers.DamageHandler;
import me.jinky.handlers.ExemptHandler;
import me.jinky.handlers.MovementHandler;
import me.jinky.logger.PlayerLogger;
import me.jinky.logger.User;
import me.jinky.util.UtilMath;
import net.md_5.bungee.api.chat.TextComponent;

public class BAC extends JavaPlugin implements Listener {

	private static Object antiLock = new Object();
	public static BAC core = null;
	public static CommandFramework _fw = null;

	public List<Player> nonotify = new ArrayList<Player>();
	private static Map<Player, HashMap<Long, String>> reports = new HashMap<Player, HashMap<Long, String>>();

	public List<Check> All_Checks = new ArrayList<Check>();

	public DamageHandler DAMAGEHANDLER = null;
	public ExemptHandler EXEMPTHANDLER = null;
	public CStatsHandler CSTATSHANDLER = null;

	@Override
	public void onEnable() {
		core = this;

		saveDefaultConfig();

		_fw = new CommandFramework(this);
		_fw.registerCommands(new BACCmd());

		DAMAGEHANDLER = new DamageHandler(this);
		EXEMPTHANDLER = new ExemptHandler(this);
		new MovementHandler(this);
		new BlockHandler(this);
		new PlayerLogger(this);

		Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(this, new Lag(), 100L, 1L);
		this.getServer().getPluginManager().registerEvents(this, this);
		Settings.loadConfig();
		BungeeHandler.load(this);

		Bukkit.getScheduler().runTaskLater(this, () -> {
			this.registerCheck(new SpeedCheck());
			this.registerCheck(new SmartFlightCheck());
			this.registerCheck(new SmartSpeedCheck());
			this.registerCheck(new BreakCheck());
			this.registerCheck(new PlaceCheck());
			this.registerCheck(new KillAuraCheck());
			this.registerCheck(new MultiAuraCheck());
			this.registerCheck(new BoatCheck());
			this.registerCheck(new WaterCheck());
			this.registerCheck(new HoverCheck());
			this.registerCheck(new FloatCheck());
			this.registerCheck(new ReachCheck());
			this.registerCheck(new EntitySpeedCheck());
			this.registerCheck(new XRayCheck());
			this.registerCheck(new AntiCactusBerryCheck());
			this.registerCheck(new CriticalCheck());
			this.registerCheck(new FlightFCheck());
			CSTATSHANDLER = new CStatsHandler(this);
		}, 100L);

	}

	public static String readableBytes(long bytes) {
		int unit = 1024;
		if (bytes < unit) {
			return bytes + " B";
		}

		int exp = (int) (Math.log(bytes) / Math.log(unit));
		String pre = "kMGTPE".charAt(exp - 1) + "i";
		return String.format("%.2f %sB", bytes / Math.pow(unit, exp), pre);
	}

	public String getUUID(String player) {
		URL url;
		for (Player p : Bukkit.getOnlinePlayers()) {
			if (player.equalsIgnoreCase(p.getName())) {
				return p.getUniqueId().toString();
			}
		}
		try {
			url = new URL("https://api.mojang.com/users/profiles/minecraft/" + player);
			String uuid = (String) ((JSONObject) new JSONParser().parse(new InputStreamReader(url.openStream())))
					.get("id");
			String realUUID = uuid.substring(0, 8) + "-" + uuid.substring(8, 12) + "-" + uuid.substring(12, 16) + "-"
					+ uuid.substring(16, 20) + "-" + uuid.substring(20, 32);
			return UUID.fromString(realUUID).toString();
		} catch (Exception e) {
			return null;
		}
	}

	public String getCSADDRESS() {
		String ip = Bukkit.getServer().getIp();
		if (ip == null || ip.length() == 0) {
			ip = "127.0.0.1";
		}
		return ip + ":" + Bukkit.getServer().getPort();
	}

	private void registerCheck(Check check) {
		if (!All_Checks.contains(check))
			All_Checks.add(check);
	}

	public void sendMessage(Player p, String msg, Boolean actionbar) {
		if (actionbar) {
			p.spigot().sendMessage(new TextComponent(Settings.PREFIX + " " + msg));
		} else {
			p.sendMessage(Settings.PREFIX + " " + msg);
		}
	}

	@Override
	public boolean onCommand(CommandSender sender, org.bukkit.command.Command command, String label, String[] args) {
		if (!(sender instanceof Player)) {
			return false;
		}
		return _fw.handleCommand(sender, label, command, args);
	}

	public static BAC getBAC() {
		return core;
	}

	public User getUser(Player p) {
		return new User(p);
	}

	public JavaPlugin getPlugin() {
		return this;
	}

	public void console(String msg) {
		Bukkit.getConsoleSender().sendMessage(Settings.PREFIX + " " + msg);
	}

	private boolean updateDatabase(Player p) {
		synchronized (antiLock) {
			List<Long> remove = new ArrayList<Long>();
			Iterator<Long> i = reports.get(p).keySet().iterator();
			while (i.hasNext()) {
				Long l = i.next();
				if (System.currentTimeMillis() > l) {
					if (!remove.contains(l)) {
						remove.add(l);
					}
				} else {

				}
			}
			for (Long r : remove) {
				if (reports.get(p).containsKey(r)) {
					reports.get(p).remove(r);
				}
			}

			Boolean punishsusc = false;
			Map<String, Integer> pdata = CStatsHandler.UC.get(p.getUniqueId());
			Integer susc = 0;
			String check = "";
			for (Entry<String, Integer> v : pdata.entrySet()) {
				susc += v.getValue();
				if (Settings.ALL_CHECKS.containsKey(v.getKey())) {
					Integer cc = v.getValue();
					Integer limit = Settings.ALL_CHECKS.get(v.getKey());
					if (limit <= cc) {
						check = v.getKey();
						punishsusc = true;
					}
				}
			}

			if (punishsusc && Settings.PUNISH && !Settings.NO_PUNISH_CHECKS.contains(check)) {
				BAC.getBAC().EXEMPTHANDLER.addExemption(p, 5000, "Punishment Applied");

				Bukkit.getScheduler().runTaskAsynchronously(this, () -> {
					String reportslist = "";
					Map<String, Integer> od = new HashMap<String, Integer>();
					for (String s : reports.get(p).values()) {
						Integer count = 1;
						if (od.containsKey(s)) {
							count = od.get(s) + 1;
							od.remove(s);
						}
						od.put(s, count);
					}
					for (Entry<String, Integer> e : od.entrySet()) {
						reportslist = reportslist + e.getKey() + "(" + e.getValue() + "), ";
					}
					reportslist = reportslist.substring(0, reportslist.length() - 2);
					p.setVelocity(new Vector(0, 0, 0));
					String pid = CStatsHandler.Punish(p.getUniqueId());
					for (String se : Settings.PUNISH_COMMAND) {
						String m = se;
						m = m.replaceAll("\\[VARIABLE_COLOR\\]", Settings.VARIABLE_COLOR);
						m = m.replaceAll("\\[DISPLAYNAME\\]", p.getDisplayName());
						m = m.replaceAll("\\[USERNAME\\]", p.getName());
						m = m.replaceAll("\\[NAME\\]", p.getName());
						m = m.replaceAll("\\[UUID\\]", p.getUniqueId().toString());
						m = m.replaceAll("\\[OFFENSES\\]", reportslist);
						m = m.replaceAll("\\[PID\\]", pid);
						final String me = m;
						Bukkit.getScheduler().runTask(BAC.getBAC(), () -> {
							if (!BungeeHandler.handleBungeePunish(me)) {
								Bukkit.getServer().dispatchCommand(getServer().getConsoleSender(), me);
							} else {
								console("Punishment request sent to Bungee.");
							}
						});
					}
				});
				return true;
			} else {
				return false;
			}
		}

	}

	public static int getPing(Player p) {
		int ping = 0;
		try {
			Object entityPlayer = p.getClass().getMethod("getHandle").invoke(p);
			ping = (int) entityPlayer.getClass().getField("ping").get(entityPlayer);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException
				| SecurityException | NoSuchFieldException e) {
			e.printStackTrace();
		}
		if (ping > 0) {
			ping = ping / 2;
		}
		return ping;
	}

	public boolean addSuspicion(Player p, String detector) {
		if (Settings.ENABLED == false) {
			return false;
		}
		if (Settings.DISABLED_CHECKS.contains(detector)) {
			return false;
		}

		if (!reports.containsKey(p))
			reports.put(p, new HashMap<Long, String>());

		if (EXEMPTHANDLER.isExempt(p)) {
			return false;
		}
		this.getUser(p).updateLastOffense();

		int ping = 0;

		try {
			Object entityPlayer = p.getClass().getMethod("getHandle").invoke(p);
			ping = (int) entityPlayer.getClass().getField("ping").get(entityPlayer);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (ping > 0)
			ping = ping / 2;

		Integer Count = 0;
		if (Settings.OFFENSE_EXPIRE_TIME == 0) {
			reports.get(p).put((System.currentTimeMillis() + (System.currentTimeMillis() * 10)), detector);
		} else {
			reports.get(p).put((System.currentTimeMillis() + (Settings.OFFENSE_EXPIRE_TIME * 1000)), detector);
		}
		for (String v : reports.get(p).values()) {
			if (v.equalsIgnoreCase(detector)) {
				Count++;
			}
		}
		if (Count <= 2) {
			return false;
		}
		if (Settings.CANCEL_ON_OFFENSE && p.getVehicle() == null) {
			EXEMPTHANDLER.addExemptionBlock(p, 100);
			if (detector.equalsIgnoreCase("Anti-Cactus") || detector.equalsIgnoreCase("Anti-BerryBush")) {
				p.damage(0.5D);
			} else if (detector.equalsIgnoreCase("WaterWalk")) {
				p.teleport(p.getLocation().add(0, -0.5, 0));
			} else if (detector.equalsIgnoreCase("Criticals") || detector.equalsIgnoreCase("XRay")) {
				// Do nothing, CriticalCheck.java cancels on its own if enabled.
				// Do nothing, XRayCheck.java, let it just continue normally, it's in heavy beta
			} else {
				p.teleport(this.getUser(p).LastRegularLocation());
			}
		}
		CStatsHandler.OC++;
		if (!CStatsHandler.CC.containsKey(detector)) {
			CStatsHandler.CC.put(detector, 1);
		} else {
			CStatsHandler.CC.put(detector, CStatsHandler.CC.get(detector) + 1);
		}

		if (Lag.getTPS() <= Settings.TPS_LAG_THRESHOLD || ping >= 125) {
			return false;
		}
		Integer c = 1;
		if (CStatsHandler.MS.containsKey(p.getName() + " - " + p.getUniqueId()))
			c = CStatsHandler.MS.get(p.getName() + " - " + p.getUniqueId());

		c++;
		if (!CStatsHandler.UC.containsKey(p.getUniqueId()))
			CStatsHandler.UC.put(p.getUniqueId(), new HashMap<String, Integer>());

		UUID uuid = p.getUniqueId();
		if (!CStatsHandler.UC.get(uuid).containsKey(detector)) {
			CStatsHandler.UC.get(uuid).put(detector, 1);
		} else {
			CStatsHandler.UC.get(uuid).put(detector, CStatsHandler.UC.get(uuid).get(detector) + 1);
		}
		CStatsHandler.MS.put(p.getName() + " - " + p.getUniqueId(), c);

		if (updateDatabase(p)) {
			return false;
		}
		String m = Settings.SUSPICION_ALERT;
		m = m.replaceAll("\\[VARIABLE_COLOR\\]", Settings.VARIABLE_COLOR);
		m = m.replaceAll("\\[DISPLAYNAME\\]", p.getDisplayName());
		m = m.replaceAll("\\[USERNAME\\]", p.getName());
		m = m.replaceAll("\\[NAME\\]", p.getName());
		m = m.replaceAll("\\[UUID\\]", p.getUniqueId().toString());
		m = m.replaceAll("\\[SUSPICION\\]", detector);
		m = m.replaceAll("\\[COUNT\\]", Count - 2 + "");
		m = m.replaceAll("\\[PING\\]", ping + "");
		m = m.replaceAll("\\[TPS\\]", Lag.getNiceTPS() + "");
		m = m.replaceAll("\\[X\\]", UtilMath.trim(2, p.getLocation().getX()) + "");
		m = m.replaceAll("\\[Y\\]", UtilMath.trim(2, p.getLocation().getY()) + "");
		m = m.replaceAll("\\[Z\\]", UtilMath.trim(2, p.getLocation().getZ()) + "");
		m = m.replaceAll("\\[WORLD\\]", p.getWorld().getName());

		broadcast(m);
		BungeeHandler.handleCrossAlert(m);

		return Settings.CANCEL_ON_OFFENSE;
	}

	public void broadcast(String... msgs) {
		for (String m : msgs) {
			console(m);
		}
		for (Player s : Bukkit.getOnlinePlayers()) {
			if ((s.hasPermission("bac.verbose") && !nonotify.contains(s))) {
				for (String m : msgs) {
					this.sendMessage(s, m, Settings.ABN);

				}
			}
		}

	}

	@EventHandler
	public void onQuit(PlayerQuitEvent event) {
		Player p = event.getPlayer();
		if (reports.containsKey(p))
			reports.remove(p);
		if (EXEMPTHANDLER.isExempt(p))
			EXEMPTHANDLER.removeExemption(p);
	}

}
