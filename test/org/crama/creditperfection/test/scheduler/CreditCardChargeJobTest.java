package org.crama.creditperfection.test.scheduler;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyDouble;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import org.crama.creditperfection.test.builders.DiscountBuilder;
import org.crama.creditperfection.test.builders.ProductBuilder;
import org.crama.creditperfection.test.builders.SubscriptionBuilder;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import models.CreditCard;
import models.Discount;
import models.Product;
import models.Subscription;
import models.Transaction;
import models.enums.DiscountStatus;
import models.enums.DiscountType;
import models.enums.SubscriptionStatus;
import models.json.ErrorResponse;
import models.json.MessageResponse;
import net.authorize.api.contract.v1.CreateTransactionResponse;
import play.Logger;
import scheduler.CreditCardChargeJob;
import services.CreditCardService;
import services.DiscountService;
import services.MailService;
import services.SubscriptionService;
import services.TransactionService;
import services.UserService;

public class CreditCardChargeJobTest {

	@Inject
	@InjectMocks
	private CreditCardChargeJob creditCardChargeJob;
	
	@Mock
	private CreditCardService creditCardServiceMock;
	
	@Mock
	private SubscriptionService subscriptionServiceMock;
	
	@Mock
	private DiscountService discountServiceMock;
	
	@Mock
	private MailService mailServiceMock;
	
	@Mock
	private UserService userServiceMock;
	
	@Mock
	private TransactionService transactionServiceMock;
	
	private void setUp() {
		MockitoAnnotations.initMocks(this);
	}
	
	@Test
	public void testNoneExpired() {
		
		setUp();
		
		Logger.info("None Expired: ");
		
		List<Subscription> activeSubscriptions = Arrays.asList(
				new SubscriptionBuilder().build(),
				new SubscriptionBuilder().id(1l).build(),
				new SubscriptionBuilder().id(2l).build());
		
		when(subscriptionServiceMock.findExcludingStatus(SubscriptionStatus.CANCELLED)).thenReturn(activeSubscriptions);
		
		when(subscriptionServiceMock.checkExpired(any(Subscription.class))).thenReturn(false);
		
		creditCardChargeJob.run();
		
		verify(subscriptionServiceMock, times(1)).findExcludingStatus(SubscriptionStatus.CANCELLED);
		verify(subscriptionServiceMock, times(activeSubscriptions.size())).checkExpired(any(Subscription.class));
		verify(discountServiceMock, times(0)).checkDiscountExpired(any(Discount.class));
		
	}
	
	@Test
	public void testExpired_NoDiscountSuccessTrial() {
		
		setUp();
		
		Logger.info("No Discount Success Trial: ");
		
		List<Subscription> activeSubscriptions = Arrays.asList(
				new SubscriptionBuilder()
				.status(SubscriptionStatus.TRIAL)
				.build());
		
		when(subscriptionServiceMock.checkTrialEnded(activeSubscriptions.get(0))).thenReturn(true);
		when(subscriptionServiceMock.findExcludingStatus(SubscriptionStatus.CANCELLED)).thenReturn(activeSubscriptions);
		when(subscriptionServiceMock.checkExpired(any(Subscription.class))).thenReturn(true);
		CreateTransactionResponse response = null;
		when(creditCardServiceMock.charge(anyDouble(), any(CreditCard.class))).thenReturn(response);
		when(creditCardServiceMock.checkTransaction(response)).thenReturn(new MessageResponse("SUCCESS", "Successful Credit Card Transaction"));
		String transactionId = "123456789";
		when(creditCardServiceMock.getTransactionId(response)).thenReturn(transactionId);
		
		creditCardChargeJob.run();
		
		double price = activeSubscriptions.get(0).getProduct().getPrice();
		
		Subscription s = activeSubscriptions.get(0);
		assertEquals(s.getLastChargeDate().toLocalDate(), LocalDate.now());
		assertEquals(s.getStatus(), SubscriptionStatus.ACTIVE);
	
		verify(subscriptionServiceMock, times(2)).checkTrialEnded(activeSubscriptions.get(0));
		verify(subscriptionServiceMock, times(1)).findExcludingStatus(SubscriptionStatus.CANCELLED);
		verify(subscriptionServiceMock, times(activeSubscriptions.size())).checkExpired(any(Subscription.class));
		verify(discountServiceMock, times(0)).checkDiscountExpired(any(Discount.class));
		verify(creditCardServiceMock, times(1)).charge(eq(price), any(CreditCard.class));
		verify(creditCardServiceMock, times(1)).checkTransaction(eq(response));
		verify(creditCardServiceMock, times(1)).getTransactionId(eq(response));
		verify(subscriptionServiceMock, times(1)).update(any(Subscription.class));
		verify(transactionServiceMock, times(1)).save(any(Transaction.class));
		
	}
	
