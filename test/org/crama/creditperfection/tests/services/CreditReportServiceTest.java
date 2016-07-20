package org.crama.creditperfection.tests.services;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
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
import models.json.AuthenticationSuccessResponse;
import models.json.CreditReportSuccessResponse;
import models.json.ErrorResponse;
import models.json.JSONResponse;
import play.Configuration;
import play.Logger;
import services.CreditReportService;
import utils.EmailGenerator;

public class CreditReportServiceTest {

	private static final String IDCS_ENROLL_URL = "https://xml.idcreditservices.com/IDSWebServicesNG/IDSEnrollment.asmx";
	private static final String IDCS_SERVER_URI = "http://tempuri.org/";
	private static final String REQUEST_SOURCE = "CRDPRF";
	private static final String PACKAGE_ID = "476";
	private static final String PARTNER_PASSWORD = "kYmfR5@23";
	
	private static final String IDCS_AUTHENTICATE_URL = "https://xml.idcreditservices.com/SIDUpdateServices/MemberUpdate.asmx";
	
	private static final String IDCS_GET_REPORT_URL = "https://xml.idcreditservices.com/IDSWebServicesNG/IDSDataMonitoringReport.asmx";
	
	
	@Mock
	private Configuration confMock;
	
	@Inject
	@InjectMocks
	private CreditReportService creditReportService;
	
	@Before
	public void setUpGetKBAQuestionsURL() {
		
		MockitoAnnotations.initMocks(this);
		
	}
	
	@Test
	public void testGetKBAQuestionsURL() {
		
		when(confMock.getString("idcs.enroll.url")).thenReturn(IDCS_ENROLL_URL);
		when(confMock.getString("idcs.server.uri")).thenReturn(IDCS_SERVER_URI);
        when(confMock.getString("credit.report.request.source")).thenReturn(REQUEST_SOURCE);
        when(confMock.getString("credit.report.package.id")).thenReturn(PACKAGE_ID);
        when(confMock.getString("credit.report.partner.password")).thenReturn(PARTNER_PASSWORD);
		
		String email = EmailGenerator.generate();
		
		User user = new UserBuilder().email(email).build();
		
		JSONResponse response = creditReportService.getKBAQuestionsUrl(user);
		Logger.info("response: " + response);
		
		assertTrue(response instanceof CreditReportSuccessResponse);
		assertTrue(response.getStatus().equals("SUCCESS"));
		assertNotNull(((CreditReportSuccessResponse)response).getReportUrl());
		
		verify(confMock, times(5)).getString(anyString());
		
	}
	
	@Before
	public void setUpGetKBAQuestionsURL_UserAlreadyEnrolledError() {
		
		MockitoAnnotations.initMocks(this);
		
	}
	
	@Test
	public void testGetKBAQuestionsURL_UserAlreadyEnrolledError() {
		
		when(confMock.getString("idcs.enroll.url")).thenReturn(IDCS_ENROLL_URL);
		when(confMock.getString("idcs.server.uri")).thenReturn(IDCS_SERVER_URI);
        when(confMock.getString("credit.report.request.source")).thenReturn(REQUEST_SOURCE);
        when(confMock.getString("credit.report.package.id")).thenReturn(PACKAGE_ID);
        when(confMock.getString("credit.report.partner.password")).thenReturn(PARTNER_PASSWORD);
		
		User user = new UserBuilder().email("test@gmail.com").build();
		
		JSONResponse response = creditReportService.getKBAQuestionsUrl(user);
		Logger.info("response: " + response);
		
		assertTrue(response instanceof ErrorResponse);
		assertTrue(response.getStatus().equals("ERROR"));
		assertNotNull(((ErrorResponse)response).getErrorMessage());
		
		verify(confMock, times(5)).getString(anyString());
		
	}
	
	@Before
	public void setUpAuthenticate_Success() {
		
		MockitoAnnotations.initMocks(this);
		
	}
	
