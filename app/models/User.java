package models;

import com.avaje.ebean.Model;

import javax.persistence.*;
import java.util.List;

@Entity
public class User extends Model {
    @Id
    public int id;
    public String first_name;
    public String last_name;
    @Column(unique = true)
    public String email;
    public String zip;
    public String password;
    public String token;
    public boolean active;
    @OneToMany(cascade = CascadeType.ALL)
    public List<CreditCard> credit_cards;
    @OneToMany(cascade = CascadeType.ALL)
    public List<Transaction> transactions;
}
