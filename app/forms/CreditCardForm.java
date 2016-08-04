package forms;

import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.avaje.ebean.Model;

import errors.ValidationError;
import models.enums.CardType;

public class CreditCardForm extends Model {
	
		private String id;
	    
	    private String name;
	   
	    private String cardType;
	    
	    private String digits;
	    
	    private String month;
	    
	    private String year;
	   
	    private String cvv;
	    
	    private String ownerId;
	    
	    
	    public String getId() {
			return id;
		}
	    
		public void setId(String id) {
			this.id = id;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getCardType() {
			return cardType;
		}

		public void setCardType(String cardType) {
			this.cardType = cardType;
		}

		public String getDigits() {
			return digits;
		}

		public void setDigits(String digits) {
			this.digits = digits;
		}

		public String getMonth() {
			return month;
		}

		public void setMonth(String month) {
			this.month = month;
		}

		public String getYear() {
			return year;
		}

		public void setYear(String year) {
			this.year = year;
		}

		public String getCvv() {
			return cvv;
		}

		public void setCvv(String cvv) {
			this.cvv = cvv;
		}

		public String getOwnerId() {
			return ownerId;
		}

		public void setOwnerId(String ownerId) {
			this.ownerId = ownerId;
		}

		public List<ValidationError> validate() {
			
	    	List<ValidationError> errors = new ArrayList<ValidationError>();

			Map<ValidationError, String> fieldErrorMap = new HashMap<ValidationError, String>();
			fieldErrorMap.put(new ValidationError("name", "Please enter Card Holder Name"), name);
			fieldErrorMap.put(new ValidationError("cardType", "Please select Card Type"), cardType);
			fieldErrorMap.put(new ValidationError("digits", "Please enter Card Number"), digits);
			fieldErrorMap.put(new ValidationError("month", "Please select Expiration Month"), month);
			fieldErrorMap.put(new ValidationError("year", "Please select Expiration Year"), year);
			fieldErrorMap.put(new ValidationError("cvv", "Please enter CVV code"), cvv);
			
			for (Map.Entry<ValidationError, String> entry: fieldErrorMap.entrySet()) {
				String field = entry.getValue(); 
				if (field == null || field.equals("")) {
					errors.add(entry.getKey());
				}
			}
			
			if (digits != null)
			{
				digits = digits.replaceAll("\\s+", "");
				if (digits.length() != 16 && digits.length() != 15) {
					errors.add(new ValidationError("digits", "Card Number should contain 15 or 16 digits"));
				}
				
				else if (!digits.matches("[0-9]+")) {
					errors.add(new ValidationError("digits", "Card Number should be numeric value"));
				}
			}
			
			if (cvv != null && cvv.length() != 3 && cvv.length() != 4) {
				errors.add(new ValidationError("cvv", "Invalid CVV number"));
			}
			
			try {
				Integer.parseInt(cvv);
			}
			catch (NumberFormatException e) {
				errors.add(new ValidationError("cvv", "CVV should contain only digits"));
			}
			
			if (month != null && month.length() == 1) {
	    		month = "0" + month;
	    	}
	    	String expDateStr = month + "/" + year;
	    	DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/yyyy");
	    	try {
	    		YearMonth.parse(expDateStr, formatter);
	    	}
	    	catch (DateTimeParseException e) {
	    		errors.add(new ValidationError("month", "Expiration date is invalid"));
	    	}
	    	
			if (errors.size() != 0) {
				return errors;
			}
			
			return null;
		    
		}
	    
}
