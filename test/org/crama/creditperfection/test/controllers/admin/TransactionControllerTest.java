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
import org.crama.creditperfection.test.builders.ProductBuilder;
import org.crama.creditperfection.test.builders.TransactionBuilder;
import org.crama.creditperfection.test.builders.TransactionFormBuilder;
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

import controllers.admin.TransactionController;
import forms.TransactionForm;
import models.CreditCard;
import models.Product;
import models.SecurityRole;
import models.Transaction;
import models.User;
import models.json.ErrorResponse;
import models.json.JSONResponse;
import models.json.MessageResponse;
import net.authorize.api.contract.v1.CreateTransactionResponse;
import play.inject.guice.GuiceApplicationBuilder;
import play.libs.Json;
import play.mvc.Http.RequestBuilder;
import play.mvc.Result;
import play.test.Helpers;
import services.CreditCardService;
import services.ProductService;
import services.TransactionService;
import services.UserService;

public class TransactionControllerTest extends ControllerTestBase {
	
	private static final String TEST_EMAIL = "test@gmail.com";
	private static Transaction testTransaction;
	private static User testUser;
	private static CreditCard testCreditCard;
	private static TransactionForm testTransactionForm;
	private static Product testProduct;
	private static CreateTransactionResponse transactionResponse;
	
	@Mock
	private UserService userServiceMock;
	
	@Mock
	private CreditCardService creditCardServiceMock;
	
	@Mock
	private ProductService productServiceMock;
	
	@Mock
	private TransactionService transactionServiceMock;
	
	@InjectMocks
	private TransactionController transactionController;
	
	private void setUp() {
		MockitoAnnotations.initMocks(this);			
		
		testTransaction = new TransactionBuilder().id(55).build();
		testTransactionForm = new TransactionFormBuilder().id("55").build();
		
		List<SecurityRole> roles = Arrays.asList(new SecurityRole(1, "user"), 
				 new SecurityRole(2, "admin"));
		testUser = new UserBuilder().email(TEST_EMAIL).id(1).roles(roles).build();
		testCreditCard = new CreditCardBuilder().id(55).build();
		testProduct = new ProductBuilder().id(55).build();
		
		transactionResponse = new CreateTransactionResponse();
		transactionResponse.setSessionToken("Session Token");
		
		List<Transaction> allTransactions = Arrays.asList(
			new TransactionBuilder()
				.id(11)
				.amount(2.5)
				.build(),
			new TransactionBuilder()
				.id(12)
				.amount(4.5)
				.build(),
				testTransaction); 
		
		when(userServiceMock.getAll()).thenReturn(Arrays.asList(testUser));
		when(productServiceMock.getAll()).thenReturn(Arrays.asList(testProduct));
		when(transactionServiceMock.findAll()).thenReturn(allTransactions);
		when(transactionServiceMock.findById(testTransaction.getId())).thenReturn(testTransaction);
		when(transactionServiceMock.createTransaction(Matchers.any())).thenReturn(testTransaction);
		
		when(creditCardServiceMock.refundTransaction(testTransaction)).thenReturn(transactionResponse);		
	}
	
	@Override
    public GuiceApplicationBuilder getBuilder() {
        GuiceApplicationBuilder builder = super.getBuilder();
        setUp();
        return builder.overrides(bind(TransactionService.class).toInstance(transactionServiceMock),
				        		 bind(UserService.class).toInstance(userServiceMock),
				        		 bind(ProductService.class).toInstance(productServiceMock),
				        		 bind(CreditCardService.class).toInstance(creditCardServiceMock));        						         						 
    }
	
	@Test
	public void testTransactions()	{
		Result result = transactionController.transactions();
		assertEquals(OK, result.status());
		assertEquals("utf-8", result.charset().get());
		assertEquals("text/html", result.contentType().get());
		
		assertTrue(contentAsString(result).contains("Add Transaction"));
		assertTrue(contentAsString(result).contains(testUser.getLastName()));
		assertTrue(contentAsString(result).contains(testCreditCard.getDigits()));
		assertTrue(contentAsString(result).contains(testProduct.getName()));
	}
	
	@Test
	public void testAddTransactions_Success() {
		JsonNode transactionFormJson = Json.toJson(testTransactionForm);
		
		RequestBuilder request = Helpers.fakeRequest()
									.method("POST")
									.bodyJson(transactionFormJson)
									.uri(controllers.admin.routes.TransactionController.addTransaction().url());
		
	    Result result = route(request);
	    assertEquals(OK, result.status());
	    
	    JsonNode responseNode = Json.parse(contentAsString(result));
	    
	    assertEquals(responseNode.get("status").asText(), "SUCCESS");	
	    assertEquals(responseNode.get("message").asText(), "Transaction was added successfully");
	}
	