	@Test
	public void testExpired_NoDiscountSuccessTrialNotEnded() {
		
		setUp();
		
		Logger.info("No Discount Success Trial Not Ended: ");
		
		Product product = new ProductBuilder()
				.trialPeriod(365)
				.price(19.99)
				.salePrice(9.99)
				.build();
		
		List<Subscription> activeSubscriptions = Arrays.asList(
				new SubscriptionBuilder()
				.status(SubscriptionStatus.TRIAL)
				.product(product)
				.build());
		
		when(subscriptionServiceMock.findExcludingStatus(SubscriptionStatus.CANCELLED)).thenReturn(activeSubscriptions);
		when(subscriptionServiceMock.checkExpired(any(Subscription.class))).thenReturn(true);
		CreateTransactionResponse response = null;
		when(creditCardServiceMock.charge(anyDouble(), any(CreditCard.class))).thenReturn(response);
		when(creditCardServiceMock.checkTransaction(response)).thenReturn(new MessageResponse("SUCCESS", "Successful Credit Card Transaction"));
		String transactionId = "123456789";
		when(creditCardServiceMock.getTransactionId(response)).thenReturn(transactionId);
		
		creditCardChargeJob.run();
		
		double price = activeSubscriptions.get(0).getProduct().getSalePrice();
		
		Subscription s = activeSubscriptions.get(0);
		assertEquals(s.getLastChargeDate().toLocalDate(), LocalDate.now());
		assertEquals(s.getStatus(), SubscriptionStatus.TRIAL);
	
		
		verify(subscriptionServiceMock, times(1)).findExcludingStatus(SubscriptionStatus.CANCELLED);
		verify(subscriptionServiceMock, times(activeSubscriptions.size())).checkExpired(any(Subscription.class));
		verify(discountServiceMock, times(0)).checkDiscountExpired(any(Discount.class));
		verify(creditCardServiceMock, times(1)).charge(eq(price), any(CreditCard.class));
		verify(creditCardServiceMock, times(1)).checkTransaction(eq(response));
		verify(creditCardServiceMock, times(1)).getTransactionId(eq(response));
		verify(subscriptionServiceMock, times(1)).update(any(Subscription.class));
		verify(transactionServiceMock, times(1)).save(any(Transaction.class));
		
	}
	
