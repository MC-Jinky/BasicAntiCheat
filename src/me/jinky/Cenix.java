package me.jinky;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import org.bukkit.Bukkit;
import org.bukkit.Location;
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
import org.bukkit.potion.PotionEffect;

import me.jinky.checks.Check;
import me.jinky.checks.CheckResult;
import me.jinky.checks.blocks.BreakCheck;
import me.jinky.checks.blocks.PlaceCheck;
import me.jinky.checks.combat.KillAuraCheck;
import me.jinky.checks.combat.MultiAuraCheck;
import me.jinky.checks.flight.BoatCheck;
import me.jinky.checks.flight.FloatCheck;
import me.jinky.checks.flight.HoverCheck;
import me.jinky.checks.flight.RiseCheck;
import me.jinky.checks.movement.SpeedCheck;
import me.jinky.command.CenixCmd;
import me.jinky.fwk.CommandFramework;
import me.jinky.logger.PlayerLogger;
import me.jinky.logger.User;
import me.jinky.util.UtilMath;

public class Cenix extends JavaPlugin implements Listener {

	private static Object antiLock = new Object();
	public static Cenix core = null;
	public static CommandFramework _fw = null;

	public List<Player> nonotify = new ArrayList<Player>();
	private static Map<Player, HashMap<Long, String>> reports = new HashMap<Player, HashMap<Long, String>>();
	private static Map<Player, Long> exempt = new HashMap<Player, Long>();
	private static Map<Player, Long> exemptblock = new HashMap<Player, Long>();

	private static List<Check> All_Checks = new ArrayList<Check>();

	@Override
	public void onEnable() {
		core = this;
		_fw = new CommandFramework(this);
		_fw.registerCommands(new CenixCmd());

		Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(this, new Lag(), 100L, 1L);
		this.getServer().getPluginManager().registerEvents(this, this);
		this.getServer().getPluginManager().registerEvents(new PlayerLogger(), this);

		Settings.loadConfig();

		if (Bukkit.getOnlinePlayers().size() > 0) {
			for (Player p : Bukkit.getOnlinePlayers()) {
				this.addExemption(p, 2000);
			}
		}

		this.registerCheck(new BreakCheck());
		this.registerCheck(new PlaceCheck());
		this.registerCheck(new KillAuraCheck());
		this.registerCheck(new MultiAuraCheck());
		this.registerCheck(new BoatCheck());
		this.registerCheck(new FloatCheck());
		this.registerCheck(new HoverCheck());
		this.registerCheck(new RiseCheck());
		this.registerCheck(new SpeedCheck());
	}

	private void registerCheck(Check check) {
		if (!All_Checks.contains(check))
			All_Checks.add(check);
	}

