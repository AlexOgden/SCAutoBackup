package com.alexogden.backup;

import com.alexogden.core.SCAutoBackup;
import com.alexogden.util.FileUtil;
import com.alexogden.util.ZipUtil;
import org.bukkit.Bukkit;
import org.bukkit.World;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.List;

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
			Bukkit.getLogger().warning("Backup already in progress!");
			return;
		}

		backupInProgress = true;

		if (worlds) {
			Bukkit.getLogger().info("Backing Up Worlds");
			backupWorlds();
		}
		if (plugins) {
			Bukkit.getLogger().info("Backing Up Plugins");
			backupPlugins(excludedFiles);
		}

		backupInProgress = false;
	}

	private void backupWorlds() {
		for (World world : worlds) {
			final File worldPath = world.getWorldFolder().getAbsoluteFile();
			final String destinationPath = generateFilePath(SCAutoBackup.getInstance().getConfig()
					.getString("backup.worlds.destination-folder"));

			try {
				ZipUtil.zipFolder(worldPath, new File(destinationPath), Collections.singletonList(""));
			} catch (IOException e) {
				backupInProgress = false;
				Bukkit.getLogger().warning("Cannot ZIP world folder!");
				throw new RuntimeException(e);
			}

			try {
				trimBackups(SCAutoBackup.getInstance().getConfig().getString("backup.worlds.destination-folder"),
						SCAutoBackup.getInstance().getConfig().getInt("backup.worlds.max-backups"));
			} catch (IOException e) {
				backupInProgress = false;
				Bukkit.getLogger().warning("Cannot trim backups!");
				throw new RuntimeException(e);
			}
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
			Bukkit.getLogger().warning("Cannot ZIP plugins folder!");
			throw new RuntimeException(e);
		}

		try {
			trimBackups(SCAutoBackup.getInstance().getConfig().getString("backup.plugins.destination-folder"),
					SCAutoBackup.getInstance().getConfig().getInt("backup.plugins.max-backups"));
		} catch (IOException e) {
			backupInProgress = false;
			Bukkit.getLogger().warning("Cannot trim backups!");
			throw new RuntimeException(e);
		}
	}

	private void trimBackups(String destinationPath, int maxBackupsCount) throws IOException {
		String[] folders = FileUtil.safeList(new File(destinationPath));
		if ((maxBackupsCount != 0) && new File(destinationPath).exists() && (folders.length > maxBackupsCount)) {
			String oldestBackupName = FileUtil.findOldestBackupName(List.of(folders));
			if (oldestBackupName != null) {
				File oldestBackup = new File(destinationPath, oldestBackupName);
				FileUtil.deleteDirectory(oldestBackup);
			}
		}
	}

	private String generateFilePath(String basePath) {
		String fileName = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").format(System.currentTimeMillis()) + ".zip";
		return Paths.get(basePath, fileName).toString();
	}

	public boolean isBackupInProgress() {
		return backupInProgress;
	}
}
