package org.crama.creditperfection.test.services;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.hamcrest.collection.IsIterableContainingInAnyOrder.containsInAnyOrder;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import org.crama.creditperfection.test.builders.ProductBuilder;
import org.crama.creditperfection.test.builders.ProductFormBuilder;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import errors.ValidationError;
import forms.ProductForm;
import models.Product;
import play.Logger;
import repository.ProductRepository;
import services.ProductService;

public class ProductServiceTest {

	@Inject
	@InjectMocks
	private ProductService productService;
	
	@Mock
	private ProductRepository productRepositoryMock;
	
	@Before
	public void setUpCreateProduct_Success() {
		
		MockitoAnnotations.initMocks(this);
		
	}
	
	@Test
	public void testCreateProduct_Success() {
		
		ProductForm productForm = new ProductFormBuilder().build();
		
		Product product = productService.createProduct(productForm);
		
		assertTrue(product.getId() == 0l);
		assertTrue(product.getName().equals("testProduct"));
		assertTrue(product.getPrice() == 10.0);
		assertTrue(product.getSalePrice() == 1.0);
		
	}
	
	@Before
	public void setUpCreateProduct_InvalidFormat() {
		
		MockitoAnnotations.initMocks(this);
		
	}
	
	@Test(expected = NumberFormatException.class)
	public void testCreateProduct_InvalidFormat() {
		
		ProductForm productForm = new ProductFormBuilder()
				.price("invalid")
				.salePrice("invalid")
				.build();
		
		productService.createProduct(productForm);
		
	}
	
	@Before
	public void setUpGetById() throws Exception {
		
		MockitoAnnotations.initMocks(this);
	}
	
	@Test
	public void testGetById() {
		
		Product testProduct = new ProductBuilder().build();
		
		when(productRepositoryMock.getById(anyLong())).thenReturn(testProduct);
		
		Product product = productService.getById(0);
		assertTrue(product.getId() == 0l);
		assertTrue(product.getName().equals("testProduct"));
		assertTrue(product.getPrice() == 10.0);
		assertTrue(product.getSalePrice() == 1.0);
		
		verify(productRepositoryMock, times(1)).getById(anyLong());
		
	}
	
	@Before
	public void setUpGetAll() throws Exception {
		
		MockitoAnnotations.initMocks(this);
	}
	
	@Test
	public void testGetAll() {
		
		List<Product> allProducts = Arrays.asList(new ProductBuilder().build(),
				new ProductBuilder().id(1l).name("firstProduct").build(),
				new ProductBuilder().id(2l).name("secondProduct").price(10.0).salePrice(1.0).build());
		
		when(productRepositoryMock.getAll()).thenReturn(allProducts);
		
		List<Product> allProductsResult = productService.getAll();
		assertTrue(allProductsResult.size() == allProducts.size());
		for (int i = 0; i < allProductsResult.size(); i++) {
			assertTrue(allProductsResult.get(i).getId() == i);
		}
		
		assertTrue(allProductsResult.get(1).getName().equals("firstProduct"));
		assertTrue(allProductsResult.get(2).getName().equals("secondProduct"));
		assertTrue(allProductsResult.get(2).getPrice() == 10.0);
		assertTrue(allProductsResult.get(2).getSalePrice() == 1.0);
		
		verify(productRepositoryMock, times(1)).getAll();
	}

	@Before
	public void setUpCkeckIsEmpty_True() {
	
		MockitoAnnotations.initMocks(this);
		
	}
	
	@Test
	public void testCheckIsEmpty_True() {
		
		when(productRepositoryMock.checkIsEmpty()).thenReturn(true);
		
		boolean isEmpty = productService.checkIsEmpty();
		
		assertTrue(isEmpty == true);
		
		verify(productRepositoryMock, times(1)).checkIsEmpty();
		
	}
	
	@Before
	public void setUpCkeckIsEmpty_False() {
	
		MockitoAnnotations.initMocks(this);
		
	}
	
	@Test
	public void testCheckIsEmpty_False() {
		
		when(productRepositoryMock.checkIsEmpty()).thenReturn(false);
		
		boolean isEmpty = productService.checkIsEmpty();
		
		assertTrue(isEmpty == false);
		
		verify(productRepositoryMock, times(1)).checkIsEmpty();
		
	}
	
	
	@Test
	public void testValidation_Success() {
		
		ProductForm productForm = new ProductFormBuilder().build();
		
		List<ValidationError> errors = productForm.validate();
		assertNull(errors);
		
	}
	
	@Test
	public void testValidation_Empty_Fields() {
		
		ProductForm productForm = new ProductFormBuilder()
				.name("")
				.price("")
				.salePrice("")
				.build();
		
		List<ValidationError> errors = productForm.validate();
		
		
		assertTrue(errors.size() == 6);
		assertThat(errors, containsInAnyOrder(
				new ValidationError("name", "Please enter Product Name"),
				new ValidationError("price", "Please enter Product Price"),
				new ValidationError("salePrice", "Please enter Product Sale Price"),
				new ValidationError("name", "Product Name should contain at least 5 characers"),
				new ValidationError("price", "Product price should be numeric value"),
				new ValidationError("salePrice", "Product sale price should be numeric value")
			));
		
		
	}
	
	@Test
	public void testValidation_Empty_NamePriceNumericErrors() {
		
		ProductForm productForm = new ProductFormBuilder()
				.name("name")
				.price("invalid")
				.salePrice("invalid")
				.build();
		
		List<ValidationError> errors = productForm.validate();
		
		
		assertTrue(errors.size() == 3);
		assertThat(errors, containsInAnyOrder(
				new ValidationError("name", "Product Name should contain at least 5 characers"),
				new ValidationError("price", "Product price should be numeric value"),
				new ValidationError("salePrice", "Product sale price should be numeric value")
			));
		
		
	}
	
	@Test
	public void testValidation_Empty_PricesGreaterThanZeroErrors() {
		
		ProductForm productForm = new ProductFormBuilder()
				.name("product")
				.price("-10")
				.salePrice("-1.8")
				.build();
		
		List<ValidationError> errors = productForm.validate();
		
		
		assertTrue(errors.size() == 2);
		assertThat(errors, containsInAnyOrder(
				new ValidationError("price", "Product price should be greater than 0"),
				new ValidationError("salePrice", "Product sale price should be greater than 0")
			));
		
		
	}
	
}
