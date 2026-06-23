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
public class TagPersistanceIT {

	private static EntityManagerFactory emf;
	private EntityManager em;
	private TagDao tagDao;

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
		tagDao = new TagDao(em);
		em.getTransaction().begin();
		em.createQuery("delete from Tag").executeUpdate();
		em.getTransaction().commit();
	}

	@After
	public void cleanup() {
		if (em != null && em.isOpen()) {
			em.close();
		}
	}

	@Test
	public void testSaveTagPersistsToDatabase() {
		Tag tag = new Tag();
		tag.setTagTitle("Fire");

		tagDao.save(tag);

		assertTrue("Tag ID should be assigned after persist", tag.getId() > 0);
	}

	@Test
	public void testFindByIdReturnsCorrectTag() {
		Tag tag = new Tag();
		tag.setTagTitle("Theft");
		tag.setTagDescription("Incidents related to theft");

		tagDao.save(tag);
		int savedId = tag.getId();

		Tag retrieved = tagDao.findById(savedId);

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

		tagDao.save(tag1);
		tagDao.save(tag2);
		tagDao.save(tag3);

		List<Tag> tags = tagDao.findAll();

		assertEquals("Should have 3 tags in database", 3, tags.size());
	}

	@Test
	public void testSaveTagWithoutDescriptionPersists() {
		Tag tag = new Tag();
		tag.setTagTitle("Vandalism");

		tagDao.save(tag);

		Tag retrieved = tagDao.findById(tag.getId());
		assertNotNull(retrieved);
		assertNull("Description should be null when not set", retrieved.getTagDescription());
	}

	@Test
	public void testFindByIdReturnsNullForNonExistentId() {
		Tag retrieved = tagDao.findById(99999);
		assertNull("Should return null for non-existent id", retrieved);
	}

	@Test
	public void testSaveTagWithExtraSpacesStoredNormalized() {
		Tag tag = new Tag();
		tag.setTagTitle("fire  alarm");

		tagDao.save(tag);

		Tag retrieved = tagDao.findById(tag.getId());
		assertEquals("Extra spaces should be normalized before storing", "fire alarm", retrieved.getTagTitle());
	}

}
