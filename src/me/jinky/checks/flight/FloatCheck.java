package me.jinky.checks.flight;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.GameMode;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffectType;

import me.jinky.Cenix;
import me.jinky.checks.Check;
import me.jinky.checks.CheckResult;
import me.jinky.logger.User;
import me.jinky.util.UtilBlock;

public class FloatCheck extends Check {

	private static Map<Player, Integer> calls = new HashMap<Player, Integer>();

	@Override
	public String getEventCall() {
		return "PlayerMoveEvent";
	}

	@Override
	public CheckResult performCheck(User u, Event e) {
		PlayerMoveEvent event = (PlayerMoveEvent) e;

		if (!calls.containsKey(u.getPlayer())) {
			calls.put(u.getPlayer(), 0);
		}
		int cc = calls.get(u.getPlayer());
		Player p = u.getPlayer();
		if (p.isFlying() || p.getGameMode() == GameMode.CREATIVE || p.getGameMode() == GameMode.SPECTATOR
				|| p.isInsideVehicle()) {
			return new CheckResult("Fly", true);
		}

		Double mpx = event.getFrom().getY() - event.getTo().getY();
		if (event.getTo().getY() == event.getFrom().getY()
				&& UtilBlock.getSurroundingIgnoreAir(u.getBlock(), true).size() == 0) {
			cc++;
		} else if (mpx <= 0.007 && !p.hasPotionEffect(PotionEffectType.SLOW_FALLING)
				&& !p.getLocation().getBlock().isLiquid()
				&& !p.getLocation().getBlock().getRelative(BlockFace.DOWN).isLiquid()
				&& UtilBlock.getSurroundingIgnoreAir(u.getBlock(), true).size() == 0) {
			cc++;
		} else if (Cenix.getCenix().getUser(p).isFalling() && mpx <= 0.07) {
			cc++;
		} else {
			if (cc > 0) {
				cc--;
			}
		}
		if (cc > 7) {
			calls.put(u.getPlayer(), 2);

			return new CheckResult("Fly", false);
		} else {
			calls.put(u.getPlayer(), cc);
		}
		return new CheckResult("Fly", true);
	}

}
