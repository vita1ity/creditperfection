package controllers;

import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.avaje.ebean.Ebean;
import com.fasterxml.jackson.databind.JsonNode;

import errors.ValidationError;
import forms.RegisterForm;
import models.CardType;
import models.CreditCard;
import models.Product;
import models.SecurityRole;
import models.Transaction;
import models.User;
import models.json.ErrorResponse;
import models.json.JSONResponse;
import models.json.MessageResponse;
import net.authorize.api.contract.v1.CreateTransactionResponse;
import play.Configuration;
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
    	if (login == null) {
    		login = false;
    	}
        return ok(index.render(userForm, productList, allTypes, login));
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
        	
        	mailService.sendEmailToken(user.email, user.token);
        	flash("message", "Email verification sent");
        	
        	session("userEmail", user.email);
	        return ok(Json.toJson(new MessageResponse("SUCCESS", "User was registered")));
	    }
    	
        //User user = formFactory.form(User.class).bindFromRequest().get();
        /*Form<User> userForm = formFactory.form(User.class).bindFromRequest();
        
        if (userForm.hasErrors()) {
        	List<Product> productList = Product.getAllProducts();
        	CardType[] allTypes = CardType.values();
    		return badRequest(views.html.index.render(userForm, productList, allTypes));
    	} 
        else {
        	User user = userForm.get();
        	user.token = Tokener.randomString(48);
        	user.save();
        	session("userEmail", user.email);
        	//mailService.sendEmailToken(user.email, user.token);
        	flash("message", "Email verification sent");
        	return redirect(routes.SignUpFlowController.chooseProductPage()); 
        }*/
    	
        /*DynamicForm form = formFactory.form().bindFromRequest();
        StringBuilder builder = new StringBuilder();
        builder.append("partnerCode=CRDPRF");
        builder.append("&partnerPass=kYmfR5@23");
        builder.append("&packageId=474");
        builder.append("&branding=CRDPRF");
        builder.append("&memberId=" + form.get("email"));
        builder.append("&firstname" + form.get("first_name"));
        builder.append("&lastname" + form.get("last_name"));
        builder.append("&address" + form.get("address"));
        builder.append("&city" + form.get("city"));
        builder.append("&state" + form.get("state"));
        builder.append("&zip" + form.get("zip"));
        builder.append("&phone" + form.get("phone"));
        builder.append("&email" + form.get("email"));
        builder.append("&password" + form.get("password"));
        builder.append("&action" + "CreateDashEnrollment");*/

        // fix SSL issue
        /*String feedUrl = "https://idcs.idandcredit.com/modal/portal/enroll.php";
        return ws.url(feedUrl).post(builder.toString()).thenApply(response ->
                        ok(response.getBody())
        );*/
        
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
   	 
    	CreditCard creditCard = Json.fromJson(json, CreditCard.class);
    	
	    if(creditCard == null) {
	        return badRequest(Json.toJson(new MessageResponse("ERROR", "Cannot parse JSON to CreditCard")));
	    } else {
	    	
	    	List<ValidationError> errors = creditCard.validate();
	    	if (errors != null) {
	    		
	    		return badRequest(Json.toJson(errors));
	    	}
	    	
	    	String month = json.findPath("month").textValue();
	    	String year = json.findPath("year").textValue();
	    	
	    	String expDateStr = month + "/" + year;
	    	DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/yyyy");
	    	YearMonth ym = YearMonth.parse(expDateStr, formatter);
	    	
	    	String userEmail = session().get("userEmail");
	    	User user = User.findByEmail(userEmail);
	    	
	    	creditCard.expDate = ym;
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
