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
    public CreditCard credit_card;
    @ManyToOne(cascade = CascadeType.ALL)
    public Product product;

}
