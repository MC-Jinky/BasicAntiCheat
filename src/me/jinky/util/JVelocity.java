package me.jinky.util;

import org.bukkit.Location;

public class JVelocity {

	private Location f;
	private Location d;

	public JVelocity(Location f, Location d) {
		this.f = f;
		this.d = d;
	}

	public double yoffset() {
		Double ty = this.d.getY();
		Double fy = this.f.getY();

		return round(ty - fy, 3);
	}

	public double zoffset() {
		Double tz = this.d.getZ();
		Double fz = this.f.getZ();

		return round(tz - fz, 3);
	}

	public double xoffset() {
		Double tx = this.d.getX();
		Double fx = this.f.getX();
		return round(tx - fx, 3);
	}

	public double offset() {
		return round(f.distance(d), 3);
	}

	public static double round(double value, int places) {
		if (places < 0)
			throw new IllegalArgumentException();

		long factor = (long) Math.pow(10, places);
		value = value * factor;
		long tmp = Math.round(value);
		return (double) tmp / factor;
	}
}
