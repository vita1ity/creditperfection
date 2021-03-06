package org.crama.creditperfection.test.services;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsIterableContainingInAnyOrder.containsInAnyOrder;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import org.crama.creditperfection.test.builders.CreditCardBuilder;
import org.crama.creditperfection.test.builders.DiscountBuilder;
import org.crama.creditperfection.test.builders.ProductBuilder;
import org.crama.creditperfection.test.builders.SubscriptionBuilder;
import org.crama.creditperfection.test.builders.SubscriptionFormBuilder;
import org.crama.creditperfection.test.builders.UserBuilder;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import errors.ValidationError;
import exceptions.UserAlreadySubscribedException;
import forms.SubscriptionForm;
import models.CreditCard;
import models.Discount;
import models.Product;
import models.Subscription;
import models.User;
import models.enums.DiscountType;
import models.enums.SubscriptionStatus;
import play.Configuration;
import play.Logger;
import repository.SubscriptionRepository;
import services.CreditCardService;
import services.ProductService;
import services.SubscriptionService;
import services.UserService;

public class SubscriptionServiceTest {

	@Inject
	@InjectMocks
	private SubscriptionService subscriptionService;
	
	@Mock
	private SubscriptionRepository subscriptionRepositoryMock;
	
	@Mock
	private UserService userServiceMock;

	@Mock 
	private CreditCardService creditCardServiceMock;
	
	@Mock 
	private ProductService productServiceMock;
	
	@Mock 
	private Configuration confMock;
	
