package com.msohailse.app.incident.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "tags")
public class Tag {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private int id;

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
		this.tagDescription = trimAllSpaces(tagDescription);
	}

	// helper methods
	// check if empty or null

	private static boolean isNullOrEmpty(String s) {
		return s == null || s.isEmpty();
	}

	private String validateAndNormalize(String value) {
		if (isNullOrEmpty(value)) {
			throw new IllegalArgumentException("Empty tagTitle");
		}
		String normalized = trimAllSpaces(value);
		if (isNullOrEmpty(normalized)) {
			throw new IllegalArgumentException("Empty tagTitle");
		}
		return normalized;
	}

	// we start from empty string builder and
	// if we found white space in original value we add one and mark lastWasSpace
	// true
	// if we found another whitespace we don't append and keep the lastWasSpace true
	// we keep ignoring the white space if lastWasSpace = true until we get actual
	// char
	private String trimAllSpaces(String value) {
		StringBuilder sb = new StringBuilder();
		boolean lastWasSpace = false;
		for (int i = 0; i < value.length(); i++) {
			char c = value.charAt(i);
			if (Character.isWhitespace(c)) {
				if (!lastWasSpace && sb.length() > 0)
					sb.append(' ');
				lastWasSpace = true;
			} else {
				sb.append(c);
				lastWasSpace = false;
			}
		}
		if (sb.length() > 0 && sb.charAt(sb.length() - 1) == ' ')
			sb.deleteCharAt(sb.length() - 1);
		return sb.toString();
	}

}
