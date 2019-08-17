package me.jinky.checks.world;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.Ageable;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

import me.jinky.BAC;
import me.jinky.checks.Check;
import me.jinky.checks.CheckResult;
import me.jinky.logger.User;

public class AntiCactusBerryCheck extends Check {

	@Override
	public String getName() {
		return "AntiDamage";
	}

	@Override
	public String getEventCall() {
		return "PlayerMoveEvent";
	}

	@Override
	public CheckResult performCheck(User u, Event e) {
		Player p = u.getPlayer();
		String exemptreason = BAC.getBAC().EXEMPTHANDLER.getExemptReason(p);
		if (!exemptreason.equals("damaged")) {
			Boolean anticactus = false;
			if (p.getLocation().add(0, 0, -0.31).getBlock().getType() == Material.CACTUS) {
				anticactus = true;
			}
			if (p.getLocation().add(0, 0, 0.31).getBlock().getType() == Material.CACTUS) {
				anticactus = true;
			}
			if (p.getLocation().add(0.31, 0, 0).getBlock().getType() == Material.CACTUS) {
				anticactus = true;
			}
			if (p.getLocation().add(-0.31, 0, 0).getBlock().getType() == Material.CACTUS) {
				anticactus = true;
			}
			if (anticactus) {
				return new CheckResult("Anti-Cactus", false, "cactus didn't hurt");
			}
			Boolean antiberry = false;
			Block bb = null;
			if (p.getLocation().add(0, 0, -0.301).getBlock().getType() == Material.SWEET_BERRY_BUSH) {
				antiberry = true;
				bb = p.getLocation().add(0, 0, -0.301).getBlock();
			}
			if (p.getLocation().add(0, 0, 0.301).getBlock().getType() == Material.SWEET_BERRY_BUSH) {
				antiberry = true;
				bb = p.getLocation().add(0, 0, 0.301).getBlock();
			}
			if (p.getLocation().add(0.301, 0, 0).getBlock().getType() == Material.SWEET_BERRY_BUSH) {
				antiberry = true;
				bb = p.getLocation().add(0.301, 0, 0).getBlock();

			}
			if (p.getLocation().add(-0.301, 0, 0).getBlock().getType() == Material.SWEET_BERRY_BUSH) {
				antiberry = true;
				bb = p.getLocation().add(-0.301, 0, 0).getBlock();
			}
			if (bb != null) {
				if (bb.getBlockData() instanceof Ageable) {
					Ageable age = (Ageable) bb.getBlockData();
					if (age.getAge() > 0) {
						antiberry = true;
					} else {
						antiberry = false;
					}
				}
			} else {
				antiberry = false;
			}
			if (antiberry) {
				return new CheckResult("Anti-BerryBush", false, "berrybusy didn't hurt");
			}
		}
		return new CheckResult("AntiDamage", true, "pass");
	}

}
