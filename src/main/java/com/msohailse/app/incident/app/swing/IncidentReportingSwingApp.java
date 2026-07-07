package com.msohailse.app.incident.app.swing;

import java.awt.EventQueue;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import com.msohailse.app.incident.JpaTransactionManager;
import com.msohailse.app.incident.controller.IncidentController;
import com.msohailse.app.incident.controller.UserController;
import com.msohailse.app.incident.view.swing.IncidentReportingSwingView;

public class IncidentReportingSwingApp {

	public static void main(String[] args) {
		EventQueue.invokeLater(() -> {
			EntityManagerFactory emf =
				Persistence.createEntityManagerFactory("incident_reporting");
			JpaTransactionManager transactionManager = new JpaTransactionManager(emf);
			IncidentReportingSwingView view = new IncidentReportingSwingView();
			UserController userController = new UserController(transactionManager, view);
			IncidentController incidentController = new IncidentController(transactionManager, view);
			view.setUserController(userController);
			view.setIncidentController(incidentController);
			view.setVisible(true);
		});
	}
}
