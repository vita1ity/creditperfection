package models;

import com.avaje.ebean.Model;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.util.HashMap;
import java.util.List;

@Entity
public class PaymentGateway extends Model {
    @Id
    public int id;
    public String name;
}
