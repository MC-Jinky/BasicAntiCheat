package me.jinky.checks.combat;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import com.comphenix.protocol.wrappers.PlayerInfoData;

import me.jinky.checks.Check;
import me.jinky.checks.CheckResult;
import me.jinky.logger.User;

public class KillAuraCheck extends Check {

	public class CheckInfo {

		private PlayerInfoData d;
		private int entID;

		public CheckInfo(PlayerInfoData d, int entID) {
			this.d = d;
			this.entID = entID;
		}

		public PlayerInfoData getPID() {
			return this.d;
		}

		public int getEID() {
			return this.entID;
		}
	}

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
		if (hit.distance(possible) <= 1.1) {
			return new CheckResult("KillAura", false);
		}
		return new CheckResult("Kill Aura", true);
	}

}
