package me.jinky.checks.flight;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

import me.jinky.checks.Check;
import me.jinky.checks.CheckResult;
import me.jinky.logger.User;
import me.jinky.util.UtilBlock;
import me.jinky.util.VersionUtil;

public class FloatCheck extends Check {

	@Override
	public String getEventCall() {
		return "PlayerMoveEvent";
	}

	private static Map<Player, Map<Integer, Double>> FloatTicks = new HashMap<Player, Map<Integer, Double>>();

	@SuppressWarnings("deprecation")
	@Override
	public CheckResult performCheck(User u, Event ev) {
		Player p = u.getPlayer();
		int Count = 0;
		if (FloatTicks.containsKey(p)) {
			if (p.getLocation().getY() == FloatTicks.get(p).values().iterator().next() && !VersionUtil.isFlying(p)
					&& !UtilBlock.onBlock(p)) {
				for (Block b : UtilBlock.getSurrounding(p.getLocation().getBlock(), true)) {
					if (b.getType() == Material.AIR) {
						p.sendBlockChange(b.getLocation(), Material.AIR, (byte) 0);
					}
				}
				Count = FloatTicks.get(p).keySet().iterator().next() + 1;
			}
		}

		if (Count > 3) {
			Boolean onboat = false;
			for (Entity e : p.getNearbyEntities(0.2, 1, 0.2)) {
				if (e.getType() == EntityType.BOAT) {
					if (e.getLocation().getY() < p.getLocation().getY()) {
						onboat = true;
						break;
					}
				}
			}
			Map<Integer, Double> R = new HashMap<Integer, Double>();
			R.put(1, p.getLocation().getY());
			FloatTicks.put(p, R);
			if (p.getLocation().getBlock().getType() == Material.AIR
					&& p.getLocation().getBlock().getRelative(BlockFace.DOWN).isLiquid()) {
				if (!onboat) {
					return new CheckResult("Fly (Water Walk)", false);
				}
			}
			if (!onboat) {
				return new CheckResult("Fly (Float)", false);
			}
			return new CheckResult("Fly (Float/Water Walk)", true);
		}
		return new CheckResult("Fly (Float/Water Walk)", true);
	}
}
