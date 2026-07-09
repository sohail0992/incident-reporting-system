package com.msohailse.app.incident.repository.postgres;

import com.msohailse.app.incident.model.User;
import com.msohailse.app.incident.repository.UserRepository;
import javax.persistence.EntityManager;
import java.util.List;

public class UserPostgresRepository implements UserRepository {

	private final EntityManager em;

	public UserPostgresRepository(EntityManager em) {
		this.em = em;
	}

	@Override
	public void save(User user) {
		if (em.contains(user) || user.getId() != 0) {
			em.merge(user);
		} else {
			em.persist(user);
		}
	}

	@Override
	public User findById(int id) {
		return em.find(User.class, id);
	}

	@Override
	public List<User> findAll() {
		return em.createQuery("select u from User u", User.class).getResultList();
	}

	@Override
	public User findByEmail(String email) {
		List<User> results = em.createQuery("select u from User u where u.email = :email", User.class)
				.setParameter("email", email).getResultList();
		return results.isEmpty() ? null : results.get(0);
	}
}