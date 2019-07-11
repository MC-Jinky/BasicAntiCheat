package me.jinky.checks.flight;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.potion.PotionEffectType;

import me.jinky.checks.Check;
import me.jinky.checks.CheckResult;
import me.jinky.logger.User;
import me.jinky.util.UtilBlock;
import me.jinky.util.VersionUtil;

public class RiseCheck extends Check {

	private static Map<Player, Map<Integer, Double>> RiseTicks = new HashMap<Player, Map<Integer, Double>>();

	@Override
	public String getEventCall() {
		return "PlayerMoveEvent";
	}

	@Override
	public CheckResult performCheck(User u, Event e) {
		int Count = 0;
		Player p = u.getPlayer();
		Boolean bouncecheck = false;
		if (u.LastSlimeBounce() != -1L) {
			if (u.LastSlimeBounce() < 1000) {
				bouncecheck = true;
			}
		}
		if (u.isBouncing() || bouncecheck
				|| (p.isInsideVehicle() && p.getVehicle().getType().toString().contains("HORSE"))) {
			return new CheckResult("Fly", true);
		}
		if (u.getPlayer().hasPotionEffect(PotionEffectType.JUMP)
				|| u.getPlayer().hasPotionEffect(PotionEffectType.SLOW_FALLING)) {
			return new CheckResult("Fly", true);
		}
		if (RiseTicks.containsKey(p)) {
			if (p.getLocation().getY() > RiseTicks.get(p).values().iterator().next() && !VersionUtil.isFlying(p)) {
				boolean nearBlocks = false;
				for (Block block : UtilBlock.getSurrounding(p.getLocation().getBlock(), true)) {
					if (block.getType() != Material.AIR) {
						nearBlocks = true;
						break;
					}
				}
				if (!nearBlocks) {
					Count = RiseTicks.get(p).keySet().iterator().next() + 1;
				}
			}
		}

		if (Count > 4) {
			Map<Integer, Double> R = new HashMap<Integer, Double>();
			R.put(2, p.getLocation().getY());
			RiseTicks.put(p, R);
			return new CheckResult("Fly", false);
		}
		Map<Integer, Double> R = new HashMap<Integer, Double>();
		R.put(Count, p.getLocation().getY());
		RiseTicks.put(p, R);
		return new CheckResult("Fly", true);
	}

}
