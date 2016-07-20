package org.crama.creditperfection.tests.builders;

import models.AuthNetAccount;

public class AuthNetAccountBuilder {

	private long id = 0l;
	
	private String name = "defaultAccount";
	
	private String description = "Default Account";
	
	private String loginId = "9yTxLt29j7Xb";
	
	private String transactionKey = "9GJJcp75mXY93f54";
	
	private boolean isLastUsed = true;
	
	
	public AuthNetAccountBuilder id(long id) {
		this.id = id;
		return this;
	}
	
	public AuthNetAccountBuilder name(String name) {
		this.name = name;
		return this;
	}
	
	public AuthNetAccountBuilder description(String description) {
		this.description = description;
		return this;
	}
	
	public AuthNetAccountBuilder loginId(String loginId) {
		this.loginId = loginId;
		return this;
	}
	
	public AuthNetAccountBuilder transactionKey(String transactionKey) {
		this.transactionKey = transactionKey;
		return this;
	}
	
	public AuthNetAccountBuilder isLastUsed(boolean isLastUsed) {
		this.isLastUsed = isLastUsed;
		return this;
	}
	
	public AuthNetAccount build() {
		
		AuthNetAccount account = new AuthNetAccount();
		account.setId(id);
		account.setName(name);
		account.setDescription(description);
		account.setLoginId(loginId);
		account.setTransactionKey(transactionKey);
		account.setIsLastUsed(isLastUsed);
		
		return account;
		
	}
}
