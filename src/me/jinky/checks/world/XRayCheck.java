package me.jinky.checks.world;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import me.jinky.checks.Check;
import me.jinky.checks.CheckResult;
import me.jinky.logger.User;
import me.jinky.util.UtilBlock;

public class XRayCheck extends Check {

	public static Map<Player, HashMap<Block, Long>> ghostblocks = new HashMap<Player, HashMap<Block, Long>>();
	public static Map<Player, HashMap<Block, Integer>> ghostblocks_closecount = new HashMap<Player, HashMap<Block, Integer>>();
	public static Map<Player, Long> lastcheck = new HashMap<Player, Long>();
	public static Map<Player, HashMap<Block, Double>> ghostblocks_lastcheckloc = new HashMap<Player, HashMap<Block, Double>>();

	@Override
	public String getName() {
		return "XRay";
	}

	@Override
	public String getEventCall() {
		return "PlayerMoveEvent";
	}

	@Override
	public String getSecondaryEventCall() {
		return "BlockBreakEvent";
	}

	@Override
	public CheckResult performCheck(User u, Event ev) {
		Player p = u.getPlayer();
		if (ev.getEventName().equalsIgnoreCase("PlayerMoveEvent")) {
			if (!lastcheck.containsKey(p)) {
				lastcheck.put(p, 1L);
			}
			PlayerMoveEvent e = (PlayerMoveEvent) ev;
			Long math = System.currentTimeMillis() - lastcheck.get(p);
			if (e.getTo().getWorld() != e.getFrom().getWorld()) {
				if (ghostblocks.containsKey(p)) {
					for (Block b : ghostblocks.get(p).keySet()) {
						b.setType(Material.STONE);
					}
					ghostblocks.remove(p);
				}
				if (ghostblocks_closecount.containsKey(p))
					ghostblocks_closecount.remove(p);
				if (ghostblocks_lastcheckloc.containsKey(p))
					ghostblocks_lastcheckloc.remove(p);
				lastcheck.put(p, System.currentTimeMillis());
				return new CheckResult("XRay", true, "WorldChange");
			}
			if (math > 1750 && ghostblocks_closecount.containsKey(p) && e.getFrom().distance(e.getTo()) > 0.1) {
				Iterator<Block> i = ghostblocks_closecount.get(p).keySet().iterator();
				List<Block> remove = new ArrayList<Block>();
				while (i.hasNext()) {
					Block b = i.next();
					if (b != null && b.getType() != null && !b.getType().toString().contains("ORE")) {
						remove.add(b);
						return new CheckResult("XRay", true, "pass");
					}
					Integer count = ghostblocks_closecount.get(p).get(b);

					if (b.getLocation().getWorld() != e.getTo().getWorld()) {
						return new CheckResult("XRay", true, "WorldChange");
					}
					if (e.getTo().distance(b.getLocation()) < ghostblocks_lastcheckloc.get(p).get(b)) {
						count++;
						ghostblocks_closecount.get(p).put(b, count);
					} else {
						count--;
						if (count < 0) {
							remove.add(b);
							b.setType(Material.STONE);
						} else {
							ghostblocks_closecount.get(p).put(b, count);
						}
					}
				}
				if (remove.size() > 0) {
					for (Block b : remove) {
						ghostblocks_closecount.get(p).remove(b);
						ghostblocks.get(p).remove(b);
						ghostblocks_lastcheckloc.get(p).remove(b);
					}
				}
				lastcheck.put(p, System.currentTimeMillis());
			}

		} else {
			if (!ghostblocks.containsKey(p)) {
				ghostblocks.put(p, new HashMap<Block, Long>());
			}
			if (!ghostblocks_closecount.containsKey(p)) {
				ghostblocks_closecount.put(p, new HashMap<Block, Integer>());
			}
			if (p.isSneaking()) {
				ghostblocks.get(p).put(p.getLocation().getBlock().getRelative(BlockFace.DOWN),
						System.currentTimeMillis());
			}

			BlockBreakEvent e = (BlockBreakEvent) ev;

			if (e.getBlock().getType().toString().contains("ORE")) {
				if (ghostblocks.get(p).containsKey(e.getBlock())) {
					Integer count = ghostblocks_closecount.get(p).get(e.getBlock());
					ghostblocks.get(p).remove(e.getBlock());
					ghostblocks_closecount.get(p).remove(e.getBlock());
					if (count > 4) {
						return new CheckResult("XRay", false, "broke ghostblocks 5+ blocks ret");
					}
				} else {
					BlockFace opposite = getopposite(p);
					Block target = e.getBlock();
					Integer g = 0;
					for (int i = 0; i < 9; i++) {
						target = target.getRelative(opposite);
						if (i > 4) {
							if (UtilBlock.xraysafe(target, target.getType()).size() == 0) {
								g++;
								if (g == 2 && target.getLocation().getWorld().equals(p.getLocation().getWorld())) {
									target.setType(e.getBlock().getType());
									ghostblocks.get(p).put(target, System.currentTimeMillis());
									ghostblocks_closecount.get(p).put(target, 4);
									if (!ghostblocks_lastcheckloc.containsKey(p)) {
										ghostblocks_lastcheckloc.put(p, new HashMap<Block, Double>());
									}
									ghostblocks_lastcheckloc.get(p).put(target,
											p.getLocation().distance(target.getLocation()));
									break;
								}

							}
						}
					}
				}
				return new CheckResult("XRay", true, "pass");
			}

			return new CheckResult("XRay", true, "pass");
		}
		return new CheckResult("XRay", true, "pass");
	}

	public static BlockFace getopposite(Player player) {
		float yaw = player.getLocation().getYaw();
		if (yaw < 0) {
			yaw += 360;
		}
		if (yaw >= 315 || yaw < 45) {
			return BlockFace.NORTH;
		} else if (yaw < 135) {
			return BlockFace.EAST;
		} else if (yaw < 225) {
			return BlockFace.SOUTH;
		} else if (yaw < 315) {
			return BlockFace.WEST;
		}
		return BlockFace.NORTH;
	}

}
