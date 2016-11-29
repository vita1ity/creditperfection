package services;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.avaje.ebean.PagedList;

import forms.SubscriptionForm;
import models.CreditCard;
import models.Product;
import models.Subscription;
import models.User;
import models.enums.SubscriptionStatus;
import play.Configuration;
import play.Logger;
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
	private Configuration conf;
	
	@Inject 
	private ProductService productService;
	
	public Subscription createSubscription(SubscriptionForm subscriptionForm) {

		User user = userService.getById(Long.parseLong(subscriptionForm.getUserId()));
		CreditCard creditCard = creditCardService.getById(Long.parseLong(subscriptionForm.getCardId()));
		Product product = productService.getById(Long.parseLong(subscriptionForm.getProductId()));
		
		SubscriptionStatus status = null;
		if (subscriptionForm.getStatus() != null) {
			status = subscriptionForm.getStatus();
		}
		else {
			status = SubscriptionStatus.TRIAL;
		}
		
		Subscription subscription = new Subscription(user, creditCard, product, status, LocalDateTime.now(), LocalDateTime.now());
		
		if (subscriptionForm.getId() != null){
			subscription.setId(Long.parseLong(subscriptionForm.getId()));
			Subscription subFromDB = findById(subscription.getId());
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
	
	public PagedList<Subscription> findByStatus(SubscriptionStatus status, int page, int pageSize) {
		return subscriptionRepository.findByStatus(status, page, pageSize);
	}
	
	public List<Subscription> findExcludingStatus(SubscriptionStatus status) {
		
		return subscriptionRepository.findExcludingStatus(status);
	}
	
	public List<Subscription> findAll() {
		return subscriptionRepository.findAll();
	}
	
	public void save(Subscription subscription) {
		subscriptionRepository.save(subscription);
	}

	public void update(Subscription subscription) {
		subscriptionRepository.update(subscription);
		
	}

	public void delete(Subscription subscription) {
		subscriptionRepository.delete(subscription);		
	}
	
	public boolean checkExpired(Subscription s) {
    	LocalDateTime lastChargeDate = s.getLastChargeDate();
    	LocalDateTime subscriptionDate = s.getSubscriptionDate();    	
    	LocalDateTime today = LocalDateTime.now();
    	
    	if (s.getStatus().equals(SubscriptionStatus.TRIAL)) {
    		
        	long daysBetween = ChronoUnit.DAYS.between(subscriptionDate, today);
        	
        	Logger.info("daysBetween: " + daysBetween);
        	int daysInTrial = conf.getInt("creditperfection.trial.days");
        	
        	if (daysBetween >= daysInTrial) {
        		return true;
        	}
        	else {
        		return false;
        	}
    	}
    	else {    	
    	
	    	long daysBetween = ChronoUnit.DAYS.between(lastChargeDate, today);
	    	
	    	//get number of days in this month
	    	int daysInMonth = lastChargeDate.getMonth().length(true);
	    	
	    	Logger.info("daysBetween: " + daysBetween + "days in current month: " + daysInMonth);
	    	
	    	if (daysBetween >= daysInMonth) {
	    		return true;
	    	}
	    	else {
	    		return false;
	    	}
    	}
    }

	public PagedList<Subscription> getSubscriptionsPage(int page, int pageSize) {
		return subscriptionRepository.getSubscriptionsPage(page, pageSize);
	}
	
	//FOR TESTTING
	
	public void generateSubscriptions(int numberOfSubscriptions) {
		
		for (int i = 0; i < numberOfSubscriptions; i++) {
			
			CreditCard creditCard = creditCardService.getById(1);
			Product product = productService.getById(1);
			User user = userService.getById(i + 2);
			
			Subscription subscription = new Subscription();
			subscription.setCreditCard(creditCard);
			subscription.setProduct(product);
			subscription.setUser(user);
			subscription.setLastChargeDate(LocalDateTime.now());
			subscription.setStatus(SubscriptionStatus.ACTIVE);
			subscription.setSubscriptionDate(LocalDateTime.now());
			
			subscriptionRepository.save(subscription);
			
			
		}
		
	}
	
	
}
