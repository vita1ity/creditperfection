package models;

import com.avaje.ebean.Model;

import javax.persistence.*;

@Entity
public class CreditCard extends Model {
    @Id
    public int id;
    public String digits;
    public String exp_date;
    public String cvv;
    @ManyToOne(cascade = CascadeType.ALL)
    public User user;
    public String address;
    public String zip;
    public String state;
}
