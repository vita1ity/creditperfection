package org.crama.creditperfection.tests.builders;

import java.util.Arrays;
import java.util.List;

import models.SecurityRole;
import models.User;

public class UserBuilder {

    private long id = 0l;
    
    private String firstName = "Sherlock";
    
    private String lastName = "Holmes";
    
    private String email = "sherlock.holmes@gmail.com";
    
    private String address = "221b Baker Street";
    
    private String city = "London";
    
    private String state = "UK";
    
    private String zip = "33333";
    
    private String password = "sherlock";
    
    private String token = "111111111111111111";
    
    private boolean active = true;
    
    private List<SecurityRole> roles = Arrays.asList(new SecurityRoleBuilder().build());
    
    public UserBuilder id(long id) {
        this.id = id;
        return this;
    }

    public UserBuilder firstName(String firstName) {
        this.firstName = firstName;
        return this;
    }

    public UserBuilder lastName(String lastName) {
        this.lastName = lastName;
        return this;
    }
    
    public UserBuilder email(String email) {
        this.email = email;
        return this;
    }
    
    public UserBuilder address(String address) {
        this.address = address;
        return this;
    }
    
    public UserBuilder city(String city) {
        this.city = city;
        return this;
    }

    public UserBuilder state(String state) {
        this.state = state;
        return this;
    }
    
    public UserBuilder zip(String zip) {
        this.zip = zip;
        return this;
    }
    
    public UserBuilder password(String password) {
        this.password = password;
        return this;
    }
    
    public UserBuilder token(String token) {
        this.token = token;
        return this;
    }
    
    public UserBuilder active(boolean active) {
        this.active = active;
        return this;
    }
    
    public UserBuilder roles(List<SecurityRole> roles) {
    	this.roles = roles;
    	return this;
    }
    
    public User build() {
    	
        User user = new User();
        
        user.setId(id);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEmail(email);
        user.setAddress(address);
        user.setCity(city);
        user.setState(state);
        user.setZip(zip);
        user.setPassword(password);
        user.setToken(token);
        user.setActive(active);
        user.setRoles(roles);
        
        return user;
    }
    
	
}
