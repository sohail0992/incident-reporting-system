package incident_reporting;

import javax.persistence.EntityManager;
import java.util.List;

public class UserDao implements UserRepository {

	private EntityManager em;

	public UserDao(EntityManager em) {
		this.em = em;
	}

	public void save(User user) {
		em.persist(user);
	}

	public User findById(int id) {
		return em.find(User.class, id);
	}

	public List<User> findAll() {
		return em.createQuery("from User", User.class).getResultList();
	}

	public User findByEmail(String email) {
		List<User> results = em.createQuery("from User where email = :email", User.class)
				.setParameter("email", email)
				.getResultList();
		return results.isEmpty() ? null : results.get(0);
	}
}