	@Test
	public void testAddTransaction_Failure_ValidationErrors() {
		testTransactionForm.setAmount("plus100500");
		JsonNode transactionFormJson = Json.toJson(testTransactionForm);
										
		RequestBuilder request = Helpers.fakeRequest()
									.method("POST")
									.bodyJson(transactionFormJson)
									.uri(controllers.admin.routes.TransactionController.addTransaction().url());
		
	    Result result = route(request);
	    assertEquals(BAD_REQUEST, result.status());
	    
	    JsonNode responseNode = Json.parse(contentAsString(result));
	    
	    assertTrue(responseNode.isArray());
	    ArrayNode responseArrayNode = (ArrayNode) responseNode;
	    
	    assertEquals(responseArrayNode.get(0).get("field").asText(), "amount");	
	    assertEquals(responseArrayNode.get(0).get("error").asText(), "Amount should be numeric value");
	}
	
	@Test
	public void testAddTransaction_Failure_ParsingJson() {
		       
		JsonNode testTransactionJson = Json.toJson("{ \"someOther\": \"json\" }");
		
		RequestBuilder request = Helpers.fakeRequest()
									.method("POST")
									.bodyJson(testTransactionJson)
									.uri(controllers.admin.routes.TransactionController.addTransaction().url());		
		
		Result result = route(request);
	    assertEquals(BAD_REQUEST, result.status());
	    	    
	    JsonNode responseNode = Json.parse(contentAsString(result));	    
	    
	    assertEquals(responseNode.get("status").asText(), "ERROR");	
	    assertEquals(responseNode.get("message").asText(), "Cannot parse JSON to TransactionForm");
	}
	
	@Test
	public void testEditTransactions_Success() {
		JsonNode transactionFormJson = Json.toJson(testTransactionForm);
		
		RequestBuilder request = Helpers.fakeRequest()
									.method("POST")
									.bodyJson(transactionFormJson)
									.uri(controllers.admin.routes.TransactionController.editTransaction().url());
		
	    Result result = route(request);
	    assertEquals(OK, result.status());
	    
	    JsonNode responseNode = Json.parse(contentAsString(result));
	    
	    assertEquals(responseNode.get("status").asText(), "SUCCESS");	
	    assertEquals(responseNode.get("message").asText(), "Transaction was edited successfully");
	}
	
	@Test
	public void testEditTransaction_Failure_ValidationErrors() {
		testTransactionForm.setAmount("plus100500");
		JsonNode transactionFormJson = Json.toJson(testTransactionForm);
										
		RequestBuilder request = Helpers.fakeRequest()
									.method("POST")
									.bodyJson(transactionFormJson)
									.uri(controllers.admin.routes.TransactionController.editTransaction().url());
		
	    Result result = route(request);
	    assertEquals(BAD_REQUEST, result.status());
	    
	    JsonNode responseNode = Json.parse(contentAsString(result));
	    
	    assertTrue(responseNode.isArray());
	    ArrayNode responseArrayNode = (ArrayNode) responseNode;
	    
	    assertEquals(responseArrayNode.get(0).get("field").asText(), "amount");	
	    assertEquals(responseArrayNode.get(0).get("error").asText(), "Amount should be numeric value");
	}
	
	@Test
	public void testEditTransaction_Failure_ParsingJson() {
		       
		JsonNode testTransactionJson = Json.toJson("{ \"someOther\": \"json\" }");
		
		RequestBuilder request = Helpers.fakeRequest()
									.method("POST")
									.bodyJson(testTransactionJson)
									.uri(controllers.admin.routes.TransactionController.editTransaction().url());		
		
		Result result = route(request);
	    assertEquals(BAD_REQUEST, result.status());
	    	    
	    JsonNode responseNode = Json.parse(contentAsString(result));	    
	    
	    assertEquals(responseNode.get("status").asText(), "ERROR");	
	    assertEquals(responseNode.get("message").asText(), "Cannot parse JSON to TransactionForm");
	}
	
