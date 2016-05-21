package models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import com.avaje.ebean.Model;
import com.fasterxml.jackson.annotation.JsonIgnore;

import errors.ValidationError;
import forms.ProductForm;

@Entity
public class Product extends Model {
	
    @Id
    public long id;
    public String name;
    
    public double price;
    public double salePrice;
    
    @JsonIgnore
    @OneToMany(cascade = CascadeType.ALL)
    public List<Transaction> transactions;
    
    public static Finder<Long, Product> find = new Finder<Long, Product>(Product.class);
    
    public static List<Product> getAllProducts() {
    	List<Product> allProducts = Product.find.all();
    	return allProducts;
    }
    
    public static Product getById(long id) {
    	Product product = Product.find.byId(id);
    	return product;
    }

	public List<ValidationError> validate() {
		
		List<ValidationError> errors = new ArrayList<ValidationError>();
		
		Map<ValidationError, String> fieldErrorMap = new HashMap<ValidationError, String>();
		fieldErrorMap.put(new ValidationError("name", "Please enter Product Name"), name);
		fieldErrorMap.put(new ValidationError("price", "Please enter Product Price"), Double.toString(price));
		fieldErrorMap.put(new ValidationError("salePrice", "Please enter Product Sale Price"), Double.toString(salePrice));
		
		for (Map.Entry<ValidationError, String> entry: fieldErrorMap.entrySet()) {
			String field = entry.getValue(); 
			if (field == null || field.equals("")) {
				errors.add(entry.getKey());
			}
		}
		
		if (name.length() < 5) {
			ValidationError error = new ValidationError("name", "Product Name should contain at least 5 characers");
			errors.add(error);
		}
		
		if (errors.size() != 0) {
			return errors;
		}
		return null;
	}

	@Override
	public String toString() {
		return "Product [id=" + id + ", name=" + name + ", price=" + price + ", salePrice=" + salePrice + "]";
	}

	public void updateProductInfo(Product product) {
		
		this.name = product.name;
		this.price = product.price;
		this.salePrice = product.salePrice;
		
	}

	public static Product createProduct(ProductForm productForm) {
		Product product = new Product();
		if (productForm.id != null) {
			product.id = Long.parseLong(productForm.id);
		}
		product.name = productForm.name;
		product.price = Double.parseDouble(productForm.price);
		product.salePrice = Double.parseDouble(productForm.salePrice);
		
		return product;
	}
	
}
