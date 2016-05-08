package controllers;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.fasterxml.jackson.databind.JsonNode;

import errors.ValidationError;
import models.Role;
import models.User;
import models.json.MessageResponse;
import play.data.DynamicForm;
import play.data.FormFactory;
import play.libs.Json;
import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import services.MailService;
import utils.Tokener;

@Singleton
@Security.Authenticated(Secured.class)
public class AdminController extends Controller {

	@Inject
    private FormFactory formFactory;
	
	@Inject
    private MailService mailService;
	
	public Result users() {
		
		String email = session().get("email");
		User user = User.findByEmail(email);
		
		List<User> allUsers = User.find.all();
		
		return ok(views.html.admin.render(user, allUsers));
	}
	
	@BodyParser.Of(BodyParser.Json.class)
	public Result editUser() {

		JsonNode json = request().body().asJson();
   	 
    	User user = Json.fromJson(json, User.class);
	    if(user == null) {
	        return badRequest(Json.toJson(new MessageResponse("ERROR", "Cannot parse JSON to user")));
	    }
	    else {
	    	
	    	List<ValidationError> errors = user.validate(true);
	    	if (errors != null) {
	    		
	    		return badRequest(Json.toJson(errors));
	    	}
	    	
	    	User userDB = User.find.byId(user.id);
	    	if (userDB == null) {
	    		return badRequest(Json.toJson(new MessageResponse("ERROR", "User with id" + user.id + "is not found")));
	    	}
	    	//user.roles = userDB.roles;
	    	//TODO not working! fix it
	    	userDB.updateUserInfo(user);
	    	userDB.save();
	    	
	        return ok(Json.toJson(new MessageResponse("SUCCESS", "User was edited successfully")));
	    }
		
	}
	
	public Result deleteUser() {
		
		DynamicForm form = formFactory.form().bindFromRequest();
		
		long id = Long.parseLong(form.get("id"));
		User user = User.find.byId(id);
		boolean deleted = user.delete();
		
		if (deleted) {
			return ok(Json.toJson(new MessageResponse("SUCCESS", "User was deleted successfully")));
		}
		else {
			return badRequest(Json.toJson(new MessageResponse("ERROR", "User was not deleted")));
		}
		
	}
	
	@BodyParser.Of(BodyParser.Json.class)
	public Result addUser() {
		
		JsonNode json = request().body().asJson();
	   	 
    	User user = Json.fromJson(json, User.class);
	    if(user == null) {
	        return badRequest(Json.toJson(new MessageResponse("ERROR", "Cannot parse JSON to user")));
	    }
	    else {
	    	
	    	List<ValidationError> errors = user.validate(true);
	    	if (errors != null) {
	    		
	    		return badRequest(Json.toJson(errors));
	    	}
	    	
	    	user.token = Tokener.randomString(48);
	    	List<Role> roles = new ArrayList<Role>();
	    	Role userRole = Role.findByName("user");
	    	roles.add(userRole);
	    	user.roles = roles;
        	user.save();
        	
        	mailService.sendEmailToken(user.email, user.token);
        	flash("message", "Email verification sent");
	    	
	        return ok(Json.toJson(new MessageResponse("SUCCESS", "User was created successfully")));
	    }
		
	}
	
}
