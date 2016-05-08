package models.json;

public class SuccessLoginResponse extends JSONResponse {
	
	private String role;
	
	public SuccessLoginResponse(String status, String role) {
		super(status);
		this.role = role;
	}


	public String getRole() {
		return role;
	}


	public void setRole(String role) {
		this.role = role;
	}

	
	
	
}
