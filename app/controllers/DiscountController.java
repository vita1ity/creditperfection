package controllers;

import javax.inject.Inject;

import models.Discount;
import models.Subscription;
import models.User;
import models.json.BooleanResponse;
import models.json.ErrorResponse;
import models.json.MessageResponse;
import play.Logger;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import security.Secured;
import services.DiscountService;
import services.MailService;
import services.UserService;

@Security.Authenticated(Secured.class)
public class DiscountController extends Controller {
	
	@Inject
	private UserService userService;
	
	@Inject
	private DiscountService discountService;
	
	@Inject
	private MailService mailService;
	
	public Result checkUserHasDiscount() {

		String email = session().get("email");
		User user = userService.findByEmail(email);
		if (user == null) {
			return badRequest(Json.toJson(new ErrorResponse("ERROR", "501", "User not found")));
		}
		
		Subscription subscription = user.getSubscription();
		if (subscription == null) {
			return badRequest(Json.toJson(new ErrorResponse("ERROR", "601", "You don't have any subscription")));
		}
		
		Discount discount = subscription.getDiscount();
		
		Logger.info("Discount: " + discount);
	
		if (discount == null) {
			return ok(Json.toJson(new BooleanResponse("SUCCESS", false)));
		}
		else {  
			return ok(Json.toJson(new BooleanResponse("SUCCESS", true)));
		}
	}
	
	public Result applyFreeWeekDiscount() {
		
		String email = session().get("email");
		User user = userService.findByEmail(email);
		if (user == null) {
			return badRequest(Json.toJson(new ErrorResponse("ERROR", "501", "User not found")));
		}
		
		Subscription subscription = user.getSubscription();
		if (subscription == null) {
			return badRequest(Json.toJson(new ErrorResponse("ERROR", "601", "You don't have any subscription")));
		}
		
		discountService.applyFreeWeekDiscount(subscription);
		
		mailService.sendFreeWeekTrialAccepted(user, subscription.getDiscount().getEndDate());
		
		return ok(Json.toJson(new MessageResponse("SUCCESS", "You have successfully applied for a FREE week trial!")));
		
	}
	
	public Result applyFreeMonthDiscount() {
		
		String email = session().get("email");
		User user = userService.findByEmail(email);
		if (user == null) {
			return badRequest(Json.toJson(new ErrorResponse("ERROR", "501", "User not found")));
		}
		
		Subscription subscription = user.getSubscription();
		if (subscription == null) {
			return badRequest(Json.toJson(new ErrorResponse("ERROR", "601", "You don't have any subscription")));
		}
		
		discountService.applyFreeMonthDiscount(subscription);
		
		mailService.sendFreeMonthTrialAccepted(user, subscription.getDiscount().getEndDate());
		
		return ok(Json.toJson(new MessageResponse("SUCCESS", "You have successfully applied for a FREE month trial!")));
		
	}
	
	public Result applyYearDiscount() {
		
		String email = session().get("email");
		User user = userService.findByEmail(email);
		if (user == null) {
			return badRequest(Json.toJson(new ErrorResponse("ERROR", "501", "User not found")));
		}
		
		Subscription subscription = user.getSubscription();
		if (subscription == null) {
			return badRequest(Json.toJson(new ErrorResponse("ERROR", "601", "You don't have any subscription")));
		}
		
		discountService.applyYearDiscount(subscription);
		
		mailService.sendYearDiscountAccepted(user, subscription.getDiscount().getDiscountAmount(), subscription.getDiscount().getEndDate());
		
		return ok(Json.toJson(new MessageResponse("SUCCESS", "You have successfully applied for a report discount for the whole year!")));
		
	}
	
}
