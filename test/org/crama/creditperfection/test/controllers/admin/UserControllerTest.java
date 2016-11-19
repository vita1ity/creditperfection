package org.crama.creditperfection.test.controllers.admin;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static play.inject.Bindings.bind;
import static play.mvc.Http.Status.BAD_REQUEST;
import static play.mvc.Http.Status.OK;
import static play.test.Helpers.contentAsString;
import static play.test.Helpers.route;

import java.util.Arrays;
import java.util.List;

import org.crama.creditperfection.test.base.ControllerTestBase;
import org.crama.creditperfection.test.builders.UserBuilder;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import controllers.admin.UserController;
import errors.ValidationError;
import models.SecurityRole;
import models.User;
import play.Configuration;
import play.inject.guice.GuiceApplicationBuilder;
import play.libs.Json;
import play.mvc.Http.RequestBuilder;
import play.mvc.Result;
import play.test.Helpers;
import services.MailService;
import services.RoleService;
import services.UserService;

public class UserControllerTest extends ControllerTestBase {
	
	private static final String TEST_EMAIL = "test@gmail.com";
	private static User testUser;
	
	@Mock
	private UserService userServiceMock;
	
	@Mock
	private MailService mailServiceMock;
	
	@Mock
	private RoleService roleServiceMock;
	
	@Mock
	private Configuration confMock;
	
	@InjectMocks
	private UserController userController;
	
