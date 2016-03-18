package models;

import com.avaje.ebean.Model;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Product extends Model {
    @Id
    public int id;
    public String type;
    public String name;
    public String description;
    public String price;
    public String sale_price;
}
