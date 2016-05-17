package models.json;

import java.util.List;

public class MultipleErrorResponse extends JSONResponse {

	private List<Error> errors;

	public MultipleErrorResponse(String status, List<Error> errors) {
		super(status);
	
		this.errors = errors;
		
	}

	public List<Error> getErrors() {
		return errors;
	}

	public void setErrors(List<Error> errors) {
		this.errors = errors;
	}
	
	
	
}
