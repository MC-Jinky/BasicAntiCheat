package me.jinky.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.BlockIterator;
import org.bukkit.util.NumberConversions;
import org.bukkit.util.Vector;

@SuppressWarnings("deprecation")
public final class Utilities {
	private static final List<Material> INSTANT_BREAK = new ArrayList<Material>();
	private static final List<Material> FOOD = new ArrayList<Material>();
	private static final List<Material> INTERACTABLE = new ArrayList<Material>();

	private static List<Material> unsolidMaterials;
	private static List<Material> stepableMaterials;

	public static boolean cantStandAtBetter(Block block) {
		Block otherBlock = block.getRelative(BlockFace.DOWN);

		boolean center1 = (otherBlock.getType() == Material.AIR);
		boolean north1 = (otherBlock.getRelative(BlockFace.NORTH).getType() == Material.AIR);
		boolean east1 = (otherBlock.getRelative(BlockFace.EAST).getType() == Material.AIR);
		boolean south1 = (otherBlock.getRelative(BlockFace.SOUTH).getType() == Material.AIR);
		boolean west1 = (otherBlock.getRelative(BlockFace.WEST).getType() == Material.AIR);
		boolean northeast1 = (otherBlock.getRelative(BlockFace.NORTH_EAST).getType() == Material.AIR);
		boolean northwest1 = (otherBlock.getRelative(BlockFace.NORTH_WEST).getType() == Material.AIR);
		boolean southeast1 = (otherBlock.getRelative(BlockFace.SOUTH_EAST).getType() == Material.AIR);
		boolean southwest1 = (otherBlock.getRelative(BlockFace.SOUTH_WEST).getType() == Material.AIR);
		boolean overAir1 = !(otherBlock.getRelative(BlockFace.DOWN).getType() != Material.AIR
				&& otherBlock.getRelative(BlockFace.DOWN).getType() != Material.WATER
				&& otherBlock.getRelative(BlockFace.DOWN).getType() != Material.LAVA);

		return (center1 && north1 && east1 && south1 && west1 && northeast1 && southeast1 && northwest1 && southwest1
				&& overAir1);
	}

	public static boolean cantStandAtSingle(Block block) {
		Block otherBlock = block.getRelative(BlockFace.DOWN);
		return (otherBlock.getType() == Material.AIR);
	}

	public static boolean cantStandAtWater(Block block) {
		Block otherBlock = block.getRelative(BlockFace.DOWN);
		boolean isHover = (block.getType() == Material.AIR);
		boolean n = (otherBlock.getRelative(BlockFace.NORTH).getType() == Material.WATER);
		boolean s = (otherBlock.getRelative(BlockFace.SOUTH).getType() == Material.WATER);
		boolean e = (otherBlock.getRelative(BlockFace.EAST).getType() == Material.WATER);
		boolean w = (otherBlock.getRelative(BlockFace.WEST).getType() == Material.WATER);
		boolean ne = (otherBlock.getRelative(BlockFace.NORTH_EAST).getType() == Material.WATER);
		boolean nw = (otherBlock.getRelative(BlockFace.NORTH_WEST).getType() == Material.WATER);
		boolean se = (otherBlock.getRelative(BlockFace.SOUTH_EAST).getType() == Material.WATER);
		boolean sw = (otherBlock.getRelative(BlockFace.SOUTH_WEST).getType() == Material.WATER);
		return (n && s && e && w && ne && nw && se && sw && isHover);
	}

	public static boolean canStandWithin(Block block) {
		boolean isSand = (block.getType() == Material.SAND);
		boolean isGravel = (block.getType() == Material.GRAVEL);
		boolean solid = (block.getType().isSolid() && !block.getType().name().toLowerCase().contains("door")
				&& !block.getType().name().toLowerCase().contains("fence")
				&& !block.getType().name().toLowerCase().contains("bars")
				&& !block.getType().name().toLowerCase().contains("sign"));

		return (!isSand && !isGravel && !solid);
	}

