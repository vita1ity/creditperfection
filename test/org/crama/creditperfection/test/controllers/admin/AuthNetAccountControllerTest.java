package org.crama.creditperfection.test.controllers.admin;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;
import static play.inject.Bindings.bind;
import static play.mvc.Http.Status.BAD_REQUEST;
import static play.mvc.Http.Status.OK;
import static play.test.Helpers.contentAsString;
import static play.test.Helpers.route;

import java.util.Arrays;
import java.util.List;

import org.crama.creditperfection.test.base.ControllerTestBase;
import org.crama.creditperfection.test.builders.AuthNetAccountBuilder;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.google.common.collect.ImmutableMap;

import controllers.admin.AuthNetAccountController;
import errors.ValidationError;
import models.AuthNetAccount;
import play.Logger;
import play.inject.guice.GuiceApplicationBuilder;
import play.libs.Json;
import play.mvc.Result;
import play.mvc.Http.RequestBuilder;
import play.test.Helpers;
import services.AuthNetAccountService;

public class AuthNetAccountControllerTest extends ControllerTestBase {
	private static AuthNetAccount testAuthNetAccount;
		
	@Mock
	private AuthNetAccountService authNetAccountServiceMock;
	
	@InjectMocks
	private AuthNetAccountController authNetAccountController;
	
	private void setUp() {
		MockitoAnnotations.initMocks(this);			
		
		testAuthNetAccount = new AuthNetAccountBuilder().id(55).build();
				
		List<AuthNetAccount> allAuthNetAccounts = Arrays.asList(
			new AuthNetAccountBuilder()
				.id(11)
				.name("testAuthNetAccount1")
				.build(),
			new AuthNetAccountBuilder()
				.id(12)
				.name("testAuthNetAccount2")
				.build(),
				testAuthNetAccount); 
		
		when(authNetAccountServiceMock.getAll()).thenReturn(allAuthNetAccounts);
		when(authNetAccountServiceMock.getById(testAuthNetAccount.getId())).thenReturn(testAuthNetAccount);				
	}
	
	@Override
    public GuiceApplicationBuilder getBuilder() {
        GuiceApplicationBuilder builder = super.getBuilder();
        setUp();
        return builder.overrides(bind(AuthNetAccountService.class).toInstance(authNetAccountServiceMock));        						         						 
    }
	
	@Test
	public void testAuthNetAccounts()	{
		Result result = authNetAccountController.authNetAccounts();
		assertEquals(OK, result.status());
		assertEquals("utf-8", result.charset().get());
		assertEquals("text/html", result.contentType().get());
		
		assertTrue(contentAsString(result).contains("Add Merchant Account"));
		assertTrue(contentAsString(result).contains(testAuthNetAccount.getName()));
		assertTrue(contentAsString(result).contains(authNetAccountServiceMock.getAll().get(0).getName()));
		assertTrue(contentAsString(result).contains(authNetAccountServiceMock.getAll().get(1).getName()));
	}
	
	@Test
	public void testAddAuthNetAccounts_Success() {
		JsonNode authNetAccountJson = Json.toJson(testAuthNetAccount);
		when(authNetAccountServiceMock.validate(testAuthNetAccount)).thenReturn(null);
		
		RequestBuilder request = Helpers.fakeRequest()
									.method("POST")
									.bodyJson(authNetAccountJson)
									.uri(controllers.admin.routes.AuthNetAccountController.addAuthNetAccount().url());
		
	    Result result = route(request);
	    assertEquals(OK, result.status());
	    
	    JsonNode responseNode = Json.parse(contentAsString(result));
	    
	    assertEquals(responseNode.get("status").asText(), "SUCCESS");	
	    assertEquals(responseNode.get("message").asText(), "Merchant Account was added successfully");
	}
	
