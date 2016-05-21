package models.json;

public class ObjectCreatedResponse extends JSONResponse {

	private String message;
	private long id;
	
	public ObjectCreatedResponse(String status, String message, long id) {
		super(status);
		this.message = message;
		this.id = id;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}
	
	

}
