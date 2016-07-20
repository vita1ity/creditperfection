package forms;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import com.avaje.ebean.Model;

import errors.ValidationError;
import models.Subscription;
import models.User;
import models.enums.SubscriptionStatus;
import services.SubscriptionService;
import services.UserService;

public class SubscriptionForm extends Model {
	
	@Inject
	private UserService userService;
	
	@Inject
	private SubscriptionService subscriptionService;
	
	private String id;
	
	private String userId;
	
	private String cardId;
	
	private String productId;
	
	private SubscriptionStatus status;
	
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

	public SubscriptionStatus getStatus() {
		return status;
	}

	public void setStatus(SubscriptionStatus status) {
		this.status = status;
	}

	public void setUserService(UserService userService) {
		this.userService = userService;
	}

	public void setSubscriptionService(SubscriptionService subscriptionService) {
		this.subscriptionService = subscriptionService;
	}

	public List<ValidationError> validate() {
		
		List<ValidationError> errors = new ArrayList<ValidationError>();
		
		Map<ValidationError, String> fieldErrorMap = new HashMap<ValidationError, String>();
		fieldErrorMap.put(new ValidationError("user", "Please choose User"), userId);
		fieldErrorMap.put(new ValidationError("creditCard", "Please choose Credit Card"), cardId);
		fieldErrorMap.put(new ValidationError("product", "Please choose Product"), productId);
		
		for (Map.Entry<ValidationError, String> entry: fieldErrorMap.entrySet()) {
			String field = entry.getValue(); 
			if (field == null || field.equals("")) {
				errors.add(entry.getKey());
			}
		}
		if (!userId.equals("") && userId != null && id == null) {
			User user = userService.getById(Long.parseLong(userId));
			
			Subscription subFromDb = subscriptionService.findByUser(user);
	    	if (subFromDb != null) {
	    		errors.add(new ValidationError("user", "User is already subscribed"));
	    	}
			
		}
		if (errors.size() != 0) {
			return errors;
		}
		return null;
	    
	}
	
}
