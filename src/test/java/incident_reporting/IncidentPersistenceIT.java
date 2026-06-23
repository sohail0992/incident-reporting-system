package incident_reporting;

import static org.junit.Assert.*;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/*
 * Integration tests using a real PostgreSQL database.
 * Docker starts automatically via Maven (docker compose up) when running mvn verify.
 *
 * To run from Eclipse: start Docker manually first:
 *   docker compose up -d
 * Then "Run As -> JUnit Test".
 * Stop after: docker compose down
 */
public class IncidentPersistenceIT {

	private static EntityManagerFactory emf;
	private EntityManager em;
	private IncidentDao incidentDao;

	private User helperUser;
	private Tag helperTag;

	@BeforeClass
	public static void setupDatabase() {
		emf = Persistence.createEntityManagerFactory("incident_reporting");
	}

	@AfterClass
	public static void teardownDatabase() {
		if (emf != null) emf.close();
	}

	@Before
	public void setup() {
		em = emf.createEntityManager();
		incidentDao = new IncidentDao(em);

		em.getTransaction().begin();
		em.createNativeQuery("delete from incident_tags").executeUpdate();
		em.createQuery("delete from Incident").executeUpdate();
		em.createQuery("delete from User").executeUpdate();
		em.createQuery("delete from Tag").executeUpdate();
		em.getTransaction().commit();

		helperUser = new User();
		helperUser.setFirstName("John");
		helperUser.setLastName("Doe");
		helperUser.setEmail("john@example.com");
		helperUser.setPassword("SecurePass123");
		new UserDao(em).save(helperUser);

		helperTag = new Tag();
		helperTag.setTagTitle("fire");
		new TagDao(em).save(helperTag);
	}

	@After
	public void cleanup() {
		if (em != null && em.isOpen()) {
			em.close();
		}
	}

	@Test
	public void testSaveIncidentPersistsToDatabase() {
		Incident incident = new Incident();
		incident.setTitle("Server room overheating");
		incident.setSeverity(Severity.HIGH);
		incident.setReportedBy(helperUser);

		incidentDao.save(incident);

		assertTrue("Incident ID should be assigned after persist", incident.getId() > 0);
	}

	@Test
	public void testFindByIdReturnsCorrectIncident() {
		Incident incident = new Incident();
		incident.setTitle("Broken window");
		incident.setSeverity(Severity.MEDIUM);
		incident.setReportedBy(helperUser);

		incidentDao.save(incident);
		int savedId = incident.getId();

		Incident retrieved = incidentDao.findById(savedId);

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

		Incident incident2 = new Incident();
		incident2.setTitle("Power outage");
		incident2.setSeverity(Severity.HIGH);
		incident2.setReportedBy(helperUser);

		incidentDao.save(incident1);
		incidentDao.save(incident2);

		List<Incident> all = incidentDao.findAll();
		assertEquals("Should have 2 incidents in database", 2, all.size());
	}

	@Test
	public void testSaveIncidentWithTagPersistsRelationship() {
		Incident incident = new Incident();
		incident.setTitle("Smoke detected");
		incident.setSeverity(Severity.HIGH);
		incident.setReportedBy(helperUser);
		incident.addTag(helperTag);

		incidentDao.save(incident);

		Incident retrieved = incidentDao.findById(incident.getId());
		assertNotNull(retrieved);
		assertEquals("Incident should have one tag", 1, retrieved.getTags().size());
		assertEquals("Tag title should match", "fire", retrieved.getTags().get(0).getTagTitle());
	}

	@Test
	public void testIsClosedDefaultsFalseAfterPersist() {
		Incident incident = new Incident();
		incident.setTitle("Door left open");
		incident.setSeverity(Severity.LOW);
		incident.setReportedBy(helperUser);

		incidentDao.save(incident);

		Incident retrieved = incidentDao.findById(incident.getId());
		assertFalse("isClosed should default to false after persist", retrieved.isClosed());
	}

	@Test
	public void testFindByIdReturnsNullForNonExistentId() {
		Incident retrieved = incidentDao.findById(99999);
		assertNull("Should return null for non-existent id", retrieved);
	}

}
