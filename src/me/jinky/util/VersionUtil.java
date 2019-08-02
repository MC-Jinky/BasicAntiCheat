package me.jinky.util;

import org.bukkit.Bukkit;
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