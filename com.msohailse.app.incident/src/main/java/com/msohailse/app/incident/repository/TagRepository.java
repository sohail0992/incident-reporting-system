package com.msohailse.app.incident.repository;

import com.msohailse.app.incident.model.Tag;
import java.util.List;

public interface TagRepository {
	void save(Tag tag);
	Tag findById(int id);
	List<Tag> findAll();
}
