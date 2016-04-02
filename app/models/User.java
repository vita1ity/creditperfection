package models;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import com.avaje.ebean.Model;

import errors.ValidationError;
import play.data.validation.Constraints.Required;
import utils.EmailValidator;

@Entity
public class User extends Model {
	
    @Id
    public int id;
    @Required
    public String firstName;
    @Required
    public String lastName;
    @Column(unique = true)
    public String email;
    public String address;
    public String city;
    public String state;
    public String zip;
    public String password;
    public String token;
    public boolean active;
    
    @OneToMany(cascade = CascadeType.ALL)
    public List<CreditCard> creditCards;
    
    @OneToMany(cascade = CascadeType.ALL)
    public List<Transaction> transactions;
    
    public static Finder<Long, User> find = new Finder<Long, User>(User.class);
    
    public static User findByEmail(String email) {
		User user = User.find.where().eq("email", email).findUnique();
		return user;
	}

    public List<ValidationError> validate() {
		
		List<ValidationError> errors = new ArrayList<ValidationError>();
		
		Map<ValidationError, String> fieldErrorMap = new HashMap<ValidationError, String>();
		fieldErrorMap.put(new ValidationError("firstName", "Please enter First Name"), firstName);
		fieldErrorMap.put(new ValidationError("lastName", "Please enter Last Name"), lastName);
		fieldErrorMap.put(new ValidationError("email", "Please enter Email"), email);
		fieldErrorMap.put(new ValidationError("address", "Please enter your Address"), address);
		fieldErrorMap.put(new ValidationError("city", "Please enter City"), city);
		fieldErrorMap.put(new ValidationError("state", "Please select State"), state);
		fieldErrorMap.put(new ValidationError("zip", "Please enter Zip code"), zip);
		fieldErrorMap.put(new ValidationError("password", "Please enter Password"), password);
		
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
	    
		
		User userByEmail = findByEmail(this.email);
		if (userByEmail != null) {
			errors.add(new ValidationError("email", "User with such email is already registered"));
			
		}
		
		if (errors.size() != 0) {
			return errors;
		}
		return null;
	    
	}

	@Override
	public String toString() {
		return "User [id=" + id + ", firstName=" + firstName + ", lastName=" + lastName + ", email=" + email
				+ ", address=" + address + ", city=" + city + ", state=" + state + ", zip=" + zip
				+ ", password=" + password + "]";
	}
    
    
    
}
