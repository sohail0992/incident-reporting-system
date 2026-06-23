package incident_reporting;

import static org.junit.Assert.*;

import java.util.Date;

import org.junit.Before;
import org.junit.Test;

public class IncidentTest {

	private Incident firstIncident;

	@Before
	public void setup() {
		firstIncident = new Incident();
	}

	// defaults

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
	public void testTagsDefaultsToEmptyList() {
		assertNotNull(firstIncident.getTags());
		assertTrue("Tags list should be empty by default", firstIncident.getTags().isEmpty());
	}

	@Test
	public void testReportedAtIsSetOnConstruction() {
		assertNotNull("reportedAt should be set automatically in constructor", firstIncident.getReportedAt());
	}

	// title valid

	@Test
	public void testTitleWhenValidShouldStoreTitle() {
		firstIncident.setTitle("Server is down");
		assertEquals("Server is down", firstIncident.getTitle());
	}

	@Test
	public void testTitleWithSingleSpaceIsValid() {
		firstIncident.setTitle("power outage");
		assertEquals("power outage", firstIncident.getTitle());
	}

	// title empty or null

	@Test
	public void testTitleWithNullShouldThrow() {
		try {
			firstIncident.setTitle(null);
			fail("Expected an IllegalArgumentException to be thrown");
		} catch (IllegalArgumentException e) {
			assertEquals("Empty title", e.getMessage());
		}
	}

	@Test
	public void testTitleWithEmptyStringShouldThrow() {
		try {
			firstIncident.setTitle("");
			fail("Expected an IllegalArgumentException to be thrown");
		} catch (IllegalArgumentException e) {
			assertEquals("Empty title", e.getMessage());
		}
	}

	@Test
	public void testTitleWithOnlySpacesShouldThrow() {
		try {
			firstIncident.setTitle("   ");
			fail("Expected an IllegalArgumentException to be thrown");
		} catch (IllegalArgumentException e) {
			assertEquals("Empty title", e.getMessage());
		}
	}

	// title whitespace normalization

	@Test
	public void testTitleWithLeadingSpaceIsNormalized() {
		firstIncident.setTitle(" fire alarm");
		assertEquals("fire alarm", firstIncident.getTitle());
	}

	@Test
	public void testTitleWithTrailingSpaceIsNormalized() {
		firstIncident.setTitle("network failure ");
		assertEquals("network failure", firstIncident.getTitle());
	}

	@Test
	public void testTitleWithMultipleSpacesInMiddleIsNormalized() {
		firstIncident.setTitle("water  leak  detected");
		assertEquals("water leak detected", firstIncident.getTitle());
	}

	@Test
	public void testTitleWithTabIsNormalized() {
		firstIncident.setTitle("door\tforced open");
		assertEquals("door forced open", firstIncident.getTitle());
	}

	// description optional

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

	// description whitespace normalization

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

	// severity valid

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

	// severity null

	@Test
	public void testSeverityNullShouldThrow() {
		IllegalArgumentException e = assertThrows(IllegalArgumentException.class,
				() -> firstIncident.setSeverity(null));
		assertEquals("Empty severity", e.getMessage());
	}

	// reportedBy

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

	// addTag and removeTag

	@Test
	public void testAddTagAppendsToList() {
		Tag tag = new Tag();
		tag.setTagTitle("fire");
		firstIncident.addTag(tag);
		assertEquals(1, firstIncident.getTags().size());
		assertTrue(firstIncident.getTags().contains(tag));
	}

	@Test
	public void testAddTagNullShouldThrow() {
		IllegalArgumentException e = assertThrows(IllegalArgumentException.class,
				() -> firstIncident.addTag(null));
		assertEquals("Tag cannot be null", e.getMessage());
	}

	@Test
	public void testRemoveTagRemovesFromList() {
		Tag tag = new Tag();
		tag.setTagTitle("theft");
		firstIncident.addTag(tag);
		firstIncident.removeTag(tag);
		assertTrue(firstIncident.getTags().isEmpty());
	}

	// isClosed

	@Test
	public void testSetIsClosedTrueSetsFlag() {
		firstIncident.setIsClosed(true);
		assertTrue(firstIncident.isClosed());
	}

}
