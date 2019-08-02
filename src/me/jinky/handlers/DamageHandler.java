package me.jinky.handlers;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;

import me.jinky.Cenix;
import me.jinky.util.MiniPlugin;

public class DamageHandler extends MiniPlugin {

	public DamageHandler(Cenix plugin) {
		super("Damage Handler", plugin);
	}

	@EventHandler
	public void onDamage(EntityDamageEvent event) {
		if (event.getEntity() instanceof Player)
			Cenix.getCenix().EXEMPTHANDLER.addExemption((Player) event.getEntity(), 845);
	}
}
