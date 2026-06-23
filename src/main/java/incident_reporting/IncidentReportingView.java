package incident_reporting;

import java.util.List;

public interface IncidentReportingView {

	void showLoginError(String message);

	void showRegistrationError(String message);

	void showIncidents(List<Incident> incidents);

	void onLoginSuccess(User user);

}