	@Test
	public void testExpired_NoDiscountFailedActive() {
		
		setUp();
	
		Logger.info("No Discount Failed Active: ");
		
		List<Subscription> activeSubscriptions = Arrays.asList(
				new SubscriptionBuilder()
				.status(SubscriptionStatus.ACTIVE)
				.build());
		when(subscriptionServiceMock.checkTrialEnded(activeSubscriptions.get(0))).thenReturn(true);
		when(subscriptionServiceMock.findExcludingStatus(SubscriptionStatus.CANCELLED)).thenReturn(activeSubscriptions);
		when(subscriptionServiceMock.checkExpired(any(Subscription.class))).thenReturn(true);
		CreateTransactionResponse response = null;
		when(creditCardServiceMock.charge(anyDouble(), any(CreditCard.class))).thenReturn(response);
		when(creditCardServiceMock.checkTransaction(response)).thenReturn(new ErrorResponse("ERROR", "201", "Transaction Failed"));
		
		creditCardChargeJob.run();
		
		double price = activeSubscriptions.get(0).getProduct().getPrice();
		
		Subscription s = activeSubscriptions.get(0);
		assertEquals(s.getStatus(), SubscriptionStatus.RENEW_FAILED);
		assertTrue(s.getUser().getActive());
	
		verify(subscriptionServiceMock, times(1)).checkTrialEnded(activeSubscriptions.get(0));
		verify(subscriptionServiceMock, times(1)).findExcludingStatus(SubscriptionStatus.CANCELLED);
		verify(subscriptionServiceMock, times(activeSubscriptions.size())).checkExpired(any(Subscription.class));
		verify(discountServiceMock, times(0)).checkDiscountExpired(any(Discount.class));
		verify(creditCardServiceMock, times(1)).charge(eq(price), any(CreditCard.class));
		verify(creditCardServiceMock, times(1)).checkTransaction(eq(response));
		verify(subscriptionServiceMock, times(1)).update(any(Subscription.class));
		verify(userServiceMock, times(0)).update(eq(s.getUser()));
		verify(mailServiceMock, times(0)).sendTransactionFailedNotification(eq(s.getUser()));
		
	}
	
	@Test
	public void testExpired_ActiveDiscountExpiredAfterUsageDiscountAmountZeroWeekly() {
		
		setUp();
		
		Discount discount = new DiscountBuilder().build();
		
		List<Subscription> activeSubscriptions = Arrays.asList(
				new SubscriptionBuilder()
				.status(SubscriptionStatus.TRIAL)
				.discount(discount)
				.build());
		
		when(subscriptionServiceMock.checkTrialEnded(activeSubscriptions.get(0))).thenReturn(false);
		when(subscriptionServiceMock.findExcludingStatus(SubscriptionStatus.CANCELLED)).thenReturn(activeSubscriptions);
		when(subscriptionServiceMock.checkExpired(any(Subscription.class))).thenReturn(true);
		when(discountServiceMock.checkDiscountExpired(any(Discount.class))).thenReturn(true);
		
		creditCardChargeJob.run();
		
		Subscription s = activeSubscriptions.get(0);
		assertEquals(s.getLastChargeDate().toLocalDate(), LocalDate.now());
		assertEquals(s.getStatus(), SubscriptionStatus.TRIAL);
		assertEquals(s.getDiscount().getDiscountStatus(), DiscountStatus.USED);
	
		verify(subscriptionServiceMock, times(1)).checkTrialEnded(activeSubscriptions.get(0));
		verify(subscriptionServiceMock, times(1)).findExcludingStatus(SubscriptionStatus.CANCELLED);
		verify(subscriptionServiceMock, times(activeSubscriptions.size())).checkExpired(any(Subscription.class));
		verify(discountServiceMock, times(1)).checkDiscountExpired(any(Discount.class));
		
		verify(subscriptionServiceMock, times(1)).update(any(Subscription.class));
	}
	
