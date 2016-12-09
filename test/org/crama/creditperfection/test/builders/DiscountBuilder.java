package org.crama.creditperfection.test.builders;

import java.time.LocalDate;

import models.Discount;
import models.Subscription;
import models.enums.DiscountStatus;
import models.enums.DiscountType;

public class DiscountBuilder {
	
	private long id = 0l;
	
	private Subscription subscription = new SubscriptionBuilder().build();
	
	private DiscountType discountType = DiscountType.WEEKLY_DISCOUNT;
	
	private double discountAmount = 0.00;
	
	private LocalDate startDate = LocalDate.now();
	
	private LocalDate endDate = LocalDate.now();
	
	private DiscountStatus discountStatus = DiscountStatus.ACTIVE; 
	
	public DiscountBuilder id(long id) {
		this.id = id;
		return this;
	}
	
	public DiscountBuilder subscription(Subscription subscription) {
		this.subscription = subscription;
		return this;
	}
	
	public DiscountBuilder discountType(DiscountType discountType) {
		this.discountType = discountType;
		return this;
	}
	
	public DiscountBuilder discountAmount(double discountAmount) {
		this.discountAmount = discountAmount;
		return this;
	}
	
	public DiscountBuilder startDate(LocalDate endDate) {
		this.endDate = endDate;
		return this;
	}
	
	public DiscountBuilder endDate(LocalDate endDate) {
		this.endDate = endDate;
		return this;
	}
	
	public DiscountBuilder discountStatus(DiscountStatus discountStatus) {
		this.discountStatus = discountStatus;
		return this;
	}
	
	public Discount build() {
		
		Discount discount = new Discount();
		
		discount.setId(id);
		discount.setSubscription(subscription);
		discount.setDiscountAmount(discountAmount);
		discount.setDiscountStatus(discountStatus);
		discount.setDiscountType(discountType);
		discount.setStartDate(startDate);
		discount.setEndDate(endDate);
		
		return discount;
	}
}
