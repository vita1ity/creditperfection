package controllers;

import javax.inject.Inject;

import models.Discount;
import models.Subscription;
import models.User;
import models.enums.DiscountStatus;
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


public class DiscountController extends Controller {
	
	@Inject
	private UserService userService;
	
	@Inject
	private DiscountService discountService;
	
	@Inject
	private MailService mailService;
	
	@Security.Authenticated(Secured.class)
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
	
	@Security.Authenticated(Secured.class)
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
	
	@Security.Authenticated(Secured.class)
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
	@Security.Authenticated(Secured.class)
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
	
	
	public Result applyFreeWeekDiscountAndUpdateSubscription() {
		
		String email = session().get("email");
		User user = userService.findByEmail(email);
		if (user == null) {
			return badRequest(Json.toJson(new ErrorResponse("ERROR", "501", "User not found")));
		}
		
		Subscription subscription = user.getSubscription();
		if (subscription == null) {
			return badRequest(Json.toJson(new ErrorResponse("ERROR", "601", "You don't have any subscription")));
		}
		
		Discount d = subscription.getDiscount();
		if (d != null) {
			if (d.getDiscountStatus().equals(DiscountStatus.ACTIVE)) {
				session().put("discountMessage", "You have successfully applied for a FREE week trial! In order to use the service please verify credit card information.");
			}
			else {	
				session().put("discountMessage", "You have already used discount at our service so it can't be applied at a time. "
						+ "In order to use the service please verify credit card information.");
			}
		}
		
		else {
			discountService.applyFreeWeekDiscount(subscription);
			
			mailService.sendFreeWeekTrialAccepted(user, subscription.getDiscount().getEndDate());
			
			session().put("discountMessage", "You have successfully applied for a FREE week trial! In order to use the service please verify credit card information.");
		}
		return redirect(routes.PaymentController.paymentPage());
		
	}
	
	public Result applyFreeMonthDiscountAndUpdateSubscription() {
		
		String email = session().get("email");
		User user = userService.findByEmail(email);
		if (user == null) {
			return badRequest(Json.toJson(new ErrorResponse("ERROR", "501", "User not found")));
		}
		
		Subscription subscription = user.getSubscription();
		if (subscription == null) {
			return badRequest(Json.toJson(new ErrorResponse("ERROR", "601", "You don't have any subscription")));
		}
		
		Discount d = subscription.getDiscount();
		if (d != null) {
			if (d.getDiscountStatus().equals(DiscountStatus.ACTIVE)) {
				session().put("discountMessage", "You have successfully applied for a FREE week trial! In order to use the service please verify credit card information.");
			}
			else {	
				session().put("discountMessage", "You have already used discount at our service so it can't be applied at a time. "
						+ "In order to use the service please verify credit card information.");
			}
		}
		
		else {
			discountService.applyFreeMonthDiscount(subscription);
			
			mailService.sendFreeMonthTrialAccepted(user, subscription.getDiscount().getEndDate());
			
			session().put("discountMessage", "You have successfully applied for a FREE month trial! In order to use the service please verify credit card information.");
		}
		return redirect(routes.PaymentController.paymentPage());
	}

	public Result applyYearDiscountAndUpdateSubscription() {
		
		String email = session().get("email");
		User user = userService.findByEmail(email);
		if (user == null) {
			return badRequest(Json.toJson(new ErrorResponse("ERROR", "501", "User not found")));
		}
		
		Subscription subscription = user.getSubscription();
		if (subscription == null) {
			return badRequest(Json.toJson(new ErrorResponse("ERROR", "601", "You don't have any subscription")));
		}
		
		Discount d = subscription.getDiscount();
		if (d != null) {
			if (d.getDiscountStatus().equals(DiscountStatus.ACTIVE)) {
				session().put("discountMessage", "You have successfully applied for a FREE week trial! In order to use the service please verify credit card information.");
			}
			else {	
				session().put("discountMessage", "You have already used discount at our service so it can't be applied at a time. "
						+ "In order to use the service please verify credit card information.");
			}
		}
		
		else {
			discountService.applyYearDiscount(subscription);
			
			mailService.sendYearDiscountAccepted(user, subscription.getDiscount().getDiscountAmount(), subscription.getDiscount().getEndDate());
			
			session().put("discountMessage", "You have successfully applied for a report discount for the whole year! In order to use the service please verify credit card information.");
		}
		return redirect(routes.PaymentController.paymentPage());
	}
	
	
	
}
