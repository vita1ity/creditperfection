package models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import com.avaje.ebean.Model;

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
	 
	 private boolean isEnabled;
	 
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

	public boolean getIsEnabled() {
		return isEnabled;
	}

	public void setIsEnabled(boolean isEnabled) {
		this.isEnabled = isEnabled;
	}

	@Override
	public String toString() {
		return "AuthorizeNetAccount [id=" + id + ", name=" + name + ", description=" + description + ", loginId="
				+ loginId + ", transactionKey=" + transactionKey + "isLAstUsed = " + isLastUsed + ", isEnabled = " + isEnabled + "]";
	}
	 

	@Override
	public int compareTo(AuthNetAccount o) {
		
		return (int)(this.id - o.id);
		
	}

	
	
	
	
}
