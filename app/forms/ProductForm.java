package forms;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.avaje.ebean.Model;

import errors.ValidationError;

public class ProductForm extends Model {

	private String id;
    private String name;
    private String price;
    private String salePrice;
    private String trialPeriod;
	
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

	public String getPrice() {
		return price;
	}

	public void setPrice(String price) {
		this.price = price;
	}

	public String getSalePrice() {
		return salePrice;
	}

	public void setSalePrice(String salePrice) {
		this.salePrice = salePrice;
	}
	

	public String getTrialPeriod() {
		return trialPeriod;
	}

	public void setTrialPeriod(String trialPeriod) {
		this.trialPeriod = trialPeriod;
	}

	public List<ValidationError> validate() {
		
		List<ValidationError> errors = new ArrayList<ValidationError>();
		
		Map<ValidationError, String> fieldErrorMap = new HashMap<ValidationError, String>();
		fieldErrorMap.put(new ValidationError("name", "Please enter Product Name"), name);
		fieldErrorMap.put(new ValidationError("price", "Please enter Product Price"), price);
		fieldErrorMap.put(new ValidationError("salePrice", "Please enter Product Sale Price"), salePrice);
		fieldErrorMap.put(new ValidationError("trialPeriod", "Please enter Product Trial Period"), trialPeriod);
		
		for (Map.Entry<ValidationError, String> entry: fieldErrorMap.entrySet()) {
			String field = entry.getValue(); 
			if (field == null || field.equals("")) {
				errors.add(entry.getKey());
			}
		}
		
		if (name != null && name.length() < 5) {
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
		
		int trialPeriodInt = 0;
		try {
			trialPeriodInt = Integer.parseInt(trialPeriod);
		}
		catch(NumberFormatException e) {
			errors.add(new ValidationError("trialPeriod", "Product trial period should be numeric value"));
		}
		
		if (doublePrice < 0) {
			errors.add(new ValidationError("price", "Product price should be greater than 0"));
		}
		if (doubleSalePrice < 0) {
			errors.add(new ValidationError("salePrice", "Product sale price should be greater than 0"));
		}
		if (trialPeriodInt < 0) {
			errors.add(new ValidationError("trialPeriod", "Product trial period should be positive value"));
		}
		
		if (errors.size() != 0) {
			return errors;
		}
		return null;
	}

    
}
