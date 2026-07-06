package com.msohailse.app.incident.repository.postgres;

import com.msohailse.app.incident.repository.IncidentReportingRepository;
import com.msohailse.app.incident.repository.postgres.UserPostgresRepository;
import com.msohailse.app.incident.repository.postgres.TagPostgresRepository;
import com.msohailse.app.incident.repository.postgres.IncidentPostgresRepository;
import com.msohailse.app.incident.model.Incident;
import com.msohailse.app.incident.model.Tag;
import com.msohailse.app.incident.model.User;
import javax.persistence.EntityManager;
import java.util.List;

public class IncidentReportingPostgresRepository implements IncidentReportingRepository {

	private final UserPostgresRepository userRepo;
	private final TagPostgresRepository tagRepo;
	private final IncidentPostgresRepository incidentRepo;

	public IncidentReportingPostgresRepository(EntityManager em) {
		this.userRepo = new UserPostgresRepository(em);
		this.tagRepo = new TagPostgresRepository(em);
		this.incidentRepo = new IncidentPostgresRepository(em);
	}

	@Override public void save(User user)              { userRepo.save(user); }
	@Override public User findUserByEmail(String email) { return userRepo.findByEmail(email); }
	@Override public User findUserById(int id)          { return userRepo.findById(id); }
	@Override public List<User> findAllUsers()          { return userRepo.findAll(); }

	@Override public void save(Tag tag)                    { tagRepo.save(tag); }
	@Override public Tag findTagById(int id)               { return tagRepo.findById(id); }
	@Override public Tag findTagByTitle(String title)      { return tagRepo.findByTitle(title); }
	@Override public List<Tag> findAllTags()               { return tagRepo.findAll(); }

	@Override public void save(Incident incident)              { incidentRepo.save(incident); }
	@Override public Incident findIncidentById(int id)         { return incidentRepo.findById(id); }
	@Override public List<Incident> findAllIncidents()         { return incidentRepo.findAll(); }
	@Override public List<Incident> findIncidentsByUser(User u) { return incidentRepo.findByUser(u); }
}
