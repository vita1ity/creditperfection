package models;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToOne;

import com.avaje.ebean.Model;

@Entity
public class KBAQuestions extends Model {
	
	@Id
	public long id;
	
	public String url;
	
	@OneToOne
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