	private void setUp() {
		MockitoAnnotations.initMocks(this);
	}
	
	
	@Test
	public void testCreateSubscription_NewSubscription() {
		
		setUp();
		
		User user = new UserBuilder().build();
		CreditCard creditCard = new CreditCardBuilder().build();
		Product product = new ProductBuilder().build();
		
		SubscriptionForm subscriptionForm = new SubscriptionFormBuilder()
				.id(null)
				.build(); 
		
		when(userServiceMock.getById(1)).thenReturn(user);
		when(creditCardServiceMock.getById(1)).thenReturn(creditCard);
		when(productServiceMock.getById(1)).thenReturn(product);
		
		Subscription subscription = null;
		try {
			subscription = subscriptionService.createSubscription(subscriptionForm);
		} catch (UserAlreadySubscribedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Logger.info("Subscription: " + subscription);
		
		assertTrue(subscription.getUser().equals(user));
		assertTrue(subscription.getCreditCard().equals(creditCard));
		assertTrue(subscription.getProduct().equals(product));
		assertTrue(subscription.getStatus().equals(SubscriptionStatus.ACTIVE));
		
		verify(userServiceMock, times(1)).getById(1);
		verify(creditCardServiceMock, times(1)).getById(1);
		verify(productServiceMock, times(1)).getById(1);
		
	}
	
	
	@Test
	public void testCreateSubscription_EditSubscription() {
		
		setUp();
		
		User user = new UserBuilder().build();
		CreditCard creditCard = new CreditCardBuilder().build();
		Product product = new ProductBuilder().build();
		
		Subscription subscription = new SubscriptionBuilder().build();
		
		SubscriptionForm subscriptionForm = new SubscriptionFormBuilder()
				.id("1")
				.status(null)
				.build(); 
	
		
		when(subscriptionRepositoryMock.findById(1)).thenReturn(subscription);
		when(userServiceMock.getById(1)).thenReturn(user);
		when(creditCardServiceMock.getById(1)).thenReturn(creditCard);
		when(productServiceMock.getById(1)).thenReturn(product);
		
		Subscription subscriptionResult = null;
		try {
			subscriptionResult = subscriptionService.createSubscription(subscriptionForm);
		} catch (UserAlreadySubscribedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Logger.info("Subscription: " + subscription);
		
		assertTrue(subscriptionResult.getUser().equals(user));
		assertTrue(subscriptionResult.getCreditCard().equals(creditCard));
		assertTrue(subscriptionResult.getProduct().equals(product));
		assertTrue(subscriptionResult.getStatus().equals(SubscriptionStatus.TRIAL));
		
		verify(subscriptionRepositoryMock, times(1)).findById(1);
		verify(userServiceMock, times(1)).getById(1);
		verify(creditCardServiceMock, times(1)).getById(1);
		verify(productServiceMock, times(1)).getById(1);
		
	}
	
	@Test
	public void testFindById() {
		
		setUp();
		
		Subscription testSubscription = new SubscriptionBuilder().build();
		
		when(subscriptionRepositoryMock.findById(anyLong())).thenReturn(testSubscription);
		
		Subscription subscription = subscriptionService.findById(0);
		assertTrue(subscription.getId() == 0l);
		assertTrue(subscription.getUser().equals(testSubscription.getUser()));
		assertTrue(subscription.getCreditCard().equals(testSubscription.getCreditCard()));
		assertTrue(subscription.getProduct().equals(testSubscription.getProduct()));
		
		verify(subscriptionRepositoryMock, times(1)).findById(anyLong());
		
	}
	
	
	@Test
	public void testFindByUser() {
	
		setUp();
		
		Subscription testSubscription = new SubscriptionBuilder().build();
		User user = new UserBuilder().build();
		
		when(subscriptionRepositoryMock.findByUser(any(User.class))).thenReturn(testSubscription);
		
		Subscription subscription = subscriptionService.findByUser(user);
		
		Logger.info("Subscription: " + subscription);
		
		assertTrue(subscription.getId() == testSubscription.getId());
		assertTrue(subscription.getUser().equals(testSubscription.getUser()));
		assertTrue(subscription.getCreditCard().equals(testSubscription.getCreditCard()));
		assertTrue(subscription.getProduct().equals(testSubscription.getProduct()));
		
		verify(subscriptionRepositoryMock, times(1)).findByUser(any(User.class));
		
	}
	
	
	//TODO fix test PgedList
	@Ignore
	@Test
	public void testFindByStatus() {
	
		setUp();
		
		List<Subscription> testSubscriptions = Arrays.asList(
				new SubscriptionBuilder().build(),
				new SubscriptionBuilder().id(1l).build(),
				new SubscriptionBuilder().id(2l).build());
		
		when(subscriptionRepositoryMock.findByStatus(SubscriptionStatus.ACTIVE, 0, 10).getList()).thenReturn(testSubscriptions);
		
		List<Subscription> activeSubscriptions = subscriptionService.findByStatus(SubscriptionStatus.ACTIVE, 0, 10).getList();
		
		assertTrue(activeSubscriptions.size() == 3);
		int i = 0;
		for (Subscription s: activeSubscriptions) {
			assertTrue(s.getId() == i);
			++i;
		}
		
		verify(subscriptionRepositoryMock, times(1)).findByStatus(SubscriptionStatus.ACTIVE, 0, 10).getList();
		
	}
	
	
	@Test
	public void testFindExcludingStatus() {
	
		setUp();
		
		List<Subscription> testSubscriptions = Arrays.asList(
				new SubscriptionBuilder().build(),
				new SubscriptionBuilder().id(1l).build(),
				new SubscriptionBuilder().id(2l).build());
		
		when(subscriptionRepositoryMock.findExcludingStatus(SubscriptionStatus.TRIAL)).thenReturn(testSubscriptions);
		
		List<Subscription> notTrialSubscriptions = subscriptionService.findExcludingStatus(SubscriptionStatus.TRIAL);
		
		assertTrue(notTrialSubscriptions.size() == 3);
		int i = 0;
		for (Subscription s: notTrialSubscriptions) {
			assertTrue(s.getId() == i);
			++i;
		}
		
		verify(subscriptionRepositoryMock, times(1)).findExcludingStatus(SubscriptionStatus.TRIAL);
		
	}
	
	
	@Test
	public void testFindAll() {
	
		setUp();
		
		List<Subscription> testSubscriptions = Arrays.asList(
				new SubscriptionBuilder().build(),
				new SubscriptionBuilder().id(1l).build(),
				new SubscriptionBuilder().id(2l).build());
		
		when(subscriptionRepositoryMock.findAll()).thenReturn(testSubscriptions);
		
		List<Subscription> allSubscriptions = subscriptionService.findAll();
		
		assertTrue(allSubscriptions.size() == 3);
		int i = 0;
		for (Subscription s: allSubscriptions) {
			assertTrue(s.getId() == i);
			++i;
		}
		
		verify(subscriptionRepositoryMock, times(1)).findAll();
		
	}
	
	@Test
	public void testValidation_Success() {
		
		SubscriptionForm subscriptionForm = new SubscriptionFormBuilder().build();
		
		List<ValidationError> errors = subscriptionForm.validate();
		
		assertNull(errors);	
		
	}
	
	@Test
	public void testValidation_emptyFields() {
		
		SubscriptionForm subscriptionForm = new SubscriptionFormBuilder()
				.userId("")
				.creditCardId("")
				.productId("")
				.build();
			
		List<ValidationError> errors = subscriptionForm.validate();
		
		assertTrue(errors.size() == 3);
		assertThat(errors, containsInAnyOrder(
				new ValidationError("user", "Please choose User"),
				new ValidationError("creditCard", "Please choose Credit Card"),
				new ValidationError("product", "Please choose Product")));
	
	}
				
	@Before
	public void setUpValidation_AlreadySubscribedError() {
		
		MockitoAnnotations.initMocks(this);
		
	}
	//TODO alreadySubscribed
	
	
	@Test
	public void testCheckExpire_trialAndExpired() {
		
		setUp();
		//when(confMock.getInt("creditperfection.trial.days")).thenReturn(7);
		
		LocalDateTime today = LocalDateTime.now();
		LocalDateTime subscriptionDate = today.minusDays(7);
		
		Logger.info("today: " + today);
		Logger.info("subscriptionDate: " + subscriptionDate);
		
		Subscription testSubscription = new SubscriptionBuilder()
											.status(SubscriptionStatus.TRIAL)
											.subscriptionDate(subscriptionDate)
											.build();
		
		Logger.info("testSubscription: " + testSubscription);
		
		boolean isExpired = subscriptionService.checkExpired(testSubscription);
		
		assertTrue(isExpired);
		
		//verify(confMock, times(1)).getInt("creditperfection.trial.days");
		
	}
	
	
	@Test
	public void testCheckExpire_trialAndNotExpired() {
	
		setUp();
		
		//when(confMock.getInt("creditperfection.trial.days")).thenReturn(7);
		
		LocalDateTime today = LocalDateTime.now();
		LocalDateTime subscriptionDate = today.minusDays(7).plusHours(1);
		
		Logger.info("today: " + today);
		Logger.info("subscriptionDate: " + subscriptionDate);
		
		Subscription testSubscription = new SubscriptionBuilder()
											.status(SubscriptionStatus.TRIAL)
											.subscriptionDate(subscriptionDate)
											.build();
		
		Logger.info("testSubscription: " + testSubscription);
		
		boolean isExpired = subscriptionService.checkExpired(testSubscription);
		
		assertFalse(isExpired);
		
		//verify(confMock, times(1)).getInt("creditperfection.trial.days");
		
	}
	
	
	@Test
	public void testCheckExpire_activeAndExpired() {
	
		setUp();
		
		LocalDateTime today = LocalDateTime.now();
		int daysInMonth = today.getMonth().minus(1).length(true);
		
		LocalDateTime lastChargeDate = today.minusDays(daysInMonth);
		
		
		Logger.info("today: " + today);
		Logger.info("lastChargeDate: " + lastChargeDate);
		
		Subscription testSubscription = new SubscriptionBuilder()
											.status(SubscriptionStatus.ACTIVE)
											.lastChargeDate(lastChargeDate)
											.build();
		
		Logger.info("testSubscription: " + testSubscription);
		
		boolean isExpired = subscriptionService.checkExpired(testSubscription);
		
		assertTrue(isExpired);
		
		
	}
	
	
	@Test
	public void testCheckExpire_activeAndNotExpired() {
	
		setUp();
		
		LocalDateTime today = LocalDateTime.now();
		int daysInMonth = today.getMonth().minus(1).length(true);
		
		LocalDateTime lastChargeDate = today.minusDays(daysInMonth).plusMinutes(1);
		
		
		Logger.info("today: " + today);
		Logger.info("lastChargeDate: " + lastChargeDate);
		
		Subscription testSubscription = new SubscriptionBuilder()
											.status(SubscriptionStatus.ACTIVE)
											.lastChargeDate(lastChargeDate)
											.build();
		
		Logger.info("testSubscription: " + testSubscription);
		
		boolean isExpired = subscriptionService.checkExpired(testSubscription);
		
		assertFalse(isExpired);
		
		
	}
	
	@Test
	public void testCheckExpire_activeWeeklyDiscountExpired() {
	
		setUp();
		
		LocalDateTime today = LocalDateTime.now();
		
		LocalDateTime lastChargeDate = today.minusDays(7);
		
		Logger.info("today: " + today);
		Logger.info("lastChargeDate: " + lastChargeDate);
		
		Discount discount = new DiscountBuilder().build();
		Subscription testSubscription = new SubscriptionBuilder()
											.status(SubscriptionStatus.ACTIVE)
											.lastChargeDate(lastChargeDate)
											.discount(discount)
											.build();
		
		
		Logger.info("testSubscription: " + testSubscription);
		
		boolean isExpired = subscriptionService.checkExpired(testSubscription);
		
		assertTrue(isExpired);
		
		
	}
	
	@Test
	public void testCheckExpire_activeWeeklyDiscountNotExpired() {
		
		setUp();
		
		LocalDateTime today = LocalDateTime.now();
		
		LocalDateTime lastChargeDate = today.minusDays(7).plusMinutes(1);
		
		Logger.info("today: " + today);
		Logger.info("lastChargeDate: " + lastChargeDate);
		
		Discount discount = new DiscountBuilder().build();
		Subscription testSubscription = new SubscriptionBuilder()
											.status(SubscriptionStatus.ACTIVE)
											.lastChargeDate(lastChargeDate)
											.discount(discount)
											.build();
		
		
		Logger.info("testSubscription: " + testSubscription);
		
		boolean isExpired = subscriptionService.checkExpired(testSubscription);
		
		assertFalse(isExpired);
		
		
	}
	
	@Test
	public void testCheckExpire_yearProductExpired() {
		
		setUp();
		
		LocalDateTime today = LocalDateTime.now();
		
		int daysInMonth = today.getMonth().minus(1).length(true);
		LocalDateTime lastChargeDate = today.minusDays(daysInMonth);
		
		Logger.info("today: " + today);
		Logger.info("lastChargeDate: " + lastChargeDate);
		
		Discount discount = new DiscountBuilder()
								.discountType(DiscountType.MONTHLY_DISCOUNT)
								.build();
		Product product = new ProductBuilder()
							.trialPeriod(365)
							.build();
		Subscription testSubscription = new SubscriptionBuilder()
											.status(SubscriptionStatus.ACTIVE)
											.lastChargeDate(lastChargeDate)
											.discount(discount)
											.product(product)
											.build();
		
		Logger.info("testSubscription: " + testSubscription);
		
		boolean isExpired = subscriptionService.checkExpired(testSubscription);
		
		assertTrue(isExpired);
		
		
	}
	
	@Test
	public void testCheckExpire_yearProductNotExpired() {
		
		setUp();
		
		LocalDateTime today = LocalDateTime.now();
		
		int daysInMonth = today.getMonth().minus(1).length(true);
		LocalDateTime lastChargeDate = today.minusDays(daysInMonth).plusMinutes(1);
		
		Logger.info("today: " + today);
		Logger.info("lastChargeDate: " + lastChargeDate);
		
		Discount discount = new DiscountBuilder()
								.discountType(DiscountType.MONTHLY_DISCOUNT)
								.build();
		Product product = new ProductBuilder()
							.trialPeriod(365)
							.build();
		Subscription testSubscription = new SubscriptionBuilder()
											.status(SubscriptionStatus.ACTIVE)
											.lastChargeDate(lastChargeDate)
											.discount(discount)
											.product(product)
											.build();
		
		
		Logger.info("testSubscription: " + testSubscription);
		
		boolean isExpired = subscriptionService.checkExpired(testSubscription);
		
		assertFalse(isExpired);
		
		
	}
	
	@Test
	public void testCheckTrialEnded_True() {
		
		LocalDateTime today = LocalDateTime.now();
		
		LocalDateTime subscriptionDate = today.minusDays(365);
		
		Logger.info("today: " + today);
		Logger.info("subscriptionDate: " + subscriptionDate);
		
		Product product = new ProductBuilder()
							.trialPeriod(365)
							.build();
		Subscription testSubscription = new SubscriptionBuilder()
											.status(SubscriptionStatus.ACTIVE)
											.subscriptionDate(subscriptionDate)
											.product(product)
											.build();
		
		
		Logger.info("testSubscription: " + testSubscription);
		
		boolean isExpired = subscriptionService.checkTrialEnded(testSubscription);
		
		assertTrue(isExpired);
		
		
	}
	
	@Test
	public void testCheckTrialEnded_False() {
		
		LocalDateTime today = LocalDateTime.now();
		
		LocalDateTime subscriptionDate = today.minusDays(365).plusMinutes(1);
		
		Logger.info("today: " + today);
		Logger.info("subscriptionDate: " + subscriptionDate);
		
		Product product = new ProductBuilder()
							.trialPeriod(365)
							.build();
		Subscription testSubscription = new SubscriptionBuilder()
											.status(SubscriptionStatus.ACTIVE)
											.subscriptionDate(subscriptionDate)
											.product(product)
											.build();
		
		
		Logger.info("testSubscription: " + testSubscription);
		
		boolean isExpired = subscriptionService.checkTrialEnded(testSubscription);
		
		assertFalse(isExpired);
		
		
	}
		
}
