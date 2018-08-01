package me.jinky.checks.blocks;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.util.Vector;

import me.jinky.Cenix;
import me.jinky.checks.Check;
import me.jinky.checks.CheckResult;
import me.jinky.logger.User;
import me.jinky.raytrace.RayTrace;
import me.jinky.util.VersionUtil;

public class PlaceCheck extends Check {

	private static Map<Player, Map<Long, Integer>> PlaceCount = new HashMap<Player, Map<Long, Integer>>();

	@Override
	public String getEventCall() {
		return "BlockPlaceEvent";
	}

	@Override
	public CheckResult performCheck(User u, Event e) {
		if (!e.getEventName().equalsIgnoreCase(this.getEventCall())) {
			Cenix.getCenix().console("§4There was an error with cenix!");
			Cenix.getCenix().console("§4BreakCheck performCheck was called on a non-applicable event!");
			Cenix.getCenix().console("§fRequired Event: " + this.getEventCall());
			Cenix.getCenix().console("§fEvent fired upon: " + e.getEventName());
			return new CheckResult("PlaceCheck err.", true);
		}
		BlockPlaceEvent event = (BlockPlaceEvent) e;
		Player p = u.getPlayer();
		int Count = 1;
		if (PlaceCount.containsKey(p)) {
			if (PlaceCount.get(p).keySet().iterator().next() > System.currentTimeMillis()) {
				Count += PlaceCount.get(p).values().iterator().next();
			} else {
				PlaceCount.get(p).clear();
			}
		} else {
		}
		if (!PlaceCount.containsKey(p)) {
			PlaceCount.put(p, new HashMap<Long, Integer>());
		}
		Map<Long, Integer> R = new HashMap<Long, Integer>();
		if (PlaceCount.get(p).size() == 0) {
			R.put(System.currentTimeMillis() + 1000, Count);
		} else {
			R.put(PlaceCount.get(p).keySet().iterator().next(), Count);
		}
		PlaceCount.put(p, R);
		if (Count > 9 && !VersionUtil.hasEfficiency(p)) {
			event.setCancelled(true);
			return new CheckResult("Fast Place (" + Count + "bps)", false);
		}
		Location placed = event.getBlockPlaced().getLocation();
		RayTrace rayTrace = new RayTrace(p.getEyeLocation().toVector(), p.getEyeLocation().getDirection());
		ArrayList<Vector> positions = rayTrace.traverse(6, 1.5);
		Boolean call = true;
		for (Vector v : positions) {
			if (v.toLocation(placed.getWorld()).distance(placed) < 3.2) {
				call = false;
				break;
			}
		}
		if (call) {
			return new CheckResult("Impossible Place (Not in LoS)", false);
		} else {
			return new CheckResult("Impossible Place / Fast Place", true);
		}
	}

}
