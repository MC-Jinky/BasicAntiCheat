package me.jinky.util;

import java.util.ArrayList;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Waterlogged;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.BlockIterator;

public class UtilBlock {

	public static boolean canReallySeeEntity(Player p, LivingEntity e) {
		BlockIterator bl = new BlockIterator(p, 7);
		boolean found = false;
		double md = 1;
		if (e.getType() == EntityType.WITHER) {
			md = 9;
		} else if (e.getType() == EntityType.ENDERMAN) {
			md = 5;
		} else {
			md = md + e.getEyeHeight();
		}
		while (bl.hasNext()) {
			found = true;
			double d = bl.next().getLocation().distanceSquared(e.getLocation());
			if (d <= md) {
				return true;
			}
		}
		bl = null;
		if (!found) {
			return true;
		}

		return false;

	}

	public static boolean isSwimming(Player p) {
		if (p.isSwimming()) {
			return true;
		}
		Material material = p.getLocation().getBlock().getType();
		BlockData blockData = p.getLocation().getBlock().getBlockData();
		if (material == Material.WATER || material == Material.LAVA
				|| blockData instanceof Waterlogged && ((Waterlogged) blockData).isWaterlogged()) {
			return true;
		}
		if (p.getLocation().getBlock().isLiquid()) {
			return true;
		}
		if (getSurroundingMat(p.getLocation().getBlock(), true).contains(Material.WATER)) {
			return true;
		}
		Material m = p.getLocation().getBlock().getType();
		if (m == Material.KELP || m == Material.KELP_PLANT || m == Material.SEAGRASS || m == Material.TALL_SEAGRASS) {
			return true;
		}
		return false;
	}

	public static boolean climbable(Block block) {
		Material m = block.getType();
		if (m == Material.VINE)
			return true;
		if (m == Material.LADDER)
			return true;
		if (m == Material.SCAFFOLDING)
			return true;
		return false;
	}

	public static ArrayList<Material> getSurroundingMat(Block block, boolean diagonals) {
		ArrayList<Material> blocks = new ArrayList<Material>();

		if (diagonals) {
			for (int x = -1; x <= 1; x++) {
				for (int y = -1; y <= 1; y++) {
					for (int z = -1; z <= 1; z++) {
						if ((x != 0) || (y != 0) || (z != 0)) {

							blocks.add(block.getRelative(x, y, z).getType());
						}
					}
				}
			}
		} else {
			blocks.add(block.getRelative(BlockFace.UP).getType());
			blocks.add(block.getRelative(BlockFace.DOWN).getType());
			blocks.add(block.getRelative(BlockFace.NORTH).getType());
			blocks.add(block.getRelative(BlockFace.SOUTH).getType());
			blocks.add(block.getRelative(BlockFace.EAST).getType());
			blocks.add(block.getRelative(BlockFace.WEST).getType());
		}

		return blocks;
	}

	public static ArrayList<Block> getSurrounding(Block block, boolean diagonals) {
		ArrayList<Block> blocks = new ArrayList<Block>();

		if (diagonals) {
			for (int x = -1; x <= 1; x++) {
				for (int y = -1; y <= 1; y++) {
					for (int z = -1; z <= 1; z++) {
						if ((x != 0) || (y != 0) || (z != 0)) {

							blocks.add(block.getRelative(x, y, z));
						}
					}
				}
			}
		} else {
			blocks.add(block.getRelative(BlockFace.UP));
			blocks.add(block.getRelative(BlockFace.DOWN));
			blocks.add(block.getRelative(BlockFace.NORTH));
			blocks.add(block.getRelative(BlockFace.SOUTH));
			blocks.add(block.getRelative(BlockFace.EAST));
			blocks.add(block.getRelative(BlockFace.WEST));
		}

		return blocks;
	}

