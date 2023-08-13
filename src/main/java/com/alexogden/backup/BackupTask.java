package com.alexogden.backup;

import com.alexogden.core.SCAutoBackup;
import com.alexogden.core.ServerTask;
import com.alexogden.core.logging.MessageLogger;
import org.bukkit.Bukkit;

import java.util.List;

public class BackupTask extends ServerTask {
	@Override
	public void run() {
		if (isPaused()) {
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
