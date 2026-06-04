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
		try {
			em.persist(user);
			em.getTransaction().commit();
		} catch (Exception e) {
			em.getTransaction().rollback();
			throw e;
		}
	}

	public User findById(int id) {
		return em.find(User.class, id);
	}

	public List<User> findAll() {
		return em.createQuery("from User", User.class).getResultList();
	}
}
