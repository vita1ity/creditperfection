package org.crama.creditperfection.test.builders;

import forms.SubscriptionForm;
import models.enums.SubscriptionStatus;

public class SubscriptionFormBuilder {

	private String id = "0";
	
	private String userId = "1";
	
	private String creditCardId = "1";
	
	private String productId = "1";
	
	private SubscriptionStatus status = SubscriptionStatus.ACTIVE;
	
	public SubscriptionFormBuilder id(String id) {
		this.id = id;
		return this;
	}
	
	public SubscriptionFormBuilder userId(String userId) {
		this.userId = userId;
		return this;
	}
	
	public SubscriptionFormBuilder creditCardId(String creditCardId) {
		this.creditCardId = creditCardId;
		return this;
	}
	
	public SubscriptionFormBuilder productId(String productId) {
		this.productId = productId;
		return this;
	}
	
	public SubscriptionFormBuilder status(SubscriptionStatus status) {
		this.status = status;
		return this;
	}
	
	public SubscriptionForm build() {
		
		SubscriptionForm subscriptionForm = new SubscriptionForm();
		subscriptionForm.setId(id);
		subscriptionForm.setUserId(userId);
		subscriptionForm.setCardId(creditCardId);
		subscriptionForm.setProductId(productId);
		subscriptionForm.setStatus(status);
		
		return subscriptionForm;
	}
	
}
