package me.jinky.checks.combat;

import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import me.jinky.checks.Check;
import me.jinky.checks.CheckResult;
import me.jinky.logger.User;

public class ReachCheck extends Check {

	@Override
	public String getEventCall() {
		return "EntityDamageByEntityEvent";
	}

	@Override
	public CheckResult performCheck(User u, Event e) {
		EntityDamageByEntityEvent event = (EntityDamageByEntityEvent) e;
		Double range = event.getEntity().getLocation().distance(event.getDamager().getLocation());
		String rf = range + "";
		try {
			range = Double.parseDouble(rf.substring(0, 4));
		} catch (Exception exception) {

		}
		if (range > 5.98) {
			return new CheckResult("Reach", false);
		}
		return new CheckResult("Reach", true);
	}

}