	private void setUp() {
		MockitoAnnotations.initMocks(this);	
		
		List<SecurityRole> roles = Arrays.asList(new SecurityRole(1, "user"), 
				 new SecurityRole(2, "admin"));
		testUser = new UserBuilder().email(TEST_EMAIL).id(1).roles(roles).build();
				
		List<User> allUsers = Arrays.asList(new UserBuilder()
												.id(11)
												.firstName("John")
												.email("john@gmail.com")
												.build(),
											new UserBuilder().firstName("Jeremy")
												.id(12)
												.email("jeremy@gmail.com")
												.build(),
												testUser);  
		
		when(confMock.getInt(Mockito.anyString())).thenReturn(10);
		when(userServiceMock.getAll()).thenReturn(allUsers);		
		when(userServiceMock.findByEmail(testUser.getEmail())).thenReturn(testUser);
		when(userServiceMock.getById(testUser.getId())).thenReturn(testUser);
		when(userServiceMock.validate(testUser, false)).thenReturn(null);
		when(userServiceMock.validate(testUser, true)).thenReturn(null);

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
	public void testUsers() {
		
		Result result = userController.users();
		assertEquals(OK, result.status());
		assertEquals("utf-8", result.charset().get());
		assertEquals("text/html", result.contentType().get());
		
		assertTrue(contentAsString(result).contains("Add User"));
		
		User firstUser = userServiceMock.getAll().get(0);
		User secondUser = userServiceMock.getAll().get(1);
		User thirdUser = userServiceMock.getAll().get(2);
		
		assertTrue("The first user was not found", contentAsString(result).contains(firstUser.getFirstName()));
		assertTrue("The second user was not found", contentAsString(result).contains(secondUser.getFirstName()));
		assertTrue("The third user was not found", contentAsString(result).contains(thirdUser.getFirstName()));

		verify(confMock, times(1)).getInt(Mockito.anyString());
		
	}
	
	@Test
	public void testEditUser_Success() {
		       
	   	testUser.setFirstName("Ben");		
		JsonNode testUserJson = Json.toJson(testUser);
		
		RequestBuilder request = Helpers.fakeRequest()
									.method("POST")
									.bodyJson(testUserJson)
									.uri(controllers.admin.routes.UserController.editUser().url());
		
		
	    Result result = route(request);
	    assertEquals(OK, result.status());
	    
	    JsonNode responseNode = Json.parse(contentAsString(result));
	    	    
	    assertEquals(responseNode.get("status").asText(), "SUCCESS");	
	    assertEquals(responseNode.get("message").asText(), "User was edited successfully");
	}
	
	@Test
	public void testEditUser_Failure_ParsingJson() {
		       
		JsonNode testUserJson = Json.toJson("{ \"someOther\": \"json\" }");
		
		RequestBuilder request = Helpers.fakeRequest()
									.method("POST")
									.bodyJson(testUserJson)
									.uri(controllers.admin.routes.UserController.editUser().url());		
		
		Result result = route(request);
	    assertEquals(BAD_REQUEST, result.status());
	    	    
	    JsonNode responseNode = Json.parse(contentAsString(result));	    
	    
	    assertEquals(responseNode.get("status").asText(), "ERROR");	
	    assertEquals(responseNode.get("message").asText(), "Cannot parse JSON to user");
	}
	
	@Test
	public void testEditUser_Failure_ValidationErrors() {
		       
		JsonNode testUserJson = Json.toJson(testUser);
		
		when(userServiceMock.validate(testUser, true))
		.thenReturn(ImmutableList.of(
				new ValidationError("email", "Please enter a valid email address")));
		
		RequestBuilder request = Helpers.fakeRequest()
									.method("POST")
									.bodyJson(testUserJson)
									.uri(controllers.admin.routes.UserController.editUser().url());		
		
		Result result = route(request);
	    assertEquals(BAD_REQUEST, result.status());
	    	    
	    JsonNode responseNode = Json.parse(contentAsString(result));
	    
	    assertTrue(responseNode.isArray());
	    ArrayNode responseArrayNode = (ArrayNode) responseNode;
	    	    
	    assertEquals(responseArrayNode.get(0).get("field").asText(), "email");	
	    assertEquals(responseArrayNode.get(0).get("error").asText(), "Please enter a valid email address");
	}
	
	@Test
	public void testEditUser_Failure_UserNotFound() {
		       
		JsonNode testUserJson = Json.toJson(testUser);
		
		when(userServiceMock.getById(testUser.getId())).thenReturn(null);
		
		RequestBuilder request = Helpers.fakeRequest()
									.method("POST")
									.bodyJson(testUserJson)
									.uri(controllers.admin.routes.UserController.editUser().url());		
		
		Result result = route(request);
	    assertEquals(BAD_REQUEST, result.status());	    
	    
	    JsonNode responseNode = Json.parse(contentAsString(result));	    
	    
	    assertEquals(responseNode.get("status").asText(), "ERROR");	
	    assertEquals(responseNode.get("message").asText(), "User with id " + testUser.getId() + " is not found");
	}
	
	@Test
	public void testAddUser_Success() {
	
		User newUser = new UserBuilder().email("new@user.email").id(777).build();		
		JsonNode newUserJson = Json.toJson(newUser);
		
		when(userServiceMock.validate(newUser, false)).thenReturn(null);
				
		RequestBuilder request = Helpers.fakeRequest()
									.method("POST")
									.bodyJson(newUserJson)
									.uri(controllers.admin.routes.UserController.addUser().url());
		
	    Result result = route(request);
	    assertEquals(OK, result.status());
	    
	    JsonNode responseNode = Json.parse(contentAsString(result));
	    
	    assertEquals(responseNode.get("status").asText(), "SUCCESS");	
	    assertEquals(responseNode.get("message").asText(), "User was created successfully");
	    assertEquals(responseNode.get("id").asLong(), 777);
	}
	
	@Test
	public void testAddUser_Failure_ValidationErrors() {
	
		User newUser = new UserBuilder().id(22).email("new@user.email").build();		
		JsonNode newUserJson = Json.toJson(newUser);
		
		when(userServiceMock.validate(newUser, false))
							.thenReturn(ImmutableList.of(
									new ValidationError("email", "Please enter a valid email address"),
									new ValidationError("zip", "Zip code should contain 5 digits")));
				
		RequestBuilder request = Helpers.fakeRequest()
									.method("POST")
									.bodyJson(newUserJson)
									.uri(controllers.admin.routes.UserController.addUser().url());
		
	    Result result = route(request);
	    assertEquals(BAD_REQUEST, result.status());
	    
	    JsonNode responseNode = Json.parse(contentAsString(result));
	    
	    assertTrue(responseNode.isArray());
	    ArrayNode responseArrayNode = (ArrayNode) responseNode;
	    
	    assertEquals(responseArrayNode.get(0).get("field").asText(), "email");	
	    assertEquals(responseArrayNode.get(0).get("error").asText(), "Please enter a valid email address");
	    
	    assertEquals(responseArrayNode.get(1).get("field").asText(), "zip");	
	    assertEquals(responseArrayNode.get(1).get("error").asText(), "Zip code should contain 5 digits");
	}
	
	@Test
	public void testAddUser_Failure_ParsingJson() {
	
		JsonNode newUserJson = Json.toJson("{ \"someOther\": \"json\" }");
						
		RequestBuilder request = Helpers.fakeRequest()
									.method("POST")
									.bodyJson(newUserJson)
									.uri(controllers.admin.routes.UserController.addUser().url());
		
	    Result result = route(request);
	    assertEquals(BAD_REQUEST, result.status());
	    	    
	    JsonNode responseNode = Json.parse(contentAsString(result));	    
	    
	    assertEquals(responseNode.get("status").asText(), "ERROR");	
	    assertEquals(responseNode.get("message").asText(), "Cannot parse JSON to user");
	    
	}

	@Test 
	public void testDeleteUser_Success() {
		when(userServiceMock.delete(Mockito.any())).thenReturn(true);
		
		RequestBuilder request = Helpers.fakeRequest()
				.method("POST")
				.bodyForm(ImmutableMap.of("id", String.valueOf(testUser.getId())))
				.uri(controllers.admin.routes.UserController.deleteUser().url());
		
		Result result = route(request);
	    assertEquals(OK, result.status());
	}
	
	@Test 
	public void testDeleteUser_Failure_UserNotFound() {
		long id = 456;
		when(userServiceMock.getById(id)).thenReturn(null);
		
		RequestBuilder request = Helpers.fakeRequest()
				.method("POST")
				.bodyForm(ImmutableMap.of("id", String.valueOf(id)))
				.uri(controllers.admin.routes.UserController.deleteUser().url());
		
		Result result = route(request);
	    assertEquals(BAD_REQUEST, result.status());
	    
	    JsonNode responseNode = Json.parse(contentAsString(result));
	    
	    assertEquals(responseNode.get("status").asText(), "ERROR");	
	    assertEquals(responseNode.get("message").asText(), "User with id " + id + " is not found");
	}
	
	@Test 
	public void testDeleteUser_Failure() {
		when(userServiceMock.delete(Mockito.any())).thenReturn(false);
		
		RequestBuilder request = Helpers.fakeRequest()
				.method("POST")
				.bodyForm(ImmutableMap.of("id", String.valueOf(testUser.getId())))
				.uri(controllers.admin.routes.UserController.deleteUser().url());
		
		Result result = route(request);
	    assertEquals(BAD_REQUEST, result.status());
	    
	    JsonNode responseNode = Json.parse(contentAsString(result));
	    
	    assertEquals(responseNode.get("status").asText(), "ERROR");	
	    assertEquals(responseNode.get("message").asText(), "User was not deleted");
	}		
	
}
