package controllers;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.fasterxml.jackson.databind.JsonNode;

import be.objectify.deadbolt.java.actions.Group;
import be.objectify.deadbolt.java.actions.Restrict;
import errors.ValidationError;
import forms.CreditCardForm;
import forms.ProductForm;
import forms.SubscriptionForm;
import forms.TransactionForm;
import models.AuthNetAccount;
import models.CreditCard;
import models.Product;
import models.SecurityRole;
import models.Subscription;
import models.Transaction;
import models.User;
import models.enums.CardType;
import models.enums.Month;
import models.enums.State;
import models.enums.SubscriptionStatus;
import models.enums.TransactionStatus;
import models.enums.Year;
import models.json.JSONResponse;
import models.json.MessageResponse;
import models.json.ObjectCreatedResponse;
import models.json.ObjectResponse;
import net.authorize.api.contract.v1.CreateTransactionResponse;
import play.Configuration;
import play.Logger;
import play.data.DynamicForm;
import play.data.FormFactory;
import play.libs.Json;
import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Result;
import services.AuthNetAccountService;
import services.CreditCardService;
import services.MailService;
import services.ProductService;
import services.RoleService;
import services.SubscriptionService;
import services.TransactionService;
import services.UserService;
import utils.Tokener;

@Singleton
@Restrict(@Group("admin"))
public class AdminController extends Controller {

	@Inject
    private FormFactory formFactory;
	
	@Inject
    private MailService mailService;
	
	@Inject
	private CreditCardService creditCardService;
	
	@Inject
	private Configuration conf;
	
	@Inject
	private UserService userService;
	
	@Inject
	private ProductService productService;
	
	@Inject
	private AuthNetAccountService authNetAccountService; 
	
	@Inject
	private RoleService roleService;
	
	@Inject
	private SubscriptionService subscriptionService;
	
	@Inject
	private TransactionService transactionService;
	
	//users
	public Result users() {
		
		String email = session().get("email");
		User user = userService.findByEmail(email);
		
		List<User> allUsers = userService.getAll();
		State[] states = State.values();
		
		return ok(views.html.adminUsers.render(user, allUsers, states));
	}
	
	@BodyParser.Of(BodyParser.Json.class)
	public Result editUser() {

		JsonNode json = request().body().asJson();
   	 
    	User user = Json.fromJson(json, User.class);
	    if(user == null) {
	        return badRequest(Json.toJson(new MessageResponse("ERROR", "Cannot parse JSON to user")));
	    }
	    else {
	    	
	    	List<ValidationError> errors = userService.validate(user, true);
	    	if (errors != null) {
	    		
	    		return badRequest(Json.toJson(errors));
	    	}
	    	
	    	User userDB = userService.getById(user.getId());
	    	if (userDB == null) {
	    		return badRequest(Json.toJson(new MessageResponse("ERROR", "User with id" + user.getId() + "is not found")));
	    	}
	    	userDB.updateUserInfo(user);
	    	userDB.save();
	    	
	        return ok(Json.toJson(new MessageResponse("SUCCESS", "User was edited successfully")));
	    }
		
	}
	
	public Result deleteUser() {
		
		DynamicForm form = formFactory.form().bindFromRequest();
		
		long id = Long.parseLong(form.get("id"));
		User user = userService.getById(id);
		boolean deleted = user.delete();
		
		if (deleted) {
			return ok(Json.toJson(new MessageResponse("SUCCESS", "User was deleted successfully")));
		}
		else {
			return badRequest(Json.toJson(new MessageResponse("ERROR", "User was not deleted")));
		}
		
	}
	
	@BodyParser.Of(BodyParser.Json.class)
	public Result addUser() {
		
		JsonNode json = request().body().asJson();
	   	 
    	User user = Json.fromJson(json, User.class);
	    if (user == null) {
	        return badRequest(Json.toJson(new MessageResponse("ERROR", "Cannot parse JSON to user")));
	    }
	    else {
	    	
	    	List<ValidationError> errors = userService.validate(user, true);
	    	if (errors != null) {
	    		
	    		return badRequest(Json.toJson(errors));
	    	}
	    	
	    	user.setToken(Tokener.randomString(48));
	    	List<SecurityRole> roles = new ArrayList<SecurityRole>();
	    	SecurityRole userRole = roleService.findByName("user");
	    	roles.add(userRole);
	    	user.setRoles(roles);
        	user.save();
        	
        	mailService.sendEmailToken(user.getEmail(), user.getToken());
        	
	        return ok(Json.toJson(new ObjectCreatedResponse("SUCCESS", "User was created successfully", user.getId())));
	    }
		
	}
	
