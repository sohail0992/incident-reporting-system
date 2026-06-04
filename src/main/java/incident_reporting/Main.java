package incident_reporting;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class Main {

	public static void main(String[] args) {
		EntityManagerFactory emf = Persistence.createEntityManagerFactory("incident_reporting");
		EntityManager em = emf.createEntityManager();
		UserDao dao = new UserDao(em);

		User user = new User();
		user.setFirstName("Muhammad");
		user.setLastName("Sohail");
		user.setEmail("muhammad.sohail@edu.unifi.it");
		user.setPassword("SecurePass123");

		System.out.println("=== PERSISTING USER TO DATABASE ===");
		dao.save(user);
		System.out.println("User saved with ID: " + user.getId());

		System.out.println("\n=== RETRIEVING USER FROM DATABASE ===");
		User retrieved = dao.findById(user.getId());
		System.out.println("Retrieved: " + retrieved.getFirstName() + " " + retrieved.getLastName());
		System.out.println("Email: " + retrieved.getEmail());

		em.close();
		emf.close();
	}

}
