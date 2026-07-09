package com.msohailse.app.incident.repository;

import java.util.List;

import com.msohailse.app.incident.model.User;

public interface UserRepository {

	void save(User user);

	User findById(int id);

	User findByEmail(String email);

	List<User> findAll();
}