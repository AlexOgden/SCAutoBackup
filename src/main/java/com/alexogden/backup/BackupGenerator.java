package com.alexogden.backup;

import com.alexogden.core.SCAutoBackup;
import com.alexogden.core.logging.MessageLogger;
import com.alexogden.exception.BackupFailedException;
import com.alexogden.exception.ZipFailedException;
import com.alexogden.util.ZipUtil;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;

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

	private BackupGenerator() {
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
		String destinationFolder = SCAutoBackup.getInstance().getConfig().getString("backup.worlds.destination-folder");

		for (World world : worlds) {
			File worldPath = world.getWorldFolder().getAbsoluteFile();
			String worldName = world.getName();
			String destinationPath = generateFilePath(destinationFolder + "/" + worldName);

			try {
				ZipUtil.zipFolder(worldPath, new File(destinationPath), Collections.singletonList(""));
				trimBackups(destinationFolder + "/" + worldName + "/", "backup.worlds.max-backups");
			} catch (IOException | BackupFailedException | ZipFailedException e) {
				backupInProgress = false;
				MessageLogger.sendConsoleMessage(Level.WARNING, "Cannot ZIP world folder! - " + e.getMessage());
			}
		}
	}

	private void backupPlugins(List<String> excludedFolders) {
		String destinationFolder = SCAutoBackup.getInstance().getConfig().getString("backup.plugins.destination-folder");

		File pluginsPath = new File("plugins");
		String destinationPath = generateFilePath(destinationFolder);

		try {
			ZipUtil.zipFolder(pluginsPath, new File(destinationPath), excludedFolders);
			trimBackups(destinationFolder, "backup.plugins.max-backups");
		} catch (IOException | BackupFailedException | ZipFailedException e) {
			backupInProgress = false;
			MessageLogger.sendConsoleMessage(Level.WARNING, "Cannot ZIP plugins folder!");
		}
	}

	private void trimBackups(String destinationFolder, String maxBackupsKey) {
		FileConfiguration pluginConfig = SCAutoBackup.getInstance().getConfig();
		int maxBackups = pluginConfig.getInt(maxBackupsKey);

		BackupTrimmer backupTrimmer = new BackupTrimmer(destinationFolder, maxBackups);
		backupTrimmer.trimExcessBackups();
	}

	private String generateFilePath(String basePath) {
		String fileName = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(System.currentTimeMillis()) + ".zip";
		return Paths.get(basePath, fileName).toString();
	}

	public boolean isBackupInProgress() {
		return backupInProgress;
	}
}
