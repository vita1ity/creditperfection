package org.crama.creditperfection.tests.services;

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

import org.crama.creditperfection.tests.builders.CreditCardBuilder;
import org.crama.creditperfection.tests.builders.ProductBuilder;
import org.crama.creditperfection.tests.builders.SubscriptionFormBuilder;
import org.crama.creditperfection.tests.builders.TransactionBuilder;
import org.crama.creditperfection.tests.builders.TransactionFormBuilder;
import org.crama.creditperfection.tests.builders.UserBuilder;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import errors.ValidationError;
import forms.SubscriptionForm;
import forms.TransactionForm;
import models.CreditCard;
import models.Product;
import models.Transaction;
import models.User;
import models.enums.TransactionStatus;
import repository.TransactionRepository;
import services.CreditCardService;
import services.ProductService;
import services.TransactionService;
import services.UserService;

public class TransactionServiceTest {

	@Inject
	@InjectMocks
	private TransactionService transactionService;
	
	@Mock
	private TransactionRepository transactionRepositoryMock;
	
	@Mock
	private UserService userServiceMock;
	
	@Mock
	private CreditCardService creditCardServiceMock;
	
	@Mock
	private ProductService productServiceMock;
	
	
	
	@Before
	public void setUpCreateTransaction_NewTransaction() {
		
		MockitoAnnotations.initMocks(this);
		
	}
	
	@Test
	public void testCreateTransaction_NewTransaction() {
	
		User user = new UserBuilder().build();
		CreditCard creditCard = new CreditCardBuilder().build();
		Product product = new ProductBuilder().build();
		
		TransactionForm transactionForm = new TransactionFormBuilder()
				.id(null)
				.build(); 
	
		
		when(userServiceMock.getById(1)).thenReturn(user);
		when(creditCardServiceMock.getById(1)).thenReturn(creditCard);
		when(productServiceMock.getById(1)).thenReturn(product);
		
		Transaction transaction = transactionService.createTransaction(transactionForm);
		
		assertTrue(transaction.getUser().equals(user));
		assertTrue(transaction.getCreditCard().equals(creditCard));
		assertTrue(transaction.getProduct().equals(product));
		assertTrue(transaction.getAmount() == 1.0);
		assertTrue(transaction.getTransactionId().equals("123"));
		assertTrue(transaction.getStatus().equals(TransactionStatus.SUCCESSFUL));
		
		verify(userServiceMock, times(1)).getById(1);
		verify(creditCardServiceMock, times(1)).getById(1);
		verify(productServiceMock, times(1)).getById(1);
		
	}
	
	@Before
	public void setUpCreateTransaction_EditTransaction() {
		
		MockitoAnnotations.initMocks(this);
		
	}
	
	@Test
	public void testCreateTransaction_EditTransaction() {
	
		User user = new UserBuilder().build();
		CreditCard creditCard = new CreditCardBuilder().build();
		Product product = new ProductBuilder().build();
		
		TransactionForm transactionForm = new TransactionFormBuilder()
				.id("1")
				.status(TransactionStatus.REFUNDED)
				.build(); 
		
		when(userServiceMock.getById(1)).thenReturn(user);
		when(creditCardServiceMock.getById(1)).thenReturn(creditCard);
		when(productServiceMock.getById(1)).thenReturn(product);
		
		Transaction transaction = transactionService.createTransaction(transactionForm);
		
		assertTrue(transaction.getId() == 1l);
		assertTrue(transaction.getUser().equals(user));
		assertTrue(transaction.getCreditCard().equals(creditCard));
		assertTrue(transaction.getProduct().equals(product));
		assertTrue(transaction.getAmount() == 1.0);
		assertTrue(transaction.getTransactionId().equals("123"));
		assertTrue(transaction.getStatus().equals(TransactionStatus.REFUNDED));
		
		verify(userServiceMock, times(1)).getById(1);
		verify(creditCardServiceMock, times(1)).getById(1);
		verify(productServiceMock, times(1)).getById(1);
		
	}
	
	@Before
	public void setUpFindAll() throws Exception {
		
		MockitoAnnotations.initMocks(this);
		
	}
	
	@Test
	public void testFindAll() {
		
		List<Transaction> testTransactions = Arrays.asList(
				new TransactionBuilder().build(),
				new TransactionBuilder().id(1l).build(),
				new TransactionBuilder().id(2l).build());
		
		when(transactionRepositoryMock.findAll()).thenReturn(testTransactions);
		
		List<Transaction> allTransactions = transactionService.findAll();
		
		assertTrue(allTransactions.size() == 3);
		int i = 0;
		for (Transaction t: allTransactions) {
			assertTrue(t.getId() == i);
			++i;
		}
		
		verify(transactionRepositoryMock, times(1)).findAll();
		
	}
	
	@Before
	public void setUpFindById() throws Exception {
		
		MockitoAnnotations.initMocks(this);
	}
	
	@Test
	public void testFindById() {
		
		Transaction testTransaction = new TransactionBuilder().build();
		
		when(transactionRepositoryMock.findById(anyLong())).thenReturn(testTransaction);
		
		Transaction transaction = transactionService.findById(0);
		assertTrue(transaction.getId() == testTransaction.getId());
		assertTrue(transaction.getUser().equals(testTransaction.getUser()));
		assertTrue(transaction.getCreditCard().equals(testTransaction.getCreditCard()));
		assertTrue(transaction.getProduct().equals(testTransaction.getProduct()));
		assertTrue(transaction.getAmount() == testTransaction.getAmount());
		assertTrue(transaction.getTransactionId().equals(testTransaction.getTransactionId()));
		assertTrue(transaction.getStatus().equals(testTransaction.getStatus()));
		
		verify(transactionRepositoryMock, times(1)).findById(anyLong());
		
	}
	
	@Test
	public void testValidation_Success() {
		
		TransactionForm transactionForm = new TransactionFormBuilder().build();
		
		List<ValidationError> errors = transactionForm.validate();
		
		assertNull(errors);	
		
	}
	
	@Test
	public void testValidation_emptyFields() {
		
		TransactionForm transactionForm = new TransactionFormBuilder()
				.userId("")
				.creditCardId("")
				.productId("")
				.amount("")
				.transactionId("")
				.build();
			
		List<ValidationError> errors = transactionForm.validate();
		
		assertTrue(errors.size() == 6);
		assertThat(errors, containsInAnyOrder(
				new ValidationError("user", "Please choose User"),
				new ValidationError("creditCard", "Please choose Credit Card"),
				new ValidationError("product", "Please choose Product"),
				new ValidationError("amount", "Please enter Amount"),
				new ValidationError("transactionId", "Please enter Transaction Id"),
				new ValidationError("amount", "Amount should be numeric value")));
	
	}
	
	@Test
	public void testValidation_AmountNotNumericError() {
		
		TransactionForm transactionForm = new TransactionFormBuilder()
				.amount("invalid")
				.build();
			
		List<ValidationError> errors = transactionForm.validate();
		
		assertTrue(errors.size() == 1);
		assertThat(errors, containsInAnyOrder(
				new ValidationError("amount", "Amount should be numeric value")));
	
	}
	
	
}
