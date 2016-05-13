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
    @ManyToOne(cascade = CascadeType.ALL)
    public User user;
    @ManyToOne(cascade = CascadeType.ALL)
    public CreditCard creditCard;
    @ManyToOne(cascade = CascadeType.ALL)
    public Product product;
    
	public Transaction(User user, CreditCard creditCard, Product product) {
		super();
		this.user = user;
		this.creditCard = creditCard;
		this.product = product;
	}
    
	public static Finder<Long, Transaction> find = new Finder<Long, Transaction>(Transaction.class);
    

}