	//products
	public Result products() {
		
		List<Product> allProducts = productService.getAll();
		return ok(views.html.adminProducts.render(allProducts));
		
	}
	
	@BodyParser.Of(BodyParser.Json.class)
	public Result addProduct() {
				
		JsonNode json = request().body().asJson();
		
    	ProductForm productForm = Json.fromJson(json, ProductForm.class);
    	
	    if(productForm == null) {
	        return badRequest(Json.toJson(new MessageResponse("ERROR", "Cannot parse JSON to Product")));
	    }
	    else {
	    	
	    	List<ValidationError> errors = productForm.validate();
	    	
	    	if (errors != null) {
	    		
	    		return badRequest(Json.toJson(errors));
	    	}
	    	
	    	Product product = productService.createProduct(productForm);
        	product.save();
        	
	        return ok(Json.toJson(new ObjectCreatedResponse("SUCCESS", "Product was created successfully", product.getId())));
	    }
	
	}
	@BodyParser.Of(BodyParser.Json.class)
	public Result editProduct() {
		
		JsonNode json = request().body().asJson();
		
    	ProductForm productForm = Json.fromJson(json, ProductForm.class);
    	
	    if(productForm == null) {
	        return badRequest(Json.toJson(new MessageResponse("ERROR", "Cannot parse JSON to Product")));
	    }
	    else {
	    	
	    	List<ValidationError> errors = productForm.validate();
	    	
	    	if (errors != null) {
	    		
	    		return badRequest(Json.toJson(errors));
	    	}
	    	Product product = productService.createProduct(productForm);
	    	Product productDB = productService.getById(product.getId());
	    	if (productDB == null) {
	    		return badRequest(Json.toJson(new MessageResponse("ERROR", "Product with id" + product.getId() + "is not found")));
	    	}
	    	productDB.updateProductInfo(product);
	    	productDB.save();
        	
	        return ok(Json.toJson(new MessageResponse("SUCCESS", "Product was edited successfully")));
	    }
		
	}
	
	public Result deleteProduct() {
		
		DynamicForm form = formFactory.form().bindFromRequest();
		
		long id = Long.parseLong(form.get("id"));
		Product product = productService.getById(id);
		boolean deleted = product.delete();
		
		if (deleted) {
			return ok(Json.toJson(new MessageResponse("SUCCESS", "Product was deleted successfully")));
		}
		else {
			return badRequest(Json.toJson(new MessageResponse("ERROR", "Error occured while deleting the Product")));
		}
		
	}
	
	//credit cards
	public Result creditCards() {
		
		List<CreditCard> allCards = creditCardService.getAll();
		List<User> allUsers = userService.getAll();
		CardType[] allTypes = CardType.values();
		Month[] months = Month.values();
    	Year[] years = Year.values();
		return ok(views.html.adminCreditCards.render(allCards, allUsers, allTypes, months, years));
		
	}
	@BodyParser.Of(BodyParser.Json.class)
	public Result addCreditCard() {
		
		JsonNode json = request().body().asJson();
	   	 
    	CreditCardForm creditCardForm = Json.fromJson(json, CreditCardForm.class);
    	
	    if(creditCardForm == null) {
	        return badRequest(Json.toJson(new MessageResponse("ERROR", "Cannot parse JSON to CreditCardForm")));
	    } else {
	    	
	    	List<ValidationError> errors = creditCardForm.validate();
	    	if (errors != null) {
	    		
	    		return badRequest(Json.toJson(errors));
	    	}
	    	
	    	long ownerId = json.findPath("ownerId").asLong();
	    	User owner = userService.getById(ownerId);
	    	
	    	CreditCard creditCard = creditCardService.createCreditCard(creditCardForm);
	    	
	    	creditCard.setUser(owner);
	    	creditCard.save();
	    	
	    	return ok(Json.toJson(new ObjectCreatedResponse("SUCCESS", "Credit Card was created successfully",
	    			creditCard.getId())));
	    }
	}
	
