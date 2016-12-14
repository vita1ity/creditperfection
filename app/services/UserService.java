package services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.avaje.ebean.PagedList;

import errors.ValidationError;
import forms.UserSearchForm;
import models.User;
import repository.UserRepository;
import utils.EmailValidator;
import utils.Tokener;

@Singleton
public class UserService {

	@Inject
	private UserRepository userRepository;
	
	public User getById(long id) {
		return userRepository.getById(id);
	}
	
	public List<User> getAll() {
		return userRepository.getAll();
	}
	
	public PagedList<User> getUsersPage(int page, int pageSize) {
		return userRepository.getUsersPage(page, pageSize);
	}
	
	public User findByEmail(String email) {
		return userRepository.findByEmail(email);
	}
	
	public void save(User user) {
		userRepository.save(user);
	}

	public void update(User user) {
		userRepository.update(user);		
	}
	
	public boolean delete(User user) {
		return userRepository.delete(user);
	}
	
	public List<ValidationError> validate(User user, boolean isEdit) {
		
		List<ValidationError> errors = new ArrayList<ValidationError>();
		
		Map<ValidationError, String> fieldErrorMap = new HashMap<ValidationError, String>();
		fieldErrorMap.put(new ValidationError("firstName", "Please enter First Name"), user.getFirstName());
		fieldErrorMap.put(new ValidationError("lastName", "Please enter Last Name"), user.getLastName());
		fieldErrorMap.put(new ValidationError("email", "Please enter Email"), user.getEmail());
		fieldErrorMap.put(new ValidationError("address", "Please enter your Address"), user.getAddress());
		fieldErrorMap.put(new ValidationError("city", "Please enter City"), user.getCity());
		fieldErrorMap.put(new ValidationError("state", "Please select State"), user.getState());
		fieldErrorMap.put(new ValidationError("zip", "Please enter Zip code"), user.getZip());
		fieldErrorMap.put(new ValidationError("password", "Please enter Password"), user.getPassword());
		
		for (Map.Entry<ValidationError, String> entry: fieldErrorMap.entrySet()) {
			String field = entry.getValue(); 
			if (field == null || field.equals("")) {
				errors.add(entry.getKey());
			}
		}
		
		EmailValidator emailValidator = new EmailValidator();
	    if (!emailValidator.validate(user.getEmail())) {
	    	ValidationError error = new ValidationError("email", "Please enter a valid email address");
			errors.add(error);
	    }
	    
	    if (user.getState().length() != 2 ) {
	    	ValidationError error = new ValidationError("state", "Please enter a valid state");
			errors.add(error);
	    }
	    
	    if (!user.getZip().matches("[0-9]+") || user.getZip().length() != 5) {
	    	ValidationError error = new ValidationError("zip", "Zip code should contain 5 digits");
			errors.add(error);
	    }
	    
	    if (user.getPassword().length() < 5) {
	    	ValidationError error = new ValidationError("password", "Password should contain at least 5 characers");
			errors.add(error);
	    }
	    
		if (!isEdit) {
			User userByEmail = findByEmail(user.getEmail());
			if (userByEmail != null) {
				errors.add(new ValidationError("email", "User with such email is already registered"));
				
			}
		}
		
		if (errors.size() != 0) {
			return errors;			
		}
		return null;
	    
	}
	
	public void updateInfo(User userDB, User user) {
		userDB.setActive(user.getActive());
		userDB.setAddress(user.getAddress());
		userDB.setCity(user.getCity());
		userDB.setEmail(user.getEmail());
		userDB.setFirstName(user.getFirstName());
		userDB.setLastName(user.getLastName());
		userDB.setState(user.getState());
		userDB.setZip(user.getZip());
	}
	
	//FOR TESTTING
	
	public void generateUsers(int numberOfUsers) {
		
		for (int i = 0; i < numberOfUsers / 2; i++) {
			
			User user = new User();
			user.setFirstName("First Name " + i);
			user.setLastName("Last Name " + i);
			user.setActive(true);
			user.setAddress("Address " + i);
			user.setCity("City " + i);
			user.setEmail("Email " + i);
			user.setPassword("Password" + i);
			user.setState("AK");
			user.setZip("22222");
			user.setToken(Tokener.randomString(48));
			userRepository.save(user);
			
			
		}
		
		for (int i = numberOfUsers / 2; i < numberOfUsers; i++) {
			
			User user = new User();
			user.setFirstName("John " + i);
			user.setLastName("Smith " + i);
			user.setActive(true);
			user.setAddress("Address " + i);
			user.setCity("City " + i);
			user.setEmail("Email " + i);
			user.setPassword("Password" + i);
			user.setState("AK");
			user.setZip("22222");
			user.setToken(Tokener.randomString(48));
			userRepository.save(user);
			
		}
		
		
	}

	public PagedList<User> searchByName(String query, int currentPage, int pageSize) {
		
		return userRepository.searchByName(query, currentPage, pageSize);
	}

	public PagedList<User> preciseSearch(UserSearchForm searchForm, int currentPage, int pageSize) {
		
		return userRepository.preciseSearch(searchForm, currentPage, pageSize);
	}

	public User findByEmailOnlyActive(String email) {
		return userRepository.findByEmailOnlyActive(email);
		
	}

	
	
}