	public static Vector getRotation(Location one, Location two) {
		double dx = two.getX() - one.getX();
		double dy = two.getY() - one.getY();
		double dz = two.getZ() - one.getZ();
		double distanceXZ = Math.sqrt(dx * dx + dz * dz);
		float yaw = (float) (Math.atan2(dz, dx) * 180.0D / Math.PI) - 90.0F;
		float pitch = (float) -(Math.atan2(dy, distanceXZ) * 180.0D / Math.PI);
		return new Vector(yaw, pitch, 0.0F);
	}

	public static double clamp180(double theta) {
		theta %= 360.0D;
		if (theta >= 180.0D)
			theta -= 360.0D;
		if (theta < -180.0D)
			theta += 360.0D;
		return theta;
	}

	public static boolean cantStandAt(Block block) {
		return (!canStand(block) && cantStandClose(block) && cantStandFar(block));
	}

	public static boolean cantStandAtExp(Location location) {
		return cantStandAt((new Location(location.getWorld(), fixXAxis(location.getX()), location.getY() - 0.01D,
				location.getBlockZ())).getBlock());
	}

	public static boolean cantStandClose(Block block) {
		return (!canStand(block.getRelative(BlockFace.NORTH)) && !canStand(block.getRelative(BlockFace.EAST))
				&& !canStand(block.getRelative(BlockFace.SOUTH)) && !canStand(block.getRelative(BlockFace.WEST)));
	}

	public static boolean cantStandFar(Block block) {
		return (!canStand(block.getRelative(BlockFace.NORTH_WEST)) && !canStand(block.getRelative(BlockFace.NORTH_EAST))
				&& !canStand(block.getRelative(BlockFace.SOUTH_WEST))
				&& !canStand(block.getRelative(BlockFace.SOUTH_EAST)));
	}

	public static boolean canStand(Block block) {
		return (!block.isLiquid() && block.getType() != Material.AIR);
	}

	public static boolean isFullyInWater(Location player) {
		double touchedX = fixXAxis(player.getX());

		if (!(new Location(player.getWorld(), touchedX, player.getY(), player.getBlockZ())).getBlock().isLiquid()
				&& !(new Location(player.getWorld(), touchedX, Math.round(player.getY()), player.getBlockZ()))
						.getBlock().isLiquid()) {
			return true;
		}

		return ((new Location(player.getWorld(), touchedX, player.getY(), player.getBlockZ())).getBlock().isLiquid()
				&& (new Location(player.getWorld(), touchedX, Math.round(player.getY()), player.getBlockZ())).getBlock()
						.isLiquid());
	}

	public static double fixXAxis(double x) {
		double touchedX = x;
		double rem = touchedX - Math.round(touchedX) + 0.01D;
		if (rem < 0.3D) {
			touchedX = (NumberConversions.floor(x) - 1);
		}
		return touchedX;
	}

	public static boolean isHoveringOverWater(Location player, int blocks) {
		for (int i = player.getBlockY(); i > player.getBlockY() - blocks; i--) {
			Block newloc = (new Location(player.getWorld(), player.getBlockX(), i, player.getBlockZ())).getBlock();
			if (newloc.getType() != Material.AIR) {
				return newloc.isLiquid();
			}
		}

		return false;
	}

	public static boolean isHoveringOverWater(Location player) {
		return isHoveringOverWater(player, 25);
	}

	public static boolean isInstantBreak(Material m) {
		return INSTANT_BREAK.contains(m);
	}

	public static boolean isFood(Material m) {
		return FOOD.contains(m);
	}

	public static boolean isInteractable(Material m) {
		if (m == null)
			return false;

		return INTERACTABLE.contains(m);
	}

	public static boolean sprintFly(Player player) {
		return !(!player.isSprinting() && !player.isFlying());
	}

	public static boolean isOnLilyPad(Player player) {
		Block block = player.getLocation().getBlock();
		Material lily = Material.LILY_PAD;

		return !(block.getType() != lily && block.getRelative(BlockFace.NORTH).getType() != lily
				&& block.getRelative(BlockFace.SOUTH).getType() != lily
				&& block.getRelative(BlockFace.EAST).getType() != lily
				&& block.getRelative(BlockFace.WEST).getType() != lily);
	}

