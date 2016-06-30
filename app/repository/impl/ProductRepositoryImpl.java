package repository.impl;

import java.util.List;

import com.avaje.ebean.Model.Finder;

import models.Product;
import repository.ProductRepository;

public class ProductRepositoryImpl implements ProductRepository {

	private Finder<Long, Product> find = new Finder<Long, Product>(Product.class);
	
	@Override
	public List<Product> getAll() {
		List<Product> allProducts = find.all();
    	return allProducts;
	}

	@Override
	public Product getById(long id) {
		Product product = find.byId(id);
    	return product;
	}

	@Override
	public boolean checkIsEmpty() {
		
		return find.findRowCount() == 0;
	}
	
	
	
}
