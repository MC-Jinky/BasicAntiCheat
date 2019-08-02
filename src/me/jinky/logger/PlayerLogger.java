package me.jinky.logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.event.vehicle.VehicleEnterEvent;

import me.jinky.Cenix;
import me.jinky.util.UtilBlock;
import me.jinky.util.VersionUtil;

public class PlayerLogger implements Listener {

	private static Map<Player, Long> LastElytraFly = new HashMap<Player, Long>();
	private static Map<Player, Long> LastFly = new HashMap<Player, Long>();
	private static Map<Player, Long> LastFall = new HashMap<Player, Long>();
	private static Map<Player, Long> LastSlimeBounce = new HashMap<Player, Long>();
	private static Map<Player, Long> LastTeleport = new HashMap<Player, Long>();
	private static Map<Player, Long> LastGroundTime = new HashMap<Player, Long>();
	private static List<Player> Falling = new ArrayList<Player>();
	private static List<Player> Bouncing = new ArrayList<Player>();
	private static Map<Player, Long> LastSprint = new HashMap<Player, Long>();
	private static Map<Player, Location> LastGroundLocation = new HashMap<Player, Location>();
	private static Map<Player, Location> LastRegularMove = new HashMap<Player, Location>();
	private static Map<Player, Location> LastRegularBoatLocation = new HashMap<Player, Location>();

	private static PlayerLogger instance = null;

	public PlayerLogger() {
		instance = this;
	}

	public static PlayerLogger getLogger() {
		return instance;
	}

	public void updateLastRegularBoatLocation(Player p) {
		LastRegularBoatLocation.put(p, p.getVehicle().getLocation());
	}

	public void updateLastRegularMove(Player p) {
		LastRegularMove.put(p, p.getLocation());
	}

