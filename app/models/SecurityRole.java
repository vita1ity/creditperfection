package models;

import javax.persistence.Entity;
import javax.persistence.Id;

import com.avaje.ebean.Model;

import be.objectify.deadbolt.java.models.Role;
import play.data.validation.Constraints.Required;

@Entity
public class SecurityRole extends Model implements Role {
	 
	@Id
	public int id;
	@Required
	public String name;
	
	public static Finder<Long, SecurityRole> find = new Finder<Long, SecurityRole>(SecurityRole.class);
    
    public static SecurityRole findByName(String name) {
		SecurityRole userRole = SecurityRole.find.where().eq("name", name).findUnique();
		return userRole;
	}

    @Override
	public String getName() {
		return name;
	}
	
}
