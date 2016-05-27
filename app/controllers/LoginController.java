package controllers;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.avaje.ebean.Ebean;

import models.SecurityRole;
import models.User;
import models.json.AuthenticationSuccessResponse;
import models.json.ErrorResponse;
import models.json.JSONResponse;
import models.json.MessageResponse;
import models.json.SuccessLoginResponse;
import play.data.DynamicForm;
import play.data.FormFactory;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import services.CreditReportService;
import services.MailService;

@Singleton
public class LoginController extends Controller {
	
	@Inject
    private FormFactory formFactory;
	
	@Inject
    private MailService mailService;
	
	@Inject
	private CreditReportService creditReportService;
    
    public Result login(){
        DynamicForm form = formFactory.form().bindFromRequest();
        String email = form.get("email");
        User user = Ebean.find(User.class).where().eq("email", email).findUnique();
        
        if (user != null){
            if (!user.password.equals(form.get("password"))) {
            	
            	return badRequest(Json.toJson(new ErrorResponse("ERROR", "301", "Invalid Password")));
            }
            if(user.active == false){
            	mailService.sendEmailToken(user.email, user.token);
                
                return badRequest(Json.toJson(new ErrorResponse("ERROR", "302", "Account not verified - e-mail verification sent")));
            }
            else {
            	
            	//authenticate to idcs
            	/*JSONResponse response = creditReportService.authenticate(email);
            	if (response instanceof ErrorResponse) {
            		return badRequest(Json.toJson(response));
            	}*/
            	
                session("email", email);
                session("name", user.firstName);
                
                //check if admin user
                SecurityRole adminRole = SecurityRole.findByName("admin");
                if (user.roles.contains(adminRole)) {
                	session("admin", "admin");
                	return ok(Json.toJson(new SuccessLoginResponse("SUCCESS", "admin")));
                }
                else {
                	//store memberId in session
                	/*AuthenticationSuccessResponse authResponse = (AuthenticationSuccessResponse)response;
                	String memberId = authResponse.getMemberId();
                	session("memberId", memberId);*/
                	
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
