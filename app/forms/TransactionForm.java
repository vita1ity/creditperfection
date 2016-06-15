package forms;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.avaje.ebean.Model;

import errors.ValidationError;
import models.enums.TransactionStatus;

public class TransactionForm extends Model {

	public String id;
	
	public String userId;
	
	public String cardId;
	
	public String productId;
	
	public String amount;
	
	public String transactionId;
	
	public TransactionStatus status;
	
	public List<ValidationError> validate() {
		
		List<ValidationError> errors = new ArrayList<ValidationError>();
		
		Map<ValidationError, String> fieldErrorMap = new HashMap<ValidationError, String>();
		fieldErrorMap.put(new ValidationError("user", "Please choose User"), userId);
		fieldErrorMap.put(new ValidationError("creditCard", "Please choose Credit Card"), cardId);
		fieldErrorMap.put(new ValidationError("product", "Please choose Product"), productId);
		fieldErrorMap.put(new ValidationError("amount", "Please enter Amount"), amount);
		fieldErrorMap.put(new ValidationError("transactionId", "Please enter Transaction Id"), transactionId);
		
		for (Map.Entry<ValidationError, String> entry: fieldErrorMap.entrySet()) {
			String field = entry.getValue(); 
			if (field == null || field.equals("")) {
				errors.add(entry.getKey());
			}
		}
		
		try {
			Double.parseDouble(amount);
		}
		catch (NumberFormatException e) {
			errors.add(new ValidationError("amount", "Amount should be numeric value"));
		}
		
		if (errors.size() != 0) {
			return errors;
		}
		return null;
	    
	}
	
	
}
