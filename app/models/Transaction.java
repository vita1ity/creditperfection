package models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.avaje.ebean.Model;

import forms.TransactionForm;
import models.enums.TransactionStatus;

@Entity
public class Transaction extends Model {
    
	@Id
	private long id;
	
    @ManyToOne
    @JoinColumn(nullable = false)
    private User user;
    
    @ManyToOne
    @JoinColumn(nullable = false)
    private CreditCard creditCard;
    
    @ManyToOne
    @JoinColumn(nullable = false)
    private Product product;
    
    @Column(nullable = false)
    private double amount;
    
    @Column(nullable = false)
    private String transactionId;
    
    @Column(nullable = false)
    private TransactionStatus status;
    
    
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public CreditCard getCreditCard() {
		return creditCard;
	}

	public void setCreditCard(CreditCard creditCard) {
		this.creditCard = creditCard;
	}

	public Product getProduct() {
		return product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public String getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}

	public TransactionStatus getStatus() {
		return status;
	}

	public void setStatus(TransactionStatus status) {
		this.status = status;
	}

	public Transaction(User user, CreditCard creditCard, Product product, double amount, 
			String transactionId, TransactionStatus status) {
		super();
		this.user = user;
		this.creditCard = creditCard;
		this.product = product;
		this.amount = amount;
		this.transactionId = transactionId;
		this.status = status;
	}
    
	public Transaction(long id, User user, CreditCard creditCard, Product product, double amount, 
			String transactionId, TransactionStatus status) {
		super();
		this.id = id;
		this.user = user;
		this.creditCard = creditCard;
		this.product = product;
		this.amount = amount;
		this.transactionId = transactionId;
		this.status = status;
	}

	
}
