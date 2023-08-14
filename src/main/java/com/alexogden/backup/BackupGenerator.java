package com.alexogden.backup;

import com.alexogden.core.SCAutoBackup;
import com.alexogden.core.logging.MessageLogger;
import com.alexogden.util.ZipUtil;
import org.bukkit.Bukkit;
import org.bukkit.World;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;

public class BackupGenerator {
	private static BackupGenerator instance;
	private final List<World> worlds;
	private boolean backupInProgress;

	public BackupGenerator() {
		worlds = Bukkit.getWorlds();
		backupInProgress = false;
	}

	public static BackupGenerator getInstance() {
		if (instance == null) {
			instance = new BackupGenerator();
		}
		return instance;
	}

	public void createBackup(boolean worlds, boolean plugins, List<String> excludedFiles) {
		if (backupInProgress) {
			MessageLogger.sendConsoleMessage(Level.WARNING, "Backup already in progress!");
			return;
		}

		backupInProgress = true;

		if (worlds) {
			MessageLogger.sendConsoleMessage(Level.INFO, "Backing Up Worlds");
			backupWorlds();
		}
		if (plugins) {
			MessageLogger.sendConsoleMessage(Level.INFO, "Backing Up Plugins");
			backupPlugins(excludedFiles);
		}

		backupInProgress = false;
	}

	private void backupWorlds() {
		for (World world : worlds) {
			final File worldPath = world.getWorldFolder().getAbsoluteFile();
			final String outputDirectory = SCAutoBackup.getInstance().getConfig()
					.getString("backup.worlds.destination-folder") + "/" + world.getName();
			final String destinationPath = generateFilePath(outputDirectory);

			try {
				ZipUtil.zipFolder(worldPath, new File(destinationPath), Collections.singletonList(""));
			} catch (IOException e) {
				backupInProgress = false;
				MessageLogger.sendConsoleMessage(Level.WARNING, "Cannot ZIP world folder!");
				throw new RuntimeException(e);
			}

			BackupTrimmer backupTrimmer = new BackupTrimmer(outputDirectory, SCAutoBackup.getInstance().getConfig()
					.getInt("backup.worlds.max-backups"));
			backupTrimmer.trimExcessBackups();
		}
	}

	private void backupPlugins(List<String> excludedFolders) {
		final File pluginsPath = new File("plugins");
		final String destinationPath = generateFilePath(SCAutoBackup.getInstance().getConfig()
				.getString("backup.plugins.destination-folder"));

		try {
			ZipUtil.zipFolder(pluginsPath, new File(destinationPath), excludedFolders);
		} catch (IOException e) {
			backupInProgress = false;
			MessageLogger.sendConsoleMessage(Level.WARNING, "Cannot ZIP plugins folder!");
			throw new RuntimeException(e);
		}

		BackupTrimmer backupTrimmer = new BackupTrimmer(SCAutoBackup.getInstance().getConfig()
				.getString("backup.plugins.destination-folder"), SCAutoBackup.getInstance().getConfig()
						.getInt("backup.plugins.max-backups"));
		backupTrimmer.trimExcessBackups();
	}

	private String generateFilePath(String basePath) {
		String fileName = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").format(System.currentTimeMillis()) + ".zip";
		return Paths.get(basePath, fileName).toString();
	}

	public boolean isBackupInProgress() {
		return backupInProgress;
	}
}
