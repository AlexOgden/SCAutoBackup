package com.alexogden.util;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FileUtil {
	public static long getTimestampFromFilename(String filename) {
		String timestampPart = filename.substring(0, filename.lastIndexOf('.')).replace("-", "");
		try {
			Date date = new SimpleDateFormat("yyyyMMddHHmmss").parse(timestampPart);
			return date.getTime();
		} catch (ParseException e) {
			return Long.MAX_VALUE;
		}
	}

	public static String[] safeList(File file) {
		String[] files = file.list();
		return files != null ? files : new String[0];
	}
}
