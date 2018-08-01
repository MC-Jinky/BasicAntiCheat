package me.jinky.raytrace;

import org.bukkit.craftbukkit.v1_12_R1.entity.CraftEntity;
import org.bukkit.entity.Entity;
import org.bukkit.util.Vector;

import net.minecraft.server.v1_12_R1.AxisAlignedBB;

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
		min = new Vector(bb.a, bb.b, bb.c);
		max = new Vector(bb.d, bb.e, bb.f);
	}

	BoundingBox(AxisAlignedBB bb) {
		min = new Vector(bb.a, bb.b, bb.c);
		max = new Vector(bb.d, bb.e, bb.f);
	}

	public Vector midPoint() {
		return max.clone().add(min).multiply(0.5);
	}

}
