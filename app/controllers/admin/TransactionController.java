package controllers.admin;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.avaje.ebean.PagedList;
import com.fasterxml.jackson.databind.JsonNode;

import be.objectify.deadbolt.java.actions.Group;
import be.objectify.deadbolt.java.actions.Restrict;
import errors.ValidationError;
import forms.TransactionForm;
import models.Product;
import models.Subscription;
import models.Transaction;
import models.User;
import models.enums.SubscriptionStatus;
import models.enums.TransactionStatus;
import models.json.JSONResponse;
import models.json.MessageResponse;
import models.json.ObjectResponse;
import models.json.PagedObjectResponse;
import net.authorize.api.contract.v1.CreateTransactionResponse;
import play.Configuration;
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
	
	@Inject
	private Configuration conf;
	
	public Result transactions() {
		
		List<User> allUsers = userService.getAll();
		List<Product> allProducts = productService.getAll();
		TransactionStatus[] allStatuses = TransactionStatus.values();
		
		int pageSize = conf.getInt("page.size");
		PagedList<Transaction> transactionsPage = transactionService.getTransactionsPage(0, pageSize);
		List<Transaction> transactions = transactionsPage.getList();
		int numberOfPages = transactionsPage.getTotalPageCount();
		
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
		
		return ok(views.html.adminTransactions.render(transactions, allUsers, allProducts, allStatuses,  numberOfPages, displayedPages, currentPage));
		
	}
	
	public Result getTransactions(int page) {
		
		if (page < 1) {
			return badRequest(Json.toJson(new MessageResponse("ERROR", "Invalid page value")));
		}
		
		int pageSize = conf.getInt("page.size");
		
		PagedList<Transaction> transactionsPage = transactionService.getTransactionsPage(page - 1, pageSize);
		
		List<Transaction> transactions = transactionsPage.getList();
		
		
		return ok(Json.toJson(new PagedObjectResponse("SUCCESS", transactions, page, transactionsPage.getTotalPageCount())));
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
