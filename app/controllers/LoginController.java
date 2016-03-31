package controllers;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.avaje.ebean.Ebean;

import models.User;
import play.data.DynamicForm;
import play.data.FormFactory;
import play.mvc.Controller;
import play.mvc.Result;
import services.MailService;

@Singleton
public class LoginController extends Controller {
	
	@Inject
    private FormFactory formFactory;
	
	@Inject
    private MailService mailService;
    
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
        
        return redirect(routes.SignUpFlowController.index());
    }

    public Result logout(){
        session().clear();
        
        return redirect(routes.SignUpFlowController.index());
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
        
        return redirect(routes.SignUpFlowController.index());
    }
}
