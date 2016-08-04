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
import org.crama.creditperfection.test.builders.CreditCardBuilder;
import org.crama.creditperfection.test.builders.UserBuilder;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.google.common.collect.ImmutableMap;

import controllers.admin.CreditCardController;
import forms.CreditCardForm;
import models.CreditCard;
import models.User;
import play.inject.guice.GuiceApplicationBuilder;
import play.libs.Json;
import play.mvc.Http.RequestBuilder;
import play.mvc.Result;
import play.test.Helpers;
import services.CreditCardService;
import services.UserService;

public class CreditCardControllerTest extends ControllerTestBase {
	
	private static CreditCard testCreditCard;
	private static CreditCardForm testCreditCardForm;
	private static User testUser;
	
	@Mock
	private CreditCardService creditCardServiceMock;
	
	@Mock
	private UserService userServiceMock;
	
	@InjectMocks
	private CreditCardController creditCardController;
	
	private void setUp() {
		MockitoAnnotations.initMocks(this);	
		
		testUser = new UserBuilder().id(1).build();
		
		testCreditCard = new CreditCardBuilder().id(55).build();
		testCreditCardForm = new CreditCardBuilder().id(55).buildForm();
		
		List<CreditCard> allCreditCards = Arrays.asList(
			new CreditCardBuilder()
				.id(11)
				.name("testCreditCard1")
				.build(),
			new CreditCardBuilder()
				.id(12)
				.name("testCreditCard2")
				.digits("2222 2222 2222 2222")
				.build(),
				testCreditCard); 
		
		when(creditCardServiceMock.getAll()).thenReturn(allCreditCards);
		when(creditCardServiceMock.getById(testCreditCard.getId())).thenReturn(testCreditCard);
		when(creditCardServiceMock.createCreditCard(Matchers.any())).thenReturn(testCreditCard);
		when(userServiceMock.getById(testUser.getId())).thenReturn(testUser);
	}
	
	@Override
    public GuiceApplicationBuilder getBuilder() {
        GuiceApplicationBuilder builder = super.getBuilder();
        setUp();
        return builder.overrides(bind(CreditCardService.class).toInstance(creditCardServiceMock),
        						 bind(UserService.class).toInstance(userServiceMock));        						         						 
    }
	
	@Test
	public void testCreditCards()	{
		Result result = creditCardController.creditCards();
		assertEquals(OK, result.status());
		assertEquals("utf-8", result.charset().get());
		assertEquals("text/html", result.contentType().get());
		
		assertTrue(contentAsString(result).contains("Add Credit Card"));
		assertTrue("The first Credit Card was not found", contentAsString(result).contains(testCreditCard.getDigits()));
		assertTrue("The second Credit Card user was not found",contentAsString(result).contains(creditCardServiceMock.getAll().get(0).getDigits()));
		assertTrue("The third Credit Card user was not found", contentAsString(result).contains(creditCardServiceMock.getAll().get(1).getDigits()));
	}
	
	@Test
	public void testAddCreditCards_Success() {
		JsonNode creditCardFormJson = Json.toJson(testCreditCardForm);
		
		RequestBuilder request = Helpers.fakeRequest()
									.method("POST")
									.bodyJson(creditCardFormJson)
									.uri(controllers.admin.routes.CreditCardController.addCreditCard().url());
		
	    Result result = route(request);
	    assertEquals(OK, result.status());
	    
	    JsonNode responseNode = Json.parse(contentAsString(result));
	    
	    assertEquals(responseNode.get("status").asText(), "SUCCESS");	
	    assertEquals(responseNode.get("message").asText(), "Credit Card was created successfully");
	}
	
	@Test
	public void testAddCreditCard_Failure_ValidationErrors() {
		testCreditCardForm.setDigits("1111 2222 3333");
		JsonNode creditCardFormJson = Json.toJson(testCreditCardForm);
										
		RequestBuilder request = Helpers.fakeRequest()
									.method("POST")
									.bodyJson(creditCardFormJson)
									.uri(controllers.admin.routes.CreditCardController.addCreditCard().url());
		
	    Result result = route(request);
	    assertEquals(BAD_REQUEST, result.status());
	    
	    JsonNode responseNode = Json.parse(contentAsString(result));
	    
	    assertTrue(responseNode.isArray());
	    ArrayNode responseArrayNode = (ArrayNode) responseNode;
	    
	    assertEquals(responseArrayNode.get(0).get("field").asText(), "digits");	
	    assertEquals(responseArrayNode.get(0).get("error").asText(), "Card Number should contain 15 or 16 digits");
	}
	
	@Test
	public void testAddCreditCard_Failure_ParsingJson() {
		       
		JsonNode testCreditCardJson = Json.toJson("{ \"someOther\": \"json\" }");
		
		RequestBuilder request = Helpers.fakeRequest()
									.method("POST")
									.bodyJson(testCreditCardJson)
									.uri(controllers.admin.routes.CreditCardController.addCreditCard().url());		
		
		Result result = route(request);
	    assertEquals(BAD_REQUEST, result.status());
	    	    
	    JsonNode responseNode = Json.parse(contentAsString(result));	    
	    
	    assertEquals(responseNode.get("status").asText(), "ERROR");	
	    assertEquals(responseNode.get("message").asText(), "Cannot parse JSON to CreditCardForm");
	}
	
