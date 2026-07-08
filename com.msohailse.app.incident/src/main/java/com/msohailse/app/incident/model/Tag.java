package com.msohailse.app.incident.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name = "tags")
public class Tag {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int id;

	@Transient
	private final StringNormalizer normalizer = new StringNormalizer();

	public Tag() {
	}

	public Tag(String tagTitle) {
		this.tagTitle = validateAndNormalize(tagTitle);
	}

	public Tag(int id, String tagTitle, String tagDescription) {
		this.id = id;
		this.tagTitle = validateAndNormalize(tagTitle);
		this.tagDescription = (tagDescription == null) ? null : normalizer.trimAllSpaces(tagDescription);
	}

	public int getId() {
		return id;
	}

	@Column(length = 100, nullable = false, unique = false)
	private String tagTitle;

	public String getTagTitle() {
		return tagTitle;
	}

	@Column(length = 500, nullable = true, unique = false)
	private String tagDescription;

	public String getTagDescription() {
		return tagDescription;
	}

	public void setTagDescription(String tagDescription) {
		if (tagDescription == null) {
			this.tagDescription = null;
			return;
		}
		this.tagDescription = normalizer.trimAllSpaces(tagDescription);
	}

	// helper methods

	private String validateAndNormalize(String value) {
		if (normalizer.isNullOrEmpty(value)) {
			throw new IllegalArgumentException("Empty tagTitle");
		}
		String normalized = normalizer.trimAllSpaces(value);
		if (normalizer.isNullOrEmpty(normalized)) {
			throw new IllegalArgumentException("Empty tagTitle");
		}
		return normalized;
	}

}
