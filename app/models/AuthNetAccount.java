package models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import com.avaje.ebean.Model;

@Entity
public class AuthNetAccount extends Model {
	
	 @Id
	 public long id;
	 
	 @Column(nullable = false)
	 public String name;
	 
	 public String description;
	 
	 @Column(nullable = false)
	 public String loginId;
	 
	 @Column(nullable = false)
	 public String transactionKey;
	 
	 public static Finder<Long, AuthNetAccount> find = new Finder<Long, AuthNetAccount>(AuthNetAccount.class);

	@Override
	public String toString() {
		return "AuthorizeNetAccount [id=" + id + ", name=" + name + ", description=" + description + ", loginId="
				+ loginId + ", transactionKey=" + transactionKey + "]";
	}
	 
	
}
