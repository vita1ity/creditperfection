package models;

import javax.persistence.Entity;
import javax.persistence.Id;

import com.avaje.ebean.Model;

import play.data.validation.Constraints.Required;

@Entity
public class Role extends Model {
	 
	@Id
	public int id;
	@Required
	public String name;
	
	public static Finder<Long, Role> find = new Finder<Long, Role>(Role.class);
    
    public static Role findByName(String name) {
		Role userRole = Role.find.where().eq("name", name).findUnique();
		return userRole;
	}
	
}
