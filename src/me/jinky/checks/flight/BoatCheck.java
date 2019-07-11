package me.jinky.checks.flight;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.Event;

import me.jinky.Cenix;
import me.jinky.checks.Check;
import me.jinky.checks.CheckResult;
import me.jinky.logger.User;
import me.jinky.util.UtilBlock;

public class BoatCheck extends Check {

	@Override
	public String getEventCall() {
		return "PlayerMoveEvent";
	}

	@Override
	public CheckResult performCheck(User u, Event e) {
		if (u.getBlock().isLiquid() || u.getBlock().getType().toString().toLowerCase().contains("carpet"))
			return new CheckResult("Boat Fly", true);

		if (u.InVehicle() && u.getVehicle().getType() == EntityType.BOAT) {
			if (u.getVehicleBlock().getType() == Material.AIR
					&& UtilBlock.getSurroundingIgnoreAir(u.getBlock(), false).size() == 0) {
				Location LastSafe = u.LastNormalBoatLoc();
				if (LastSafe != null) {
					Cenix.getCenix().addExemptionBlock(u, 5);
					Entity v = u.getVehicle();
					u.eject();
					u.teleport(LastSafe);
					v.teleport(LastSafe);
					Bukkit.getScheduler().runTaskLater(Cenix.getCenix().getPlugin(), new Runnable() {
						@SuppressWarnings("deprecation")
						@Override
						public void run() {
							v.setPassenger(u.getPlayer());
						}
					}, 1L);
				} else {
					u.eject();
				}
				return new CheckResult("Boat Fly", false);
			}
		}
		return new CheckResult("Fly", true);
	}

}
