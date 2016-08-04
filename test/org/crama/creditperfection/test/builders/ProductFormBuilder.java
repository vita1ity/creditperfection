package org.crama.creditperfection.test.builders;

import forms.ProductForm;

public class ProductFormBuilder {

	private String id = "0";
	
	private String name = "testProduct";
	
	private String price = "10.0";
	
	private String salePrice = "1.0";
	
	
	public ProductFormBuilder id(String id) {
		this.id = id;
		return this;
	}
	
	public ProductFormBuilder name(String name) {
		this.name = name;
		return this;
	}
	
	public ProductFormBuilder price(String price) {
		this.price = price;
		return this;
	}
	
	public ProductFormBuilder salePrice(String salePrice) {
		this.salePrice = salePrice;
		return this;
	}
	
	public ProductForm build() {
		
		ProductForm productForm = new ProductForm();
		
		productForm.setId(id);
		productForm.setName(name);
		productForm.setPrice(price);
		productForm.setSalePrice(salePrice);
		
		return productForm;
		
	}
	
}
