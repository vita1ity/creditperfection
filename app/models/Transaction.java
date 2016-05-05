package models;

import com.avaje.ebean.Model;

import javax.persistence.*;

@Entity
public class Transaction extends Model {
    
	@Id
    public int id;
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
    
    
    

}
