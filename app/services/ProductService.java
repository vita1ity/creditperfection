package services;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import forms.ProductForm;
import models.Product;
import repository.ProductRepository;

@Singleton
public class ProductService {

	@Inject
	private ProductRepository productRepository;
	
	public Product createProduct(ProductForm productForm) {
		Product product = new Product();
		if (productForm.getId() != null) {
			product.setId(Long.parseLong(productForm.getId()));
		}
		product.setName(productForm.getName());
		product.setPrice(Double.parseDouble(productForm.getPrice()));
		product.setSalePrice(Double.parseDouble(productForm.getSalePrice()));
		product.setTrialPeriod(Integer.parseInt(productForm.getTrialPeriod()));
		return product;
	}
	
	public List<Product> getAll() {
		return productRepository.getAll();
	}
	
	public Product getById(long id) {
		return productRepository.getById(id);
	}
	
	public boolean checkIsEmpty() {
		return productRepository.checkIsEmpty();
	}

	public void save(Product product) {
		productRepository.save(product);		
	}

	public void update(Product product) {
		productRepository.update(product);		
	}

	public boolean delete(Product product) {
		return productRepository.delete(product);
	}

	public void editProduct(Product productDB, Product product) {
		productDB.setName(product.getName());
		productDB.setPrice(product.getPrice());
		productDB.setSalePrice(product.getSalePrice());
		productDB.setTrialPeriod(product.getTrialPeriod());
	}
	
}
