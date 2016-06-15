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
    public long id;
	
    @ManyToOne
    @JoinColumn(nullable = false)
    public User user;
    
    @ManyToOne
    @JoinColumn(nullable = false)
    public CreditCard creditCard;
    
    @ManyToOne
    @JoinColumn(nullable = false)
    public Product product;
    
    @Column(nullable = false)
    public double amount;
    
    @Column(nullable = false)
    public String transactionId;
    
    @Column(nullable = false)
    public TransactionStatus status;
    
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

	public static Finder<Long, Transaction> find = new Finder<Long, Transaction>(Transaction.class);

	public static Transaction createTransaction(TransactionForm transactionForm) {
		
		User user = User.find.byId(Long.parseLong(transactionForm.userId));
		CreditCard creditCard = CreditCard.find.byId(Long.parseLong(transactionForm.cardId));
		Product product = Product.find.byId(Long.parseLong(transactionForm.productId));
		double amount = Double.parseDouble(transactionForm.amount);
		String transactionId = transactionForm.transactionId;
		
		TransactionStatus status = null;
		if (transactionForm.status != null) {
			status = transactionForm.status;
		}
		else {
			status = TransactionStatus.SUCCESSFUL;
		}
		
		Transaction transaction = new Transaction(user, creditCard, product, amount, transactionId, status);
		
		if (transactionForm.id != null){
			transaction.id = Long.parseLong(transactionForm.id);
		}
		return transaction;
	}
    

}
