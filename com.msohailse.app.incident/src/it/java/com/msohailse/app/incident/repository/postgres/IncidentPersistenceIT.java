package com.msohailse.app.incident.repository.postgres;

import static org.junit.Assert.*;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;
import org.testcontainers.containers.PostgreSQLContainer;

import com.msohailse.app.incident.PostgresITSupport;
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
		emf = PostgresITSupport.createEntityManagerFactory(postgres);
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

		helperTag = new Tag("fire");

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
		Incident incident = new Incident("Server room overheating", null, Severity.HIGH, helperUser, helperTag);

		saveInTransaction(incident);

		assertTrue("Incident ID should be assigned after persist", incident.getId() > 0);
	}

	@Test
	public void testFindByIdReturnsCorrectIncident() {
		Incident incident = new Incident("Broken window", null, Severity.MEDIUM, helperUser, helperTag);

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
		Incident incident1 = new Incident("Water leak", null, Severity.LOW, helperUser, helperTag);
		Incident incident2 = new Incident("Power outage", null, Severity.HIGH, helperUser, helperTag);

		saveInTransaction(incident1);
		saveInTransaction(incident2);

		List<Incident> all = incidentRepo.findAll();
		assertEquals("Should have 2 incidents in database", 2, all.size());
	}

	@Test
	public void testSaveIncidentTagRelationshipPersists() {
		Incident incident = new Incident("Smoke detected", null, Severity.HIGH, helperUser, helperTag);

		saveInTransaction(incident);

		Incident retrieved = incidentRepo.findById(incident.getId());
		assertNotNull(retrieved);
		assertNotNull("Tag should be persisted with the incident", retrieved.getTag());
		assertEquals("Tag title should match", "fire", retrieved.getTag().getTagTitle());
	}

	@Test
	public void testIsClosedDefaultsFalseAfterPersist() {
		Incident incident = new Incident("Door left open", null, Severity.LOW, helperUser, helperTag);

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
