package repository;

import java.util.List;

import com.google.inject.ImplementedBy;

import models.Product;
import repository.impl.ProductRepositoryImpl;

@ImplementedBy(ProductRepositoryImpl.class)
public interface ProductRepository {

	List<Product> getAll();

	Product getById(long id);

	boolean checkIsEmpty();

	void save(Product product);

	void update(Product product);

	boolean delete(Product product);

}
