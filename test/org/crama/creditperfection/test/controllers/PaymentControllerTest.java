package org.crama.creditperfection.test.controllers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static play.mvc.Http.Status.OK;
import static play.test.Helpers.contentAsString;

import org.crama.creditperfection.test.base.ControllerTestBase;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;


import controllers.PaymentController;
import play.inject.guice.GuiceApplicationBuilder;
import play.mvc.Result;

public class PaymentControllerTest extends ControllerTestBase {
	
	@InjectMocks
	private PaymentController paymentController;
	
	private void setUp() {
		MockitoAnnotations.initMocks(this);
			
	}
	
	@Override
    public GuiceApplicationBuilder getBuilder() {
        GuiceApplicationBuilder builder = super.getBuilder();
        setUp();
        return builder;
        						 					 
    }
	
	@Test
	public void upgradeSubscriptionPage() {
				
		Result result = paymentController.paymentPage();
		assertEquals(OK, result.status());
		assertEquals("utf-8", result.charset().get());
		assertEquals("text/html", result.contentType().get());
		
		assertTrue(contentAsString(result).contains("Payment page"));		
	}

}
