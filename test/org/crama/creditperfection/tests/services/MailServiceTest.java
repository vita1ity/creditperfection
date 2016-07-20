package org.crama.creditperfection.tests.services;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import javax.inject.Inject;

import org.crama.creditperfection.tests.builders.UserBuilder;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import models.User;
import play.Configuration;
import services.MailService;
import utils.Tokener;

public class MailServiceTest {

	public static final String APP_HOST = "https://secure.creditperfection.org";
	public static final String SUPPORT_EMAIL = "support@creditperfection.org";
	public static final String EMAIL_USERNAME = "support";
	public static final String EMAIL_PASSWORD = "cr3d!t123";
	public static final String EMAIL_HOST = "mail.creditperfection.org";
	public static final String EMAIL_PORT = "587";
	
	public static final String EMAIL_TO = "vitalii.oleksiv@mail.ru";
	
	@Inject
	@InjectMocks
	private MailService mailService;

	@Mock
	private Configuration confMock;
	
	@Before
	public void setUpSendEmailToken() {
		
		MockitoAnnotations.initMocks(this);
		
	}
	
	@Test
	public void testSendEmailToken() {
		
		when(confMock.getString("app.host")).thenReturn(APP_HOST);
		when(confMock.getString("email.support")).thenReturn(SUPPORT_EMAIL);
		when(confMock.getString("email.host")).thenReturn(EMAIL_HOST);
		when(confMock.getString("email.port")).thenReturn(EMAIL_PORT);
	    when(confMock.getString("email.username")).thenReturn(EMAIL_USERNAME);
	    when(confMock.getString("email.password")).thenReturn(EMAIL_PASSWORD);
	    
		String token = Tokener.randomString(20);
		
		mailService.sendEmailToken(EMAIL_TO, token);
		
		verify(confMock, times(6)).getString(anyString());
		
	}

	
	@Before
	public void setUpSendEmailPassword() {
		
		MockitoAnnotations.initMocks(this);
		
	}
	
	@Test
	public void testSendEmailPassword() {
		
		when(confMock.getString("email.support")).thenReturn(SUPPORT_EMAIL);
		when(confMock.getString("email.host")).thenReturn(EMAIL_HOST);
		when(confMock.getString("email.port")).thenReturn(EMAIL_PORT);
	    when(confMock.getString("email.username")).thenReturn(EMAIL_USERNAME);
	    when(confMock.getString("email.password")).thenReturn(EMAIL_PASSWORD);
	    
		mailService.sendEmailPassword(EMAIL_TO, "password");
		
		verify(confMock, times(5)).getString(anyString());
		
	}
	
	@Before
	public void setUpCancelSubscriptionNotification() {
		
		MockitoAnnotations.initMocks(this);
		
	}
	
	@Test
	public void testCancelSubscriptionNotification() {
		
		when(confMock.getString("email.support")).thenReturn(SUPPORT_EMAIL);
		when(confMock.getString("email.host")).thenReturn(EMAIL_HOST);
		when(confMock.getString("email.port")).thenReturn(EMAIL_PORT);
	    when(confMock.getString("email.username")).thenReturn(EMAIL_USERNAME);
	    when(confMock.getString("email.password")).thenReturn(EMAIL_PASSWORD);
	    
		User user = new UserBuilder().email(EMAIL_TO).build();
		
		mailService.sendCancelSubscriptionNotification(user);
		
		verify(confMock, times(5)).getString(anyString());
		
	}
	
	
}