	@Test
	public void testExpired_ActiveDiscountNotExpiredDiscountAmountZeroMonthly() {
		
		setUp();
		
		Discount discount = new DiscountBuilder()
				.discountType(DiscountType.MONTHLY_DISCOUNT)
				.build();
		
		List<Subscription> activeSubscriptions = Arrays.asList(
				new SubscriptionBuilder()
				.status(SubscriptionStatus.TRIAL)
				.discount(discount)
				.build());
		
		when(subscriptionServiceMock.checkTrialEnded(activeSubscriptions.get(0))).thenReturn(true);
		when(subscriptionServiceMock.findExcludingStatus(SubscriptionStatus.CANCELLED)).thenReturn(activeSubscriptions);
		when(subscriptionServiceMock.checkExpired(any(Subscription.class))).thenReturn(true);
		when(discountServiceMock.checkDiscountExpired(any(Discount.class))).thenReturn(false);
		
		creditCardChargeJob.run();
		
		Subscription s = activeSubscriptions.get(0);
		assertEquals(s.getLastChargeDate().toLocalDate(), LocalDate.now());
		assertEquals(s.getStatus(), SubscriptionStatus.ACTIVE);
		assertEquals(s.getDiscount().getDiscountStatus(), DiscountStatus.ACTIVE);
	
		verify(subscriptionServiceMock, times(1)).checkTrialEnded(activeSubscriptions.get(0));
		verify(subscriptionServiceMock, times(1)).findExcludingStatus(SubscriptionStatus.CANCELLED);
		verify(subscriptionServiceMock, times(activeSubscriptions.size())).checkExpired(any(Subscription.class));
		verify(discountServiceMock, times(1)).checkDiscountExpired(any(Discount.class));
		
		verify(subscriptionServiceMock, times(1)).update(any(Subscription.class));
	}
	
	@Test
	public void testExpired_ActiveDiscountNotExpiredDiscountAmountNotZeroYearly() {
		
		setUp();
		
		final double discountAmount = 9.95;
		
		Discount discount = new DiscountBuilder()
				.discountType(DiscountType.YEARLY_DISCOUNT)
				.discountAmount(discountAmount)
				.build();
		
		List<Subscription> activeSubscriptions = Arrays.asList(
				new SubscriptionBuilder()
				.status(SubscriptionStatus.TRIAL)
				.discount(discount)
				.build());

		when(subscriptionServiceMock.checkTrialEnded(activeSubscriptions.get(0))).thenReturn(true);
		when(subscriptionServiceMock.findExcludingStatus(SubscriptionStatus.CANCELLED)).thenReturn(activeSubscriptions);
		when(subscriptionServiceMock.checkExpired(any(Subscription.class))).thenReturn(true);
		when(discountServiceMock.checkDiscountExpired(any(Discount.class))).thenReturn(false);
		CreateTransactionResponse response = null;
		when(creditCardServiceMock.charge(anyDouble(), any(CreditCard.class))).thenReturn(response);
		when(creditCardServiceMock.checkTransaction(response)).thenReturn(new MessageResponse("SUCCESS", "Successful Credit Card Transaction"));
		String transactionId = "123456789";
		when(creditCardServiceMock.getTransactionId(response)).thenReturn(transactionId);
		
		
		creditCardChargeJob.run();
		
		Subscription s = activeSubscriptions.get(0);
		assertEquals(s.getLastChargeDate().toLocalDate(), LocalDate.now());
		assertEquals(s.getStatus(), SubscriptionStatus.ACTIVE);
		assertEquals(s.getDiscount().getDiscountStatus(), DiscountStatus.ACTIVE);
	

		verify(subscriptionServiceMock, times(1)).checkTrialEnded(activeSubscriptions.get(0));
		verify(subscriptionServiceMock, times(1)).findExcludingStatus(SubscriptionStatus.CANCELLED);
		verify(subscriptionServiceMock, times(activeSubscriptions.size())).checkExpired(any(Subscription.class));
		verify(discountServiceMock, times(1)).checkDiscountExpired(any(Discount.class));
		verify(creditCardServiceMock, times(1)).charge(eq(discountAmount), any(CreditCard.class));
		verify(creditCardServiceMock, times(1)).checkTransaction(eq(response));
		verify(creditCardServiceMock, times(1)).getTransactionId(eq(response));
		verify(subscriptionServiceMock, times(1)).update(any(Subscription.class));
		verify(transactionServiceMock, times(1)).save(any(Transaction.class));
	}

