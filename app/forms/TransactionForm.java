package forms;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.avaje.ebean.Model;

import errors.ValidationError;
import models.enums.TransactionStatus;

public class TransactionForm extends Model {

	private String id;
	
	private String userId;
	
	private String cardId;
	
	private String productId;
	
	private String amount;
	
	private String transactionId;
	
	private TransactionStatus status;
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getCardId() {
		return cardId;
	}

	public void setCardId(String cardId) {
		this.cardId = cardId;
	}

	public String getProductId() {
		return productId;
	}

	public void setProductId(String productId) {
		this.productId = productId;
	}
	
	public String getAmount() {
		return amount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}

	public String getTransactionId() {
		return transactionId;
	}

	public void setTransactionId(String transactionId) {
		this.transactionId = transactionId;
	}

	public TransactionStatus getStatus() {
		return status;
	}

	public void setStatus(TransactionStatus status) {
		this.status = status;
	}
	

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
