package models;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import com.avaje.ebean.Model;

import forms.TransactionForm;

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
    
	public Transaction(User user, CreditCard creditCard, Product product) {
		super();
		this.user = user;
		this.creditCard = creditCard;
		this.product = product;
	}
    
	public Transaction(long transactionId, User user, CreditCard creditCard, Product product) {
		super();
		this.id = transactionId;
		this.user = user;
		this.creditCard = creditCard;
		this.product = product;
	}

	public static Finder<Long, Transaction> find = new Finder<Long, Transaction>(Transaction.class);

	public static Transaction createTransaction(TransactionForm transactionForm) {
		
		User user = User.find.byId(Long.parseLong(transactionForm.userId));
		CreditCard creditCard = CreditCard.find.byId(Long.parseLong(transactionForm.cardId));
		Product product = Product.find.byId(Long.parseLong(transactionForm.productId));
		Transaction transaction = new Transaction(user, creditCard, product);
		
		if (transactionForm.transactionId != null){
			transaction.id = Long.parseLong(transactionForm.transactionId);
		}
		return transaction;
	}
    

}
