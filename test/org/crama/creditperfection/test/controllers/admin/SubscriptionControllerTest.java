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
import org.crama.creditperfection.test.builders.SubscriptionBuilder;
import org.crama.creditperfection.test.builders.SubscriptionFormBuilder;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import controllers.admin.SubscriptionController;
import forms.SubscriptionForm;
import models.Subscription;
import models.enums.SubscriptionStatus;
import play.inject.guice.GuiceApplicationBuilder;
import play.libs.Json;
import play.mvc.Http.RequestBuilder;
import play.mvc.Result;
import play.test.Helpers;
import services.ProductService;
import services.SubscriptionService;
import services.UserService;

public class SubscriptionControllerTest extends ControllerTestBase {
	private static Subscription testSubscription;
	private static SubscriptionForm testSubscriptionForm;
	
	@Mock
	private UserService userServiceMock;
	
	@Mock
	private ProductService productServiceMock;
	
	@Mock
	private SubscriptionService subscriptionServiceMock;
	
	@InjectMocks
	private SubscriptionController subscriptionController;
	
	private void setUp() {
		MockitoAnnotations.initMocks(this);
		
		testSubscription = new SubscriptionBuilder().id(55).status(SubscriptionStatus.ACTIVE).build();
		testSubscriptionForm = new SubscriptionFormBuilder().build();
		
		List<Subscription> allSubscriptions = Arrays.asList(
			new SubscriptionBuilder()
				.id(11)
				.status(SubscriptionStatus.TRIAL)
				.build(),
			new SubscriptionBuilder()
				.id(12)
				.status(SubscriptionStatus.TRIAL)
				.build(),
				testSubscription); 
		
		when(subscriptionServiceMock.findAll()).thenReturn(allSubscriptions);
		when(subscriptionServiceMock.findByStatus(SubscriptionStatus.TRIAL, 1, 10).getList()).thenReturn(
					ImmutableList.of(allSubscriptions.get(0), allSubscriptions.get(1)));
		when(subscriptionServiceMock.findById(testSubscription.getId())).thenReturn(testSubscription);
		when(subscriptionServiceMock.createSubscription(testSubscriptionForm)).thenReturn(testSubscription);
	}
	
	@Override
    public GuiceApplicationBuilder getBuilder() {
        GuiceApplicationBuilder builder = super.getBuilder();
        setUp();
        return builder.overrides(bind(UserService.class).toInstance(userServiceMock),
			        			 bind(ProductService.class).toInstance(productServiceMock),
			        			 bind(SubscriptionService.class).toInstance(subscriptionServiceMock));
        						         						 
    }
	
	@Test
	public void testSubscriptions() {
		Result result = subscriptionController.subscriptions();
		assertEquals(OK, result.status());
		assertEquals("utf-8", result.charset().get());
		assertEquals("text/html", result.contentType().get());
		
		assertTrue(contentAsString(result).contains("Add Subscription"));
		assertTrue(contentAsString(result).contains("Subscription Filter:"));
		
	}
	
	@Test
	public void testAddSubscription_Success() {
			
		JsonNode subscriptionFormJson = Json.toJson(testSubscriptionForm);
						
		RequestBuilder request = Helpers.fakeRequest()
									.method("POST")
									.bodyJson(subscriptionFormJson)
									.uri(controllers.admin.routes.SubscriptionController.addSubscription().url());
		
	    Result result = route(request);
	    assertEquals(OK, result.status());
	    
	    JsonNode responseNode = Json.parse(contentAsString(result));
	    
	    assertEquals(responseNode.get("status").asText(), "SUCCESS");	
	    assertEquals(responseNode.get("message").asText(), "Subscription was added successfully");
	    
	}
	
	@Test
	public void testAddSubscription_Failure_ValidationErrors() {
			
		testSubscriptionForm.setCardId(null);
		JsonNode subscriptionFormJson = Json.toJson(testSubscriptionForm);
										
		RequestBuilder request = Helpers.fakeRequest()
									.method("POST")
									.bodyJson(subscriptionFormJson)
									.uri(controllers.admin.routes.SubscriptionController.addSubscription().url());
		
	    Result result = route(request);
	    assertEquals(BAD_REQUEST, result.status());
	    
	    JsonNode responseNode = Json.parse(contentAsString(result));
	    
	    assertTrue(responseNode.isArray());
	    ArrayNode responseArrayNode = (ArrayNode) responseNode;
	    
	    assertEquals(responseArrayNode.get(0).get("field").asText(), "creditCard");	
	    assertEquals(responseArrayNode.get(0).get("error").asText(), "Please choose Credit Card");
	    
	}
	
	@Test
	public void testAddSubscription_Failure_ParsingError() {
			
		JsonNode subscriptionFormJson = Json.toJson("{ \"someOther\": \"json\" }");
										
		RequestBuilder request = Helpers.fakeRequest()
									.method("POST")
									.bodyJson(subscriptionFormJson)
									.uri(controllers.admin.routes.SubscriptionController.addSubscription().url());
		
	    Result result = route(request);
	    assertEquals(BAD_REQUEST, result.status());
	    
	    JsonNode responseNode = Json.parse(contentAsString(result));
	    
	    assertEquals(responseNode.get("status").asText(), "ERROR");	
	    assertEquals(responseNode.get("message").asText(), "Cannot parse JSON to Subscription form");
	    
	}
	
