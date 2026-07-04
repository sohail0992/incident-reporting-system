package incident_reporting;

public class UserController {

	private TransactionManager transactionManager;
	private IncidentReportingView view;

	public UserController(TransactionManager transactionManager, IncidentReportingView view) {
		this.transactionManager = transactionManager;
		this.view = view;
	}

	public void login(String email, String password) {
		transactionManager.doInTransaction(userRepository -> {
			User user = userRepository.findByEmail(email);
			if (user == null) {
				view.showError("User not found");
				return null;
			}
			if (!user.getPassword().equals(password)) {
				view.showError("Invalid password");
				return null;
			}
			view.userLoggedIn(user);
			return null;
		});
	}

	public void registerUser(String firstName, String lastName, String email, String password) {
		transactionManager.doInTransaction(userRepository -> {
			if (userRepository.findByEmail(email) != null) {
				view.showError("Email already registered");
				return null;
			}
			User user = new User();
			user.setFirstName(firstName);
			user.setLastName(lastName);
			user.setEmail(email);
			user.setPassword(password);
			userRepository.save(user);
			return null;
		});
	}

}
