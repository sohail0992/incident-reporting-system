package incident_reporting;

import java.util.List;

import javax.persistence.EntityManager;

public class TagDao {
	private EntityManager em;

	public TagDao(EntityManager em) {
		this.em = em;
	}
	
	public void save(Tag tag) {
		em.getTransaction().begin();
		try {
			em.persist(tag);
			em.getTransaction().commit();
		} catch (Exception e) {
			em.getTransaction().rollback();
			throw e;
		}
	}

	public Tag findById(int id) {
		return em.find(Tag.class, id);
	}

	public List<Tag> findAll() {
		return em.createQuery("from Tag", Tag.class).getResultList();
	}
	


}
