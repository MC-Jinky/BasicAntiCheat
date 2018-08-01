package me.jinky.logger;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class User {

	private Player p;

	public User(Player p) {
		this.p = p;
	}

	public Location LastNormalBoatLoc() {
		return PlayerLogger.getLogger().getLastRegularBoatLocation(p);
	}

	public long LastElytraFly() {
		return PlayerLogger.getLogger().getLastElytraFly(p);
	}

	public long LastFall() {
		return PlayerLogger.getLogger().getLastFall(p);
	}

	public long LastSlimeBounce() {
		return PlayerLogger.getLogger().getLastSlimeBounce(p);
	}

	public long LastTeleport() {
		return PlayerLogger.getLogger().getLastTeleport(p);
	}

	public long LastGroundTime() {
		return PlayerLogger.getLogger().getLastGroundTime(p);
	}

	public boolean isFalling() {
		return PlayerLogger.getLogger().isFalling(p);
	}

	public boolean isBouncing() {
		return PlayerLogger.getLogger().isBouncing(p);
	}

	public Player getPlayer() {
		return p;
	}

	public Block getBlock() {
		return p.getLocation().getBlock();
	}

	public Block getBlockBelow() {
		return getBlock().getRelative(BlockFace.DOWN);
	}

	public boolean InVehicle() {
		return p.isInsideVehicle();
	}

	public Entity getVehicle() {
		return p.getVehicle();
	}

	public Block getVehicleBlock() {
		return this.getVehicle().getLocation().getBlock();
	}

	public void teleport(Location l) {
		p.teleport(l);
	}

	public void eject() {
		p.eject();
	}

	public Location LastGroundLocation() {
		return PlayerLogger.getLogger().getLastGroundLocation(p);
	}

	public Location LastRegularLocation() {
		return PlayerLogger.getLogger().getLastRegularMove(p);
	}

}
