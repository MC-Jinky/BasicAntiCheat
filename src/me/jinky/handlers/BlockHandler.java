package me.jinky.handlers;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

import me.jinky.BAC;
import me.jinky.Settings;
import me.jinky.checks.Check;
import me.jinky.checks.CheckResult;
import me.jinky.util.MiniPlugin;

public class BlockHandler extends MiniPlugin {

	public BlockHandler(BAC plugin) {
		super("Block Handler", plugin);
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onPlace(BlockPlaceEvent event) {
		if (Settings.ENABLED == false) {
			return;
		}
		for (Check c : this.getPlugin().All_Checks) {
			if (c.getEventCall().equals(event.getEventName())
					|| c.getSecondaryEventCall().equals(event.getEventName())) {
				CheckResult result = c.performCheck(this.getPlugin().getUser(event.getPlayer()), event);
				if (!result.passed()) {
					this.getPlugin().addSuspicion(event.getPlayer(), result.getCheckName());
				}
			}
		}
	}

	@EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
	public void onBreak(BlockBreakEvent event) {
		if (Settings.ENABLED == false) {
			return;
		}
		for (Check c : this.getPlugin().All_Checks) {
			if (c.getEventCall().equals(event.getEventName())
					|| c.getSecondaryEventCall().equals(event.getEventName())) {
				try {
					CheckResult result = c.performCheck(this.getPlugin().getUser(event.getPlayer()), event);
					if (!result.passed()) {
						this.getPlugin().addSuspicion(event.getPlayer(), result.getCheckName());
					}
				} catch (Exception e) {

				}
			}
		}
	}
}