	@Test
	public void testAddAuthNetAccount_Failure_ValidationErrors() {
		
		when(authNetAccountServiceMock.validate(testAuthNetAccount)).thenReturn(
				Arrays.asList(new ValidationError("loginId", "Please enter Login ID")));
		JsonNode authNetAccountJson = Json.toJson(testAuthNetAccount);
										
		RequestBuilder request = Helpers.fakeRequest()
									.method("POST")
									.bodyJson(authNetAccountJson)
									.uri(controllers.admin.routes.AuthNetAccountController.addAuthNetAccount().url());
		
	    Result result = route(request);
	    assertEquals(BAD_REQUEST, result.status());
	    
	    JsonNode responseNode = Json.parse(contentAsString(result));
	    

	    assertTrue(responseNode.isArray());
	    ArrayNode responseArrayNode = (ArrayNode) responseNode;
	    
	    assertEquals(responseArrayNode.get(0).get("field").asText(), "loginId");	
	    assertEquals(responseArrayNode.get(0).get("error").asText(), "Please enter Login ID");
	}
	
	@Test
	public void testAddAuthNetAccount_Failure_ParsingJson() {
		       
		JsonNode testAuthNetAccountJson = Json.toJson("{ \"someOther\": \"json\" }");
		
		RequestBuilder request = Helpers.fakeRequest()
									.method("POST")
									.bodyJson(testAuthNetAccountJson)
									.uri(controllers.admin.routes.AuthNetAccountController.addAuthNetAccount().url());		
		
		Result result = route(request);
	    assertEquals(BAD_REQUEST, result.status());
	    	    
	    JsonNode responseNode = Json.parse(contentAsString(result));	    
	    
	    assertEquals(responseNode.get("status").asText(), "ERROR");	
	    assertEquals(responseNode.get("message").asText(), "Cannot parse JSON to Merchant Account");
	}
	
	@Test
	public void testEditAuthNetAccounts_Success() {
		JsonNode authNetAccountJson = Json.toJson(testAuthNetAccount);
		when(authNetAccountServiceMock.validate(testAuthNetAccount)).thenReturn(null);
		
		RequestBuilder request = Helpers.fakeRequest()
									.method("POST")
									.bodyJson(authNetAccountJson)
									.uri(controllers.admin.routes.AuthNetAccountController.editAuthNetAccount().url());
		
	    Result result = route(request);
	    assertEquals(OK, result.status());
	    
	    JsonNode responseNode = Json.parse(contentAsString(result));
	    
	    assertEquals(responseNode.get("status").asText(), "SUCCESS");	
	    assertEquals(responseNode.get("message").asText(), "Merchant Account was edited successfully");
	}
	
	@Test
	public void testEditAuthNetAccount_Failure_ValidationErrors() {
		
		when(authNetAccountServiceMock.validate(testAuthNetAccount)).thenReturn(
				Arrays.asList(new ValidationError("loginId", "Please enter Login ID")));
		JsonNode authNetAccountJson = Json.toJson(testAuthNetAccount);
		
		RequestBuilder request = Helpers.fakeRequest()
									.method("POST")
									.bodyJson(authNetAccountJson)
									.uri(controllers.admin.routes.AuthNetAccountController.editAuthNetAccount().url());
		
	    Result result = route(request);
	    assertEquals(BAD_REQUEST, result.status());
	    
	    JsonNode responseNode = Json.parse(contentAsString(result));
	    
	    assertTrue(responseNode.isArray());
	    ArrayNode responseArrayNode = (ArrayNode) responseNode;
	    
	    Logger.error(contentAsString(result));
	    
	    assertEquals(responseArrayNode.get(0).get("field").asText(), "loginId");	
	    assertEquals(responseArrayNode.get(0).get("error").asText(), "Please enter Login ID");
	}
	
