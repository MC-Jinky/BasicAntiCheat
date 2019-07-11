package me.jinky.checks.movement;

import java.lang.reflect.InvocationTargetException;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.player.PlayerTeleportEvent.TeleportCause;

import me.jinky.checks.Check;
import me.jinky.checks.CheckResult;
import me.jinky.logger.User;

public class BlinkCheck extends Check {

	@Override
	public String getEventCall() {
		return "PlayerTeleportEvent";
	}

	public int getPing(User u) {
		Player p = u.getPlayer();
		int ping = 0;
		try {
			Object entityPlayer = p.getClass().getMethod("getHandle").invoke(p);
			ping = (int) entityPlayer.getClass().getField("ping").get(entityPlayer);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException
				| SecurityException | NoSuchFieldException e) {
			e.printStackTrace();
		}

		return ping;
	}

	@Override
	public CheckResult performCheck(User u, Event ev) {
		PlayerTeleportEvent e = (PlayerTeleportEvent) ev;
		if (e.getCause() == TeleportCause.UNKNOWN) {
			e.setCancelled(true);
		}
		return new CheckResult("Invalid Teleport", true);
	}

}
