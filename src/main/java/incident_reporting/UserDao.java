package incident_reporting;

import javax.persistence.EntityManager;
import java.util.List;

public class UserDao {

	private EntityManager em;

	public UserDao(EntityManager em) {
		this.em = em;
	}

	public void save(User user) {
		em.getTransaction().begin();
		em.persist(user);
		em.getTransaction().commit();
	}

	public User findById(int id) {
		return em.find(User.class, id);
	}

	public List<User> findAll() {
		return em.createQuery("from User", User.class).getResultList();
	}
}
