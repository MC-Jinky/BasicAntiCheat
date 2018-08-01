package me.jinky.util;

import java.text.DecimalFormat;
import java.util.Random;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;

public class UtilMath {
	public static double trim(int degree, double d) {
		String format = "#.#";

		for (int i = 1; i < degree; i++) {
			format = format + "#";
		}
		DecimalFormat twoDForm = new DecimalFormat(format);
		return Double.valueOf(twoDForm.format(d)).doubleValue();
	}

	public static Random random = new Random();

	public static int r(int i) {
		return random.nextInt(i);
	}

	public static int getRandom(int low, int high) {
		return low + (int) (Math.random() * ((high - low) + 1));
	}

	public static double offset2d(Entity a, Entity b) {
		return offset2d(a.getLocation().toVector(), b.getLocation().toVector());
	}

	public static double offset2d(Location a, Location b) {
		return offset2d(a.toVector(), b.toVector());
	}

	public static double offset2d(Vector a, Vector b) {
		a.setY(0);
		b.setY(0);
		return a.subtract(b).length();
	}

	public static double offset(Entity a, Entity b) {
		return offset(a.getLocation().toVector(), b.getLocation().toVector());
	}

	public static double offset(Location a, Location b) {
		return offset(a.toVector(), b.toVector());
	}

	public static double offset(Vector a, Vector b) {
		return a.subtract(b).length();
	}
}