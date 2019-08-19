package me.jinky.checks.world;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.block.BlockBreakEvent;

import me.jinky.checks.Check;
import me.jinky.checks.CheckResult;
import me.jinky.logger.User;
import me.jinky.util.VersionUtil;

public class BreakCheck extends Check {

	@Override
	public String getName() {
		return "BreakCheck";
	}

	private static Map<Player, Map<Long, Integer>> BreakCount = new HashMap<Player, Map<Long, Integer>>();

	@Override
	public String getEventCall() {
		return "BlockBreakEvent";
	}

	@SuppressWarnings("deprecation")
	@Override
	public CheckResult performCheck(User u, Event e) {
		BlockBreakEvent event = (BlockBreakEvent) e;
		Player p = u.getPlayer();
		Boolean instant = false;
		Material m = event.getBlock().getType();
		try {
			if (m.getHardness() <= 0.1D) {
				instant = true;
			}
		} catch (Exception ex) {
		}
		List<Material> l = new ArrayList<Material>();
		l.add(Material.ACACIA_LEAVES);
		l.add(Material.BIRCH_LEAVES);
		l.add(Material.DARK_OAK_LEAVES);
		l.add(Material.JUNGLE_LEAVES);
		l.add(Material.OAK_LEAVES);
		l.add(Material.SPRUCE_LEAVES);
		l.add(Material.LEGACY_LEAVES);
		l.add(Material.LEGACY_LEAVES_2);
		if (p.getInventory().getItemInMainHand() != null) {
			if (p.getInventory().getItemInMainHand().getType() == Material.SHEARS) {
				if (l.contains(event.getBlock().getType())) {
					instant = true;
				}
			}
		}
		if (p.getGameMode() == GameMode.CREATIVE) {
			instant = true;
		}
		if (!instant) {
			Integer Count = 1;
			if (BreakCount.containsKey(p)) {
				if (BreakCount.get(p).keySet().iterator().next() > System.currentTimeMillis()) {
					Count += BreakCount.get(p).values().iterator().next();
				} else {
					BreakCount.get(p).clear();
				}
			}
			if (!BreakCount.containsKey(p)) {
				BreakCount.put(p, new HashMap<Long, Integer>());
			}
			Map<Long, Integer> R = new HashMap<Long, Integer>();
			if (BreakCount.get(p).size() == 0) {
				R.put(System.currentTimeMillis() + 1000, Count);
			} else {
				R.put(BreakCount.get(p).keySet().iterator().next(), Count);
			}
			if (!BreakCount.containsKey(p)) {
				BreakCount.put(p, new HashMap<Long, Integer>());
			}
			BreakCount.put(p, R);
			if (event.getBlock().getType().toString().contains("MUSHROOM")
					|| event.getBlock().getType().toString().contains("SNOW")) {
				return new CheckResult("Impossible Break", true, "Ignored blocks, different breaking speeds.");
			}
			if (event.getBlock().getType() == Material.BAMBOO) {
				return new CheckResult("Impossible Break", true, "Ignored blocks, different breaking speeds.");
			}
			if (Bukkit.getPluginManager().getPlugin("GraviTree") != null
					&& event.getBlock().getType().toString().contains("LOG")) {
				return new CheckResult("Impossible Break", true, "Ignored blocks, GraviTree compatibility.");
			}
			if (Count > 9 && !VersionUtil.hasEfficiency(p)) {
				event.setCancelled(true);
				return new CheckResult("Fast Break", false, "Broke too Fast");
			}
			if (VersionUtil.hasEfficiency(p)) {
				return new CheckResult("Impossible Break", true, "Efficiency Effect");
			}
			Location placed = event.getBlock().getLocation();
			Block target = p.getTargetBlockExact(15);
			Boolean call = false;
			if (placed.distance(target.getLocation()) > 4.7) {
				call = true;
			}
			if (event.getBlock().getType() == Material.NETHERRACK
					|| event.getBlock().getType().toString().contains("BAMBOO")) {
				call = false;
			}
			if (call) {
				return new CheckResult("Impossible Break", false, "Broke out of LoS");
			} else {
				return new CheckResult("Impossible Break", true, "Pass");
			}
		} else {
			return new CheckResult("Impossible Break", true, "Pass");
		}
	}
}
