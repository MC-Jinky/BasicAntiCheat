package me.jinky.util;

import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.scheduler.BukkitScheduler;

import me.jinky.Cenix;

public abstract class MiniPlugin implements Listener {

	protected String _moduleName = "Default";

	protected Cenix _plugin;

	public MiniPlugin(String moduleName, Cenix plugin) {
		this._moduleName = moduleName;
		this._plugin = plugin;
		onEnable();

		registerEvents(this);
	}

	public void runASync(Runnable r) {
		Bukkit.getScheduler().runTaskAsynchronously(this._plugin, r);
	}

	public PluginManager getPluginManager() {
		return this._plugin.getServer().getPluginManager();
	}

	public BukkitScheduler getScheduler() {
		return this._plugin.getServer().getScheduler();
	}

	public Cenix getPlugin() {
		return this._plugin;
	}

	public Server getServer() {
		return this._plugin.getServer();
	}

	public void registerEvents(Listener listener) {
		this._plugin.getServer().getPluginManager().registerEvents(listener, this._plugin);
	}

	public final void onEnable() {
		addCommands();
		log("§aEnabled Module: " + this.getName());
	}

	public final void onDisable() {
		log("§cDisabled Module: " + this.getName());
	}

	public void addCommands() {
	}

	public final String getName() {
		return this._moduleName;
	}

	protected void log(String message) {
		this.getPlugin().logFile(message);
	}
}