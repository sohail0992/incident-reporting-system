package com.msohailse.app.incident.repository.postgres;

import com.msohailse.app.incident.repository.TagRepository;
import com.msohailse.app.incident.model.Tag;
import javax.persistence.EntityManager;
import java.util.List;

public class TagPostgresRepository implements TagRepository {

	private final EntityManager em;

	public TagPostgresRepository(EntityManager em) {
		this.em = em;
	}

	@Override
	public void save(Tag tag) {
		em.persist(tag);
	}

	@Override
	public Tag findById(int id) {
		return em.find(Tag.class, id);
	}

	public Tag findByTitle(String title) {
		List<Tag> results = em.createQuery("select t from Tag t where t.tagTitle = :title", Tag.class)
				.setParameter("title", title).getResultList();
		return results.isEmpty() ? null : results.get(0);
	}

	@Override
	public List<Tag> findAll() {
		return em.createQuery("select t from Tag t", Tag.class).getResultList();
	}
}
