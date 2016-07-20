package org.crama.creditperfection.tests.services;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsIterableContainingInAnyOrder.containsInAnyOrder;
import static org.hamcrest.collection.IsIterableContainingInOrder.contains;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import org.crama.creditperfection.tests.builders.RegisterFormBuilder;
import org.crama.creditperfection.tests.builders.UserBuilder;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import errors.ValidationError;
import forms.RegisterForm;
import models.User;
import repository.UserRepository;
import services.UserService;

public class UserServiceTest {
	
	@Mock
	private UserRepository userRepositoryMock;
	
	@Inject
	@InjectMocks
	private UserService userService;
	
	public static final String TEST_EMAIL = "test@gmail.com";
	public static final String INVALID_EMAIL = "test.gmail.com";
	public static final String INVALID_STATE = "STA";
	public static final String INVALID_ZIP = "111111";
	
	@Before
	public void setUpGetById() throws Exception {
		
		MockitoAnnotations.initMocks(this);
	}
	
	@Test
	public void testGetById() {
		
		User testUser = new UserBuilder().build();
		
		when(userRepositoryMock.getById(anyLong())).thenReturn(testUser);
		
		User user = userService.getById(0);
		assertTrue(user.getId() == 0l);
		assertTrue(user.getEmail().equals("sherlock.holmes@gmail.com"));
		
		verify(userRepositoryMock, times(1)).getById(anyLong());
		
	}
	
	
	@Before
	public void setUpGetAll() throws Exception {
		
		MockitoAnnotations.initMocks(this);
	}
	
	@Test
	public void tetsGetAll() {
		
		List<User> allUsers = Arrays.asList(new UserBuilder().build(),
				new UserBuilder().id(1).email("david.guilmor@gmail.com").build(),
				new UserBuilder().id(2).firstName("Roger").lastName("Waters").email("roger.waters@gmail.com").build());
		
		when(userRepositoryMock.getAll()).thenReturn(allUsers);
		
		List<User> allUsersResult = userService.getAll();
		assertTrue(allUsersResult.size() == allUsers.size());
		for (int i = 0; i < allUsersResult.size(); i++) {
			assertTrue(allUsersResult.get(i).getId() == i);
		}
		
		assertTrue(allUsersResult.get(1).getEmail().equals("david.guilmor@gmail.com"));
		assertTrue(allUsersResult.get(2).getFirstName().equals("Roger"));
		
		verify(userRepositoryMock, times(1)).getAll();
	}
	
	@Before
	public void setUpFindByEmail() throws Exception {
		
		MockitoAnnotations.initMocks(this);
		
	}
	
	@Test
	public void testFindByEmail() {
		
		User testUser = new UserBuilder().build();
		
		when(userRepositoryMock.findByEmail(anyString())).thenReturn(testUser);
		
		User user = userService.findByEmail("sherlock.holmes@gmail.com");
		assertTrue(user.getId() == 0l);
		assertTrue(user.getEmail().equals("sherlock.holmes@gmail.com"));
		
		verify(userRepositoryMock, times(1)).findByEmail(anyString());
		
	}
	
	
	@Test
	public void testValidation_Success() {
		
		User testUser = new UserBuilder().build();
		
		List<ValidationError> errors = userService.validate(testUser, false);
		assertNull(errors);
		
		
	}
	
	@Test
	public void testValidation_AddBlankErrors() {
		
		User blankUser = new UserBuilder()
				.firstName("")
				.lastName("")
				.email("")
				.address("")
				.city("")
				.state("")
				.zip("")
				.password("")
				.build();
		
		
		List<ValidationError> errors = userService.validate(blankUser, false);
		
		/*Logger.info("num of errors: " + errors.size());
		for (ValidationError error: errors) {
			Logger.info("error: " + error);
		}*/
		
		assertThat(errors, containsInAnyOrder(new ValidationError("firstName", "Please enter First Name"),
				new ValidationError("lastName", "Please enter Last Name"), 
				new ValidationError("email", "Please enter Email"), 
				new ValidationError("address", "Please enter your Address"), 
				new ValidationError("city", "Please enter City"), 
				new ValidationError("state", "Please select State"), 
				new ValidationError("zip", "Please enter Zip code"),
				new ValidationError("password", "Please enter Password"),
				new ValidationError("email", "Please enter a valid email address"),
				new ValidationError("state", "Please enter a valid state"),
				new ValidationError("zip", "Zip code should contain 5 digits"),
				new ValidationError("password", "Password should contain at least 5 characers")));
		
	}
	
