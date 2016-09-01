package org.crama.creditperfection.test.controllers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;
import static play.inject.Bindings.bind;
import static play.mvc.Http.Status.OK;
import static play.mvc.Http.Status.BAD_REQUEST;
import static play.mvc.Http.Status.SEE_OTHER;
import static play.test.Helpers.contentAsString;
import static play.test.Helpers.route;

import java.util.Arrays;
import java.util.List;

import org.crama.creditperfection.test.base.ControllerTestBase;
import org.crama.creditperfection.test.builders.ProductBuilder;
import org.crama.creditperfection.test.builders.SubscriptionBuilder;
import org.crama.creditperfection.test.builders.SubscriptionFormBuilder;
import org.crama.creditperfection.test.builders.UserBuilder;
import org.junit.Test;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.collect.ImmutableMap;

import controllers.SubscriptionController;
import forms.SubscriptionForm;
import models.Product;
import models.SecurityRole;
import models.Subscription;
import models.User;
import models.enums.SubscriptionStatus;
import play.Logger;
import play.data.FormFactory;
import play.inject.guice.GuiceApplicationBuilder;
import play.libs.Json;
import play.mvc.Http.RequestBuilder;
import play.mvc.Result;
import play.test.Helpers;
import services.MailService;
import services.ProductService;
import services.RoleService;
import services.SubscriptionService;
import services.UserService;

public class SubscriptionControllerTest extends ControllerTestBase {
	
	private static User testUser;
	private static User testUserAdmin;
	
	private static Product testProduct;
	
	private static Subscription testSubscription;
	private static SubscriptionForm testSubscriptionForm;
	    
	@Mock(answer = Answers.RETURNS_DEEP_STUBS)
	public FormFactory formFactoryMock;
	
	@Mock
    private UserService userServiceMock;

	@Mock
    private RoleService roleServiceMock;
	
	@Mock
    private MailService mailServiceMock;
	
	@Mock
	private ProductService productServiceMock;
	
	@Mock
	private SubscriptionService subscriptionServiceMock;
	
	@InjectMocks
	private SubscriptionController subscriptionController;
	
	private void setUp() {
		MockitoAnnotations.initMocks(this);
		
		List<SecurityRole> roles = Arrays.asList(new SecurityRole(1, "user"), 
				 new SecurityRole(2, "admin"));
		
		testUserAdmin = new UserBuilder().id(1).roles(roles).build();
		testUser = new UserBuilder().id(2).roles(Arrays.asList(roles.get(0))).build();
		
		testSubscription = new SubscriptionBuilder().id(55).status(SubscriptionStatus.ACTIVE).build();
		testSubscriptionForm = new SubscriptionFormBuilder().build();
		
		testProduct = new ProductBuilder().id(1l).build();
		
		testUserAdmin.setSubscription(testSubscription);
				
	}
	
	@Override
    public GuiceApplicationBuilder getBuilder() {
        GuiceApplicationBuilder builder = super.getBuilder();
        setUp();
        return builder.overrides(bind(FormFactory.class).toInstance(formFactoryMock),
        						 bind(UserService.class).toInstance(userServiceMock),
        						 bind(MailService.class).toInstance(mailServiceMock),
        						 bind(ProductService.class).toInstance(productServiceMock),
        						 bind(SubscriptionService.class).toInstance(subscriptionServiceMock),
        						 bind(RoleService.class).toInstance(roleServiceMock));
        						 					 
    }
	
	@Test
	public void upgradeSubscriptionPage() {
		when(userServiceMock.findByEmail(Matchers.anyString())).thenReturn(testUserAdmin);		
		
		Result result = subscriptionController.upgradeSubscriptionPage();
		assertEquals(OK, result.status());
		assertEquals("utf-8", result.charset().get());
		assertEquals("text/html", result.contentType().get());
		
		assertTrue(contentAsString(result).contains("Upgrade Subscription"));		
	}
	
	@Test
	public void upgradeSubscription_Success() {
		when(userServiceMock.findByEmail(Matchers.anyString())).thenReturn(testUserAdmin);
		when(productServiceMock.getById(Matchers.anyLong())).thenReturn(testProduct);
		
		when(formFactoryMock.form().bindFromRequest().get("product")).thenReturn(String.valueOf(testProduct.getId()));
		
		RequestBuilder request = Helpers.fakeRequest().session("email", testUserAdmin.getEmail())
				.method("POST")
				.uri(controllers.routes.SubscriptionController.upgradeSubscription().url());
		
		Result result = route(request);
	    assertEquals(OK, result.status());
	    
	    JsonNode responseNode = Json.parse(contentAsString(result));
	    
	    assertEquals(responseNode.get("status").asText(), "SUCCESS");	
	    assertEquals(responseNode.get("message").asText(), "Subscription updated successfully");
		
	}
	
	@Test
	public void upgradeSubscription_AuthorzationError() {
				
		RequestBuilder request = Helpers.fakeRequest()
				.method("POST")
				.uri(controllers.routes.SubscriptionController.upgradeSubscription().url());
		
		Result result = route(request);
	    assertEquals(SEE_OTHER, result.status());
	}
	
