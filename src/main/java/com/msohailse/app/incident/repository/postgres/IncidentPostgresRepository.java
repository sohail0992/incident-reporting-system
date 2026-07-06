package com.msohailse.app.incident.repository.postgres;

import com.msohailse.app.incident.repository.IncidentRepository;
import com.msohailse.app.incident.model.Incident;
import com.msohailse.app.incident.model.User;
import javax.persistence.EntityManager;
import java.util.List;

public class IncidentPostgresRepository implements IncidentRepository {

	private final EntityManager em;

	public IncidentPostgresRepository(EntityManager em) {
		this.em = em;
	}

	@Override
	public void save(Incident incident) {
		em.persist(incident);
	}

	@Override
	public Incident findById(int id) {
		return em.find(Incident.class, id);
	}

	@Override
	public List<Incident> findAll() {
		return em.createQuery("select i from Incident i", Incident.class).getResultList();
	}

	@Override
	public List<Incident> findByUser(User user) {
		return em.createQuery("select i from Incident i where i.reportedBy = :user", Incident.class)
				.setParameter("user", user)
				.getResultList();
	}
}
