package me.jinky.checks.movement;

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
import me.jinky.util.JVelocity;

public class SmartSpeedCheck extends Check {

	@Override
	public String getEventCall() {
		return "PlayerMoveEvent";
	}

	@Override
	public CheckResult performCheck(User u, Event e) {
		PlayerMoveEvent event = (PlayerMoveEvent) e;
		Player p = u.getPlayer();
		JVelocity jv = new JVelocity(event.getFrom(), event.getTo());
		Double x = jv.xoffset();
		Double y = jv.yoffset();
		Double z = jv.zoffset();

		Double mxz = 0.672;
		Double my = 0.76;

		Double lxz = -0.672;

		if (p.isInsideVehicle() || p.isFlying() || p.isGliding() || p.getGameMode() == GameMode.CREATIVE
				|| p.getGameMode() == GameMode.SPECTATOR || Cenix.getCenix().getUser(p).isBouncing())
			return new CheckResult("SmartSpeed", true);

		if (p.hasPotionEffect(PotionEffectType.JUMP)) {
			my += 0.10 * p.getPotionEffect(PotionEffectType.JUMP).getAmplifier();
		}

		if (p.hasPotionEffect(PotionEffectType.SPEED)) {
			mxz += 0.60 * p.getPotionEffect(PotionEffectType.SPEED).getAmplifier();
			lxz += -0.60 * p.getPotionEffect(PotionEffectType.SPEED).getAmplifier();
		}

		if (p.getLocation().getBlock().getType().toString().contains("STAIRS")
				|| p.getLocation().getBlock().getRelative(BlockFace.DOWN).getType().toString().contains("STAIRS")) {
			my += 0.75;
		}

		if (p.getLocation().getBlock().getRelative(BlockFace.DOWN).getType().toString().contains("ICE")
				|| p.getLocation().getBlock().getRelative(0, -2, 0).getType().toString().contains("ICE")) {
			mxz += 0.375;
			lxz -= 0.375;
			my += 0.25;
		}

		if (Cenix.getCenix().EXEMPTHANDLER.isExempt(p)) {
			return new CheckResult("SmartSpeed", true);
		}

		Boolean speed = false;
		if (x < 0 && Math.abs(x) > Math.abs(lxz)) {
			speed = true;
		}
		if (z < 0 && Math.abs(z) > Math.abs(lxz)) {
			speed = true;
		}
		if (x > mxz) {
			speed = true;
		}
		if (y > my) {
			return new CheckResult("Flight", false);
		}
		if (z > mxz) {
			speed = true;
		}

		if (speed) {
			return new CheckResult("Speed", false);
		}

		return new CheckResult("Speed/Flight", true);
	}

}