	public static boolean isSubmersed(Player player) {
		return (player.getLocation().getBlock().isLiquid()
				&& player.getLocation().getBlock().getRelative(BlockFace.UP).isLiquid());
	}

	public static boolean isInWater(Player player) {
		return !(!player.getLocation().getBlock().isLiquid()
				&& !player.getLocation().getBlock().getRelative(BlockFace.DOWN).isLiquid()
				&& !player.getLocation().getBlock().getRelative(BlockFace.UP).isLiquid());
	}

	public static boolean isInWeb(Player player) {
		return !(player.getLocation().getBlock().getType() != Material.COBWEB
				&& player.getLocation().getBlock().getRelative(BlockFace.DOWN).getType() != Material.COBWEB
				&& player.getLocation().getBlock().getRelative(BlockFace.UP).getType() != Material.COBWEB);
	}

	public static boolean isClimbableBlock(Block block) {
		return !(block.getType() != Material.VINE && block.getType() != Material.LADDER
				&& block.getType() != Material.WATER && block.getType() != Material.WATER);
	}

	public static boolean isOnVine(Player player) {
		return (player.getLocation().getBlock().getType() == Material.VINE);
	}

	public static boolean isPlayerOnGround(Player p) {
		return isPlayerLocationOnGround(p);
	}

	public static boolean isLocationOnGround(Location loc) {
		List<Material> materials = getMaterialsAround(loc.clone().add(0.0D, -0.001D, 0.0D));
		for (Material m : materials) {
			if (!isUnsolid(m) && m != Material.WATER && m != Material.WATER && m != Material.LAVA && m != Material.LAVA)
				return true;
		}
		return false;
	}

	public static boolean isPlayerLocationOnGround(Player p) {
		return isLocationOnGround(p.getLocation());
	}

	public static boolean isUnderBlock(Player p) {
		Block blockAbove = p.getEyeLocation().getBlock().getRelative(BlockFace.UP);
		return (blockAbove != null && !isUnsolid(blockAbove));
	}

	public static boolean isOnIce(Player p, boolean strict) {
		if (isPlayerOnGround(p) || strict) {

			List<Material> materials = getMaterialsAround(p.getLocation().clone().add(0.0D, -0.001D, 0.0D));
			return !(!materials.contains(Material.ICE) && !materials.contains(Material.PACKED_ICE));
		}

		List<Material> m1 = getMaterialsAround(p.getLocation().clone().add(0.0D, -1.0D, 0.0D));
		List<Material> m2 = getMaterialsAround(p.getLocation().clone().add(0.0D, -2.0D, 0.0D));
		return !(!m1.contains(Material.ICE) && !m1.contains(Material.PACKED_ICE) && !m2.contains(Material.ICE)
				&& !m2.contains(Material.PACKED_ICE));
	}

	public static boolean isOnSteps(Player p) {
		List<Material> materials = getMaterialsAround(p.getLocation().clone().add(0.0D, -0.001D, 0.0D));
		for (Material m : materials) {
			if (isStepable(m))
				return true;
		}
		return false;
	}

	public static boolean isGlidingWithElytra(Player p) {
		ItemStack chestplate = p.getInventory().getChestplate();
		return (p.isGliding() && chestplate != null && chestplate.getType() == Material.ELYTRA);
	}

	public static List<Material> getMaterialsAround(Location loc) {
		List<Material> result = new ArrayList<Material>();
		result.add(loc.getBlock().getType());
		result.add(loc.clone().add(0.3D, 0.0D, -0.3D).getBlock().getType());
		result.add(loc.clone().add(-0.3D, 0.0D, -0.3D).getBlock().getType());
		result.add(loc.clone().add(0.3D, 0.0D, 0.3D).getBlock().getType());
		result.add(loc.clone().add(-0.3D, 0.0D, 0.3D).getBlock().getType());
		return result;
	}

