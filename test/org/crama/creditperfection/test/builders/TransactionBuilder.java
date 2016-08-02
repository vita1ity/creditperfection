package org.crama.creditperfection.test.builders;

import models.Transaction;

import models.CreditCard;
import models.Product;
import models.User;
import models.enums.TransactionStatus;

public class TransactionBuilder {

	private long id = 0l;
	
	private User user = new UserBuilder().build();
	
	private CreditCard creditCard = new CreditCardBuilder().build();
	
	private Product product = new ProductBuilder().build();
			
	private double amount = 1.0;
	
	private String transactionId = "123456789";
	
	private TransactionStatus status = TransactionStatus.SUCCESSFUL;
	
	
	public TransactionBuilder id(long id) {
		this.id = id;
		return this;
	}
	
	public TransactionBuilder user(User user) {
		this.user = user;
		return this;
	}
	
	public TransactionBuilder creditCard(CreditCard creditCard) {
		this.creditCard = creditCard;
		return this;
	}
	
	public TransactionBuilder product(Product product) {
		this.product = product;
		return this;
	}
	
	public TransactionBuilder amount(double amount) {
		this.amount = amount;
		return this;
	}
	
	public TransactionBuilder transactionId(String transactionId) {
		this.transactionId = transactionId;
		return this;
	}
	
	public TransactionBuilder status(TransactionStatus status) {
		this.status = status;
		return this;
	}
	
	public Transaction build() {
		
		Transaction transaction = new Transaction();
		transaction.setId(id);
		transaction.setUser(user);
		transaction.setCreditCard(creditCard);
		transaction.setProduct(product);
		transaction.setAmount(amount);
		transaction.setTransactionId(transactionId);
		transaction.setStatus(status);
	
		return transaction;
		
	}
	
}
