package com.alexogden.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;

public class FileUtil {
	public static void deleteDirectory(File file) throws IOException {
		if (!file.exists()) {
			return;
		}
		Files.walkFileTree(file.toPath(), new FileVisitor<>() {
			@Override
			public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {
				return FileVisitResult.CONTINUE;
			}

			@Override
			public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
				Files.delete(file);
				return FileVisitResult.CONTINUE;
			}

			@Override
			public FileVisitResult visitFileFailed(Path file, IOException exc) {
				return FileVisitResult.CONTINUE;
			}

			@Override
			public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
				Files.delete(dir);
				return FileVisitResult.CONTINUE;
			}
		});
		file.delete();
	}

	public static String findOldestBackupName(Collection<String> backups) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss");
		String oldestBackupName = null;
		long old = System.currentTimeMillis();
		for (String timestampString : backups) {
			try {
				long cur;
				if (timestampString.endsWith(".zip")) {
					cur = sdf.parse(timestampString.substring(0, timestampString.indexOf(".zip"))).getTime();
				} else {
					cur = sdf.parse(timestampString).getTime();
				}
				if (cur < old) {
					old = cur;
					oldestBackupName = timestampString;
				}
			} catch (ParseException e) {
				throw new RuntimeException(e);
			}
		}
		return oldestBackupName;
	}

	public static String[] safeList(File file) {
		String[] files = file.list();
		return files != null ? files : new String[0];
	}
}
