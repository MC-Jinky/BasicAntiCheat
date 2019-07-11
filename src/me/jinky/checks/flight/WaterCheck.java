package me.jinky.checks.flight;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

import me.jinky.Cenix;
import me.jinky.checks.Check;
import me.jinky.checks.CheckResult;
import me.jinky.logger.User;
import me.jinky.util.UtilBlock;
import me.jinky.util.Utilities;

public class WaterCheck extends Check {

	@Override
	public String getEventCall() {
		return "PlayerMoveEvent";
	}

	private static Map<Player, Map<Integer, Double>> FloatTicks = new HashMap<Player, Map<Integer, Double>>();

	@Override
	public CheckResult performCheck(User u, Event ev) {
		Player p = u.getPlayer();
		int Count = 0;
		if (FloatTicks.containsKey(p)) {
			Count = FloatTicks.get(p).keySet().iterator().next() + 1;
			if (isWaterWalking(p, ev)) {
				Count++;
			} else {
				Count--;
			}
		}

		if (Count > 15) {
			Map<Integer, Double> R = new HashMap<Integer, Double>();
			R.put(0, p.getLocation().getY());
			FloatTicks.put(p, R);
			Map<Integer, Double> RE = new HashMap<Integer, Double>();
			int nc = Count;
			nc--;
			if (nc < 0) {
				nc = 0;
			}
			RE.put(5, p.getLocation().getY());
			FloatTicks.put(p, RE);
			return new CheckResult("Water Walk", false);
		}
		Map<Integer, Double> RE = new HashMap<Integer, Double>();
		int nc = Count;
		nc--;
		if (nc < 0) {
			nc = 0;
		}
		if (p.getLocation().getBlock().getRelative(BlockFace.UP).getType() == Material.WATER
				&& p.getLocation().getBlock().getType() == Material.WATER) {
			nc = 0;
		}
		RE.put(nc, p.getLocation().getY());
		FloatTicks.put(p, RE);
		return new CheckResult("Water Walk", true);

	}

	public boolean isWaterWalking(Player p, Event ev) {
		User u = Cenix.getCenix().getUser(p);
		List<Block> b = UtilBlock.getSurroundingIgnoreAir(u.getBlock(), true);
		boolean sneak = false;
		boolean haswater = false;
		for (Block be : b) {
			if (be.getType() == Material.WATER) {
				haswater = true;
			}
			if (be.getLocation().getY() < u.getBlock().getY() && be.getType() != Material.WATER
					&& be.getType() != Material.KELP) {
				sneak = true;
			}
		}

		if (haswater == false) {
			return false;
		}
		double vel = p.getVelocity().getY();
		if (vel < 0) {
			vel = (vel + vel + vel);
		}
		if (p.getLocation().getBlock().getRelative(BlockFace.UP).getType() == Material.WATER
				&& p.getLocation().getBlock().getType() == Material.WATER) {
			return false;
		}

		if (!sneak
				&& ((p.getLocation().getY() + "").contains(".00250") || (p.getLocation().getY() + "").contains(".99"))
				&& !Utilities.isOnEntity(p, EntityType.BOAT) && !Utilities.isOnLilyPad(p) && !Utilities.isOnSteps(p)
				&& !Utilities.isOnVine(p) && !Utilities.isGlidingWithElytra(p)
				&& !u.getBlock().getType().toString().contains("CARPET")) {
			return true;
		}
		return false;
	}

}
