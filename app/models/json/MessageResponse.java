package models.json;

public class MessageResponse extends JSONResponse {
	
	private String message;
	
	public MessageResponse(String status, String message) {
		super(status);
		
		this.message = message;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
}
