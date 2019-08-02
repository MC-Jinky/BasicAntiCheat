package me.jinky;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import me.jinky.checks.Check;
import me.jinky.checks.CheckResult;
import me.jinky.checks.blocks.BreakCheck;
import me.jinky.checks.blocks.PlaceCheck;
import me.jinky.checks.combat.KillAuraCheck;
import me.jinky.checks.combat.MultiAuraCheck;
import me.jinky.checks.combat.ReachCheck;
import me.jinky.checks.flight.BoatCheck;
import me.jinky.checks.flight.FloatCheck;
import me.jinky.checks.flight.HoverCheck;
import me.jinky.checks.flight.RiseCheck;
import me.jinky.checks.flight.SmartFlightCheck;
import me.jinky.checks.flight.WaterCheck;
import me.jinky.checks.movement.SmartSpeedCheck;
import me.jinky.checks.movement.SpeedCheck;
import me.jinky.command.CenixCmd;
import me.jinky.fwk.CommandFramework;
import me.jinky.handlers.CStatsHandler;
import me.jinky.handlers.DamageHandler;
import me.jinky.handlers.ExemptHandler;
import me.jinky.logger.PlayerLogger;
import me.jinky.logger.User;

public class Cenix extends JavaPlugin implements Listener {

	private static Object antiLock = new Object();
	public static Cenix core = null;
	public static CommandFramework _fw = null;

	public List<Player> nonotify = new ArrayList<Player>();
	private static Map<Player, HashMap<Long, String>> reports = new HashMap<Player, HashMap<Long, String>>();

	private static List<Check> All_Checks = new ArrayList<Check>();

	public DamageHandler DAMAGEHANDLER = null;
	public ExemptHandler EXEMPTHANDLER = null;
	public CStatsHandler CSTATSHANDLER = null;

