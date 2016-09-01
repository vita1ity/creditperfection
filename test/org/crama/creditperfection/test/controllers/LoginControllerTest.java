package org.crama.creditperfection.test.controllers;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;
import static play.inject.Bindings.bind;
import static play.mvc.Http.Status.OK;
import static play.mvc.Http.Status.BAD_REQUEST;
import static play.test.Helpers.contentAsString;
import static play.test.Helpers.route;

import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import org.crama.creditperfection.test.base.ControllerTestBase;
import org.crama.creditperfection.test.builders.UserBuilder;
import org.junit.Test;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.ImmutableMap;

import controllers.LoginController;
import models.KBAQuestions;
import models.SecurityRole;
import models.User;
import play.Logger;
import play.data.FormFactory;
import play.inject.guice.GuiceApplicationBuilder;
import play.libs.Json;
import play.mvc.Result;
import play.mvc.Http.RequestBuilder;
import play.test.Helpers;
import services.MailService;
import services.RoleService;
import services.UserService;

public class LoginControllerTest extends ControllerTestBase {
	
	private static User testUser;
	private static User testUserAdmin;
	private static SecurityRole adminRole;
	    
	@Mock
    private UserService userServiceMock;

	@Mock
    private RoleService roleServiceMock;
	
	@Mock
    private MailService mailServiceMock;
	
	@InjectMocks
	private LoginController loginController;
	
	private void setUp() {
		MockitoAnnotations.initMocks(this);
		
		List<SecurityRole> roles = Arrays.asList(new SecurityRole(1, "user"), 
				 new SecurityRole(2, "admin"));
		
		testUserAdmin = new UserBuilder().id(1).roles(roles).build();
		testUser = new UserBuilder().id(2).roles(Arrays.asList(roles.get(0))).build();
		
		adminRole = new SecurityRole(2, "admin");		
	}
	
	@Override
    public GuiceApplicationBuilder getBuilder() {
        GuiceApplicationBuilder builder = super.getBuilder();
        setUp();
        return builder.overrides(bind(UserService.class).toInstance(userServiceMock),
        						 bind(MailService.class).toInstance(mailServiceMock),
        						 bind(RoleService.class).toInstance(roleServiceMock));
        						 					 
    }
	
	@Test
	public void login_Success() {
		
		User registeredUser = new UserBuilder().id(3).build();
		registeredUser.setKbaQuestions(new KBAQuestions());
		when(userServiceMock.findByEmail(Matchers.anyString())).thenReturn(registeredUser);
		
		RequestBuilder request = Helpers.fakeRequest()
				.method("POST")
				.bodyForm(ImmutableMap.of("email", registeredUser.getEmail(),
										  "password", registeredUser.getPassword()))
				.uri(controllers.routes.LoginController.login().url());
		
		Result result = route(request);
		assertEquals(OK, result.status());
	    
	    JsonNode responseNode = Json.parse(contentAsString(result));
	    
	    assertEquals(responseNode.get("status").asText(), "SUCCESS");	
	    assertEquals(responseNode.get("role").asText(), "user");
	    assertEquals(responseNode.get("subscription").asBoolean(), true);
				
	}
	
	@Test
	public void login_Success_Admin() {
		
		User registeredUser = testUserAdmin;
		registeredUser.setKbaQuestions(new KBAQuestions());
		when(userServiceMock.findByEmail(Matchers.anyString())).thenReturn(registeredUser);
		when(roleServiceMock.findByName("admin")).thenReturn(adminRole);
		
		RequestBuilder request = Helpers.fakeRequest()
				.method("POST")
				.bodyForm(ImmutableMap.of("email", registeredUser.getEmail(),
										  "password", registeredUser.getPassword()))
				.uri(controllers.routes.LoginController.login().url());
		
		Result result = route(request);
		assertEquals(OK, result.status());
	    
	    JsonNode responseNode = Json.parse(contentAsString(result));
	    
	    assertEquals(responseNode.get("status").asText(), "SUCCESS");	
	    assertEquals(responseNode.get("role").asText(), "admin");
	    assertEquals(responseNode.get("subscription").asBoolean(), true);
				
	}
	
	@Test
	public void login_Failure_InavlidPassword() {
		
		User registeredUser = new UserBuilder().id(3).build();
		registeredUser.setKbaQuestions(new KBAQuestions());
		when(userServiceMock.findByEmail(Matchers.anyString())).thenReturn(registeredUser);
		
		RequestBuilder request = Helpers.fakeRequest()
				.method("POST")
				.bodyForm(ImmutableMap.of("email", registeredUser.getEmail(),
										  "password", "wrond_password"))
				.uri(controllers.routes.LoginController.login().url());
		
		Result result = route(request);
		assertEquals(BAD_REQUEST, result.status());
	    
	    JsonNode responseNode = Json.parse(contentAsString(result));
	    
	    assertEquals(responseNode.get("status").asText(), "ERROR");	
	    assertEquals(responseNode.get("errorMessage").asText(), "Invalid Password");
	    				
	}
	
	@Test
	public void login_Failure_UncompleteRegistration() {
		
		User registeredUser = new UserBuilder().id(3).build();
		
		when(userServiceMock.findByEmail(Matchers.anyString())).thenReturn(registeredUser);
		
		RequestBuilder request = Helpers.fakeRequest()
				.method("POST")
				.bodyForm(ImmutableMap.of("email", registeredUser.getEmail(),
										  "password", registeredUser.getPassword()))
				.uri(controllers.routes.LoginController.login().url());
		
		Result result = route(request);
		assertEquals(BAD_REQUEST, result.status());
	    
	    JsonNode responseNode = Json.parse(contentAsString(result));
	    
	    assertEquals(responseNode.get("status").asText(), "ERROR");	
	    assertEquals(responseNode.get("errorMessage").asText(), "You haven't completed registration process. "
    			+ "Please contact support for help: support@creditperfection.org ");
	    				
	}
}
