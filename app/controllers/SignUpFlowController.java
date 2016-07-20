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
import services.CreditCardService;
import services.CreditReportService;
import services.MailService;
import services.ProductService;
import services.RoleService;
import services.UserService;
import utils.Tokener;
import views.html.index;

@Singleton
public class SignUpFlowController extends Controller {

    @Inject
    private FormFactory formFactory;
    
    @Inject
    private MailService mailService;
    
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
    
    public Result index(Boolean login){
    	
    	//session().clear();
    	
    	List<Product> productList = productService.getAll();
    	CardType[] allTypes = CardType.values();
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
    	Product product = new Product();
    	if (session().get("productId") != null) {
    		long productId = Long.parseLong(session().get("productId"));
			product = productService.getById(productId);
		}
    	
        return ok(index.render(user, product, productList, allTypes, states, months, years, login, prePopulateOnly));
    }
    
    
    public Result prePopulateRegisterForm(){
    	
    	session().clear();
    	
    	List<Product> productList = productService.getAll();
    	CardType[] allTypes = CardType.values();
    	State[] states = State.values();
    	Month[] months = Month.values();
    	Year[] years = Year.values();
    	
    	boolean login = false;
    	boolean prePopulateOnly = true;
    	
    	Form<User> userForm = formFactory.form(User.class);
    	User user = userForm.bindFromRequest().get();
    	
    	Logger.info("User: " + user);
    	
    	Product product = new Product();
    	
        return ok(index.render(user, product, productList, allTypes, states, months, years, login, prePopulateOnly));
    }	
    
    @BodyParser.Of(BodyParser.Json.class)
    public Result register() throws Exception {
    	
    	JsonNode json = request().body().asJson();
    	 
    	RegisterForm registerForm = Json.fromJson(json, RegisterForm.class);
    	//User user = Json.fromJson(json, User.class);
	    if(registerForm == null) {
	        return badRequest(Json.toJson(new MessageResponse("ERROR", "Cannot parse JSON to user")));
	    } else {
	    	
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
					userByEmail.update();
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
        	user.save();
        	
        	Logger.info("Sending email to : " + user.getEmail());
        	mailService.sendEmailToken(user.getEmail(), user.getToken());
        	
        	session("userEmail", user.getEmail());
	        return ok(Json.toJson(new MessageResponse("SUCCESS", "Email verification sent")));
	    }
    	
    }
    
    public Result registerToken(String token) {
        User user = Ebean.find(User.class).where().eq("token", token).findUnique();
        if (user != null) {
            user.setActive(true);
            user.update();
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
   	 
    	CreditCardForm creditCardForm = Json.fromJson(json, CreditCardForm.class);
    	
	    if(creditCardForm == null) {
	        return badRequest(Json.toJson(new MessageResponse("ERROR", "Cannot parse JSON to CreditCardForm")));
	    } else {
	    	
	    	List<ValidationError> errors = creditCardForm.validate();
	    	if (errors != null) {
	    		
	    		return badRequest(Json.toJson(errors));
	    	}
	    	String userEmail = session().get("userEmail");
	    	User user = userService.findByEmail(userEmail);
	    	
	    	//get kba questions
	    	JSONResponse kbaUrlResponse = creditReportService.getKBAQuestionsUrl(user);
    		if (kbaUrlResponse instanceof ErrorResponse) {
    			return badRequest(Json.toJson(kbaUrlResponse));
    		}
    		else {
    			
    			CreditCard creditCard = creditCardService.createCreditCard(creditCardForm);
    			
    	    	//charge amount for the product from credit card
    	    	long productId = Long.parseLong(session().get("productId"));
    	    	Product product = productService.getById(productId);
    	    	
    	    	Logger.info("Product to be purchased: " + product);
    	    	
    	    	CreateTransactionResponse response = (CreateTransactionResponse)creditCardService.charge(product.getSalePrice(), creditCard);
    	    	JSONResponse transactionResponse = creditCardService.checkTransaction(response);
    	    	if (transactionResponse instanceof MessageResponse) {
    	    		
    	    		//save credit card 
        	    	creditCard.setUser(user);
        	    	creditCard.save();
        	    	
        	    	String transactionId = creditCardService.getTransactionId(response);
    	    		//save transaction
    	    		Transaction transaction = new Transaction(user, creditCard, product, product.getSalePrice(), 
    	    				transactionId, TransactionStatus.SUCCESSFUL);
    	    		transaction.save();
    	    		
    	    		//save kba questions url for user
    	    		CreditReportSuccessResponse reportResponse = (CreditReportSuccessResponse)kbaUrlResponse;
        			KBAQuestions kbaQuestions = new KBAQuestions(reportResponse.getReportUrl(), user);
        			user.setKbaQuestions(kbaQuestions);
        			user.update();
        			
        			//subscribe user
        			Subscription subscription = new Subscription(user, creditCard, product, 
        					SubscriptionStatus.TRIAL, LocalDateTime.now(), LocalDateTime.now());
        			subscription.save();
        			
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
        			 
    	    		return ok(Json.toJson(kbaUrlResponse));
    	    		
    	    	}
    	    	else {
    	    		
    	    		return badRequest(Json.toJson(transactionResponse));
    	    	}
    			
    		}
	    	
	    	
	    }
	    
	    
    }
    


}
