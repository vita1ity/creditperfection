package forms;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.avaje.ebean.Model;

import errors.ValidationError;

public class ProductForm extends Model {

	public String id;
    public String name;
    
    public String price;
    public String salePrice;
	
    public List<ValidationError> validate() {
		
		List<ValidationError> errors = new ArrayList<ValidationError>();
		
		Map<ValidationError, String> fieldErrorMap = new HashMap<ValidationError, String>();
		fieldErrorMap.put(new ValidationError("name", "Please enter Product Name"), name);
		fieldErrorMap.put(new ValidationError("price", "Please enter Product Price"), price);
		fieldErrorMap.put(new ValidationError("salePrice", "Please enter Product Sale Price"), salePrice);
		
		for (Map.Entry<ValidationError, String> entry: fieldErrorMap.entrySet()) {
			String field = entry.getValue(); 
			if (field == null || field.equals("")) {
				errors.add(entry.getKey());
			}
		}
		
		if (name.length() < 5) {
			ValidationError error = new ValidationError("name", "Product Name should contain at least 5 characers");
			errors.add(error);
		}
		
		double doublePrice = 0;
		try {
			 doublePrice = Double.parseDouble(price);
		}
		catch(NumberFormatException e) {
			errors.add(new ValidationError("price", "Product price should be numeric value"));
		}
		
		double doubleSalePrice = 0;
		try {
			doubleSalePrice = Double.parseDouble(salePrice);
		}
		catch(NumberFormatException e) {
			errors.add(new ValidationError("salePrice", "Product sale price should be numeric value"));
		}
		
		if (doublePrice < 0) {
			errors.add(new ValidationError("price", "Product price should be greater than 0"));
		}
		if (doubleSalePrice < 0) {
			errors.add(new ValidationError("salePrice", "Product sale price should be greater than 0"));
		}
		
		if (errors.size() != 0) {
			return errors;
		}
		return null;
	}

    
}
