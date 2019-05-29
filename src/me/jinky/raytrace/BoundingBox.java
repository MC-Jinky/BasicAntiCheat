package me.jinky.raytrace;

import org.bukkit.craftbukkit.v1_13_R2.entity.CraftEntity;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;

import net.minecraft.server.v1_13_R2.AxisAlignedBB;

public class BoundingBox {

	// min and max points of hit box
	Vector max;
	Vector min;

	BoundingBox(Vector min, Vector max) {
		this.max = max;
		this.min = min;
	}

	BoundingBox(Entity entity) {
		AxisAlignedBB bb = ((CraftEntity) entity).getHandle().getBoundingBox();
		min = new Vector(bb.minX, bb.minY, bb.minZ);
		max = new Vector(bb.maxX, bb.maxY, bb.maxZ);
	}

	BoundingBox(AxisAlignedBB bb) {
		min = new Vector(bb.minX, bb.minY, bb.minZ);
		max = new Vector(bb.maxX, bb.maxY, bb.maxZ);
	}

	public Vector midPoint() {
		return max.clone().add(min).multiply(0.5);
	}

}
