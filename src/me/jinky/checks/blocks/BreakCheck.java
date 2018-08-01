package me.jinky.checks.blocks;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.util.Vector;

import me.jinky.Cenix;
import me.jinky.checks.Check;
import me.jinky.checks.CheckResult;
import me.jinky.logger.User;
import me.jinky.raytrace.RayTrace;
import me.jinky.util.BlockHardness;
import me.jinky.util.VersionUtil;

public class BreakCheck extends Check {

	private static Map<Player, Map<Long, Integer>> BreakCount = new HashMap<Player, Map<Long, Integer>>();

	@Override
	public String getEventCall() {
		return "BlockBreakEvent";
	}

	@Override
	public CheckResult performCheck(User u, Event e) {
		if (!e.getEventName().equalsIgnoreCase(this.getEventCall())) {
			Cenix.getCenix().console("§4There was an error with cenix!");
			Cenix.getCenix().console("§4BreakCheck performCheck was called on a non-applicable event!");
			Cenix.getCenix().console("§fRequired Event: " + this.getEventCall());
			Cenix.getCenix().console("§fEvent fired upon: " + e.getEventName());
			return new CheckResult("BreakCheck err.", true);
		}
		BlockBreakEvent event = (BlockBreakEvent) e;
		Player p = u.getPlayer();
		Boolean instant = false;
		Material m = event.getBlock().getType();
		try {
			if (BlockHardness.hasBlockHardness(event.getBlock().getType())) {
				if (BlockHardness.getHardness(event.getBlock().getType()).getHardness() <= 0.1D) {
					instant = true;
				}
			}
		} catch (Exception ex) {

		}
		if (p.getGameMode() == GameMode.CREATIVE || m == Material.SLIME_BLOCK || m == Material.NETHER_WARTS) {
			instant = true;
		}

		if (!instant) {
			Integer Count = 1;
			if (BreakCount.containsKey(p)) {
				if (BreakCount.get(p).keySet().iterator().next() > System.currentTimeMillis()) {
					Count += BreakCount.get(p).values().iterator().next();
				} else {
					BreakCount.get(p).clear();
				}
			}
			if (!BreakCount.containsKey(p)) {
				BreakCount.put(p, new HashMap<Long, Integer>());
			}
			Map<Long, Integer> R = new HashMap<Long, Integer>();
			if (BreakCount.get(p).size() == 0) {
				R.put(System.currentTimeMillis() + 1000, Count);
			} else {
				R.put(BreakCount.get(p).keySet().iterator().next(), Count);
			}
			if (!BreakCount.containsKey(p)) {
				BreakCount.put(p, new HashMap<Long, Integer>());
			}
			BreakCount.put(p, R);
			if (Count > 9 && !VersionUtil.hasEfficiency(p)) {
				event.setCancelled(true);
				return new CheckResult("Fast Break (" + Count + "bps)", false);
			}
			if (VersionUtil.hasEfficiency(p)) {
				return new CheckResult("Impossible Break / Fast Break", true);
			}
			Location placed = event.getBlock().getLocation();
			RayTrace rayTrace = new RayTrace(p.getEyeLocation().toVector(), p.getEyeLocation().getDirection());
			ArrayList<Vector> positions = rayTrace.traverse(6, 1.5);
			Boolean call = true;
			for (Vector v : positions) {
				if (v.toLocation(placed.getWorld()).distance(placed) < 2) {
					call = false;
					break;
				}
			}
			if (call) {
				return new CheckResult("Impossible Break (Not in LoS)", false);
			} else {
				return new CheckResult("Impossible Break / Fast Break", true);
			}
		} else {
			return new CheckResult("Impossible Break / Fast Break", true);
		}
	}
}