	public Location getLastRegularMove(Player p) {
		if (!LastRegularMove.containsKey(p))
			return null;
		return LastRegularMove.get(p);
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onGamemode(PlayerGameModeChangeEvent event) {
		if (event.getNewGameMode() != GameMode.CREATIVE && event.getNewGameMode() != GameMode.SPECTATOR) {
			updateLastFly(event.getPlayer());
			Cenix.getCenix().EXEMPTHANDLER.addExemption(event.getPlayer(), 3000);

		}
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onEnter(VehicleEnterEvent event) {
		if (event.getEntered() instanceof Player) {
			if (event.getVehicle().getType() == EntityType.BOAT) {
				updateLastRegularBoatLocation((Player) event.getEntered());
			}
		}
	}

	public Location getLastRegularBoatLocation(Player p) {
		if (!LastRegularBoatLocation.containsKey(p))
			return null;
		return LastRegularBoatLocation.get(p);
	}

	@EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
	public void onDamage(EntityDamageEvent event) {
		if (event.getEntity() instanceof Player) {
			Player p = (Player) event.getEntity();
			if (event.getCause().toString().toUpperCase().contains("EXPLOSION")) {
				Cenix.getCenix().EXEMPTHANDLER.addExemption(p, 3000);
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onFly(PlayerToggleFlightEvent event) {
		if (!event.isFlying()) {
			updateLastFly(event.getPlayer());
			Cenix.getCenix().EXEMPTHANDLER.addExemption(event.getPlayer(), 3000);
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onTeleport(PlayerTeleportEvent event) {
		this.updateLastTeleport(event.getPlayer());
		Cenix.getCenix().EXEMPTHANDLER.addExemption(event.getPlayer(), 5000);
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onMove(PlayerMoveEvent event) {
		Player p = event.getPlayer();
		Location f = event.getFrom();
		Location t = event.getTo();
		if (UtilBlock.onBlock(p)) {
			this.updateLastGroundTime(p);
			if (!p.getLocation().getBlock().getRelative(BlockFace.DOWN).isLiquid()) {
				LastGroundLocation.put(p, p.getLocation());
			}
		}
		if (p.isSprinting()) {
			LastSprint.put(p, System.currentTimeMillis());
		}
		if (p.isInsideVehicle()) {
			if (p.getVehicle().getLocation().getBlock().isLiquid()) {
				this.updateLastRegularBoatLocation(p);
			}
		}
		if (this.getLastElytraFly(p) < 150 && !p.isGliding() && this.getLastElytraFly(p) != -1L) {
			Cenix.getCenix().EXEMPTHANDLER.addExemption(p, 500);
		}
		if (t.getY() < f.getY() && !VersionUtil.isFlying(p)) {
			this.updateFalling(p, true);
			this.updateLastFall(p);
			this.updateBouncing(p, false);
			this.updateLastSlimeBounce(p);
		} else {
			if (p.isGliding())
				this.updateLastElytraFly(p);

			this.updateFalling(p, false);
			if (p.getLocation().getBlock().getRelative(BlockFace.DOWN).getType() == Material.SLIME_BLOCK) {
				this.updateBouncing(p, true);
				this.updateLastSlimeBounce(p);
			} else if (this.isBouncing(p) && this.getDown(t) == Material.AIR) {
				this.updateLastSlimeBounce(p);
			} else {
				this.updateBouncing(p, false);
			}
		}
		if (t.getY() > f.getY()) {
			this.updateFalling(p, false);
		}
	}

	private Material getDown(Location p) {
		return p.getBlock().getRelative(BlockFace.DOWN).getType();
	}

	public Location getLastGroundLocation(Player p) {
		if (LastGroundLocation.containsKey(p)) {
			return LastGroundLocation.get(p);
		} else {
			return null;
		}
	}

	public Long getLastElytraFly(Player p) {
		if (!LastElytraFly.containsKey(p))
			return -1L;
		return (System.currentTimeMillis() - LastElytraFly.get(p));
	}

	public Long getLastFly(Player p) {
		if (!LastFly.containsKey(p))
			return -1L;
		return (System.currentTimeMillis() - LastFly.get(p));
	}

	public Long getLastFall(Player p) {
		if (!LastFall.containsKey(p))
			return -1L;
		return (System.currentTimeMillis() - LastFall.get(p));
	}

	public Long getLastSlimeBounce(Player p) {
		if (!LastSlimeBounce.containsKey(p))
			return -1L;
		return (System.currentTimeMillis() - LastSlimeBounce.get(p));
	}

	public Long getLastSprint(Player p) {
		if (!LastSprint.containsKey(p))
			return -1L;
		return (System.currentTimeMillis() - LastSprint.get(p));
	}

	public Long getLastTeleport(Player p) {
		if (!LastTeleport.containsKey(p))
			return -1L;
		return (System.currentTimeMillis() - LastTeleport.get(p));
	}

	public Long getLastGroundTime(Player p) {
		if (!LastGroundTime.containsKey(p))
			return -1L;
		return (System.currentTimeMillis() - LastGroundTime.get(p));
	}

	public Boolean isFalling(Player p) {
		return Falling.contains(p);
	}

	public Boolean isElytra(Player p) {
		Boolean answer = false;
		if (p.isGliding())
			answer = true;

		if (LastElytraFly.containsKey(p)) {
			Long math = System.currentTimeMillis() - LastElytraFly.get(p);
			if (math <= 250) {
				answer = true;
			}
		}
		return answer;
	}

	public Boolean isBouncing(Player p) {
		return Bouncing.contains(p);
	}

	private void updateLastElytraFly(Player p) {
		LastElytraFly.put(p, System.currentTimeMillis());
	}

	private void updateLastFly(Player p) {
		LastFly.put(p, System.currentTimeMillis());
	}

	private void updateLastFall(Player p) {
		LastFall.put(p, System.currentTimeMillis());
	}

	private void updateLastSlimeBounce(Player p) {
		LastSlimeBounce.put(p, System.currentTimeMillis());
	}

	private void updateLastTeleport(Player p) {
		LastTeleport.put(p, System.currentTimeMillis());
	}

	private void updateLastGroundTime(Player p) {
		LastGroundTime.put(p, System.currentTimeMillis());
	}

	private void updateFalling(Player p, Boolean f) {
		if (f) {
			if (!Falling.contains(p))
				Falling.add(p);
			return;
		}
		if (Falling.contains(p))
			Falling.remove(p);
	}

	private void updateBouncing(Player p, Boolean b) {
		if (b) {
			if (!Bouncing.contains(p))
				Bouncing.add(p);
			return;
		}
		if (Bouncing.contains(p))
			Bouncing.remove(p);
	}
}
