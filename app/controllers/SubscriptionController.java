package controllers;

import java.util.List;

import javax.inject.Inject;

import models.Product;
import models.Subscription;
import models.User;
import models.json.ErrorResponse;
import models.json.MessageResponse;
import play.data.DynamicForm;
import play.data.FormFactory;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;

@Security.Authenticated(Secured.class)
public class SubscriptionController extends Controller {
	
	@Inject
    private FormFactory formFactory;
	
	public Result upgradeSubscriptionPage() {
		
		String email = session().get("email");
		User user = User.findByEmail(email);
		
		Subscription subscription = user.subscription;
		
		List<Product> productList = Product.find.all();
		
		return ok(views.html.upgradeSubscription.render(subscription, productList));
		
	}
	
	public Result upgradeSubscription() {
		
		String email = session().get("email");
		User user = User.findByEmail(email);
		if (user == null) {
			return ok(Json.toJson(new ErrorResponse("ERROR", "501", "User not found")));
		}
		
		DynamicForm form = formFactory.form().bindFromRequest();
    	String productId = form.get("product");
    	
    	Product product = Product.getById(Long.parseLong(productId));
    	if (product == null) {
    		return ok(Json.toJson(new ErrorResponse("ERROR", "502", "Product not found")));
    	}
    	
    	Subscription subscription = user.subscription;
    	subscription.product = product;
    	subscription.update();
    	
    	return ok(Json.toJson(new MessageResponse("SUCCESS", "Subscription updated successfully")));
		
	}
	
}