	public static boolean isAround(Location loc, Material mat) {
		Block blockDown = loc.getBlock().getRelative(BlockFace.DOWN);

		ArrayList<Material> materials = new ArrayList<Material>();
		materials.add(blockDown.getType());
		materials.add(blockDown.getRelative(BlockFace.NORTH).getType());
		materials.add(blockDown.getRelative(BlockFace.NORTH_EAST).getType());
		materials.add(blockDown.getRelative(BlockFace.EAST).getType());
		materials.add(blockDown.getRelative(BlockFace.SOUTH_EAST).getType());
		materials.add(blockDown.getRelative(BlockFace.SOUTH).getType());
		materials.add(blockDown.getRelative(BlockFace.SOUTH_WEST).getType());
		materials.add(blockDown.getRelative(BlockFace.WEST).getType());
		materials.add(blockDown.getRelative(BlockFace.NORTH_WEST).getType());

		Block blockDown2 = loc.getBlock().getRelative(BlockFace.DOWN, 2);
		materials.add(blockDown2.getType());
		materials.add(blockDown2.getRelative(BlockFace.NORTH).getType());
		materials.add(blockDown2.getRelative(BlockFace.NORTH_EAST).getType());
		materials.add(blockDown2.getRelative(BlockFace.EAST).getType());
		materials.add(blockDown2.getRelative(BlockFace.SOUTH_EAST).getType());
		materials.add(blockDown2.getRelative(BlockFace.SOUTH).getType());
		materials.add(blockDown2.getRelative(BlockFace.SOUTH_WEST).getType());
		materials.add(blockDown2.getRelative(BlockFace.WEST).getType());
		materials.add(blockDown2.getRelative(BlockFace.NORTH_WEST).getType());

		Block block = loc.getBlock();
		materials.add(block.getType());
		materials.add(block.getRelative(BlockFace.NORTH).getType());
		materials.add(block.getRelative(BlockFace.NORTH_EAST).getType());
		materials.add(block.getRelative(BlockFace.EAST).getType());
		materials.add(block.getRelative(BlockFace.SOUTH_EAST).getType());
		materials.add(block.getRelative(BlockFace.SOUTH).getType());
		materials.add(block.getRelative(BlockFace.SOUTH_WEST).getType());
		materials.add(block.getRelative(BlockFace.WEST).getType());
		materials.add(block.getRelative(BlockFace.NORTH_WEST).getType());

		Block blockUp = loc.getBlock().getRelative(BlockFace.UP);
		materials.add(blockUp.getType());
		materials.add(blockUp.getRelative(BlockFace.NORTH).getType());
		materials.add(blockUp.getRelative(BlockFace.NORTH_EAST).getType());
		materials.add(blockUp.getRelative(BlockFace.EAST).getType());
		materials.add(blockUp.getRelative(BlockFace.SOUTH_EAST).getType());
		materials.add(blockUp.getRelative(BlockFace.SOUTH).getType());
		materials.add(blockUp.getRelative(BlockFace.SOUTH_WEST).getType());
		materials.add(blockUp.getRelative(BlockFace.WEST).getType());
		materials.add(blockUp.getRelative(BlockFace.NORTH_WEST).getType());

		for (Material m : materials) {
			if (m != mat)
				return false;
		}
		return true;
	}

	public static Location getPlayerStandOnBlockLocation(Location locationUnderPlayer, Material mat) {
		Location b11 = locationUnderPlayer.clone().add(0.3D, 0.0D, -0.3D);
		if (b11.getBlock().getType() != mat) {
			return b11;
		}
		Location b12 = locationUnderPlayer.clone().add(-0.3D, 0.0D, -0.3D);
		if (b12.getBlock().getType() != mat) {
			return b12;
		}
		Location b21 = locationUnderPlayer.clone().add(0.3D, 0.0D, 0.3D);
		if (b21.getBlock().getType() != mat) {
			return b21;
		}
		Location b22 = locationUnderPlayer.clone().add(-0.3D, 0.0D, 0.3D);
		if (b22.getBlock().getType() != mat) {
			return b22;
		}
		return locationUnderPlayer;
	}

	public static boolean isInBlock(Player p, Material block) {
		Location loc = p.getLocation().add(0.0D, 0.0D, 0.0D);
		return (getPlayerStandOnBlockLocation(loc, block).getBlock().getType() == block);
	}

