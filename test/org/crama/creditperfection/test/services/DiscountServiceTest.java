package org.crama.creditperfection.test.services;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.LocalDateTime;

import javax.inject.Inject;

import org.crama.creditperfection.test.builders.DiscountBuilder;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import models.Discount;
import models.enums.DiscountType;
import play.Configuration;
import play.Logger;
import services.DiscountService;
import services.SubscriptionService;

public class DiscountServiceTest {

	@Inject
	@InjectMocks
	private DiscountService discountService;
	
	@Mock
	private SubscriptionService subscriptionServiceMock;
	
	@Mock
	private Configuration confMock;
	
	private void setUp() {
		
		MockitoAnnotations.initMocks(this);
	
	}
	
	@Test
	public void testCheckDiscountExpired_Expired() {
		
		setUp();
		
		when(confMock.getInt("creditperfection.trial.days")).thenReturn(7);
		
		LocalDate today = LocalDate.now();
		LocalDate endDate = today.plusDays(7).minusDays(1);
		
		Logger.info("today: " + today);
		Logger.info("endDate: " + endDate);
		
		Discount testDiscount = new DiscountBuilder()
											.discountType(DiscountType.WEEKLY_DISCOUNT)
											.endDate(endDate)
											.build();
		
		Logger.info("testDiscount: " + testDiscount);
		
		boolean isExpired = discountService.checkDiscountExpired(testDiscount);
		
		assertTrue(isExpired);
		
		verify(confMock, times(1)).getInt("creditperfection.trial.days");
	}
	
	@Test
	public void testCheckDiscountExpired_notExpired() {
		
		setUp();
		
		when(confMock.getInt("creditperfection.trial.days")).thenReturn(7);
		
		LocalDate today = LocalDate.now();
		LocalDate endDate = today.plusDays(7);
		
		Logger.info("today: " + today);
		Logger.info("endDate: " + endDate);
		
		Discount testDiscount = new DiscountBuilder()
											.discountType(DiscountType.WEEKLY_DISCOUNT)
											.endDate(endDate)
											.build();
		
		Logger.info("testDiscount: " + testDiscount);
		
		boolean isExpired = discountService.checkDiscountExpired(testDiscount);
		
		assertFalse(isExpired);
		
		verify(confMock, times(1)).getInt("creditperfection.trial.days");
	}
	
	@Test
	public void testCheckDiscountExpired_activeExpired() {
		
		setUp();
		
		when(confMock.getInt("creditperfection.trial.days")).thenReturn(7);
		
		LocalDate today = LocalDate.now();
		int daysInMonth = today.getMonth().length(true);
		LocalDate endDate = today.plusDays(daysInMonth).minusDays(20);
		
		Logger.info("today: " + today);
		Logger.info("endDate: " + endDate);
		
		Discount testDiscount = new DiscountBuilder()
											.discountType(DiscountType.MONTHLY_DISCOUNT)
											.endDate(endDate)
											.build();
		
		Logger.info("testDiscount: " + testDiscount);
		
		boolean isExpired = discountService.checkDiscountExpired(testDiscount);
		
		assertTrue(isExpired);
		
		verify(confMock, times(0)).getInt("creditperfection.trial.days");
	}
	
	@Test
	public void testCheckDiscountExpired_activeNotExpired() {
		
		setUp();
		
		when(confMock.getInt("creditperfection.trial.days")).thenReturn(7);
		
		LocalDate today = LocalDate.now();
		LocalDate endDate = today.plusDays(60);
		
		Logger.info("today: " + today);
		Logger.info("endDate: " + endDate);
		
		Discount testDiscount = new DiscountBuilder()
											.discountType(DiscountType.YEARLY_DISCOUNT)
											.endDate(endDate)
											.build();
		
		Logger.info("testDiscount: " + testDiscount);
		
		boolean isExpired = discountService.checkDiscountExpired(testDiscount);
		
		assertFalse(isExpired);
		
		verify(confMock, times(0)).getInt("creditperfection.trial.days");
	}
	
}
