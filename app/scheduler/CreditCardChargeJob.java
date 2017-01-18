package scheduler;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import javax.inject.Inject;

import models.AuthNetAccount;
import models.Discount;
import models.Subscription;
import models.Transaction;
import models.User;
import models.enums.DiscountStatus;
import models.enums.SubscriptionStatus;
import models.enums.TransactionStatus;
import models.json.JSONResponse;
import models.json.MessageResponse;
import net.authorize.api.contract.v1.CreateTransactionResponse;
import play.Logger;
import play.libs.Json;
import services.AuthNetAccountService;
import services.CreditCardService;
import services.DiscountService;
import services.MailService;
import services.SubscriptionService;
import services.TransactionService;
import services.UserService;

public class CreditCardChargeJob implements Runnable {
	
	@Inject
	private CreditCardService creditCardService;
	
	@Inject
	private SubscriptionService subscriptionService;
	
	@Inject
	private DiscountService discountService;
	
	@Inject
	private MailService mailService;
	
	@Inject
	private UserService userService;
	
	@Inject
	private TransactionService transactionService;
	
	@Inject
	private AuthNetAccountService authNetAccountService;
	
    @Override
    public void run() {
    	
        Logger.info("Credit card charging job is running...");
     
        //subscriptions with failed transaction
        processFailed();
        
        List<Subscription> activeSubscriptions = subscriptionService.findExcludingStatus(SubscriptionStatus.CANCELLED);
        if (activeSubscriptions != null) {
        	
		    for (Subscription s: activeSubscriptions) {
		    	
		    	Logger.info("subscription: " + s);
		    	
		        if (subscriptionService.checkExpired(s)) {
		        	
		        	double chargeAmount = 0.00;
		        	Discount discount = s.getDiscount();
		        	
		    		if (discount != null && discount.getDiscountStatus().equals(DiscountStatus.ACTIVE)) {
		
		    			
		    			//check if still active after usage
		    			if (discountService.checkDiscountExpired(discount)) {
		    				discount.setDiscountStatus(DiscountStatus.USED);
		    			}
		    			
		    			//do not charge credit card
		    			if (discount.getDiscountAmount() == 0.00) {
		    				
		    				s.setLastChargeDate(LocalDateTime.now());
		    				
		    				
		    				if (subscriptionService.checkTrialEnded(s)) {
		    					
		    					Logger.info("Setting Active status to the subscription...");
		    					
		    					s.setStatus(SubscriptionStatus.ACTIVE);	 
		    				}
		    				
		    				subscriptionService.update(s);
		    				
		    				break;
		    				
		    			}
		    			//charge credit card
		    			else {
		    				chargeAmount = discount.getDiscountAmount();
		    			}
		        			
		        	}
		    		
		        	if (chargeAmount == 0.00) {
		        		
		        		if (subscriptionService.checkTrialEnded(s)) {
		        			chargeAmount = s.getProduct().getPrice();
		        			
		        		}
		        		else {
		        			
		        			chargeAmount = s.getProduct().getSalePrice();
		        			
		        		}
		        		
		        	}
		        	
		        	Logger.info("Charge amount: " + chargeAmount);
		        	
		        	
		        	processPayment(s, chargeAmount);
		        	
		    	}
		    }
        
        }
    }
     