	@Test
	public void testAuthenticate_Success() {
		
		when(confMock.getString("idcs.authenticate.url")).thenReturn(IDCS_AUTHENTICATE_URL);
		when(confMock.getString("idcs.server.uri")).thenReturn(IDCS_SERVER_URI);
		
		User user = new UserBuilder().email("test@gmail.com").build();
		
		JSONResponse response = creditReportService.authenticate(user.getEmail(), user.getPassword());
		Logger.info("response: " + response);
		
		assertTrue(response instanceof AuthenticationSuccessResponse);
		assertTrue(response.getStatus().equals("SUCCESS"));
		assertTrue(((AuthenticationSuccessResponse)response).getMemberId().equals("test@gmail.com"));
		
		verify(confMock, times(2)).getString(anyString());
		
	}
	
	@Before
	public void setUpAuthenticate_Failure() {
		
		MockitoAnnotations.initMocks(this);
		
	}
	
	@Test
	public void testAuthenticate_Failure() {
		
		when(confMock.getString("idcs.authenticate.url")).thenReturn(IDCS_AUTHENTICATE_URL);
		when(confMock.getString("idcs.server.uri")).thenReturn(IDCS_SERVER_URI);
		
		User user = new UserBuilder().email("invalid@gmail.com").build();
		
		JSONResponse response = creditReportService.authenticate(user.getEmail(), user.getPassword());
		Logger.info("response: " + response);
		
		assertTrue(response instanceof ErrorResponse);
		assertTrue(response.getStatus().equals("ERROR"));
		assertTrue(((ErrorResponse)response).getErrorMessage().equals("IDCS authentication failed"));
		
		verify(confMock, times(2)).getString(anyString());
		
	}
	
	@Before
	public void setUpGetReport_Success() {
	
		MockitoAnnotations.initMocks(this);
		
	}
	
	@Test
	public void testGetReport_Success() {
		
		when(confMock.getString("idcs.getreport.url")).thenReturn(IDCS_GET_REPORT_URL);
		when(confMock.getString("idcs.server.uri")).thenReturn(IDCS_SERVER_URI);
		when(confMock.getString("credit.report.request.source")).thenReturn(REQUEST_SOURCE);
	    when(confMock.getString("credit.report.partner.password")).thenReturn(PARTNER_PASSWORD);
		
	    User user = new UserBuilder().email("test@gmail.com").build();
		
		JSONResponse response = creditReportService.getReport(user.getEmail());
		Logger.info("response: " + response);
		
		assertTrue(response instanceof CreditReportSuccessResponse);
		assertTrue(response.getStatus().equals("SUCCESS"));
		assertNotNull(((CreditReportSuccessResponse)response).getReportUrl());
		
		verify(confMock, times(4)).getString(anyString());
	}
	
	@Before
	public void setUpGetReport_Failure() {
	
		MockitoAnnotations.initMocks(this);
		
	}
	
	@Test
	public void testGetReport_Failure() {
		
		when(confMock.getString("idcs.getreport.url")).thenReturn(IDCS_GET_REPORT_URL);
		when(confMock.getString("idcs.server.uri")).thenReturn(IDCS_SERVER_URI);
		when(confMock.getString("credit.report.request.source")).thenReturn(REQUEST_SOURCE);
	    when(confMock.getString("credit.report.partner.password")).thenReturn(PARTNER_PASSWORD);
		
	    User user = new UserBuilder().email("invalid@gmail.com").build();
		
		JSONResponse response = creditReportService.getReport(user.getEmail());
		Logger.info("response: " + response);
		
		assertTrue(response instanceof ErrorResponse);
		assertTrue(response.getStatus().equals("ERROR"));
		assertTrue(((ErrorResponse)response).getErrorMessage().equals("MemberId is invalid"));
		
		verify(confMock, times(4)).getString(anyString());
	}
	
	
}
