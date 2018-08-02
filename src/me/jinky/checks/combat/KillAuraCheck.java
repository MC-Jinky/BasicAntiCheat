package me.jinky.checks.combat;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.EnumWrappers.EntityUseAction;
import com.comphenix.protocol.wrappers.EnumWrappers.NativeGameMode;
import com.comphenix.protocol.wrappers.EnumWrappers.PlayerInfoAction;
import com.comphenix.protocol.wrappers.PlayerInfoData;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.comphenix.protocol.wrappers.WrappedGameProfile;

import me.jinky.Cenix;
import me.jinky.checks.Check;
import me.jinky.checks.CheckResult;
import me.jinky.logger.User;
import me.jinky.packets.WrapperPlayClientUseEntity;
import me.jinky.packets.WrapperPlayServerEntityDestroy;
import me.jinky.packets.WrapperPlayServerNamedEntitySpawn;
import me.jinky.packets.WrapperPlayServerPlayerInfo;
import me.jinky.util.NameGenerator;
import me.jinky.util.VersionUtil;

public class KillAuraCheck extends Check {

	private boolean registered = false;

	public class CheckInfo {

		private PlayerInfoData d;
		private int entID;

		public CheckInfo(PlayerInfoData d, int entID) {
			this.d = d;
			this.entID = entID;
		}

		public PlayerInfoData getPID() {
			return this.d;
		}

		public int getEID() {
			return this.entID;
		}
	}

	private static Map<Player, Long> lastCheck = new HashMap<Player, Long>();
	private static Map<Player, CheckInfo> curCheck = new HashMap<Player, CheckInfo>();

	@Override
	public String getEventCall() {
		return "EntityDamageByEntityEvent";
	}

	private void registerPacketListener() {
		ProtocolLibrary.getProtocolManager().addPacketListener(new PacketAdapter(Cenix.getCenix().getPlugin(),
				ListenerPriority.NORMAL, PacketType.Play.Client.USE_ENTITY) {
			@Override
			public void onPacketReceiving(PacketEvent event) {
				if (event.getPacketType() == PacketType.Play.Client.USE_ENTITY) {
					WrapperPlayClientUseEntity packet = new WrapperPlayClientUseEntity(event.getPacket());
					int entID = packet.getTarget();
					Player target = null;
					if (packet.getType() == EntityUseAction.ATTACK) {
						for (Entry<Player, CheckInfo> checks : curCheck.entrySet()) {
							if (checks.getValue().getEID() == entID) {
								Player p = checks.getKey();
								if (p != null && p.isOnline()) {
									target = p;
									event.setCancelled(true);
									Bukkit.getScheduler().runTask(Cenix.getCenix().getPlugin(), new Runnable() {
										@Override
										public void run() {
											/*
											 * Because of how packets and listeners work, I'm not using a callback
											 * method for this cause it's a pain, and won't make much of a difference,
											 * so we're just sticking with this way.
											 * 
											 * If someone wants to re-write this part with callback go ahead, but I
											 * don't have any plans on it
											 */
											Cenix.getCenix().addSuspicion(p, "Kill Aura");
										}
									});
									destroy(p);
								}
								break;
							}
						}
						if (target != null) {
							curCheck.remove(target);
							lastCheck.put(target, System.currentTimeMillis() - 1000);
						}
					}
				}
			}
		});
	}

	private static void destroy(Player p) {
		if (curCheck.containsKey(p)) {
			WrapperPlayServerEntityDestroy d = new WrapperPlayServerEntityDestroy();
			d.setEntityIds(new int[] { curCheck.get(p).getEID() });
			d.sendPacket(p);
		}
	}

	@Override
	public CheckResult performCheck(User u, Event ex) {
		if (!registered) {
			this.registerPacketListener();
			registered = true;
		}
		Player p = u.getPlayer();
		if (p.isBlocking()) {
			return new CheckResult("Impossible Fight (Combat while Blocking)", false);
		}
		if (p.isSleeping()) {
			return new CheckResult("Impossible Fight (Combat while Sleeping)", false);
		}
		if (p.isDead()) {
			return new CheckResult("Impossible Fight (Combat while Dead)", false);
		}
		if (!lastCheck.containsKey(p)) {
			lastCheck.put(p, System.currentTimeMillis() - 500);
		}

		long lastcheck = lastCheck.get(p);
		if ((System.currentTimeMillis() - lastcheck) > 5000) {
			Location location = p.getLocation().add(p.getLocation().getDirection().multiply(-2.75));
			UUID uuid = UUID.randomUUID();
			int entID = new Random().nextInt(950) + 150;
			String tname = NameGenerator.newName(p);
			Player tt = Bukkit.getPlayer(tname);
			if (tt != null && tt.isOnline()) {
				uuid = tt.getUniqueId();
			}
			WrapperPlayServerPlayerInfo i = new WrapperPlayServerPlayerInfo();
			WrapperPlayServerNamedEntitySpawn e = new WrapperPlayServerNamedEntitySpawn();
			PlayerInfoData d = new PlayerInfoData(new WrappedGameProfile(uuid, tname), entID, NativeGameMode.SURVIVAL,
					WrappedChatComponent.fromText(tname));
			WrapperPlayServerPlayerInfo ei = new WrapperPlayServerPlayerInfo();
			ei.setAction(PlayerInfoAction.REMOVE_PLAYER);
			ei.setData(Arrays.asList(new PlayerInfoData[] { d }));
			i.setData(Arrays.asList(new PlayerInfoData[] { d }));
			i.setAction(PlayerInfoAction.ADD_PLAYER);
			e.setPlayerUUID(uuid);
			e.setEntityID(entID);

			if (!VersionUtil.isPlus19()) {
				e.setX(location.getX());
				e.setY(location.getY());
				e.setZ(location.getZ());
			} else {
				e.setPosition(location.toVector());
			}
			i.sendPacket(p);
			e.sendPacket(p);
			ei.sendPacket(p);

			curCheck.put(p, new CheckInfo(d, entID));
			lastCheck.put(p, System.currentTimeMillis() + 500);
			Bukkit.getScheduler().runTaskLater(Cenix.getCenix().getPlugin(), new Runnable() {

				@Override
				public void run() {
					if (p != null && p.isOnline() && curCheck.containsKey(p)) {
						destroy(p);
					}
				}
			}, 5L);
		}
		return new CheckResult("Kill Aura", true);
	}

}
