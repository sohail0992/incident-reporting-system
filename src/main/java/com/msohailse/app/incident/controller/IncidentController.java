package com.msohailse.app.incident.controller;

import java.security.cert.PKIXRevocationChecker.Option;
import java.util.Optional;

import com.msohailse.app.incident.TransactionManager;
import com.msohailse.app.incident.model.Incident;
import com.msohailse.app.incident.model.Severity;
import com.msohailse.app.incident.model.Tag;
import com.msohailse.app.incident.model.User;
import com.msohailse.app.incident.view.IncidentReportingView;

public class IncidentController {

	private final IncidentReportingView view;
	private final TransactionManager transactionManager;

	public IncidentController(TransactionManager transactionManager, IncidentReportingView view) {
		this.transactionManager = transactionManager;
		this.view = view;
	}

	public void reportIncident(String title, String description, Severity severity, String tagTitle, User loggedInUser) {
		transactionManager.doInTransaction(repo -> {
			// like by the book e optional.of but here in class to avoid
			// static null
			Tag tag = Optional.ofNullable(repo.findTagByTitle(tagTitle))
					.orElseGet(() -> {
	                    Tag newTag = new Tag(tagTitle);
	                    repo.save(newTag);
	                    return newTag;
	                });
			Incident incident = new Incident();
			incident.setTitle(title);
			incident.setDescription(description);
			incident.setSeverity(severity);
			incident.setReportedBy(loggedInUser);
			incident.setTag(tag);
			repo.save(incident);
			view.incidentAdded(incident);
			return null;
		});
	}
}
