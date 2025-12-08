package de.hysky.skyblocker.utils;

import java.util.Locale;

/**
 * Utility class for text operations.
 */
public final class TextUtils {
	private TextUtils() {}

	public static String titleCase(String string) {
		String[] split = string.toLowerCase(Locale.ENGLISH).split(" ");
		if (split.length == 0) return string;

		for (int i = 0; i < split.length; i++) {
			if (split[i].isEmpty()) continue;
			split[i] = Character.toUpperCase(split[i].charAt(0)) + split[i].substring(1);
		}

		return String.join(" ", split);
	}
}
