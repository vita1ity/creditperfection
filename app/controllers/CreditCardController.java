package controllers;

import java.util.List;

import javax.inject.Inject;

import com.fasterxml.jackson.databind.JsonNode;

import errors.ValidationError;
import forms.CreditCardForm;
import models.CreditCard;
import models.User;
import models.enums.CardType;
import models.enums.Month;
import models.enums.Year;
import models.json.MessageResponse;
import play.Logger;
import play.libs.Json;
import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import security.Secured;
import services.CreditCardService;
import services.UserService;

@Security.Authenticated(Secured.class)
public class CreditCardController extends Controller {

	@Inject
	private UserService userService;
	
	@Inject
	private CreditCardService creditCardService;
	
	public Result updateCreditCardPage() {

		String email = session().get("email");
		User user = userService.findByEmail(email);
		
		CreditCard creditCard = user.getSubscription().getCreditCard();
		
		CardType[] allTypes = CardType.valuesVisaDiscover();
		Month[] months = Month.values();
    	Year[] years = Year.values();
    	
		return ok(views.html.updateCreditCard.render(creditCard, allTypes, months, years));
		
	}
	
	@BodyParser.Of(BodyParser.Json.class)
	public Result updateCreditCard() {
		
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
	        	
	    return ok(Json.toJson(new MessageResponse("SUCCESS", "Credit Card was updated successfully")));
		
	}
	
}
