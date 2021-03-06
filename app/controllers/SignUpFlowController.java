package controllers;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.avaje.ebean.Ebean;
import com.fasterxml.jackson.databind.JsonNode;

import errors.ValidationError;
import forms.CreditCardForm;
import forms.RegisterForm;
import models.AuthNetAccount;
import models.CreditCard;
import models.KBAQuestions;
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
import models.json.CreditReportSuccessResponse;
import models.json.ErrorResponse;
import models.json.JSONResponse;
import models.json.MessageResponse;
import net.authorize.api.contract.v1.CreateTransactionResponse;
import play.Configuration;
import play.Logger;
import play.data.DynamicForm;
import play.data.Form;
import play.data.FormFactory;
import play.libs.Json;
import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Result;
import services.AuthNetAccountService;
import services.CreditCardService;
import services.CreditReportService;
import services.ProductService;
import services.RoleService;
import services.SubscriptionService;
import services.TransactionService;
import services.UserService;
import utils.Tokener;
import views.html.index;

@Singleton
public class SignUpFlowController extends Controller {
	
    @Inject
    private FormFactory formFactory;

    @Inject
    private CreditCardService creditCardService;
    
    @Inject
    private CreditReportService creditReportService;
    
    @Inject
	private Configuration conf;
    
    @Inject
    private UserService userService;

    @Inject
    private ProductService productService;

    @Inject
    private RoleService roleService;
    
    @Inject
    private TransactionService transactionService;
    
    @Inject
    private SubscriptionService subscriptionService;
    
    @Inject
    private AuthNetAccountService authNetAccountService;
    
    public Result index(Boolean login){
    	
    	//session().clear();
     	
    	CardType[] allTypes = CardType.valuesVisaDiscover();
    	State[] states = State.values();
    	Month[] months = Month.values();
    	Year[] years = Year.values();
    	if (login == null) {
    		login = false;
    	}
    	boolean prePopulateOnly = false;
    	User user = new User();
    	String userEmail = session().get("userEmail");
    	if (userEmail != null) {
    		user = userService.findByEmail(userEmail);
    	}
        
    	return ok(index.render(user, allTypes, states, months, years, login, prePopulateOnly));
    }
    
    
    public Result prePopulateRegisterForm(){
    	
    	session().clear();
    	
    	CardType[] allTypes = CardType.valuesVisaDiscover();
    	State[] states = State.values();
    	Month[] months = Month.values();
    	Year[] years = Year.values();
    	
    	boolean login = false;
    	boolean prePopulateOnly = true;
    	
    	Form<User> userForm = formFactory.form(User.class);
    	User user = userForm.bindFromRequest().get();
    	
    	Logger.info("User: " + user);
    	    	
        return ok(index.render(user, allTypes, states, months, years, login, prePopulateOnly));
    }	
    
    @BodyParser.Of(BodyParser.Json.class)
    public Result register() throws Exception {
    	
    	JsonNode json = request().body().asJson();
    	 
    	RegisterForm registerForm = null;
    	try {
    		registerForm = Json.fromJson(json, RegisterForm.class);
		} 
		catch (RuntimeException e) {
			Logger.error("Cannot parse JSON to register form.");
	        return badRequest(Json.toJson(new MessageResponse("ERROR", "Cannot parse JSON to register form")));
		}
    	    	
	    Logger.info(registerForm.toString());
	    
	    List<ValidationError> errors = registerForm.validate();
	    
	    if (errors != null) {
	    	return badRequest(Json.toJson(errors));
	    }
	    
	    User user = new User(registerForm);
	    
	    User userByEmail = userService.findByEmail(user.getEmail());
		if (userByEmail != null) {
			//user is already registered
			if (userByEmail.getKbaQuestions() == null) {
				//registration process is not completed
				session("userEmail", user.getEmail());
				userByEmail.updateUserInfo(user);
				userService.update(userByEmail);
				return ok(Json.toJson(new MessageResponse("SUCCESS", "You have unfinished registration process")));
			}
			else {
				errors = new ArrayList<ValidationError>();
				errors.add(new ValidationError("email", "User with such email is already registered"));
				return badRequest(Json.toJson(errors));
			}
		}

	    
	    user.setToken(Tokener.randomString(48));
	    List<SecurityRole> roles = new ArrayList<SecurityRole>();
	    SecurityRole userRole = roleService.findByName("user");
	    roles.add(userRole);
	    user.setRoles(roles);
	    user.setActive(true);
        userService.save(user);
        
        session("userEmail", user.getEmail());
	    return ok(Json.toJson(new MessageResponse("SUCCESS", "User was successfully registered")));    
    	
    }
    
    public Result registerToken(String token) {
        User user = Ebean.find(User.class).where().eq("token", token).findUnique();
        if (user != null) {
            user.setActive(true);
            userService.update(user);
            flash("message", "Account verified, please login");
        } 
        else {
            flash("error", "Account not verified, please try again");
        }
       
        return redirect(routes.SignUpFlowController.index(false));
    }

    public Result chooseProduct() {
    	DynamicForm form = formFactory.form().bindFromRequest();
    	String product = form.get("product");
    	
    	session("productId", product);
    	
    	Logger.info("Product added to session: " + product);
    	
    	return ok("success");
        
    }
    
