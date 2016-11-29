package forms;

import java.util.ArrayList;
import java.util.List;

import errors.ValidationError;
import models.enums.UserStateFilter;
import utils.EmailValidator;

public class UserSearchForm {

	private String id;
	private String firstName;
	private String lastName;
	private String email;
    private String address;
    private String city;
    private String state;
    private String zip;
	private UserStateFilter active;
	
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public String getZip() {
		return zip;
	}
	public void setZip(String zip) {
		this.zip = zip;
	}
	public UserStateFilter getActive() {
		return active;
	}
	public void setActive(UserStateFilter active) {
		this.active = active;
	}
	
	public List<ValidationError> validate() {
		

		List<ValidationError> errors = new ArrayList<ValidationError>();
		if (id != null && !id.equals("")) {
			try {
				Integer.parseInt(id);
			}
			catch (NumberFormatException e) {
				ValidationError error = new ValidationError("id", "Id should be a numeric value");
				errors.add(error);
			}
		}
		
		EmailValidator emailValidator = new EmailValidator();
	    if (email != null && !email.equals("") && !emailValidator.validate(email)) {
	    	ValidationError error = new ValidationError("email", "Please enter a valid email address");
			errors.add(error);
	    }
	    if (zip != null && !zip.equals("")) {
		    if (!zip.matches("[0-9]+") || zip.length() != 5) {
		    	ValidationError error = new ValidationError("zip", "Zip code should contain 5 digits");
				errors.add(error);
		    }
	    }
	    return errors;
	}
	@Override
	public String toString() {
		return "UserSearchForm [id=" + id + ", firstName=" + firstName + ", lastName=" + lastName + ", email=" + email
				+ ", address=" + address + ", city=" + city + ", state=" + state + ", zip=" + zip + ", active=" + active
				+ "]";
	}
	
	
	
}