	@Test 
	public void testDeleteTransaction_Success() {
		when(transactionServiceMock.delete(Mockito.any())).thenReturn(true);
		
		RequestBuilder request = Helpers.fakeRequest()
				.method("POST")
				.bodyForm(ImmutableMap.of("id", String.valueOf(testTransaction.getId())))
				.uri(controllers.admin.routes.TransactionController.deleteTransaction().url());
		
		Result result = route(request);
	    assertEquals(OK, result.status());
	    
	    JsonNode responseNode = Json.parse(contentAsString(result));
	    
	    assertEquals(responseNode.get("status").asText(), "SUCCESS");	
	    assertEquals(responseNode.get("message").asText(), "Transaction was deleted successfully");
	}
	
	@Test 
	public void testDeleteTransaction_Failure_TransactionNotFound() {
		long id = 456;
		when(transactionServiceMock.findById(id)).thenReturn(null);
				
		RequestBuilder request = Helpers.fakeRequest()
				.method("POST")
				.bodyForm(ImmutableMap.of("id", String.valueOf(id)))
				.uri(controllers.admin.routes.TransactionController.deleteTransaction().url());
		
		Result result = route(request);
	    assertEquals(BAD_REQUEST, result.status());
	    
	    JsonNode responseNode = Json.parse(contentAsString(result));
	    
	    assertEquals(responseNode.get("status").asText(), "ERROR");	
	    assertEquals(responseNode.get("message").asText(), "Transaction with id " + id + " is not found");
	}
	
	@Test 
	public void testDeleteTransaction_Failure() {
		when(transactionServiceMock.delete(Mockito.any())).thenReturn(false);
		
		RequestBuilder request = Helpers.fakeRequest()
				.method("POST")
				.bodyForm(ImmutableMap.of("id", String.valueOf(testTransaction.getId())))
				.uri(controllers.admin.routes.TransactionController.deleteTransaction().url());
		
		Result result = route(request);
	    assertEquals(BAD_REQUEST, result.status());
	    
	    JsonNode responseNode = Json.parse(contentAsString(result));
	    
	    assertEquals(responseNode.get("status").asText(), "ERROR");	
	    assertEquals(responseNode.get("message").asText(), "Transaction was not deleted");
	}
	
	@Test
	public void testRefundTransaction_Success()
	{
		JSONResponse jsonResponse = new MessageResponse("SUCCESS", "Successful Credit Card Transaction");
		when(creditCardServiceMock.checkTransaction(transactionResponse)).thenReturn(jsonResponse);
		
		RequestBuilder request = Helpers.fakeRequest()
				.method("POST")
				.bodyForm(ImmutableMap.of("id", String.valueOf(testTransaction.getId())))
				.uri(controllers.admin.routes.TransactionController.refundTransaction().url());
		
		Result result = route(request);
	    assertEquals(OK, result.status());
	    
	    JsonNode responseNode = Json.parse(contentAsString(result));
	    
	    assertEquals(responseNode.get("status").asText(), "SUCCESS");	
	    assertEquals(responseNode.get("message").asText(), "Successful Credit Card Transaction");		
	}
	
	@Test
	public void testRefundTarnsaction_Failure()
	{
		JSONResponse jsonResponse = new ErrorResponse("ERROR", "101", "Error during processing transaction");
		when(creditCardServiceMock.checkTransaction(transactionResponse)).thenReturn(jsonResponse);
		
		RequestBuilder request = Helpers.fakeRequest()
				.method("POST")
				.bodyForm(ImmutableMap.of("id", String.valueOf(testTransaction.getId())))
				.uri(controllers.admin.routes.TransactionController.refundTransaction().url());
		
		Result result = route(request);
	    assertEquals(BAD_REQUEST, result.status());
	    
	    JsonNode responseNode = Json.parse(contentAsString(result));
	    
	    assertEquals(responseNode.get("status").asText(), "ERROR");	
	    assertEquals(responseNode.get("errorCode").asText(), "101");
	    assertEquals(responseNode.get("errorMessage").asText(), "Error during processing transaction");
	}
	
	@Test
	public void testRefundTarnsaction_Failure_TransactionNotFound()
	{
		long id = 456;
		when(transactionServiceMock.findById(id)).thenReturn(null);
		
		RequestBuilder request = Helpers.fakeRequest()
				.method("POST")
				.bodyForm(ImmutableMap.of("id", String.valueOf(id)))
				.uri(controllers.admin.routes.TransactionController.refundTransaction().url());
		
		Result result = route(request);
	    assertEquals(BAD_REQUEST, result.status());
	    
	    JsonNode responseNode = Json.parse(contentAsString(result));
	    
	    assertEquals(responseNode.get("status").asText(), "ERROR");	
	    assertEquals(responseNode.get("message").asText(), "Transaction with id " + id + " is not found");
	}

}
