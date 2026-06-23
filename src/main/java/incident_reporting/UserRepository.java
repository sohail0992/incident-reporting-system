package incident_reporting;

import java.util.List;

public interface UserRepository {

	void save(User user);

	User findById(int id);

	List<User> findAll();

	User findByEmail(String email);

}