	@Before
	public void setUpTestValidation_UserAlreadyExistError() throws Exception {
		
		MockitoAnnotations.initMocks(this);
		
	}
	
	@Test
	public void testValidation_UserAlreadyExistError() {
		
		User testUser = new UserBuilder()
				.email(TEST_EMAIL)
				.build();
		
		when(userRepositoryMock.findByEmail(TEST_EMAIL)).thenReturn(testUser);
		
		List<ValidationError> errors = userService.validate(testUser, false);
		
		assertTrue(errors.size() == 1);
		
		assertThat(errors, contains(new ValidationError("email", "User with such email is already registered")));
		
		errors = userService.validate(testUser, true);
		assertNull(errors);
	}
	
	@Test
	public void testValidation_InvalidEmailStateZip() {
		
		User testUser = new UserBuilder()
				.email(INVALID_EMAIL)
				.state(INVALID_STATE)
				.zip(INVALID_ZIP)
				.build();
		
		List<ValidationError> errors = userService.validate(testUser, true);
		
		assertTrue(errors.size() == 3);
		
		assertThat(errors, containsInAnyOrder(new ValidationError("email", "Please enter a valid email address"),
				new ValidationError("state", "Please enter a valid state"),
				new ValidationError("zip", "Zip code should contain 5 digits")));
		
	}
	
	@Test
	public void testRegisterFormValidation_Success() {
		
		RegisterForm registerForm = new RegisterFormBuilder().build();
		
		List<ValidationError> errors = registerForm.validate();
		assertNull(errors);	
		
	}
	
	@Test
	public void testRegisterFormValidation_EmptyFieldsErrors() {
		
		RegisterForm blankForm = new RegisterFormBuilder()
				.firstName("")
				.lastName("")
				.email("")
				.confirmEmail("")
				.address("")
				.city("")
				.state("")
				.zip("")
				.password("")
				.confirmPassword("")
				.build();
		
		
		List<ValidationError> errors = blankForm.validate();
		
		/*Logger.info("num of errors: " + errors.size());
		for (ValidationError error: errors) {
			Logger.info("error: " + error);
		}*/
		assertTrue(errors.size() == 14);
		assertThat(errors, containsInAnyOrder(
				new ValidationError("firstName", "Please enter First Name"),
				new ValidationError("lastName", "Please enter Last Name"), 
				new ValidationError("email", "Please enter Email"), 
				new ValidationError("address", "Please enter your Address"), 
				new ValidationError("city", "Please enter City"), 
				new ValidationError("state", "Please select State"), 
				new ValidationError("zip", "Please enter Zip code"),
				new ValidationError("password", "Please enter Password"),
				new ValidationError("email", "Please enter a valid email address"),
				new ValidationError("state", "Please enter a valid state"),
				new ValidationError("zip", "Zip code should contain 5 digits"),
				new ValidationError("password", "Password should contain at least 5 characers"),
				new ValidationError("confirmPassword", "Please enter Password Confirmation"),
				new ValidationError("confirmEmail", "Please enter Email Confirmation")));
		
	}
	
	@Test
	public void testRegisterFormValidation_InvalidEmailStateZip() {
		
		RegisterForm testForm = new RegisterFormBuilder()
				.email(INVALID_EMAIL)
				.state(INVALID_STATE)
				.zip(INVALID_ZIP)
				.build();
		
		List<ValidationError> errors = testForm.validate();
		
		assertTrue(errors.size() == 4);
		
		assertThat(errors, containsInAnyOrder(
				new ValidationError("email", "Please enter a valid email address"),
				new ValidationError("state", "Please enter a valid state"),
				new ValidationError("zip", "Zip code should contain 5 digits"),
				new ValidationError("confirmEmail", "Emails don't match")));
		
	}
	
	@Test
	public void testRegisterFormValidation_PasswordAndEmailDontMatch() {
		
		RegisterForm testForm = new RegisterFormBuilder()
				.email(TEST_EMAIL)
				.confirmEmail(INVALID_EMAIL)
				.password("12345")
				.confirmPassword("54321")
				.build();
		
		List<ValidationError> errors = testForm.validate();
		
		assertTrue(errors.size() == 2);
		
		assertThat(errors, containsInAnyOrder(
				new ValidationError("confirmPassword", "Passwords don't match"),
				new ValidationError("confirmEmail", "Emails don't match")));
				
	}
	
}
