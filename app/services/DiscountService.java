package services;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import javax.inject.Inject;
import javax.inject.Singleton;

import models.Discount;
import models.Subscription;
import models.enums.DiscountStatus;
import models.enums.DiscountType;
import play.Configuration;
import play.Logger;

@Singleton
public class DiscountService {

	@Inject
	private SubscriptionService subscriptionService;
	
	@Inject
	private Configuration conf;
	
	public boolean checkDiscountExpired(Discount d) {
		
		LocalDate endDate = d.getEndDate();
		LocalDate today = LocalDate.now();

		long daysBetween = ChronoUnit.DAYS.between(today, endDate);
		
		if (d.getDiscountType().equals(DiscountType.WEEKLY_DISCOUNT)) {
		
			int daysInTrial = conf.getInt("creditperfection.trial.days");
			
			Logger.info("DiscountService: daysBetween: " + daysBetween + "days in trial: " + daysInTrial);
			
			if  (daysBetween <= daysInTrial) {
				return true;
			}
			else {
				return false;
			}
		}
		else {
			//get number of days in this month
	    	int daysInMonth = today.getMonth().length(true);
	    	
	    	Logger.info("DiscountService: daysBetween: " + daysBetween + "days in current month: " + daysInMonth);
	    	
	    	if (daysBetween <= daysInMonth) {
	    		return true;
	    	}
	    	else {
	    		return false;
	    	}
		}
		
	}

	public void applyFreeWeekDiscount(Subscription subscription) {
		
		LocalDate startDate = null;
		
		LocalDateTime lastChargedDate = subscription.getLastChargeDate();
		LocalDateTime subscriptionDate = subscription.getSubscriptionDate();
		
		if (lastChargedDate.equals(subscriptionDate)) {
			startDate = lastChargedDate.plusDays(7).toLocalDate();
		}
		else {
			int daysInMonth = lastChargedDate.getMonth().length(true);
			startDate = lastChargedDate.plusDays(daysInMonth).toLocalDate();
			
		}
		
		Discount discount = new Discount(subscription, DiscountType.WEEKLY_DISCOUNT, 0.01, startDate, startDate.plusDays(7), DiscountStatus.ACTIVE);
		
		subscription.setDiscount(discount);
		
		subscriptionService.update(subscription);
		
	}

	public void applyFreeMonthDiscount(Subscription subscription) {

		LocalDate startDate = null;
		
		LocalDateTime lastChargedDate = subscription.getLastChargeDate();
		LocalDateTime subscriptionDate = subscription.getSubscriptionDate();
		
		int daysInMonth = lastChargedDate.getMonth().length(true);
		
		if (lastChargedDate.equals(subscriptionDate)) {
			startDate = lastChargedDate.plusDays(7).toLocalDate();
		}
		else {
			
			startDate = lastChargedDate.plusDays(daysInMonth).toLocalDate();
			
		}
		
		Discount discount = new Discount(subscription, DiscountType.MONTHLY_DISCOUNT, 0.01, startDate, startDate.plusDays(daysInMonth), DiscountStatus.ACTIVE);
		
		subscription.setDiscount(discount);
		
		subscriptionService.update(subscription);
		
	}

	public void applyYearDiscount(Subscription subscription) {

		LocalDate startDate = null;
		
		LocalDateTime lastChargedDate = subscription.getLastChargeDate();
		LocalDateTime subscriptionDate = subscription.getSubscriptionDate();
		
		int daysInMonth = lastChargedDate.getMonth().length(true);
		long daysInYear = ChronoUnit.DAYS.between(lastChargedDate, lastChargedDate.plusYears(1));
		
		if (lastChargedDate.equals(subscriptionDate)) {
			startDate = lastChargedDate.plusDays(7).toLocalDate();
		}
		else {
			
			startDate = lastChargedDate.plusDays(daysInMonth).toLocalDate();
			
		}
		
		Discount discount = new Discount(subscription, DiscountType.YEARLY_DISCOUNT, 10.00, startDate, startDate.plusDays(daysInYear), DiscountStatus.ACTIVE);
		
		subscription.setDiscount(discount);
		
		subscriptionService.update(subscription);
		
	}

	
	
}