	@Override
	public void onEnable() {
		core = this;
		saveDefaultConfig();

		_fw = new CommandFramework(this);
		_fw.registerCommands(new CenixCmd());

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
		this.registerCheck(new RiseCheck());
		this.registerCheck(new FloatCheck());
		this.registerCheck(new ReachCheck());
		DAMAGEHANDLER = new DamageHandler(this);
		EXEMPTHANDLER = new ExemptHandler(this);
		CSTATSHANDLER = new CStatsHandler(this);

		Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(this, new Lag(), 100L, 1L);
		this.getServer().getPluginManager().registerEvents(this, this);
		this.getServer().getPluginManager().registerEvents(new PlayerLogger(), this);
		Settings.loadConfig();
		if (Bukkit.getOnlinePlayers().size() > 0) {
			for (Player p : Bukkit.getOnlinePlayers()) {
				EXEMPTHANDLER.addExemption(p, 2000);
			}
		}

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

	public void sendMessage(Player p, String msg) {
		p.sendMessage(Settings.PREFIX + " " + msg);
	}

	public void sendMessageBAR(Player p, String msg) {
		p.sendActionBar(Settings.PREFIX + " " + msg);
	}

	@Override
	public boolean onCommand(CommandSender sender, org.bukkit.command.Command command, String label, String[] args) {
		if (!(sender instanceof Player)) {
			return false;
		}
		return _fw.handleCommand(sender, label, command, args);
	}

	public static Cenix getCenix() {
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

	@EventHandler(ignoreCancelled = true)
	public void onMove(PlayerMoveEvent event) {
		if (Settings.ENABLED == false || event.getPlayer().getGameMode() == GameMode.CREATIVE
				|| event.getPlayer().getGameMode() == GameMode.SPECTATOR) {
			return;
		}
		if (!EXEMPTHANDLER.isExempt(event.getPlayer())) {
			for (Check c : All_Checks) {
				if (c.getEventCall().equals(event.getEventName())) {
					CheckResult result = c.performCheck(this.getUser(event.getPlayer()), event);
					if (!result.passed()) {
						event.setCancelled(this.addSuspicion(event.getPlayer(), result.getCheckName()));
					}
				}
			}
		}

	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onPlace(BlockPlaceEvent event) {
		if (Settings.ENABLED == false) {
			return;
		}
		for (Check c : All_Checks) {
			if (c.getEventCall().equals(event.getEventName())) {
				CheckResult result = c.performCheck(this.getUser(event.getPlayer()), event);
				if (!result.passed()) {
					event.setCancelled(this.addSuspicion(event.getPlayer(), result.getCheckName()));
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onDamage(EntityDamageByEntityEvent event) {
		if (Settings.ENABLED == false) {
			return;
		}
		if (event.getDamager() instanceof Player) {
			Player p = (Player) event.getDamager();
			for (Check c : All_Checks) {
				if (c.getEventCall().equals(event.getEventName())) {
					CheckResult result = c.performCheck(this.getUser(p), event);
					if (!result.passed()) {
						event.setCancelled(this.addSuspicion(p, result.getCheckName()));
					}
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onBreak(BlockBreakEvent event) {
		if (Settings.ENABLED == false) {
			return;
		}
		for (Check c : All_Checks) {
			if (c.getEventCall().equals(event.getEventName())) {
				CheckResult result = c.performCheck(this.getUser(event.getPlayer()), event);
				if (!result.passed()) {
					event.setCancelled(this.addSuspicion(event.getPlayer(), result.getCheckName()));
				}
			}
		}
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
			if (reports.get(p).size() >= Settings.PUNISH_OFFENSE_COUNT && Settings.PUNISH) {
				String m = Settings.PUNISH_COMMAND;
				m = m.replaceAll("\\[VARIABLE_COLOR\\]", Settings.VARIABLE_COLOR);
				m = m.replaceAll("\\[DISPLAYNAME\\]", p.getDisplayName());
				m = m.replaceAll("\\[USERNAME\\]", p.getName());
				m = m.replaceAll("\\[NAME\\]", p.getName());
				m = m.replaceAll("\\[UUID\\]", p.getUniqueId().toString());
				Bukkit.getServer().dispatchCommand(getServer().getConsoleSender(), m);
				return true;
			} else {
				return false;
			}
		}
	}

	public void logFile(String line) {
		if (Settings.LOG_OFFENSES) {
			try {
				FileWriter fw = new FileWriter(new File(this.getDataFolder(), "offenses.txt"), true);
				PrintWriter pw = new PrintWriter(fw);
				Date d = new Date();
				SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss a");
				try {
					sdf.setTimeZone(TimeZone.getTimeZone(Settings.TIMEZONE));
				} catch (Exception e) {
					this.console("§c'timezone' is not a valid Time Zone! Defaulting to America/New_York");
					Settings.TIMEZONE = "America/New_York";
				}
				pw.println("[" + sdf.format(d).toLowerCase().replaceAll(" am", "am").replaceAll(" pm", "pm") + "] "
						+ line);
				pw.flush();
				pw.close();
			} catch (IOException e) {
				e.printStackTrace();
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

		if (!reports.containsKey(p))
			reports.put(p, new HashMap<Long, String>());

		if (EXEMPTHANDLER.isExempt(p)) {
			return false;
		}

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
		CSTATSHANDLER.OC++;
		if (!CSTATSHANDLER.CC.containsKey(detector)) {
			CSTATSHANDLER.CC.put(detector, 1);
		} else {
			CSTATSHANDLER.CC.put(detector, CSTATSHANDLER.CC.get(detector) + 1);
		}

		if (Lag.getTPS() <= Settings.TPS_LAG_THRESHOLD || ping >= 125) {
			return false;
		}
		Integer c = 1;
		if (CSTATSHANDLER.MS.containsKey(p.getName() + " - " + p.getUniqueId()))
			c = CSTATSHANDLER.MS.get(p.getName() + " - " + p.getUniqueId());

		c++;
		if (!CSTATSHANDLER.UC.containsKey(p.getUniqueId()))
			CSTATSHANDLER.UC.put(p.getUniqueId(), new HashMap<String, Integer>());

		UUID uuid = p.getUniqueId();
		if (!CSTATSHANDLER.UC.get(uuid).containsKey(detector)) {
			CSTATSHANDLER.UC.get(uuid).put(detector, 1);
		} else {
			CSTATSHANDLER.UC.get(uuid).put(detector, CSTATSHANDLER.UC.get(uuid).get(detector) + 1);
		}
		CSTATSHANDLER.MS.put(p.getName() + " - " + p.getUniqueId(), c);

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

		broadcast(m);

		return Settings.CANCEL_ON_OFFENSE;
	}

	public void broadcast(String... msgs) {
		for (String m : msgs) {
			console(m);
		}
		for (Player s : Bukkit.getOnlinePlayers()) {
			if ((s.hasPermission("cenix.notify") && !nonotify.contains(s))) {
				for (String m : msgs) {
					if (Settings.ABN) {
						this.sendMessageBAR(s, m);
					} else {
						this.sendMessage(s, m);
					}
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
