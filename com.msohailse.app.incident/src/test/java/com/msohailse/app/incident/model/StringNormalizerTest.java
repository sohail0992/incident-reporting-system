package com.msohailse.app.incident.model;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

public class StringNormalizerTest {

	private StringNormalizer normalizer;

	@Before
	public void setup() {
		normalizer = new StringNormalizer();
	}

	@Test
	public void testIsNullOrEmptyWithNull() {
		assertTrue(normalizer.isNullOrEmpty(null));
	}

	@Test
	public void testIsNullOrEmptyWithEmptyString() {
		assertTrue(normalizer.isNullOrEmpty(""));
	}

	@Test
	public void testIsNullOrEmptyWithNonEmptyString() {
		assertFalse(normalizer.isNullOrEmpty("abc"));
	}

	@Test
	public void testTrimAllSpacesWithNoSpaces() {
		assertEquals("abc", normalizer.trimAllSpaces("abc"));
	}

	@Test
	public void testTrimAllSpacesWithLeadingSpace() {
		assertEquals("abc", normalizer.trimAllSpaces(" abc"));
	}

	@Test
	public void testTrimAllSpacesWithTrailingSpace() {
		assertEquals("abc", normalizer.trimAllSpaces("abc "));
	}

	@Test
	public void testTrimAllSpacesWithMultipleSpacesInMiddle() {
		assertEquals("a b c", normalizer.trimAllSpaces("a  b   c"));
	}

	@Test
	public void testTrimAllSpacesWithTab() {
		assertEquals("a b", normalizer.trimAllSpaces("a\tb"));
	}

	@Test
	public void testTrimAllSpacesWithAllSpaces() {
		assertEquals("", normalizer.trimAllSpaces("   "));
	}

	@Test
	public void testTrimAllSpacesWithEmptyString() {
		assertEquals("", normalizer.trimAllSpaces(""));
	}
}
