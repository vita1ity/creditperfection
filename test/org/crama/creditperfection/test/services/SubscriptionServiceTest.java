package org.crama.creditperfection.test.services;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.hamcrest.collection.IsIterableContainingInAnyOrder.containsInAnyOrder;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import org.crama.creditperfection.test.builders.CreditCardBuilder;
import org.crama.creditperfection.test.builders.ProductBuilder;
import org.crama.creditperfection.test.builders.SubscriptionBuilder;
import org.crama.creditperfection.test.builders.SubscriptionFormBuilder;
import org.crama.creditperfection.test.builders.UserBuilder;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import errors.ValidationError;
import forms.SubscriptionForm;
import models.CreditCard;
import models.Product;
import models.Subscription;
import models.User;
import models.enums.SubscriptionStatus;
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
	
	@Before
	public void setUpCreateSubscription_NewSubscription() {
		
		MockitoAnnotations.initMocks(this);
		
	}
	
	@Test
	public void testCreateSubscription_NewSubscription() {
		
		User user = new UserBuilder().build();
		CreditCard creditCard = new CreditCardBuilder().build();
		Product product = new ProductBuilder().build();
		
		SubscriptionForm subscriptionForm = new SubscriptionFormBuilder()
				.id(null)
				.build(); 
		
		when(userServiceMock.getById(1)).thenReturn(user);
		when(creditCardServiceMock.getById(1)).thenReturn(creditCard);
		when(productServiceMock.getById(1)).thenReturn(product);
		
		Subscription subscription = subscriptionService.createSubscription(subscriptionForm);
		
		Logger.info("Subscription: " + subscription);
		
		assertTrue(subscription.getUser().equals(user));
		assertTrue(subscription.getCreditCard().equals(creditCard));
		assertTrue(subscription.getProduct().equals(product));
		assertTrue(subscription.getStatus().equals(SubscriptionStatus.ACTIVE));
		
		verify(userServiceMock, times(1)).getById(1);
		verify(creditCardServiceMock, times(1)).getById(1);
		verify(productServiceMock, times(1)).getById(1);
		
	}
	
	@Before
	public void setUpCreateSubscription_EditSubscription() {
		
		MockitoAnnotations.initMocks(this);
		
	}
	
	@Test
	public void testCreateSubscription_EditSubscription() {
		
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
		
		Subscription subscriptionResult = subscriptionService.createSubscription(subscriptionForm);
		
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
	
	@Before
	public void setUpFindById() throws Exception {
		
		MockitoAnnotations.initMocks(this);
	}
	
	@Test
	public void testFindById() {
		
		Subscription testSubscription = new SubscriptionBuilder().build();
		
		when(subscriptionRepositoryMock.findById(anyLong())).thenReturn(testSubscription);
		
		Subscription subscription = subscriptionService.findById(0);
		assertTrue(subscription.getId() == 0l);
		assertTrue(subscription.getUser().equals(testSubscription.getUser()));
		assertTrue(subscription.getCreditCard().equals(testSubscription.getCreditCard()));
		assertTrue(subscription.getProduct().equals(testSubscription.getProduct()));
		
		verify(subscriptionRepositoryMock, times(1)).findById(anyLong());
		
	}
	
	@Before
	public void setUpFindByUser() throws Exception {
		
		MockitoAnnotations.initMocks(this);
		
	}
	
	@Test
	public void testFindByUser() {
		
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
	
	@Before
	public void setUpFindByStatus() throws Exception {
		
		MockitoAnnotations.initMocks(this);
		
	}
	
	@Test
	public void testFindByStatus() {
		
		List<Subscription> testSubscriptions = Arrays.asList(
				new SubscriptionBuilder().build(),
				new SubscriptionBuilder().id(1l).build(),
				new SubscriptionBuilder().id(2l).build());
		
		when(subscriptionRepositoryMock.findByStatus(SubscriptionStatus.ACTIVE)).thenReturn(testSubscriptions);
		
		List<Subscription> activeSubscriptions = subscriptionService.findByStatus(SubscriptionStatus.ACTIVE);
		
		assertTrue(activeSubscriptions.size() == 3);
		int i = 0;
		for (Subscription s: activeSubscriptions) {
			assertTrue(s.getId() == i);
			++i;
		}
		
		verify(subscriptionRepositoryMock, times(1)).findByStatus(SubscriptionStatus.ACTIVE);
		
	}
	
	@Before
	public void setUpFindExcludingStatus() throws Exception {
		
		MockitoAnnotations.initMocks(this);
		
	}
	
	@Test
	public void testFindExcludingStatus() {
		
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
	
	@Before
	public void setUpFindAll() throws Exception {
		
		MockitoAnnotations.initMocks(this);
		
	}
	
	@Test
	public void testFindAll() {
		
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
	
	@Test
	public void testValidation_AlreadySubscribedError() {
		
		SubscriptionForm subscriptionForm = new SubscriptionFormBuilder()
				.id(null)
				.build();
		
		subscriptionForm.setSubscriptionService(subscriptionService);
		subscriptionForm.setUserService(userServiceMock);
		
		Subscription testSubscription = new SubscriptionBuilder().build();
		User testUser = new UserBuilder().build();
		
		when(userServiceMock.getById(anyLong())).thenReturn(testUser);
		when(subscriptionRepositoryMock.findByUser(any(User.class))).thenReturn(testSubscription);
			
		List<ValidationError> errors = subscriptionForm.validate();
		
		assertTrue(errors.size() == 1);
		assertThat(errors, containsInAnyOrder(
				new ValidationError("user", "User is already subscribed")));
		
		verify(userServiceMock, times(1)).getById(anyLong());
		verify(subscriptionRepositoryMock, times(1)).findByUser(any(User.class));
	}	
	
	
}
