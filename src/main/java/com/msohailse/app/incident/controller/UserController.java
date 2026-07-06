package com.msohailse.app.incident.controller;

import com.msohailse.app.incident.TransactionManager;
import com.msohailse.app.incident.view.IncidentReportingView;
import com.msohailse.app.incident.model.User;

public class UserController {
	private final IncidentReportingView view;
	private final TransactionManager transactionManager;

	public UserController(TransactionManager transactionManager, IncidentReportingView view) {
		this.transactionManager = transactionManager;
		this.view = view;
	}

	public void login(String email, String password) {
		transactionManager.doInTransaction(repo -> {
			User user = repo.findUserByEmail(email);
			if (user == null) {
				view.showError("User not found");
				return null;
			}
			if (!user.getPassword().equals(password)) {
				view.showError("Invalid password");
				return null;
			}
			view.userLoggedIn(user);
			view.showAllIncidents(repo.findIncidentsByUser(user));
			return null;
		});
	}

	public void registerUser(String firstName, String lastName, String email, String password) {
		transactionManager.doInTransaction(repo -> {
			if (repo.findUserByEmail(email) != null) {
				view.showError("Email already registered");
				return null;
			}
			User user = new User();
			user.setFirstName(firstName);
			user.setLastName(lastName);
			user.setEmail(email);
			user.setPassword(password);
			repo.save(user);
			return null;
		});
	}
}