	@Test
	public void testEditSubscription_Success() {
		testSubscriptionForm.setUserId("20");
		JsonNode subscriptionFormJson = Json.toJson(testSubscriptionForm);
		
		RequestBuilder request = Helpers.fakeRequest()
									.method("POST")
									.bodyJson(subscriptionFormJson)
									.uri(controllers.admin.routes.SubscriptionController.editSubscription().url());
		
	    Result result = route(request);
	    assertEquals(OK, result.status());
	    
	    JsonNode responseNode = Json.parse(contentAsString(result));
	    
	    assertEquals(responseNode.get("status").asText(), "SUCCESS");	
	    assertEquals(responseNode.get("message").asText(), "Subscription was edited successfully");
	}
	
	@Test
	public void testEditSubscription_Failure_ValidationErrors() {
		testSubscriptionForm.setUserId(null);
		JsonNode subscriptionFormJson = Json.toJson(testSubscriptionForm);
		
		RequestBuilder request = Helpers.fakeRequest()
									.method("POST")
									.bodyJson(subscriptionFormJson)
									.uri(controllers.admin.routes.SubscriptionController.editSubscription().url());
		
	    Result result = route(request);
	    assertEquals(BAD_REQUEST, result.status());
	    
	    JsonNode responseNode = Json.parse(contentAsString(result));
	    
	    assertTrue(responseNode.isArray());
	    ArrayNode responseArrayNode = (ArrayNode) responseNode;
	    
	    assertEquals(responseArrayNode.get(0).get("field").asText(), "user");	
	    assertEquals(responseArrayNode.get(0).get("error").asText(), "Please choose User");
	}
	
	@Test
	public void testEditSubscription_Failure_ParsingError() {
			
		JsonNode subscriptionFormJson = Json.toJson("{ \"someOther\": \"json\" }");
										
		RequestBuilder request = Helpers.fakeRequest()
									.method("POST")
									.bodyJson(subscriptionFormJson)
									.uri(controllers.admin.routes.SubscriptionController.editSubscription().url());
		
	    Result result = route(request);
	    assertEquals(BAD_REQUEST, result.status());
	    
	    JsonNode responseNode = Json.parse(contentAsString(result));
	  	    
	    assertEquals(responseNode.get("status").asText(), "ERROR");	
	    assertEquals(responseNode.get("message").asText(), "Cannot parse JSON to Subscription");
	    
	}
	
	@Test
	public void testDeleteSubscription_Success() {
		RequestBuilder request = Helpers.fakeRequest()
				.method("POST")
				.bodyForm(ImmutableMap.of("id", String.valueOf(testSubscription.getId())))
				.uri(controllers.admin.routes.SubscriptionController.deleteSubscription().url());
		
		Result result = route(request);
	    assertEquals(OK, result.status());
	}
	
	@Test
	public void testDeleteSubscription_Failure() {
		RequestBuilder request = Helpers.fakeRequest()
				.method("POST")
				.bodyForm(ImmutableMap.of("id", "321"))
				.uri(controllers.admin.routes.SubscriptionController.deleteSubscription().url());
		
		Result result = route(request);
		assertEquals(BAD_REQUEST, result.status());
	    
	    JsonNode responseNode = Json.parse(contentAsString(result));	    
	    
	    assertEquals(responseNode.get("status").asText(), "ERROR");	
	    assertEquals(responseNode.get("message").asText(), "Subscription not found");   
	    
	}
	
	@Test
	public void testCancelSubscription_Success() {
		RequestBuilder request = Helpers.fakeRequest()
				.method("POST")
				.bodyForm(ImmutableMap.of("id", String.valueOf(testSubscription.getId())))
				.uri(controllers.admin.routes.SubscriptionController.cancelSubscription().url());
		
		Result result = route(request);
	    assertEquals(OK, result.status());
	}
	
	@Test
	public void testCancelSubscription_Failure() {
		RequestBuilder request = Helpers.fakeRequest()
				.method("POST")
				.bodyForm(ImmutableMap.of("id", "321"))
				.uri(controllers.admin.routes.SubscriptionController.cancelSubscription().url());
		
		Result result = route(request);
		assertEquals(BAD_REQUEST, result.status());
	    
	    JsonNode responseNode = Json.parse(contentAsString(result));	    
	    
	    assertEquals(responseNode.get("status").asText(), "ERROR");	
	    assertEquals(responseNode.get("message").asText(), "Subscription not found");   
	    
	}
	
	@Test
	public void testFilterSubscription_Success() {
		RequestBuilder request = Helpers.fakeRequest()
				.method("POST")
				.bodyForm(ImmutableMap.of("status", "TRIAL"))
				.uri(controllers.admin.routes.SubscriptionController.filterSubscriptions().url());
		
		Result result = route(request);
	    assertEquals(OK, result.status());
	    
	    JsonNode responseNode = Json.parse(contentAsString(result));
	    
	    assertEquals(responseNode.get("status").asText(), "SUCCESS");	
	    assertEquals(responseNode.get("message").asText(), "Subscriptions Filtered");
	    	    
	}	
		
}
