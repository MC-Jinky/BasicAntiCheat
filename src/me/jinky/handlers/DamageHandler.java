package me.jinky.handlers;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.spigotmc.event.entity.EntityDismountEvent;

import me.jinky.BAC;
import me.jinky.Settings;
import me.jinky.checks.Check;
import me.jinky.checks.CheckResult;
import me.jinky.util.MiniPlugin;

public class DamageHandler extends MiniPlugin {

	public DamageHandler(BAC plugin) {
		super("Damage Handler", plugin);
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onDamage(EntityDamageEvent event) {
		if (event.getEntity() instanceof Player)
			BAC.getBAC().EXEMPTHANDLER.addExemption((Player) event.getEntity(), 845, "damaged");
	}

	@EventHandler
	public void onEject(EntityDismountEvent event) {
		if (event.getEntity() instanceof Player) {
			Player p = (Player) event.getEntity();
			BAC.getBAC().EXEMPTHANDLER.addExemption(p, 500, "vehicle exempt");
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onDamage(EntityDamageByEntityEvent event) {
		if (Settings.ENABLED == false) {
			return;
		}
		if (event.getDamager() instanceof Player) {
			Player p = (Player) event.getDamager();
			for (Check c : this.getPlugin().All_Checks) {
				if (c.getEventCall().equals(event.getEventName())
						|| c.getSecondaryEventCall().equals(event.getEventName())) {
					CheckResult result = c.performCheck(this.getPlugin().getUser(p), event);
					if (!result.passed()) {
						this.getPlugin().addSuspicion(p, result.getCheckName());
					}
				}
			}
		}
	}
}
