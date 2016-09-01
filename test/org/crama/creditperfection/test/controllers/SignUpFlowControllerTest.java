package org.crama.creditperfection.test.controllers;

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

import javax.inject.Inject;

import org.crama.creditperfection.test.base.ControllerTestBase;
import org.crama.creditperfection.test.builders.AuthNetAccountBuilder;
import org.crama.creditperfection.test.builders.CreditCardBuilder;
import org.crama.creditperfection.test.builders.ProductBuilder;
import org.crama.creditperfection.test.builders.ProductFormBuilder;
import org.crama.creditperfection.test.builders.RegisterFormBuilder;
import org.crama.creditperfection.test.builders.UserBuilder;
import org.junit.Test;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.google.common.collect.ImmutableList;

import controllers.SignUpFlowController;
import controllers.admin.AuthNetAccountController;
import errors.ValidationError;
import forms.CreditCardForm;
import forms.ProductForm;
import forms.RegisterForm;
import models.AuthNetAccount;
import models.CreditCard;
import models.KBAQuestions;
import models.Product;
import models.SecurityRole;
import models.User;
import models.json.CreditReportSuccessResponse;
import models.json.ErrorResponse;
import models.json.MessageResponse;
import net.authorize.api.contract.v1.CreateTransactionResponse;
import play.Configuration;
import play.data.Form;
import play.data.FormFactory;
import play.inject.guice.GuiceApplicationBuilder;
import play.libs.Json;
import play.mvc.Http.RequestBuilder;
import play.mvc.Http.Session;
import play.test.Helpers;
import play.mvc.Result;
import services.AuthNetAccountService;
import services.CreditCardService;
import services.CreditReportService;
import services.ProductService;
import services.RoleService;
import services.SubscriptionService;
import services.TransactionService;
import services.UserService;

public class SignUpFlowControllerTest extends ControllerTestBase {
	
	private static User testUser;
	private static Product testProduct;
	private static RegisterForm testRegisterForm;
	private static CreditCardForm testCreditCardForm;
	private static CreditCard testCreditCard;
    
	@Mock(answer = Answers.RETURNS_DEEP_STUBS)
	public FormFactory formFactoryMock;

	@Mock
    private CreditCardService creditCardServiceMock;
    
	@Mock
    private CreditReportService creditReportServiceMock;
    
	@Mock
	private Configuration confMock;
    
	@Mock
    private UserService userServiceMock;

	@Mock
    private ProductService productServiceMock;

	@Mock
    private RoleService roleServiceMock;
	
	@Mock
    private TransactionService transactionServiceMock;
    
	@Mock
    private SubscriptionService subscriptionServiceMock;
	
	@InjectMocks
	private SignUpFlowController signupFlowController;
	
	private void setUp() {
		MockitoAnnotations.initMocks(this);			
		
		testProduct = new ProductBuilder().id(55).build();
		testRegisterForm = new RegisterFormBuilder().build();
		testCreditCardForm = new CreditCardBuilder().buildForm();
		testCreditCard = new CreditCardBuilder().build(); 
				
		List<Product> allProducts = Arrays.asList(
			new ProductBuilder()
				.id(11)
				.name("testProduct1")
				.build(),
			new ProductBuilder()
				.id(12)
				.name("testProduct2")
				.build(),
				testProduct); 
		
		when(productServiceMock.getAll()).thenReturn(allProducts);
		when(productServiceMock.getById(Mockito.anyLong())).thenReturn(testProduct);
		when(productServiceMock.createProduct(Matchers.any())).thenReturn(testProduct);
		
		List<SecurityRole> roles = Arrays.asList(new SecurityRole(1, "user"), 
				 new SecurityRole(2, "admin"));
		testUser = new UserBuilder().id(1).roles(roles).build();
				
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
		
		when(userServiceMock.getAll()).thenReturn(allUsers);		
		when(userServiceMock.getById(testUser.getId())).thenReturn(testUser);
	}
	
	@Override
    public GuiceApplicationBuilder getBuilder() {
        GuiceApplicationBuilder builder = super.getBuilder();
        setUp();
        return builder.overrides(bind(FormFactory.class).toInstance(formFactoryMock),
        						 bind(CreditCardService.class).toInstance(creditCardServiceMock),
        						 bind(CreditReportService.class).toInstance(creditReportServiceMock),
        						 bind(Configuration.class).toInstance(confMock),
        						 bind(UserService.class).toInstance(userServiceMock),
        						 bind(ProductService.class).toInstance(productServiceMock),
        						 bind(RoleService.class).toInstance(roleServiceMock),
        						 bind(TransactionService.class).toInstance(transactionServiceMock),
        						 bind(SubscriptionService.class).toInstance(subscriptionServiceMock));        						         						 
    }
	