	public static ArrayList<Block> xraysafe(Block block, Material ignoret) {
		ArrayList<Block> blocks = new ArrayList<Block>();
		ArrayList<Material> ignore = new ArrayList<Material>();
		ignore.add(Material.STONE);
		ignore.add(Material.EMERALD_ORE);
		ignore.add(Material.COAL_ORE);
		ignore.add(Material.REDSTONE_ORE);
		ignore.add(Material.IRON_ORE);
		ignore.add(Material.DIRT);
		ignore.add(Material.LAPIS_ORE);
		ignore.add(Material.GRAVEL);
		ignore.add(Material.WATER);
		ignore.add(Material.DIAMOND_ORE);
		ignore.add(Material.DIORITE);
		ignore.add(Material.GRANITE);
		ignore.add(Material.ANDESITE);
		ignore.add(Material.LAVA);

		for (int x = -1; x <= 1; x++) {
			for (int y = -1; y <= 1; y++) {
				for (int z = -1; z <= 1; z++) {
					if ((x != 0) || (y != 0) || (z != 0)) {

						if ((!ignore.contains(block.getRelative(x, y, z).getType())
								&& block.getRelative(x, y, z).getType() != ignoret)
								|| block.getRelative(x, y, z).getType() == Material.AIR
								|| block.getRelative(x, y, z).getType() == Material.CAVE_AIR) {
							blocks.add(block.getRelative(x, y, z));
						}
					}
				}
			}
		}
		return blocks;
	}

	public static ArrayList<Block> getSurroundingIgnoreAir(Block block, boolean diagonals) {
		ArrayList<Block> blocks = new ArrayList<Block>();

		if (diagonals) {
			for (int x = -1; x <= 1; x++) {
				for (int y = -1; y <= 1; y++) {
					for (int z = -1; z <= 1; z++) {
						if ((x != 0) || (y != 0) || (z != 0)) {

							if (block.getRelative(x, y, z).getType() != Material.AIR)
								blocks.add(block.getRelative(x, y, z));
						}
					}
				}
			}
		} else {
			if (block.getRelative(BlockFace.UP).getType() != Material.AIR)
				blocks.add(block.getRelative(BlockFace.UP));
			if (block.getRelative(BlockFace.DOWN).getType() != Material.AIR)
				blocks.add(block.getRelative(BlockFace.DOWN));
			if (block.getRelative(BlockFace.NORTH).getType() != Material.AIR)
				blocks.add(block.getRelative(BlockFace.NORTH));
			if (block.getRelative(BlockFace.SOUTH).getType() != Material.AIR)
				blocks.add(block.getRelative(BlockFace.SOUTH));
			if (block.getRelative(BlockFace.EAST).getType() != Material.AIR)
				blocks.add(block.getRelative(BlockFace.EAST));
			if (block.getRelative(BlockFace.WEST).getType() != Material.AIR)
				blocks.add(block.getRelative(BlockFace.WEST));
		}

		return blocks;
	}

	public static boolean contains(Block b, String meta) {
		return b.getType().toString().toLowerCase().contains(meta.toLowerCase());
	}

	public static Block getBlockAbove(Player p) {
		return p.getLocation().getBlock().getRelative(BlockFace.UP).getRelative(BlockFace.UP);
	}

	public static boolean onStairs(Player p) {
		String m = p.getLocation().getBlock().getType().toString().toLowerCase();
		String mu = p.getLocation().getBlock().getRelative(BlockFace.DOWN).getType().toString().toLowerCase();
		if (m.contains("stair") || mu.contains("stair")) {
			return true;
		} else {
			return false;
		}
	}

	public static boolean onBlock(Location loc) {
		double xMod = loc.getX() % 1.0D;
		if (loc.getX() < 0.0D) {
			xMod += 1.0D;
		}
		double zMod = loc.getZ() % 1.0D;
		if (loc.getZ() < 0.0D) {
			zMod += 1.0D;
		}
		int xMin = 0;
		int xMax = 0;
		int zMin = 0;
		int zMax = 0;

		if (xMod < 0.3D)
			xMin = -1;
		if (xMod > 0.7D) {
			xMax = 1;
		}
		if (zMod < 0.3D)
			zMin = -1;
		if (zMod > 0.7D) {
			zMax = 1;
		}

		for (int x = xMin; x <= xMax; x++) {
			for (int z = zMin; z <= zMax; z++) {
				if (loc.add(x, 0, z).getBlock().getType() == Material.LILY_PAD) {
					return true;
				}
				if ((loc.add(x, -0.5D, z).getBlock().getType() != Material.AIR)
						&& (!loc.add(x, -0.5D, z).getBlock().isLiquid())) {
					return true;
				}

				Material beneath = loc.add(x, -1.5D, z).getBlock().getType();
				if ((loc.getY() % 0.5D == 0.0D) && (beneath.toString().toLowerCase().contains("fence")
						|| (beneath.toString().toLowerCase().contains("rod")
								|| beneath.toString().toLowerCase().contains("bamboo")
								|| beneath.toString().toLowerCase().contains("wall")))) {
					return true;
				}
			}
		}
		return false;
	}

	public static boolean onBlock(Player arg0) {
		return onBlock(arg0.getLocation());
	}
}
