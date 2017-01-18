package org.crama.creditperfection.test.services;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsIterableContainingInAnyOrder.containsInAnyOrder;
import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.YearMonth;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import org.crama.creditperfection.test.builders.AuthNetAccountBuilder;
import org.crama.creditperfection.test.builders.CreditCardBuilder;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import errors.ValidationError;
import forms.CreditCardForm;
import models.AuthNetAccount;
import models.CreditCard;
import models.enums.CardType;
import models.json.ErrorResponse;
import models.json.JSONResponse;
import net.authorize.api.contract.v1.CreateTransactionResponse;
import net.authorize.api.contract.v1.MessageTypeEnum;
import net.authorize.api.contract.v1.TransactionResponse;
import play.Configuration;
import play.Logger;
import repository.CreditCardRepository;
import services.AuthNetAccountService;
import services.CreditCardService;

public class CreditCardServiceTest {

	@Mock
	private CreditCardRepository creditCardRepositoryMock;
	
	@Mock
	private AuthNetAccountService authNetAccountServiceMock;
	
	@Mock
	private Configuration confMock;
	
	
	private final static String AUTH_NET_LOGIN_ID = "9yTxLt29j7Xb";
	private final static String	AUTH_NET_TRANSACTION_KEY = "33h923f5FYL4j3bE";
	
	@Inject
	@InjectMocks
	private CreditCardService creditCardService;
	
	@Before
	public void setUpCreateCreditCard() throws Exception {
		
		MockitoAnnotations.initMocks(this);
	}
	
	
	@Test
	public void testCreateCreditCard_Success() {
		
		CreditCardForm testForm = new CreditCardForm();
		testForm.setId("0");
		testForm.setName("Owner");
		testForm.setCardType("VISA");
		testForm.setDigits("1111 1111 1111 1111");
		testForm.setMonth("01");
		testForm.setYear("2018");
		testForm.setCvv("111");
		
		CreditCard creditCard = creditCardService.createCreditCard(testForm);
		
		assertTrue(creditCard.getId() == 0l);
		assertTrue(creditCard.getName().equals("Owner"));
		assertTrue(creditCard.getCardType().equals(CardType.VISA));
		assertTrue(creditCard.getDigits().equals("1111 1111 1111 1111"));
		assertTrue(creditCard.getExpDate().equals(YearMonth.of(2018, 1)));
		assertTrue(creditCard.getCvv() == 111);
		
	}
	
	@Before
	public void setUpCreateCreditCard_NumberFormatException() throws Exception {
		
		MockitoAnnotations.initMocks(this);
	}
	
	
	@Test(expected = NumberFormatException.class)
	public void testCreateCreditCard_NumberFormatException() {
		
		CreditCardForm testForm = new CreditCardForm();
		testForm.setId("invalid");
		
		creditCardService.createCreditCard(testForm);
		
	}
	
	@Before
	public void setUpCreateCreditCard_IllegalArgumentException() throws Exception {
		
		MockitoAnnotations.initMocks(this);
	}
	
	
	@Test(expected = IllegalArgumentException.class)
	public void testCreateCreditCard_IllegalArgumentException() {
		
		CreditCardForm testForm = new CreditCardForm();
		testForm.setCardType("invalid");
		
		creditCardService.createCreditCard(testForm);
		
	}
	
	@Before
	public void setUpCreateCreditCard_DateTimeParseException() throws Exception {
		
		MockitoAnnotations.initMocks(this);
	}
	
	
	@Test(expected = DateTimeParseException.class)
	public void testCreateCreditCard_DateTimeParseException() {
		
		CreditCardForm testForm = new CreditCardForm();
		testForm.setId("0");
		testForm.setName("Owner");
		testForm.setCardType("VISA");
		testForm.setMonth("-1");
		testForm.setYear("-2018");
		testForm.setCvv("111");
		
		creditCardService.createCreditCard(testForm);
		
	}
	
	@Before
	public void setUpGetAll() throws Exception {
		
		MockitoAnnotations.initMocks(this);
	}
	
