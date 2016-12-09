package controllers;

import java.util.List;

import javax.inject.Inject;

import models.Product;
import models.Subscription;
import models.User;
import models.enums.SubscriptionStatus;
import models.json.ErrorResponse;
import models.json.MessageResponse;
import play.Logger;
import play.data.DynamicForm;
import play.data.FormFactory;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import security.Secured;
import services.MailService;
import services.ProductService;
import services.SubscriptionService;
import services.UserService;

@Security.Authenticated(Secured.class)
public class SubscriptionController extends Controller {
	
	@Inject
    private FormFactory formFactory;
	
	@Inject
	private MailService mailService; 
	
	@Inject
	private UserService userService;
	
	@Inject
	private ProductService productService;
	
	@Inject
	private SubscriptionService subscriptionService;
	
	public Result upgradeSubscriptionPage() {
		
		String email = session().get("email");
		User user = userService.findByEmail(email);
		
		Subscription subscription = user.getSubscription();
		
		List<Product> productList = productService.getAll();
		
		return ok(views.html.upgradeSubscription.render(subscription, productList));
		
	}
	
	public Result  upgradeSubscription() {
		
		String email = session().get("email");
		User user = userService.findByEmail(email);
		
		if (user == null) {
			return badRequest(Json.toJson(new ErrorResponse("ERROR", "501", "User not found")));
		}
		
		DynamicForm form = formFactory.form().bindFromRequest();
    	String productId = form.get("product");
    	
    	Product product = productService.getById(Long.parseLong(productId));
    	if (product == null) {
    		return badRequest(Json.toJson(new ErrorResponse("ERROR", "502", "Product not found")));
    	}
    	
    	Subscription subscription = user.getSubscription();
    	subscription.setProduct(product);
    	subscriptionService.update(subscription);
    	
    	return ok(Json.toJson(new MessageResponse("SUCCESS", "Subscription updated successfully")));
		
	}
	
	public Result cancelSubscription() {
		
		String email = session().get("email");
		User user = userService.findByEmail(email);
		if (user == null) {
			return badRequest(Json.toJson(new ErrorResponse("ERROR", "501", "User not found")));
		}
		
		Subscription subscription = user.getSubscription();
		if (subscription == null) {
			return badRequest(Json.toJson(new ErrorResponse("ERROR", "601", "You don't have any subscription")));
		}
		else if (subscription.getStatus() == SubscriptionStatus.PENDING) {
			return ok(Json.toJson(new MessageResponse("SUCCESS", "Your subscription is in status Pending. "
					+ "It will be cancelled shortly")));
		}
		else if (subscription.getStatus() == SubscriptionStatus.CANCELLED) {
			return badRequest(Json.toJson(new ErrorResponse("ERROR", "603", "Your subscription is already cancelled")));
		}
		subscription.setStatus(SubscriptionStatus.PENDING);
		subscriptionService.update(subscription);
				
		mailService.sendCancelSubscriptionNotification(user);
		
		return ok(Json.toJson(new MessageResponse("SUCCESS", "Cancellation request has been submitted")));
		
	}
	
	
}
