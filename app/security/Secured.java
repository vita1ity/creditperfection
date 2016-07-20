package security;

import play.mvc.Http.Context;
import controllers.routes;
import play.mvc.Result;
import play.mvc.Security;

public class Secured extends Security.Authenticator {

    @Override
    public String getUsername(Context ctx){
        return ctx.session().get("email");
    }

    @Override
    public Result onUnauthorized(Context ctx){
        
        //redirect to index page with open login form
    	Boolean login = true;
        return redirect(routes.SignUpFlowController.index(login));
    }
}