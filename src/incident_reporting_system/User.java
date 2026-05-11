package incident_reporting_system;

public class User {
	private int id;
	private String firstName;
	private String lastName;
	private String Email;
	private String Password;
	private static int lastId = 0;
	
	public User() {
		this.id = ++lastId;
	}

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getFirstName() {
		return this.firstName;
	}

	public void setFirstName(String firstName) throws Exception {
		validateNameString(firstName);
		this.firstName = firstName;
	}

	public String getLastName() {
		return this.lastName;
	}

	public void setLastName(String lastName) {
		validateNameString(lastName);
		this.lastName = lastName;
	}

	public String getEmail() {
		return this.Email;
	}

	public void setEmail(String email) {
		validateEmailString(email);
		Email = email;
	}

	public String getPassword() {
		return this.Password;
	}

	public void setPassword(String password) {
		validatePasswordString(password);
		this.Password = password;
	}
	
	
	// helper methods can be moved to a separate utility class if needed in future
	/**
	 * DRY (don't repeat principle) we will use same function to validate both first and last name since they have same validation rules.
     * Helper method to validate name strings.
     * Throws IllegalArgumentException if validation fails.
     */
	private void validateNameString(String name) {
		if (name == null || name.trim().isEmpty()) {
			// name is empty 
			throw new IllegalArgumentException("Empty name");
		}
		int i = 0;
		// since the name is not empty now we can look further
		while (i < name.length()) {
			if (Character.isWhitespace(name.charAt(i))) {
				throw new IllegalArgumentException("White space found");
			}
			i++;
		}
	}

	// helper method to validate email string
	private void validateEmailString(String email) {
		// email validation logic can be implemented here
		if (email == null || email.trim().isEmpty()) {
			// email is empty 
			throw new IllegalArgumentException("Empty email");
		}
		// further email validation can be added here (e.g. regex check for valid email format)
		email = email.trim();
		if (!email.contains("@") || !email.contains(".")) {
			throw new IllegalArgumentException("Invalid email format");
		}
	}	

	// helper method to validate password
	// password has to be 
	private void validatePasswordString(String password) {
		if (password == null || password.isEmpty()) {
			throw new IllegalArgumentException("Empty password");
		}
		if (password.length() < 8) {
			throw new IllegalArgumentException("Password too short");
		}
		boolean hasDigit = false;
		boolean hasUpper = false;
		for (int i = 0; i < password.length(); i++) {
			char c = password.charAt(i);
			if (Character.isDigit(c)) hasDigit = true;
			if (Character.isUpperCase(c)) hasUpper = true;
		}
		if (!hasDigit) {
			throw new IllegalArgumentException("Password must contain a digit");
		}
		if (!hasUpper) {
			throw new IllegalArgumentException("Password must contain an uppercase letter");
		}
	}

}
