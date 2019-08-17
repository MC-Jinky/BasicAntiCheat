package me.jinky.checks.combat;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import me.jinky.checks.Check;
import me.jinky.checks.CheckResult;
import me.jinky.logger.User;

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
	public CheckResult performCheck(User u, Event ex) {
		Player p = u.getPlayer();
		if (p.isBlocking()) {
			return new CheckResult("Impossible Fight", false, "player is blocking + attacking");
		}
		if (p.isSleeping()) {
			return new CheckResult("Impossible Fight", false, "player is sleeping");
		}
		if (p.isDead()) {
			return new CheckResult("Impossible Fight", false, "player is dead");
		}

		EntityDamageByEntityEvent event = (EntityDamageByEntityEvent) ex;
		Location hit = event.getEntity().getLocation();
		Location possible = event.getDamager().getLocation().getDirection().multiply(-2.2)
				.toLocation(event.getDamager().getWorld());

		if (hit.distance(possible) <= 0.85) {
			return new CheckResult("Kill Aura", false, "hit target not in sight");
		}
		return new CheckResult("Kill Aura", true, "pass");
	}

}
