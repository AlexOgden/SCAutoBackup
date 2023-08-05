package com.alexogden.util;

public class TimeUtil {
	public static long convertMinutesToTicks(int minutes) {
		long ticksPerSecond = 20L;
		long ticksPerMinute = 60L * ticksPerSecond;

		return minutes * ticksPerMinute;
	}
}
