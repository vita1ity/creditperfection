package org.crama.creditperfection.test.builders;

import forms.TransactionForm;
import models.enums.TransactionStatus;

public class TransactionFormBuilder {

	private String id = "0";
	
	private String userId = "1";
	
	private String creditCardId = "1";
	
	private String productId = "1";
			
	private String amount = "1.0";
	
	private String transactionId = "123";
	
	private TransactionStatus status = TransactionStatus.SUCCESSFUL;
	
	
	public TransactionFormBuilder id(String id) {
		this.id = id;
		return this;
	}
	
	public TransactionFormBuilder userId(String userId) {
		this.userId = userId;
		return this;
	}
	
	public TransactionFormBuilder creditCardId(String creditCardId) {
		this.creditCardId = creditCardId;
		return this;
	}
	
	public TransactionFormBuilder productId(String productId) {
		this.productId = productId;
		return this;
	}
	
	public TransactionFormBuilder amount(String amount) {
		this.amount = amount;
		return this;
	}
	
	public TransactionFormBuilder transactionId(String transactionId) {
		this.transactionId = transactionId;
		return this;
	}
	
	public TransactionFormBuilder status(TransactionStatus status) {
		this.status = status;
		return this;
	}
	
	public TransactionForm build() {
		
		TransactionForm transactionForm = new TransactionForm();
		transactionForm.setId(id);
		transactionForm.setUserId(userId);
		transactionForm.setCardId(creditCardId);
		transactionForm.setProductId(productId);
		transactionForm.setAmount(amount);
		transactionForm.setTransactionId(transactionId);
		transactionForm.setStatus(status);
	
		return transactionForm;
		
	}
	
}