	@Test
	public void testEditAuthNetAccount_Failure_ParsingJson() {
		       
		JsonNode testAuthNetAccountJson = Json.toJson("{ \"someOther\": \"json\" }");
		
		RequestBuilder request = Helpers.fakeRequest()
									.method("POST")
									.bodyJson(testAuthNetAccountJson)
									.uri(controllers.admin.routes.AuthNetAccountController.editAuthNetAccount().url());		
		
		Result result = route(request);
	    assertEquals(BAD_REQUEST, result.status());
	    	    
	    JsonNode responseNode = Json.parse(contentAsString(result));	    
	    
	    assertEquals(responseNode.get("status").asText(), "ERROR");	
	    assertEquals(responseNode.get("message").asText(), "Cannot parse JSON to Merchant Account");
	}
	
	@Test
	public void testEditAuthNetAccount_Failure_AuthNetAccountNotFound() {
		       
		JsonNode testAuthNetAccountJson = Json.toJson(testAuthNetAccount);
		
		when(authNetAccountServiceMock.validate(testAuthNetAccount)).thenReturn(null);
		when(authNetAccountServiceMock.getById(testAuthNetAccount.getId())).thenReturn(null);
		
		RequestBuilder request = Helpers.fakeRequest()
									.method("POST")
									.bodyJson(testAuthNetAccountJson)
									.uri(controllers.admin.routes.AuthNetAccountController.editAuthNetAccount().url());		
		
		Result result = route(request);
	    assertEquals(BAD_REQUEST, result.status());
	    	    
	    JsonNode responseNode = Json.parse(contentAsString(result));	
	    	    
	    assertEquals(responseNode.get("status").asText(), "ERROR");	
	    assertEquals(responseNode.get("message").asText(), "Merchant Account with id 55 is not found");
	}
	
	@Test 
	public void testDeleteAuthNetAccount_Success() {
		when(authNetAccountServiceMock.delete(Mockito.any())).thenReturn(true);
		
		RequestBuilder request = Helpers.fakeRequest()
				.method("POST")
				.bodyForm(ImmutableMap.of("id", String.valueOf(testAuthNetAccount.getId())))
				.uri(controllers.admin.routes.AuthNetAccountController.deleteAuthNetAccount().url());
		
		Result result = route(request);
	    assertEquals(OK, result.status());
	    
	    JsonNode responseNode = Json.parse(contentAsString(result));
	    
	    assertEquals(responseNode.get("status").asText(), "SUCCESS");	
	    assertEquals(responseNode.get("message").asText(), "Merchant Account was deleted successfully");
	}
	
	@Test 
	public void testDeleteAuthNetAccount_Failure_AuthNetAccountNotFound() {
		long id = 456;
		when(authNetAccountServiceMock.getById(id)).thenReturn(null);
		
		RequestBuilder request = Helpers.fakeRequest()
				.method("POST")
				.bodyForm(ImmutableMap.of("id", String.valueOf(id)))
				.uri(controllers.admin.routes.AuthNetAccountController.deleteAuthNetAccount().url());
		
		Result result = route(request);
	    assertEquals(BAD_REQUEST, result.status());
	    
	    JsonNode responseNode = Json.parse(contentAsString(result));
	    
	    assertEquals(responseNode.get("status").asText(), "ERROR");	
	    assertEquals(responseNode.get("message").asText(), "Merchant Account with id " + id + " is not found");
	}
	
	@Test 
	public void testDeleteAuthNetAccount_Failure() {
		when(authNetAccountServiceMock.delete(Mockito.any())).thenReturn(false);
		
		RequestBuilder request = Helpers.fakeRequest()
				.method("POST")
				.bodyForm(ImmutableMap.of("id", String.valueOf(testAuthNetAccount.getId())))
				.uri(controllers.admin.routes.AuthNetAccountController.deleteAuthNetAccount().url());
		
		Result result = route(request);
	    assertEquals(BAD_REQUEST, result.status());
	    
	    JsonNode responseNode = Json.parse(contentAsString(result));
	    
	    assertEquals(responseNode.get("status").asText(), "ERROR");	
	    assertEquals(responseNode.get("message").asText(), "Error occured while deleting the Merchant Account");
	}
	
	
}
