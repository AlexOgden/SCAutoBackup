package com.alexogden.util;

import com.alexogden.exception.BackupFailedException;
import org.bukkit.Bukkit;

import java.io.*;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ZipUtil {
	public static void zipFolder(final File srcDir, final File destFile, List<String> excludeFolders) throws IOException, BackupFailedException {
		destFile.getParentFile().mkdirs();

		try (OutputStream fos = new FileOutputStream(destFile)) {
			zipFolder(srcDir, fos, excludeFolders);
		}
	}

	public static void zipFolder(final File srcDir, final OutputStream outputStream, List<String> excludeFolders) throws IOException, BackupFailedException {
		try (BufferedOutputStream bufOutStream = new BufferedOutputStream(outputStream)) {
			try (ZipOutputStream zipOutStream = new ZipOutputStream(bufOutStream)) {
				zipDir(excludeFolders, zipOutStream, srcDir, "");
			}
			bufOutStream.flush();
		}
	}

	private static void zipDir(List<String> excludefolders, ZipOutputStream zipOutStream, final File srcDir, String currentDir) throws IOException, BackupFailedException {
		final File zipDir = new File(srcDir, currentDir);

		for (String child : FileUtil.safeList(zipDir)) {
			File srcFile = new File(zipDir, child);

			if (srcFile.isDirectory()) {
				if (!isFolderExcluded(excludefolders, srcDir.getName() + File.separator + currentDir + child)) {
					zipDir(excludefolders, zipOutStream, srcDir, currentDir + child + '/');
				}
			} else {
				zipFile(zipOutStream, srcFile, srcDir.getName() + '/' + currentDir + child);
			}
		}
	}

	public static boolean isFolderExcluded(List<String> excludelist, String folderPath) {
		String folderName = new File(folderPath).getAbsolutePath();

		for (String excludedFolder : excludelist) {
			// Asterisk at the end of excluded folder path excludes any folders starting with excluded folder pathname
			if (excludedFolder.endsWith("*")) {
				String excludedFolderName = new File(excludedFolder.substring(0, excludedFolder.length() - 1)).getAbsolutePath();
				if (folderName.startsWith(excludedFolderName)) {
					return true;
				}
			} else {
				if (folderName.equals(new File(excludedFolder).getAbsolutePath())) {
					return true;
				}
			}
		}

		return false;
	}

	private static void zipFile(ZipOutputStream zipOutStream, final File srcFile, final String entry) throws IOException, BackupFailedException {
		if (srcFile.getName().equals("session.lock")) {
			return;
		}
		if (!srcFile.canRead()) {
			Bukkit.getLogger().warning("Failed to backup file: " + srcFile.getAbsolutePath() + ", reason: canRead() returned false");
			throw new BackupFailedException("canRead() returned false");
		}
		InputStream inStream;
		try {
			// First attempt to construct the input stream, may throw exception if file gone missing or some other thing happened
			inStream = new FileInputStream(srcFile);
		} catch (IOException e) {
			Bukkit.getLogger().warning("Failed to backup file: " + srcFile.getAbsolutePath() + ", reason: exception when opening reading channel: " + e.getMessage());
			throw new BackupFailedException("exception when opening reading channel");
		}
		int firstByte;

		// Check if we can read from input stream
		try {
			firstByte = inStream.read();
		} catch (IOException e) {
			Bukkit.getLogger().warning("Failed to backup file: " + srcFile.getAbsolutePath() + ", reason: exception when reading first byte: " + e.getMessage());
			throw new BackupFailedException("exception when reading first byte");
		}

		//Empty file, put entry anyway
		if (firstByte == -1) {
			ZipEntry zipEntry = new ZipEntry(entry);
			zipEntry.setTime(srcFile.lastModified());
			zipOutStream.putNextEntry(zipEntry);
			zipOutStream.closeEntry();
			return;
		}

		ZipEntry zipEntry = new ZipEntry(entry);
		zipEntry.setTime(srcFile.lastModified());
		zipOutStream.putNextEntry(zipEntry);
		zipOutStream.write(firstByte);
		final byte[] buf = new byte[8192];

		int len;
		while ((len = inStream.read(buf)) != -1) {
			zipOutStream.write(buf, 0, len);
		}

		zipOutStream.closeEntry();

		try {
			inStream.close();
		} catch (IOException ignored) {
			throw new RuntimeException("Could not close stream");
		}
	}
}
