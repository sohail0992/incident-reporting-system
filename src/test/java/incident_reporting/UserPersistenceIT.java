package incident_reporting;

import static org.junit.Assert.*;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.AfterClass;
import org.junit.Test;

/*
 * Integration tests using a real PostgreSQL database.
 * Docker starts PostgreSQL automatically via Maven exec-maven-plugin (docker-compose up).
 *
 * To run from Eclipse: start Docker manually first:
 *   docker-compose up -d
 * Then "Run As -> JUnit Test".
 * Stop after: docker-compose down
 */
public class UserPersistenceIT {

	private static EntityManagerFactory emf;
	private EntityManager em;
	private UserDao dao;

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
		dao = new UserDao(em);
		em.getTransaction().begin();
		em.createQuery("delete from User").executeUpdate();
		em.getTransaction().commit();
	}

	@After
	public void cleanup() {
		if (em != null && em.isOpen()) {
			em.close();
		}
	}

	@Test
	public void testSaveUserPersistsToDatabase() {
		User user = new User();
		user.setFirstName("John");
		user.setLastName("Doe");
		user.setEmail("john@example.com");
		user.setPassword("SecurePass123");

		dao.save(user);

		assertTrue("User ID should be assigned after persist", user.getId() > 0);
	}

	@Test
	public void testFindByIdReturnsCorrectUser() {
		User user = new User();
		user.setFirstName("Jane");
		user.setLastName("Smith");
		user.setEmail("jane@example.com");
		user.setPassword("AnotherPass456");

		dao.save(user);
		int savedId = user.getId();

		User retrieved = dao.findById(savedId);

		assertNotNull("Retrieved user should not be null", retrieved);
		assertEquals("First name should match", "Jane", retrieved.getFirstName());
		assertEquals("Last name should match", "Smith", retrieved.getLastName());
		assertEquals("Email should match", "jane@example.com", retrieved.getEmail());
	}

	@Test
	public void testFindAllReturnsAllSavedUsers() {
		User user1 = new User();
		user1.setFirstName("Alice");
		user1.setLastName("Johnson");
		user1.setEmail("alice@example.com");
		user1.setPassword("AlicePass789");

		User user2 = new User();
		user2.setFirstName("Bob");
		user2.setLastName("Brown");
		user2.setEmail("bob@example.com");
		user2.setPassword("BobPass321");

		dao.save(user1);
		dao.save(user2);

		assertEquals("Should have 2 users in database", 2, dao.findAll().size());
	}

	@Test
	public void testSaveWithDuplicateEmailThrowsException() {
		User user1 = new User();
		user1.setFirstName("David");
		user1.setLastName("Davis");
		user1.setEmail("duplicate@example.com");
		user1.setPassword("DavidPass654");

		dao.save(user1);

		User user2 = new User();
		user2.setFirstName("Eve");
		user2.setLastName("Evans");
		user2.setEmail("duplicate@example.com");
		user2.setPassword("EvePass999");

		try {
			dao.save(user2);
			fail("Should have thrown an exception due to duplicate email constraint");
		} catch (Exception e) {
			assertTrue("Exception should be thrown for duplicate email", true);
		}
	}

}