	@Test
	public void indexEmpty_Success() {
		Result result = signupFlowController.index(false);
		assertEquals(OK, result.status());
		assertEquals("utf-8", result.charset().get());
		assertEquals("text/html", result.contentType().get());
		
		assertTrue(contentAsString(result).contains("Start Here"));
		assertTrue(contentAsString(result).contains("First Name:"));
		assertTrue(contentAsString(result).contains("Continue"));
		
	}
	
	@Test
	public void indexFilled_Success() {
		when(userServiceMock.findByEmail(testUser.getEmail())).thenReturn(testUser);
		context().session().put("userEmail", testUser.getEmail());
		
		Result result = signupFlowController.index(false);
		assertEquals(OK, result.status());
		assertEquals("utf-8", result.charset().get());
		assertEquals("text/html", result.contentType().get());
		
		assertTrue(contentAsString(result).contains("Start Here"));
		assertTrue(contentAsString(result).contains("First Name:"));
		assertTrue(contentAsString(result).contains(testUser.getFirstName()));
		assertTrue(contentAsString(result).contains(testUser.getLastName()));
		assertTrue(contentAsString(result).contains(testUser.getEmail()));
		assertTrue(contentAsString(result).contains(testUser.getAddress()));
		assertTrue(contentAsString(result).contains(testUser.getZip()));
		assertTrue(contentAsString(result).contains("Continue"));		
	}
	
	@Test
	public void testPrepopulateForm_Success() {
		Form<User> userForm = new Form<User>(User.class, null, null, null);
		
		when(formFactoryMock.form(User.class).bindFromRequest().get()).thenReturn(testUser);
				
		userForm.fill(testUser);
		
		RequestBuilder request = Helpers.fakeRequest()
									.method("POST")
									.bodyForm(userForm.data())
									.uri(controllers.routes.SignUpFlowController.prePopulateRegisterForm().url());
		
	    Result result = route(request);
	    assertEquals(OK, result.status());
	    
	    assertTrue(contentAsString(result).contains("Start Here"));
		assertTrue(contentAsString(result).contains("First Name:"));
		assertTrue(contentAsString(result).contains(testUser.getFirstName()));
		assertTrue(contentAsString(result).contains(testUser.getLastName()));
		assertTrue(contentAsString(result).contains(testUser.getEmail()));
		assertTrue(contentAsString(result).contains(testUser.getAddress()));
		assertTrue(contentAsString(result).contains(testUser.getZip()));
		assertTrue(contentAsString(result).contains("Continue"));
	}
	
	@Test
	public void testRegister_Success() {
		when(userServiceMock.findByEmail(testUser.getEmail())).thenReturn(null);
		JsonNode registerFormJson = Json.toJson(testRegisterForm);
		
		RequestBuilder request = Helpers.fakeRequest()
									.method("POST")
									.bodyJson(registerFormJson)
									.uri(controllers.routes.SignUpFlowController.register().url());
		
	    Result result = route(request);
	    assertEquals(OK, result.status());
	    
	    JsonNode responseNode = Json.parse(contentAsString(result));
	    
	    assertEquals(responseNode.get("status").asText(), "SUCCESS");	
	    assertEquals(responseNode.get("message").asText(), "User was successfully registered");
	}
	
	@Test
	public void testRegister_Unfinished_Success() {
		when(userServiceMock.findByEmail(testUser.getEmail())).thenReturn(testUser);
		JsonNode registerFormJson = Json.toJson(testRegisterForm);
		
		RequestBuilder request = Helpers.fakeRequest()
									.method("POST")
									.bodyJson(registerFormJson)
									.uri(controllers.routes.SignUpFlowController.register().url());
		
	    Result result = route(request);
	    assertEquals(OK, result.status());
	    
	    JsonNode responseNode = Json.parse(contentAsString(result));
	    
	    assertEquals(responseNode.get("status").asText(), "SUCCESS");	
	    assertEquals(responseNode.get("message").asText(), "You have unfinished registration process");
	}
	
	@Test
	public void testRegister_Failure_ParsingJson() {
		       
		JsonNode testRegisterForm = Json.toJson("{ \"someOther\": \"json\" }");
		
		RequestBuilder request = Helpers.fakeRequest()
									.method("POST")
									.bodyJson(testRegisterForm)
									.uri(controllers.routes.SignUpFlowController.register().url());		
		
		Result result = route(request);
	    assertEquals(BAD_REQUEST, result.status());
	    	    
	    JsonNode responseNode = Json.parse(contentAsString(result));	    
	    
	    assertEquals(responseNode.get("status").asText(), "ERROR");	
	    assertEquals(responseNode.get("message").asText(), "Cannot parse JSON to register form");
	}
	
