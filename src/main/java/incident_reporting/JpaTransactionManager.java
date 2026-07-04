package incident_reporting;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;

public class JpaTransactionManager implements TransactionManager {

	private EntityManagerFactory emf;

	public JpaTransactionManager(EntityManagerFactory emf) {
		this.emf = emf;
	}

	@Override
	public <T> T doInTransaction(UserTransactionCode<T> code) {
		EntityManager em = emf.createEntityManager();
		try {
			em.getTransaction().begin();
			T result = code.apply(new UserDao(em));
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
