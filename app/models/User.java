package models;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import com.avaje.ebean.Model;

import play.data.validation.Constraints.Required;
import errors.ValidationError;

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
    public String phone;
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
		
		User userByEmail = findByEmail(this.email);
		if (userByEmail != null) {
			errors.add(new ValidationError("email", "User with such email is already registered"));
			return errors;
		}
		
		return null;
	    
	}

	@Override
	public String toString() {
		return "User [id=" + id + ", firstName=" + firstName + ", lastName=" + lastName + ", email=" + email
				+ ", phone=" + phone + ", address=" + address + ", city=" + city + ", state=" + state + ", zip=" + zip
				+ ", password=" + password + "]";
	}
    
    
    
}
