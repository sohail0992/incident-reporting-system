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

import com.msohailse.app.incident.model.Incident;
import com.msohailse.app.incident.model.Severity;
import com.msohailse.app.incident.model.Tag;
import com.msohailse.app.incident.model.User;

public class IncidentPersistenceIT {

	@ClassRule
	public static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15");

	private static EntityManagerFactory emf;
	private EntityManager em;
	private IncidentPostgresRepository incidentRepo;

	private User helperUser;
	private Tag helperTag;

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
		incidentRepo = new IncidentPostgresRepository(em);

		em.getTransaction().begin();
		em.createQuery("delete from Incident").executeUpdate();
		em.createQuery("delete from User").executeUpdate();
		em.createQuery("delete from Tag").executeUpdate();
		em.getTransaction().commit();

		helperUser = new User();
		helperUser.setFirstName("John");
		helperUser.setLastName("Doe");
		helperUser.setEmail("john@example.com");
		helperUser.setPassword("SecurePass123");

		helperTag = new Tag();
		helperTag.setTagTitle("fire");

		em.getTransaction().begin();
		new UserPostgresRepository(em).save(helperUser);
		new TagPostgresRepository(em).save(helperTag);
		em.getTransaction().commit();
	}

	@After
	public void cleanup() {
		if (em != null && em.isOpen()) em.close();
	}

	private void saveInTransaction(Incident incident) {
		em.getTransaction().begin();
		incidentRepo.save(incident);
		em.getTransaction().commit();
	}

	@Test
	public void testSaveIncidentPersistsToDatabase() {
		Incident incident = new Incident();
		incident.setTitle("Server room overheating");
		incident.setSeverity(Severity.HIGH);
		incident.setReportedBy(helperUser);
		incident.setTag(helperTag);

		saveInTransaction(incident);

		assertTrue("Incident ID should be assigned after persist", incident.getId() > 0);
	}

	@Test
	public void testFindByIdReturnsCorrectIncident() {
		Incident incident = new Incident();
		incident.setTitle("Broken window");
		incident.setSeverity(Severity.MEDIUM);
		incident.setReportedBy(helperUser);
		incident.setTag(helperTag);

		saveInTransaction(incident);
		int savedId = incident.getId();

		Incident retrieved = incidentRepo.findById(savedId);

		assertNotNull("Retrieved incident should not be null", retrieved);
		assertEquals("Title should match", "Broken window", retrieved.getTitle());
		assertEquals("Severity should match", Severity.MEDIUM, retrieved.getSeverity());
		assertEquals("Reporter first name should match", "John", retrieved.getReportedBy().getFirstName());
	}

	@Test
	public void testFindAllReturnsAllSavedIncidents() {
		Incident incident1 = new Incident();
		incident1.setTitle("Water leak");
		incident1.setSeverity(Severity.LOW);
		incident1.setReportedBy(helperUser);
		incident1.setTag(helperTag);

		Incident incident2 = new Incident();
		incident2.setTitle("Power outage");
		incident2.setSeverity(Severity.HIGH);
		incident2.setReportedBy(helperUser);
		incident2.setTag(helperTag);

		saveInTransaction(incident1);
		saveInTransaction(incident2);

		List<Incident> all = incidentRepo.findAll();
		assertEquals("Should have 2 incidents in database", 2, all.size());
	}

	@Test
	public void testSaveIncidentTagRelationshipPersists() {
		Incident incident = new Incident();
		incident.setTitle("Smoke detected");
		incident.setSeverity(Severity.HIGH);
		incident.setReportedBy(helperUser);
		incident.setTag(helperTag);

		saveInTransaction(incident);

		Incident retrieved = incidentRepo.findById(incident.getId());
		assertNotNull(retrieved);
		assertNotNull("Tag should be persisted with the incident", retrieved.getTag());
		assertEquals("Tag title should match", "fire", retrieved.getTag().getTagTitle());
	}

	@Test
	public void testIsClosedDefaultsFalseAfterPersist() {
		Incident incident = new Incident();
		incident.setTitle("Door left open");
		incident.setSeverity(Severity.LOW);
		incident.setReportedBy(helperUser);
		incident.setTag(helperTag);

		saveInTransaction(incident);

		Incident retrieved = incidentRepo.findById(incident.getId());
		assertFalse("isClosed should default to false after persist", retrieved.isClosed());
	}

	@Test
	public void testFindByIdReturnsNullForNonExistentId() {
		Incident retrieved = incidentRepo.findById(99999);
		assertNull("Should return null for non-existent id", retrieved);
	}
}