	@Test
	public void testRegister_Failure_AlreadyRegistered() {
		User testUserRegistered = new UserBuilder().build();
		testUserRegistered.setKbaQuestions(new KBAQuestions());
		when(userServiceMock.findByEmail(testUser.getEmail())).thenReturn(testUserRegistered);
		
		JsonNode registerFormJson = Json.toJson(testRegisterForm);
		
		RequestBuilder request = Helpers.fakeRequest()
									.method("POST")
									.bodyJson(registerFormJson)
									.uri(controllers.routes.SignUpFlowController.register().url());		
		
		Result result = route(request);
	    assertEquals(BAD_REQUEST, result.status());
	    	    
	    JsonNode responseNode = Json.parse(contentAsString(result));
	    
	    assertTrue(responseNode.isArray());
	    ArrayNode responseArrayNode = (ArrayNode) responseNode;
	    	    
	    assertEquals(responseArrayNode.get(0).get("field").asText(), "email");	
	    assertEquals(responseArrayNode.get(0).get("error").asText(), "User with such email is already registered");
	}
	
	@Test
	public void testRegister_Failure_ValidationErrors() {
		RegisterForm formWithErrors = new RegisterFormBuilder().zip("123").build();  
		JsonNode registerFormJson = Json.toJson(formWithErrors);
		
		RequestBuilder request = Helpers.fakeRequest()
									.method("POST")
									.bodyJson(registerFormJson)
									.uri(controllers.routes.SignUpFlowController.register().url());		
		
		Result result = route(request);
	    assertEquals(BAD_REQUEST, result.status());
	    	    
	    JsonNode responseNode = Json.parse(contentAsString(result));
	    
	    assertTrue(responseNode.isArray());
	    ArrayNode responseArrayNode = (ArrayNode) responseNode;
	    	    
	    assertEquals(responseArrayNode.get(0).get("field").asText(), "zip");	
	    assertEquals(responseArrayNode.get(0).get("error").asText(), "Zip code should contain 5 digits");
	}
	
	@Test
	public void testPayment_Success() {
		CreateTransactionResponse response = new CreateTransactionResponse();
		MessageResponse successfulResponse = new MessageResponse("SUCCESS", "Successful Credit Card Transaction");
		when(creditCardServiceMock.charge(1.0, testCreditCard)).thenReturn(response);
		when(creditCardServiceMock.checkTransaction(Matchers.any())).thenReturn(successfulResponse);
		when(creditCardServiceMock.createCreditCard(Matchers.any())).thenReturn(testCreditCard);
		
		CreditReportSuccessResponse responseKba = new CreditReportSuccessResponse("SUCCESS", "http://somekba.url");
		when(creditReportServiceMock.getKBAQuestionsUrl(testUser)).thenReturn(responseKba);
		
		JsonNode creditCardFormJson = Json.toJson(testCreditCardForm);
		
		when(userServiceMock.findByEmail(Matchers.anyString())).thenReturn(testUser);
		
		RequestBuilder request = Helpers.fakeRequest()
									.method("POST")
									.bodyJson(creditCardFormJson)
									.uri(controllers.routes.SignUpFlowController.processPaymentAndGetReport().url());		
		
		Result result = route(request);
	    assertEquals(OK, result.status());
		
	    JsonNode responseNode = Json.parse(contentAsString(result));	    
	    
	    assertEquals(responseNode.get("status").asText(), "SUCCESS");	
	    assertEquals(responseNode.get("reportUrl").asText(), "http://somekba.url");
	}
	
	@Test
	public void testPayment_Failure_ParsingJson() {
				
		JsonNode creditCardFormJson = Json.toJson("{ \"someOther\": \"json\" }");
				
		RequestBuilder request = Helpers.fakeRequest()
									.method("POST")
									.bodyJson(creditCardFormJson)
									.uri(controllers.routes.SignUpFlowController.processPaymentAndGetReport().url());		
		
		Result result = route(request);
	    assertEquals(BAD_REQUEST, result.status());
		
	    JsonNode responseNode = Json.parse(contentAsString(result));	    
	    
	    assertEquals(responseNode.get("status").asText(), "ERROR");	
	    assertEquals(responseNode.get("message").asText(), "Cannot parse JSON to CreditCardForm");
	}
	
