package org.crama.creditperfection.test.builders;

import models.Product;

public class ProductBuilder {

	private long id = 0l;
	
	private String name = "testProduct";
	
	private double price = 10.0;
	
	private double salePrice = 1.0;
	
	private int trialPeriod = 7;
	
	public ProductBuilder id(long id) {
		this.id = id;
		return this;
	}
	
	public ProductBuilder name(String name) {
		this.name = name;
		return this;
	}
	
	public ProductBuilder price(double price) {
		this.price = price;
		return this;
	}
	
	public ProductBuilder salePrice(double salePrice) {
		this.salePrice = salePrice;
		return this;
	}
	
	public ProductBuilder trialPeriod(int trialPeriod) {
		this.trialPeriod = trialPeriod;
		return this;
	}
	
	public Product build() {
		
		Product product = new Product();
		
		product.setId(id);
		product.setName(name);
		product.setPrice(price);
		product.setSalePrice(salePrice);
		product.setTrialPeriod(trialPeriod);
		
		return product;
		
	}
	
}
