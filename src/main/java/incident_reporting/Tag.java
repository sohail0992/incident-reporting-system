package incident_reporting;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;


@Entity
@Table(name="tags")
public class Tag {
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private int id;
	

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@Column(length=100, nullable=false, unique=false)
	private String tagTitle;

	public String getTagTitle() {
		return tagTitle;
	}

	public void setTagTitle(String tagTitle) {
		this.tagTitle = tagTitle;
	}

	@Column(length=500, nullable=true, unique=false)
	private String tagDescription;
	

	public String getTagDescription() {
		return tagDescription;
	}

	public void setTagDescription(String tagDescription) {
		this.tagDescription = tagDescription;
	}
	
}
