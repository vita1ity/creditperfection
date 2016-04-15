package com.mnt.adattration.data;

import java.util.ArrayList;
import java.util.List;

public class Customer {

	public String customerId;
	
	// One customer can have many payment methods. 
	public List<String> paymentIds;
	
	public Customer(String customerId) {
		this.customerId = customerId;
	}
	
	public Customer addPaymentId(String paymentId) {
		if(paymentIds == null) {
			paymentIds = new ArrayList<String>();
		}
		paymentIds.add(paymentId);
		
		return this;
	}
}
