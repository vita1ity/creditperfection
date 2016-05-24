package controllers;

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
import models.Product;
import models.SecurityRole;
import models.Transaction;
import models.User;
import models.enums.CardType;
import models.enums.Month;
import models.enums.State;
import models.enums.Year;
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
import utils.Tokener;
import views.html.index;

@Singleton
public class SignUpFlowController extends Controller {

    @Inject
    private FormFactory formFactory;
    
    /*@Inject
    private WSClient ws;*/
    
    @Inject
    private MailService mailService;
    
    @Inject
    private CreditCardService creditCardService;
    
    @Inject
    private CreditReportService creditReportService;
    
    @Inject
	private Configuration conf;

    public Result index(Boolean login){
    	
    	Form<User> userForm = formFactory.form(User.class);
    	List<Product> productList = Product.getAllProducts();
    	CardType[] allTypes = CardType.values();
    	State[] states = State.values();
    	Month[] months = Month.values();
    	Year[] years = Year.values();
    	if (login == null) {
    		login = false;
    	}
        return ok(index.render(userForm, productList, allTypes, states, months, years, login));
    }
    
    
    @BodyParser.Of(BodyParser.Json.class)
    public Result register() throws Exception {
    	
    	JsonNode json = request().body().asJson();
    	 
    	RegisterForm registerForm = Json.fromJson(json, RegisterForm.class);
    	//User user = Json.fromJson(json, User.class);
	    if(registerForm == null) {
	        return badRequest(Json.toJson(new MessageResponse("ERROR", "Cannot parse JSON to user")));
	    } else {
	    	
	    	List<ValidationError> errors = registerForm.validate(false);
	    	if (errors != null) {
	    		
	    		return badRequest(Json.toJson(errors));
	    	}
	    	User user = new User(registerForm);
	    	
	    	user.token = Tokener.randomString(48);
	    	List<SecurityRole> roles = new ArrayList<SecurityRole>();
	    	SecurityRole userRole = SecurityRole.findByName("user");
	    	roles.add(userRole);
	    	user.roles = roles;
        	user.save();
        	
        	Logger.info("Sending email to : " + user.email);
        	mailService.sendEmailToken(user.email, user.token);
        	
        	session("userEmail", user.email);
	        return ok(Json.toJson(new MessageResponse("SUCCESS", "Email verification sent")));
	    }
    	
    }
    
    public Result registerToken(String token) {
        User user = Ebean.find(User.class).where().eq("token", token).findUnique();
        if (user != null) {
            user.active = true;
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
	    	User user = User.findByEmail(userEmail);
	    	
	    	CreditCard creditCard = CreditCard.createCreditCard(creditCardForm); 
	    	
	    	creditCard.user = user;
	    	creditCard.save();
	    	
	    	//charge amount for the product from credit card
	    	long productId = Long.parseLong(session().get("productId"));
	    	Product product = Product.getById(productId);
	    	
	    	//final String loginId = conf.getString("authorise.net.sandbox.login.id");
	    	//final String transactionKey = conf.getString("authorise.net.sandbox.transaction.key");
	    	final String loginId = conf.getString("authorise.net.login.id");
	    	final String transactionKey = conf.getString("authorise.net.transaction.key");
	    	
	    	CreateTransactionResponse response = (CreateTransactionResponse)creditCardService.charge(loginId, 
	    			transactionKey, product.price, creditCard);
	    	JSONResponse transactionResponse = creditCardService.checkTransaction(response);
	    	if (transactionResponse instanceof MessageResponse) {
	    		
	    		//save transaction in the db
	    		Transaction transaction = new Transaction(user, creditCard, product);
	    		transaction.save();
	    		
	    		JSONResponse reportResponse = creditReportService.getCreditReport(user);
	    		if (reportResponse instanceof ErrorResponse) {
	    			return badRequest(Json.toJson(reportResponse));
	    		}
	    		else {
	    			return ok(Json.toJson(reportResponse));
	    		}
	    		
	    	}
	    	else {
	    		
	    		return badRequest(Json.toJson(transactionResponse));
	    	}
	    	
	    }
	    
	    
    }
    
    


}