	@Test
	public void tetsGetAll() {
		
		List<CreditCard> allCreditCards = Arrays.asList(new CreditCardBuilder().build(),
				new CreditCardBuilder().id(1).name("John Snow").build(),
				new CreditCardBuilder().id(2).name("Nicolas Cage").cardType(CardType.DISCOVER)
					.digits("1234 5678 9012 3456").cvv(123).expDate(YearMonth.of(2019, 4)).build());
		
		when(creditCardRepositoryMock.getAll()).thenReturn(allCreditCards);
		
		List<CreditCard> allCreditCardsResult = creditCardService.getAll();
		assertTrue(allCreditCardsResult.size() == allCreditCards.size());
		for (int i = 0; i < allCreditCardsResult.size(); i++) {
			assertTrue(allCreditCardsResult.get(i).getId() == i);
		}
		
		assertTrue(allCreditCardsResult.get(1).getName().equals("John Snow"));
		assertTrue(allCreditCardsResult.get(2).getName().equals("Nicolas Cage"));
		assertTrue(allCreditCardsResult.get(2).getCardType().equals(CardType.DISCOVER));
		assertTrue(allCreditCardsResult.get(2).getDigits().equals("1234 5678 9012 3456"));
		assertTrue(allCreditCardsResult.get(2).getCvv() == 123);
		assertTrue(allCreditCardsResult.get(2).getExpDate().equals(YearMonth.of(2019, 4)));
		
		verify(creditCardRepositoryMock, times(1)).getAll();
	}
	
	@Before
	public void setUpGetById() throws Exception {
		
		MockitoAnnotations.initMocks(this);
	}
	
	@Test
	public void testGetById() {
		
		CreditCard creditCard = new CreditCardBuilder().build();
		
		when(creditCardRepositoryMock.getById(anyLong())).thenReturn(creditCard);
		
		CreditCard creditCardResult = creditCardService.getById(0);
		assertTrue(creditCardResult.getId() == 0l);
		assertTrue(creditCardResult.getName().equals("Card Owner"));
		
		verify(creditCardRepositoryMock, times(1)).getById(anyLong());
		
	}
	
	@Before
	public void setUpChooseMerchantAccount_DefaultAccount() throws Exception {
		
		MockitoAnnotations.initMocks(this);
	}
	
	@Test
	public void testChooseMerchantAccount_DefaultAccount() {
		
		List<AuthNetAccount> authNetAccounts = new ArrayList<AuthNetAccount>();
		
		when(authNetAccountServiceMock.getEnabled()).thenReturn(authNetAccounts);
		
		when(confMock.getString("authorise.net.login.id")).thenReturn("loginId");
		when(confMock.getString("authorise.net.transaction.key")).thenReturn("transactionKey");
		
		AuthNetAccount account = creditCardService.chooseMerchantAccount();
		
		assertTrue(account.getLoginId().equals("loginId"));
		assertTrue(account.getTransactionKey().equals("transactionKey"));
		
		verify(authNetAccountServiceMock, times(1)).getEnabled();
		verify(confMock, times(2)).getString(anyString());
		
	}
	
	@Before
	public void setUpChooseMerchantAccount_NextAccount() throws Exception {
		
		MockitoAnnotations.initMocks(this);
	}
	
	@Test
	public void testChooseMerchantAccount_NextAccount() {
		
		List<AuthNetAccount> authNetAccounts = Arrays.asList(
				new AuthNetAccountBuilder().isLastUsed(false).build(),
				new AuthNetAccountBuilder().id(1).loginId("first").transactionKey("firstKey").isLastUsed(true).build(),
				new AuthNetAccountBuilder().id(2).loginId("second").transactionKey("secondKey").isLastUsed(false).build(),
				new AuthNetAccountBuilder().id(3).loginId("third").transactionKey("thirdKey").isLastUsed(false).build()
				);
		
		when(authNetAccountServiceMock.getEnabled()).thenReturn(authNetAccounts);
		
		when(confMock.getString("authorise.net.login.id")).thenReturn("loginId");
		when(confMock.getString("authorise.net.transaction.key")).thenReturn("transactionKey");
		
		AuthNetAccount account = creditCardService.chooseMerchantAccount();
		
		assertTrue(account.getLoginId().equals("second"));
		assertTrue(account.getTransactionKey().equals("secondKey"));
		
		verify(authNetAccountServiceMock, times(1)).getEnabled();
		verify(authNetAccountServiceMock, times(2)).update(any(AuthNetAccount.class));
		verify(confMock, times(0)).getString(anyString());
		
	}
	
