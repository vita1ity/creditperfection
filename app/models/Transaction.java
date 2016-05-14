package models;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import com.avaje.ebean.Model;

@Entity
public class Transaction extends Model {
    
	@Id
    public long id;
    @ManyToOne(cascade = CascadeType.REFRESH)
    public User user;
    @ManyToOne(cascade = CascadeType.REFRESH)
    public CreditCard creditCard;
    @ManyToOne(cascade = CascadeType.REFRESH)
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
    

}
