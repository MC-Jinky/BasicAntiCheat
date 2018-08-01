package me.jinky;

import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerMoveEvent;

public abstract class Detector {

	public abstract String getName();

	public abstract void handleQuit(Player p);

	public abstract void handleJoin(Player p);

	public abstract String handleMove(Player p, PlayerMoveEvent event);

	public abstract String handlePlace(Player p, BlockPlaceEvent event);

	public abstract String handleBreak(Player p, BlockBreakEvent event);

	public abstract String handleCombat(Player p, EntityDamageByEntityEvent event);
}
