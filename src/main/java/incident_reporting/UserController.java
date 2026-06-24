package incident_reporting;

public class UserController {

	private UserRepository userRepository;
	private IncidentReportingView view;

	public UserController(UserRepository userRepository, IncidentReportingView view) {
		this.userRepository = userRepository;
		this.view = view;
	}

	public void login(String email, String password) {
		User user = userRepository.findByEmail(email);
		if (user == null) {
			view.showError("User not found");
			return;
		}
		if (!user.getPassword().equals(password)) {
			view.showError("Invalid password");
			return;
		}
		view.userLoggedIn(user);
	}

	public void registerUser(String firstName, String lastName, String email, String password) {
		if (userRepository.findByEmail(email) != null) {
			view.showError("Email already registered");
			return;
		}
		User user = new User();
		user.setFirstName(firstName);
		user.setLastName(lastName);
		user.setEmail(email);
		user.setPassword(password);
		userRepository.save(user);
	}

}
