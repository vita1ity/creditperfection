package models.json;

public class SuccessLoginResponse extends JSONResponse {
	
	private String role;
	private boolean subscription;
	
	public SuccessLoginResponse(String status, String role, boolean subscription) {
		super(status);
		this.role = role;
		this.subscription = subscription;
	}


	public String getRole() {
		return role;
	}


	public void setRole(String role) {
		this.role = role;
	}


	public boolean getSubscription() {
		return subscription;
	}


	public void setSubscription(boolean subscription) {
		this.subscription = subscription;
	}

}
