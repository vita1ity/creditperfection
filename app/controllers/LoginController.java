package controllers;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.avaje.ebean.Ebean;

import models.SecurityRole;
import models.User;
import models.enums.SubscriptionStatus;
import models.json.ErrorResponse;
import models.json.MessageResponse;
import models.json.SuccessLoginResponse;
import play.data.DynamicForm;
import play.data.FormFactory;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import services.CreditReportService;
import services.MailService;
import services.RoleService;

@Singleton
public class LoginController extends Controller {
	
	@Inject
    private FormFactory formFactory;
	
	@Inject
    private MailService mailService;
	
	@Inject
	private CreditReportService creditReportService;
    
	@Inject
	private RoleService roleService;
	
    public Result login(){
        DynamicForm form = formFactory.form().bindFromRequest();
        String email = form.get("email");
        User user = Ebean.find(User.class).where().eq("email", email).findUnique();
        
        if (user != null){
            if (!user.getPassword().equals(form.get("password"))) {
            	
            	return badRequest(Json.toJson(new ErrorResponse("ERROR", "301", "Invalid Password")));
            }
           //check if admin user
            SecurityRole adminRole = roleService.findByName("admin");
            if (user.getRoles().contains(adminRole)) {
            	session("email", email);
                session("name", user.getFirstName());
            	session("admin", "admin");
            	return ok(Json.toJson(new SuccessLoginResponse("SUCCESS", "admin", true)));
            }
            else if (user.getActive() == false && user.getSubscription() != null && 
            		user.getSubscription().getStatus().equals(SubscriptionStatus.CANCELLED)) {
            	
            	return ok(Json.toJson(new SuccessLoginResponse("SUCCESS", "user", false)));
            }
            else if (user.getActive() == false) {
            	mailService.sendEmailToken(user.getEmail(), user.getToken());
                
                return badRequest(Json.toJson(new ErrorResponse("ERROR", "302", "Account not verified - e-mail verification sent")));
            }
            
            else {
            	
            	//authenticate to idcs
            	/*JSONResponse response = creditReportService.authenticate(email, user.password);
            	if (response instanceof ErrorResponse) {
            		return badRequest(Json.toJson(response));
            	}*/
            	
            	//store memberId in session
            	/*AuthenticationSuccessResponse authResponse = (AuthenticationSuccessResponse)response;
            	String memberId = authResponse.getMemberId();
            	session("memberId", memberId);*/
            	
            	if (user.getKbaQuestions() == null) {
                	return badRequest(Json.toJson(new ErrorResponse("ERROR", "304", "You haven't completed registration process. "
                			+ "Please contact support for help: support@creditperfection.org ")));
                }
            	else {
            		session("email", email);
                    session("name", user.getFirstName());
                    
                    return ok(Json.toJson(new SuccessLoginResponse("SUCCESS", "user", true)));
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
            mailService.sendEmailPassword(user.getEmail(), user.getPassword());
            return ok(Json.toJson(new MessageResponse("SUCCESS", "Password sent to email")));
        }
        else {
            flash("message","The account with given email is not registered");
            return badRequest(Json.toJson(new ErrorResponse("ERROR", "304", "The account with given email is not registered")));
        }
        
    }
    
}
