package models;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import com.avaje.ebean.Model;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import be.objectify.deadbolt.java.models.Permission;
import be.objectify.deadbolt.java.models.Role;
import be.objectify.deadbolt.java.models.Subject;
import forms.RegisterForm;
import play.data.validation.Constraints.Required;

@Entity
public class User extends Model implements Subject {
	
    @Id
    private long id;
    
    @Required
    @Column(nullable = false)
    private String firstName;
    
    @Required
    @Column(nullable = false)
    private String lastName;
    
    @Column(unique = true, nullable = false)
    private String email;
    
    @Column(nullable = false)
    private String address;
    
    @Column(nullable = false)
    private String city;
    
    @Column(nullable = false)
    private String state;
    
    @Column(nullable = false)
    private String zip;
    
    @Column(nullable = false)
    private String password;
    
    @Column(nullable = false)
    private String token;
    
    private boolean active;
    
    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(name = "user_role")
    private List<SecurityRole> roles;
    
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "user")
    private List<CreditCard> creditCards;
    
    @JsonIgnore
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "user")
    private List<Transaction> transactions;
    
    @JsonIgnore
    @OneToOne(cascade = CascadeType.ALL, mappedBy = "user")
    private Subscription subscription;
    
    @JsonManagedReference
    @OneToOne(cascade = CascadeType.ALL, mappedBy = "user")
    private KBAQuestions kbaQuestions;
    
    
    public long getId() {
		return id;
	}

	public void setId(long id) {
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

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public boolean getActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public List<CreditCard> getCreditCards() {
		return creditCards;
	}

	public void setCreditCards(List<CreditCard> creditCards) {
		this.creditCards = creditCards;
	}

	public List<Transaction> getTransactions() {
		return transactions;
	}

	public void setTransactions(List<Transaction> transactions) {
		this.transactions = transactions;
	}

	public Subscription getSubscription() {
		return subscription;
	}

	public void setSubscription(Subscription subscription) {
		this.subscription = subscription;
	}

	public KBAQuestions getKbaQuestions() {
		return kbaQuestions;
	}

	public void setKbaQuestions(KBAQuestions kbaQuestions) {
		this.kbaQuestions = kbaQuestions;
	}

	public void setRoles(List<SecurityRole> roles) {
		this.roles = roles;
	}

    public User() {
    	
    }
    
    public User(RegisterForm registerForm) {
    	
		this.firstName = registerForm.firstName;
		this.lastName = registerForm.lastName;
		this.address = registerForm.address;
		this.email = registerForm.email;
		this.city = registerForm.city;
		this.password = registerForm.password;
		this.state = registerForm.state;
		this.zip = registerForm.zip;
		
	}


	@Override
	public String toString() {
		return "User [id=" + id + ", firstName=" + firstName + ", lastName=" + lastName + ", email=" + email
				+ ", address=" + address + ", city=" + city + ", state=" + state + ", zip=" + zip
				+ ", password=" + password + "]";
	}

	public void updateUserInfo(User user) {
		
		this.firstName = user.firstName;
		this.lastName = user.lastName;
		this.address = user.address;
		this.city = user.city;
		this.state = user.state;
		this.email = user.email;
		this.zip = user.zip;
		this.active = user.active;
		
	}

	@Override
	public List<? extends Role> getRoles() {
		return roles;
	}

	@Override
	public List<? extends Permission> getPermissions() {
		
		return null;
	}

	@Override
	public String getIdentifier() {
		
		return email;
	}
    
    
    
}
