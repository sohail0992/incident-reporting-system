package com.msohailse.app.incident.app.swing;

import java.awt.EventQueue;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import com.msohailse.app.incident.JpaTransactionManager;
import com.msohailse.app.incident.controller.IncidentController;
import com.msohailse.app.incident.controller.UserController;
import com.msohailse.app.incident.view.swing.IncidentReportingSwingView;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

@Command(mixinStandardHelpOptions = true)
public class IncidentReportingSwingApp implements Callable<Void> {

	@Option(names = { "--db-host" }, description = "PostgreSQL host address")
	private String dbHost = "localhost";

	@Option(names = { "--db-port" }, description = "PostgreSQL host port")
	private int dbPort = 5432;

	@Option(names = { "--db-name" }, description = "Database name")
	private String dbName = "incident_db";

	@Option(names = { "--db-user" }, description = "Database user")
	private String dbUser = "incident_user";

	@Option(names = { "--db-password" }, description = "Database password")
	private String dbPassword = "";

	public static void main(String[] args) {
		new CommandLine(new IncidentReportingSwingApp()).execute(args);
	}

	@Override
	public Void call() {
		EventQueue.invokeLater(() -> {
			try {
				Map<String, String> properties = new HashMap<>();
				properties.put("javax.persistence.jdbc.url",
						"jdbc:postgresql://" + dbHost + ":" + dbPort + "/" + dbName);
				properties.put("javax.persistence.jdbc.user", dbUser);
				properties.put("javax.persistence.jdbc.password", dbPassword);

				EntityManagerFactory emf = Persistence.createEntityManagerFactory("incident_reporting", properties);
				JpaTransactionManager transactionManager = new JpaTransactionManager(emf);
				IncidentReportingSwingView view = new IncidentReportingSwingView();
				UserController userController = new UserController(transactionManager, view);
				IncidentController incidentController = new IncidentController(transactionManager, view);
				view.setUserController(userController);
				view.setIncidentController(incidentController);
				view.setVisible(true);
			} catch (Exception e) {
				Logger.getLogger(getClass().getName()).log(Level.SEVERE, "Exception", e);
			}
		});
		return null;
	}
}
