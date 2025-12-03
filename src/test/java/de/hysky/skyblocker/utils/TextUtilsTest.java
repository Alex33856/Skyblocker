package de.hysky.skyblocker.utils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class TextUtilsTest {
	@Test
	void testTitleCase() {
		Assertions.assertEquals("Hello World", TextUtils.titleCase("hello world"));
		Assertions.assertEquals("Hello World", TextUtils.titleCase("HELLO WORLD"));
		Assertions.assertEquals("Aaaaaaaaa", TextUtils.titleCase("AaAaAaAaA"));

		Assertions.assertEquals("", TextUtils.titleCase(""));
		Assertions.assertEquals("Whitespace   Between", TextUtils.titleCase("whitespace   between"));
	}
}
