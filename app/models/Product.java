package models;

import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Id;

import com.avaje.ebean.Model;

@Entity
public class Product extends Model {
	
    @Id
    public int id;
    public String name;
    
    public double price;
    public double salePrice;
    
    public static Finder<Long, Product> find = new Finder<Long, Product>(Product.class);
    
    public static List<Product> getAllProducts() {
    	List<Product> allProducts = Product.find.all();
    	return allProducts;
    }
    
    public static Product getById(long id) {
    	Product product = Product.find.byId(id);
    	return product;
    }
    
}
