package com.msohailse.app.incident;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.*;

import java.util.HashMap;
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
		transactionManager = new JpaTransactionManager(emf);
		em = emf.createEntityManager();
		em.getTransaction().begin();
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
}
