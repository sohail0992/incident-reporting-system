package com.msohailse.app.incident.repository.postgres;

import static org.junit.Assert.*;

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
import com.msohailse.app.incident.model.User;

public class UserPersistenceIT {

	@ClassRule
	public static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15");

	private static EntityManagerFactory emf;
	private EntityManager em;
	private UserPostgresRepository userRepo;

	@BeforeClass
	public static void setupDatabase() {
		emf = PostgresITSupport.createEntityManagerFactory(postgres);
	}

	@AfterClass
	public static void teardownDatabase() {
		if (emf != null)
			emf.close();
	}

	@Before
	public void setup() {
		em = emf.createEntityManager();
		userRepo = new UserPostgresRepository(em);
		em.getTransaction().begin();
		em.createQuery("delete from User").executeUpdate();
		em.getTransaction().commit();
	}

	@After
	public void cleanup() {
		if (em != null && em.isOpen())
			em.close();
	}

	private void saveInTransaction(User user) {
		em.getTransaction().begin();
		userRepo.save(user);
		em.getTransaction().commit();
	}

	@Test
	public void testSaveUserPersistsToDatabase() {
		User user = new User();
		user.setFirstName("John");
		user.setLastName("Doe");
		user.setEmail("john@example.com");
		user.setPassword("SecurePass123");

		saveInTransaction(user);

		assertTrue("User ID should be assigned after persist", user.getId() > 0);
	}

	@Test
	public void testFindByIdReturnsCorrectUser() {
		User user = new User();
		user.setFirstName("Jane");
		user.setLastName("Smith");
		user.setEmail("jane@example.com");
		user.setPassword("AnotherPass456");

		saveInTransaction(user);
		int savedId = user.getId();

		User retrieved = userRepo.findById(savedId);

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

		saveInTransaction(user1);
		saveInTransaction(user2);

		assertEquals("Should have 2 users in database", 2, userRepo.findAll().size());
	}

	@Test
	public void testSaveExistingUserMergesChanges() {
		User user = new User();
		user.setFirstName("Charlie");
		user.setLastName("Clark");
		user.setEmail("charlie@example.com");
		user.setPassword("CharliePass1");

		saveInTransaction(user);
		int savedId = user.getId();

		user.setFirstName("Charles");
		saveInTransaction(user);

		User retrieved = userRepo.findById(savedId);
		assertEquals("First name should be updated after merge", "Charles", retrieved.getFirstName());
	}

	@Test
	public void testSaveDetachedExistingUserMergesChanges() {
		User user = new User();
		user.setFirstName("Frank");
		user.setLastName("Foster");
		user.setEmail("frank@example.com");
		user.setPassword("FrankPass123");

		saveInTransaction(user);
		int savedId = user.getId();

		// use a fresh EntityManager so the entity is detached (not em.contains(...))
		// but still has a non-zero id, exercising the other merge branch
		EntityManager freshEm = emf.createEntityManager();
		UserPostgresRepository freshUserRepo = new UserPostgresRepository(freshEm);
		User detachedUser = new User();
		detachedUser.setId(savedId);
		detachedUser.setFirstName("Franklin");
		detachedUser.setLastName("Foster");
		detachedUser.setEmail("frank@example.com");
		detachedUser.setPassword("FrankPass123");

		freshEm.getTransaction().begin();
		freshUserRepo.save(detachedUser);
		freshEm.getTransaction().commit();

		// verify via the same fresh EntityManager to avoid reading a stale
		// first-level-cache copy from the original em
		User retrieved = freshUserRepo.findById(savedId);
		assertEquals("First name should be updated after merging a detached entity", "Franklin",
				retrieved.getFirstName());
		freshEm.close();
	}

	@Test
	public void testFindByEmailReturnsNullWhenNoMatch() {
		User retrieved = userRepo.findByEmail("nonexistent@example.com");
		assertNull("Should return null when no user matches the email", retrieved);
	}

	@Test
	public void testSaveWithDuplicateEmailThrowsException() {
		User user1 = new User();
		user1.setFirstName("David");
		user1.setLastName("Davis");
		user1.setEmail("duplicate@example.com");
		user1.setPassword("DavidPass654");

		saveInTransaction(user1);

		User user2 = new User();
		user2.setFirstName("Eve");
		user2.setLastName("Evans");
		user2.setEmail("duplicate@example.com");
		user2.setPassword("EvePass999");

		em.getTransaction().begin();
		try {
			userRepo.save(user2);
			em.getTransaction().commit();
			fail("Should have thrown an exception due to duplicate email constraint");
		} catch (Exception e) {
			assertTrue("Exception should be thrown for duplicate email", true);
		}
	}
}
