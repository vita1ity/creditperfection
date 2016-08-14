package repository.impl;

import java.util.List;

import javax.inject.Inject;

import com.avaje.ebean.Model.Finder;

import models.Product;
import play.Configuration;
import repository.ProductRepository;

public class ProductRepositoryImpl implements ProductRepository {
	
	@Inject
	private Configuration conf;

	private Finder<Long, Product> find = new Finder<Long, Product>(Product.class);
	
	@Override
	public List<Product> getAll() {
		long trialProductId = conf.getLong("creditperfection.trial.productId");		
		List<Product> allProducts = find.where().ne("id", trialProductId).findList();
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

	@Override
	public void save(Product product) {
		product.save();
		
	}

	@Override
	public void update(Product product) {
		product.updateProductInfo(product);
		product.save();
		
	}

	@Override
	public boolean delete(Product product) {
		return product.delete();
	}
	
	
	
}
