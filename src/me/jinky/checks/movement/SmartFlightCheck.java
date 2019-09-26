package me.jinky.checks.movement;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffectType;

import me.jinky.BAC;
import me.jinky.checks.Check;
import me.jinky.checks.CheckResult;
import me.jinky.logger.User;
import me.jinky.util.JVelocity;
import me.jinky.util.UtilBlock;
import me.jinky.util.UtilMath;

public class SmartFlightCheck extends Check {

	@Override
	public String getName() {
		return "SmartFlyCheck";
	}

	@Override
	public String getEventCall() {
		return "PlayerMoveEvent";
	}

	private static Map<Player, Integer> gc = new HashMap<Player, Integer>();
	private static Map<Player, Integer> sc = new HashMap<Player, Integer>();
	private static Map<Player, Integer> fl = new HashMap<Player, Integer>();
	private static Map<Player, Double> fr = new HashMap<Player, Double>();

	@Override
	public CheckResult performCheck(User u, Event e) {
		Player p = u.getPlayer();

		if (p.isInsideVehicle() || p.isGliding() || p.isFlying() || p.isSwimming()
				|| p.getLocation().getBlock().isLiquid()
				|| p.getLocation().getBlock().getRelative(BlockFace.DOWN).isLiquid() || p.isInsideVehicle()) {
			return new CheckResult("SmartFlight", true, "pass");
		}
		PlayerMoveEvent ev = (PlayerMoveEvent) e;
		JVelocity v = new JVelocity(ev.getFrom(), ev.getTo());

		Double h = v.yoffset();

		if (UtilBlock.onStairs(p)) {
			BAC.getBAC().EXEMPTHANDLER.addExemption(p, 5, "on stairs");
		}

		Boolean aroundwater = false;
		for (Block b : UtilBlock.getSurrounding(p.getLocation().getBlock(), true)) {
			if (b.isLiquid()) {
				aroundwater = true;
				break;
			}
		}
		Boolean inwater = UtilBlock.isSwimming(p);
		if (!gc.containsKey(p))
			gc.put(p, 0);

		if (!fr.containsKey(p))
			fr.put(p, 0.0);

		if (!sc.containsKey(p))
			sc.put(p, 0);

		if (!fl.containsKey(p))
			fl.put(p, 0);

		if (h >= 0.120 && UtilBlock.climbable(p.getLocation().getBlock()) && aroundwater == false) {
			fl.put(p, fl.get(p) + 1);
		} else {
			fl.put(p, 0);
		}
		if (h == 0.2 && !UtilBlock.climbable(p.getLocation().getBlock())) {
			sc.put(p, sc.get(p) + 1);
		} else {
			sc.put(p, 0);
		}

		if (!UtilBlock.climbable(p.getLocation().getBlock()) && !p.hasPotionEffect(PotionEffectType.SLOW_FALLING)
				&& h.equals(fr.get(p)) && u.isFalling() && h > -0.3 && inwater == false) {
			gc.put(p, gc.get(p) + 1);
		} else {
			gc.put(p, 0);
		}
		if (gc.get(p) > 4 && inwater == false) {
			gc.put(p, 0);
			if (!UtilBlock.climbable(p.getLocation().getBlock())) {
				return new CheckResult("Glide/SlowFall", false, "slower than usual & steady rate");
			}
		}

		if (sc.get(p) > 4 && inwater == false) {
			sc.put(p, 0);
			return new CheckResult("Spider", false, "abnormal y increase");
		}

		if (fl.get(p) > 4) {
			fl.put(p, 0);
			return new CheckResult("FastClimb", false, "climbed at " + UtilMath.trim(4, h) + ", max possible is 0.120");
		}

		if (h < 0.001) {
			fr.put(p, h);
		}
		return new CheckResult("SmartFlight", true, "pass");
	}

}
