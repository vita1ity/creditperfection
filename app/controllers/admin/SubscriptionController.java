package controllers.admin;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.avaje.ebean.PagedList;
import com.fasterxml.jackson.databind.JsonNode;

import be.objectify.deadbolt.java.actions.Group;
import be.objectify.deadbolt.java.actions.Restrict;
import errors.ValidationError;
import exceptions.UserAlreadySubscribedException;
import forms.SubscriptionForm;
import models.Product;
import models.Subscription;
import models.User;
import models.enums.SubscriptionStatus;
import models.json.MessageResponse;
import models.json.ObjectResponse;
import models.json.PagedObjectResponse;
import play.Configuration;
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
	
	@Inject
	private Configuration conf;
	
	public Result subscriptions() {
		
		List<User> allUsers = userService.getAll();
		List<Product> allProducts = productService.getAll();
		
		int pageSize = conf.getInt("page.size");
		PagedList<Subscription> subscriptionsPage = subscriptionService.getSubscriptionsPage(0, pageSize);
		List<Subscription> subscriptions = subscriptionsPage.getList();
		int numberOfPages = subscriptionsPage.getTotalPageCount();
		
		Logger.info("Number of pages for subscriptions: " + Integer.toString(numberOfPages));
		
		int[] displayedPages = new int[1];
		if (numberOfPages > 10 ) {
			displayedPages = new int[10];
			for (int i = 0; i < 10; i++) {
				displayedPages[i] = (i + 1);
			}
		}
		else {
			displayedPages = new int[numberOfPages];
			for (int i = 0; i < numberOfPages; i++) {
				displayedPages[i] = (i + 1);
			}
		}
		
		int currentPage = 1;
		
		SubscriptionStatus[] allStatuses = SubscriptionStatus.values(); 
		
		return ok(views.html.adminSubscriptions.render(subscriptions, allUsers, allProducts, allStatuses, numberOfPages, displayedPages, currentPage));
		
	}
	
	public Result getSubscriptions(int page) {
		
		DynamicForm form = formFactory.form().bindFromRequest();
		String status = form.get("status");
		
		if (page < 1) {
			return badRequest(Json.toJson(new MessageResponse("ERROR", "Invalid page value")));
		}
		
		int pageSize = conf.getInt("page.size");
		List<Subscription> subscriptions = null;
		PagedList<Subscription> subscriptionsPage = null;
		
		if (status.equals("ALL")) { 
			subscriptionsPage = subscriptionService.getSubscriptionsPage(page - 1, pageSize);
			subscriptions = subscriptionsPage.getList();
		}
		else {
			SubscriptionStatus subscriptionStatus = SubscriptionStatus.valueOf(status);
			subscriptionsPage = subscriptionService.findByStatus(subscriptionStatus, page - 1, pageSize);
			subscriptions = subscriptionsPage.getList();
		}
		
		return ok(Json.toJson(new PagedObjectResponse("SUCCESS", subscriptions, page, subscriptionsPage.getTotalPageCount())));
	}
	
	public Result addSubscription() {
		
		JsonNode json = request().body().asJson();
				
		SubscriptionForm subscriptionForm = null;
		try {
			subscriptionForm = Json.fromJson(json, SubscriptionForm.class);
		} 
		catch (RuntimeException e) {
			Logger.error("Cannot parse JSON to Subscription form");
			return badRequest(Json.toJson(new MessageResponse("ERROR", "Cannot parse JSON to Subscription form")));
		}
				
		List<ValidationError> errors = subscriptionForm.validate();
    	if (errors != null) {
    		Logger.error(errors.toString());
    		return badRequest(Json.toJson(errors));
    	}
		
    	User user = userService.getById(Long.parseLong(subscriptionForm.getUserId()));
			
		Subscription subFromDb = subscriptionService.findByUser(user);
	    if (subFromDb != null) {
	    	return badRequest(Json.toJson(new MessageResponse("ERROR", "User is already subscribed")));
	    }
	
    	Subscription subscription;
		try {
			subscription = subscriptionService.createSubscription(subscriptionForm);
		} catch (UserAlreadySubscribedException e) {
			return badRequest(Json.toJson(new MessageResponse("ERROR", e.getMessage())));
		}
    	
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
    	
		Subscription subscription;
		try {
			subscription = subscriptionService.createSubscription(subscriptionForm);
		} catch (UserAlreadySubscribedException e) {
			return badRequest(Json.toJson(new MessageResponse("ERROR", e.getMessage())));
		}
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
		
		int pageSize = conf.getInt("page.size");
		
		PagedList<Subscription> subscriptionsPage = null;
		if (status.equals("ALL")) {
			
			subscriptionsPage = subscriptionService.getSubscriptionsPage(0, pageSize);
			
		}
		else {
			SubscriptionStatus subscriptionStatus = SubscriptionStatus.valueOf(status);
			 subscriptionsPage = subscriptionService.findByStatus(subscriptionStatus, 0, pageSize);
			 
		}		
		
		return ok(Json.toJson(new PagedObjectResponse("SUCCESS", subscriptionsPage.getList(), 1, subscriptionsPage.getTotalPageCount())));
		
	}
	
}
