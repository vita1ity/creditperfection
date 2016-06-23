package forms;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.avaje.ebean.Model;

import errors.ValidationError;
import models.Subscription;
import models.User;
import models.enums.SubscriptionStatus;
import models.json.MessageResponse;
import play.libs.Json;

public class SubscriptionForm extends Model {
	
	public String id;
	
	public String userId;
	
	public String cardId;
	
	public String productId;
	
	public SubscriptionStatus status;
	
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
			User user = User.find.byId(Long.parseLong(userId));
			
			Subscription subFromDb = Subscription.findByUser(user);
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
