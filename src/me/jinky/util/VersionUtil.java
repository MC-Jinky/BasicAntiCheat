package me.jinky.util;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

public class VersionUtil {

	public static String getVersion() {
		return Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3];
	}

	public static boolean isFlying(Player p) {
		return p.isFlying() || p.isGliding() || p.hasPotionEffect(PotionEffectType.LEVITATION);
	}

	public static boolean isSwimming(Player p) {
		if (p.getLocation().getBlock().getType() == Material.WATER)
			return true;
		if (p.getLocation().getBlock().getRelative(BlockFace.DOWN).getType() == Material.WATER)
			return true;
		if (p.isSwimming())
			return true;
		if (UtilBlock.getSurroundingMat(p.getLocation().getBlock(), true).contains(Material.WATER))
			return true;

		return false;
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

}