	@Test
	public void testEditCreditCards_Success() {
		JsonNode creditCardFormJson = Json.toJson(testCreditCardForm);
		
		RequestBuilder request = Helpers.fakeRequest()
									.method("POST")
									.bodyJson(creditCardFormJson)
									.uri(controllers.admin.routes.CreditCardController.editCreditCard().url());
		
	    Result result = route(request);
	    assertEquals(OK, result.status());
	    
	    JsonNode responseNode = Json.parse(contentAsString(result));
	    
	    assertEquals(responseNode.get("status").asText(), "SUCCESS");	
	    assertEquals(responseNode.get("message").asText(), "Credit Card was edited successfully");
	}
	
	@Test
	public void testEditCreditCard_Failure_ValidationErrors() {
		testCreditCardForm.setCvv(null);
		JsonNode creditCardFormJson = Json.toJson(testCreditCardForm);
										
		RequestBuilder request = Helpers.fakeRequest()
									.method("POST")
									.bodyJson(creditCardFormJson)
									.uri(controllers.admin.routes.CreditCardController.editCreditCard().url());
		
	    Result result = route(request);
	    assertEquals(BAD_REQUEST, result.status());
	    
	    JsonNode responseNode = Json.parse(contentAsString(result));
	    
	    assertTrue(responseNode.isArray());
	    ArrayNode responseArrayNode = (ArrayNode) responseNode;
	    
	    assertEquals(responseArrayNode.get(0).get("field").asText(), "cvv");	
	    assertEquals(responseArrayNode.get(0).get("error").asText(), "Please enter CVV code");
	}
	
	@Test
	public void testEditCreditCard_Failure_ParsingJson() {
		       
		JsonNode testCreditCardJson = Json.toJson("{ \"someOther\": \"json\" }");
		
		RequestBuilder request = Helpers.fakeRequest()
									.method("POST")
									.bodyJson(testCreditCardJson)
									.uri(controllers.admin.routes.CreditCardController.editCreditCard().url());		
		
		Result result = route(request);
	    assertEquals(BAD_REQUEST, result.status());
	    	    
	    JsonNode responseNode = Json.parse(contentAsString(result));	    
	    
	    assertEquals(responseNode.get("status").asText(), "ERROR");	
	    assertEquals(responseNode.get("message").asText(), "Cannot parse JSON to CreditCardForm");
	}
	
	@Test
	public void testEditCreditCard_Failure_CreditCardNotFound() {
		       
		JsonNode testCreditCardJson = Json.toJson(testCreditCardForm);
		
		when(creditCardServiceMock.getById(testCreditCard.getId())).thenReturn(null);
		
		RequestBuilder request = Helpers.fakeRequest()
									.method("POST")
									.bodyJson(testCreditCardJson)
									.uri(controllers.admin.routes.CreditCardController.editCreditCard().url());		
		
		Result result = route(request);
	    assertEquals(BAD_REQUEST, result.status());
	    	    
	    JsonNode responseNode = Json.parse(contentAsString(result));	    
	    
	    assertEquals(responseNode.get("status").asText(), "ERROR");	
	    assertEquals(responseNode.get("message").asText(), "Credit Card with id 55 is not found");
	}
	
	@Test 
	public void testDeleteCreditCard_Success() {
		when(creditCardServiceMock.delete(Mockito.any())).thenReturn(true);
		
		RequestBuilder request = Helpers.fakeRequest()
				.method("POST")
				.bodyForm(ImmutableMap.of("id", String.valueOf(testCreditCard.getId())))
				.uri(controllers.admin.routes.CreditCardController.deleteCreditCard().url());
		
		Result result = route(request);
	    assertEquals(OK, result.status());
	    
	    JsonNode responseNode = Json.parse(contentAsString(result));
	    
	    assertEquals(responseNode.get("status").asText(), "SUCCESS");	
	    assertEquals(responseNode.get("message").asText(), "Credit Card was deleted successfully");
	}
	
	@Test 
	public void testDeleteCreditCard_Failure_CardNotFound() {
		long id = 456;
		when(creditCardServiceMock.getById(id)).thenReturn(null);
		
		RequestBuilder request = Helpers.fakeRequest()
				.method("POST")
				.bodyForm(ImmutableMap.of("id", String.valueOf(id)))
				.uri(controllers.admin.routes.CreditCardController.deleteCreditCard().url());
		
		Result result = route(request);
	    assertEquals(BAD_REQUEST, result.status());
	    
	    JsonNode responseNode = Json.parse(contentAsString(result));
	    
	    assertEquals(responseNode.get("status").asText(), "ERROR");	
	    assertEquals(responseNode.get("message").asText(), "Credit Card with id " + id + " is not found");
	}
	
	@Test 
	public void testDeleteCreditCard_Failure() {
		when(creditCardServiceMock.delete(Mockito.any())).thenReturn(false);
		
		RequestBuilder request = Helpers.fakeRequest()
				.method("POST")
				.bodyForm(ImmutableMap.of("id", String.valueOf(testCreditCard.getId())))
				.uri(controllers.admin.routes.CreditCardController.deleteCreditCard().url());
		
		Result result = route(request);
	    assertEquals(BAD_REQUEST, result.status());
	    
	    JsonNode responseNode = Json.parse(contentAsString(result));
	    
	    assertEquals(responseNode.get("status").asText(), "ERROR");	
	    assertEquals(responseNode.get("message").asText(), "Error occured while deleting the Credit Card");
	}

}