    public Result processPaymentAndGetReport() {
    	
    	JsonNode json = request().body().asJson();
   	 
    	CreditCardForm creditCardForm = null;
    	try {
    		creditCardForm = Json.fromJson(json, CreditCardForm.class);
    	}
    	catch (RuntimeException e) {
    		String message = "Cannot parse JSON to CreditCardForm";
    		Logger.error(message);
    		return badRequest(Json.toJson(new MessageResponse("ERROR", message)));
    	}
    		    	
	    List<ValidationError> errors = creditCardForm.validate();
	    if (errors != null) {
	    	
	    	return badRequest(Json.toJson(errors));
	    }
	    String userEmail = session().get("userEmail");
	    User user = userService.findByEmail(userEmail);
	    
	    //get kba questions
	    JSONResponse kbaUrlResponse = creditReportService.getKBAQuestionsUrl(user);
    	if (kbaUrlResponse instanceof ErrorResponse) {
    		Logger.error(((ErrorResponse) kbaUrlResponse).getErrorMessage());
    		return badRequest(Json.toJson(kbaUrlResponse));
    	}
    	else {
    		
    		CreditCard creditCard = creditCardService.createCreditCard(creditCardForm);
    		
    	   	//charge amount for the product from credit card
    		long productId = conf.getLong("creditperfection.default.productId");
    	   	Product product = productService.getById(productId);
    	   	if (product == null) {
    	   		String errorMessage = "Prodcut with id = " 	+ productId + " is missing";
    	   		Logger.error(errorMessage);
    	   		return badRequest(Json.toJson(new MessageResponse("ERROR", errorMessage)));
    	   	}
    	   	
    	   	Logger.info("Product to be purchased: " + product);
    	   	
    	    //try all merchant account until successful transaction
        	List<AuthNetAccount> authNetAccounts = authNetAccountService.getEnabled();
        	AuthNetAccount account = null;
        	if (authNetAccounts == null || authNetAccounts.size() == 0) {
        		
        		account = authNetAccountService.getDefaultAccount();
        		
        		if (chargeAndProcessTransaction(product, creditCard, user, kbaUrlResponse, account)) {
       			 	return ok(Json.toJson(kbaUrlResponse));
        		}
        	}
        	
        	else {
        		int nextPriority = 1;
        		for (int i = 1; i <= authNetAccounts.size(); i++) {
        			
	        		account = authNetAccountService.getAccountByPriority(nextPriority);
	        		if (account == null) {
	        			
	        			Logger.error("Can't find Auth Net Account with priority: " + i + ". Default account is used");
	        			
	        			account = authNetAccountService.getDefaultAccount();
	        			nextPriority++;
	        		}
	        		else {
	        			nextPriority = account.getPriority();
	        			nextPriority++;
	        		}
	        		if (chargeAndProcessTransaction(product, creditCard, user, kbaUrlResponse, account)) {
	        			 return ok(Json.toJson(kbaUrlResponse));
	        		}
	    	   	
        	
        		}
        	}
        	JSONResponse transactionResponse = new ErrorResponse("ERROR", "201", "Transaction Failed");
        	
        	return badRequest(Json.toJson(transactionResponse));
    	   	    			
    	}	    	    
    }
    
    private boolean chargeAndProcessTransaction(Product product, CreditCard creditCard, User user, JSONResponse kbaUrlResponse, AuthNetAccount account) {
    	CreateTransactionResponse response = (CreateTransactionResponse)creditCardService.charge(product.getSalePrice(), creditCard, account);
	   	JSONResponse transactionResponse = creditCardService.checkTransaction(response);
	   	if (transactionResponse instanceof MessageResponse) {
	   		
	   		//save credit card 
        	creditCard.setUser(user);
        	creditCardService.save(creditCard);
        	
        	String transactionId = creditCardService.getTransactionId(response);
	   		//save transaction
	   		Transaction transaction = new Transaction(user, creditCard, product, product.getSalePrice(), 
	   				transactionId, TransactionStatus.SUCCESSFUL);
	   		transactionService.save(transaction);
	   		
	   		//save kba questions url for user
	   		CreditReportSuccessResponse reportResponse = (CreditReportSuccessResponse)kbaUrlResponse;
    		KBAQuestions kbaQuestions = new KBAQuestions(reportResponse.getReportUrl(), user);
    		user.setKbaQuestions(kbaQuestions);
    		userService.update(user);
    		
    		//assign default product to subscription        	    	
        	Logger.info("Product to be purchased after end of the Trial period: " + product);
    		
    		//subscribe user
    		Subscription subscription = new Subscription(user, creditCard, product, 
    				SubscriptionStatus.TRIAL, LocalDateTime.now(), LocalDateTime.now());
    		subscriptionService.save(subscription);
    		        			
    		//TODO subscribe user 
    		/*Akka.system().scheduler().schedule(
                    Duration.create(5, TimeUnit.SECONDS), //Initial delay 0 milliseconds
                    Duration.create(10, TimeUnit.SECONDS),     //Frequency 30 minutes
                    new CreditCardChargeJob(),
                    Akka.system().dispatcher()
            );*/
	   		
    		//clear session
    		session().remove("userEmail");
    		session().remove("productId");
    		 
	   		return true;
	   		
	   	}
	   	else return false;
    }
    
}
