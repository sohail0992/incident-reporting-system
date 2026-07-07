package com.msohailse.app.incident.model;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

public class IncidentTest {

	private Incident firstIncident;

	@Before
	public void setup() {
		firstIncident = new Incident();
	}

	@Test
	public void testIdDefaultsToZeroBeforePersistence() {
		assertTrue("Id should default to 0 before JPA persists", firstIncident.getId() == 0);
	}

	@Test
	public void testTitleDefaultsToNull() {
		assertNull(firstIncident.getTitle());
	}

	@Test
	public void testDescriptionDefaultsToNull() {
		assertNull(firstIncident.getDescription());
	}

	@Test
	public void testIsClosedDefaultsToFalse() {
		assertFalse("isClosed should default to false", firstIncident.isClosed());
	}

	@Test
	public void testTagDefaultsToNull() {
		assertNull(firstIncident.getTag());
	}

	@Test
	public void testReportedAtIsSetOnConstruction() {
		assertNotNull("reportedAt should be set automatically in constructor", firstIncident.getReportedAt());
	}

	@Test
	public void testTitleWhenValidShouldStoreTitle() {
		Incident incident = new Incident("Server is down", null, null, null, null);
		assertEquals("Server is down", incident.getTitle());
	}

	@Test
	public void testTitleWithSingleSpaceIsValid() {
		Incident incident = new Incident("power outage", null, null, null, null);
		assertEquals("power outage", incident.getTitle());
	}

	@Test
	public void testTitleWithNullShouldThrow() {
		try {
			new Incident(null, null, null, null, null);
			fail("Expected an IllegalArgumentException to be thrown");
		} catch (IllegalArgumentException e) {
			assertEquals("Empty title", e.getMessage());
		}
	}

	@Test
	public void testTitleWithEmptyStringShouldThrow() {
		try {
			new Incident("", null, null, null, null);
			fail("Expected an IllegalArgumentException to be thrown");
		} catch (IllegalArgumentException e) {
			assertEquals("Empty title", e.getMessage());
		}
	}

	@Test
	public void testTitleWithOnlySpacesShouldThrow() {
		try {
			new Incident("   ", null, null, null, null);
			fail("Expected an IllegalArgumentException to be thrown");
		} catch (IllegalArgumentException e) {
			assertEquals("Empty title", e.getMessage());
		}
	}

	@Test
	public void testTitleWithLeadingSpaceIsNormalized() {
		Incident incident = new Incident(" fire alarm", null, null, null, null);
		assertEquals("fire alarm", incident.getTitle());
	}

	@Test
	public void testTitleWithTrailingSpaceIsNormalized() {
		Incident incident = new Incident("network failure ", null, null, null, null);
		assertEquals("network failure", incident.getTitle());
	}

	@Test
	public void testTitleWithMultipleSpacesInMiddleIsNormalized() {
		Incident incident = new Incident("water  leak  detected", null, null, null, null);
		assertEquals("water leak detected", incident.getTitle());
	}

	@Test
	public void testTitleWithTabIsNormalized() {
		Incident incident = new Incident("door\tforced open", null, null, null, null);
		assertEquals("door forced open", incident.getTitle());
	}

	@Test
	public void testDescriptionCanBeNull() {
		firstIncident.setDescription(null);
		assertNull(firstIncident.getDescription());
	}

	@Test
	public void testDescriptionCanBeEmptyString() {
		firstIncident.setDescription("");
		assertEquals("", firstIncident.getDescription());
	}

	@Test
	public void testDescriptionWhenSetShouldStoreDescription() {
		firstIncident.setDescription("Smoke detected on the second floor near the server room");
		assertEquals("Smoke detected on the second floor near the server room", firstIncident.getDescription());
	}

	@Test
	public void testDescriptionWithLeadingAndTrailingSpacesIsNormalized() {
		firstIncident.setDescription("  broken window  ");
		assertEquals("broken window", firstIncident.getDescription());
	}

	@Test
	public void testDescriptionWithMultipleSpacesInMiddleIsNormalized() {
		firstIncident.setDescription("door  was  found  open");
		assertEquals("door was found open", firstIncident.getDescription());
	}

	@Test
	public void testSeverityLowShouldStore() {
		firstIncident.setSeverity(Severity.LOW);
		assertEquals(Severity.LOW, firstIncident.getSeverity());
	}

	@Test
	public void testSeverityHighShouldStore() {
		firstIncident.setSeverity(Severity.HIGH);
		assertEquals(Severity.HIGH, firstIncident.getSeverity());
	}

	@Test
	public void testSeverityNullShouldThrow() {
		IllegalArgumentException e = assertThrows(IllegalArgumentException.class,
				() -> firstIncident.setSeverity(null));
		assertEquals("Empty severity", e.getMessage());
	}

	@Test
	public void testReportedByWhenValidShouldStore() {
		User user = new User();
		user.setFirstName("Alice");
		user.setLastName("Smith");
		user.setEmail("alice@example.com");
		user.setPassword("AlicePass1");
		firstIncident.setReportedBy(user);
		assertEquals(user, firstIncident.getReportedBy());
	}

	@Test
	public void testReportedByNullShouldThrow() {
		IllegalArgumentException e = assertThrows(IllegalArgumentException.class,
				() -> firstIncident.setReportedBy(null));
		assertEquals("Reporter cannot be null", e.getMessage());
	}

	@Test
	public void testSetTagWhenValidShouldStore() {
		Tag tag = new Tag("fire");
		firstIncident.setTag(tag);
		assertEquals(tag, firstIncident.getTag());
	}

	@Test
	public void testSetTagNullShouldThrow() {
		IllegalArgumentException e = assertThrows(IllegalArgumentException.class,
				() -> firstIncident.setTag(null));
		assertEquals("Tag cannot be null", e.getMessage());
	}

	@Test
	public void testSetIsClosedTrueSetsFlag() {
		firstIncident.setIsClosed(true);
		assertTrue(firstIncident.isClosed());
	}

	@Test
	public void testToStringWithNullTag() {
		Incident incident = new Incident("Server Down", "Main server crashed", Severity.HIGH, null, null);

		String result = incident.toString();

		assertTrue(result.contains("[HIGH]"));
		assertTrue(result.contains("Server Down"));
		assertTrue(result.contains("Description: Main server crashed"));
		assertTrue(result.contains("Category: null"));
	}

	@Test
	public void testToStringWithValidTag() {
		Tag tag = new Tag("Frontend");
		Incident incident = new Incident("UI Bug", "Button misaligned", Severity.LOW, null, tag);

		String result = incident.toString();

		assertTrue(result.contains("[LOW]"));
		assertTrue(result.contains("UI Bug"));
		assertTrue(result.contains("Description: Button misaligned"));
		assertTrue(result.contains("Category: Frontend"));
	}

}