	@Test
	public void testExpired_NoDiscountFailedSecondTime() {
		
		setUp();
	
		Logger.info("No Discount Failed Second Time: ");
		
		List<Subscription> activeSubscriptions = Arrays.asList(
				new SubscriptionBuilder()
				.status(SubscriptionStatus.RENEW_FAILED)
				.build());
		when(subscriptionServiceMock.checkTrialEnded(activeSubscriptions.get(0))).thenReturn(true);
		when(subscriptionServiceMock.findExcludingStatus(SubscriptionStatus.CANCELLED)).thenReturn(activeSubscriptions);
		when(subscriptionServiceMock.checkExpired(any(Subscription.class))).thenReturn(true);
		CreateTransactionResponse response = null;
		when(creditCardServiceMock.charge(anyDouble(), any(CreditCard.class))).thenReturn(response);
		when(creditCardServiceMock.checkTransaction(response)).thenReturn(new ErrorResponse("ERROR", "201", "Transaction Failed"));
		
		creditCardChargeJob.run();
		
		double price = activeSubscriptions.get(0).getProduct().getPrice();
		
		Subscription s = activeSubscriptions.get(0);
		assertEquals(s.getStatus(), SubscriptionStatus.CANCELLED);
		assertFalse(s.getUser().getActive());
	
		verify(subscriptionServiceMock, times(1)).checkTrialEnded(activeSubscriptions.get(0));
		verify(subscriptionServiceMock, times(1)).findExcludingStatus(SubscriptionStatus.CANCELLED);
		verify(subscriptionServiceMock, times(activeSubscriptions.size())).checkExpired(any(Subscription.class));
		verify(discountServiceMock, times(0)).checkDiscountExpired(any(Discount.class));
		verify(creditCardServiceMock, times(1)).charge(eq(price), any(CreditCard.class));
		verify(creditCardServiceMock, times(1)).checkTransaction(eq(response));
		verify(subscriptionServiceMock, times(1)).update(any(Subscription.class));
		verify(userServiceMock, times(1)).update(eq(s.getUser()));
		verify(mailServiceMock, times(1)).sendTransactionFailedNotification(eq(s.getUser()));
		
	}
	
	@Test
	public void testExpired_FailedToRenewSecondDay() {
		
		setUp();
	
		Logger.info("Failed To Renew Second Day: ");
		
		LocalDate renewFailedDate = LocalDate.now().minusDays(1);
		
		List<Subscription> failedSubscriptions = Arrays.asList(
				new SubscriptionBuilder()
				.status(SubscriptionStatus.CANCELLED)
				.renewFailedDate(renewFailedDate)
				.build());
		
		LocalDate startDate = LocalDate.now().minusDays(3);
		LocalDate endDate = LocalDate.now().minusDays(1);
		
		when(subscriptionServiceMock.findFailedToRenew(startDate, endDate)).thenReturn(failedSubscriptions);
		when(subscriptionServiceMock.findExcludingStatus(SubscriptionStatus.CANCELLED)).thenReturn(null);
		
		creditCardChargeJob.run();
		
		Subscription s = failedSubscriptions.get(0);
	
		verify(subscriptionServiceMock, times(1)).findExcludingStatus(SubscriptionStatus.CANCELLED);
		verify(subscriptionServiceMock, times(1)).findFailedToRenew(startDate, endDate);
		verify(mailServiceMock, times(1)).sendFreeWeekOffer(eq(s.getUser()), eq(s.getRenewFailedDate()));
		
	} 
	
