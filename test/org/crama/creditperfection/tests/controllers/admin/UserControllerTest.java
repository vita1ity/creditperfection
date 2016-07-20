package org.crama.creditperfection.tests.controllers.admin;


import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static play.mvc.Http.Status.OK;
import static play.test.Helpers.contentAsString;
import static play.test.Helpers.route;
import static play.test.Helpers.start;
import static play.test.Helpers.stop;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.crama.creditperfection.tests.builders.UserBuilder;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import controllers.admin.UserController;
import models.SecurityRole;
import models.User;
import play.Application;
import play.Logger;
import play.Mode;
import play.http.HttpEntity;
import play.inject.guice.GuiceApplicationBuilder;
import play.mvc.Http;
import play.mvc.Http.RequestBuilder;
import play.mvc.Http.Session;
import play.mvc.Result;
import services.RoleService;
import services.UserService;

public class UserControllerTest {

	private static final String TEST_EMAIL = "test@gmail.com";
	
	@Mock
	private static Http.Request request;
	
	@Mock
	private UserService userServiceMock;
	
	@Mock
	private RoleService roleServiceMock;
	
	@Inject
	@InjectMocks
	private UserController userController;
	
	private User adminUser;
	
	private static Application fakeApplication;

    @BeforeClass
    public static void startApp() throws Exception {
    	/*fakeApplication = new GuiceApplicationBuilder()
    		    .in(Mode.TEST)
    		    .build();
    	start(fakeApplication);*/
    	
    
    }
	
	@Before
	public void setUpTestUsers() throws Exception {
		
		MockitoAnnotations.initMocks(this);
		 
	    Map<String, String> flashData = Collections.emptyMap();
	    Map<String, Object> argData = Collections.emptyMap();
	    Long id = 2L;
	    play.api.mvc.RequestHeader header = mock(play.api.mvc.RequestHeader.class);
	    Http.Context context = new Http.Context(id, header, request, flashData, flashData, argData);
	    Session session = context.session();
	    session.put("email", TEST_EMAIL);
	    Http.Context.current.set(context);
		
	}
	
	@Test
	public void testUsers() {
		
		List<SecurityRole> roles = Arrays.asList(new SecurityRole(1, "user"), 
												 new SecurityRole(2, "admin"));
		User adminUser = new UserBuilder().email(TEST_EMAIL).roles(roles).build();
		
		List<User> allUsers = Arrays.asList(
				new UserBuilder().firstName("John")
	                             .email("jogn@gmail.com")
	                             .build(),
	            new UserBuilder().firstName("Jeremy")
	                             .email("jeremy@gmail.com")
	                             .build());                		 
		
		assertNotNull(userServiceMock);
		when(userServiceMock.findByEmail(TEST_EMAIL)).thenReturn(adminUser);
		when(userServiceMock.getAll()).thenReturn(allUsers);
		
		assertNotNull(userController);
		
		Result result = userController.users();
		
        assertTrue(contentAsString(result).contains("Add User"));
        assertTrue(contentAsString(result).contains("John"));
        assertTrue(contentAsString(result).contains("Jeremy"));
        assertTrue(result.contentType().get().equals("text/html"));
		assertTrue(result.status() == OK);
		assertTrue(result.charset().get().equals("utf-8"));
		
		
		verify(userServiceMock, times(1)).findByEmail(TEST_EMAIL);
		verify(userServiceMock, times(1)).getAll();
		
	}
	
	/*@Before
	public void setUpAddUsersSuccess() throws Exception {
		
		MockitoAnnotations.initMocks(this);
		 
	    Map<String, String> flashData = Collections.emptyMap();
	    Map<String, Object> argData = Collections.emptyMap();
	    Long id = 2L;
	    play.api.mvc.RequestHeader header = mock(play.api.mvc.RequestHeader.class);
	    Http.Context context = new Http.Context(id, header, request, flashData, flashData, argData);
	    Session session = context.session();
	    session.put("email", TEST_EMAIL);
	    Http.Context.current.set(context);
		
	}*/
	
	//TODO not correct behavior
	/*@Test
	public void addUser_Success() throws JsonProcessingException, IOException {
		
		List<SecurityRole> roles = Arrays.asList(new SecurityRole(1, "user"), 
				 new SecurityRole(2, "admin"));
		User adminUser = new UserBuilder().email(TEST_EMAIL).roles(roles).build();
		when(userServiceMock.findByEmail(TEST_EMAIL)).thenReturn(adminUser);
		
		JsonNode userNode = (new ObjectMapper()).readTree("{ \"firstName\": \"Luke\", "
				+ "\"lastName\": \"Skywalker\", \"email\": \"luke.skywalker@gmail.com\", "
				+ "\"address\": \"Matama Ancienne\", \"city\": \"Matmata\", "
				+ "\"state\": \"TX\", \"zip\": \"60700\", \"password\": \"theforce\" }");
		
	    RequestBuilder request = new RequestBuilder().method("POST")
	            .bodyJson(userNode)
	            
	            .uri(controllers.admin.routes.UserController.addUser().url());
	    Result result = route(request);

	    assertTrue(result.status() == OK);
	    
	    //verify(userServiceMock, times(1)).findByEmail(TEST_EMAIL);
	    
	    Logger.info(contentAsString(result));
	}*/
	

	@AfterClass
	public static void stopApp() throws Exception {
	    /*stop(fakeApplication);*/
	}
	
}
