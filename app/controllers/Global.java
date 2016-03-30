package controllers;

import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.avaje.ebean.Ebean;

import models.CardType;
import models.CreditCard;
import models.Product;
import models.User;
import play.Configuration;
import play.data.DynamicForm;
import play.data.Form;
import play.data.FormFactory;
import play.libs.ws.WSClient;
import play.mvc.Controller;
import play.mvc.Result;
import services.CreditCardService;
import services.MailService;
import utils.Tokener;
import views.html.index;

@Singleton
public class Global extends Controller {

    @Inject
    private FormFactory formFactory;
    
    @Inject
    private WSClient ws;
    
    @Inject
    private MailService mailService;
    
    @Inject
    private CreditCardService creditCardService;
    
    @Inject
	private Configuration conf;

    public Result index(){
    	Form<User> userForm = formFactory.form(User.class);
        return ok(index.render(userForm));
    }
    
    public Result register() throws Exception {
        //User user = formFactory.form(User.class).bindFromRequest().get();
        Form<User> userForm = formFactory.form(User.class).bindFromRequest();
        
        if (userForm.hasErrors()) {
    		return badRequest(views.html.index.render(userForm));
    	} 
        else {
        	User user = userForm.get();
        	user.token = Tokener.randomString(48);
        	user.save();
        	session("userEmail", user.email);
        	//mailService.sendEmailToken(user.email, user.token);
        	flash("message", "Email verification sent");
        	return redirect(routes.Global.chooseProductPage()); 
        }
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

    public Result chooseProductPage() {
    	List<Product> productList = Product.getAllProducts();
    	return ok(views.html.chooseProduct.render(productList));
    }
    
    public Result chooseProduct() {
    	DynamicForm form = formFactory.form().bindFromRequest();
    	String productId = form.get("product");
    	session("productId", productId);
        return redirect(routes.Global.paymentDetailsPage());
    	
    }
    
    public Result paymentDetailsPage() {
    	
    	CardType[] allTypes = CardType.values();
    	
    	return ok(views.html.paymentDetails.render(allTypes));
    }
    
    public Result processPayment() {
    	
    	//save credit card info
    	DynamicForm form = formFactory.form().bindFromRequest();
    	
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
    	
    	final String loginId = conf.getString("authorise.net.login.id");
    	final String transactionKey = conf.getString("authorise.net.transaction.key");
    	creditCardService.charge(loginId, transactionKey, product.price, creditCard);
    	
    	return ok("Credit Card was saved");
    }
    
    public Result login(){
        DynamicForm form = formFactory.form().bindFromRequest();
        String email = form.get("email");
        User user = Ebean.find(User.class).where().eq("email", email).findUnique();
        if (user!=null){
            if (user.password.equals(form.get("password")) && user.active == true){
                session("email", email);
                session("name", user.firstName);
            }else{
                mailService.sendEmailToken(user.email,user.token);
                flash("message","Account not verified - Email verification sent");
            }
        } else {
            flash("message","invalid credentials");
        }
        
        return redirect(routes.Global.index());
    }

    public Result logout(){
        session().clear();
        
        return redirect(routes.Global.index());
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
       
        return redirect(routes.Global.index());
    }

    public Result forgotPassword(){
        DynamicForm form = formFactory.form().bindFromRequest();
        String email = form.get("email");
        User user = Ebean.find(User.class).where().eq("email", email).findUnique();
        if (user != null) {
            flash("message", "Password sent to email");
            mailService.sendEmailPassword(user.email, user.password);
        }
        else {
            flash("message","Could not find account with that email");
        }
        
        return redirect(routes.Global.index());
    }

    private boolean authorizePayment() {
        return true;
    }

}