    //subscription expired. charge credit card
    public boolean processPayment(Subscription s, double chargeAmount) {
    	
    	//try all merchant account until successful transaction
    	List<AuthNetAccount> authNetAccounts = authNetAccountService.getEnabled();
    	
    	AuthNetAccount account = null;
    	if (authNetAccounts == null || authNetAccounts.size() == 0) {
    		
    		account = authNetAccountService.getDefaultAccount();
    		
    		if (chargeAndProcessTransaction(s, chargeAmount, account)) {
   			 	return true;
    		}
    	}
    	
    	else {
    	
    		int nextPriority = 1;
	    	for (int i = 1; i <= authNetAccounts.size(); i++) {
	    		
	    		account = authNetAccountService.getAccountByPriority(nextPriority);
	    		
        		if (account == null) {
        			
        			Logger.error("Can't find Auth Net Account with priority: " + i + ". Default account is used");
        			
        			account = authNetAccountService.getDefaultAccount();
        			nextPriority++;
        		}
        		else {
        			nextPriority = account.getPriority();
        			nextPriority++;
        		}
	    		if (chargeAndProcessTransaction(s, chargeAmount, account)) {
	   			 	return true;
	    		}
	    		
	    	}
    	
    	}
    	
		Logger.error("Payment Transaction was unsuccessful. Subscription cannot be renewed");
		
		
		//transaction failed for the second time. subscription cannot be renewed. make user inactive and cancel the subscription
		if (s.getStatus().equals(SubscriptionStatus.RENEW_FAILED)) {
			User user = s.getUser(); 
    		mailService.sendTransactionFailedNotification(user);
    		user.setActive(false);
    		
    		userService.update(user);
    		
    		s.setStatus(SubscriptionStatus.CANCELLED);
    		s.setRenewFailedDate(LocalDate.now());
    		
    		
		}
		//transaction failed for the first time. keep subscription active but change status to indicate failure
		else {
			s.setStatus(SubscriptionStatus.RENEW_FAILED);
		}
		
		
		subscriptionService.update(s);
		
		return false;
		
	}

    
    private boolean chargeAndProcessTransaction(Subscription s, double chargeAmount, AuthNetAccount account) {
    	
    	CreateTransactionResponse response = (CreateTransactionResponse)creditCardService.charge(chargeAmount, 
				s.getCreditCard(), account);
		JSONResponse transactionResponse = creditCardService.checkTransaction(response);
    	if (transactionResponse instanceof MessageResponse) {
		
    		s.setLastChargeDate(LocalDateTime.now());
    		
    		if (s.getStatus().equals(SubscriptionStatus.TRIAL) && subscriptionService.checkTrialEnded(s)) {
    			
    			Logger.info("Setting Active status to the subscription...");
    			s.setStatus(SubscriptionStatus.ACTIVE);	   			
    		}
    		
    		subscriptionService.update(s);
    		
    		//save transaction
    		String transactionId = creditCardService.getTransactionId(response);
    		Transaction transaction = new Transaction(s.getUser(), s.getCreditCard(), s.getProduct(), chargeAmount, 
    				transactionId, TransactionStatus.SUCCESSFUL);
    		
    		Logger.info("Successful transaction: " + transaction);
    		
    		transactionService.save(transaction);
    		
    		return true;
    		
    	}
    	else return false;
    }
    
    private void processFailed() {
    	
    	LocalDate today = LocalDate.now();
        LocalDate yesterday = today.minusDays(1);
        LocalDate startDate = today.minusDays(3);
    	
    	List<Subscription> failedToRenewList = subscriptionService.findFailedToRenew(startDate, yesterday);
       
    	for (Subscription s: failedToRenewList) {
    		Discount discount = s.getDiscount();
    		
    		//if discount haven't been used yet
    		if (discount == null) {
    			User user = s.getUser();
	    		LocalDate renewFailedDate = s.getRenewFailedDate();
	    		int daysBetween = (int)ChronoUnit.DAYS.between(renewFailedDate, today);
	    		switch (daysBetween) {
	    			//2 day of failure
	    			case 1:
	    				mailService.sendFreeWeekOffer(user, renewFailedDate);
	    				break;
	    			//3 day of failure
	    			case 2:
	    				mailService.sendFreeMonthOffer(user, renewFailedDate);
	    				break;
	    			//4 day of failure
	    			case 3:
	    				mailService.sendYearDiscountOffer(user, renewFailedDate);
	    				break;
	    			default:
	    				Logger.error("Wrong subscription renew failed date: " + renewFailedDate);
	    				break;
	    			
	    		}
    		}
    		
    	}
    }
    
    
}
