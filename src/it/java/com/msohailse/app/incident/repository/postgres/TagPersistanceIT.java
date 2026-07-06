package com.msohailse.app.incident.repository.postgres;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;
import org.testcontainers.containers.PostgreSQLContainer;

import com.msohailse.app.incident.model.Tag;

public class TagPersistanceIT {

	@ClassRule
	public static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15");

	private static EntityManagerFactory emf;
	private EntityManager em;
	private TagPostgresRepository tagRepo;

	@BeforeClass
	public static void setupDatabase() {
		Map<String, String> properties = new HashMap<>();
		properties.put("javax.persistence.jdbc.url", postgres.getJdbcUrl());
		properties.put("javax.persistence.jdbc.user", postgres.getUsername());
		properties.put("javax.persistence.jdbc.password", postgres.getPassword());
		emf = Persistence.createEntityManagerFactory("incident_reporting", properties);
	}

	@AfterClass
	public static void teardownDatabase() {
		if (emf != null) emf.close();
	}

	@Before
	public void setup() {
		em = emf.createEntityManager();
		tagRepo = new TagPostgresRepository(em);
		em.getTransaction().begin();
		em.createQuery("delete from Tag").executeUpdate();
		em.getTransaction().commit();
	}

	@After
	public void cleanup() {
		if (em != null && em.isOpen()) em.close();
	}

	private void saveInTransaction(Tag tag) {
		em.getTransaction().begin();
		tagRepo.save(tag);
		em.getTransaction().commit();
	}

	@Test
	public void testSaveTagPersistsToDatabase() {
		Tag tag = new Tag();
		tag.setTagTitle("Fire");

		saveInTransaction(tag);

		assertTrue("Tag ID should be assigned after persist", tag.getId() > 0);
	}

	@Test
	public void testFindByIdReturnsCorrectTag() {
		Tag tag = new Tag();
		tag.setTagTitle("Theft");
		tag.setTagDescription("Incidents related to theft");

		saveInTransaction(tag);
		int savedId = tag.getId();

		Tag retrieved = tagRepo.findById(savedId);

		assertNotNull("Retrieved tag should not be null", retrieved);
		assertEquals("Title should match", "Theft", retrieved.getTagTitle());
		assertEquals("Description should match", "Incidents related to theft", retrieved.getTagDescription());
	}

	@Test
	public void testFindAllReturnsAllSavedTags() {
		Tag tag1 = new Tag();
		tag1.setTagTitle("Fire");
		Tag tag2 = new Tag();
		tag2.setTagTitle("Flood");
		Tag tag3 = new Tag();
		tag3.setTagTitle("Theft");

		saveInTransaction(tag1);
		saveInTransaction(tag2);
		saveInTransaction(tag3);

		List<Tag> tags = tagRepo.findAll();
		assertEquals("Should have 3 tags in database", 3, tags.size());
	}

	@Test
	public void testSaveTagWithoutDescriptionPersists() {
		Tag tag = new Tag();
		tag.setTagTitle("Vandalism");

		saveInTransaction(tag);

		Tag retrieved = tagRepo.findById(tag.getId());
		assertNotNull(retrieved);
		assertNull("Description should be null when not set", retrieved.getTagDescription());
	}

	@Test
	public void testFindByIdReturnsNullForNonExistentId() {
		Tag retrieved = tagRepo.findById(99999);
		assertNull("Should return null for non-existent id", retrieved);
	}

	@Test
	public void testSaveTagWithExtraSpacesStoredNormalized() {
		Tag tag = new Tag();
		tag.setTagTitle("fire  alarm");

		saveInTransaction(tag);

		Tag retrieved = tagRepo.findById(tag.getId());
		assertEquals("Extra spaces should be normalized before storing", "fire alarm", retrieved.getTagTitle());
	}
}
