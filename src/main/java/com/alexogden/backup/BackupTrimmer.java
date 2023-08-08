package com.alexogden.backup;

import com.alexogden.util.FileUtil;
import org.bukkit.Bukkit;

import java.io.File;
import java.util.Arrays;
import java.util.Comparator;

public class BackupTrimmer {

	private final String backupDirectoryPath;
	private final int maxBackupCount;

	public BackupTrimmer(final String backupDirectoryPath, final int maxBackupCount) {
		this.backupDirectoryPath = backupDirectoryPath;
		this.maxBackupCount = maxBackupCount;
	}

	public void trimExcessBackups() {
		File backupDirectory = new File(backupDirectoryPath);

		if (backupDirectory.exists() && backupDirectory.isDirectory() && maxBackupCount > 0) {
			File[] backupFiles = backupDirectory.listFiles();

			if (backupFiles != null && backupFiles.length > maxBackupCount) {
				Arrays.sort(backupFiles, Comparator.comparingLong(file -> FileUtil.getTimestampFromFilename(file.getName())));

				int filesToDeleteCount = backupFiles.length - maxBackupCount;
				File[] backupsToDelete = Arrays.copyOfRange(backupFiles, 0, filesToDeleteCount);

				for (File backupToDelete : backupsToDelete) {
					if (backupToDelete.isFile()) {
						if (backupToDelete.delete()) {
							Bukkit.getLogger().info("Deleted backup file: " + backupToDelete.getName());
						} else {
							Bukkit.getLogger().info("Failed to delete backup file: " + backupToDelete.getName());
						}
					}
				}
			}
		}
	}
}
