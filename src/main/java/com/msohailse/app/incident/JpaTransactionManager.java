package com.msohailse.app.incident;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import com.msohailse.app.incident.repository.postgres.IncidentReportingPostgresRepository;

public class JpaTransactionManager implements TransactionManager {

	private final EntityManagerFactory emf;

	public JpaTransactionManager(EntityManagerFactory emf) {
		this.emf = emf;
	}

	@Override
	public <T> T doInTransaction(TransactionCode<T> code) {
		EntityManager em = emf.createEntityManager();
		try {
			em.getTransaction().begin();
			T result = code.apply(new IncidentReportingPostgresRepository(em));
			em.getTransaction().commit();
			return result;
		} catch (RuntimeException e) {
			if (em.getTransaction().isActive()) {
				em.getTransaction().rollback();
			}
			throw e;
		} finally {
			em.close();
		}
	}
}