	@Test
	public void testExpired_FailedToRenewThirdDay() {
		
		setUp();
	
		Logger.info("Failed To Renew Third Day: ");
		
		LocalDate renewFailedDate = LocalDate.now().minusDays(2);
		
		List<Subscription> failedSubscriptions = Arrays.asList(
				new SubscriptionBuilder()
				.status(SubscriptionStatus.CANCELLED)
				.renewFailedDate(renewFailedDate)
				.build());
		
		LocalDate startDate = LocalDate.now().minusDays(3);
		LocalDate endDate = LocalDate.now().minusDays(1);
		
		when(subscriptionServiceMock.findFailedToRenew(startDate, endDate)).thenReturn(failedSubscriptions);
		when(subscriptionServiceMock.findExcludingStatus(SubscriptionStatus.CANCELLED)).thenReturn(null);
		
		creditCardChargeJob.run();
		
		Subscription s = failedSubscriptions.get(0);
	
		verify(subscriptionServiceMock, times(1)).findExcludingStatus(SubscriptionStatus.CANCELLED);
		verify(subscriptionServiceMock, times(1)).findFailedToRenew(startDate, endDate);
		verify(mailServiceMock, times(1)).sendFreeMonthOffer(eq(s.getUser()), eq(s.getRenewFailedDate()));
		
	} 
	
	@Test
	public void testExpired_FailedToRenewForthDay() {
		
		setUp();
	
		Logger.info("Failed To Renew Third Day: ");
		
		LocalDate renewFailedDate = LocalDate.now().minusDays(3);
		
		List<Subscription> failedSubscriptions = Arrays.asList(
				new SubscriptionBuilder()
				.status(SubscriptionStatus.CANCELLED)
				.renewFailedDate(renewFailedDate)
				.build());
		
		LocalDate startDate = LocalDate.now().minusDays(3);
		LocalDate endDate = LocalDate.now().minusDays(1);
		
		when(subscriptionServiceMock.findFailedToRenew(startDate, endDate)).thenReturn(failedSubscriptions);
		when(subscriptionServiceMock.findExcludingStatus(SubscriptionStatus.CANCELLED)).thenReturn(null);
		
		creditCardChargeJob.run();
		
		Subscription s = failedSubscriptions.get(0);
	
		verify(subscriptionServiceMock, times(1)).findExcludingStatus(SubscriptionStatus.CANCELLED);
		verify(subscriptionServiceMock, times(1)).findFailedToRenew(startDate, endDate);
		verify(mailServiceMock, times(1)).sendYearDiscountOffer(eq(s.getUser()), eq(s.getRenewFailedDate()));
		
	} 
	
	@Test
	public void testExpired_FailedToRenewWithDiscount() {
		
		setUp();
	
		Logger.info("Failed To Renew With Discount: ");
		
		LocalDate renewFailedDate = LocalDate.now().minusDays(3);
		
		Discount discount = new DiscountBuilder().build();
		
		List<Subscription> failedSubscriptions = Arrays.asList(
				new SubscriptionBuilder()
				.status(SubscriptionStatus.CANCELLED)
				.renewFailedDate(renewFailedDate)
				.discount(discount)
				.build());
		
		LocalDate startDate = LocalDate.now().minusDays(3);
		LocalDate endDate = LocalDate.now().minusDays(1);
		
		when(subscriptionServiceMock.findFailedToRenew(startDate, endDate)).thenReturn(failedSubscriptions);
		when(subscriptionServiceMock.findExcludingStatus(SubscriptionStatus.CANCELLED)).thenReturn(null);
		
		creditCardChargeJob.run();
		
		Subscription s = failedSubscriptions.get(0);
	
		verify(subscriptionServiceMock, times(1)).findExcludingStatus(SubscriptionStatus.CANCELLED);
		verify(subscriptionServiceMock, times(1)).findFailedToRenew(startDate, endDate);
		verify(mailServiceMock, times(0)).sendFreeWeekOffer(eq(s.getUser()), eq(s.getRenewFailedDate()));
		verify(mailServiceMock, times(0)).sendFreeMonthOffer(eq(s.getUser()), eq(s.getRenewFailedDate()));
		verify(mailServiceMock, times(0)).sendYearDiscountOffer(eq(s.getUser()), eq(s.getRenewFailedDate()));
		
	} 
	
}
