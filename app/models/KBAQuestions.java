package models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

import com.avaje.ebean.Model;
import com.fasterxml.jackson.annotation.JsonBackReference;

@Entity
public class KBAQuestions extends Model {
	
	@Id
	private long id;
	
	@Column(nullable = false)
	private String url;
	
	@JsonBackReference
	@OneToOne
	@JoinColumn(nullable = false)
	private User user;
	
	public KBAQuestions() {
		
	}
	
	public KBAQuestions(String url, User user) {
		super();
		this.url = url;
		this.user = user;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	
}
