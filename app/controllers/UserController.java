package controllers;

import javax.inject.Singleton;

import models.User;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;

@Singleton
@Security.Authenticated(Secured.class)
public class UserController extends Controller {
	
	public Result userPage() {
		
		String email = session().get("email");
		User user = User.findByEmail(email);
		
        return ok(views.html.user.render(user));
    }
	
}
