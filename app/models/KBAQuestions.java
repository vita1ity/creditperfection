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
	public long id;
	
	@Column(nullable = false)
	public String url;
	
	@JsonBackReference
	@OneToOne
	@JoinColumn(nullable = false)
	public User user;
	
	public KBAQuestions() {
		
	}
	
	public KBAQuestions(String url, User user) {
		super();
		this.url = url;
		this.user = user;
	}

	public static Finder<Long, KBAQuestions> find = new Finder<Long, KBAQuestions>(KBAQuestions.class);
	
}
