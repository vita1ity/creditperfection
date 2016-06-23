package scheduler;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import javax.inject.Inject;

import models.Subscription;
import models.enums.SubscriptionStatus;
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
        		creditCardService.charge(s.product.price, s.creditCard);
        		
        		if (s.status.equals(SubscriptionStatus.TRIAL)) {
        			s.status = SubscriptionStatus.ACTIVE;
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
