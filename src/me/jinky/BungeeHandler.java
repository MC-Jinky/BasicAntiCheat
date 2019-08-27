package me.jinky;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

public class BungeeHandler implements PluginMessageListener {

	public static void load(BAC plugin) {
		Bukkit.getMessenger().registerOutgoingPluginChannel(plugin, "BAC:BACAlert".toLowerCase());
		Bukkit.getMessenger().registerOutgoingPluginChannel(plugin, "BAC:BACPunish".toLowerCase());
		Bukkit.getMessenger().registerIncomingPluginChannel(plugin, "BAC:BACAlert".toLowerCase(), new BungeeHandler());
	}

	private static void sbmsg(String msg, String ch) {
		ByteArrayOutputStream b = new ByteArrayOutputStream();
		DataOutputStream out = new DataOutputStream(b);
		try {
			out.writeUTF(msg);
		} catch (IOException e) {
			e.printStackTrace();
		}
		Bukkit.getServer().sendPluginMessage(BAC.getBAC(), ch.toLowerCase(), b.toByteArray());
	}

	public static void handleCrossAlert(String msg) {
		sbmsg(msg, "bac:bacalert");
	}

	public static boolean handleBungeePunish(String msg) {
		if (msg.toLowerCase().startsWith("bungeecord:")) {
			sbmsg(msg.replaceAll("bungeecord:", ""), "bac:bacpunish");
			return true;
		}
		return false;
	}

	@Override
	public synchronized void onPluginMessageReceived(String channel, Player player, byte[] message) {
		DataInputStream in = new DataInputStream(new ByteArrayInputStream(message));
		if (channel.equals("bac:bacalert")) {
			try {
				String msg = in.readUTF();
				BAC.getBAC().broadcast(msg);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}
}
