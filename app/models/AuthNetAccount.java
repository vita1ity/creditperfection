package models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import com.avaje.ebean.Model;

import errors.ValidationError;

@Entity
public class AuthNetAccount extends Model implements Comparable<AuthNetAccount> {
	
	 @Id
	 public long id;
	 
	 @Column(nullable = false)
	 public String name;
	 
	 public String description;
	 
	 @Column(nullable = false)
	 public String loginId;
	 
	 @Column(nullable = false)
	 public String transactionKey;
	 
	 public boolean isLastUsed;
	 
	 public AuthNetAccount() {
		 
	 }
	 
	 public AuthNetAccount(String loginId, String transactionKey) {
		 this.loginId = loginId;
		 this.transactionKey = transactionKey;
	 }
	 
	 public static Finder<Long, AuthNetAccount> find = new Finder<Long, AuthNetAccount>(AuthNetAccount.class);

	@Override
	public String toString() {
		return "AuthorizeNetAccount [id=" + id + ", name=" + name + ", description=" + description + ", loginId="
				+ loginId + ", transactionKey=" + transactionKey + "]";
	}
	 
	
	public List<ValidationError> validate() {
		
		List<ValidationError> errors = new ArrayList<ValidationError>();

		Map<ValidationError, String> fieldErrorMap = new HashMap<ValidationError, String>();
		fieldErrorMap.put(new ValidationError("name", "Please enter Account Name"), name);
		fieldErrorMap.put(new ValidationError("loginId", "Please enter Login ID"), loginId);
		fieldErrorMap.put(new ValidationError("transactionKey", "Please enter Transaction Key"), transactionKey);
		
		for (Map.Entry<ValidationError, String> entry: fieldErrorMap.entrySet()) {
			String field = entry.getValue(); 
			if (field == null || field.equals("")) {
				errors.add(entry.getKey());
			}
		}
		
		if (errors.size() != 0) {
			return errors;
		}
		
		return null;
	}


	@Override
	public int compareTo(AuthNetAccount o) {
		
		return (int)(this.id - o.id);
		
	}
	
}
