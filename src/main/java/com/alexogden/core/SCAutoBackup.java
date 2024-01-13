package com.alexogden.core;


import com.alexogden.backup.BackupTask;
import com.alexogden.command.CmdTabCompleter;
import com.alexogden.command.CommandHandler;
import com.alexogden.core.logging.MessageLogger;
import com.alexogden.event.PlayerEventListener;
import com.alexogden.save.SaveTask;
import com.alexogden.util.TimeUtil;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SCAutoBackup extends JavaPlugin {
	private static SCAutoBackup instance;

	private static final BackupTask backupTask = new BackupTask();
	private static final SaveTask saveTask = new SaveTask();
	private final List<Integer> taskIDs;

	public SCAutoBackup() {
		instance = this;
		taskIDs = new ArrayList<>();
	}

	public static SCAutoBackup getInstance() {
		if (instance == null) {
			throw new IllegalStateException("Instance access before init");
		}
		return instance;
	}

	@Override
	public void onEnable() {
		if (!Bukkit.isPrimaryThread()) {
			throw new IllegalStateException("Init not fom main thread");
		}
		saveDefaultConfig();

		var command = this.getCommand("scab");
		command.setExecutor(new CommandHandler());
		command.setTabCompleter(new CmdTabCompleter());

		getServer().getPluginManager().registerEvents(new PlayerEventListener(this), this);

		scheduleTasks();

		if(getConfig().getBoolean("backup.pause-on-start")) {
			backupTask.pause();
			MessageLogger.sendConsoleMessage(Level.INFO, "Auto Backup Paused");
		}
	}

	@Override
	public void onDisable() {
		for (var taskID : taskIDs) {
			MessageLogger.sendConsoleMessage(Level.INFO, "Cancelling Async Task");
			getServer().getScheduler().cancelTask(taskID);
		}

		MessageLogger.sendConsoleMessage(Level.INFO, "Task Threads Cancelled!");
	}

	private void scheduleTasks() {
		// Backup Thread
		if (getConfig().getBoolean("backup.enabled")) {
			MessageLogger.sendConsoleMessage(Level.INFO, "Auto Backup Enabled");
			long interval = TimeUtil.convertMinutesToTicks(getConfig().getInt("backup.interval"));
			taskIDs.add(getServer().getScheduler()
					.runTaskTimerAsynchronously(this, backupTask, interval,
							interval)
					.getTaskId());
		}

		// Save Thread
		if (getConfig().getBoolean("save.enabled")) {
			MessageLogger.sendConsoleMessage(Level.INFO, "Auto Save Enabled");
			long interval = TimeUtil.convertMinutesToTicks(getConfig().getInt("save.interval"));
			taskIDs.add(getServer().getScheduler()
					.runTaskTimerAsynchronously(this, saveTask, interval,
							interval)
					.getTaskId());
		}
	}

	public BackupTask getBackupTask() {
		return backupTask;
	}

	public SaveTask getSaveTask() {
		return saveTask;
	}

	@Override
	public @NotNull Logger getLogger() {
		return super.getLogger();
	}
}
