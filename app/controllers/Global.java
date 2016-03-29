package controllers;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.avaje.ebean.Ebean;

import models.Product;
import models.User;
import play.data.DynamicForm;
import play.data.FormFactory;
import play.libs.ws.WSClient;
import play.mvc.Controller;
import play.mvc.Result;
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
    

    public Result index(){
        return ok(index.render());
    }

    public Result register() throws Exception {
        User user = formFactory.form(User.class).bindFromRequest().get();
        user.token = Tokener.randomString(48);
        user.save();
        mailService.sendEmailToken(user.email, user.token);
        flash("message", "Email verification sent");
        
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
        return redirect(routes.Global.chooseProductPage()); 
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
    	return ok();
    }
    
    public Result login(){
        DynamicForm form = formFactory.form().bindFromRequest();
        String email = form.get("email");
        User user = Ebean.find(User.class).where().eq("email", email).findUnique();
        if (user!=null){
            if (user.password.equals(form.get("password")) && user.active == true){
                session("email",email);
                session("name",user.first_name);
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
