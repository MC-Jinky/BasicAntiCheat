package me.jinky.checks.flight;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffectType;

import me.jinky.Cenix;
import me.jinky.checks.Check;
import me.jinky.checks.CheckResult;
import me.jinky.logger.User;
import me.jinky.util.JVelocity;
import me.jinky.util.UtilBlock;

public class SmartFlightCheck extends Check {

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

		if (p.isInsideVehicle() || p.isGliding() || p.isSwimming() || p.getLocation().getBlock().isLiquid()
				|| p.getLocation().getBlock().getRelative(BlockFace.DOWN).isLiquid() || p.isInsideVehicle()) {
			return new CheckResult("SmartFlight", true);
		}
		PlayerMoveEvent ev = (PlayerMoveEvent) e;
		JVelocity v = new JVelocity(ev.getFrom(), ev.getTo());

		Double h = v.yoffset();

		if (UtilBlock.onStairs(p)) {
			Cenix.getCenix().EXEMPTHANDLER.addExemption(p, 5);
		}

		if (!gc.containsKey(p))
			gc.put(p, 0);

		if (!fr.containsKey(p))
			fr.put(p, 0.0);

		if (!sc.containsKey(p))
			sc.put(p, 0);

		if (!fl.containsKey(p))
			fl.put(p, 0);

		if (h >= 0.120 && UtilBlock.climbable(p.getLocation().getBlock())) {
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
				&& h.equals(fr.get(p)) && u.isFalling() && h > -0.3) {
			gc.put(p, gc.get(p) + 1);
		} else {
			gc.put(p, 0);
		}
		if (gc.get(p) > 4) {
			gc.put(p, 0);
			return new CheckResult("Glide/SlowFall", false);
		}

		if (sc.get(p) > 4) {
			sc.put(p, 0);
			return new CheckResult("Spider", false);
		}

		if (fl.get(p) > 4) {
			fl.put(p, 0);
			return new CheckResult("FastClimb", false);
		}

		if (h < 0.001) {
			fr.put(p, h);
		}
		return new CheckResult("SmartFlight", true);
	}

}
