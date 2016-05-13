package models;

import java.time.YearMonth;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import com.avaje.ebean.Model;
import com.avaje.ebean.Model.Finder;

import errors.ValidationError;
import play.data.format.Formats;

@Entity
public class CreditCard extends Model {
	
    @Id
    public long id;
    
    public String name;
    
    public CardType cardType;
    
    public String digits;
    
    @Formats.DateTime(pattern="MM/yyyy")
    public YearMonth expDate;
   
    public int cvv;
    
    @ManyToOne(cascade = CascadeType.REFRESH)
    public User user;

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
		if (digits.length() != 16) {
			errors.add(new ValidationError("digits", "Card Number should contain 16 digits"));
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
    
    
    
}
