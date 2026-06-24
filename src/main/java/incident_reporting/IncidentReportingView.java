package incident_reporting;

import java.util.List;

public interface IncidentReportingView {

	void showError(String message);

	void showAllIncidents(List<Incident> incidents);

	void userLoggedIn(User user);

}
