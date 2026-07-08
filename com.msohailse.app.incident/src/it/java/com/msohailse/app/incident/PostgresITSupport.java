package com.msohailse.app.incident;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.testcontainers.containers.PostgreSQLContainer;

public final class PostgresITSupport {

	private PostgresITSupport() {
	}

	public static EntityManagerFactory createEntityManagerFactory(PostgreSQLContainer<?> postgres) {
		Map<String, String> properties = new HashMap<>();
		properties.put("javax.persistence.jdbc.url", postgres.getJdbcUrl());
		properties.put("javax.persistence.jdbc.user", postgres.getUsername());
		properties.put("javax.persistence.jdbc.password", postgres.getPassword());
		return Persistence.createEntityManagerFactory("incident_reporting", properties);
	}

}