	@Before
	public void setUpChooseMerchantAccount_GetFirstAccount() throws Exception {
		
		MockitoAnnotations.initMocks(this);
	}
	
	@Test
	public void testChooseMerchantAccount_GetFirstcount() {
		
		List<AuthNetAccount> authNetAccounts = Arrays.asList(
				new AuthNetAccountBuilder().isLastUsed(false).build(),
				new AuthNetAccountBuilder().id(1).loginId("first").transactionKey("firstKey").isLastUsed(false).build(),
				new AuthNetAccountBuilder().id(2).loginId("second").transactionKey("secondKey").isLastUsed(false).build(),
				new AuthNetAccountBuilder().id(3).loginId("third").transactionKey("thirdKey").isLastUsed(true).build()
				);
		
		when(authNetAccountServiceMock.getEnabled()).thenReturn(authNetAccounts);
		
		when(confMock.getString("authorise.net.login.id")).thenReturn("loginId");
		when(confMock.getString("authorise.net.transaction.key")).thenReturn("transactionKey");
		
		AuthNetAccount account = creditCardService.chooseMerchantAccount();
		
		assertTrue(account.getLoginId().equals("9yTxLt29j7Xb"));
		assertTrue(account.getTransactionKey().equals("9GJJcp75mXY93f54"));
		
		verify(authNetAccountServiceMock, times(1)).getEnabled();
		verify(authNetAccountServiceMock, times(2)).update(any(AuthNetAccount.class));
		verify(confMock, times(0)).getString(anyString());
		
	}
	
	@Before
	public void setUpChooseMerchantAccount_LastUsedNotFound() throws Exception {
		
		MockitoAnnotations.initMocks(this);
	}
	
	@Test
	public void testChooseMerchantAccount_LastUsedNotFound() {
		
		List<AuthNetAccount> authNetAccounts = Arrays.asList(
				new AuthNetAccountBuilder().isLastUsed(false).build(),
				new AuthNetAccountBuilder().id(1).loginId("first").transactionKey("firstKey").isLastUsed(false).build(),
				new AuthNetAccountBuilder().id(2).loginId("second").transactionKey("secondKey").isLastUsed(false).build(),
				new AuthNetAccountBuilder().id(3).loginId("third").transactionKey("thirdKey").isLastUsed(false).build()
				);
		
		when(authNetAccountServiceMock.getEnabled()).thenReturn(authNetAccounts);
		
		when(confMock.getString("authorise.net.login.id")).thenReturn("loginId");
		when(confMock.getString("authorise.net.transaction.key")).thenReturn("transactionKey");
		
		AuthNetAccount account = creditCardService.chooseMerchantAccount();
		
		assertTrue(account.getLoginId().equals("9yTxLt29j7Xb"));
		assertTrue(account.getTransactionKey().equals("9GJJcp75mXY93f54"));
		
		verify(authNetAccountServiceMock, times(1)).getEnabled();
		verify(authNetAccountServiceMock, times(1)).update(any(AuthNetAccount.class));
		verify(confMock, times(0)).getString(anyString());
		
	}
	
	
	@Test
	public void testValidation_Success() {
		
		CreditCardForm creditCardForm = new CreditCardBuilder().buildForm();
		
		List<ValidationError> errors = creditCardForm.validate();
		assertNull(errors);
		
	}
	
	@Test
	public void testValidation_BlankFields() {
		
		CreditCardForm creditCardForm = new CreditCardBuilder()
				.id(0l)
				.name("")
				.digits("")
				.expDate(null)
				.cardType(null)
				.cvv(0)
				.buildForm();
		
		List<ValidationError> errors = creditCardForm.validate();
		
		assertTrue(errors.size() == 10);
		assertThat(errors, containsInAnyOrder(
				new ValidationError("name", "Please enter Card Holder Name"),
				new ValidationError("cardType", "Please select Card Type"),
				new ValidationError("digits", "Please enter Card Number"),
				new ValidationError("month", "Please select Expiration Month"),
				new ValidationError("year", "Please select Expiration Year"),
				new ValidationError("cvv", "Please enter CVV code"),
				new ValidationError("digits", "Card Number should contain 15 or 16 digits"),
				new ValidationError("cvv", "Invalid CVV number"),
				new ValidationError("cvv", "CVV should contain only digits"),
				new ValidationError("month", "Expiration date is invalid")
			));
		
	}
	
