package com.alexogden.event;

import com.alexogden.core.SCAutoBackup;
import com.alexogden.core.ServerTask;
import com.alexogden.core.logging.MessageLogger;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.logging.Level;

public class PlayerEventListener implements Listener {

	private final SCAutoBackup plugin;

	public PlayerEventListener(SCAutoBackup plugin) {
		this.plugin = plugin;
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		ServerTask backupTask = plugin.getBackupTask();
		boolean isBackupPaused = backupTask.isPaused();
		boolean isAutoPauseEnabled = plugin.getConfig().getBoolean("backup.pause-on-empty-server");

		if (isBackupPaused && isAutoPauseEnabled) {
			backupTask.resume();
			MessageLogger.sendConsoleMessage(Level.INFO, "Auto Backup Resumed!");
		}
	}

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		Bukkit.getScheduler().runTaskLater(plugin, () -> {
			ServerTask backupTask = plugin.getBackupTask();
			boolean isServerEmpty = Bukkit.getServer().getOnlinePlayers().isEmpty();
			boolean isAutoPauseEnabled = plugin.getConfig().getBoolean("backup.pause-on-empty-server");

			if (isServerEmpty && isAutoPauseEnabled) {
				MessageLogger.sendConsoleMessage(Level.INFO, "Server is empty, triggering backup and pausing auto backup.");
				backupTask.run();
				backupTask.pause();
			}
		}, 40L); // 2-Second Delay
	}
}

