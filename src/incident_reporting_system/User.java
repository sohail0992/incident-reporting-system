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

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return this.lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getEmail() {
		return this.Email;
	}

	public void setEmail(String email) {
		Email = email;
	}

	public String getPassword() {
		return this.Password;
	}

	public void setPassword(String password) {
		this.Password = password;
	}

}
