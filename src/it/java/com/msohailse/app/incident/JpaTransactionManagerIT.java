package com.msohailse.app.incident;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
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

import com.msohailse.app.incident.model.Incident;
import com.msohailse.app.incident.model.Severity;
import com.msohailse.app.incident.model.Tag;
import com.msohailse.app.incident.model.User;
import com.msohailse.app.incident.repository.postgres.UserPostgresRepository;

public class JpaTransactionManagerIT {

	@ClassRule
	public static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15");

	private static EntityManagerFactory emf;
	private EntityManager em;
	private JpaTransactionManager transactionManager;

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
		transactionManager = new JpaTransactionManager(emf);
		em = emf.createEntityManager();
		em.getTransaction().begin();
		em.createQuery("delete from Incident").executeUpdate();
		em.createQuery("delete from Tag").executeUpdate();
		em.createQuery("delete from User").executeUpdate();
		em.getTransaction().commit();
	}

	@After
	public void cleanup() {
		if (em != null && em.isOpen()) em.close();
	}

	private User buildUser(String email) {
		User user = new User();
		user.setFirstName("John");
		user.setLastName("Doe");
		user.setEmail(email);
		user.setPassword("SecurePass123");
		return user;
	}

	private Tag buildTag(String title) {
		return new Tag(title);
	}

	private Incident buildIncident(String title, User user, Tag tag) {
		return new Incident(title, null, Severity.HIGH, user, tag);
	}

	@Test
	public void testDoInTransactionCommitsChanges() {
		transactionManager.doInTransaction(repo -> {
			repo.save(buildUser("john@example.com"));
			return null;
		});

		assertEquals("User should be committed to the database",
				1, new UserPostgresRepository(em).findAll().size());
	}

	@Test
	public void testDoInTransactionReturnsCodeResult() {
		transactionManager.doInTransaction(repo -> {
			repo.save(buildUser("john@example.com"));
			return null;
		});

		User found = transactionManager.doInTransaction(
				repo -> repo.findUserByEmail("john@example.com"));

		assertNotNull("The result of the code should be returned", found);
		assertEquals("john@example.com", found.getEmail());
	}

	@Test
	public void testDoInTransactionRollsBackWhenCodeThrows() {
		assertThatThrownBy(() ->
			transactionManager.doInTransaction(repo -> {
				repo.save(buildUser("john@example.com"));
				throw new RuntimeException("failure inside the transaction");
			}))
			.isInstanceOf(RuntimeException.class)
			.hasMessage("failure inside the transaction");

		assertEquals("Nothing should be committed after a rollback",
				0, new UserPostgresRepository(em).findAll().size());
	}

	@Test
	public void testDoInTransactionRollsBackWhenCommitFails() {
		transactionManager.doInTransaction(repo -> {
			repo.save(buildUser("duplicate@example.com"));
			return null;
		});

		assertThatThrownBy(() ->
			transactionManager.doInTransaction(repo -> {
				repo.save(buildUser("duplicate@example.com"));
				return null;
			}))
			.isInstanceOf(RuntimeException.class);

		assertEquals("Only the first user should be in the database",
				1, new UserPostgresRepository(em).findAll().size());
	}

	@Test
	public void testFindUserByIdReturnsCorrectUser() {
		User saved = transactionManager.doInTransaction(repo -> {
			User user = buildUser("id@example.com");
			repo.save(user);
			return user;
		});

		User found = transactionManager.doInTransaction(repo -> repo.findUserById(saved.getId()));

		assertNotNull(found);
		assertEquals("id@example.com", found.getEmail());
	}

	@Test
	public void testFindAllUsersReturnsAllSaved() {
		transactionManager.doInTransaction(repo -> {
			repo.save(buildUser("a@example.com"));
			repo.save(buildUser("b@example.com"));
			return null;
		});

		int count = transactionManager.doInTransaction(repo -> repo.findAllUsers().size());

		assertEquals(2, count);
	}

	@Test
	public void testSaveTagAndFindByTitleAndById() {
		Tag saved = transactionManager.doInTransaction(repo -> {
			Tag tag = buildTag("fire");
			repo.save(tag);
			return tag;
		});

		Tag byTitle = transactionManager.doInTransaction(repo -> repo.findTagByTitle("fire"));
		Tag byId = transactionManager.doInTransaction(repo -> repo.findTagById(saved.getId()));

		assertNotNull(byTitle);
		assertEquals("fire", byTitle.getTagTitle());
		assertNotNull(byId);
		assertEquals("fire", byId.getTagTitle());
	}

	@Test
	public void testFindAllTagsReturnsAllSaved() {
		transactionManager.doInTransaction(repo -> {
			repo.save(buildTag("fire"));
			repo.save(buildTag("flood"));
			return null;
		});

		int count = transactionManager.doInTransaction(repo -> repo.findAllTags().size());

		assertEquals(2, count);
	}

	@Test
	public void testSaveIncidentAndFindByIdAndFindAll() {
		transactionManager.doInTransaction(repo -> {
			User user = buildUser("inc@example.com");
			repo.save(user);
			Tag tag = buildTag("smoke");
			repo.save(tag);
			repo.save(buildIncident("Server overheating", user, tag));
			return null;
		});

		int total = transactionManager.doInTransaction(repo -> repo.findAllIncidents().size());
		assertEquals(1, total);
	}

	@Test
	public void testFindIncidentByIdAndFindByUser() {
		Incident saved = transactionManager.doInTransaction(repo -> {
			User user = buildUser("byuser@example.com");
			repo.save(user);
			Tag tag = buildTag("leak");
			repo.save(tag);
			Incident incident = buildIncident("Water pipe burst", user, tag);
			repo.save(incident);
			return incident;
		});

		Incident byId = transactionManager.doInTransaction(
				repo -> repo.findIncidentById(saved.getId()));
		assertNotNull(byId);
		assertEquals("Water pipe burst", byId.getTitle());

		int byUser = transactionManager.doInTransaction(repo -> {
			User user = repo.findUserByEmail("byuser@example.com");
			return repo.findIncidentsByUser(user).size();
		});
		assertEquals(1, byUser);
	}
}
