package controllers.admin;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.fasterxml.jackson.databind.JsonNode;

import be.objectify.deadbolt.java.actions.Group;
import be.objectify.deadbolt.java.actions.Restrict;
import errors.ValidationError;
import models.SecurityRole;
import models.User;
import models.enums.State;
import models.json.MessageResponse;
import models.json.ObjectCreatedResponse;
import play.Logger;
import play.data.DynamicForm;
import play.data.FormFactory;
import play.libs.Json;
import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Result;
import services.MailService;
import services.RoleService;
import services.UserService;
import utils.Tokener;

@Singleton
@Restrict(@Group("admin")) 
public class UserController extends Controller {
	
	@Inject
	private FormFactory formFactory;
	
	@Inject
	private UserService userService;
	
	@Inject
	private MailService mailService;
	
	@Inject
	private RoleService roleService;

	//users	
	public Result users() {
		
		String email = session().get("email");
		User user = userService.findByEmail(email);
		
		List<User> allUsers = userService.getAll();
		State[] states = State.values();
		
		return ok(views.html.adminUsers.render(user, allUsers, states));
	}
	
	@BodyParser.Of(BodyParser.Json.class)
	public Result editUser() {
		
		JsonNode json = request().body().asJson();
		User user = null;
		try {
			user = Json.fromJson(json, User.class);
		} 
		catch (RuntimeException e) {
			Logger.error("Cannot parse JSON to user.");
	        return badRequest(Json.toJson(new MessageResponse("ERROR", "Cannot parse JSON to user")));
		}
	    	    	
	    List<ValidationError> errors = userService.validate(user, true);
	    if (errors != null) {
	    	Logger.error(errors.toString());
	    	return badRequest(Json.toJson(errors));
	    }
	    
	    User userDB = userService.getById(user.getId());
	    if (userDB == null) {
	    	Logger.error("User with id " + user.getId() + " is not found");
	    	return badRequest(Json.toJson(new MessageResponse("ERROR", "User with id " + user.getId() + " is not found")));
	    }
	    userService.update(userDB);
	    
	    return ok(Json.toJson(new MessageResponse("SUCCESS", "User was edited successfully")));
	    		
	}
	
	public Result deleteUser() {
		
		DynamicForm form = formFactory.form().bindFromRequest();
		
		long id = Long.parseLong(form.get("id"));
		User user = userService.getById(id);
		if (user == null) {
	    	Logger.error("User with id " + id + " is not found");
	    	return badRequest(Json.toJson(new MessageResponse("ERROR", "User with id " + id + " is not found")));
	    }
		
		boolean deleted = userService.delete(user);
		
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
	   	
		User user = null;
		try {
			user = Json.fromJson(json, User.class);
		} 
		catch (RuntimeException e) {
			Logger.error("Cannot parse JSON to user");
	        return badRequest(Json.toJson(new MessageResponse("ERROR", "Cannot parse JSON to user")));
		}
			    	
	    List<ValidationError> errors = userService.validate(user, false);
	    if (errors != null) {
	    	Logger.error(errors.toString());
	    	return badRequest(Json.toJson(errors));
	    }
	    
	    SecurityRole userRole = roleService.findByName("user");
	    List<SecurityRole> roles = new ArrayList<SecurityRole>();
	    roles.add(userRole);
	    user.setRoles(roles);
	    user.setToken(Tokener.randomString(48));
	    	    	
	    userService.save(user);
        
        mailService.sendEmailToken(user.getEmail(), user.getToken());
        
	    return ok(Json.toJson(new ObjectCreatedResponse("SUCCESS", "User was created successfully", user.getId())));

	}
}
