package security;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import javax.inject.Inject;

import controllers.routes;
import models.User;
import play.Configuration;
import play.Logger;
import play.mvc.Http.Context;
import play.mvc.Http.Session;
import play.mvc.Result;
import play.mvc.Security;
import services.UserService;

public class Secured extends Security.Authenticator {

	@Inject
	private UserService userService;
	
	@Inject
	private Configuration conf;
	
    @Override
    public String getUsername(Context ctx){
    	Session session = ctx.session();
    	
    	String email = session.get("email");
    	User user = userService.findByEmailOnlyActive(email);
    	if (user == null) {
    		return null;
    	}
    	
    	Logger.info("Checking if session is expired...");
    	// see if the session is expired
        String previousTick = session.get("userTime");
        if (previousTick != null && !previousTick.equals("")) {
            LocalDateTime previousT = LocalDateTime.parse(previousTick);
            LocalDateTime now = LocalDateTime.now();
            long timeout = Long.valueOf(conf.getString("user.session.timeout"));
            
            long minutesBetween = ChronoUnit.MINUTES.between(previousT, now);
            Logger.info("minutesBetween: " + minutesBetween + ", timeout: " + timeout);
            
            if (minutesBetween > timeout) {
                // session expired
                session.clear();
                session.put("sessionExpired", "true");
                return null;
            } 
        }
        // update time in session
        String tickString = LocalDateTime.now().toString();
        session.put("userTime", tickString);
    	
    	return email;
    	
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