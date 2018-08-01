package me.jinky.util;

import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class NameGenerator {
	public NameGenerator() {
	}

	private static final java.util.List<Alphabet> letters = java.util.Collections
			.unmodifiableList(java.util.Arrays.asList(Alphabet.values()));
	private static Random rand = new Random();

	public static String newName(Player target) {
		String name = null;

		if (Bukkit.getOnlinePlayers().size() > 1) {
			for (Player p : Bukkit.getOnlinePlayers()) {
				if (p != target) {
					if (p.getWorld() != target.getWorld() || p.getLocation().distance(target.getLocation()) > 30) {
						name = p.getName();
						break;
					}
				}
			}
		}
		if (name != null) {
			return name;
		}
		int size = 3 + rand.nextInt(4);

		StringBuilder stringBuilder = new StringBuilder();
		while (size > 0) {
			size--;
			stringBuilder.append(getRandomLetter());
		}
		stringBuilder.append(rand.nextInt(999999));

		return stringBuilder.toString();
	}

	private static String getRandomLetter() {
		return letters.get(rand.nextInt(letters.size())).name();
	}

	private static enum Alphabet {
		a, b, c, d, e, f, g, h, i, j, k, l, m, n, o, p, q, r, s, t, u, v, w, x, y, z;

		private Alphabet() {
		}
	}
}