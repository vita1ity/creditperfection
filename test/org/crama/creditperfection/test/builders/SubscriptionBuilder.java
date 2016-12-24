package org.crama.creditperfection.test.builders;

import java.time.LocalDate;
import java.time.LocalDateTime;

import models.CreditCard;
import models.Discount;
import models.Product;
import models.Subscription;
import models.User;
import models.enums.SubscriptionStatus;

public class SubscriptionBuilder {

	private long id = 0l;
	
	private User user = new UserBuilder().build();
	
	private CreditCard creditCard = new CreditCardBuilder().build();
	
	private Product product = new ProductBuilder().build();
	
	private SubscriptionStatus status = SubscriptionStatus.ACTIVE;
	
	private LocalDateTime subscriptionDate = LocalDateTime.now().minusDays(61);
	
	private LocalDateTime lastChargeDate = LocalDateTime.now().minusDays(30);
	
	private Discount discount = null;
	
	private LocalDate renewFailedDate = null;
	
	public SubscriptionBuilder id(long id) {
		this.id = id;
		return this;
	}
	
	public SubscriptionBuilder user(User user) {
		this.user = user;
		return this;
	}
	
	public SubscriptionBuilder creditCard(CreditCard creditCard) {
		this.creditCard = creditCard;
		return this;
	}
	
	public SubscriptionBuilder product(Product product) {
		this.product = product;
		return this;
	}
	
	public SubscriptionBuilder status(SubscriptionStatus status) {
		this.status = status;
		return this;
	}
	
	public SubscriptionBuilder subscriptionDate(LocalDateTime subscriptionDate) {
		this.subscriptionDate = subscriptionDate;
		return this;
	}
	
	public SubscriptionBuilder lastChargeDate(LocalDateTime lastChargeDate) {
		this.lastChargeDate = lastChargeDate;
		return this;
	}
	
	public SubscriptionBuilder discount(Discount discount) {
		this.discount = discount;
		return this;
	}
	
	public SubscriptionBuilder renewFailedDate(LocalDate renewFailedDate) {
		this.renewFailedDate = renewFailedDate;
		return this;
	}
	
	public Subscription build() {
		
		Subscription subscription = new Subscription();
		subscription.setId(id);
		subscription.setUser(user);
		subscription.setCreditCard(creditCard);
		subscription.setProduct(product);
		subscription.setStatus(status);
		subscription.setSubscriptionDate(subscriptionDate);
		subscription.setLastChargeDate(lastChargeDate);
		subscription.setDiscount(discount);
		subscription.setRenewFailedDate(renewFailedDate);
		
		return subscription;
	}
	
}
