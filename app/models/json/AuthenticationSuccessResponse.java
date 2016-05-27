package models.json;

public class AuthenticationSuccessResponse extends JSONResponse {
	
	private String memberId;
	
	public AuthenticationSuccessResponse(String status, String memberId) {
		super(status);
		
		this.memberId = memberId;
	}

	public String getMemberId() {
		return memberId;
	}

	public void setMemberId(String memberId) {
		this.memberId = memberId;
	}
	
}
