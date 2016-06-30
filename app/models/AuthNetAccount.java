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
	 private long id;
	 
	 @Column(nullable = false)
	 private String name;
	 
	 private String description;
	 
	 @Column(nullable = false)
	 private String loginId;
	 
	 @Column(nullable = false)
	 private String transactionKey;
	 
	 private boolean isLastUsed;
	 
	 public AuthNetAccount() {
		 
	 }
	 
	 public AuthNetAccount(String loginId, String transactionKey) {
		 this.loginId = loginId;
		 this.transactionKey = transactionKey;
	 }
	 
	 public long getId() {
			return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getLoginId() {
		return loginId;
	}

	public void setLoginId(String loginId) {
		this.loginId = loginId;
	}

	public String getTransactionKey() {
		return transactionKey;
	}

	public void setTransactionKey(String transactionKey) {
		this.transactionKey = transactionKey;
	}

	public boolean getIsLastUsed() {
		return isLastUsed;
	}

	public void setIsLastUsed(boolean isLastUsed) {
		this.isLastUsed = isLastUsed;
	}
	 

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
