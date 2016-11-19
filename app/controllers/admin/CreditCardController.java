package controllers.admin;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.fasterxml.jackson.databind.JsonNode;

import be.objectify.deadbolt.java.actions.Group;
import be.objectify.deadbolt.java.actions.Restrict;
import errors.ValidationError;
import forms.CreditCardForm;
import models.CreditCard;
import models.User;
import models.enums.CardType;
import models.enums.Month;
import models.enums.Year;
import models.json.MessageResponse;
import models.json.ObjectCreatedResponse;
import play.Logger;
import play.data.DynamicForm;
import play.data.FormFactory;
import play.libs.Json;
import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Result;
import services.CreditCardService;
import services.UserService;

@Singleton
@Restrict(@Group("admin"))
public class CreditCardController extends Controller {

	@Inject
    private FormFactory formFactory;
	
	@Inject
	private CreditCardService creditCardService;
	
	@Inject
	private UserService userService;
	
	public Result creditCards() {
		
		List<CreditCard> allCards = creditCardService.getAll();
		List<User> allUsers = userService.getAll();
		CardType[] allTypes = CardType.valuesVisaDiscover();
		Month[] months = Month.values();
    	Year[] years = Year.values();
		return ok(views.html.adminCreditCards.render(allCards, allUsers, allTypes, months, years));
		
	}
	@BodyParser.Of(BodyParser.Json.class)
	public Result addCreditCard() {
		
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
	    	
	    long ownerId = json.findPath("ownerId").asLong();
	    User owner = userService.getById(ownerId);
	    	
	    CreditCard creditCard = creditCardService.createCreditCard(creditCardForm);
	    	
	    creditCard.setUser(owner);
	    creditCardService.save(creditCard);
	    	
	    return ok(Json.toJson(new ObjectCreatedResponse("SUCCESS", "Credit Card was created successfully",
	    			creditCard.getId())));
	    
	}
	
	@BodyParser.Of(BodyParser.Json.class)
	public Result editCreditCard() {
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
	        	
	    return ok(Json.toJson(new MessageResponse("SUCCESS", "Credit Card was edited successfully")));
	}
	
	public Result deleteCreditCard() {
		
		DynamicForm form = formFactory.form().bindFromRequest();
		
		long id = Long.parseLong(form.get("id"));
		CreditCard creditCard = creditCardService.getById(id);
		
		if (creditCard == null) {
	    	return badRequest(Json.toJson(new MessageResponse("ERROR", "Credit Card with id " + 
	    			id + " is not found")));
	    }
		boolean deleted = creditCardService.delete(creditCard);
		
		if (deleted) {
			return ok(Json.toJson(new MessageResponse("SUCCESS", "Credit Card was deleted successfully")));
		}
		else {
			return badRequest(Json.toJson(new MessageResponse("ERROR", "Error occured while deleting the Credit Card")));
		}
		
		
	}
	
	public Result getUserCreditCards() {
		
		DynamicForm form = formFactory.form().bindFromRequest();
		long id = Long.parseLong(form.get("userId"));
		
		User user = userService.getById(id);
		if (user == null) {
	    	return badRequest(Json.toJson(new MessageResponse("ERROR", "User with id " + id + " is not found")));
	    }
		List<CreditCard> userCreditCards = user.getCreditCards();
		
		for (CreditCard creditCard: userCreditCards) {
			Logger.info("Credit Card: " + creditCard);
		}
		
		return ok(Json.toJson(userCreditCards));
	}
	
}
