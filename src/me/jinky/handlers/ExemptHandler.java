package me.jinky.handlers;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.entity.Player;

import me.jinky.BAC;
import me.jinky.util.MiniPlugin;

public class ExemptHandler extends MiniPlugin {

	public ExemptHandler(BAC plugin) {
		super("Exemption Handler", plugin);
	}

	private static Map<Player, Long> EXEMPT = new HashMap<Player, Long>();
	private static Map<Player, Long> EXEMPT_BLOCK = new HashMap<Player, Long>();
	private static Map<Player, String> EXEMPT_REASON = new HashMap<Player, String>();

	public boolean isExemptBlock(Player p) {
		if (EXEMPT_BLOCK.containsKey(p)) {
			if (System.currentTimeMillis() < EXEMPT_BLOCK.get(p)) {
				return true;
			} else {
				EXEMPT_BLOCK.remove(p);
				return false;
			}
		}
		return false;
	}

	public void removeExemptionBlock(Player p) {
		if (EXEMPT_BLOCK.containsKey(p))
			EXEMPT_BLOCK.remove(p);
	}

	public void addExemptionBlock(Player p, int ms) {
		if (isExempt(p))
			removeExemption(p);

		EXEMPT_BLOCK.put(p, System.currentTimeMillis() + ms);
	}

	public boolean isExempt(Player p) {
		if (EXEMPT.containsKey(p)) {
			if (System.currentTimeMillis() < EXEMPT.get(p) && !isExemptBlock(p)) {
				return true;
			} else {
				EXEMPT.remove(p);

				if (EXEMPT_REASON.containsKey(p))
					EXEMPT_REASON.remove(p);
				return false;
			}
		}
		return false;
	}

	public void removeExemption(Player p) {
		if (EXEMPT.containsKey(p))
			EXEMPT.remove(p);
		if (EXEMPT_REASON.containsKey(p))
			EXEMPT_REASON.remove(p);
	}

	public String getExemptReason(Player p) {
		if (EXEMPT_REASON.containsKey(p)) {
			return EXEMPT_REASON.get(p);
		}
		return "unknown/notexempt";
	}

	public void addExemption(Player p, int ms, String s) {
		if (isExemptBlock(p)) {
			return;
		}
		if (isExempt(p))
			removeExemption(p);
		EXEMPT_REASON.put(p, s);
		EXEMPT.put(p, System.currentTimeMillis() + ms);

	}

}
