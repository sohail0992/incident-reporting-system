package com.msohailse.app.incident.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name="incidents")
public class Incident {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private int id;

	@Column(length=200, nullable=false)
	private String title;

	@Column(length=2000, nullable=true)
	private String description;

	@Column(nullable=false)
	@Enumerated(EnumType.STRING)
	private Severity severity;

	@Column(nullable=false)
	@Temporal(TemporalType.TIMESTAMP)
	private Date reportedAt;

	@Column(nullable=false)
	private boolean isClosed;

	@ManyToOne
	@JoinColumn(name="user_id", nullable=false)
	private User reportedBy;

	@ManyToOne
	@JoinColumn(name="tag_id", nullable=false)
	private Tag tag;

	private final StringNormalizer normalizer = new StringNormalizer();

	public Incident() {
		this.reportedAt = new Date();
		this.isClosed = false;
	}

	public Incident(String title, String description, Severity severity, User reportedBy, Tag tag) {
		// to prevent mutation we can throw exception from constructor
		this.title = validateAndNormalize(title);
		this.description = (description == null) ? null : normalizer.trimAllSpaces(description);
		this.severity = severity;
		this.reportedBy = reportedBy;
		this.tag = tag;
		this.reportedAt = new Date();
		this.isClosed = false;
	}

	public Incident(int id, String title, String description, Severity severity, User reportedBy, Tag tag) {
		this.id = id;
		this.title = validateAndNormalize(title);
		this.description = (description == null) ? null : normalizer.trimAllSpaces(description);
		this.severity = severity;
		this.reportedBy = reportedBy;
		this.tag = tag;
		this.reportedAt = new Date();
		this.isClosed = false;
	}

	public int getId() {
		return id;
	}

	public String getTitle() {
		return title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		if (description == null) {
			this.description = null;
			return;
		}
		this.description = normalizer.trimAllSpaces(description);
	}


	public Severity getSeverity() {
		return severity;
	}

	public void setSeverity(Severity severity) {
		if (severity == null) {
			throw new IllegalArgumentException("Empty severity");
		}
		this.severity = severity;
	}

	public Date getReportedAt() {
		return reportedAt;
	}

	public boolean isClosed() {
		return isClosed;
	}

	public void setIsClosed(boolean isClosed) {
		this.isClosed = isClosed;
	}

	public User getReportedBy() {
		return reportedBy;
	}

	public void setReportedBy(User reportedBy) {
		if (reportedBy == null) {
			throw new IllegalArgumentException("Reporter cannot be null");
		}
		this.reportedBy = reportedBy;
	}

	public Tag getTag() {
		return tag;
	}

	public void setTag(Tag tag) {
		if (tag == null) {
			throw new IllegalArgumentException("Tag cannot be null");
		}
		this.tag = tag;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("[").append(severity).append("] ").append(title)
		  .append(" Description: ").append(description)
		  .append(" Category: ").append(tag == null ? "null" : tag.getTagTitle())
		  .append(" at ").append(reportedAt);
		return sb.toString();
	}

	
	// helper methods

	private String validateAndNormalize(String value) {
		if (normalizer.isNullOrEmpty(value)) {
			throw new IllegalArgumentException("Empty title");
		}
		String normalized = normalizer.trimAllSpaces(value);
		if (normalizer.isNullOrEmpty(normalized)) {
			throw new IllegalArgumentException("Empty title");
		}
		return normalized;
	}

}
