package com.alexogden.core;


import com.alexogden.backup.BackupTask;
import com.alexogden.command.CmdTabCompleter;
import com.alexogden.command.CommandHandler;
import com.alexogden.event.PlayerEventListener;
import com.alexogden.save.SaveTask;
import com.alexogden.util.TimeUtil;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

public class SCAutoBackup extends JavaPlugin {

	private static SCAutoBackup instance;

	private static BackupTask backupTask;
	private static SaveTask saveTask;

	private final List<Integer> taskIDs;

	public SCAutoBackup() {
		instance = this;
		backupTask = new BackupTask();
		saveTask = new SaveTask();

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
			getLogger().info("Auto Backup Paused");
		}
	}

	@Override
	public void onDisable() {
		for (var taskID : taskIDs) {
			getLogger().info("Cancelling Async Task");
			getServer().getScheduler().cancelTask(taskID);
		}

		getLogger().info("Task Threads Cancelled!");
	}

	private void scheduleTasks() {
		// Backup Thread
		if (getConfig().getBoolean("backup.enabled")) {
			getLogger().info("Auto Backup Enabled");
			long interval = TimeUtil.convertMinutesToTicks(getConfig().getInt("backup.interval"));
			taskIDs.add(getServer().getScheduler()
					.runTaskTimerAsynchronously(this, backupTask, interval,
							interval)
					.getTaskId());
		}

		// Save Thread
		if (getConfig().getBoolean("save.enabled")) {
			getLogger().info("Auto Save Enabled");
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
}
