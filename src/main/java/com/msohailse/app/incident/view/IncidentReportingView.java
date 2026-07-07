package com.msohailse.app.incident.view;

import java.util.List;

import com.msohailse.app.incident.model.Incident;
import com.msohailse.app.incident.model.User;

public interface IncidentReportingView {

	void showError(String message);

	void showAllIncidents(List<Incident> incidents);

	void userLoggedIn(User user);

	void incidentAdded(Incident incident);

	void incidentRemoved(Incident incident);

	void userRegistered(User user);

}
