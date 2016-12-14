package scheduler;

import java.time.LocalDateTime;
import java.util.List;

import javax.inject.Inject;

import models.Discount;
import models.Subscription;
import models.Transaction;
import models.User;
import models.enums.DiscountStatus;
import models.enums.DiscountType;
import models.enums.SubscriptionStatus;
import models.enums.TransactionStatus;
import models.json.JSONResponse;
import models.json.MessageResponse;
import net.authorize.api.contract.v1.CreateTransactionResponse;
import play.Logger;
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
	
    @Override
    public void run() {
    	
        Logger.info("Credit card charging job is running...");
     
        List<Subscription> activeSubscriptions = subscriptionService.findExcludingStatus(SubscriptionStatus.CANCELLED);
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
        				
        				if (!discount.getDiscountType().equals(DiscountType.WEEKLY_DISCOUNT)) {
        					
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
            		chargeAmount = s.getProduct().getPrice();
            		
            	}
        		
            	processPayment(s, chargeAmount, discount);
            	
        	}
        }
        
        
    }
    
     
    public boolean processPayment(Subscription s, double chargeAmount, Discount discount) {
    	
    	//subscription expired. charge credit card
		CreateTransactionResponse response = (CreateTransactionResponse)creditCardService.charge(chargeAmount, 
				s.getCreditCard());
		JSONResponse transactionResponse = creditCardService.checkTransaction(response);
    	if (transactionResponse instanceof MessageResponse) {
		
    		s.setLastChargeDate(LocalDateTime.now());
    		
    		
    		if (s.getStatus().equals(SubscriptionStatus.TRIAL) && (discount == null || !discount.getDiscountType().equals(DiscountType.WEEKLY_DISCOUNT))) {
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
    	else {
    		
    		Logger.error("Payment Transaction was unsuccessful. Subscription cannot be renewed");
    		
    		
    		//TODO only cancel after second try
    		//subscription cannot be renewed. make user inactive and cancel the subscription
    		User user = s.getUser(); 
    		mailService.sendTransactionFailedNotification(user);
    		user.setActive(false);
    		
    		userService.update(user);
    		
    		s.setStatus(SubscriptionStatus.CANCELLED);
    		
    		subscriptionService.update(s);
    		
    		return false;
    		
    	}
    }
    
    
}
