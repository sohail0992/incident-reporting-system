package incident_reporting;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.EnumType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
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

	@ManyToMany
	@JoinTable(
		name="incident_tags",
		joinColumns=@JoinColumn(name="incident_id"),
		inverseJoinColumns=@JoinColumn(name="tag_id")
	)
	private List<Tag> tags = new ArrayList<>();

	public Incident() {
		this.reportedAt = new Date();
		this.isClosed = false;
	}

	public int getId() {
		return id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		checkIfEmpty(title, "title");
		String normalized = trimAllSpaces(title);
		checkIfEmpty(normalized, "title");
		this.title = normalized;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		if (description == null) {
			this.description = null;
			return;
		}
		this.description = trimAllSpaces(description);
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

	public List<Tag> getTags() {
		return tags;
	}

	public void addTag(Tag tag) {
		if (tag == null) {
			throw new IllegalArgumentException("Tag cannot be null");
		}
		tags.add(tag);
	}

	public void removeTag(Tag tag) {
		tags.remove(tag);
	}

	// helper methods

	private static boolean isNullOrEmpty(String s) {
		return s == null || s.isEmpty();
	}

	private void checkIfEmpty(String value, String fieldName) {
		if (isNullOrEmpty(value)) {
			throw new IllegalArgumentException("Empty " + fieldName);
		}
	}

	// we start from empty string builder and
	// if we found white space in original value we add one and mark lastWasSpace true
	// if we found another whitespace we don't append and keep the lastWasSpace true
	// we keep ignoring the white space if lastWasSpace = true until we get actual char
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
