package org.crama.creditperfection.test.controllers;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;
import static play.inject.Bindings.bind;
import static play.mvc.Http.Status.OK;
import static play.mvc.Http.Status.BAD_REQUEST;
import static play.test.Helpers.contentAsString;

import java.util.Arrays;
import java.util.List;

import org.crama.creditperfection.test.base.ControllerTestBase;
import org.crama.creditperfection.test.builders.UserBuilder;
import org.junit.Test;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import controllers.CreditReportController;
import models.KBAQuestions;
import models.SecurityRole;
import models.User;
import play.Configuration;
import play.data.FormFactory;
import play.inject.guice.GuiceApplicationBuilder;
import play.mvc.Result;
import services.CreditCardService;
import services.CreditReportService;
import services.ProductService;
import services.RoleService;
import services.SubscriptionService;
import services.TransactionService;
import services.UserService;

public class CreditReportControllerTest extends ControllerTestBase {
	private static User testUser;
	
	@Mock(answer = Answers.RETURNS_DEEP_STUBS)
	public FormFactory formFactoryMock;
	
	@Mock
    private UserService userServiceMock;
	
	@InjectMocks
	private CreditReportController creditReportController;
	
	private void setUp() {
		MockitoAnnotations.initMocks(this);
		
		List<SecurityRole> roles = Arrays.asList(new SecurityRole(1, "user"), 
				 new SecurityRole(2, "admin"));
		testUser = new UserBuilder().id(1).roles(roles).build();
		testUser.setKbaQuestions(new KBAQuestions());
	
	}
	
	@Override
    public GuiceApplicationBuilder getBuilder() {
        GuiceApplicationBuilder builder = super.getBuilder();
        setUp();
        return builder.overrides(bind(FormFactory.class).toInstance(formFactoryMock),
        						 bind(UserService.class).toInstance(userServiceMock));
        						 					 
    }
	
	@Test
	public void reportPage_Success() {
		
		when(userServiceMock.findByEmail(Matchers.anyString())).thenReturn(testUser);
		
		Result result = creditReportController.reportPage();
		assertEquals(OK, result.status());
		assertEquals("utf-8", result.charset().get());
		assertEquals("text/html", result.contentType().get());
				
	}
	
	@Test
	public void reportPage_Failure() {
		when(userServiceMock.findByEmail(Matchers.anyString())).thenReturn(null);
		
		Result result = creditReportController.reportPage();
		assertEquals(BAD_REQUEST, result.status());
	}
	
}