	@BodyParser.Of(BodyParser.Json.class)
	public Result editCreditCard() {
		JsonNode json = request().body().asJson();
	   	 
    	CreditCardForm creditCardForm = Json.fromJson(json, CreditCardForm.class);
    	
	    if(creditCardForm == null) {
	        return badRequest(Json.toJson(new MessageResponse("ERROR", "Cannot parse JSON to CreditCardForm")));
	    } else {
	    	
	    	List<ValidationError> errors = creditCardForm.validate();
	    	if (errors != null) {
	    		
	    		return badRequest(Json.toJson(errors));
	    	}
	    	
	    	CreditCard creditCard = creditCardService.createCreditCard(creditCardForm);
	    	
	    	CreditCard creditCardDB = creditCardService.getById(creditCard.getId());
	    	if (creditCardDB == null) {
	    		return badRequest(Json.toJson(new MessageResponse("ERROR", "Credit Card with id" + 
	    				creditCard.getId() + "is not found")));
	    	}
	    	creditCardDB.updateCreditCardInfo(creditCard);
	    	creditCardDB.save();
	    	
	    	
	    	return ok(Json.toJson(new MessageResponse("SUCCESS", "Credit Card was edited successfully")));
	    }
	}
	
	public Result deleteCreditCard() {
		
		DynamicForm form = formFactory.form().bindFromRequest();
		
		long id = Long.parseLong(form.get("id"));
		CreditCard creditCard = creditCardService.getById(id);
		boolean deleted = creditCard.delete();
		
		if (deleted) {
			return ok(Json.toJson(new MessageResponse("SUCCESS", "Credit Card was deleted successfully")));
		}
		else {
			return badRequest(Json.toJson(new MessageResponse("ERROR", "Error occured while deleting the Credit Card")));
		}
		
		
	}
	
	//transactions
	public Result transactions() {
		
		List<User> allUsers = userService.getAll();
		List<Product> allProducts = productService.getAll();
		List<Transaction> allTransactions = transactionService.findAll();
		TransactionStatus[] allStatuses = TransactionStatus.values(); 
		
		return ok(views.html.adminTransactions.render(allTransactions, allUsers, allProducts, allStatuses));
		
	}
	
	public Result getUserCreditCards() {
		
		DynamicForm form = formFactory.form().bindFromRequest();
		long id = Long.parseLong(form.get("userId"));
		
		User user = userService.getById(id);
		List<CreditCard> userCreditCards = user.getCreditCards();
		
		for (CreditCard creditCard: userCreditCards) {
			Logger.info("Credit Card: " + creditCard);
		}
		
		return ok(Json.toJson(userCreditCards));
	}
	
	@BodyParser.Of(BodyParser.Json.class)
	public Result addTransaction() {
		
		JsonNode json = request().body().asJson();
		TransactionForm transactionForm = Json.fromJson(json, TransactionForm.class);
		
		List<ValidationError> errors = transactionForm.validate();
    	if (errors != null) {
    		
    		return badRequest(Json.toJson(errors));
    	}
		
    	Transaction transaction = transactionService.createTransaction(transactionForm);
    	
		transaction.save();
		
		return ok(Json.toJson(new ObjectResponse("SUCCESS", "Transaction was added successfully", transaction)));
		
	}
	
	public Result editTransaction() {
		
		JsonNode json = request().body().asJson();
		TransactionForm transactionForm = Json.fromJson(json, TransactionForm.class);
		
		List<ValidationError> errors = transactionForm.validate();
    	if (errors != null) {
    		
    		return badRequest(Json.toJson(errors));
    	}
		Transaction transaction = transactionService.createTransaction(transactionForm);
		transaction.update();
		
		return ok(Json.toJson(new ObjectResponse("SUCCESS", "Transaction was edited successfully", transaction)));
		
	}
	
	public Result deleteTransaction() {
		
		DynamicForm form = formFactory.form().bindFromRequest();
		long id = Long.parseLong(form.get("id"));
		
		Transaction transaction = transactionService.findById(id);
		transaction.delete();
		
		return ok(Json.toJson(new MessageResponse("SUCCESS", "Transaction was deleted successfully")));
		
	}
	
