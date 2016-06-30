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
		if (productForm.id != null) {
			product.setId(Long.parseLong(productForm.id));
		}
		product.setName(productForm.name);
		product.setPrice(Double.parseDouble(productForm.price));
		product.setSalePrice(Double.parseDouble(productForm.salePrice));
		
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
	
}
