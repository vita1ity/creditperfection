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
import forms.TransactionForm;
import models.CreditCard;
import models.Product;
import models.SecurityRole;
import models.Transaction;
import models.User;
import models.enums.CardType;
import models.enums.Month;
import models.enums.State;
import models.enums.Year;
import models.json.MessageResponse;
import models.json.ObjectCreatedResponse;
import models.json.TransactionResponse;
import play.Logger;
import play.data.DynamicForm;
import play.data.FormFactory;
import play.libs.Json;
import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Result;
import services.MailService;
import utils.Tokener;

@Singleton
@Restrict(@Group("admin"))
public class AdminController extends Controller {

	@Inject
    private FormFactory formFactory;
	
	@Inject
    private MailService mailService;
	
	
	//users
	public Result users() {
		
		String email = session().get("email");
		User user = User.findByEmail(email);
		
		List<User> allUsers = User.find.all();
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
	    	
	    	List<ValidationError> errors = user.validate(true);
	    	if (errors != null) {
	    		
	    		return badRequest(Json.toJson(errors));
	    	}
	    	
	    	User userDB = User.find.byId(user.id);
	    	if (userDB == null) {
	    		return badRequest(Json.toJson(new MessageResponse("ERROR", "User with id" + user.id + "is not found")));
	    	}
	    	userDB.updateUserInfo(user);
	    	userDB.save();
	    	
	        return ok(Json.toJson(new MessageResponse("SUCCESS", "User was edited successfully")));
	    }
		
	}
	
	public Result deleteUser() {
		
		DynamicForm form = formFactory.form().bindFromRequest();
		
		long id = Long.parseLong(form.get("id"));
		User user = User.find.byId(id);
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
	    	
	    	List<ValidationError> errors = user.validate(true);
	    	if (errors != null) {
	    		
	    		return badRequest(Json.toJson(errors));
	    	}
	    	
	    	user.token = Tokener.randomString(48);
	    	List<SecurityRole> roles = new ArrayList<SecurityRole>();
	    	SecurityRole userRole = SecurityRole.findByName("user");
	    	roles.add(userRole);
	    	user.roles = roles;
        	user.save();
        	
        	mailService.sendEmailToken(user.email, user.token);
        	flash("message", "Email verification sent");
	    	
	        return ok(Json.toJson(new ObjectCreatedResponse("SUCCESS", "User was created successfully", user.id)));
	    }
		
	}
	
	//products
	public Result products() {
		
		List<Product> allProducts = Product.find.all();
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
	    	
	    	Product product = Product.createProduct(productForm);
        	product.save();
        	
	        return ok(Json.toJson(new ObjectCreatedResponse("SUCCESS", "Product was created successfully", product.id)));
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
	    	Product product = Product.createProduct(productForm);
	    	Product productDB = Product.find.byId(product.id);
	    	if (productDB == null) {
	    		return badRequest(Json.toJson(new MessageResponse("ERROR", "Product with id" + product.id + "is not found")));
	    	}
	    	productDB.updateProductInfo(product);
	    	productDB.save();
        	
	        return ok(Json.toJson(new MessageResponse("SUCCESS", "Product was edited successfully")));
	    }
		
	}
	
	public Result deleteProduct() {
		
		DynamicForm form = formFactory.form().bindFromRequest();
		
		long id = Long.parseLong(form.get("id"));
		Product product = Product.find.byId(id);
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
		
		List<CreditCard> allCards = CreditCard.find.all();
		List<User> allUsers = User.find.all();
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
	    	User owner = User.find.byId(ownerId);
	    	
	    	CreditCard creditCard = CreditCard.createCreditCard(creditCardForm);
	    	
	    	creditCard.user = owner;
	    	creditCard.save();
	    	
	    	return ok(Json.toJson(new ObjectCreatedResponse("SUCCESS", "Credit Card was created successfully", creditCard.id)));
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
	    	
	    	CreditCard creditCard = CreditCard.createCreditCard(creditCardForm);
	    	
	    	CreditCard creditCardDB = CreditCard.find.byId(creditCard.id);
	    	if (creditCardDB == null) {
	    		return badRequest(Json.toJson(new MessageResponse("ERROR", "Credit Card with id" + creditCard.id + "is not found")));
	    	}
	    	creditCardDB.updateCreditCardInfo(creditCard);
	    	creditCardDB.save();
	    	
	    	
	    	return ok(Json.toJson(new MessageResponse("SUCCESS", "Credit Card was edited successfully")));
	    }
	}
	
	public Result deleteCreditCard() {
		
		DynamicForm form = formFactory.form().bindFromRequest();
		
		long id = Long.parseLong(form.get("id"));
		CreditCard creditCard = CreditCard.find.byId(id);
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
		
		List<User> allUsers = User.find.all();
		List<Product> allProducts = Product.find.all();
		List<Transaction> allTransactions = Transaction.find.all();
		
		return ok(views.html.adminTransactions.render(allTransactions, allUsers, allProducts));
		
	}
	
	public Result getUserCreditCards() {
		
		DynamicForm form = formFactory.form().bindFromRequest();
		long id = Long.parseLong(form.get("userId"));
		
		User user = User.find.byId(id);
		List<CreditCard> userCreditCards = user.creditCards;
		
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
		
    	Transaction transaction = Transaction.createTransaction(transactionForm);
    	
		transaction.save();
		
		return ok(Json.toJson(new TransactionResponse("SUCCESS", "Transaction was added successfully", transaction)));
		
	}
	
	public Result editTransaction() {
		
		JsonNode json = request().body().asJson();
		TransactionForm transactionForm = Json.fromJson(json, TransactionForm.class);
		
		List<ValidationError> errors = transactionForm.validate();
    	if (errors != null) {
    		
    		return badRequest(Json.toJson(errors));
    	}
		Transaction transaction = Transaction.createTransaction(transactionForm);
		transaction.update();
		
		return ok(Json.toJson(new TransactionResponse("SUCCESS", "Transaction was edited successfully", transaction)));
		
	}
	
	public Result deleteTransaction() {
		
		DynamicForm form = formFactory.form().bindFromRequest();
		long id = Long.parseLong(form.get("id"));
		
		Transaction transaction = Transaction.find.byId(id);
		transaction.delete();
		
		return ok(Json.toJson(new MessageResponse("SUCCESS", "Transaction was deleted successfully")));
		
	}
}
