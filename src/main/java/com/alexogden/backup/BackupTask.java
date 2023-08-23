package com.alexogden.backup;

import com.alexogden.core.SCAutoBackup;
import com.alexogden.core.ServerTask;
import com.alexogden.core.logging.MessageLogger;
import com.alexogden.util.TimeUtil;
import org.bukkit.Bukkit;

import java.util.List;
import java.util.logging.Level;

public class BackupTask extends ServerTask {
	@Override
	public void executeTask() {
		if (isPaused()) {
			return;
		}
		if (TimeUtil.getSecondsSince(getLastExecutionTime()) < 30) {
			MessageLogger.sendConsoleMessage(Level.INFO, "Last backup was less than 30 seconds ago. Aborting.");
			return;
		}
		boolean broadcastMessages = SCAutoBackup.getInstance().getConfig().getBoolean("backup.broadcast");
		// First save the world and player data
		SCAutoBackup.getInstance().getSaveTask().run();

		Bukkit.getScheduler().runTaskAsynchronously(SCAutoBackup.getInstance(), () -> {
			if (broadcastMessages)
				MessageLogger.broadcast("<yellow><bold>Starting Backup</bold></yellow>");

			boolean backupWorlds = SCAutoBackup.getInstance().getConfig().getBoolean("backup.worlds.enabled");
			boolean backupPlugins = SCAutoBackup.getInstance().getConfig().getBoolean("backup.plugins.enabled");
			List<String> excludedFiles = SCAutoBackup.getInstance().getConfig().getStringList("backup.plugins.excluded-folders");

			BackupGenerator.getInstance().createBackup(backupWorlds, backupPlugins, excludedFiles);

			if (broadcastMessages)
				MessageLogger.broadcast("<green><bold>Backup Complete</bold></green>");
		});
	}
}
