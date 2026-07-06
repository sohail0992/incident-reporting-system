package com.msohailse.app.incident.controller;

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
			Tag tag = repo.findTagByTitle(tagTitle);
			if (tag == null) {
				tag = new Tag();
				tag.setTagTitle(tagTitle);
				repo.save(tag);
			}
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
