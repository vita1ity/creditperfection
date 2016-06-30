package services;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

import errors.ValidationError;
import models.User;
import repository.UserRepository;
import utils.EmailValidator;

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
	
	public User findByEmail(String email) {
		return userRepository.findByEmail(email);
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
	
}
