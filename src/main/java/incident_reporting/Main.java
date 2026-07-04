package incident_reporting;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class Main {

	public static void main(String[] args) {
		// Manual demo of the persistence stack.
		// Requires the PostgreSQL container from docker-compose.yml:
		//   docker compose up -d
		EntityManagerFactory emf = Persistence.createEntityManagerFactory("incident_reporting");
		TransactionManager transactionManager = new JpaTransactionManager(emf);

		User user = new User();
		user.setFirstName("Muhammad");
		user.setLastName("Sohail");
		user.setEmail("sohail1@example.com");
		user.setPassword("SecurePass123");

		transactionManager.doInTransaction(userRepository -> {
			userRepository.save(user);
			return null;
		});
		System.out.println("User saved with ID: " + user.getId());

		User retrieved = transactionManager.doInTransaction(
				userRepository -> userRepository.findByEmail("sohail1@example.com"));
		System.out.println(retrieved.getFirstName());

		emf.close();
	}

}
