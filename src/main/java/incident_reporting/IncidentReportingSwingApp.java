package incident_reporting;

import java.awt.EventQueue;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

public class IncidentReportingSwingApp {

	public static void main(String[] args) {
		EventQueue.invokeLater(() -> {
			EntityManagerFactory emf = Persistence.createEntityManagerFactory("incident_reporting");

			TransactionManager transactionManager = new JpaTransactionManager(emf);

			IncidentReportingSwingView view = new IncidentReportingSwingView();
			UserController userController = new UserController(transactionManager, view);
			view.setUserController(userController);
			view.setVisible(true);
		});
	}

}
