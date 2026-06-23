package incident_reporting;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class Main {

	public static void main(String[] args) {
	    // 1. Create a factory (expensive, do once)
	    EntityManagerFactory emf = Persistence.createEntityManagerFactory("incident_reporting");
	    
	    // 2. Create a manager from the factory (cheap, do many times)
	    EntityManager em = emf.createEntityManager();
	    
	    // 3. Create a DAO with that manager
	    UserDao userDAO = new UserDao(em);
	    
	    // 4. Create a User object (still in memory, not in DB)
	    User user = new User();
	    user.setFirstName("Muhammad");
	    user.setLastName("Sohail");
	    user.setEmail("sohail1@example.com");
	    user.setPassword("SecurePass123");
	    
	    // 5. Save it to the database
	    userDAO.save(user);
	    System.out.println("User saved with ID: " + user.getId());  // Prints: 1
	    
	    // 6. Retrieve it back from the database
	    User userRetrieved = userDAO.findById(user.getId());
	    System.out.println(userRetrieved.getFirstName());  // Prints: Muhammad
	    
	    // 7. for tag testing create dao instance with em
	    TagDao tagDAO = new TagDao(em);
	    
	    // normal tag
	    Tag tag = new Tag();
	    tag.setTagTitle("fire Alert");
	    tag.setTagDescription("I am seeing some smoke coming out of the building");
	    
	    tagDAO.save(tag);
	    
	    System.out.println("Tag saved with ID: " + tag.getId());  // Prints: 1
	    
	    // 6. Retrieve it back from the database
	    Tag tagRetrieved = tagDAO.findById(tag.getId());
	    System.out.println(tagRetrieved.getTagDescription());  // Prints: description of tag
	    
	    
	    // 7. Clean up
	    em.close();
	    emf.close();
	}

}
