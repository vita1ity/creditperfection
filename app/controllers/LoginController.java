package controllers;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.avaje.ebean.Ebean;

import models.SecurityRole;
import models.User;
import models.json.ErrorResponse;
import models.json.MessageResponse;
import models.json.SuccessLoginResponse;
import play.data.DynamicForm;
import play.data.FormFactory;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import services.MailService;

@Singleton
public class LoginController extends Controller {
	
	@Inject
    private FormFactory formFactory;
	
	@Inject
    private MailService mailService;
    
	/*public Result loginPage() {
        return ok(views.html.login.render());
    }    */
	
	//login page form
	/*public Result login(){
        DynamicForm form = formFactory.form().bindFromRequest();
        String email = form.get("email");
        User user = Ebean.find(User.class).where().eq("email", email).findUnique();
        
        if (user != null){
            if (!user.password.equals(form.get("password"))) {
            	flash("message","Invalid Password");
                return badRequest(views.html.login.render());
            }
            if(user.active == false){
            	mailService.sendEmailToken(user.email,user.token);
                flash("message","Account not verified - Email verification sent");
                return badRequest(views.html.login.render());
            }
            else {
                session("email", email);
                session("name", user.firstName);
                session("admin", "admin");
                
                //check if admin user
                Role adminRole = Role.findByName("admin");
                if (user.roles.contains(adminRole)) {
                	return redirect(routes.AdminController.users());
                }
                else {
                	return redirect(routes.UserController.userPage());
                }
            }
        } else {
            flash("message","Invalid Email");
            return badRequest(views.html.login.render());
        }
        
    }*/
	
	//login ajax method
    public Result login(){
        DynamicForm form = formFactory.form().bindFromRequest();
        String email = form.get("email");
        User user = Ebean.find(User.class).where().eq("email", email).findUnique();
        
        if (user != null){
            if (!user.password.equals(form.get("password"))) {
            	
            	return badRequest(Json.toJson(new ErrorResponse("ERROR", "301", "Invalid Password")));
            }
            if(user.active == false){
            	mailService.sendEmailToken(user.email,user.token);
                
                return badRequest(Json.toJson(new ErrorResponse("ERROR", "302", "Account not verified - e-mail verification sent")));
            }
            else {
                session("email", email);
                session("name", user.firstName);
                
                //check if admin user
                SecurityRole adminRole = SecurityRole.findByName("admin");
                if (user.roles.contains(adminRole)) {
                	session("admin", "admin");
                	return ok(Json.toJson(new SuccessLoginResponse("SUCCESS", "admin")));
                }
                else {
                	return ok(Json.toJson(new SuccessLoginResponse("SUCCESS", "user")));
                }
            }
        } else {
            
            return badRequest(Json.toJson(new ErrorResponse("ERROR", "303", "Invalid e-mail")));
        }
        
    }

    public Result logout(){
        session().clear();
        
        return redirect(routes.SignUpFlowController.index(false));
    }
    
    //forgot password form
    /*public Result forgotPassword(){
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
        
        return redirect(routes.SignUpFlowController.index());
    }*/
    
    //forgot password ajax method
    public Result forgotPassword(){
        DynamicForm form = formFactory.form().bindFromRequest();
        String email = form.get("email");
        User user = Ebean.find(User.class).where().eq("email", email).findUnique();
        if (user != null) {
            mailService.sendEmailPassword(user.email, user.password);
            return ok(Json.toJson(new MessageResponse("SUCCESS", "Password sent to email")));
        }
        else {
            flash("message","The account with given email is not registered");
            return badRequest(Json.toJson(new ErrorResponse("ERROR", "304", "The account with given email is not registered")));
        }
        
    }
    
}
