package incident_reporting;

import java.awt.EventQueue;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class IncidentReportingSwingApp {

	public static void main(String[] args) {
		EventQueue.invokeLater(() -> {
			EntityManagerFactory emf = Persistence.createEntityManagerFactory("incident_reporting");
			EntityManager em = emf.createEntityManager();

			UserRepository userRepository = new UserDao(em);

			IncidentReportingSwingView view = new IncidentReportingSwingView();
			UserController userController = new UserController(userRepository, view);
			view.setUserController(userController);
			view.setVisible(true);
		});
	}

}
