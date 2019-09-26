package me.jinky.checks.combat;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.util.Vector;

import me.jinky.checks.Check;
import me.jinky.checks.CheckResult;
import me.jinky.logger.User;
import me.jinky.util.UtilMath;

public class KillAuraCheck extends Check {

	@Override
	public String getName() {
		return "AuraCheck";
	}

	@Override
	public String getEventCall() {
		return "EntityDamageByEntityEvent";
	}

	@Override
	public String getSecondaryEventCall() {
		return "PlayerMoveEvent";
	}

	private static Map<Player, Location> lastloc = new HashMap<Player, Location>();

	@Override
	public CheckResult performCheck(User u, Event ex) {
		Player p = u.getPlayer();

		if (ex.getEventName().equalsIgnoreCase("PlayerMoveEvent")) {
			lastloc.put(p, p.getLocation());
			return new CheckResult("Kill Aura", true, "playermovelog");
		}
		if (p.isBlocking()) {
			return new CheckResult("Impossible Fight", false, "player is blocking + attacking");
		}
		if (p.isSleeping()) {
			return new CheckResult("Impossible Fight", false, "player is sleeping");
		}
		if (p.isDead()) {
			return new CheckResult("Impossible Fight", false, "player is dead");
		}
		if (ex.getEventName().equalsIgnoreCase("EntityDamageByEntityEvent")) {
			EntityDamageByEntityEvent event = (EntityDamageByEntityEvent) ex;
			if (!(event.getDamager() instanceof Player)) {
				return new CheckResult("Kill Aura", true, "not a player attacking");
			}
			if (event.getCause() == DamageCause.ENTITY_SWEEP_ATTACK) {
				return new CheckResult("Kill Aura", true, "sweep attack");
			}

			Player entity1 = (Player) event.getDamager();
			Entity entity2 = event.getEntity();
			boolean lineOfSight = false;
			Vector line = entity1.getLocation().toVector().clone().subtract(entity2.getLocation().toVector())
					.normalize();
			Vector dirFacing = ((LivingEntity) entity2).getEyeLocation().getDirection().clone().normalize();
			double angle = Math.acos(line.dot(dirFacing));
			if (angle > 0.785398163) {
				lineOfSight = false;
			}
			if (!lastloc.containsKey(p) || p.getGameMode() == GameMode.CREATIVE
					|| p.getGameMode() == GameMode.SPECTATOR) {
				return new CheckResult("Kill Aura", true, "move event didnt update, probably in creative mode.");
			}
			double oy = Math.abs(lastloc.get(p).getYaw());
			double op = Math.abs(lastloc.get(p).getPitch());
			double cy = Math.abs(p.getLocation().getYaw());
			double cp = Math.abs(p.getLocation().getPitch());
			boolean f_yaw = Math.abs(UtilMath.trim(1, cy - oy)) > 35;
			boolean f_pitch = Math.abs(UtilMath.trim(1, cp - op)) > 35;
			if (f_yaw || f_pitch) {
				return new CheckResult("Kill Aura", false, "irregular head snapping");
			}
			if (lineOfSight) {
				return new CheckResult("Kill Aura", false, "hit target not in sight");
			}
			return new CheckResult("Kill Aura", true, "pass");
		}
		return new CheckResult("Kill Aura", true, "pass");
	}

}