	public void sendMessage(Player p, String msg) {
		p.sendMessage(Settings.PREFIX + " " + msg);
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

	public boolean isExempt(Player p) {
		if (exempt.containsKey(p)) {
			if (System.currentTimeMillis() < exempt.get(p))
				return true;
		}
		return false;
	}

	public void addExemptionBlock(Player p, long ms) {
		exemptblock.put(p, System.currentTimeMillis() + ms);
	}

	public void addExemption(Player p, long ms) {
		if (exemptblock.containsKey(p)) {
			long a = exemptblock.get(p);
			if (System.currentTimeMillis() > a) {
				exemptblock.remove(p);
				exempt.put(p, System.currentTimeMillis() + ms);
			}
		} else {
			exempt.put(p, System.currentTimeMillis() + ms);
		}
	}

	public void console(String msg) {
		Bukkit.getConsoleSender().sendMessage(Settings.PREFIX + " " + msg);
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onMove(PlayerMoveEvent event) {
		if (!this.isExempt(event.getPlayer())) {
			for (Check c : All_Checks) {
				if (c.getEventCall().equals(event.getEventName())) {
					CheckResult result = c.performCheck(this.getUser(event.getPlayer()), event);
					if (!result.passed()) {
						this.addSuspicion(event.getPlayer(), result.getCheckName());
						Location newloc = this.getUser(event.getPlayer()).LastGroundLocation();
						if (result.getCheckName().contains("speed") && !result.getCheckName().contains("fly")) {
							newloc = this.getUser(event.getPlayer()).LastRegularLocation();
						}
						if (newloc != null) {
							event.getPlayer().teleport(newloc);
						} else {
							event.setCancelled(true);
						}
					}
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onPlace(BlockPlaceEvent event) {
		for (Check c : All_Checks) {
			if (c.getEventCall().equals(event.getEventName())) {
				CheckResult result = c.performCheck(this.getUser(event.getPlayer()), event);
				if (!result.passed()) {
					event.setCancelled(true);
					this.addSuspicion(event.getPlayer(), result.getCheckName());
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onDamage(EntityDamageByEntityEvent event) {
		if (event.getDamager() instanceof Player) {
			Player p = (Player) event.getDamager();
			for (Check c : All_Checks) {
				if (c.getEventCall().equals(event.getEventName())) {
					CheckResult result = c.performCheck(this.getUser(p), event);
					if (!result.passed()) {
						event.setCancelled(true);
						this.addSuspicion(p, result.getCheckName());
					}
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onBreak(BlockBreakEvent event) {
		for (Check c : All_Checks) {
			if (c.getEventCall().equals(event.getEventName())) {
				CheckResult result = c.performCheck(this.getUser(event.getPlayer()), event);
				if (!result.passed()) {
					event.setCancelled(true);
					this.addSuspicion(event.getPlayer(), result.getCheckName());
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
				int rid = 0;
				if (Settings.LOG_REPORTS) {
					String offenses = Settings.VARIABLE_COLOR;
					Date d = new Date();
					SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss a");
					sdf.setTimeZone(TimeZone.getTimeZone(Settings.TIMEZONE));
					String time = sdf.format(d).toLowerCase().replaceAll(" am", "am").replaceAll(" pm", "pm");
					int tick = 0;
					for (String s : reports.get(p).values()) {
						if (!offenses.contains(s)) {
							if (tick == 0) {
								offenses = "§f" + s;
							} else {
								offenses += "§7, " + Settings.VARIABLE_COLOR + s;
							}
							tick++;
						}
					}
					List<String> r = new ArrayList<String>();
					r.add("§f--- Report for " + "[VC]" + p.getName() + "§f ---");
					r.add("Time [EST]: " + "[VC]" + time);
					r.add("Offenses: " + "[VC]" + offenses);
					r.add(" ");
					r.add("The following was fetched before-punishment:");
					r.add("In Vehicle: " + "[VC]" + (p.isInsideVehicle() ? "Yes" : "No"));
					if (p.isInsideVehicle()) {
						r.add("Vehicle Type: [VC]"
								+ p.getVehicle().getType().toString().toLowerCase().replaceAll("_", " "));
					}
					r.add("Health: " + "[VC]" + UtilMath.trim(1, p.getHealth()) + " ("
							+ UtilMath.trim(1, (p.getHealth() / 2)) + " hearts)");
					r.add("Food Level: " + "[VC]" + p.getFoodLevel());
					r.add("Coordinates (X,Y,Z): " + "[VC]" + p.getLocation().getBlockX() + "§f, " + "[VC]"
							+ p.getLocation().getBlockY() + "§f, " + "[VC]" + p.getLocation().getBlockZ());
					r.add("On Fire: " + "[VC]" + (p.getFireTicks() > 0 ? "Yes" : "No"));
					r.add("Falling: " + "[VC]" + (PlayerLogger.getLogger().isFalling(p) ? "Yes" : "No"));
					r.add("Bouncing: " + "[VC]" + (PlayerLogger.getLogger().isBouncing(p) ? "Yes" : "No"));
					r.add("Flying: " + "[VC]" + (p.isFlying() ? "Yes" : "No"));
					r.add("GameMode: " + "[VC]" + p.getGameMode().toString().toLowerCase());
					r.add("[INACCURATE] Ping: " + "[VC]"
							+ ((org.bukkit.craftbukkit.v1_13_R2.entity.CraftPlayer) p).getHandle().ping / 2);
					r.add("Potion Effects: " + "[VC]" + (p.getActivePotionEffects().size() != 0 ? "Yes" : "No"));
					for (PotionEffect eff : p.getActivePotionEffects()) {
						r.add("  §f- " + "[VC]" + eff.getType().getName() + " (x" + eff.getAmplifier() + ")");
					}
					r.add("§f--- End of Report ---");
					int id = Reports.saveReport(r);
					rid = id;
					if (!Settings.REPORT_SAVED_ALERT.equalsIgnoreCase("")) {
						String m = Settings.REPORT_SAVED_ALERT;
						m = m.replaceAll("\\[VARIABLE_COLOR\\]", Settings.VARIABLE_COLOR);
						m = m.replaceAll("\\[DISPLAYNAME\\]", p.getDisplayName());
						m = m.replaceAll("\\[USERNAME\\]", p.getName());
						m = m.replaceAll("\\[NAME\\]", p.getName());
						m = m.replaceAll("\\[UUID\\]", p.getUniqueId().toString());
						m = m.replaceAll("\\[REPORT_ID\\]", id + "");
						broadcast(m);
					}
				}
				String m = Settings.PUNISH_COMMAND;
				m = m.replaceAll("\\[VARIABLE_COLOR\\]", Settings.VARIABLE_COLOR);
				m = m.replaceAll("\\[DISPLAYNAME\\]", p.getDisplayName());
				m = m.replaceAll("\\[USERNAME\\]", p.getName());
				m = m.replaceAll("\\[NAME\\]", p.getName());
				m = m.replaceAll("\\[UUID\\]", p.getUniqueId().toString());
				m = m.replaceAll("\\[REPORT_ID\\]", rid + "");
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
				sdf.setTimeZone(TimeZone.getTimeZone(Settings.TIMEZONE));
				pw.println("[" + sdf.format(d).toLowerCase().replaceAll(" am", "am").replaceAll(" pm", "pm") + "] "
						+ line);
				pw.flush();
				pw.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void addSuspicion(Player p, String detector) {
		if (!reports.containsKey(p)) {
			reports.put(p, new HashMap<Long, String>());
		}
		if (this.isExempt(p)) {
			return;
		}
		this.addExemptionBlock(p, 50);
		if (Lag.getTPS() <= Settings.TPS_LAG_THRESHOLD) {
			String m = Settings.SUSPICION_ALERT_IGNORE_TPS;
			m = m.replaceAll("\\[VARIABLE_COLOR\\]", Settings.VARIABLE_COLOR);
			m = m.replaceAll("\\[DISPLAYNAME\\]", p.getDisplayName());
			m = m.replaceAll("\\[USERNAME\\]", p.getName());
			m = m.replaceAll("\\[NAME\\]", p.getName());
			m = m.replaceAll("\\[UUID\\]", p.getUniqueId().toString());
			m = m.replaceAll("\\[SUSPICION\\]", detector);
			m = m.replaceAll("\\[COUNT\\]", "");
			broadcast(m);
			this.logFile(m);
			return;
		}
		if (updateDatabase(p)) {
			return;
		}
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
		String m = Settings.SUSPICION_ALERT;
		m = m.replaceAll("\\[VARIABLE_COLOR\\]", Settings.VARIABLE_COLOR);
		m = m.replaceAll("\\[DISPLAYNAME\\]", p.getDisplayName());
		m = m.replaceAll("\\[USERNAME\\]", p.getName());
		m = m.replaceAll("\\[NAME\\]", p.getName());
		m = m.replaceAll("\\[UUID\\]", p.getUniqueId().toString());
		m = m.replaceAll("\\[SUSPICION\\]", detector);
		m = m.replaceAll("\\[COUNT\\]", Count + "");
		this.logFile(m);
		broadcast(m);
	}

	public void broadcast(String... msgs) {
		for (String m : msgs) {
			this.console(m);
		}
		for (Player s : Bukkit.getOnlinePlayers()) {
			if (s.hasPermission("cenix.notify") && !nonotify.contains(s)) {
				for (String m : msgs) {
					this.sendMessage(s, m);
				}
			}
		}
	}

	@EventHandler
	public void onQuit(PlayerQuitEvent event) {
		Player p = event.getPlayer();
		if (reports.containsKey(p))
			reports.remove(p);
		if (exempt.containsKey(p))
			exempt.remove(p);
	}

	public void addExemptionBlock(User u, int ms) {
		this.addExemptionBlock(u.getPlayer(), ms);
	}
}
