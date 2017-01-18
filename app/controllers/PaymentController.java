package controllers;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.fasterxml.jackson.databind.JsonNode;

import errors.ValidationError;
import forms.CreditCardForm;
import models.CreditCard;
import models.Discount;
import models.Subscription;
import models.User;
import models.enums.CardType;
import models.enums.DiscountStatus;
import models.enums.Month;
import models.enums.SubscriptionStatus;
import models.enums.Year;
import models.json.MessageResponse;
import play.Logger;
import play.libs.Json;
import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Result;
import scheduler.CreditCardChargeJob;
import services.CreditCardService;
import services.SubscriptionService;
import services.UserService;

@Singleton
public class PaymentController extends Controller {

	@Inject
	private UserService userService;
	
	@Inject
	private CreditCardService creditCardService;
	
	@Inject
	private CreditCardChargeJob creditCardChargeJob;
	
	@Inject
	private SubscriptionService subscriptionService;
	
	public Result paymentPage() {
		
		String discountMessage = session().get("discountMessage");
		if (discountMessage != null) {
			
			session().remove("discountMessage");
		}
		
		String email = session().get("email");
		User user = userService.findByEmail(email);
		
		if (user == null || user.getActive()) {
			return redirect(routes.SignUpFlowController.index(false));
		}
		
		CreditCard creditCard = user.getSubscription().getCreditCard();
		
		CardType[] allTypes = CardType.valuesVisaDiscover();
		Month[] months = Month.values();
    	Year[] years = Year.values();
    	
		return ok(views.html.paymentPage.render(creditCard, allTypes, months, years, discountMessage));
		
	}
	
	@BodyParser.Of(BodyParser.Json.class)
	public Result updateAndProcess() {
		
		JsonNode json = request().body().asJson();
	   	 
		CreditCardForm creditCardForm = null;
		try {
			creditCardForm = Json.fromJson(json, CreditCardForm.class);
		} 
		catch (RuntimeException e) {
			Logger.error("Cannot parse JSON to CreditCardForm.");
			return badRequest(Json.toJson(new MessageResponse("ERROR", "Cannot parse JSON to CreditCardForm")));
		}
	    	
	    List<ValidationError> errors = creditCardForm.validate();
	    if (errors != null) {
	    	
	    	return badRequest(Json.toJson(errors));
	    }
	    
	    CreditCard creditCard = creditCardService.createCreditCard(creditCardForm);
	    
	    CreditCard creditCardDB = creditCardService.getById(creditCard.getId());
	    if (creditCardDB == null) {
	    	return badRequest(Json.toJson(new MessageResponse("ERROR", "Credit Card with id " + 
	    			creditCard.getId() + " is not found")));
	    }
	    
	    creditCardService.updateInfo(creditCardDB, creditCard);
	    creditCardService.update(creditCardDB);
	    
	    String email = session().get("email");
		User user = userService.findByEmail(email);
		Subscription subscription = user.getSubscription();
		
		Discount discount = subscription.getDiscount();
    	
		double amount = 0.00;
		
		if (discount != null && discount.getDiscountStatus().equals(DiscountStatus.ACTIVE)) {
			amount = discount.getDiscountAmount();
		}
		else { 
			amount = subscription.getProduct().getPrice();
		}
		
		boolean success = creditCardChargeJob.processPayment(subscription, amount);
		
		if (success) {
			subscription.setStatus(SubscriptionStatus.ACTIVE);
			user.setActive(true);
			subscriptionService.update(subscription);
			userService.update(user);
			
			return ok(Json.toJson(new MessageResponse("SUCCESS", "Transaction passed successfully. Your account is now active and you can use the service. "
					+ "You will be redirected to the report page in 5 seconds")));
		}
		else {
			return badRequest(Json.toJson(new MessageResponse("ERROR", "Unfortunately we were not able to process your credit card. "
					+ "Please try another card to renew your subscription.")));
		}
		
		
	}
	
}
