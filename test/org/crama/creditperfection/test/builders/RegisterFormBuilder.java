package org.crama.creditperfection.test.builders;

import forms.RegisterForm;

public class RegisterFormBuilder {
    
    private String firstName = "Sherlock";
    
    private String lastName = "Holmes";
    
    private String email = "sherlock.holmes@gmail.com";
    
    private String confirmEmail = "sherlock.holmes@gmail.com";
    
    private String address = "221b Baker Street";
    
    private String city = "London";
    
    private String state = "UK";
    
    private String zip = "33333";
    
    private String password = "sherlock";
    
    private String confirmPassword = "sherlock";
    

    public RegisterFormBuilder firstName(String firstName) {
        this.firstName = firstName;
        return this;
    }

    public RegisterFormBuilder lastName(String lastName) {
        this.lastName = lastName;
        return this;
    }
    
    public RegisterFormBuilder email(String email) {
        this.email = email;
        return this;
    }
    
    public RegisterFormBuilder confirmEmail(String confirmEmail) {
        this.confirmEmail = confirmEmail;
        return this;
    }
    
    public RegisterFormBuilder address(String address) {
        this.address = address;
        return this;
    }
    
    public RegisterFormBuilder city(String city) {
        this.city = city;
        return this;
    }

    public RegisterFormBuilder state(String state) {
        this.state = state;
        return this;
    }
    
    public RegisterFormBuilder zip(String zip) {
        this.zip = zip;
        return this;
    }
    
    public RegisterFormBuilder password(String password) {
        this.password = password;
        return this;
    }
    
    public RegisterFormBuilder confirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
        return this;
    }
   
    
    public RegisterForm build() {
    	
        RegisterForm registerForm = new RegisterForm();
        
        registerForm.setFirstName(firstName);
        registerForm.setLastName(lastName);
        registerForm.setEmail(email);
        registerForm.setAddress(address);
        registerForm.setCity(city);
        registerForm.setState(state);
        registerForm.setZip(zip);
        registerForm.setPassword(password);
        registerForm.setConfirmEmail(confirmEmail);
        registerForm.setConfirmPassword(confirmPassword);
        
        return registerForm;
    }
	
}
