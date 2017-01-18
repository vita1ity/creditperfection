package controllers.admin;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.avaje.ebean.PagedList;
import com.fasterxml.jackson.databind.JsonNode;

import be.objectify.deadbolt.java.actions.Group;
import be.objectify.deadbolt.java.actions.Restrict;
import errors.ValidationError;
import forms.UserSearchForm;
import models.Product;
import models.SecurityRole;
import models.Subscription;
import models.User;
import models.enums.State;
import models.enums.SubscriptionStatus;
import models.json.MessageResponse;
import models.json.ObjectCreatedResponse;
import models.json.PagedObjectResponse;
import models.json.UserSubscriptionSearchResultResponse;
import play.Configuration;
import play.Logger;
import play.cache.CacheApi;
import play.data.DynamicForm;
import play.data.FormFactory;
import play.libs.Json;
import play.mvc.BodyParser;
import play.mvc.Controller;
import play.mvc.Result;
import services.ProductService;
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
	private RoleService roleService;
	
	@Inject
	private Configuration conf;
	
	@Inject
	private CacheApi cache;
	
	@Inject
	private ProductService productService;

	//users	
	public Result users() {
		
		session().remove("query");
		cache.remove("search.form");
		
		String email = session().get("email");
		User user = userService.findByEmail(email);
		
		int pageSize = conf.getInt("page.size");
		PagedList<User> usersPage = userService.getUsersPage(0, pageSize);
		State[] states = State.values();
		
		int numberOfPages = usersPage.getTotalPageCount();
		
		Logger.info("Number of pages for users: " + Integer.toString(numberOfPages));
		
		int[] displayedPages = new int[1];
		if (numberOfPages > 10 ) {
			displayedPages = new int[10];
			for (int i = 0; i < 10; i++) {
				displayedPages[i] = (i + 1);
			}
		}
		else {
			displayedPages = new int[numberOfPages];
			for (int i = 0; i < numberOfPages; i++) {
				displayedPages[i] = (i + 1);
			}
		}
		
		int currentPage = 1;
		
		List<User> users = usersPage.getList();
		
		List<User> allUsers = userService.getAll();
		List<Product> allProducts = productService.getAll();
		SubscriptionStatus[] allStatuses = SubscriptionStatus.values(); 
		
		return ok(views.html.adminUsers.render(user, users, states, numberOfPages, displayedPages, currentPage, allUsers, allProducts, allStatuses));
	}
	
	public Result getUsers(int page) {
		
		if (page < 1) {
			return badRequest(Json.toJson(new MessageResponse("ERROR", "Invalid page value")));
		}
		
		String query = session().get("query");
		UserSearchForm searchForm = cache.get("search.form");
		Logger.info("query: " + query);
		Logger.info("search form: " + searchForm);
		
		int pageSize = conf.getInt("page.size");
		
		PagedList<User> usersPage = null;
		List<User> users = null;
		List<Subscription> subscriptions = new ArrayList<Subscription>();
		
		if (query != null) {
			usersPage = userService.searchByName(query, page - 1, pageSize);
			users = usersPage.getList();
			
			for (User user: users) {
				subscriptions.add(user.getSubscription());
			}
			
		}
		else if (searchForm != null) {
			usersPage = userService.preciseSearch(searchForm, page - 1, pageSize);
			users = usersPage.getList();
			
			for (User user: users) {
				subscriptions.add(user.getSubscription());
			}
			
		}
		else {
			usersPage = userService.getUsersPage(page - 1, pageSize);
		}
		
		if (subscriptions.size() != 0) {
			return ok(Json.toJson(new UserSubscriptionSearchResultResponse("SUCCESS", users, subscriptions, page, usersPage.getTotalPageCount())));
		}
		else {
			return ok(Json.toJson(new PagedObjectResponse("SUCCESS", usersPage.getList(), page, usersPage.getTotalPageCount())));
		}
		
		
	}
	
	public Result viewAll() {
		
		session().remove("query");
		cache.remove("search.form");
		
		int pageSize = conf.getInt("page.size");
		
		PagedList<User> usersPage = userService.getUsersPage(0, pageSize);
		
		return ok(Json.toJson(new PagedObjectResponse("SUCCESS", usersPage.getList(), 1, usersPage.getTotalPageCount())));
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
	    userService.updateInfo(userDB, user);
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
	    user.setActive(true);
	    	    	
	    userService.save(user);
        
        return ok(Json.toJson(new ObjectCreatedResponse("SUCCESS", "User was created successfully", user.getId())));

	}
	
	public Result searchUser() {
		
		DynamicForm form = formFactory.form().bindFromRequest();
		
		String query = form.get("query");
		
		session().put("query", query);
		
		if (query == null) {
			return badRequest(Json.toJson(new MessageResponse("ERROR", "Please enter search criteria")));
		}
		
		int pageSize = conf.getInt("page.size");
		
		PagedList<User> searchResults = userService.searchByName(query, 0, pageSize);
		
		List<Subscription> subscriptions = new ArrayList<Subscription>();
		List<User> users = searchResults.getList();
		
		for (User user: users) {
			subscriptions.add(user.getSubscription());
		}
		
		return ok(Json.toJson(new UserSubscriptionSearchResultResponse("SUCCESS", users, subscriptions, 1, searchResults.getTotalPageCount())));
	}
	
	@BodyParser.Of(BodyParser.Json.class)
	public Result preciseSearchUser() {
		
		JsonNode json = request().body().asJson();
		UserSearchForm searchForm = null;
		try {
			searchForm = Json.fromJson(json, UserSearchForm.class);
		} 
		catch (RuntimeException e) {
			Logger.error("Cannot parse JSON to UserSearchForm.");
	        return badRequest(Json.toJson(new MessageResponse("ERROR", "Cannot parse JSON to search form")));
		}
		
	    List<ValidationError> errors = searchForm.validate();
	    if (errors.size() != 0) {
	    	Logger.error(errors.toString());
	    	return badRequest(Json.toJson(errors));
	    }
		
	    cache.set("search.form", searchForm);
	    	    	
		int pageSize = conf.getInt("page.size");
		
		PagedList<User> searchResults = userService.preciseSearch(searchForm, 0, pageSize);
		
		List<Subscription> subscriptions = new ArrayList<Subscription>();
		List<User> users = searchResults.getList();
		
		for (User user: users) {
			subscriptions.add(user.getSubscription());
		}
		
		return ok(Json.toJson(new UserSubscriptionSearchResultResponse("SUCCESS", users, subscriptions, 1, searchResults.getTotalPageCount())));
				
	}
}