	@Test
	public void testValidation_InvalidCardNumberCVVExpDate() {
		
		CreditCardForm creditCardForm = new CreditCardForm();
		creditCardForm.setId("1");
		creditCardForm.setName("Card Owner");
		creditCardForm.setDigits("123456");
		creditCardForm.setMonth("13");
		creditCardForm.setYear("132");
		creditCardForm.setCvv("1s");
		creditCardForm.setCardType("VISA");
		
		List<ValidationError> errors = creditCardForm.validate();
		
		assertTrue(errors.size() == 4);
		assertThat(errors, containsInAnyOrder(
				new ValidationError("digits", "Card Number should contain 15 or 16 digits"),
				new ValidationError("cvv", "Invalid CVV number"),
				new ValidationError("cvv", "CVV should contain only digits"),
				new ValidationError("month", "Expiration date is invalid")
			));
		
	}
	
	
	@Before
	public void setUpCharge_DefaultAccountSuccess() throws Exception {
		
		MockitoAnnotations.initMocks(this);
	}
	
	//TODO check
	@Test
	public void testCharge_DefaultAccountSuccess() {
		
		CreditCard realCreditCard = new CreditCardBuilder()
				.id(1l)
				.name("Michael Durham")
				.cardType(CardType.VISA)
				.digits("4635890004078894")
				.expDate(YearMonth.of(2020, 3))
				.cvv(146)
				.build();
		
		when(confMock.getString("authorise.net.login.id")).thenReturn(AUTH_NET_LOGIN_ID);
		when(confMock.getString("authorise.net.transaction.key")).thenReturn(AUTH_NET_TRANSACTION_KEY);
		
		AuthNetAccount account = new AuthNetAccount(AUTH_NET_LOGIN_ID, AUTH_NET_TRANSACTION_KEY);
		
		CreateTransactionResponse response = (CreateTransactionResponse)creditCardService.charge(0.01, realCreditCard, account);
		
		Logger.info("Response: " + response);
		
		JSONResponse jsonResponse = creditCardService.checkTransaction(response);
		Logger.info("JSON Response: " + jsonResponse);
		
		assertNotNull(response);
		assertTrue(response.getMessages().getResultCode() == MessageTypeEnum.OK);
		TransactionResponse result = response.getTransactionResponse();
		assertTrue(result.getResponseCode().equals("1"));
    
		verify(confMock, times(2)).getString(anyString());
		
	}
	
	
	@Before
	public void setUpCharge_InvalidCerditCard() throws Exception {
		
		MockitoAnnotations.initMocks(this);
	}
	
	//TODO finish
	@Test
	public void testCharge_InvalidCreditCard() {
		
		CreditCard realCreditCard = new CreditCardBuilder()
				.id(1l)
				.name("Invalid Owner")
				.cardType(CardType.VISA)
				.digits("1234567890123456")
				.expDate(YearMonth.of(2020, 3))
				.cvv(123)
				.build();
		
		when(confMock.getString("authorise.net.login.id")).thenReturn(AUTH_NET_LOGIN_ID);
		when(confMock.getString("authorise.net.transaction.key")).thenReturn(AUTH_NET_TRANSACTION_KEY);
		
		AuthNetAccount account = new AuthNetAccount(AUTH_NET_LOGIN_ID, AUTH_NET_TRANSACTION_KEY);
		
		CreateTransactionResponse response = (CreateTransactionResponse)creditCardService.charge(0.01, realCreditCard, account);
		
		Logger.info("Response: " + response);
		
		ErrorResponse errorResponse = (ErrorResponse)creditCardService.checkTransaction(response);
		Logger.info("JSON Response: " + errorResponse);
		
		assertNotNull(errorResponse);
		assertTrue(errorResponse.getStatus().equals("ERROR"));
		assertTrue(errorResponse.getErrorCode().equals("201"));
    
		verify(confMock, times(2)).getString(anyString());
		
	}
	
	//TODO test other methods
	
}
