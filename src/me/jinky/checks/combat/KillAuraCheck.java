package me.jinky.checks.combat;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import me.jinky.checks.Check;
import me.jinky.checks.CheckResult;
import me.jinky.logger.User;
import me.jinky.util.Utilities;

public class KillAuraCheck extends Check {

	private static Map<Player, Long> lastCheck = new HashMap<Player, Long>();

	@Override
	public String getEventCall() {
		return "EntityDamageByEntityEvent";
	}

	@Override
	public CheckResult performCheck(User u, Event ex) {
		Player p = u.getPlayer();
		if (p.isBlocking()) {
			return new CheckResult("Impossible Fight (Combat while Blocking)", false);
		}
		if (p.isSleeping()) {
			return new CheckResult("Impossible Fight (Combat while Sleeping)", false);
		}
		if (p.isDead()) {
			return new CheckResult("Impossible Fight (Combat while Dead)", false);
		}
		if (!lastCheck.containsKey(p)) {
			lastCheck.put(p, System.currentTimeMillis() - 500);
		}

		EntityDamageByEntityEvent event = (EntityDamageByEntityEvent) ex;
		Location hit = event.getEntity().getLocation();
		Location possible = event.getDamager().getLocation().getDirection().multiply(-2.2)
				.toLocation(event.getDamager().getWorld());
		if (!Utilities.canReallySeeEntity(u.getPlayer(), (LivingEntity) event.getEntity())) {
			return new CheckResult("Kill Aura", false);

		}
		if (hit.distance(possible) <= 1.1) {
			return new CheckResult("Kill Aura", false);
		}
		return new CheckResult("Kill Aura", true);
	}

}
