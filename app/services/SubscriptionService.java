package services;

import java.time.LocalDateTime;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import forms.SubscriptionForm;
import models.CreditCard;
import models.Product;
import models.Subscription;
import models.User;
import models.enums.SubscriptionStatus;
import repository.SubscriptionRepository;

@Singleton
public class SubscriptionService {
	
	@Inject
	private SubscriptionRepository subscriptionRepository;
	
	@Inject 
	private UserService userService;
	
	@Inject 
	private CreditCardService creditCardService;
	
	@Inject 
	private ProductService productService;
	
	public Subscription createSubscription(SubscriptionForm subscriptionForm) {

		User user = userService.getById(Long.parseLong(subscriptionForm.userId));
		CreditCard creditCard = creditCardService.getById(Long.parseLong(subscriptionForm.cardId));
		Product product = productService.getById(Long.parseLong(subscriptionForm.productId));
		
		SubscriptionStatus status = null;
		if (subscriptionForm.status != null) {
			status = subscriptionForm.status;
		}
		else {
			status = SubscriptionStatus.TRIAL;
		}
		
		Subscription subscription = new Subscription(user, creditCard, product, status, LocalDateTime.now(), LocalDateTime.now());
		
		if (subscriptionForm.id != null){
			subscription.id = Long.parseLong(subscriptionForm.id);
			Subscription subFromDB = findById(subscription.id);
			subscription.setSubscriptionDate(subFromDB.getSubscriptionDate());
			subscription.setLastChargeDate(subFromDB.getLastChargeDate());
		}
		return subscription;
		
	}
	
	public Subscription findById(long id) {
		return subscriptionRepository.findById(id);
	}
	
	public Subscription findByUser(User user) {
		return subscriptionRepository.findByUser(user);
	}
	
	public List<Subscription> findByStatus(SubscriptionStatus status) {
		return subscriptionRepository.findByStatus(status);
	}
	
	public List<Subscription> findExcludingStatus(SubscriptionStatus status) {
		
		return subscriptionRepository.findExcludingStatus(status);
	}
	
	public List<Subscription> findAll() {
		return subscriptionRepository.findAll();
	}
	
}
