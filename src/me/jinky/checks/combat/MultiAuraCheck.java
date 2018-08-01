package me.jinky.checks.combat;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

import me.jinky.Cenix;
import me.jinky.checks.Check;
import me.jinky.checks.CheckResult;
import me.jinky.logger.User;
import me.jinky.util.VersionUtil;

public class MultiAuraCheck extends Check {

	private static Map<Player, Map<Long, Location>> LastHit = new HashMap<Player, Map<Long, Location>>();

	@Override
	public String getEventCall() {
		return "EntityDamageByEntityEvent";
	}

	@Override
	public CheckResult performCheck(User u, Event ex) {
		if (!ex.getEventName().equalsIgnoreCase(this.getEventCall())) {
			Cenix.getCenix().console("§4There was an error with cenix!");
			Cenix.getCenix().console("§4BreakCheck performCheck was called on a non-applicable event!");
			Cenix.getCenix().console("§fRequired Event: " + this.getEventCall());
			Cenix.getCenix().console("§fEvent fired upon: " + ex.getEventName());
			return new CheckResult("MultiAura err.", true);
		}
		EntityDamageByEntityEvent event = (EntityDamageByEntityEvent) ex;
		Location hit = event.getEntity().getLocation();
		String ret = null;
		Player p = u.getPlayer();
		if (VersionUtil.isPlus19()) {
			if (event.getCause() == DamageCause.ENTITY_SWEEP_ATTACK) {
				return null;
			}
		}
		if (LastHit.containsKey(p)) {
			long time = System.currentTimeMillis() - LastHit.get(p).keySet().iterator().next();
			double distance = LastHit.get(p).values().iterator().next().distance(hit);

			if (distance > 1.5 && time < 8) {
				ret = "Multi Aura";
			}
		}
		Map<Long, Location> R = new HashMap<Long, Location>();
		R.put(System.currentTimeMillis(), hit);
		LastHit.put(p, R);
		if (ret != null) {
			return new CheckResult("Multi Aura", false);
		}
		return new CheckResult("Multi Aura", true);
	}

}
