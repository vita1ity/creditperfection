package security;

import javax.inject.Inject;

import controllers.routes;
import models.User;
import play.mvc.Http.Context;
import play.mvc.Result;
import play.mvc.Security;
import services.UserService;

public class Secured extends Security.Authenticator {

	@Inject
	private UserService userService;
	
    @Override
    public String getUsername(Context ctx){
    	String email = ctx.session().get("email");
    	User user = userService.findByEmailOnlyActive(email);
    	if (user == null) {
    		return null;
    	}
    	else {
    		return email;
    	}
    }

    @Override
    public Result onUnauthorized(Context ctx){
        
        //redirect to index page with open login form
    	String email = ctx.session().get("email");
    	if (email != null) {
    		User user = userService.findByEmailOnlyActive(email);
        	if (user == null) {
        		
        		return redirect(routes.PaymentController.paymentPage());
        		
        	}
        	else {
        		Boolean login = true;
        		return redirect(routes.SignUpFlowController.index(login));
        	}
        	
    		
    	}
    	else {
    		Boolean login = true;
    		return redirect(routes.SignUpFlowController.index(login));
    	}
    }
}