	public static boolean isOnWater(Player p, double depth) {
		Location loc = p.getLocation().subtract(0.0D, depth, 0.0D);
		return !(getPlayerStandOnBlockLocation(loc, Material.WATER).getBlock().getType() != Material.WATER);
	}

	public static boolean isOnEntity(Player p, EntityType type) {
		for (Entity e : p.getWorld().getNearbyEntities(p.getLocation(), 1.0D, 1.0D, 1.0D)) {
			if (e.getType() == type && e.getLocation().getY() < p.getLocation().getY())
				return true;
		}
		return false;
	}

	public static List<Material> getUnsolidMaterials() {
		return unsolidMaterials;
	}

	public static boolean isUnsolid(Material m) {
		return getUnsolidMaterials().contains(m);
	}

	public static boolean isUnsolid(Block b) {
		return isUnsolid(b.getType());
	}

	public static List<Material> getStepableMaterials() {
		return stepableMaterials;
	}

	public static boolean isStepable(Material m) {
		return getStepableMaterials().contains(m);
	}

	public static boolean isStepable(Block b) {
		return isStepable(b.getType());
	}

	public static boolean canReallySeeEntity(Player p, LivingEntity e) {
		BlockIterator bl = new BlockIterator(p, 7);

		boolean found = false;

		double md = 3.25D;

		if (e.getType() == EntityType.WITHER) {

			md = 9.0D;
		} else if (e.getType() == EntityType.ENDERMAN) {

			md = 5.0D;
		} else {

			md += e.getEyeHeight();
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

	public static LivingEntity getTarget(Player player) {
		int range = 8;
		ArrayList<LivingEntity> livingE = new ArrayList<LivingEntity>();

		for (Entity e : player.getNearbyEntities(range, range, range)) {
			if (e instanceof LivingEntity) {
				livingE.add((LivingEntity) e);
			}
		}

		LivingEntity target = null;
		BlockIterator bItr = new BlockIterator(player, range);

		double md = Double.MAX_VALUE;

		while (bItr.hasNext()) {
			Block block = bItr.next();
			int bx = block.getX();
			int by = block.getY();
			int bz = block.getZ();

			for (LivingEntity e : livingE) {
				Location loc = e.getLocation();
				double ex = loc.getX();
				double ey = loc.getY();
				double ez = loc.getZ();
				double d = loc.distanceSquared(player.getLocation());
				if (e.getType() == EntityType.HORSE) {

					if (bx - 1.2D <= ex && ex <= bx + 2.2D && bz - 1.2D <= ez && ez <= bz + 2.2D && by - 2.5D <= ey
							&& ey <= by + 4.5D && d < md) {
						md = d;
						target = e;
					}

					continue;
				}

				if (bx - 0.8D <= ex && ex <= bx + 1.85D && bz - 0.8D <= ez && ez <= bz + 1.85D && by - 2.5D <= ey
						&& ey <= by + 4.5D && d < md) {
					md = d;
					target = e;
				}
			}
		}

		livingE.clear();
		return target;
	}

	static {

		unsolidMaterials = Arrays.asList(new Material[] { Material.AIR, Material.LEGACY_SIGN, Material.LEGACY_SIGN_POST,
				Material.TRIPWIRE, Material.TRIPWIRE_HOOK, Material.LEGACY_SUGAR_CANE_BLOCK, Material.LEGACY_LONG_GRASS,
				Material.FLOWER_POT, Material.LEGACY_YELLOW_FLOWER });
		stepableMaterials = Arrays.asList(new Material[] { Material.LEGACY_STEP, Material.ACACIA_STAIRS,
				Material.BIRCH_STAIRS, Material.BIRCH_STAIRS, Material.BRICK_STAIRS, Material.COBBLESTONE_STAIRS,
				Material.DARK_OAK_STAIRS, Material.JUNGLE_STAIRS, Material.NETHER_BRICK_STAIRS, Material.QUARTZ_STAIRS,
				Material.RED_SANDSTONE_STAIRS, Material.SANDSTONE_STAIRS, Material.LEGACY_SMOOTH_STAIRS,
				Material.SPRUCE_STAIRS, Material.OAK_STAIRS, Material.STONE_SLAB });
	}
}
