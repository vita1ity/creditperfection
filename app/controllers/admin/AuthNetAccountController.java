package controllers.admin;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.fasterxml.jackson.databind.JsonNode;

import be.objectify.deadbolt.java.actions.Group;
import be.objectify.deadbolt.java.actions.Restrict;
import errors.ValidationError;
import forms.CreditCardForm;
import models.AuthNetAccount;
import models.json.MessageResponse;
import models.json.ObjectCreatedResponse;
import play.Logger;
import play.data.DynamicForm;
import play.data.FormFactory;
import play.libs.Json;
import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Result;
import services.AuthNetAccountService;

@Singleton
@Restrict(@Group("admin"))
public class AuthNetAccountController extends Controller {

	@Inject
    private FormFactory formFactory;
	
	@Inject
	private AuthNetAccountService authNetAccountService; 
	
	public Result authNetAccounts() {
		
		List<AuthNetAccount> allAccounts = authNetAccountService.getAll();
		
		return ok(views.html.adminAuthNetAccounts.render(allAccounts));
		
	}
	
	@BodyParser.Of(BodyParser.Json.class)
	public Result addAuthNetAccount() {
		
		JsonNode json = request().body().asJson();
		
		AuthNetAccount account = null;
		try {
			account = Json.fromJson(json, AuthNetAccount.class);
		} 
		catch (RuntimeException e) {
			Logger.error("Cannot parse JSON to Merchant Account.");
			return badRequest(Json.toJson(new MessageResponse("ERROR", "Cannot parse JSON to Merchant Account")));
		}
		
		List<ValidationError> errors = authNetAccountService.validate(account);
    	if (errors != null) {
    		
    		return badRequest(Json.toJson(errors));
    	}
		
    	authNetAccountService.save(account);
		
		return ok(Json.toJson(new ObjectCreatedResponse("SUCCESS", "Merchant Account was added successfully", 
				account.getId())));
		
	}
	
	@BodyParser.Of(BodyParser.Json.class)
	public Result editAuthNetAccount() {
		JsonNode json = request().body().asJson();
		
		AuthNetAccount account = null;
		try {
			account = Json.fromJson(json, AuthNetAccount.class);
		} 
		catch (RuntimeException e) {
			Logger.error("Cannot parse JSON to Merchant Account.");
			return badRequest(Json.toJson(new MessageResponse("ERROR", "Cannot parse JSON to Merchant Account")));
		}
		
		Logger.info("account: " + account);
		
		List<ValidationError> errors = authNetAccountService.validate(account);
    	if (errors != null) {
    		
    		return badRequest(Json.toJson(errors));
    	}
		
    	AuthNetAccount accountDB = authNetAccountService.getById(account.getId());
    	if (accountDB == null) {
	    	return badRequest(Json.toJson(new MessageResponse("ERROR", "Merchant Account with id " + 
	    			account.getId() + " is not found")));
	    }
    	
    	authNetAccountService.updateInfo(accountDB, account);
    	authNetAccountService.update(accountDB);
		
		return ok(Json.toJson(new MessageResponse("SUCCESS", "Merchant Account was edited successfully")));
	}
	
	public Result deleteAuthNetAccount() {
		
		DynamicForm form = formFactory.form().bindFromRequest();
		long id = Long.parseLong(form.get("id"));
		
		AuthNetAccount account = authNetAccountService.getById(id);
		if (account == null) {
	    	return badRequest(Json.toJson(new MessageResponse("ERROR", "Merchant Account with id " + 
	    			id + " is not found")));
	    }
		
		boolean deleted = authNetAccountService.delete(account);
		if (deleted) {
			return ok(Json.toJson(new MessageResponse("SUCCESS", "Merchant Account was deleted successfully")));
		} else {
			return badRequest(Json.toJson(new MessageResponse("ERROR", "Error occured while deleting the Merchant Account")));
		}		
	}
	
}
