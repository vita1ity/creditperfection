package scheduler;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import javax.inject.Inject;

import models.Subscription;
import models.Transaction;
import models.User;
import models.enums.SubscriptionStatus;
import models.enums.TransactionStatus;
import models.json.JSONResponse;
import models.json.MessageResponse;
import net.authorize.api.contract.v1.CreateTransactionResponse;
import play.Logger;
import services.CreditCardService;

public class CreditCardChargeJob implements Runnable {
	
	@Inject
	private CreditCardService creditCardService;
	
    @Override
    public void run() {
    	
        Logger.info("Credit card charging job is running...");
     
        List<Subscription> activeSubscriptions = Subscription.findExcludingStatus(SubscriptionStatus.CANCELLED);
        for (Subscription s: activeSubscriptions) {
        	
        	Logger.info("subscription: " + s);
        	
        	if (checkExpired(s.lastChargeDate)) {
        		//subscription expired. charge credit card
        		CreateTransactionResponse response = (CreateTransactionResponse)creditCardService.charge(s.product.price, s.creditCard);
        		JSONResponse transactionResponse = creditCardService.checkTransaction(response);
    	    	if (transactionResponse instanceof MessageResponse) {
        		
	        		s.lastChargeDate = LocalDateTime.now();
	        				
	        		if (s.status.equals(SubscriptionStatus.TRIAL)) {
	        			s.status = SubscriptionStatus.ACTIVE;
	        			
	        		}
	        		s.update();
	        		
	        		//save transaction
	        		String transactionId = creditCardService.getTransactionId(response);
    	    		Transaction transaction = new Transaction(s.user, s.creditCard, s.product, s.product.price, 
    	    				transactionId, TransactionStatus.SUCCESSFUL);
    	    		transaction.save();
	        		
    	    	}
    	    	else {
    	    		
    	    		Logger.error("Payment Transaction was unsuccessful. Subscription cannot be renewed");
    	    		
    	    		//subscription cannot be renewed. make user inactive and cancel the subscription
    	    		User user = s.user; 
    	    		user.active = false;
    	    		user.update();
    	    		
    	    		s.status = SubscriptionStatus.CANCELLED;
    	    		s.update();
    	    		
    	    	}
        	}
        }
        
        
    }
    
    private boolean checkExpired(LocalDateTime date) {
    	
    	LocalDateTime today = LocalDateTime.now();
    	
    	long daysBetween = ChronoUnit.DAYS.between(date, today);
    	
    	//get number of days in this month
    	int daysInMonth = date.getMonth().length(true);
    	
    	Logger.info("daysBetween: " + daysBetween + "days in current month: " + daysInMonth);
    	
    	if (daysBetween >= daysInMonth) {
    		return true;
    	}
    	else {
    		return false;
    	}
    }
    
}
