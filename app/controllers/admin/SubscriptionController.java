package controllers.admin;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.fasterxml.jackson.databind.JsonNode;

import be.objectify.deadbolt.java.actions.Group;
import be.objectify.deadbolt.java.actions.Restrict;
import errors.ValidationError;
import forms.SubscriptionForm;
import models.Product;
import models.Subscription;
import models.User;
import models.enums.SubscriptionStatus;
import models.json.MessageResponse;
import models.json.ObjectResponse;
import play.Logger;
import play.data.DynamicForm;
import play.data.FormFactory;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import services.ProductService;
import services.SubscriptionService;
import services.UserService;

@Singleton
@Restrict(@Group("admin"))
public class SubscriptionController extends Controller {

	@Inject
    private FormFactory formFactory;
	
	@Inject
	private UserService userService;
	
	@Inject
	private ProductService productService;
	
	@Inject
	private SubscriptionService subscriptionService;
	
	public Result subscriptions() {
		
		List<User> allUsers = userService.getAll();
		List<Product> allProducts = productService.getAll();
		List<Subscription> allSubscriptions = subscriptionService.findAll();
		SubscriptionStatus[] allStatuses = SubscriptionStatus.values(); 
		
		return ok(views.html.adminSubscriptions.render(allSubscriptions, allUsers, allProducts, allStatuses));
		
	}
	
	public Result addSubscription() {
		
		JsonNode json = request().body().asJson();
		
		SubscriptionForm subscriptionForm = null;
		try {
			subscriptionForm = Json.fromJson(json, SubscriptionForm.class);
		} 
		catch (RuntimeException e) {
			Logger.error("Cannot parse JSON to Subscription.");
			return badRequest(Json.toJson(new MessageResponse("ERROR", "Cannot parse JSON to Subscription")));
		}
				
		List<ValidationError> errors = subscriptionForm.validate();
    	if (errors != null) {
    		Logger.error(errors.toString());
    		return badRequest(Json.toJson(errors));
    	}
		
    	Subscription subscription = subscriptionService.createSubscription(subscriptionForm);
    	
		subscriptionService.save(subscription);
		
		return ok(Json.toJson(new ObjectResponse("SUCCESS", "Subscription was added successfully", subscription)));
			
	}
	public Result editSubscription() {

		JsonNode json = request().body().asJson();
		SubscriptionForm subscriptionForm = null;
		try {
			subscriptionForm = Json.fromJson(json, SubscriptionForm.class);
		} 
		catch (RuntimeException e) {
			Logger.error("Cannot parse JSON to Subscription.");
			return badRequest(Json.toJson(new MessageResponse("ERROR", "Cannot parse JSON to Subscription")));
		}
		
		List<ValidationError> errors = subscriptionForm.validate();
    	if (errors != null) {
    		Logger.error(errors.toString());
    		return badRequest(Json.toJson(errors));
    	}
		Subscription subscription = subscriptionService.createSubscription(subscriptionForm);
		subscriptionService.update(subscription);
		
		return ok(Json.toJson(new ObjectResponse("SUCCESS", "Subscription was edited successfully", subscription)));
		
		
	}
	public Result deleteSubscription() {

		DynamicForm form = formFactory.form().bindFromRequest();
		long id = Long.parseLong(form.get("id"));
		
		Subscription subscription = subscriptionService.findById(id);
		if (subscription == null) {
			Logger.error("Subscription not found");
    		return badRequest(Json.toJson(new MessageResponse("ERROR", "Subscription not found")));
		}
		subscriptionService.delete(subscription);		
		
		return ok(Json.toJson(new MessageResponse("SUCCESS", "Subscription was deleted successfully")));
		
	}

	public Result cancelSubscription() {
		
		DynamicForm form = formFactory.form().bindFromRequest();
		long id = Long.parseLong(form.get("id"));
		
		Subscription subscription = subscriptionService.findById(id);
		if (subscription == null) {
			Logger.error("Subscription not found");
    		return badRequest(Json.toJson(new MessageResponse("ERROR", "Subscription not found")));
		}
		
		subscription.setStatus(SubscriptionStatus.CANCELLED);
		subscriptionService.update(subscription);		
		
		return ok(Json.toJson(new MessageResponse("SUCCESS", "Subscription was canceled successfully")));
		
	}
	
	public Result filterSubscriptions() {
		
		DynamicForm form = formFactory.form().bindFromRequest();
		String status = form.get("status");
				
		Logger.info("Subscription Filter: " + status);
		
		List<Subscription> subscriptions = null;
		if (status.equals("ALL")) {
			subscriptions = subscriptionService.findAll();
		}
		else {
			SubscriptionStatus subscriptionStatus = SubscriptionStatus.valueOf(status);
			subscriptions = subscriptionService.findByStatus(subscriptionStatus);
		}		
		
		return ok(Json.toJson(new ObjectResponse("SUCCESS", "Subscriptions Filtered", subscriptions)));
		
	}
	
}
