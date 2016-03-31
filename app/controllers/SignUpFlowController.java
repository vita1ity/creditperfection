package controllers;

import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.avaje.ebean.Ebean;
import com.fasterxml.jackson.databind.JsonNode;

import models.CardType;
import models.CreditCard;
import models.Product;
import models.User;
import play.Configuration;
import play.data.DynamicForm;
import play.data.Form;
import play.data.FormFactory;
import errors.ValidationError;
import play.libs.Json;
import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Result;
import services.CreditCardService;
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
	private Configuration conf;

    public Result index(){
    	
    	Form<User> userForm = formFactory.form(User.class);
    	List<Product> productList = Product.getAllProducts();
    	CardType[] allTypes = CardType.values();
    	
        return ok(index.render(userForm, productList, allTypes));
    }
    
    @BodyParser.Of(BodyParser.Json.class)
    public Result register() throws Exception {
    	
    	JsonNode json = request().body().asJson();
    	 
    	User user = Json.fromJson(json, User.class);
	    if(user == null) {
	        return badRequest("{message: Cannot parse JSON to user}");
	    } else {
	    	
	    	//TODO validate user
	    	List<ValidationError> errors = user.validate();
	    	if (errors != null) {
	    		
	    		for (ValidationError err: errors) {
	    			System.out.println(err);
	    		}
	    		
	    		return badRequest(Json.toJson(errors));
	    	}
	    	
	    	
	    	user.token = Tokener.randomString(48);
        	user.save();
        	session("userEmail", user.email);
	        return ok(Json.toJson("{message: success}"));
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

    public Result chooseProduct() {
    	DynamicForm form = formFactory.form().bindFromRequest();
    	String product = form.get("product");
    	session("productId", product);
    	return ok("success");
        
    }
    
    public Result processPayment() {
    	
    	JsonNode json = request().body().asJson();
   	 
    	CreditCard creditCard = Json.fromJson(json, CreditCard.class);
    	
	    if(creditCard == null) {
	        return badRequest("Cannot parse JSON to CreditCard");
	    } else {
	    	
	    	System.out.println(creditCard);
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
	    	
	    	final String loginId = conf.getString("authorise.net.sandbox.login.id");
	    	final String transactionKey = conf.getString("authorise.net.sandbox.transaction.key");
	    	creditCardService.charge(loginId, transactionKey, product.price, creditCard);
	    	
	    	return ok("success");
	    	
	    }
	    
	    
	    
	    
    	//save credit card info
    	/*DynamicForm form = formFactory.form().bindFromRequest();
    	
    	//TODO add form validations
    	
    	String name = form.get("name");
    	String cardType = form.get("cardType");
    	String digits = form.get("digits");
    	String month = form.get("month");
    	String year = form.get("year");
    	String cvv = form.get("cvv");
    	
    	String expDateStr = month + "/" + year;
    	DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/yyyy");
    	YearMonth ym = YearMonth.parse(expDateStr, formatter);
    	
    	CardType type = CardType.valueOf(CardType.class, cardType);
    	
    	int cvvCode = Integer.parseInt(cvv);
    	
    	String userEmail = session().get("userEmail");
    	User user = User.findByEmail(userEmail);
    	
    	CreditCard creditCard = new CreditCard(name, type, digits, ym, cvvCode, user);
    	creditCard.save();
    	
    	//charge amount for the product from credit card
    	long productId = Long.parseLong(session().get("productId"));
    	Product product = Product.getById(productId);
    	
    	final String loginId = conf.getString("authorise.net.sandbox.login.id");
    	final String transactionKey = conf.getString("authorise.net.sandbox.transaction.key");
    	creditCardService.charge(loginId, transactionKey, product.price, creditCard);
    	
    	return ok("Credit Card was saved"); */
    }
    
    



    public Result registerToken(String token){
        User user = Ebean.find(User.class).where().eq("token", token).findUnique();
        if (user!=null){
            user.active = true;
            user.update();
            flash("message", "Account verified, please login");
        }else{
            flash("error", "Account not verified, please try again");
        }
       
        return redirect(routes.SignUpFlowController.index());
    }

    

    private boolean authorizePayment() {
        return true;
    }

}
