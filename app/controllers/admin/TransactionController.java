package controllers.admin;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.fasterxml.jackson.databind.JsonNode;

import be.objectify.deadbolt.java.actions.Group;
import be.objectify.deadbolt.java.actions.Restrict;
import errors.ValidationError;
import forms.TransactionForm;
import models.Product;
import models.Transaction;
import models.User;
import models.enums.TransactionStatus;
import models.json.JSONResponse;
import models.json.MessageResponse;
import models.json.ObjectResponse;
import net.authorize.api.contract.v1.CreateTransactionResponse;
import play.Logger;
import play.data.DynamicForm;
import play.data.FormFactory;
import play.libs.Json;
import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Result;
import services.CreditCardService;
import services.ProductService;
import services.TransactionService;
import services.UserService;

@Singleton
@Restrict(@Group("admin"))
public class TransactionController extends Controller {

	@Inject
    private FormFactory formFactory;
	
	@Inject
	private UserService userService;
	
	@Inject
	private ProductService productService;
	
	@Inject
	private TransactionService transactionService;
	
	@Inject
	private CreditCardService creditCardService;
	
	public Result transactions() {
		
		List<User> allUsers = userService.getAll();
		List<Product> allProducts = productService.getAll();
		List<Transaction> allTransactions = transactionService.findAll();
		TransactionStatus[] allStatuses = TransactionStatus.values(); 
		
		return ok(views.html.adminTransactions.render(allTransactions, allUsers, allProducts, allStatuses));
		
	}
	
	
	@BodyParser.Of(BodyParser.Json.class)
	public Result addTransaction() {
		
		JsonNode json = request().body().asJson();
		
		TransactionForm transactionForm = null;
		try {
			transactionForm = Json.fromJson(json, TransactionForm.class);
		} 
		catch (RuntimeException e) {
			Logger.error("Cannot parse JSON to TransactionForm.");
			return badRequest(Json.toJson(new MessageResponse("ERROR", "Cannot parse JSON to TransactionForm")));
		} 
				
		List<ValidationError> errors = transactionForm.validate();
    	if (errors != null) {
    		
    		return badRequest(Json.toJson(errors));
    	}
		
    	Transaction transaction = transactionService.createTransaction(transactionForm);
    	
    	transactionService.save(transaction);
		
		return ok(Json.toJson(new ObjectResponse("SUCCESS", "Transaction was added successfully", transaction)));
		
	}
	
	public Result editTransaction() {
		
		JsonNode json = request().body().asJson();
		TransactionForm transactionForm = null;
		try {
			transactionForm = Json.fromJson(json, TransactionForm.class);
		} 
		catch (RuntimeException e) {
			Logger.error("Cannot parse JSON to TransactionForm.");
			return badRequest(Json.toJson(new MessageResponse("ERROR", "Cannot parse JSON to TransactionForm")));
		} 
		
		List<ValidationError> errors = transactionForm.validate();
    	if (errors != null) {
    		
    		return badRequest(Json.toJson(errors));
    	}
		Transaction transaction = transactionService.createTransaction(transactionForm);
		transactionService.update(transaction);
		
		return ok(Json.toJson(new ObjectResponse("SUCCESS", "Transaction was edited successfully", transaction)));
		
	}
	
	public Result deleteTransaction() {
		
		DynamicForm form = formFactory.form().bindFromRequest();
		long id = Long.parseLong(form.get("id"));
		
		Transaction transaction = transactionService.findById(id);
		if (transaction == null) {
	    	return badRequest(Json.toJson(new MessageResponse("ERROR", "Transaction with id " + id + " is not found")));
	    }
		
		boolean deleted = transactionService.delete(transaction);
		
		if (deleted) {
			return ok(Json.toJson(new MessageResponse("SUCCESS", "Transaction was deleted successfully")));
		}
		else {
			return badRequest(Json.toJson(new MessageResponse("ERROR", "Transaction was not deleted")));
		}		
	}
	
	public Result refundTransaction() {
		
		DynamicForm form = formFactory.form().bindFromRequest();
		long id = Long.parseLong(form.get("id"));
		
		Transaction transaction = transactionService.findById(id);
		if (transaction == null) {
	    	return badRequest(Json.toJson(new MessageResponse("ERROR", "Transaction with id " + id + " is not found")));
	    }
		
    	CreateTransactionResponse response = (CreateTransactionResponse)creditCardService.refundTransaction(transaction);
		JSONResponse transactionResponse = creditCardService.checkTransaction(response);
    	if (transactionResponse instanceof MessageResponse) {
    		
    		transaction.setStatus(TransactionStatus.REFUNDED);
    		transactionService.update(transaction);
    		return ok(Json.toJson(transactionResponse)); 
    		
    	}
    	else {    		
    		return badRequest(Json.toJson(transactionResponse));
    	}
		
		
	}
	
}