	@Test
	public void testPayment_Failure_ValidationErrors() {
		CreditCardForm formWithErrors = new CreditCardBuilder().cvv(12).buildForm();  
		JsonNode ccFormJson = Json.toJson(formWithErrors);
		
		RequestBuilder request = Helpers.fakeRequest()
									.method("POST")
									.bodyJson(ccFormJson)
									.uri(controllers.routes.SignUpFlowController.processPaymentAndGetReport().url());		
		
		Result result = route(request);
	    assertEquals(BAD_REQUEST, result.status());
	    	    
	    JsonNode responseNode = Json.parse(contentAsString(result));
	    
	    assertTrue(responseNode.isArray());
	    ArrayNode responseArrayNode = (ArrayNode) responseNode;
	    	    
	    assertEquals(responseArrayNode.get(0).get("field").asText(), "cvv");	
	    assertEquals(responseArrayNode.get(0).get("error").asText(), "Invalid CVV number");
	}
	
	@Test
	public void testPayment_Failure_KBAQuestionsError() {
				
		ErrorResponse responseKba = new ErrorResponse("ERROR", "101", "Some error with KBA");
		when(creditReportServiceMock.getKBAQuestionsUrl(testUser)).thenReturn(responseKba);
		
		JsonNode creditCardFormJson = Json.toJson(testCreditCardForm);
		
		when(userServiceMock.findByEmail(Matchers.anyString())).thenReturn(testUser);
		
		RequestBuilder request = Helpers.fakeRequest()
									.method("POST")
									.bodyJson(creditCardFormJson)
									.uri(controllers.routes.SignUpFlowController.processPaymentAndGetReport().url());		
		
		Result result = route(request);
	    assertEquals(BAD_REQUEST, result.status());
		
	    JsonNode responseNode = Json.parse(contentAsString(result));	    
	    
	    assertEquals(responseNode.get("status").asText(), "ERROR");	
	    assertEquals(responseNode.get("errorMessage").asText(), "Some error with KBA");
	}
	
	@Test
	public void testPayment_Failure_ProductNotFound() {
				
		when(creditCardServiceMock.createCreditCard(Matchers.any())).thenReturn(testCreditCard);
		
		CreditReportSuccessResponse responseKba = new CreditReportSuccessResponse("SUCCESS", "http://somekba.url");
		when(creditReportServiceMock.getKBAQuestionsUrl(testUser)).thenReturn(responseKba);
		
		JsonNode creditCardFormJson = Json.toJson(testCreditCardForm);
		
		long productId = 0;
		
		when(confMock.getLong(Mockito.anyString())).thenReturn(productId);
		when(productServiceMock.getById(Mockito.anyLong())).thenReturn(null);
		when(userServiceMock.findByEmail(Matchers.anyString())).thenReturn(testUser);
		
		RequestBuilder request = Helpers.fakeRequest()
									.method("POST")
									.bodyJson(creditCardFormJson)
									.uri(controllers.routes.SignUpFlowController.processPaymentAndGetReport().url());		
		
		Result result = route(request);
	    assertEquals(BAD_REQUEST, result.status());
		
	    JsonNode responseNode = Json.parse(contentAsString(result));	    
	    
	    assertEquals(responseNode.get("status").asText(), "ERROR");	
	    assertEquals(responseNode.get("message").asText(),  "Prodcut with id = " + productId + " is missing");
	}
	
	@Test
	public void testPayment_Failure_Transaction() {
				
		CreateTransactionResponse response = new CreateTransactionResponse();
		ErrorResponse errorResponse = new ErrorResponse("ERROR", "111", "Transaction Failed");
		when(creditCardServiceMock.charge(1.0, testCreditCard)).thenReturn(response);
		when(creditCardServiceMock.checkTransaction(Matchers.any())).thenReturn(errorResponse);
		when(creditCardServiceMock.createCreditCard(Matchers.any())).thenReturn(testCreditCard);
		
		CreditReportSuccessResponse responseKba = new CreditReportSuccessResponse("SUCCESS", "http://somekba.url");
		when(creditReportServiceMock.getKBAQuestionsUrl(testUser)).thenReturn(responseKba);
		
		JsonNode creditCardFormJson = Json.toJson(testCreditCardForm);
		
		when(userServiceMock.findByEmail(Matchers.anyString())).thenReturn(testUser);
		
		RequestBuilder request = Helpers.fakeRequest()
									.method("POST")
									.bodyJson(creditCardFormJson)
									.uri(controllers.routes.SignUpFlowController.processPaymentAndGetReport().url());		
		
		Result result = route(request);
	    assertEquals(BAD_REQUEST, result.status());
		
	    JsonNode responseNode = Json.parse(contentAsString(result));	    
	    
	    assertEquals(responseNode.get("status").asText(), "ERROR");	
	    assertEquals(responseNode.get("errorMessage").asText(),  "Transaction Failed");
	}
	
}
