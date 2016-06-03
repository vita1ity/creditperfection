package models;

import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import com.avaje.ebean.Model;
import com.fasterxml.jackson.annotation.JsonIgnore;

import errors.ValidationError;
import forms.CreditCardForm;
import models.enums.CardType;
import play.data.format.Formats;

@Entity
public class CreditCard extends Model {
	
    @Id
    public long id;
    
    @Column(nullable = false)
    public String name;
    
    @Column(nullable = false)
    public CardType cardType;
    
    @Column(nullable = false)
    public String digits;
    
    @Formats.DateTime(pattern="MM/yyyy")
    @Column(nullable = false)
    public YearMonth expDate;
    
    @Column(nullable = false)
    public int cvv;
    
    @JsonIgnore
    @ManyToOne
    @JoinColumn(nullable = false)
    public User user;
    
    @JsonIgnore
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "creditCard")
    public List<Transaction> transactions;

    public CreditCard() {
    	
    }
    
	public CreditCard(String name, CardType cardType, String digits, YearMonth expDate, int cvv, User user) {
		super();
		
		this.name = name;
		this.cardType = cardType;
		this.digits = digits;
		this.expDate = expDate;
		this.cvv = cvv;
		this.user = user;
	}

	public static Finder<Long, CreditCard> find = new Finder<Long, CreditCard>(CreditCard.class);
	
	@Override
	public String toString() {
		return "CreditCard [id=" + id + ", name=" + name + ", cardType=" + cardType + ", digits=" + digits
				+ ", expDate=" + expDate + ", cvv=" + cvv + "]";
	}

	public List<ValidationError> validate() {
		
		List<ValidationError> errors = new ArrayList<ValidationError>();

		Map<ValidationError, String> fieldErrorMap = new HashMap<ValidationError, String>();
		fieldErrorMap.put(new ValidationError("name", "Please enter Card Holder Name"), name);
		fieldErrorMap.put(new ValidationError("cardType", "Please select Card Type"), cardType.value);
		fieldErrorMap.put(new ValidationError("digits", "Please enter Card Number"), digits);
		
		for (Map.Entry<ValidationError, String> entry: fieldErrorMap.entrySet()) {
			String field = entry.getValue(); 
			if (field == null || field.equals("")) {
				errors.add(entry.getKey());
			}
		}
		digits = digits.replaceAll("\\s+", "");
		if (digits.length() != 16 && digits.length() != 15) {
			errors.add(new ValidationError("digits", "Card Number should contain 15 or 16 digits"));
		}
		
		if (cvv == 0) {
			errors.add(new ValidationError("cvv", "Please enter Card Security Code"));
		}
		
		if (String.valueOf(cvv).length() != 3 && String.valueOf(cvv).length() != 4) {
			errors.add(new ValidationError("cvv", "Invalid CVV number"));
		}
		
		if (errors.size() != 0) {
			return errors;
		}
		
		return null;
	}

	public void updateCreditCardInfo(CreditCard creditCard) {
		
		this.name = creditCard.name;
		this.cardType = creditCard.cardType;
		this.expDate = creditCard.expDate;
		this.digits = creditCard.digits;
		this.cvv = creditCard.cvv;
		
		
	}

	public static CreditCard createCreditCard(CreditCardForm creditCardForm) {
		
		CreditCard creditCard = new CreditCard();
		if (creditCardForm.id != null) {
			creditCard.id = Long.parseLong(creditCardForm.id);
		}
		creditCard.name = creditCardForm.name;
		creditCard.cardType = CardType.valueOf(creditCardForm.cardType);
		creditCard.digits = creditCardForm.digits;
		creditCard.cvv = Integer.parseInt(creditCardForm.cvv);
		String expDateStr = creditCardForm.month + "/" + creditCardForm.year;
    	DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/yyyy");
    	YearMonth expDate = YearMonth.parse(expDateStr, formatter);
    	creditCard.expDate = expDate; 
		
		return creditCard;
	}
    
    
    
}
