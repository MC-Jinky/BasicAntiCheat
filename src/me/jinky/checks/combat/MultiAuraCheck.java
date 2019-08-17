package me.jinky.checks.combat;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

import me.jinky.checks.Check;
import me.jinky.checks.CheckResult;
import me.jinky.logger.User;

public class MultiAuraCheck extends Check {

	@Override
	public String getName() {
		return "MultiAuraCheck";
	}

	private static Map<Player, Map<Long, Location>> LastHit = new HashMap<Player, Map<Long, Location>>();

	@Override
	public String getEventCall() {
		return "EntityDamageByEntityEvent";
	}

	@Override
	public CheckResult performCheck(User u, Event ex) {
		EntityDamageByEntityEvent event = (EntityDamageByEntityEvent) ex;
		Location hit = event.getEntity().getLocation();
		String ret = null;
		Player p = u.getPlayer();
		if (event.getCause() == DamageCause.ENTITY_SWEEP_ATTACK) {
			return new CheckResult("Multi Aura", true, "sweep ignore");
		}
		if (LastHit.containsKey(p)) {
			long time = System.currentTimeMillis() - LastHit.get(p).keySet().iterator().next();
			Location last = LastHit.get(p).values().iterator().next();
			if (last.getWorld() != hit.getWorld()) {
				return new CheckResult("MultiAura", true, "pass");
			}
			double distance = last.distance(hit);

			if (distance > 1.5 && time < 8) {
				ret = "Multi Aura";
			}
		}
		Map<Long, Location> R = new HashMap<Long, Location>();
		R.put(System.currentTimeMillis(), hit);
		LastHit.put(p, R);
		if (ret != null) {
			return new CheckResult("Multi Aura", false, "hit multiple entities in less than 8ms");
		}
		return new CheckResult("Multi Aura", true, "pass");
	}

}
