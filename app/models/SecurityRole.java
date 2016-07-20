package models;

import javax.persistence.Entity;
import javax.persistence.Id;

import com.avaje.ebean.Model;

import be.objectify.deadbolt.java.models.Role;
import play.data.validation.Constraints.Required;

@Entity
public class SecurityRole extends Model implements Role {
	 
	@Id
	private int id;
	
	@Required
	private String name;
	
	public SecurityRole(int id, String name) {
		super();
		this.id = id;
		this.name = name;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setName(String name) {
		this.name = name;
	}


    @Override
	public String getName() {
		return name;
	}
	
}
