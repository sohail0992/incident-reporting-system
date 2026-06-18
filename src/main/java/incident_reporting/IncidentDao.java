package incident_reporting;

import java.util.List;

import javax.persistence.EntityManager;

public class IncidentDao {

	private EntityManager em;

	public IncidentDao(EntityManager em) {
		this.em = em;
	}

	public void save(Incident incident) {
		em.getTransaction().begin();
		try {
			em.persist(incident);
			em.getTransaction().commit();
		} catch (Exception e) {
			em.getTransaction().rollback();
			throw e;
		}
	}

	public Incident findById(int id) {
		return em.find(Incident.class, id);
	}

	public List<Incident> findAll() {
		return em.createQuery("from Incident", Incident.class).getResultList();
	}

	public List<Incident> findByUser(User user) {
		return em.createQuery("from Incident where reportedBy = :user", Incident.class)
				.setParameter("user", user)
				.getResultList();
	}

}
