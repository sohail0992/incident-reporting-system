package com.msohailse.app.incident.repository;

import com.msohailse.app.incident.model.Incident;
import com.msohailse.app.incident.model.Tag;
import com.msohailse.app.incident.model.User;
import java.util.List;

public interface IncidentReportingRepository {
	void save(User user);

	User findUserByEmail(String email);

	User findUserById(int id);

	List<User> findAllUsers();

	void save(Tag tag);

	Tag findTagById(int id);

	Tag findTagByTitle(String title);

	List<Tag> findAllTags();

	void save(Incident incident);

	Incident findIncidentById(int id);

	List<Incident> findAllIncidents();

	List<Incident> findIncidentsByUser(User user);
}