	@Test
	public void upgradeSubscription_Failure_UserNotFound() {
		when(userServiceMock.findByEmail(Matchers.anyString())).thenReturn(null);
		
		RequestBuilder request = Helpers.fakeRequest().session("email", testUserAdmin.getEmail())
				.method("POST")
				.uri(controllers.routes.SubscriptionController.upgradeSubscription().url());
		
		Result result = route(request);
	    assertEquals(BAD_REQUEST, result.status());
				
	    JsonNode responseNode = Json.parse(contentAsString(result));
	    
	    assertEquals(responseNode.get("status").asText(), "ERROR");	
	    assertEquals(responseNode.get("errorMessage").asText(), "User not found");	
	}
	
	@Test
	public void upgradeSubscription_Failure_ProductNotFound() {
		when(userServiceMock.findByEmail(Matchers.anyString())).thenReturn(testUserAdmin);
		when(formFactoryMock.form().bindFromRequest().get("product")).thenReturn(String.valueOf(testProduct.getId()));
		when(productServiceMock.getById(Matchers.anyLong())).thenReturn(null);
		
		RequestBuilder request = Helpers.fakeRequest().session("email", testUserAdmin.getEmail())
				.method("POST")
				.uri(controllers.routes.SubscriptionController.upgradeSubscription().url());
		
		Result result = route(request);
	    assertEquals(BAD_REQUEST, result.status());
				
	    JsonNode responseNode = Json.parse(contentAsString(result));
	    
	    assertEquals(responseNode.get("status").asText(), "ERROR");	
	    assertEquals(responseNode.get("errorMessage").asText(), "Product not found");	
	}
	
	@Test
	public void cancelSubscription_Success() {
		when(userServiceMock.findByEmail(Matchers.anyString())).thenReturn(testUserAdmin);
		when(productServiceMock.getById(Matchers.anyLong())).thenReturn(testProduct);
				
		RequestBuilder request = Helpers.fakeRequest().session("email", testUserAdmin.getEmail())
				.method("POST")
				.uri(controllers.routes.SubscriptionController.cancelSubscription().url());

		Result result = route(request);
		assertEquals(OK, result.status());
		
		JsonNode responseNode = Json.parse(contentAsString(result));
	    
	    assertEquals(responseNode.get("status").asText(), "SUCCESS");	
	    assertEquals(responseNode.get("message").asText(), "Cancellation request has been submitted");
	}
	
	@Test
	public void cancelSubscription_Success_SubscriptionPending() {
		testUserAdmin.getSubscription().setStatus(SubscriptionStatus.PENDING);
		when(userServiceMock.findByEmail(Matchers.anyString())).thenReturn(testUserAdmin);
		when(productServiceMock.getById(Matchers.anyLong())).thenReturn(testProduct);
				
		RequestBuilder request = Helpers.fakeRequest().session("email", testUserAdmin.getEmail())
				.method("POST")
				.uri(controllers.routes.SubscriptionController.cancelSubscription().url());

		Result result = route(request);
		assertEquals(OK, result.status());
		
		JsonNode responseNode = Json.parse(contentAsString(result));
	    
	    assertEquals(responseNode.get("status").asText(), "SUCCESS");	
	    assertEquals(responseNode.get("message").asText(), "Your subscription is in status Pending. "
				+ "It will be cancelled shortly");
	}
	
	@Test
	public void cancelSubscription_AuthorzationError() {
		
		RequestBuilder request = Helpers.fakeRequest()
				.method("POST")
				.uri(controllers.routes.SubscriptionController.cancelSubscription().url());

		Result result = route(request);
		assertEquals(SEE_OTHER, result.status());
	}
	
	@Test
	public void cancelSubscription_Failure_SubscriptionNotFound() {
		when(userServiceMock.findByEmail(Matchers.anyString())).thenReturn(testUser);
						
		RequestBuilder request = Helpers.fakeRequest().session("email", testUser.getEmail())
				.method("POST")
				.uri(controllers.routes.SubscriptionController.cancelSubscription().url());

		Result result = route(request);
		assertEquals(BAD_REQUEST, result.status());
		
		JsonNode responseNode = Json.parse(contentAsString(result));
	    
	    assertEquals(responseNode.get("status").asText(), "ERROR");	
	    assertEquals(responseNode.get("errorMessage").asText(), "You don't have any subscription");
	}
	
	@Test
	public void cancelSubscription_Failure_SubscriptionCanceled() {
		testUserAdmin.getSubscription().setStatus(SubscriptionStatus.CANCELLED);
		when(userServiceMock.findByEmail(Matchers.anyString())).thenReturn(testUserAdmin);
		when(productServiceMock.getById(Matchers.anyLong())).thenReturn(testProduct);
						
		RequestBuilder request = Helpers.fakeRequest().session("email", testUser.getEmail())
				.method("POST")
				.uri(controllers.routes.SubscriptionController.cancelSubscription().url());

		Result result = route(request);
		assertEquals(BAD_REQUEST, result.status());
		
		JsonNode responseNode = Json.parse(contentAsString(result));
	    
	    assertEquals(responseNode.get("status").asText(), "ERROR");	
	    assertEquals(responseNode.get("errorMessage").asText(), "Your subscription is already cancelled");
	}
}
