package org.crama.creditperfection.test.services;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsIterableContainingInAnyOrder.containsInAnyOrder;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import org.crama.creditperfection.test.builders.AuthNetAccountBuilder;
import org.crama.creditperfection.test.builders.UserBuilder;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import errors.ValidationError;
import models.AuthNetAccount;
import models.User;
import repository.AuthNetAccountRepository;
import services.AuthNetAccountService;

public class AuthNetAccountServiceTest {

	@Mock
	private AuthNetAccountRepository accountRepositoryMock;
	
	@Inject
	@InjectMocks
	private AuthNetAccountService accountService;
	
	@Before
	public void setUpGetById() throws Exception {
		
		MockitoAnnotations.initMocks(this);
	}
	
	@Test
	public void testGetById() {
		
		AuthNetAccount authNetAccount = new AuthNetAccountBuilder().build();
	
		when(accountRepositoryMock.getById(anyLong())).thenReturn(authNetAccount);
		
		AuthNetAccount account = accountService.getById(0);
		assertTrue(account.getId() == 0l);
		assertTrue(account.getName().equals("defaultAccount"));
		
		verify(accountRepositoryMock, times(1)).getById(anyLong());
		
	}
	
	@Before
	public void setUpGetAll() throws Exception {
		
		MockitoAnnotations.initMocks(this);
	}
	
	@Test
	public void tetsGetAll() {
		
		List<AuthNetAccount> allAccounts = Arrays.asList(new AuthNetAccountBuilder().build(),
				new AuthNetAccountBuilder().id(1).name("secondAccount").build(),
				new AuthNetAccountBuilder().id(2).name("fullAccount").description("Account with all fields set")
					.loginId("123456789").transactionKey("transactionKey").isLastUsed(false).build());
		
		when(accountRepositoryMock.getAll()).thenReturn(allAccounts);
		
		List<AuthNetAccount> allAccountsResult = accountService.getAll();
		assertTrue(allAccountsResult.size() == allAccounts.size());
		for (int i = 0; i < allAccountsResult.size(); i++) {
			assertTrue(allAccountsResult.get(i).getId() == i);
		}
		
		assertTrue(allAccountsResult.get(1).getName().equals("secondAccount"));
		assertTrue(allAccountsResult.get(2).getName().equals("fullAccount"));
		assertTrue(allAccountsResult.get(2).getDescription().equals("Account with all fields set"));
		assertTrue(allAccountsResult.get(2).getLoginId().equals("123456789"));
		assertTrue(allAccountsResult.get(2).getTransactionKey().equals("transactionKey"));
		assertTrue(allAccountsResult.get(2).getIsLastUsed() == false);
		
		verify(accountRepositoryMock, times(1)).getAll();
	}
	
	@Test
	public void testValidation_Success() {
		
		AuthNetAccount testAccount = new AuthNetAccountBuilder().build();
		
		List<ValidationError> errors = accountService.validate(testAccount);
		assertNull(errors);
		
		
	}
	
	@Test
	public void testValidation_AddBlankErrors() {
		
		AuthNetAccount blankAccount = new AuthNetAccountBuilder()
				.name("")
				.description("")
				.loginId("")
				.transactionKey("")
				.build();
		
		
		List<ValidationError> errors = accountService.validate(blankAccount);
		
		assertThat(errors, containsInAnyOrder(new ValidationError("name", "Please enter Account Name"),
				new ValidationError("loginId", "Please enter Login ID"),
				new ValidationError("transactionKey", "Please enter Transaction Key")));
		
	}
	
	
}
