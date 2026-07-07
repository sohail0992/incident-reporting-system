package com.msohailse.app.incident.model;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class TagTest {

	private Tag firstTag;

	@Before
	public void setup() {
		firstTag = new Tag();
	}

	@Test
	public void testIdDefaultsToZeroBeforePersistence() {
		assertTrue("Id should default to 0 before JPA persists", firstTag.getId() == 0);
	}

	@Test
	public void testTagTitleDefaultsToNull() {
		assertNull(firstTag.getTagTitle());
	}

	@Test
	public void testTagDescriptionDefaultsToNull() {
		assertNull(firstTag.getTagDescription());
	}

	@Test
	public void testTagTitleWhenValidShouldStoreTitle() {
		Tag tag = new Tag("Fire");
		assertEquals("Fire", tag.getTagTitle());
	}

	@Test
	public void testTagTitleWithSingleSpaceIsValid() {
		Tag tag = new Tag("fire alarm");
		assertEquals("fire alarm", tag.getTagTitle());
	}

	@Test
	public void testTagTitleWithNull() {
		try {
			new Tag((String) null);
			fail("Expected an IllegalArgumentException to be thrown");
		} catch (IllegalArgumentException e) {
			assertEquals("Empty tagTitle", e.getMessage());
		}
	}

	@Test
	public void testTagTitleWithEmptyString() {
		try {
			new Tag("");
			fail("Expected an IllegalArgumentException to be thrown");
		} catch (IllegalArgumentException e) {
			assertEquals("Empty tagTitle", e.getMessage());
		}
	}

	@Test
	public void testTagTitleWithOnlySpacesShouldThrow() {
		try {
			new Tag("   ");
			fail("Expected an IllegalArgumentException to be thrown");
		} catch (IllegalArgumentException e) {
			assertEquals("Empty tagTitle", e.getMessage());
		}
	}

	@Test
	public void testTagTitleWithLeadingSpaceIsNormalized() {
		Tag tag = new Tag(" fire");
		assertEquals("fire", tag.getTagTitle());
	}

	@Test
	public void testTagDescriptionWhenSetShouldStoreDescription() {
		firstTag.setTagDescription("Incidents related to fire or smoke");
		assertEquals("Incidents related to fire or smoke", firstTag.getTagDescription());
	}

	@Test
	public void testTagDescriptionCanBeNull() {
		firstTag.setTagDescription(null);
		assertNull(firstTag.getTagDescription());
	}

	@Test
	public void testTagDescriptionCanBeEmptyString() {
		firstTag.setTagDescription("");
		assertEquals("", firstTag.getTagDescription());
	}

	@Test
	public void testTagDescriptionWithLeadingSpaceIsNormalized() {
		firstTag.setTagDescription(" fire related");
		assertEquals("fire related", firstTag.getTagDescription());
	}
}
