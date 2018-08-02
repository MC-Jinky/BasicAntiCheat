package me.jinky.util;

import java.util.EnumSet;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

public class VersionUtil {

	public static String getVersion() {
		return Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3];
	}

	private static final EnumSet<Material> MOVE_UP_BLOCKS_1_8 = EnumSet.of(Material.ACACIA_STAIRS,
			Material.BIRCH_WOOD_STAIRS, Material.BRICK_STAIRS, Material.COBBLESTONE_STAIRS, Material.DARK_OAK_STAIRS,
			Material.JUNGLE_WOOD_STAIRS, Material.NETHER_BRICK_STAIRS, Material.QUARTZ_STAIRS,
			Material.RED_SANDSTONE_STAIRS, Material.SANDSTONE_STAIRS, Material.SMOOTH_STAIRS,
			Material.SPRUCE_WOOD_STAIRS, Material.WOOD_STAIRS);

	public static boolean isFlying(Player p) {
		if (isPlus19()) {
			return p.isFlying() || p.isGliding() || p.hasPotionEffect(NMS_1_9_PLUS.LEVITATION);
		} else {
			return p.isFlying();
		}
	}

	@SuppressWarnings("deprecation")
	public static boolean hasEfficiency(Player player) {
		if (player.hasPotionEffect(PotionEffectType.FAST_DIGGING)
				&& player.getPotionEffect(PotionEffectType.FAST_DIGGING).getAmplifier() > 3) {
			return true;
		}
		if (player.getItemInHand() != null) {
			if (player.getItemInHand().containsEnchantment(Enchantment.DIG_SPEED)) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	public static boolean isPlus19() {
		if (getVersion().startsWith("v1_9") || getVersion().startsWith("v1_10") || getVersion().startsWith("v1_11")
				|| getVersion().startsWith("v1_12")) {
			return true;
		} else {
			return false;
		}
	}

	public static EnumSet<Material> getMoveUpBlocks() {
		if (getVersion().startsWith("v1_9") || getVersion().startsWith("v1_10") || getVersion().startsWith("v1_11")
				|| getVersion().startsWith("v1_12")) {
			return NMS_1_9_PLUS.MOVE_UP_BLOCKS_1_9;
		} else {
			return MOVE_UP_BLOCKS_1_8;
		}
	}

	public static boolean isNewYSpeed() {
		if (getVersion().startsWith("v1_9") || getVersion().startsWith("v1_10") || getVersion().startsWith("v1_11")
				|| getVersion().startsWith("v1_12")) {
			return true;
		} else {
			return false;
		}
	}

	public static long getHealTime() {
		if (getVersion().startsWith("v1_9") || getVersion().startsWith("v1_10") || getVersion().startsWith("v1_11")
				|| getVersion().startsWith("v1_12")) {
			return 495;
		} else {
			return 1995;
		}
	}

	public static boolean isFrostWalk(Player player) {
		if (getVersion().startsWith("v1_9") || getVersion().startsWith("v1_10") || getVersion().startsWith("v1_11")
				|| getVersion().startsWith("v1_12")) {
			return player.getInventory().getBoots().containsEnchantment(NMS_1_9_PLUS.FROST_WALKER);
		} else {
			return false;
		}
	}

}