package me.jinky.checks.movement;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import me.jinky.Cenix;
import me.jinky.checks.Check;
import me.jinky.checks.CheckResult;
import me.jinky.logger.PlayerLogger;
import me.jinky.logger.User;
import me.jinky.util.UtilBlock;
import me.jinky.util.UtilMath;
import me.jinky.util.UtilTime;

public class SpeedCheck extends Check {

	@Override
	public String getEventCall() {
		return "PlayerMoveEvent";
	}

	private HashMap<Player, Map<Integer, Long>> SpeedTicks = new HashMap<Player, Map<Integer, Long>>();

	@Override
	public CheckResult performCheck(User u, Event ex) {
		if (!ex.getEventName().equalsIgnoreCase(this.getEventCall())) {
			Cenix.getCenix().console("§4There was an error with cenix!");
			Cenix.getCenix().console("§4BreakCheck performCheck was called on a non-applicable event!");
			Cenix.getCenix().console("§fRequired Event: " + this.getEventCall());
			Cenix.getCenix().console("§fEvent fired upon: " + ex.getEventName());
			return new CheckResult("SpeedCheck err.", true);
		}
		PlayerMoveEvent event = (PlayerMoveEvent) ex;
		Integer Count = 0;
		Player p = u.getPlayer();
		double Offset = 0;
		double Limit = 0.35;
		if (this.SpeedTicks.containsKey(p)) {
			if (event.getFrom().getY() > event.getTo().getY()) {
				Offset = UtilMath.offset2d(event.getFrom(), event.getTo());
			} else {
				Offset = UtilMath.offset(event.getFrom(), event.getTo());
			}

			if (p.isGliding() || u.isBouncing() || u.isFalling() || u.getPlayer().isInsideVehicle()) {
				return new CheckResult("Speed", true);
			}
			if (UtilBlock.onBlock(p)) {
				Limit = 0.56;
			}
			if (UtilBlock.onStairs(p)) {
				Limit = 0.77;
			}
			if (Limit < 0.77 && UtilBlock.getBlockAbove(p).getType() != Material.AIR) {
				Limit = 0.77;
			}
			if (PlayerLogger.getLogger().getLastElytraFly(p) != -1L) {
				if (PlayerLogger.getLogger().getLastElytraFly(p) < 150) {
					return new CheckResult("Speed", true);
				}
			}
			Boolean ice = false;
			if (u.getBlockBelow().getType().toString().toLowerCase().contains("ice")) {
				ice = true;
			}

			/*
			 * For those wondering, this is for the 2 block height running speed-bug in
			 * minecraft Makes you run (at highest) around 1.17, so I bumped up to 1.2 just
			 * to be safe
			 */
			if (p.getLocation().getBlock().getRelative(BlockFace.DOWN).getType().toString().contains("ICE")
					|| p.getLocation().getBlock().getRelative(0, -2, 0).getType().toString().contains("ICE")) {
				Limit += 2.0;
			}

			/*
			 * TODO: Fix for flast elytra flight up/down when on ice, increases speed
			 * slightly.
			 */
			if (ice && (p.isGliding() || u.LastElytraFly() <= 650)) {
				Limit += 0.38D;
			}
			if (p.isSwimming()) {
				Limit += 0.15D;
			}
			if (p.getInventory().getBoots() != null) {
				if (p.getInventory().getBoots().containsEnchantment(Enchantment.DEPTH_STRIDER)) {
					Limit += 0.08 * (p.getInventory().getBoots().getEnchantmentLevel(Enchantment.DEPTH_STRIDER) + 1);
				}
			}
			for (PotionEffect e : p.getActivePotionEffects()) {
				if (e.getType().equals(PotionEffectType.SPEED)) {
					if (UtilBlock.onStairs(p)) {
						Limit += 0.14D * (e.getAmplifier() + 1);
					} else if (p.isOnGround()) {
						Limit += 0.08D * (e.getAmplifier() + 1);
					} else {
						Limit += 0.04D * (e.getAmplifier() + 1);
					}
				} else if (e.getType().equals(PotionEffectType.JUMP)) {
					Limit += 0.18D * (e.getAmplifier() + 1);
				}
			}
			if (Offset > Limit && !UtilTime.elapsed(SpeedTicks.get(p).entrySet().iterator().next().getValue(), 200L)) {
				Count = SpeedTicks.get(p).entrySet().iterator().next().getKey() + 1;
			} else {
				Count = 0;
			}
		}
		Boolean call = false;
		if (Count > 3) {
			Map<Integer, Long> R = new HashMap<Integer, Long>();
			R.put(4, System.currentTimeMillis());
			SpeedTicks.put(p, R);
			if (!UtilBlock.onBlock(p)) {
				call = true;
			}
			if (call) {
				if (!UtilBlock.onBlock(p)) {
					return new CheckResult("Flight", false);
				} else {
					return new CheckResult("Speed", false);
				}
			}
		} else {
			if (Count > 0)
				Count--;

			Map<Integer, Long> R = new HashMap<Integer, Long>();
			R.put(Count, System.currentTimeMillis());
			SpeedTicks.put(p, R);
		}
		return new CheckResult("Speed", true);
	}

}