	public Result refundTransaction() {
		
		DynamicForm form = formFactory.form().bindFromRequest();
		long id = Long.parseLong(form.get("id"));
		
		Transaction transaction = transactionService.findById(id);
		
    	CreateTransactionResponse response = (CreateTransactionResponse)creditCardService.refundTransaction(transaction);
		JSONResponse transactionResponse = creditCardService.checkTransaction(response);
    	if (transactionResponse instanceof MessageResponse) {
    		
    		transaction.setStatus(TransactionStatus.REFUNDED);
    		transaction.update();
    		return ok(Json.toJson(transactionResponse)); 
    		
    	}
    	else {
    		
    		return badRequest(Json.toJson(transactionResponse));
    	}
		
		
	}
	
	//Authorize .NET Accounts
	public Result authNetAccounts() {
		
		List<AuthNetAccount> allAccounts = authNetAccountService.getAll();
		
		return ok(views.html.adminAuthNetAccounts.render(allAccounts));
		
	}
	
	@BodyParser.Of(BodyParser.Json.class)
	public Result addAuthNetAccount() {
		
		JsonNode json = request().body().asJson();
		AuthNetAccount account = Json.fromJson(json, AuthNetAccount.class);
		
		List<ValidationError> errors = account.validate();
    	if (errors != null) {
    		
    		return badRequest(Json.toJson(errors));
    	}
		
		account.save();
		
		return ok(Json.toJson(new ObjectCreatedResponse("SUCCESS", "Merchant Account was added successfully", 
				account.getId())));
		
	}
	
	@BodyParser.Of(BodyParser.Json.class)
	public Result editAuthNetAccount() {
		JsonNode json = request().body().asJson();
		AuthNetAccount account = Json.fromJson(json, AuthNetAccount.class);
		
		List<ValidationError> errors = account.validate();
    	if (errors != null) {
    		
    		return badRequest(Json.toJson(errors));
    	}
		
		account.update();
		
		return ok(Json.toJson(new MessageResponse("SUCCESS", "Merchant Account was edited successfully")));
	}
	public Result deleteAuthNetAccount() {
		
		DynamicForm form = formFactory.form().bindFromRequest();
		long id = Long.parseLong(form.get("id"));
		
		AuthNetAccount account = authNetAccountService.getById(id);
		account.delete();
		
		return ok(Json.toJson(new MessageResponse("SUCCESS", "Merchant Account was deleted successfully")));
		
	}
	
	public Result subscriptions() {
		
		List<User> allUsers = userService.getAll();
		List<Product> allProducts = productService.getAll();
		List<Subscription> allSubscriptions = subscriptionService.findAll();
		SubscriptionStatus[] allStatuses = SubscriptionStatus.values(); 
		
		return ok(views.html.adminSubscriptions.render(allSubscriptions, allUsers, allProducts, allStatuses));
		
	}
	
	public Result addSubscription() {
		
		JsonNode json = request().body().asJson();
		SubscriptionForm subscriptionForm = Json.fromJson(json, SubscriptionForm.class);
		
		List<ValidationError> errors = subscriptionForm.validate();
    	if (errors != null) {
    		
    		return badRequest(Json.toJson(errors));
    	}
		
    	Subscription subscription = subscriptionService.createSubscription(subscriptionForm);
    	
		subscription.save();
		
		return ok(Json.toJson(new ObjectResponse("SUCCESS", "Subscription was added successfully", subscription)));
			
	}
	public Result editSubscription() {

		JsonNode json = request().body().asJson();
		SubscriptionForm subscriptionForm = Json.fromJson(json, SubscriptionForm.class);
		
		List<ValidationError> errors = subscriptionForm.validate();
    	if (errors != null) {
    		
    		return badRequest(Json.toJson(errors));
    	}
		Subscription subscription = subscriptionService.createSubscription(subscriptionForm);
		subscription.update();
		
		return ok(Json.toJson(new ObjectResponse("SUCCESS", "Subscription was edited successfully", subscription)));
		
		
	}
	public Result deleteSubscription() {

		DynamicForm form = formFactory.form().bindFromRequest();
		long id = Long.parseLong(form.get("id"));
		
		Subscription subscription = subscriptionService.findById(id);
		subscription.delete();
		
		
		return ok(Json.toJson(new MessageResponse("SUCCESS", "Subscription was deleted successfully")));
		
	}

	public Result cancelSubscription() {
		
		DynamicForm form = formFactory.form().bindFromRequest();
		long id = Long.parseLong(form.get("id"));
		
		Subscription subscription = subscriptionService.findById(id);
		
		subscription.setStatus(SubscriptionStatus.CANCELLED);
		subscription.update();
		
		
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
