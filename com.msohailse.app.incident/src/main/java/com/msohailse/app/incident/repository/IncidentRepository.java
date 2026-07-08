package com.msohailse.app.incident.repository;

import com.msohailse.app.incident.model.Incident;
import com.msohailse.app.incident.model.User;
import java.util.List;

public interface IncidentRepository {
	void save(Incident incident);
	Incident findById(int id);
	List<Incident> findAll();
	List<Incident> findByUser(User user);
}
