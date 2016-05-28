package forms;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.avaje.ebean.Model;

import errors.ValidationError;
import models.User;
import utils.EmailValidator;

public class RegisterForm extends Model {
	
	public String firstName;
    public String lastName;
    public String email;
    public String password;
    public String confirmEmail;
    public String confirmPassword;
    public String address;
    public String city;
    public String state;
    public String zip;
    

    public List<ValidationError> validate(boolean isEdit) {
		
		List<ValidationError> errors = new ArrayList<ValidationError>();
		
		Map<ValidationError, String> fieldErrorMap = new HashMap<ValidationError, String>();
		fieldErrorMap.put(new ValidationError("firstName", "Please enter First Name"), firstName);
		fieldErrorMap.put(new ValidationError("lastName", "Please enter Last Name"), lastName);
		fieldErrorMap.put(new ValidationError("email", "Please enter Email"), email);
		fieldErrorMap.put(new ValidationError("confirmEmail", "Please enter Email Confirmation"), confirmEmail);
		fieldErrorMap.put(new ValidationError("address", "Please enter your Address"), address);
		fieldErrorMap.put(new ValidationError("city", "Please enter City"), city);
		fieldErrorMap.put(new ValidationError("state", "Please select State"), state);
		fieldErrorMap.put(new ValidationError("zip", "Please enter Zip code"), zip);
		fieldErrorMap.put(new ValidationError("password", "Please enter Password"), password);
		fieldErrorMap.put(new ValidationError("confirmPassword", "Please enter Password Confirmation"), confirmPassword);
		
		for (Map.Entry<ValidationError, String> entry: fieldErrorMap.entrySet()) {
			String field = entry.getValue(); 
			if (field == null || field.equals("")) {
				errors.add(entry.getKey());
			}
		}
		
		EmailValidator emailValidator = new EmailValidator();
	    if (!emailValidator.validate(email)) {
	    	ValidationError error = new ValidationError("email", "Please enter a valid email address");
			errors.add(error);
	    }
	    if (!email.equals(confirmEmail)) {
	    	ValidationError error = new ValidationError("confirmEmail", "Emails don't match");
			errors.add(error);
	    }
	    
	    if (state.length() != 2 ) {
	    	ValidationError error = new ValidationError("state", "Please enter a valid state");
			errors.add(error);
	    }
	    
	    if (!zip.matches("[0-9]+") || zip.length() != 5) {
	    	ValidationError error = new ValidationError("zip", "Zip code should contain 5 digits");
			errors.add(error);
	    }
	    
	    if (password.length() < 5) {
	    	ValidationError error = new ValidationError("password", "Password should contain at least 5 characers");
			errors.add(error);
	    }
	    if (!password.equals(confirmPassword)) {
	    	ValidationError error = new ValidationError("confirmPassword", "Passwords don't match");
			errors.add(error);
	    }
	    	
		/*if (!isEdit) {
			User userByEmail = User.findByEmail(this.email);
			if (userByEmail != null) {
				
				errors.add(new ValidationError("email", "User with such email is already registered"));
				
			}
		}*/
		
		if (errors.size() != 0) {
			return errors;
		}
		return null;
	    
	}
}
